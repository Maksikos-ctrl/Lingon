package sk.uniza.fri.lingon.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

/**
 * Správca databázy H2 pre ukladanie histórie testov.
 * KOMPATIBILNÁ VERZIA PRE SYNCHRONIZÁCIU!
 */
public class DatabaseManager {

    // 🔧 JEDNODUCHÁ H2 KONFIGURÁCIA BEZ PROBLÉMOV
    private static final String SHARED_DB_PATH = getSyncedDatabasePath();
    private static final String DB_URL = "jdbc:h2:" + SHARED_DB_PATH + "lingon_historia";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private static HikariDataSource dataSource;

    static {
        try {
            System.out.println("🔗 Desktop DB Path: " + DB_URL);
            setupDataSource();
            vytvorTabulky();
        } catch (Exception e) {
            System.err.println("❌ Chyba pri inicializácii H2 Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🔄 Získa synchronizovaný path k databáze
     */
    private static String getSyncedDatabasePath() {
        // Používame Documents folder pre jednoduchosť
        String externalPath = System.getProperty("user.home") + "/Documents/LingonQuiz/";

        // Vytvoríme priečinok ak neexistuje
        File dir = new File(externalPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("✅ Vytvorený synchronizačný priečinok: " + externalPath);
            } else {
                System.out.println("⚠️ Nepodarilo sa vytvoriť priečinok: " + externalPath);
                // Fallback na aktuálny priečinok
                externalPath = "./";
            }
        }

        return externalPath;
    }

    /**
     * JEDNODUCHÁ konfigurácia H2 Database
     */
    private static void setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("org.h2.Driver");

        // ZÁKLADNÉ nastavenia bez problémových opcií
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
        System.out.println("✅ H2 Database connection pool inicializovaný (KOMPATIBILNÝ)");
    }

    /**
     * Vytvorí tabuľky ak neexistujú.
     */
    private static void vytvorTabulky() throws SQLException {
        // ROVNAKÁ ŠTRUKTÚRA AKO V MOBILE!
        String sqlHistoria = """
            CREATE TABLE IF NOT EXISTS historia (
                id IDENTITY PRIMARY KEY,
                pouzivatel_email VARCHAR(255) NOT NULL,
                kategoria_nazov VARCHAR(255) NOT NULL,
                cas_ukoncenia TIMESTAMP NOT NULL,
                pocet_otazok INTEGER NOT NULL,
                spravne_odpovede INTEGER NOT NULL,
                nespravne_odpovede INTEGER NOT NULL,
                uspesnost DECIMAL(5,2) NOT NULL
            )""";

        String sqlPouzivatelia = """
            CREATE TABLE IF NOT EXISTS pouzivatelia (
                id IDENTITY PRIMARY KEY,
                meno VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                celkove_xp INTEGER DEFAULT 0,
                spravne_odpovede INTEGER DEFAULT 0,
                nespravne_odpovede INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )""";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlHistoria);
            System.out.println("✅ Tabuľka 'historia' pripravená");

            stmt.execute(sqlPouzivatelia);
            System.out.println("✅ Tabuľka 'pouzivatelia' pripravená");

            // Vytvoríme indexy pre lepší výkon
            vytvorIndexy(stmt);

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri vytváraní tabuliek: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Vytvorí indexy pre optimálny výkon
     */
    private static void vytvorIndexy(Statement stmt) throws SQLException {
        try {
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_historia_email ON historia(pouzivatel_email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_historia_cas ON historia(cas_ukoncenia)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_pouzivatelia_email ON pouzivatelia(email)");
            System.out.println("✅ Indexy vytvorené");
        } catch (SQLException e) {
            System.out.println("💡 Indexy už existujú alebo nie sú podporované");
        }
    }

    /**
     * Získa spojenie s H2 databázou
     */
    private static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("H2 connection pool nie je inicializovaný");
        }
        return dataSource.getConnection();
    }

