package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

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
        // Stredový panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        centerPanel.setOpaque(false);

        // Logo panel
        JPanel logoPanel = this.vytvorLogoPanel();
        centerPanel.add(logoPanel);
        centerPanel.add(Box.createVerticalStrut(70));

        // Play tlačidlo
        JButton hratButton = this.vytvorPlayButton();
        centerPanel.add(hratButton);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = this.vytvorFooter();
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