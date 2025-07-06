package sk.uniza.fri.lingon;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.db.DatabaseManager;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.util.Objects;

/**
 * Hlavna trieda aplikacie Lingon
 * Sluzi ako vstupny bod aplikacie
 */
public class Main {
    /**
     * Hlavna metoda - vstupny bod aplikacie
     * @param args Argumenty prikazoveho riadku
     */
    public static void main(String[] args) {
        // Nastavenie vzhľadu aplikácie
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pridáme shutdown hook pre korektné zatvorenie PostgreSQL
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🔄 Zatváram aplikáciu...");
            DatabaseManager.shutdown();
        }));

        // Spustenie aplikacie v EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(Main::vytvorGUI);
    }

    /**
     * Vytvori graficke rozhranie aplikacie
     */
    private static void vytvorGUI() {
        // Vytvorenie hlavneho okna
        JFrame hlavneOkno = new JFrame("Lingon");

        // Pridáme WindowListener pre správne zatvorenie databázy
        hlavneOkno.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        hlavneOkno.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("🔄 Zatváram aplikáciu cez GUI...");
                DatabaseManager.shutdown();
                System.exit(0);
            }
        });

        hlavneOkno.setSize(800, 600);
        hlavneOkno.setMinimumSize(new Dimension(640, 480));

        // Nastavenie ikony aplikácie
        try {
            // Načítaj ikonu z resources
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/images/icon.png")));
            hlavneOkno.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Nepodarilo sa načítať ikonu: " + e.getMessage());
        }

        // Vytvorenie a nastavenie ovladaca hlavneho okna
        OvladacHlavnehoOkna ovladac = new OvladacHlavnehoOkna(hlavneOkno);

        // Centrovanie okna na obrazovke
        hlavneOkno.setLocationRelativeTo(null);

        // Zobrazenie okna
        hlavneOkno.setVisible(true);

        // Zobrazenie uvodnej obrazovky
        ovladac.zobrazUvodnuObrazovku();
    }
}