    /**
     * Vytvorí a nakonfiguruje objekt VysledokTestu s danými parametrami.
     */
    private static VysledokTestu vytvorVysledok(String kategoriaId, String kategoriaNazov,
                                                int pocetOtazok, String pouzivatelEmail,
                                                int spravne, int nespravne) {
        VysledokTestu vysledok = new VysledokTestu(kategoriaId, kategoriaNazov, pocetOtazok);
        vysledok.setPouzivatelEmail(pouzivatelEmail);

        for (int i = 0; i < spravne; i++) {
            vysledok.pridajSpravnuOdpoved();
        }
        for (int i = 0; i < nespravne; i++) {
            vysledok.pridajNespravnuOdpoved();
        }

        return vysledok;
    }

    /**
     * Uloží výsledok testu do databázy.
     */
    public static void ulozVysledok(VysledokTestu vysledok) {
        String sql = """
            INSERT INTO historia (pouzivatel_email, kategoria_nazov, cas_ukoncenia, 
                                 pocet_otazok, spravne_odpovede, nespravne_odpovede, uspesnost) 
            VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vysledok.getPouzivatelEmail());
            pstmt.setString(2, vysledok.getKategoriaNazov());
            pstmt.setObject(3, vysledok.getCasUkoncenia());
            pstmt.setInt(4, vysledok.getPocetOtazok());
            pstmt.setInt(5, vysledok.getSpravneOdpovede());
            pstmt.setInt(6, vysledok.getNespravneOdpovede());
            pstmt.setDouble(7, vysledok.getUspesnost());

            pstmt.executeUpdate();
            System.out.println("✅ Výsledok uložený do SYNCHRONIZOVANEJ H2 Database: " + vysledok.getPouzivatelEmail());

            // Aktualizujeme používateľa po teste
            aktualizujPouzivatelaPoTeste(vysledok);

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri ukladaní: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🔄 Aktualizuje XP používateľa po teste
     */
    private static void aktualizujPouzivatelaPoTeste(VysledokTestu vysledok) {
        try {
            Pouzivatel pouzivatel = nacitajPouzivatela(vysledok.getPouzivatelEmail());
            if (pouzivatel != null) {
                // Pridáme XP za test
                int bonusXP = vysledok.getSpravneOdpovede() * 10; // 10 XP za správnu odpoveď
                pouzivatel.setCelkoveXP(pouzivatel.getCelkoveXP() + bonusXP);
                pouzivatel.setSpravneOdpovede(pouzivatel.getSpravneOdpovede() + vysledok.getSpravneOdpovede());
                pouzivatel.setNespravneOdpovede(pouzivatel.getNespravneOdpovede() + vysledok.getNespravneOdpovede());

                aktualizujPouzivatela(pouzivatel);
                System.out.println("🔄 Používateľ synchronizovaný: " + pouzivatel.getEmail() + " (+" + bonusXP + " XP)");
            }
        } catch (Exception e) {
            System.err.println("❌ Chyba pri aktualizácii používateľa: " + e.getMessage());
        }
    }

    /**
     * Načíta históriu testov pre konkrétneho používateľa.
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
                LocalDateTime casUkoncenia = rs.getTimestamp("cas_ukoncenia").toLocalDateTime();
                int pocetOtazok = rs.getInt("pocet_otazok");
                int spravne = rs.getInt("spravne_odpovede");
                int nespravne = rs.getInt("nespravne_odpovede");

                VysledokTestu vysledok = vytvorVysledok("", kategoriaNazov, pocetOtazok,
                        email, spravne, nespravne);
                setCasUkoncenia(vysledok, casUkoncenia);
                historia.add(vysledok);
            }

            System.out.println("✅ Načítané " + historia.size() + " záznamov pre: " + email);

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri čítaní histórie: " + e.getMessage());
        }

        return historia;
    }

    /**
     * Pomocná metóda na nastavenie času ukončenia.
     */
    private static void setCasUkoncenia(VysledokTestu vysledok, LocalDateTime cas) {
        try {
            Field field = VysledokTestu.class.getDeclaredField("casUkoncenia");
            field.setAccessible(true);
            field.set(vysledok, cas);

            field = VysledokTestu.class.getDeclaredField("uspesnost");
            field.setAccessible(true);
            field.set(vysledok, (double)vysledok.getSpravneOdpovede() / vysledok.getPocetOtazok() * 100);
        } catch (Exception e) {
            System.err.println("Chyba pri nastavovaní času ukončenia: " + e.getMessage());
        }
    }

    /**
     * Načíta všetku históriu testov.
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
                String pouzivatelEmail = rs.getString("pouzivatel_email");
                LocalDateTime casUkoncenia = rs.getTimestamp("cas_ukoncenia").toLocalDateTime();

                VysledokTestu vysledok = vytvorVysledok("", kategoriaNazov, pocetOtazok,
                        pouzivatelEmail, spravne, nespravne);
                setCasUkoncenia(vysledok, casUkoncenia);
                historia.add(vysledok);
            }

            System.out.println("✅ Načítané " + historia.size() + " záznamov z H2 Database");

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri čítaní z databázy: " + e.getMessage());
        }

        return historia;
    }

    /**
     * Vymaže históriu testov konkrétneho používateľa.
     */
    public static void vymazHistoriuPouzivatela(String email) {
        String sql = "DELETE FROM historia WHERE pouzivatel_email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            int pocet = pstmt.executeUpdate();
            System.out.println("✅ História vymazaná pre: " + email + " (" + pocet + " záznamov)");

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri mazaní histórie: " + e.getMessage());
        }
    }

    /**
     * Vymaže všetky záznamy z histórie.
     */
    public static void vymazHistoriu() {
        String sql = "DELETE FROM historia";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ História vymazaná z H2 Database");

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri mazaní histórie: " + e.getMessage());
        }
    }

    /**
     * Kontroluje, či používateľ s daným emailom existuje v databáze.
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
            System.err.println("❌ Chyba pri kontrole existencie používateľa: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uloží používateľa do databázy.
     */
    public static boolean ulozPouzivatela(Pouzivatel pouzivatel) {
        String sql = """
            INSERT INTO pouzivatelia (meno, email, celkove_xp, spravne_odpovede, nespravne_odpovede) 
            VALUES (?, ?, ?, ?, ?)""";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pouzivatel.getMeno());
            pstmt.setString(2, pouzivatel.getEmail());
            pstmt.setInt(3, pouzivatel.getCelkoveXP());
            pstmt.setInt(4, pouzivatel.getSpravneOdpovede());
            pstmt.setInt(5, pouzivatel.getNespravneOdpovede());

            pstmt.executeUpdate();
            System.out.println("✅ Používateľ uložený do H2 Database: " + pouzivatel.getEmail());
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri ukladaní používateľa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Načíta používateľa z databázy podľa emailu.
     */
    public static Pouzivatel nacitajPouzivatela(String email) {
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
                pouzivatel.setCelkoveXP(celkoveXP);
                pouzivatel.setSpravneOdpovede(spravneOdpovede);
                pouzivatel.setNespravneOdpovede(nespravneOdpovede);

                System.out.println("✅ Používateľ načítaný z H2 Database: " + email);
                return pouzivatel;
            }
        } catch (SQLException e) {
            System.err.println("❌ Chyba pri načítaní používateľa: " + e.getMessage());
        }

        System.out.println("⚠️ Používateľ s emailom " + email + " nebol nájdený");
        return null;
    }

    /**
     * Aktualizuje používateľa v databáze.
     */
    public static boolean aktualizujPouzivatela(Pouzivatel pouzivatel) {
        String sql = """
            UPDATE pouzivatelia 
            SET meno = ?, celkove_xp = ?, spravne_odpovede = ?, nespravne_odpovede = ? 
            WHERE email = ?""";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pouzivatel.getMeno());
            pstmt.setInt(2, pouzivatel.getCelkoveXP());
            pstmt.setInt(3, pouzivatel.getSpravneOdpovede());
            pstmt.setInt(4, pouzivatel.getNespravneOdpovede());
            pstmt.setString(5, pouzivatel.getEmail());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Používateľ aktualizovaný v H2 Database: " + pouzivatel.getEmail());
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Chyba pri aktualizácii používateľa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Zatvorí všetky databázové spojenia pri ukončení aplikácie
     */
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("🔒 H2 Database connection pool zatvorený");
        }
    }
}