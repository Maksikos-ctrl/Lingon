package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Úvodná obrazovka aplikácie
 * Zobrazuje uvítaciu obrazovku s tlačidlom PLAY
 */
public class UvodnaObrazovka extends JPanel {
    private OvladacHlavnehoOkna ovladac;

    /**
     * Konštruktor úvodnej obrazovky
     * @param ovladac Hlavný ovládač aplikácie
     */
    public UvodnaObrazovka(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
        setLayout(new BorderLayout());
        this.inicializujUI();
    }

    /**
     * Inicializuje užívateľské rozhranie
     */
    private void inicializujUI() {
        setLayout(new BorderLayout());

        // Panel pre centrálny obsah
        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();

                // Vytvorenie gradientu
                int w = getWidth();
                int h = getHeight();

                // Animovaný gradient s časom
                long time = System.currentTimeMillis() % 10000L;  // 10-sekundový cyklus
                float ratio = (float) time / 10000f;

                // Farby pre gradient (rôzne odtiene modrej)
                Color color1 = new Color(41, 65, 114);  // Tmavá modrá
                Color color2 = new Color(65, 105, 225); // Kráľovská modrá
                Color color3 = new Color(100, 149, 237); // Kukličková modrá

                // Vypočítame pozíciu stredu gradientu, ktorá sa pohybuje
                float centerX = w * 0.5f + (float) Math.sin(ratio * 2 * Math.PI) * w * 0.3f;
                float centerY = h * 0.5f + (float) Math.cos(ratio * 2 * Math.PI) * h * 0.3f;

                // Vytvorenie radiálneho gradientu
                RadialGradientPaint paint = new RadialGradientPaint(
                        centerX, centerY, Math.max(w, h) * 0.8f,
                        new float[]{0f, 0.5f, 1.0f},
                        new Color[]{color2, color1, color3}
                );

                g2d.setPaint(paint);
                g2d.fillRect(0, 0, w, h);

                // Pridajte vzor (sieť) cez gradient
                g2d.setColor(new Color(255, 255, 255, 20)); // Biela s priehľadnosťou
                int gridSize = 30;

                for (int i = 0; i < w; i += gridSize) {
                    g2d.drawLine(i, 0, i, h);
                }

                for (int i = 0; i < h; i += gridSize) {
                    g2d.drawLine(0, i, w, i);
                }

                g2d.dispose();
            }
        };
        contentPanel.setOpaque(false);

        // Nastavenie repaintu pre animáciu
        Timer animationTimer = new Timer(50, e -> contentPanel.repaint());
        animationTimer.start();

        // Logo a nadpis
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("LINGON");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Interaktívne kvízy");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 32));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        // Nastavenie rozloženia
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        logoPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        logoPanel.add(subtitleLabel, gbc);

        // Tlačidlo PLAY s animáciou
        JButton playButton = new JButton("PLAY");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.setForeground(Color.WHITE);
        playButton.setBackground(new Color(76, 175, 80));
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);
        playButton.setPreferredSize(new Dimension(200, 60));
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efekt prechodov
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playButton.setBackground(new Color(46, 145, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                playButton.setBackground(new Color(76, 175, 80));
            }
        });

        // Akcia po kliknutí
        playButton.addActionListener(e -> {
            if (ovladac.getAktualnyPouzivatel() == null) {
                ovladac.getSpravcaPouzivatela().zobrazDialogNovehoPouzivatela();
            } else {
                ovladac.zobrazHlavneMenu();
            }
        });

        // Pridanie komponentov do panelu
        gbc.gridy = 2;
        gbc.insets = new Insets(40, 0, 0, 0);
        logoPanel.add(playButton, gbc);

        contentPanel.add(logoPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Copyright v päte
        JLabel copyrightLabel = new JLabel("© 2025 Lingon - Projekt pre interaktívne kvízy");
        copyrightLabel.setForeground(new Color(255, 255, 255, 150));
        copyrightLabel.setHorizontalAlignment(JLabel.CENTER);
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(copyrightLabel, BorderLayout.CENTER);

        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Vytvorí panel s logom
     * @return Logo panel
     */
    private JPanel vytvorLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);

        JLabel nadpisLabel = new JLabel("LINGON");
        nadpisLabel.setFont(new Font("Arial", Font.BOLD, 60));
        nadpisLabel.setForeground(Color.WHITE);
        nadpisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(nadpisLabel);

        JLabel podnadpisLabel = new JLabel("Učenie jazykov");
        podnadpisLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        podnadpisLabel.setForeground(new Color(255, 255, 255, 220));
        podnadpisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(podnadpisLabel);

        return logoPanel;
    }

    /**
     * Vytvorí PLAY tlačidlo
     * @return Play tlačidlo
     */
    private JButton vytvorPlayButton() {
        JButton hratButton = new JButton("PLAY");
        hratButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hratButton.setFont(new Font("Arial", Font.BOLD, 28));
        hratButton.setPreferredSize(new Dimension(220, 80));
        hratButton.setMaximumSize(new Dimension(220, 80));

        // Vlastný UI pre tlačidlo
        hratButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(76, 175, 80),
                        0, h, new Color(56, 142, 60)
                );

                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillRoundRect(0, 0, w, h / 2, 20, 20);

                FontMetrics fm = g2d.getFontMetrics(c.getFont());
                String text = "PLAY";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                g2d.setColor(Color.WHITE);
                g2d.setFont(c.getFont());
                g2d.drawString(text, (w - textWidth) / 2, h / 2 + textHeight / 4);

                g2d.dispose();
            }
        });

        hratButton.addActionListener(e -> {
            if (this.ovladac.getAktualnyPouzivatel() == null) {
                this.ovladac.getSpravcaPouzivatela().zobrazDialogNovehoPouzivatela();
            } else {
                this.ovladac.zobrazHlavneMenu();
            }
        });

        hratButton.setBorderPainted(false);
        hratButton.setFocusPainted(false);
        hratButton.setContentAreaFilled(false);

        // Hover efekt
        hratButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hratButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                hratButton.setFont(new Font("Arial", Font.BOLD, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                hratButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                hratButton.setFont(new Font("Arial", Font.BOLD, 28));
            }
        });

        return hratButton;
    }

    /**
     * Vytvorí footer panel
     * @return Footer panel
     */
    private JPanel vytvorFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 Lingon - Projekt pre výuku jazykov");
        footerLabel.setForeground(new Color(255, 255, 255, 180));
        footerPanel.add(footerLabel);
        return footerPanel;
    }

    /**
     * Prekreslí pozadie s gradientom
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        Color color1 = new Color(41, 65, 114);
        Color color2 = new Color(84, 125, 190);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
    }
}