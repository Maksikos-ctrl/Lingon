package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaObrazoviek;
import sk.uniza.fri.lingon.grafika.komponenty.ModerneButtonUI;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

/**
 * Obrazovka profilu používateľa
 * Zobrazuje informácie o používateľovi
 */
public class ProfilObrazovka {
    private final OvladacHlavnehoOkna ovladac;
    private final Pouzivatel pouzivatel;

    /**
     * Konštruktor profilu obrazovky
     * @param ovladac Hlavný ovládač aplikácie
     * @param pouzivatel Používateľ ktorého profil sa zobrazuje
     */
    public ProfilObrazovka(OvladacHlavnehoOkna ovladac, Pouzivatel pouzivatel) {
        this.ovladac = ovladac;
        this.pouzivatel = pouzivatel;
    }

    /**
     * Zobrazí profil používateľa
     */
    public void zobraz() {
        SpravcaObrazoviek spravcaObrazoviek = new SpravcaObrazoviek(this.ovladac);
        spravcaObrazoviek.odstranNavigaciuPanel();
        this.ovladac.getKontajner().vymazObsah();

        JPanel panel = this.vytvorProfilPanel();
        this.ovladac.getKontajner().pridajKomponent(panel);
        this.ovladac.getHlavneOkno().revalidate();
        this.ovladac.getHlavneOkno().repaint();
    }

    /**
     * Vytvorí panel profilu
     * @return Panel profilu
     */
    private JPanel vytvorProfilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(new Color(240, 240, 245));

        // Nadpis
        JLabel nadpisLabel = new JLabel("Profil používateľa");
        nadpisLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nadpisLabel.setForeground(new Color(59, 89, 152));
        nadpisLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(nadpisLabel, BorderLayout.NORTH);

        // Stredný panel s avatárom a informáciami
        JPanel strednyPanel = new JPanel(new BorderLayout());
        strednyPanel.setOpaque(false);

        // Avatar panel - zobrazenie kruhového avatára
        JPanel avatarPanel = this.vytvorProfilFotkaPanel();
        strednyPanel.add(avatarPanel, BorderLayout.NORTH);

        // Informácie o používateľovi
        JPanel infoPanel = this.vytvorInfoPanel();
        strednyPanel.add(infoPanel, BorderLayout.CENTER);

        panel.add(strednyPanel, BorderLayout.CENTER);

        // Tlačidlá
        JPanel buttonPanel = this.vytvorButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Vytvára panel s profilovou fotkou (avatar)
     * @return Panel s avatárom
     */
    private JPanel vytvorProfilFotkaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Avatar - kruh s písmenom
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = this.getWidth();
                int height = this.getHeight();
                int diameter = Math.min(width, height);

                // Kruhový tvar
                g2d.setColor(new Color(59, 89, 152)); // Modrá farba
                g2d.fillOval(0, 0, diameter, diameter);

