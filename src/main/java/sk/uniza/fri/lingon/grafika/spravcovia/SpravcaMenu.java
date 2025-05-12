package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Správca menu aplikácie
 * Zodpovedný za vytvorenie a správu menu
 */
public class SpravcaMenu {
    private OvladacHlavnehoOkna ovladac;

    /**
     * Konštruktor správcu menu
     * @param ovladac Hlavný ovládač aplikácie
     */
    public SpravcaMenu(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
    }

    /**
     * Vytvorí menu aplikácie
     */
    public void vytvorMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Aplikácia
        JMenu aplikaciaMenu = new JMenu("Aplikácia");

        JMenuItem novyPouzivatelItem = new JMenuItem("Nový používateľ");
        novyPouzivatelItem.addActionListener(e ->
                this.ovladac.getSpravcaPouzivatela().zobrazDialogNovehoPouzivatela());
        aplikaciaMenu.add(novyPouzivatelItem);

        JMenuItem koniecItem = new JMenuItem("Koniec");
        koniecItem.addActionListener(e -> System.exit(0));
        aplikaciaMenu.add(koniecItem);

        // Pridanie menu do menu baru
        menuBar.add(aplikaciaMenu);

        this.ovladac.getHlavneOkno().setJMenuBar(menuBar);
    }
}