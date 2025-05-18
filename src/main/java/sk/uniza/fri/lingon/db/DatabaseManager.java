package sk.uniza.fri.lingon.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

/**
 * Správca databázy SQLite pre ukladanie histórie testov
 */
public class DatabaseManager {
    private static final String DB_NAME = "lingon_historia.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            vytvorTabulky();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver nenájdený: " + e.getMessage());
        }
    }

    /**
     * Vytvorí tabuľky ak neexistujú
     */
    private static void vytvorTabulky() {
        String sql = "CREATE TABLE IF NOT EXISTS historia (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pouzivatel_email TEXT NOT NULL," + // Nový stĺpec pre email používateľa
                "kategoria_nazov TEXT NOT NULL," +
                "cas_ukoncenia TEXT NOT NULL," +
                "pocet_otazok INTEGER NOT NULL," +
                "spravne_odpovede INTEGER NOT NULL," +
                "nespravne_odpovede INTEGER NOT NULL," +
                "uspesnost REAL NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabuľka 'historia' je pripravená.");

            // Kontrola či existuje stĺpec pouzivatel_email
            try {
                ResultSet rs = stmt.executeQuery("SELECT pouzivatel_email FROM historia LIMIT 1");
                rs.close();
            } catch (SQLException e) {
                // Stĺpec neexistuje, pridáme ho
                try {
                    stmt.execute("ALTER TABLE historia ADD COLUMN pouzivatel_email TEXT DEFAULT 'unknown'");
                    System.out.println("Stĺpec 'pouzivatel_email' pridaný do tabuľky 'historia'.");
                } catch (SQLException e2) {
                    System.err.println("Chyba pri pridávaní stĺpca 'pouzivatel_email': " + e2.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Chyba pri vytváraní tabuľky: " + e.getMessage());
        }
    }

    /**
     * Získa spojenie s databázou
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Uloží výsledok testu
     */
    public static void ulozVysledok(VysledokTestu vysledok) {
        String sql = "INSERT INTO historia (pouzivatel_email, kategoria_nazov, cas_ukoncenia, pocet_otazok, " +
                "spravne_odpovede, nespravne_odpovede, uspesnost) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String casovaZnacka = vysledok.getCasUkoncenia().format(formatter);

            pstmt.setString(1, vysledok.getPouzivatelEmail());
            pstmt.setString(2, vysledok.getKategoriaNazov());
            pstmt.setString(3, casovaZnacka); // Uložíme čas v DB formáte
            pstmt.setInt(4, vysledok.getPocetOtazok());
            pstmt.setInt(5, vysledok.getSpravneOdpovede());
            pstmt.setInt(6, vysledok.getNespravneOdpovede());
            pstmt.setDouble(7, vysledok.getUspesnost());

            pstmt.executeUpdate();
            System.out.println("Výsledok testu uložený do databázy pre používateľa: " + vysledok.getPouzivatelEmail() + " v čase: " + casovaZnacka);

        } catch (SQLException e) {
            System.err.println("Chyba pri ukladaní do databázy: " + e.getMessage());
        }
    }

    /**
     * Načíta históriu testov pre konkrétneho používateľa
     */
    public static List<VysledokTestu> nacitajHistoriuPouzivatela(String email) {
        List<VysledokTestu> historia = new ArrayList<>();
        String sql = "SELECT * FROM historia WHERE pouzivatel_email = ? ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String kategoriaNazov = rs.getString("kategoria_nazov");
                String casUkonceniaStr = rs.getString("cas_ukoncenia");
                int pocetOtazok = rs.getInt("pocet_otazok");
                int spravne = rs.getInt("spravne_odpovede");
                int nespravne = rs.getInt("nespravne_odpovede");

                // Vytvoríme výsledok
                VysledokTestu vysledok = new VysledokTestu("", kategoriaNazov, pocetOtazok);
                vysledok.setPouzivatelEmail(email);

                // Nastavíme správne a nesprávne odpovede
                for (int i = 0; i < spravne; i++) {
                    vysledok.pridajSpravnuOdpoved();
                }
                for (int i = 0; i < nespravne; i++) {
                    vysledok.pridajNespravnuOdpoved();
                }

                // Nastavíme presný čas z databázy
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime casUkoncenia = LocalDateTime.parse(casUkonceniaStr, formatter);
                    // Nastavíme čas ukončenia cez reflexiu alebo použijeme setterCasUkoncenia
                    setCasUkoncenia(vysledok, casUkoncenia);
                } catch (Exception e) {
                    // Ak sa nepodarí parse, ukončíme test normálne
                    vysledok.ukonciTest();
                }

                historia.add(vysledok);
            }

            System.out.println("Načítané " + historia.size() + " záznamov z databázy pre používateľa: " + email);

        } catch (SQLException e) {
            System.err.println("Chyba pri čítaní z databázy: " + e.getMessage());
        }

        return historia;
    }

    // Pomocná metóda na nastavenie času ukončenia
    private static void setCasUkoncenia(VysledokTestu vysledok, LocalDateTime cas) {
        try {
            // Použitie reflexie pre prístup k privátnym poliam, keďže možno nemáme setter
            Field field = VysledokTestu.class.getDeclaredField("casUkoncenia");
            field.setAccessible(true);
            field.set(vysledok, cas);

            // Musíme tiež nastaviť hodnotu úspešnosti
            field = VysledokTestu.class.getDeclaredField("uspesnost");
            field.setAccessible(true);
            field.set(vysledok, (double)vysledok.getSpravneOdpovede() / vysledok.getPocetOtazok() * 100);
        } catch (Exception e) {
            System.err.println("Chyba pri nastavovaní času ukončenia: " + e.getMessage());
        }
    }

    /**
     * Načíta všetku históriu testov (ponecháme pre spätnú kompatibilitu)
     */
    public static List<VysledokTestu> nacitajHistoriu() {
        List<VysledokTestu> historia = new ArrayList<>();
        String sql = "SELECT * FROM historia ORDER BY id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String kategoriaNazov = rs.getString("kategoria_nazov");
                int pocetOtazok = rs.getInt("pocet_otazok");
                int spravne = rs.getInt("spravne_odpovede");
                int nespravne = rs.getInt("nespravne_odpovede");

                // Získanie emailu používateľa (ak existuje stĺpec)
                String pouzivatelEmail = "unknown";
                try {
                    pouzivatelEmail = rs.getString("pouzivatel_email");
                } catch (SQLException e) {
                    // Stĺpec neexistuje, použijeme predvolenú hodnotu
                }

                VysledokTestu vysledok = new VysledokTestu("", kategoriaNazov, pocetOtazok);
                vysledok.setPouzivatelEmail(pouzivatelEmail);

                // Nastavíme hodnoty
                for (int i = 0; i < spravne; i++) {
                    vysledok.pridajSpravnuOdpoved();
                }
                for (int i = 0; i < nespravne; i++) {
                    vysledok.pridajNespravnuOdpoved();
                }

                vysledok.ukonciTest();
                historia.add(vysledok);
            }

            System.out.println("Načítané " + historia.size() + " záznamov z databázy.");

        } catch (SQLException e) {
            System.err.println("Chyba pri čítaní z databázy: " + e.getMessage());
        }

        return historia;
    }

    /**
     * Vymaže históriu testov konkrétneho používateľa
     */
    public static void vymazHistoriuPouzivatela(String email) {
        String sql = "DELETE FROM historia WHERE pouzivatel_email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            int pocet = pstmt.executeUpdate();
            System.out.println("História vymazaná pre používateľa: " + email + " (" + pocet + " záznamov)");

        } catch (SQLException e) {
            System.err.println("Chyba pri mazaní histórie: " + e.getMessage());
        }
    }

    /**
     * Vymaže všetky záznamy z histórie
     */
    public static void vymazHistoriu() {
        String sql = "DELETE FROM historia";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("História vymazaná.");

        } catch (SQLException e) {
            System.err.println("Chyba pri mazaní histórie: " + e.getMessage());
        }
    }

    /**
     * Kontroluje, či používateľ s daným emailom existuje v databáze
     */
    public static boolean existujePouzivatel(String email) {
        String sql = "SELECT COUNT(*) FROM pouzivatelia WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Chyba pri kontrole existencie používateľa: " + e.getMessage());

            // Skúsime vytvoriť tabuľku, ak neexistuje
            vytvorTabulkuPouzivatelia();
        }

        return false;
    }

    /**
     * Vytvorí tabuľku pre používateľov, ak neexistuje
     */
    private static void vytvorTabulkuPouzivatelia() {
        String sql = "CREATE TABLE IF NOT EXISTS pouzivatelia (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "meno TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "celkove_xp INTEGER DEFAULT 0," +
                "spravne_odpovede INTEGER DEFAULT 0," +
                "nespravne_odpovede INTEGER DEFAULT 0" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabuľka 'pouzivatelia' je pripravená.");
        } catch (SQLException e) {
            System.err.println("Chyba pri vytváraní tabuľky pouzivatelia: " + e.getMessage());
        }
    }

    /**
     * Uloží používateľa do databázy
     */
    public static boolean ulozPouzivatela(Pouzivatel pouzivatel) {
        vytvorTabulkuPouzivatelia();

        String sql = "INSERT INTO pouzivatelia (meno, email, celkove_xp, spravne_odpovede, nespravne_odpovede) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pouzivatel.getMeno());
            pstmt.setString(2, pouzivatel.getEmail());
            pstmt.setInt(3, pouzivatel.getCelkoveXP());
            pstmt.setInt(4, pouzivatel.getSpravneOdpovede());
            pstmt.setInt(5, pouzivatel.getNespravneOdpovede());

            pstmt.executeUpdate();
            System.out.println("Používateľ úspešne uložený do databázy: " + pouzivatel.getEmail());
            return true;

        } catch (SQLException e) {
            System.err.println("Chyba pri ukladaní používateľa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Načíta používateľa z databázy podľa emailu
     */
    /**
     * Načíta používateľa z databázy podľa emailu
     */
    public static Pouzivatel nacitajPouzivatela(String email) {
        vytvorTabulkuPouzivatelia(); // Uistíme sa, že tabuľka existuje

        String sql = "SELECT * FROM pouzivatelia WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String meno = rs.getString("meno");
                int celkoveXP = rs.getInt("celkove_xp");
                int spravneOdpovede = rs.getInt("spravne_odpovede");
                int nespravneOdpovede = rs.getInt("nespravne_odpovede");

                Pouzivatel pouzivatel = new Pouzivatel(meno, email);

                // Nastavenie hodnôt
                pouzivatel.setCelkoveXP(celkoveXP);
                pouzivatel.setSpravneOdpovede(spravneOdpovede);
                pouzivatel.setNespravneOdpovede(nespravneOdpovede);

                System.out.println("Používateľ úspešne načítaný z databázy: " + email);
                return pouzivatel;
            }
        } catch (SQLException e) {
            System.err.println("Chyba pri načítaní používateľa: " + e.getMessage());
        }

        System.out.println("Používateľ s emailom " + email + " nebol nájdený v databáze.");
        return null;
    }

    /**
     * Aktualizuje používateľa v databáze
     */
    public static boolean aktualizujPouzivatela(Pouzivatel pouzivatel) {
        vytvorTabulkuPouzivatelia(); // Uistíme sa, že tabuľka existuje

        String sql = "UPDATE pouzivatelia SET meno = ?, celkove_xp = ?, " +
                "spravne_odpovede = ?, nespravne_odpovede = ? WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pouzivatel.getMeno());
            pstmt.setInt(2, pouzivatel.getCelkoveXP());
            pstmt.setInt(3, pouzivatel.getSpravneOdpovede());
            pstmt.setInt(4, pouzivatel.getNespravneOdpovede());
            pstmt.setString(5, pouzivatel.getEmail());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Používateľ úspešne aktualizovaný v databáze: " + pouzivatel.getEmail());
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Chyba pri aktualizácii používateľa: " + e.getMessage());
            return false;
        }
    }




}