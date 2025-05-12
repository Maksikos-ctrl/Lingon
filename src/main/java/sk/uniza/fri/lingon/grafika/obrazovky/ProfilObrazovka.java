package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaObrazoviek;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Obrazovka profilu používateľa
 * Zobrazuje informácie o používateľovi
 */
public class ProfilObrazovka {
    private OvladacHlavnehoOkna ovladac;
    private Pouzivatel pouzivatel;

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

        // Informácie o používateľovi
        JPanel infoPanel = this.vytvorInfoPanel();
        panel.add(infoPanel, BorderLayout.CENTER);

        // Tlačidlá
        JPanel buttonPanel = this.vytvorButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Vytvorí panel s informáciami
     * @return Info panel
     */
    private JPanel vytvorInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        infoPanel.setOpaque(false);

        // Avatar
        JPanel avatarPanel = this.vytvorAvatar();
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(avatarPanel);
        infoPanel.add(Box.createVerticalStrut(20));

        // Meno
        JLabel menoLabel = new JLabel("Meno: " + this.pouzivatel.getMeno());
        menoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        menoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(menoLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Email
        JLabel emailLabel = new JLabel("Email: " + this.pouzivatel.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(20));

        // XP a úroveň
        JLabel xpLabel = new JLabel("XP: " + this.pouzivatel.getCelkoveXP());
        xpLabel.setFont(new Font("Arial", Font.BOLD, 22));
        xpLabel.setForeground(new Color(76, 175, 80));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(xpLabel);

        int uroven = this.pouzivatel.getCelkoveXP() / 100;
        JLabel urovenLabel = new JLabel("Úroveň: " + uroven);
        urovenLabel.setFont(new Font("Arial", Font.BOLD, 22));
        urovenLabel.setForeground(new Color(59, 89, 152));
        urovenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(urovenLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Úspešnosť
        JLabel uspesnostLabel = new JLabel("Úspešnosť: " + this.pouzivatel.getUspesnost() + "%");
        uspesnostLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        uspesnostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(uspesnostLabel);

        return infoPanel;
    }

    /**
     * Vytvorí avatar panel
     * @return Avatar panel
     */
    private JPanel vytvorAvatar() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2d.setColor(new Color(59, 89, 152));
                g2d.fillOval(x, y, size, size);

                String text = String.valueOf(ProfilObrazovka.this.pouzivatel.getMeno().charAt(0)).toUpperCase();
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text,
                        x + (size - fm.stringWidth(text)) / 2,
                        y + size / 2 + fm.getHeight() / 4);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }
        };
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
        JButton spatButton = new JButton();
        if (this.ovladac.getSpravcaKvizu().getOtazky() != null && !this.ovladac.getSpravcaKvizu().getOtazky().isEmpty()) {
            spatButton.setText("← Späť do testu");
            spatButton.addActionListener(e -> this.ovladac.zobrazOtazku());
        } else {
            spatButton.setText("Hlavné menu");
            spatButton.addActionListener(e -> this.ovladac.zobrazHlavneMenu());
        }
        spatButton.setFont(new Font("Arial", Font.BOLD, 16));
        spatButton.setBackground(new Color(59, 89, 152));
        spatButton.setForeground(Color.WHITE);
        spatButton.setFocusPainted(false);
        spatButton.setPreferredSize(new Dimension(180, 40));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(spatButton);
        buttonPanel.add(leftPanel, BorderLayout.WEST);

        return buttonPanel;
    }
}