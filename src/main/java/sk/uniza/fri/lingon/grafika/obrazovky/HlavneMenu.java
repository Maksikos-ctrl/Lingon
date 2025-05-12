package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.core.KategoriaTrivia;
import sk.uniza.fri.lingon.db.OtazkyLoader;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.animacie.AnimovanyProgressBar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Hlavne menu aplikacie s vyberom kategorii
 */
public class HlavneMenu extends JPanel {
    private final OvladacHlavnehoOkna ovladac;
    private List<KategoriaTrivia> kategorie;

    /**
     * Konstruktor pre vytvorenie hlavneho menu
     * @param ovladac Ovladac hlavneho okna
     */
    public HlavneMenu(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
        this.kategorie = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        this.inicializujUI();
        this.nacitajKategorie();
    }

    /**
     * Inicializuje uzivatelske rozhranie
     */
    private void inicializujUI() {
        // Horny panel s nadpisom
        JPanel hornyPanel = this.vytvorHornyPanel();
        add(hornyPanel, BorderLayout.NORTH);

        // Stredny panel pre kategorie (zatial prazdny)
        JPanel strednyPanel = new JPanel();
        strednyPanel.setOpaque(false);
        add(strednyPanel, BorderLayout.CENTER);

        // Dolny panel s informaciami
        JPanel dolnyPanel =  this.vytvorDolnyPanel();
        add(dolnyPanel, BorderLayout.SOUTH);
    }

    /**
     * Vytvori horny panel s nadpisom
     * @return Horny panel
     */
    private JPanel vytvorHornyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 65, 114));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ľavá časť - nadpisy
        JPanel nadpisyPanel = new JPanel();
        nadpisyPanel.setLayout(new BoxLayout(nadpisyPanel, BoxLayout.Y_AXIS));
        nadpisyPanel.setOpaque(false);

        JLabel nadpis = new JLabel("LINGON QUIZ");
        nadpis.setFont(new Font("Arial", Font.BOLD, 36));
        nadpis.setForeground(Color.WHITE);
        nadpis.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel podnadpis = new JLabel("Vyber si kategóriu");
        podnadpis.setFont(new Font("Arial", Font.ITALIC, 20));
        podnadpis.setForeground(new Color(255, 255, 255, 200));
        podnadpis.setAlignmentX(Component.LEFT_ALIGNMENT);

        nadpisyPanel.add(nadpis);
        nadpisyPanel.add(Box.createVerticalStrut(10));
        nadpisyPanel.add(podnadpis);

        panel.add(nadpisyPanel, BorderLayout.CENTER);

        // Pravá časť - ikona účtu
        JPanel accountPanel = new JPanel();
        accountPanel.setOpaque(false);
        accountPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton accountButton = new JButton();
        accountButton.setPreferredSize(new Dimension(40, 40));
        accountButton.setBorderPainted(false);
        accountButton.setContentAreaFilled(false);
        accountButton.setFocusPainted(false);
        accountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Vytvorenie ikony účtu
        accountButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 40;
                // Kruh pre hlavu
                g2.setColor(Color.WHITE);
                g2.fillOval(15, 5, 10, 10);

                // Telo (ramená)
                g2.fillArc(10, 15, 20, 20, 0, 180);

                g2.dispose();
            }
        });

        accountButton.addActionListener(e ->  this.ovladac.getSpravcaPouzivatela().zobrazProfilPouzivatela());
        accountPanel.add(accountButton);

        panel.add(accountPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Vytvori dolny panel s informaciami
     * @return Dolny panel
     */
    private JPanel vytvorDolnyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Prázdny panel, prípadne sem môžeme pridať iné informácie
        JLabel footerLabel = new JLabel("© 2025 Lingon");
        footerLabel.setForeground(new Color(100, 100, 100));
        panel.add(footerLabel);

        return panel;
    }

    /**
     * Nacita kategorie z API a vytvori karty
     */
    private void nacitajKategorie() {
        // Vytvoríme animovaný loading panel
        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setOpaque(false);

        // Animovaný progress bar
        AnimovanyProgressBar progressBar = new AnimovanyProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 20));

        JLabel loadingLabel = new JLabel("Načítavam kategórie...");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(loadingLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(progressBar);

        loadingPanel.add(centerPanel);

        Component strednyPanel = ((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER);
        remove(strednyPanel);
        add(loadingPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        // Spustíme animáciu
        progressBar.start();

        // Nacitame kategorie na pozadi
        SwingWorker<List<KategoriaTrivia>, Void> worker = new SwingWorker<List<KategoriaTrivia>, Void>() {
            @Override
            protected List<KategoriaTrivia> doInBackground() throws Exception {
                return OtazkyLoader.nacitajKategorie();
            }

            @Override
            protected void done() {
                try {
                    HlavneMenu.this.kategorie = get();
                    HlavneMenu.this.zobrazKategorie();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(HlavneMenu.this,
                            "Nepodarilo sa načítať kategórie: " + e.getMessage(),
                            "Chyba",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    /**
     * Zobrazi kategorie ako karty
     */
    private void zobrazKategorie() {
        // Odstranime loading panel
        Component current = ((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER);
        remove(current);

        // Vytvorime panel pre kategorie
        JPanel kategoriePanel = new JPanel(new GridBagLayout());
        kategoriePanel.setOpaque(false);
        kategoriePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Zobrazime maximalne 6 kategorii v riadku
        int stlpcov = 6;
        int riadok = 0;
        int stlpec = 0;

        for (KategoriaTrivia kategoria :  this.kategorie) {
            JPanel karta =  this.vytvorKartuKategorie(kategoria);

            gbc.gridx = stlpec;
            gbc.gridy = riadok;
            kategoriePanel.add(karta, gbc);

            stlpec++;
            if (stlpec >= stlpcov) {
                stlpec = 0;
                riadok++;
            }
        }

        // Pridame scrollpane pre pripad vela kategorii
        JScrollPane scrollPane = new JScrollPane(kategoriePanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Vytvori kartu pre kategoriu
     * @param kategoria Kategoria pre ktoru sa vytvara karta
     * @return Panel predstavujuci kartu
     */
    private JPanel vytvorKartuKategorie(KategoriaTrivia kategoria) {
        JPanel karta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vyplnime pozadie farbou kategorie
                g2.setColor(kategoria.getFarba());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.dispose();
            }
        };

        karta.setLayout(new BorderLayout());
        karta.setPreferredSize(new Dimension(200, 120));
        karta.setOpaque(false);
        karta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Text kategorie - biely na farebnom pozadi
        JLabel nazovLabel = new JLabel(kategoria.getNazov(), SwingConstants.CENTER);
        nazovLabel.setForeground(Color.WHITE);
        nazovLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nazovLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Zalamovanie textu ak je prilis dlhy
        nazovLabel.setText("<html><center>" + kategoria.getNazov() + "</center></html>");

        karta.add(nazovLabel, BorderLayout.CENTER);

        // Hover efekt
        karta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                karta.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                karta.setBorder(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                HlavneMenu.this.ovladac.getSpravcaKvizu().spustiKvizPreKategoriu(kategoria);
            }
        });

        return karta;
    }
}