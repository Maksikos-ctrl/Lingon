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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

/**
 * Obrazovka zobrazujúca históriu testov
 */
public class HistoriaObrazovka extends JPanel {
    private final OvladacHlavnehoOkna ovladac;
    private JTable tabulka;

    /**
     * Konštruktor obrazovky histórie
     * @param ovladac Hlavný ovládač aplikácie
     */
    public HistoriaObrazovka(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;

        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 240, 245));
        this.inicializujUI();
    }

    /**
     * Inicializuje užívateľské rozhranie
     */
    private void inicializujUI() {
        // Horný panel s nadpisom
        JPanel hornyPanel =  this.vytvorHornyPanel();
        this.add(hornyPanel, BorderLayout.NORTH);

        // Kontrola histórie pred vytvorením stredného panelu
        List<VysledokTestu> historia =  this.ovladac.getSpravcaHistorie().getHistoria();

        if (historia == null || historia.isEmpty()) {
            // Zobrazíme správu o prázdnej histórii
            JPanel prazdnyPanel = new JPanel(new GridBagLayout());
            prazdnyPanel.setOpaque(false);
            prazdnyPanel.setPreferredSize(new Dimension(800, 400));

            JLabel prazdnaLabel = new JLabel("Zatiaľ neboli dokončené žiadne testy");
            prazdnaLabel.setFont(new Font("Arial", Font.ITALIC, 20));
            prazdnaLabel.setForeground(new Color(108, 117, 125));

            JLabel infoLabel = new JLabel("Dokončite aspoň jeden test pre zobrazenie histórie");
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            infoLabel.setForeground(new Color(108, 117, 125));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(prazdnaLabel);
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(infoLabel);

            prazdnaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            prazdnyPanel.add(textPanel);
            this.add(prazdnyPanel, BorderLayout.CENTER);
        } else {
            // Stredný panel s tabuľkou
            JPanel strednyPanel =  this.vytvorStrednyPanel();
            this.add(strednyPanel, BorderLayout.CENTER);

            // Načítať históriu do tabuľky
            this.nacitajHistoriu();
        }

        // Dolný panel s tlačidlami
        JPanel dolnyPanel =  this.vytvorDolnyPanel();
        this.add(dolnyPanel, BorderLayout.SOUTH);
    }

    /**
     * Vytvorí horný panel
     */
    private JPanel vytvorHornyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(41, 65, 114));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel nadpis = new JLabel("História testov");
        nadpis.setFont(new Font("Arial", Font.BOLD, 32));
        nadpis.setForeground(Color.WHITE);
        nadpis.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(nadpis);
        return panel;
    }

    /**
     * Vytvorí stredný panel s tabuľkou
     */
    private JPanel vytvorStrednyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Skontrolujeme či existuje história
        List<VysledokTestu> historia =  this.ovladac.getSpravcaHistorie().getHistoria();

        if (historia == null || historia.isEmpty()) {
            // Prázdna história - zobrazíme informatívnu správu
            JPanel prazdnyPanel = new JPanel(new GridBagLayout());
            prazdnyPanel.setOpaque(false);

            JLabel prazdnaLabel = new JLabel("Zatiaľ neboli dokončené žiadne testy");
            prazdnaLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            prazdnaLabel.setForeground(new Color(108, 117, 125));
            prazdnyPanel.add(prazdnaLabel);

            panel.add(prazdnyPanel, BorderLayout.CENTER);
        } else {
            // Vytvorenie tabuľky
            String[] stlpce = {"Kategória", "Dátum a čas", "Otázky", "Správne", "Nesprávne", "Úspešnosť"};
            DefaultTableModel model = new DefaultTableModel(stlpce, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            this.tabulka = new JTable(model);
            this.tabulka.setFont(new Font("Arial", Font.PLAIN, 14));
            this.tabulka.setRowHeight(30);
            this.tabulka.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
            this.tabulka.getTableHeader().setBackground(new Color(41, 65, 114));
            this.tabulka.getTableHeader().setForeground(Color.WHITE);
            this.tabulka.getTableHeader().setReorderingAllowed(false); // Zakázanie presúvania stĺpcov
            this.tabulka.getTableHeader().setResizingAllowed(true); // Povolenie zmeny veľkosti stĺpcov

            // Zarovnanie hodnôt v stĺpcoch
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            this.tabulka.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            this.tabulka.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            this.tabulka.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            this.tabulka.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

            JScrollPane scrollPane = new JScrollPane(this.tabulka);
            scrollPane.setPreferredSize(new Dimension(800, 400));

            panel.add(scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    /**
     * Vytvorí dolný panel s tlačidlami
     */
    private JPanel vytvorDolnyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panel.setOpaque(false);

        // Tlačidlo späť do menu
        JButton menuButton = ModerneButtonUI.vytvorModerneTlacidlo("Späť do menu", new Color(59, 89, 152));
        menuButton.addActionListener(_ -> this.ovladac.zobrazHlavneMenu());

        panel.add(menuButton);

        return panel;
    }

    /**
     * Načíta históriu testov do tabuľky
     */
    private void nacitajHistoriu() {
        if (this.tabulka == null) {
            return; // Ak nemáme tabuľku, znamená to že je história prázdna
        }

        DefaultTableModel model = (DefaultTableModel)this.tabulka.getModel();
        model.setRowCount(0);

        List<VysledokTestu> historia = this.ovladac.getSpravcaHistorie().getHistoria();

        for (VysledokTestu vysledok : historia) {
            Object[] riadok = {
                    vysledok.getKategoriaNazov(),
                    vysledok.getFormatovanyCas(),
                    vysledok.getPocetOtazok(),
                    vysledok.getSpravneOdpovede(),
                    vysledok.getNespravneOdpovede(),
                    String.format("%.0f%%", vysledok.getUspesnost())
            };
            model.addRow(riadok);
        }
    }
}