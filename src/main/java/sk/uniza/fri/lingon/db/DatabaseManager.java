package sk.uniza.fri.lingon.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sk.uniza.fri.lingon.core.VysledokTestu;

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

            pstmt.setString(1, vysledok.getPouzivatelEmail());
            pstmt.setString(2, vysledok.getKategoriaNazov());
            pstmt.setString(3, vysledok.getFormatovanyCas());
            pstmt.setInt(4, vysledok.getPocetOtazok());
            pstmt.setInt(5, vysledok.getSpravneOdpovede());
            pstmt.setInt(6, vysledok.getNespravneOdpovede());
            pstmt.setDouble(7, vysledok.getUspesnost());

            pstmt.executeUpdate();
            System.out.println("Výsledok testu uložený do databázy pre používateľa: " + vysledok.getPouzivatelEmail());

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
                int pocetOtazok = rs.getInt("pocet_otazok");
                int spravne = rs.getInt("spravne_odpovede");
                int nespravne = rs.getInt("nespravne_odpovede");
                String pouzivatelEmail = rs.getString("pouzivatel_email");

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

            System.out.println("Načítané " + historia.size() + " záznamov z databázy pre používateľa: " + email);

        } catch (SQLException e) {
            System.err.println("Chyba pri čítaní z databázy: " + e.getMessage());
        }

        return historia;
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
}