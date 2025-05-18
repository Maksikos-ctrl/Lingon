package sk.uniza.fri.lingon.pouzivatel.lekcia;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

/**
 * Pomocná trieda pre spracovanie správnych a nesprávnych odpovedí
 */
public class VyberuSpravnehoSpracovania {

    /**
     * Zvýrazní správnu odpoveď zelenou farbou
     * @param button Tlačidlo so správnou odpoveďou
     */
    public static void zvyrazniSpravnuOdpoved(JButton button) {
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(56, 142, 60), 2));
    }

    /**
     * Zakáže všetky tlačidlá po výbere odpovede
     * @param panel Panel s tlačidlami
     */
    public static void zakazTlacidla(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(false);
            }
        }
    }
}