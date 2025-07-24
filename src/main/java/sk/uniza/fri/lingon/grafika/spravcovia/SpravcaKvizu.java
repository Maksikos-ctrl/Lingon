package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.core.KategoriaTrivia;
import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.db.DatabaseManager;
import sk.uniza.fri.lingon.db.OtazkyLoader;
import sk.uniza.fri.lingon.gui.IZadanie;
import sk.uniza.fri.lingon.core.AbstractneZadanie;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.animacie.NacitaciaObrazovka;
import sk.uniza.fri.lingon.grafika.komponenty.ModerneButtonUI;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Správca kvízu
 * Zodpovedný za načítavanie a správu otázok
 */
public class SpravcaKvizu {
    private final OvladacHlavnehoOkna ovladac;
    private List<IZadanie> otazky;
    private int aktualnaOtazka;
    private VysledokTestu aktualnyVysledok;

    /**
     * Konštruktor správcu kvízu
     * @param ovladac Hlavný ovládač aplikácie
     */
    public SpravcaKvizu(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
        this.aktualnaOtazka = 0;
    }

    /**
     * Načíta otázky z API alebo demo otázky
     */
    public void nacitajOtazky() {
        NacitaciaObrazovka nacitaciaObrazovka = new NacitaciaObrazovka(
                () -> {
                    try {
                        Thread.sleep(2000);
                        this.otazky = OtazkyLoader.nacitajOtazky();
                        this.aktualnaOtazka = 0;
                    } catch (Exception e) {
                        System.out.println("Chyba pri načítaní otázok z API: " + e.getMessage());
                        this.otazky = OtazkyLoader.getDemoOtazky();
                        this.aktualnaOtazka = 0;
                    }
                },
                this.ovladac::zobrazOtazku
        );

        SwingUtilities.invokeLater(() -> {
            this.ovladac.getKontajner().pridajKomponent(nacitaciaObrazovka);
            Timer timer = new Timer(100, e -> {
                nacitaciaObrazovka.startLoading();
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Spustí kvíz pre vybranú kategóriu
     * @param kategoria Vybraná kategória
     */
    public void spustiKvizPreKategoriu(KategoriaTrivia kategoria) {

        NacitaciaObrazovka nacitaciaObrazovka = new NacitaciaObrazovka(
                () -> {
                    try {
                        Thread.sleep(1000);
                        this.otazky = OtazkyLoader.nacitajOtazkyPreKategoriu(kategoria.getId());
                        this.aktualnaOtazka = 0;
                        // Vytvoríme nový výsledok testu
                        this.aktualnyVysledok = new VysledokTestu(
                                String.valueOf(kategoria.getId()),
                                kategoria.getNazov(),
                                this.otazky.size()
                        );

                        // Pridáme email používateľa k výsledku
                        if (this.ovladac.getAktualnyPouzivatel() != null) {
                            this.aktualnyVysledok.setPouzivatelEmail(this.ovladac.getAktualnyPouzivatel().getEmail());
                        }
                    } catch (Exception e) {
                        System.out.println("Chyba pri načítaní otázok pre kategóriu: " + e.getMessage());
                        this.otazky = OtazkyLoader.getDemoOtazky();
                        this.aktualnaOtazka = 0;
                        this.aktualnyVysledok = new VysledokTestu(
                                "demo",
                                "Demo kategória",
                                this.otazky.size()
                        );

                        // Pridáme email používateľa k výsledku aj pre demo otázky
                        if (this.ovladac.getAktualnyPouzivatel() != null) {
                            this.aktualnyVysledok.setPouzivatelEmail(this.ovladac.getAktualnyPouzivatel().getEmail());
                        }
                    }
                },
                () -> {
                    if (this.otazky != null && !this.otazky.isEmpty()) {
                        this.ovladac.zobrazOtazku();
                    } else {
                        JOptionPane.showMessageDialog(this.ovladac.getHlavneOkno(),
                                "Pre túto kategóriu nie sú dostupné žiadne otázky.",
                                "Informácia",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.ovladac.zobrazHlavneMenu();
                    }
                }
        );

        this.ovladac.getKontajner().pridajKomponent(nacitaciaObrazovka);
        SwingUtilities.invokeLater(nacitaciaObrazovka::startLoading);
    }

    /**
     * Zobrazí aktuálnu otázku
     */
    public void zobrazOtazku() {
        SpravcaObrazoviek spravcaObrazoviek = new SpravcaObrazoviek(this.ovladac);
        spravcaObrazoviek.odstranNavigaciuPanel();
        this.ovladac.getKontajner().vymazObsah();

        // Vytvorenie info panelu
        JPanel quizInfoPanel =  this.vytvorInfoPanel();
        this.ovladac.getHlavneOkno().add(quizInfoPanel, BorderLayout.NORTH);

        if (this.otazky == null || this.otazky.isEmpty()) {
            this.nacitajOtazky();
            return;
        }

        if (this.aktualnaOtazka < this.otazky.size()) {
            IZadanie zadanie = this.otazky.get(this.aktualnaOtazka);

            if (zadanie instanceof AbstractneZadanie) {
                ((AbstractneZadanie)zadanie).setOvladac(this.ovladac);
            }
            zadanie.zobrazGrafiku(this.ovladac.getKontajner());

            // Pridanie navigačného panelu
            JPanel navigaciaPanel = this.vytvorNavigaciuPanel();
            this.ovladac.getHlavneOkno().add(navigaciaPanel, BorderLayout.SOUTH);
        } else {
            this.ovladac.zobrazUvodnuObrazovku();
        }

        this.ovladac.getHlavneOkno().revalidate();
        this.ovladac.getHlavneOkno().repaint();
    }

    /**
     * Vytvorí informačný panel pre kvíz
     * @return Informačný panel
     */
    private JPanel vytvorInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(new Color(240, 240, 240));

        JLabel progressLabel = new JLabel("Otázka " + (this.aktualnaOtazka + 1) + " z " + this.otazky.size());
        progressLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(progressLabel, BorderLayout.WEST);

        // XP label napravo, väčší
        JLabel xpLabel =  this.ovladac.getSpravcaXP().vytvorXPLabel();
        xpLabel.setFont(new Font("Arial", Font.BOLD, 20));
        xpLabel.setText("XP: " + this.ovladac.getAktualnyPouzivatel().getCelkoveXP());
        panel.add(xpLabel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Vytvorí navigačný panel
     * @return Navigačný panel
     */
    private JPanel vytvorNavigaciuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Ľavá časť - tlačidlo do menu (skryté počas testu)
        JButton menuButton = ModerneButtonUI.vytvorModerneTlacidlo("Späť do menu", new Color(59, 89, 152));
        menuButton.setVisible(false); // Skryjeme tlačidlo počas testu

        // menuButton.addActionListener(e -> ovladac.zobrazHlavneMenu());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(menuButton);
        panel.add(leftPanel, BorderLayout.WEST);

        // Pravá časť - tlačidlo ďalšia otázka alebo ukončiť kvíz
        JButton rightButton;

        if (this.aktualnaOtazka == this.otazky.size() - 1) {
            // Posledná otázka - tlačidlo na ukončenie
            rightButton = ModerneButtonUI.vytvorModerneTlacidlo("Ukončiť kvíz", new Color(220, 53, 69));
            rightButton.addActionListener(_ -> {
                this.aktualnaOtazka++;  // Posunieme na koniec
                this.ukonciTest(); // Použijeme metódu ukonciTest namiesto priameho volania
            });
        } else {
            // Normálna otázka - tlačidlo ďalej
            rightButton = ModerneButtonUI.vytvorModerneTlacidlo("Ďalšia otázka →", new Color(76, 175, 80));
            rightButton.addActionListener(_ -> {
                this.aktualnaOtazka++;
                this.ovladac.zobrazOtazku();
            });
        }

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(rightButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Ukončí test a spracuje výsledky - KOMPLETNE OPRAVENÁ VERZIA
     */
    public void ukonciTest() {
        if (this.aktualnyVysledok != null) {
            // Ukončíme test
            this.aktualnyVysledok.ukonciTest();

            System.out.println("📊 Test ukončený. Výsledky spracováva DatabaseManager...");

            // ❌ ODSTRÁNENÉ VŠETKO XP POČÍTANIE - bude sa robiť v DatabaseManager
            // ❌ ODSTRÁNENÉ: int pridaneXP = (int)(this.aktualnyVysledok.getUspesnost() / 10);
            // ❌ ODSTRÁNENÉ: this.ovladac.pridajXP(pridaneXP);
            // ❌ ODSTRÁNENÉ: Aktualizácia štatistík používateľa
            // ❌ ODSTRÁNENÉ: DatabaseManager.aktualizujPouzivatela(aktualnyPouzivatel);

            // ✅ Iba zobrazíme výsledky - XP sa spočíta automaticky
            this.ovladac.zobrazVysledky(this.aktualnyVysledok);
        } else {
            // Ak nemáme výsledok, vrátime sa do menu
            this.ovladac.zobrazHlavneMenu();
        }
    }

    // Gettery
    public List<IZadanie> getOtazky() {
        return this.otazky;
    }

    public VysledokTestu getAktualnyVysledok() {
        return this.aktualnyVysledok;
    }
}