                // Písmeno používateľa
                String letter = "";
                if (ProfilObrazovka.this.pouzivatel != null && ProfilObrazovka.this.pouzivatel.getMeno() != null && !ProfilObrazovka.this.pouzivatel.getMeno().isEmpty()) {
                    letter = ProfilObrazovka.this.pouzivatel.getMeno().substring(0, 1).toUpperCase();
                }

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, diameter / 2));

                FontMetrics fm = g2d.getFontMetrics();
                int letterWidth = fm.stringWidth(letter);
                int letterHeight = fm.getHeight();

                g2d.drawString(letter, (diameter - letterWidth) / 2,
                        (diameter - letterHeight) / 2 + fm.getAscent());

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 120);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(120, 120);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(120, 120);
            }
        };

        panel.add(avatarPanel);
        return panel;
    }

    /**
     * Vytvorí panel s informáciami
     * @return Info panel
     */
    private JPanel vytvorInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Meno používateľa
        JLabel menoLabel = new JLabel("Meno: " + this.pouzivatel.getMeno());
        menoLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        menoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(menoLabel);
        panel.add(Box.createVerticalStrut(10));

        // Email používateľa
        JLabel emailLabel = new JLabel("Email: " + this.pouzivatel.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(30));

        // XP - väčšie a farebné
        JLabel xpLabel = new JLabel("XP: " + this.pouzivatel.getCelkoveXP());
        xpLabel.setFont(new Font("Arial", Font.BOLD, 26));
        xpLabel.setForeground(new Color(76, 175, 80));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(xpLabel);
        panel.add(Box.createVerticalStrut(10));

        // Úroveň - s vizuálnym indikátorom progresu
        int uroven = this.pouzivatel.getUroven();
        JLabel urovenLabel = new JLabel("Úroveň: " + uroven);
        urovenLabel.setFont(new Font("Arial", Font.BOLD, 22));
        urovenLabel.setForeground(new Color(59, 89, 152));
        urovenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(urovenLabel);

        // Progress k ďalšej úrovni
        JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
        progressPanel.setOpaque(false);
        progressPanel.setMaximumSize(new Dimension(300, 25));
        progressPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = this.getJProgressBar(uroven);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(10)); // pridáme medzeru pred progress barom
        panel.add(progressPanel);
        panel.add(Box.createVerticalStrut(30));

        // Úspešnosť
        JLabel uspesnostLabel = new JLabel("Úspešnosť: " + this.pouzivatel.getUspesnost() + "%");
        uspesnostLabel.setFont(new Font("Arial", Font.BOLD, 18));
        uspesnostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(uspesnostLabel);

        return panel;
    }

    private JProgressBar getJProgressBar(int uroven) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Lepšie formátovanie progress baru
        progressBar.setPreferredSize(new Dimension(300, 20));
        progressBar.setBackground(new Color(230, 230, 240));
        progressBar.setForeground(new Color(41, 128, 185));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));

        // Výpočet progresu k ďalšej úrovni
        int xp = this.pouzivatel.getCelkoveXP();
        int cieloveXP;
        int startXP = 0;

        if (uroven == 0) {
            cieloveXP = 30;
        } else if (uroven == 1) {
            startXP = 30;
            cieloveXP = 50;
        } else if (uroven == 2) {
            startXP = 50;
            cieloveXP = 80;
        } else {
            // Maximálna úroveň
            startXP = 80;
            cieloveXP = 100; // Len pre vizuálny indikátor
        }

        // Výpočet percentuálneho progresu
        int progress;
        if (uroven < 3) {
            progress = (int)((float)(xp - startXP) / (cieloveXP - startXP) * 100);
            progress = Math.min(100, Math.max(0, progress));
            progressBar.setValue(progress);

            // Lepší popis progress baru
            progressBar.setString(xp + " / " + cieloveXP + " XP");
        } else {
            progressBar.setValue(100);
            progressBar.setString("Maximálna úroveň dosiahnutá!");
        }
        return progressBar;
    }

    /**
     * Vytvorí panel s tlačidlami
     * @return Button panel
     */
    private JPanel vytvorButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Dynamické tlačidlo - ak sú otázky, tak "Späť do testu", inak "Hlavné menu"
        JButton spatButton;
        if (this.ovladac.getSpravcaKvizu().getOtazky() != null && !this.ovladac.getSpravcaKvizu().getOtazky().isEmpty()) {
            spatButton = ModerneButtonUI.vytvorModerneTlacidlo("← Späť do testu", new Color(76, 175, 80));
            spatButton.addActionListener(_ -> this.ovladac.zobrazOtazku());
        } else {
            spatButton = ModerneButtonUI.vytvorModerneTlacidlo("Hlavné menu", new Color(59, 89, 152));
            spatButton.addActionListener(_ -> this.ovladac.zobrazHlavneMenu());
        }

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(spatButton);
        buttonPanel.add(leftPanel, BorderLayout.WEST);

        return buttonPanel;
    }
}