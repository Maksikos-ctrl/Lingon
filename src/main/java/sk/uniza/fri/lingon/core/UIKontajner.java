package sk.uniza.fri.lingon.core;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Trieda reprezentujuca kontajner pre UI prvky
 * Sluzi ako wrapper pre vkladanie UI komponentov do aplikacie
 */
public class UIKontajner extends JPanel {
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
}