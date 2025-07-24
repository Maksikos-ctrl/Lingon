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
                this.zobrazDialogNovehoPouzivatela();
                return;
            }

            // Kontrola formátu emailu
            if (!this.jeEmailValidy(email)) {
                JOptionPane.showMessageDialog(
                        this.ovladac.getHlavneOkno(),
                        "Zadajte platný email.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
                this.zobrazDialogNovehoPouzivatela();
                return;
            }

            // ✅ OPRAVENÁ LOGIKA HĽADANIA/VYTVÁRANIA POUŽÍVATEĽA
            System.out.println("🔍 Hľadám používateľa: " + email);

            Pouzivatel existujuciPouzivatel = DatabaseManager.nacitajPouzivatela(email);

            if (existujuciPouzivatel != null) {
                // Používateľ existuje
                System.out.println("👤 Používateľ existuje: " + existujuciPouzivatel.getMeno() + " (XP: " + existujuciPouzivatel.getCelkoveXP() + ")");

                int odpoved = JOptionPane.showConfirmDialog(
                        this.ovladac.getHlavneOkno(),
                        "Používateľ s týmto emailom už existuje.\nChcete sa prihlásiť ako " +
                                existujuciPouzivatel.getMeno() + "?",
                        "Používateľ existuje",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (odpoved == JOptionPane.YES_OPTION) {
                    this.aktualnyPouzivatel = existujuciPouzivatel;
                    System.out.println("✅ Používateľ prihlásený: " + this.aktualnyPouzivatel.getEmail());
                    this.ovladac.zobrazHlavneMenu();
                } else {
                    this.zobrazDialogNovehoPouzivatela();
                }
            } else {
                // ✅ VYTVORENIE NOVÉHO POUŽÍVATEĽA S LEPŠÍM ERROR HANDLING
                System.out.println("👤 Vytváram nového používateľa: " + meno + " (" + email + ")");

                this.aktualnyPouzivatel = new Pouzivatel(meno, email);
                System.out.println("🔧 DEBUG: Calling DatabaseManager.ulozPouzivatela...");
                boolean ulozenyDoH2 = DatabaseManager.ulozPouzivatela(this.aktualnyPouzivatel);

                if (ulozenyDoH2) {
                    System.out.println("✅ Nový používateľ vytvorený a uložený: " + email);

                    System.out.println("🔧 DEBUG: Waiting for Firebase sync to complete...");

                    // Krátka pauza pre Firebase sync
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("🔧 DEBUG: Verifying Firebase sync...");
                    // Overenie že sa používateľ načítal správne (vrátane Firebase)
                    Pouzivatel overenyPouzivatel = DatabaseManager.nacitajPouzivatela(email);
                    if (overenyPouzivatel != null) {
                        this.aktualnyPouzivatel = overenyPouzivatel;
                        System.out.println("✅ Používateľ overený a načítaný: " + email);
                    }

                    this.ovladac.zobrazHlavneMenu();
                } else {
                    System.err.println("❌ Nepodarilo sa uložiť používateľa do databázy");
                    JOptionPane.showMessageDialog(
                            this.ovladac.getHlavneOkno(),
                            "Nepodarilo sa vytvoriť nového používateľa.\nSkúste to znova.",
                            "Chyba databázy",
                            JOptionPane.ERROR_MESSAGE
                    );
                    this.zobrazDialogNovehoPouzivatela();
                }
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
            System.out.println("🔄 Obnovovanie používateľa: " + email);

            Pouzivatel obnovenyPouzivatel = DatabaseManager.nacitajPouzivatela(email);

            if (obnovenyPouzivatel != null) {
                int stareXP = this.aktualnyPouzivatel.getCelkoveXP();
                int noveXP = obnovenyPouzivatel.getCelkoveXP();

                this.aktualnyPouzivatel = obnovenyPouzivatel;

                System.out.println("🔄 UI používateľ obnovený: " + email +
                        " (XP: " + stareXP + " → " + noveXP + ")");

                // Debug informácie
                System.out.println("📊 Aktuálny stav používateľa:");
                System.out.println("   - Meno: " + this.aktualnyPouzivatel.getMeno());
                System.out.println("   - Email: " + this.aktualnyPouzivatel.getEmail());
                System.out.println("   - XP: " + this.aktualnyPouzivatel.getCelkoveXP());
                System.out.println("   - Správne: " + this.aktualnyPouzivatel.getSpravneOdpovede());
                System.out.println("   - Nesprávne: " + this.aktualnyPouzivatel.getNespravneOdpovede());
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