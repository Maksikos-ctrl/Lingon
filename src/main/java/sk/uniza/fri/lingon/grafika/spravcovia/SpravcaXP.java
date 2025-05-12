package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

import javax.swing.JLabel;
import java.awt.Font;

/**
 * Správca XP bodov a progresu
 * Zodpovedný za sledovanie a aktualizáciu XP
 */
public class SpravcaXP {
    private int currentXP;
    private JLabel xpLabel;
    private JLabel progressLabel;

    /**
     * Konštruktor správcu XP
     */
    public SpravcaXP() {
        this.currentXP = 0;
    }

    /**
     * Pridá XP body používateľovi
     * @param xp Počet XP bodov
     * @param pouzivatel Používateľ ktorému sa pridávajú body
     */
    public void pridajXP(int xp, Pouzivatel pouzivatel) {
        this.currentXP += xp;
        pouzivatel.pridajXP(xp);
        this.updateXPLabel(pouzivatel);
    }

    /**
     * Aktualizuje XP label
     * @param pouzivatel Aktuálny používateľ
     */
    public void updateXPLabel(Pouzivatel pouzivatel) {
        if (this.xpLabel != null) {
            this.xpLabel.setText("XP: " + pouzivatel.getCelkoveXP());
        }
    }

    /**
     * Vytvorí XP label
     * @return XP label
     */
    public JLabel vytvorXPLabel() {
        this.xpLabel = new JLabel("XP: 0");
        this.xpLabel.setFont(new Font("Arial", Font.BOLD, 20));
        return this.xpLabel;
    }

    /**
     * Vytvorí progress label
     * @param aktualnaOtazka Číslo aktuálnej otázky
     * @param celkovyPocet Celkový počet otázok
     * @return Progress label
     */
    public JLabel vytvorProgressLabel(int aktualnaOtazka, int celkovyPocet) {
        this.progressLabel = new JLabel("Otázka " + (aktualnaOtazka + 1) + " z " + celkovyPocet);
        return this.progressLabel;
    }

    // Gettery
    public JLabel getXPLabel() {
        return this.xpLabel;
    }
    public JLabel getProgressLabel() {
        return this.progressLabel;
    }
    public int getCurrentXP() {
        return this.currentXP;
    }
}