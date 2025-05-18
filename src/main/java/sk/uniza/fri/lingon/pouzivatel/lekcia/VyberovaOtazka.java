package sk.uniza.fri.lingon.pouzivatel.lekcia;

import sk.uniza.fri.lingon.core.AbstractneZadanie;
import sk.uniza.fri.lingon.core.OdpovedDelegate;
import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.core.PresnaZhodaStrategia;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trieda reprezentujuca otazku s vyberom z viacerych moznosti
 * Konkretna implementacia abstraktnej triedy AbstractneZadanie
 */
public class VyberovaOtazka extends AbstractneZadanie {
    private final List<String> moznosti;
    private final String spravnaOdpoved;
    private OdpovedDelegate odpovedDelegate;

    /**
     * Konstruktor pre vytvorenie novej otazky s vyberom
     * @param text Text otazky
     * @param moznosti Zoznam moznosti
     * @param spravnaOdpoved Spravna odpoved
     */
    public VyberovaOtazka(String text, List<String> moznosti, String spravnaOdpoved) {
        super(text);
        this.moznosti = new ArrayList<>(moznosti);
        this.spravnaOdpoved = spravnaOdpoved;

        // Nastavenie odpovede pre abstraktnu triedu
        this.setOdpoved(spravnaOdpoved);

        // Nastavenie strategie kontroly odpovedi
        this.setStrategia(new PresnaZhodaStrategia());
    }

    /**
     * Nastaví delegáta pre spracovanie odpovede
     * @param delegate Delegát pre odpoveď
     */
    public void setOdpovedDelegate(OdpovedDelegate delegate) {
        this.odpovedDelegate = delegate;
    }

    /**
     * Zobrazi graficke rozhranie pre otazku s vyberom
     * Implementacia abstraktnej metody z AbstractneZadanie
     * @param kontajner UI kontajner pre zobrazenie
     */
    @Override
    public void zobrazGrafiku(UIKontajner kontajner) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);

        // Text otazky s ikonou otáznika
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        // Ikona otáznika
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(this.getWidth(), this.getHeight()) - 4;
                int x = (this.getWidth() - size) / 2;
                int y = (this.getHeight() - size) / 2;

                // Kruh
                g2d.setColor(new Color(66, 103, 178));
                g2d.fillOval(x, y, size, size);

                // Otáznik
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "?";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                g2d.drawString(text, x + (size - textWidth) / 2,
                        y + size / 2 + textHeight / 4);

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

        // Panel s moznostami
        JPanel moznostiPanel = new JPanel();
        moznostiPanel.setLayout(new BoxLayout(moznostiPanel, BoxLayout.Y_AXIS));
        moznostiPanel.setOpaque(false);
        moznostiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Zamiesanie moznosti
        List<String> zamiesaneMoznosti = new ArrayList<>(this.moznosti);
        Collections.shuffle(zamiesaneMoznosti);

        ButtonGroup buttonGroup = new ButtonGroup();
        List<JRadioButton> radioButtons = new ArrayList<>();

        for (String moznost : zamiesaneMoznosti) {
            JPanel optionPanel = new JPanel(new BorderLayout());
            optionPanel.setOpaque(false);
            optionPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JRadioButton radioButton = this.getJRadioButton(moznost, radioButtons);

            buttonGroup.add(radioButton);
            radioButtons.add(radioButton);

            optionPanel.add(radioButton, BorderLayout.CENTER);
            moznostiPanel.add(optionPanel);
        }

        JScrollPane scrollPane = new JScrollPane(moznostiPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);

        kontajner.pridajKomponent(panel);
    }

    private JRadioButton getJRadioButton(String moznost, List<JRadioButton> radioButtons) {
        JRadioButton radioButton = new JRadioButton(moznost);
        radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        radioButton.setActionCommand(moznost);
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(false);

        // Automatické odoslanie po výbere
        radioButton.addActionListener(_ -> {
            // Počkáme 500ms pred spracovaním
            Timer timer = new Timer(500, _ -> {
                this.spracujOdpoved(radioButton.getActionCommand(), radioButtons);
            });
            timer.setRepeats(false);
            timer.start();
        });
        return radioButton;
    }

    /**
     * Spracuje odpoveď
     */
    private void spracujOdpoved(String vybranaOdpoved, List<JRadioButton> radioButtons) {
        boolean jeSpravna = this.skontrolujOdpoved(vybranaOdpoved);

        // Zablokovanie všetkých možností
        for (JRadioButton button : radioButtons) {
            button.setEnabled(false);

            // Zvýraznenie správnej odpovede
            if (button.getActionCommand().equals(this.spravnaOdpoved)) {
                button.setForeground(new Color(76, 175, 80));
                button.setFont(new Font("Arial", Font.BOLD, 14));
            }

            // Zvýraznenie nesprávnej vybranej odpovede
            if (!jeSpravna && button.getActionCommand().equals(vybranaOdpoved)) {
                button.setForeground(new Color(244, 67, 54));
            }
        }

        // Získame ovládač z kontajnera
        OvladacHlavnehoOkna ovladac = this.getOvladac();

        // Ukážeme správu o výsledku
        SwingUtilities.invokeLater(() -> {
            if (jeSpravna) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(radioButtons.getFirst()),
                        "Správna odpoveď! +10 XP",
                        "Výborne!",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(radioButtons.getFirst()),
                        "Nesprávna odpoveď!",
                        "Škoda",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Použitie delegáta pre odpoveď
        if (this.odpovedDelegate != null) {
            this.odpovedDelegate.spracujOdpoved(vybranaOdpoved, jeSpravna, this.getTypOtazky());
        }
    }

    /**
     * Vrati typ otazky
     * @return Typ otazky
     */
    @Override
    public String getTypOtazky() {
        return "Výberová otázka";
    }

    /**
     * Vrati zoznam moznosti
     * @return Zoznam moznosti
     */
    public List<String> getMoznosti() {
        return Collections.unmodifiableList(this.moznosti);
    }
}