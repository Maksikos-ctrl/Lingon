package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.obrazovky.ProfilObrazovka;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

/**
 * Správca používateľov
 * Zodpovedný za správu používateľov a ich profilov
 */
public class SpravcaPouzivatela {
    private OvladacHlavnehoOkna ovladac;
    private Pouzivatel aktualnyPouzivatel;

    /**
     * Konštruktor správcu používateľov
     * @param ovladac Hlavný ovládač aplikácie
     */
    public SpravcaPouzivatela(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
    }

    /**
     * Zobrazí dialóg pre vytvorenie nového používateľa
     */
    public void zobrazDialogNovehoPouzivatela() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Meno:"));
        JTextField menoField = new JTextField(20);
        panel.add(menoField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        panel.add(emailField);

        int vysledok = JOptionPane.showConfirmDialog(
                this.ovladac.getHlavneOkno(),
                panel,
                "Nový používateľ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (vysledok == JOptionPane.OK_OPTION) {
            String meno = menoField.getText().trim();
            String email = emailField.getText().trim();

            if (!meno.isEmpty() && !email.isEmpty()) {
                this.aktualnyPouzivatel = new Pouzivatel(meno, email);
                this.ovladac.zobrazHlavneMenu();
            } else {
                JOptionPane.showMessageDialog(
                        this.ovladac.getHlavneOkno(),
                        "Prosím, vyplňte meno a email.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Zobrazí profil používateľa
     */
    public void zobrazProfilPouzivatela() {
        ProfilObrazovka profilObrazovka = new ProfilObrazovka(this.ovladac, this.aktualnyPouzivatel);
        profilObrazovka.zobraz();
    }

    /**
     * Getter pre aktuálneho používateľa
     * @return Aktuálny používateľ
     */
    public Pouzivatel getAktualnyPouzivatel() {
        return this.aktualnyPouzivatel;
    }

    /**
     * Setter pre aktuálneho používateľa
     * @param pouzivatel Nový používateľ
     */
    public void setAktualnyPouzivatel(Pouzivatel pouzivatel) {
        this.aktualnyPouzivatel = pouzivatel;
    }
}