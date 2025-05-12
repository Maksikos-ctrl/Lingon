package sk.uniza.fri.lingon.core;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Trieda reprezentujuca kontajner pre UI prvky
 * Sluzi ako wrapper pre vkladanie UI komponentov do aplikacie
 */
public class UIKontajner extends JPanel {
    private OvladacHlavnehoOkna ovladac;

    /**
     * Konstruktor pre vytvorenie noveho UI kontajnera
     */
    public UIKontajner() {
        super(new BorderLayout());
    }

    /**
     * Prida komponent do kontajnera
     * @param component Komponent na pridanie
     */
    public void pridajKomponent(Component component) {
        this.removeAll();
        this.add(component, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Odstrani vsetky komponenty z kontajnera
     */
    public void vymazObsah() {
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    /**
     * Nastavi ovladac
     * @param ovladac Ovladac hlavneho okna
     */
    public void setOvladac(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
    }

    /**
     * Vrati ovladac
     * @return Ovladac hlavneho okna
     */
    public OvladacHlavnehoOkna getOvladac() {
        return this.ovladac;
    }
}