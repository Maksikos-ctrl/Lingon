package sk.uniza.fri.lingon.pouzivatel.lekcia;

import sk.uniza.fri.lingon.core.AbstractneZadanie;
import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.core.PresnaZhodaStrategia;
import sk.uniza.fri.lingon.core.OdpovedDelegate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;

/**
 * Trieda reprezentujuca otazku s parovanim pojmov
 * Dalsia konkretna implementacia abstraktnej triedy AbstractneZadanie
 * Demonstruje polymorfizmus - rovnake rozhranie, ina implementacia
 */
public class ParovaciaOtazka extends AbstractneZadanie {
    private Map<String, String> spravnePary;
    private Map<JComboBox<String>, String> uiPrvkyNaPojmy;
    private OdpovedDelegate odpovedDelegate;

    /**
     * Konstruktor pre vytvorenie novej párovacej otázky
     * @param text Text otazky
     * @param spravnePary Mapa spravnych parov (kluc-hodnota)
     */
    public ParovaciaOtazka(String text, Map<String, String> spravnePary) {
        super(text);
        this.spravnePary = new HashMap<>(spravnePary);
        this.uiPrvkyNaPojmy = new HashMap<>();

        // Nastavenie odpovede pre abstraktnu triedu
        this.setOdpoved(spravnePary);

        // Vytvorenie vlastnej strategie pre kontrolu parovacich odpovedi
        this.setStrategia(new PresnaZhodaStrategia() {
            @Override
            public boolean validuj(String vstup, Object ocakavany) {
                // Pre parovaciu otazku pouzivame specialnu implementaciu
                // Toto je dalsi priklad polymorfizmu - prepisujeme metodu nadtriedy
                if (!(ocakavany instanceof Map) || vstup == null) {
                    return false;
                }

                try {
                    Map<String, String> odpovede = new HashMap<>();
                    String[] pary = vstup.split(";");
                    for (String par : pary) {
                        String[] prvky = par.split("=");
                        if (prvky.length == 2) {
                            odpovede.put(prvky[0].trim(), prvky[1].trim());
                        }
                    }


                    Map<String, String> ocakavanePary = (Map<String, String>)ocakavany;

                    return spravnePary.equals(odpovede);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String getNazovStrategie() {
                return "Kontrola párov";
            }
        });
    }

    /**
     * Nastaví delegáta pre spracovanie odpovede
     * @param delegate Delegát pre odpoveď
     */
    public void setOdpovedDelegate(OdpovedDelegate delegate) {
        this.odpovedDelegate = delegate;
    }

    /**
     * Zobrazi graficke rozhranie pre parovaciu otazku
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

        // Ikona spojenia (jednoduchý štvorec s ikonou)
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
                g2d.setColor(new Color(126, 87, 194)); // Fialová
                g2d.fillRoundRect(x, y, size, size, 6, 6);

                // Spojenie ikona (jednoduché)
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));

                int margin = size / 4;
                g2d.drawLine(x + margin, y + margin, x + size - margin, y + size - margin);
                g2d.drawLine(x + margin, y + size - margin, x + size - margin, y + margin);

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

        // Panel s parmi
        JPanel paryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        paryPanel.setOpaque(false);
        paryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Priprava zoznamu vsetkych hodnot pre dropdown
        List<String> vsetkyHodnoty = new ArrayList<>(this.spravnePary.values());
        Collections.shuffle(vsetkyHodnoty);

        // Vytvorenie UI prvkov pre parovanie
        this.uiPrvkyNaPojmy.clear();

        // Vytvorenie zoznamu kľúčov a premiešanie
        List<String> kluce = new ArrayList<>(this.spravnePary.keySet());
        Collections.shuffle(kluce);

        for (String kluc : kluce) {
            // Label pre kluc
            JLabel klucLabel = new JLabel(kluc);
            klucLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            klucLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            paryPanel.add(klucLabel);

            // Dropdown pre vyber hodnoty
            JComboBox<String> hodnotaComboBox = new JComboBox<>(vsetkyHodnoty.toArray(new String[0]));
            hodnotaComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            hodnotaComboBox.setSelectedIndex(-1); // Ziadna predvolena hodnota
            hodnotaComboBox.setBackground(Color.WHITE);
            paryPanel.add(hodnotaComboBox);

            // Ulozenie referencie na UI prvok pre neskoršiu kontrolu
            this.uiPrvkyNaPojmy.put(hodnotaComboBox, kluc);
        }

        JScrollPane scrollPane = new JScrollPane(paryPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tlacidlo pre potvrdenie
        JButton potvrditButton = new JButton("Potvrdiť");
        potvrditButton.setFont(new Font("Arial", Font.BOLD, 14));
        potvrditButton.setBackground(new Color(76, 175, 80));
        potvrditButton.setForeground(Color.WHITE);
        potvrditButton.setFocusPainted(false);

        potvrditButton.addActionListener(e -> {
            // Ziskanie odpovedi od uzivatela
            Map<String, String> odpovede = new HashMap<>();
            boolean vsetkyVyplnene = true;

            for (Map.Entry<JComboBox<String>, String> entry : this.uiPrvkyNaPojmy.entrySet()) {
                JComboBox<String> comboBox = entry.getKey();
                String kluc = entry.getValue();

                if (comboBox.getSelectedItem() == null) {
                    vsetkyVyplnene = false;
                    break;
                }

                odpovede.put(kluc, comboBox.getSelectedItem().toString());
            }

            if (vsetkyVyplnene) {
                // Prevod na retazec pre kontrolu
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : odpovede.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
                }
                String odpovedeString = sb.toString();

                boolean jeSpravna = this.skontrolujOdpoved(odpovedeString);

                // Zablokovanie prvkov a zvýraznenie správnych/nesprávnych odpovedí
                JPanel vysledkyPanel = new JPanel();
                vysledkyPanel.setLayout(new BoxLayout(vysledkyPanel, BoxLayout.Y_AXIS));
                vysledkyPanel.setOpaque(false);
                vysledkyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

                for (Map.Entry<JComboBox<String>, String> entry : this.uiPrvkyNaPojmy.entrySet()) {
                    JComboBox<String> comboBox = entry.getKey();
                    String kluc = entry.getValue();
                    String vybranaHodnota = comboBox.getSelectedItem().toString();
                    String spravnaHodnota = this.spravnePary.get(kluc);

                    // Zablokovanie combo boxu
                    comboBox.setEnabled(false);

                    // Zvýraznenie správnej/nesprávnej odpovede
                    if (vybranaHodnota.equals(spravnaHodnota)) {
                        comboBox.setBackground(new Color(232, 245, 233)); // Svetlo zelená
                        comboBox.setForeground(new Color(76, 175, 80));
                    } else {
                        comboBox.setBackground(new Color(253, 236, 234)); // Svetlo červená
                        comboBox.setForeground(new Color(244, 67, 54));

                        // Pridanie informácie o správnej odpovedi
                        JLabel spravnaLabel = new JLabel(kluc + " = " + spravnaHodnota + " (správne)");
                        spravnaLabel.setForeground(new Color(76, 175, 80));
                        spravnaLabel.setFont(new Font("Arial", Font.BOLD, 12));
                        spravnaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        vysledkyPanel.add(spravnaLabel);
                        vysledkyPanel.add(Box.createVerticalStrut(5));
                    }
                }

                if (!(vysledkyPanel.getComponents().length == 0)) {
                    JScrollPane vysledkyScrollPane = new JScrollPane(vysledkyPanel);
                    vysledkyScrollPane.setBorder(null);
                    vysledkyScrollPane.setOpaque(false);
                    vysledkyScrollPane.getViewport().setOpaque(false);
                    vysledkyScrollPane.setPreferredSize(new Dimension(400, 100));

                    JPanel vysledkyContainer = new JPanel(new BorderLayout());
                    vysledkyContainer.setOpaque(false);
                    vysledkyContainer.setBorder(BorderFactory.createTitledBorder("Správne odpovede"));
                    vysledkyContainer.add(vysledkyScrollPane, BorderLayout.CENTER);

                    panel.add(vysledkyContainer, BorderLayout.SOUTH);
                }

                // Zablokovanie tlačidla
                potvrditButton.setEnabled(false);

                // Použitie delegáta pre odpoveď
                if (this.odpovedDelegate != null) {
                    this.odpovedDelegate.spracujOdpoved(odpovedeString, jeSpravna, this.getTypOtazky());
                }

                panel.revalidate();
                panel.repaint();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(panel),
                        "Prosím, vyplňte všetky páry",
                        "Upozornenie",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // Panel pre tlačidlo
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(potvrditButton);

        // Pridanie tlačidla do hlavného panelu
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        kontajner.pridajKomponent(panel);
    }

    /**
     * Vrati typ otazky
     * @return Typ otazky
     */
    @Override
    public String getTypOtazky() {
        return "Párovacia otázka";
    }
}