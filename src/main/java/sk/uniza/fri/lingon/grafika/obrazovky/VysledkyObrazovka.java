package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.komponenty.ModerneButtonUI;

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
import java.awt.GridLayout;

/**
 * Obrazovka zobrazujúca výsledky testu
 */
public class VysledkyObrazovka extends JPanel {
    private OvladacHlavnehoOkna ovladac;
    private VysledokTestu vysledok;

    /**
     * Konštruktor obrazovky výsledkov
     * @param ovladac Hlavný ovládač aplikácie
     * @param vysledok Výsledok testu
     */
    public VysledkyObrazovka(OvladacHlavnehoOkna ovladac, VysledokTestu vysledok) {
        this.ovladac = ovladac;
        this.vysledok = vysledok;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));
        this.inicializujUI();
    }

    /**
     * Inicializuje užívateľské rozhranie
     */
    private void inicializujUI() {
        // Horný panel s nadpisom
        JPanel hornyPanel = this.vytvorHornyPanel();
        add(hornyPanel, BorderLayout.NORTH);

        // Stredný panel s výsledkami
        JPanel strednyPanel = this.vytvorStrednyPanel();
        add(strednyPanel, BorderLayout.CENTER);

        // Dolný panel s tlačidlami
        JPanel dolnyPanel = this.vytvorDolnyPanel();
        add(dolnyPanel, BorderLayout.SOUTH);
    }

    /**
     * Vytvorí horný panel
     */
    private JPanel vytvorHornyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(41, 65, 114));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel nadpis = new JLabel("Výsledky testu");
        nadpis.setFont(new Font("Arial", Font.BOLD, 32));
        nadpis.setForeground(Color.WHITE);
        nadpis.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(nadpis);
        return panel;
    }

    /**
     * Vytvorí stredný panel s výsledkami
     */
    private JPanel vytvorStrednyPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Kategória
        JLabel kategoriaLabel = new JLabel("Kategória: " + this.vysledok.getKategoriaNazov());
        kategoriaLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        kategoriaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(kategoriaLabel);
        panel.add(Box.createVerticalStrut(20));

        // Celkový výsledok - veľké číslo
        double uspesnost = this.vysledok.getUspesnost();
        JLabel uspesnostLabel = new JLabel(String.format("%.0f%%", uspesnost));
        uspesnostLabel.setFont(new Font("Arial", Font.BOLD, 72));
        uspesnostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Farba podľa úspešnosti
        if (uspesnost >= 80) {
            uspesnostLabel.setForeground(new Color(76, 175, 80));
        } else if (uspesnost >= 60) {
            uspesnostLabel.setForeground(new Color(255, 152, 0));
        } else {
            uspesnostLabel.setForeground(new Color(244, 67, 54));
        }

        panel.add(uspesnostLabel);
        panel.add(Box.createVerticalStrut(30));

        // Detaily
        JPanel detailyPanel = new JPanel(new GridLayout(3, 2, 20, 10));
        detailyPanel.setOpaque(false);
        detailyPanel.setMaximumSize(new Dimension(400, 100));

        detailyPanel.add(this.vytvorDetailLabel("Správne odpovede:", new Color(76, 175, 80)));
        detailyPanel.add(this.vytvorDetailLabel(String.valueOf(this.vysledok.getSpravneOdpovede()), new Color(76, 175, 80)));

        detailyPanel.add(this.vytvorDetailLabel("Nesprávne odpovede:", new Color(244, 67, 54)));
        detailyPanel.add(this.vytvorDetailLabel(String.valueOf(this.vysledok.getNespravneOdpovede()), new Color(244, 67, 54)));

        detailyPanel.add(this.vytvorDetailLabel("Celkovo otázok:", Color.BLACK));
        detailyPanel.add(this.vytvorDetailLabel(String.valueOf(this.vysledok.getPocetOtazok()), Color.BLACK));

        panel.add(detailyPanel);
        panel.add(Box.createVerticalStrut(30));

        // Čas ukončenia
        JLabel casLabel = new JLabel("Ukončené: " + this.vysledok.getFormatovanyCas());
        casLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        casLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(casLabel);

        return panel;
    }

    /**
     * Vytvorí label pre detail
     */
    private JLabel vytvorDetailLabel(String text, Color farba) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(farba);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * Vytvorí dolný panel s tlačidlami
     */
    private JPanel vytvorDolnyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panel.setOpaque(false);

        // Tlačidlo späť do menu
        JButton menuButton = ModerneButtonUI.vytvorModerneTlacidlo("Hlavné menu", new Color(59, 89, 152));
        menuButton.addActionListener(e -> this.ovladac.zobrazHlavneMenu());

        panel.add(menuButton);

        return panel;
    }
}