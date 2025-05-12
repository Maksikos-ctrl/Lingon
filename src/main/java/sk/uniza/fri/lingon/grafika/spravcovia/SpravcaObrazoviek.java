package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.obrazovky.UvodnaObrazovka;

import javax.swing.JPanel;
import java.awt.Component;

/**
 * Správca obrazoviek
 * Zodpovedný za zobrazovanie rôznych obrazoviek
 */
public class SpravcaObrazoviek {
    private OvladacHlavnehoOkna ovladac;

    /**
     * Konštruktor správcu obrazoviek
     * @param ovladac Hlavný ovládač aplikácie
     */
    public SpravcaObrazoviek(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
    }

    /**
     * Zobrazí úvodnú obrazovku
     */
    public void zobrazUvodnuObrazovku() {
        this.odstranNavigaciuPanel();
        UvodnaObrazovka uvodnaObrazovka = new UvodnaObrazovka(this.ovladac);
        this.ovladac.getKontajner().pridajKomponent(uvodnaObrazovka);
        this.ovladac.getHlavneOkno().revalidate();
        this.ovladac.getHlavneOkno().repaint();
    }

    /**
     * Odstráni navigačný panel z hlavného okna
     */
    public void odstranNavigaciuPanel() {
        Component[] komponenty = this.ovladac.getHlavneOkno().getContentPane().getComponents();
        for (Component komponent : komponenty) {
            if (komponent instanceof JPanel && komponent != this.ovladac.getKontajner()) {
                this.ovladac.getHlavneOkno().remove(komponent);
            }
        }
        this.ovladac.getHlavneOkno().revalidate();
        this.ovladac.getHlavneOkno().repaint();
    }
}