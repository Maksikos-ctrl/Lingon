package sk.uniza.fri.lingon;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.*;
import java.awt.Dimension;

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

        // Spustenie aplikacie v EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            vytvorGUI();
        });
    }

    /**
     * Vytvori graficke rozhranie aplikacie
     */
    private static void vytvorGUI() {
        // Vytvorenie hlavneho okna
        JFrame hlavneOkno = new JFrame("Lingon");
        hlavneOkno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hlavneOkno.setSize(800, 600);
        hlavneOkno.setMinimumSize(new Dimension(640, 480));

        // Nastavenie ikony aplikácie
        try {
            // Načítaj ikonu z resources
            ImageIcon icon = new ImageIcon(Main.class.getResource("/images/icon.png"));
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