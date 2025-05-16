package sk.uniza.fri.lingon.pouzivatel.lekcia;

import sk.uniza.fri.lingon.core.AbstractneZadanie;
import sk.uniza.fri.lingon.core.OdpovedDelegate;
import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.core.NerozlisujucaStrategia;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Trieda reprezentujuca otazku, kde uzivatel musi vpisat odpoved
 * Konkretna implementacia abstraktnej triedy AbstractneZadanie
 */
public class VpisovaciaOtazka extends AbstractneZadanie {
    private String spravnaOdpoved;
    private OdpovedDelegate odpovedDelegate;

    /**
     * Konstruktor pre vytvorenie novej vpisovacej otazky
     * @param text Text otazky
     * @param spravnaOdpoved Spravna odpoved
     */
    public VpisovaciaOtazka(String text, String spravnaOdpoved) {
        super(text);
        this.spravnaOdpoved = spravnaOdpoved;

        // Nastavenie odpovede pre abstraktnu triedu
        this.setOdpoved(spravnaOdpoved);

        // Nastavenie strategie kontroly odpovedi - nerozlišujeme veľké/malé písmená
        this.setStrategia(new NerozlisujucaStrategia());
    }

    /**
     * Nastaví delegáta pre spracovanie odpovede
     * @param delegate Delegát pre odpoveď
     */
    public void setOdpovedDelegate(OdpovedDelegate delegate) {
        this.odpovedDelegate = delegate;
    }

    /**
     * Zobrazi graficke rozhranie pre vpisovaciu otazku
     * Implementacia abstraktnej metody z AbstractneZadanie
     * @param kontajner UI kontajner pre zobrazenie
     */
    @Override
    public void zobrazGrafiku(UIKontajner kontajner) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);

        // Text otazky s ikonou
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        // Ikona pera (jednoduchý štvorec s perom)
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Štvorec
                g2d.setColor(new Color(233, 151, 0));
                g2d.fillRoundRect(x, y, size, size, 6, 6);

                // Pero ikona (jednoduchá)
                g2d.setColor(Color.WHITE);
                int penSize = size / 2;
                int startX = x + (size - penSize) / 2;
                int startY = y + (size - penSize) / 2;

                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(startX, startY + penSize, startX + penSize, startY);
                g2d.drawLine(startX, startY + penSize, startX + penSize / 4, startY + penSize - penSize / 4);
                g2d.drawLine(startX + penSize, startY, startX + penSize - penSize / 4, startY + penSize / 4);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }
        };
        headerPanel.add(iconPanel, BorderLayout.WEST);

        // Text otázky
        JLabel otazkaLabel = new JLabel(this.getText());
        otazkaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(otazkaLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel pre vstup
        JPanel vstupPanel = new JPanel(new BorderLayout(5, 15));
        vstupPanel.setOpaque(false);
        vstupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel vstupLabel = new JLabel("Napíšte vašu odpoveď:");
        vstupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        vstupPanel.add(vstupLabel, BorderLayout.NORTH);

        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        vstupPanel.add(textField, BorderLayout.CENTER);

        panel.add(vstupPanel, BorderLayout.CENTER);

        // Tlacidlo pre potvrdenie
        JButton potvrditButton = new JButton("Potvrdiť");
        potvrditButton.setFont(new Font("Arial", Font.BOLD, 14));
        potvrditButton.setBackground(new Color(76, 175, 80));
        potvrditButton.setForeground(Color.WHITE);
        potvrditButton.setFocusPainted(false);

        potvrditButton.addActionListener(e -> {
            String odpoved = textField.getText();
            if (odpoved != null && !odpoved.trim().isEmpty()) {
                boolean jeSpravna = this.skontrolujOdpoved(odpoved);

                // Zablokovanie vstupu po odpovedi
                textField.setEditable(false);

                // Zobrazenie správnej odpovede
                JLabel spravnaOdpovedLabel = new JLabel("Správna odpoveď: " + this.spravnaOdpoved);
                spravnaOdpovedLabel.setFont(new Font("Arial", Font.BOLD, 14));
                spravnaOdpovedLabel.setForeground(new Color(76, 175, 80));
                vstupPanel.add(spravnaOdpovedLabel, BorderLayout.SOUTH);

                // Zmena farby vstupu podľa správnosti
                if (jeSpravna) {
                    textField.setBackground(new Color(232, 245, 233)); // Svetlo zelená
                    textField.setForeground(new Color(76, 175, 80));
                } else {
                    textField.setBackground(new Color(253, 236, 234)); // Svetlo červená
                    textField.setForeground(new Color(244, 67, 54));
                }

                // Zablokovanie tlačidla
                potvrditButton.setEnabled(false);

                // Použitie delegáta pre odpoveď
                if (this.odpovedDelegate != null) {
                    this.odpovedDelegate.spracujOdpoved(odpoved, jeSpravna, this.getTypOtazky());
                }
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(panel),
                        "Prosím, zadajte odpoveď",
                        "Upozornenie",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // Panel pre tlačidlo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(potvrditButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        kontajner.pridajKomponent(panel);
    }

    /**
     * Vrati typ otazky
     * @return Typ otazky
     */
    @Override
    public String getTypOtazky() {
        return "Vpisovacia otázka";
    }
}