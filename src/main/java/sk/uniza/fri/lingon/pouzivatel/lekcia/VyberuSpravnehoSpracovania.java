package sk.uniza.fri.lingon.pouzivatel.lekcia;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

/**
 * Pomocná trieda pre spracovanie správnych a nesprávnych odpovedí
 */
public class VyberuSpravnehoSpracovania {

    /**
     * Zobrazí správnu odpoveď a zvýrazní ju
     * @param button Tlačidlo s odpoveďou
     * @param jeSpravna Či je odpoveď správna
     * @param ovladac Ovládač hlavného okna
     */
    public static void spracujOdpoved(JButton button, boolean jeSpravna, OvladacHlavnehoOkna ovladac) {
        if (jeSpravna) {
            // Zelené pozadie pre správnu odpoveď
            button.setBackground(new Color(76, 175, 80));
            button.setForeground(Color.WHITE);

            // Pridáme XP
            ovladac.pridajXP(10);

            // Aktualizujeme XP label
            ovladac.getSpravcaXP().updateXPLabel(ovladac.getAktualnyPouzivatel());

            // Zobrazíme správu
            JOptionPane.showMessageDialog(button.getParent(),
                    "Správna odpoveď! +10 XP",
                    "Výborne!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Červené pozadie pre nesprávnu odpoveď
            button.setBackground(new Color(244, 67, 54));
            button.setForeground(Color.WHITE);
        }
    }

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