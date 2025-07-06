package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.db.DatabaseManager;
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
    private final OvladacHlavnehoOkna ovladac;
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

            // Validácia
            if (meno.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this.ovladac.getHlavneOkno(),
                        "Prosím, vyplňte meno a email.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
                this.zobrazDialogNovehoPouzivatela(); // Zobrazíme dialóg znova
                return;
            }

            // Kontrola formátu emailu pomocou regulárneho výrazu
            if (!this.jeEmailValidy(email)) {
                JOptionPane.showMessageDialog(
                        this.ovladac.getHlavneOkno(),
                        "Zadajte platný email.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
                this.zobrazDialogNovehoPouzivatela(); // Zobrazíme dialóg znova
                return;
            }

            // Kontrola existencie používateľa
            Pouzivatel existujuciPouzivatel = DatabaseManager.nacitajPouzivatela(email);

            if (existujuciPouzivatel != null) {
                // Používateľ existuje
                int odpoved = JOptionPane.showConfirmDialog(
                        this.ovladac.getHlavneOkno(),
                        "Používateľ s týmto emailom už existuje.\nChcete sa prihlásiť ako " +
                                existujuciPouzivatel.getMeno() + "?",
                        "Používateľ existuje",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (odpoved == JOptionPane.YES_OPTION) {
                    // Prihlásenie
                    this.aktualnyPouzivatel = existujuciPouzivatel;
                    this.ovladac.zobrazHlavneMenu();
                } else {
                    // Zrušiť a zobraziť dialóg znova
                    this.zobrazDialogNovehoPouzivatela();
                }
            } else {
                // Vytvorenie a uloženie nového používateľa
                this.aktualnyPouzivatel = new Pouzivatel(meno, email);
                DatabaseManager.ulozPouzivatela(this.aktualnyPouzivatel);
                this.ovladac.zobrazHlavneMenu();
            }
        }
    }

    /**
     * Kontroluje, či je email validný
     * @param email Email na kontrolu
     * @return true ak je email validný, inak false
     */
    private boolean jeEmailValidy(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Zobrazí profil používateľa
     */
    public void zobrazProfilPouzivatela() {
        ProfilObrazovka profilObrazovka = new ProfilObrazovka(this.ovladac, this.aktualnyPouzivatel);
        profilObrazovka.zobraz();
    }

    /**
     * 🔄 Obnoví aktuálneho používateľa z databázy (po testoch, aktualizáciách)
     */
    public void obnovAktualnehoPozivatela() {
        if (this.aktualnyPouzivatel != null) {
            String email = this.aktualnyPouzivatel.getEmail();
            Pouzivatel obnovenyPouzivatel = DatabaseManager.nacitajPouzivatela(email);

            if (obnovenyPouzivatel != null) {
                int stareXP = this.aktualnyPouzivatel.getCelkoveXP();
                int noveXP = obnovenyPouzivatel.getCelkoveXP();

                this.aktualnyPouzivatel = obnovenyPouzivatel;

                System.out.println("🔄 UI používateľ obnovený: " + email +
                        " (XP: " + stareXP + " → " + noveXP + ")");
            } else {
                System.err.println("❌ Nepodarilo sa obnoviť používateľa: " + email);
            }
        }
    }

    /**
     * Getter pre aktuálneho používateľa
     * @return Aktuálny používateľ
     */
    public Pouzivatel getAktualnyPouzivatel() {
        return this.aktualnyPouzivatel;
    }
}