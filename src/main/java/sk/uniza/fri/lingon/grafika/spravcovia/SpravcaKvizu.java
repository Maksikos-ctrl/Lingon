package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.core.AbstractneZadanie;
import sk.uniza.fri.lingon.core.KategoriaTrivia;
import sk.uniza.fri.lingon.db.OtazkyLoader;
import sk.uniza.fri.lingon.GUI.IZadanie;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.animacie.NacitaciaObrazovka;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Správca kvízu
 * Zodpovedný za načítavanie a správu otázok
 */
public class SpravcaKvizu {
    private OvladacHlavnehoOkna ovladac;
    private List<IZadanie> otazky;
    private int aktualnaOtazka;

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
                () -> {
                    this.ovladac.zobrazOtazku();
                }
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
                    } catch (Exception e) {
                        System.out.println("Chyba pri načítaní otázok pre kategóriu: " + e.getMessage());
                        this.otazky = OtazkyLoader.getDemoOtazky();
                        this.aktualnaOtazka = 0;
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
        SwingUtilities.invokeLater(() -> nacitaciaObrazovka.startLoading());
    }

    /**
     * Zobrazí aktuálnu otázku
     */
    public void zobrazOtazku() {
        SpravcaObrazoviek spravcaObrazoviek = new SpravcaObrazoviek(this.ovladac);
        spravcaObrazoviek.odstranNavigaciuPanel();
        this.ovladac.getKontajner().vymazObsah();

        // Vytvorenie info panelu
        JPanel quizInfoPanel = this.vytvorInfoPanel();
        this.ovladac.getHlavneOkno().add(quizInfoPanel, BorderLayout.NORTH);

        if (this.otazky == null || this.otazky.isEmpty()) {
            this.nacitajOtazky();
            return;
        }

        if (this.aktualnaOtazka < this.otazky.size()) {
            IZadanie zadanie = this.otazky.get(this.aktualnaOtazka);
            // Если это наш AbstractneZadanie, установим овладач
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
        JLabel xpLabel = this.ovladac.getSpravcaXP().vytvorXPLabel();
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

        // Ľavá časť - tlačidlo do menu
        JButton menuButton = new JButton("Späť do menu");
        menuButton.setFont(new Font("Arial", Font.BOLD, 16));
        menuButton.setBackground(new Color(59, 89, 152));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(150, 40));
        menuButton.addActionListener(e -> this.ovladac.zobrazHlavneMenu());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(menuButton);
        panel.add(leftPanel, BorderLayout.WEST);

        // Pravá časť - tlačidlo ďalšia otázka
        JButton dalsiaButton = new JButton("Ďalšia otázka →");
        dalsiaButton.setFont(new Font("Arial", Font.BOLD, 16));
        dalsiaButton.setBackground(new Color(66, 103, 178));
        dalsiaButton.setForeground(Color.WHITE);
        dalsiaButton.setFocusPainted(false);
        dalsiaButton.setPreferredSize(new Dimension(180, 40));
        dalsiaButton.addActionListener(e -> {
            this.aktualnaOtazka = (this.aktualnaOtazka + 1) % this.otazky.size();
            ovladac.zobrazOtazku();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(dalsiaButton);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // Gettery
    public List<IZadanie> getOtazky() {
        return this.otazky;
    }
    public int getAktualnaOtazka() {
        return this.aktualnaOtazka;
    }
}