package sk.uniza.fri.lingon.grafika.obrazovky;

import sk.uniza.fri.lingon.core.KategoriaTrivia;
import sk.uniza.fri.lingon.db.OtazkyLoader;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;
import sk.uniza.fri.lingon.grafika.animacie.AnimovanyProgressBar;
import sk.uniza.fri.lingon.grafika.komponenty.ModerneButtonUI;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;

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
import javax.swing.Timer;
import java.awt.BasicStroke;
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
import java.awt.RadialGradientPaint;
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

        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 242, 245));

        this.inicializujUI();
        this.nacitajKategorie();

        // Pridáme timer pre animáciu pozadia
        Timer animationTimer = new Timer(50, _ -> this.repaint());
        animationTimer.start();
    }

    /**
     * Inicializuje uzivatelske rozhranie
     */
    private void inicializujUI() {
        // Horny panel s nadpisom
        JPanel hornyPanel = this.vytvorHornyPanel();
        this.add(hornyPanel, BorderLayout.NORTH);

        // Stredny panel pre kategorie (zatial prazdny)
        JPanel strednyPanel = new JPanel();
        strednyPanel.setOpaque(false);
        this.add(strednyPanel, BorderLayout.CENTER);

        // Dolny panel s informaciami
        JPanel dolnyPanel = this.vytvorDolnyPanel();
        this.add(dolnyPanel, BorderLayout.SOUTH);
    }

    /**
     * Prekreslí pozadie s animovaným gradientom
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = this.getWidth();
        int h = this.getHeight();

        // Animovaný gradient s časom
        long time = System.currentTimeMillis() % 15000L;  // 15-sekundový cyklus
        RadialGradientPaint paint = getRadialGradientPaint((float)time, w, h);

        g2d.setPaint(paint);
        g2d.fillRect(0, 0, w, h);

        // Pridajte vzor cez gradient
        g2d.setColor(new Color(255, 255, 255, 10)); // Biela s priehľadnosťou
        int gridSize = 40;

        // Použijeme pomocnú metódu na vykreslenie mriežky
        this.drawGridLines(g2d, w, h, gridSize);

        g2d.dispose();
    }

    /**
     * Vykresli mriežku na pozadie
     * @param g2d Graphics2D objekt pre kreslenie
     * @param width Šírka oblasti
     * @param height Výška oblasti
     * @param gridSize Veľkosť mriežky
     */
    private void drawGridLines(Graphics2D g2d, int width, int height, int gridSize) {
        // Vertikálne čiary
        for (int i = 0; i < width; i += gridSize) {
            g2d.drawLine(i, 0, i, height);
        }

        // Horizontálne čiary
        for (int i = 0; i < height; i += gridSize) {
            g2d.drawLine(0, i, width, i);
        }
    }

    private static RadialGradientPaint getRadialGradientPaint(float time, int w, int h) {
        float ratio = time / 15000f;

        // Farby pre gradient (odtiene modrej a fialovej)
        Color color1 = new Color(41, 65, 114);  // Tmavá modrá
        Color color2 = new Color(100, 80, 180); // Fialová
        Color color3 = new Color(60, 120, 190); // Svetlá modrá

        // Získame pozíciu stredu gradientu použitím pomocnej metódy
        float[] centerPosition = calculateGradientCenter(ratio, w, h);
        float centerX = centerPosition[0];
        float centerY = centerPosition[1];

        // Vytvorenie radiálneho gradientu
        return new RadialGradientPaint(
                centerX, centerY, Math.max(w, h) * 0.8f,
                new float[]{0f, 0.5f, 1.0f},
                new Color[]{color2, color1, color3}
        );
    }

    /**
     * Vypočíta stredovú pozíciu gradientu na základe pomeru času
     * @param ratio Pomer času (0.0f - 1.0f)
     * @param width Šírka oblasti
     * @param height Výška oblasti
     * @return Pole s hodnotami [centerX, centerY]
     */
    private static float[] calculateGradientCenter(float ratio, int width, int height) {
        // Vypočítame pozíciu stredu gradientu, ktorá sa pohybuje
        float centerX = width * 0.5f + (float)Math.sin(ratio * 2 * Math.PI) * width * 0.3f;
        float centerY = height * 0.5f + (float)Math.cos(ratio * 2 * Math.PI) * height * 0.3f;

        return new float[] {centerX, centerY};
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
        JPanel nadpisyPanel = this.vytvorNadpisyPanel();
        panel.add(nadpisyPanel, BorderLayout.CENTER);

        // Pravá časť - ikona účtu
        JPanel accountPanel = this.vytvorAccountPanel();
        panel.add(accountPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Vytvori panel s nadpismi
     * @return Panel s nadpismi
     */
    private JPanel vytvorNadpisyPanel() {
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

        return nadpisyPanel;
    }

    /**
     * Vytvori panel s uctom
     * @return Panel s uctom
     */
    private JPanel vytvorAccountPanel() {
        JPanel accountPanel = new JPanel();
        accountPanel.setOpaque(false);
        accountPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton accountButton = this.vytvorAccountButton();
        accountPanel.add(accountButton);

        // Pridajme text s úrovňou používateľa a XP
        Pouzivatel aktualny = this.ovladac.getAktualnyPouzivatel();
        if (aktualny != null) {
            JLabel xpLabel = new JLabel(String.format("XP: %d | Úroveň: %d",
                    aktualny.getCelkoveXP(), aktualny.getUroven()));
            xpLabel.setForeground(Color.WHITE);
            xpLabel.setFont(new Font("Arial", Font.BOLD, 14));
            xpLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            accountPanel.add(xpLabel);
        }

        return accountPanel;
    }

    /**
     * Vytvori tlacidlo pre ucet
     * @return Tlacidlo pre ucet
     */
    private JButton vytvorAccountButton() {
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

                // Kruh pre hlavu
                g2.setColor(Color.WHITE);
                g2.fillOval(15, 5, 10, 10);

                // Telo (ramená)
                g2.fillArc(10, 15, 20, 20, 0, 180);

                g2.dispose();
            }
        });

        accountButton.addActionListener(_ -> this.ovladac.getSpravcaPouzivatela().zobrazProfilPouzivatela());
        return accountButton;
    }

    /**
     * Vytvori dolny panel s informaciami
     * @return Dolny panel
     */
    private JPanel vytvorDolnyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Ľavá časť - copyright
        JLabel footerLabel = new JLabel("© 2025 Lingon");
        footerLabel.setForeground(Color.WHITE);
        panel.add(footerLabel, BorderLayout.WEST);

        // Stredná časť - tlačidlo pre históriu
        JButton historiaButton = ModerneButtonUI.vytvorModerneTlacidlo("História testov", new Color(108, 117, 125));
        historiaButton.addActionListener(_ -> this.ovladac.zobrazHistoriu());

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(historiaButton);
        panel.add(centerPanel, BorderLayout.CENTER);

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
        loadingLabel.setForeground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(loadingLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(progressBar);

        loadingPanel.add(centerPanel);

        Component strednyPanel = ((BorderLayout)this.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        this.remove(strednyPanel);
        this.add(loadingPanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();

        // Spustíme animáciu
        progressBar.start();

        // Nacitame kategorie na pozadi
        SwingWorker<List<KategoriaTrivia>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KategoriaTrivia> doInBackground() throws Exception {
                return OtazkyLoader.nacitajKategorie();
            }

            @Override
            protected void done() {
                try {
                    HlavneMenu.this.kategorie = this.get();
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
        Component current = ((BorderLayout)this.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        this.remove(current);

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
        int index = 0;

        // Získame aktuálnu úroveň používateľa
        int uroven = 0;
        Pouzivatel aktualny = this.ovladac.getAktualnyPouzivatel();
        if (aktualny != null) {
            uroven = aktualny.getUroven();
        }

        for (KategoriaTrivia kategoria : this.kategorie) {
            // Zistíme, či je kategória odomknutá
            boolean jeOdomknuta = this.kontrolujOdomknutieKategorie(index, uroven);

            JPanel karta = this.vytvorKartuKategorie(kategoria, jeOdomknuta, index);

            gbc.gridx = stlpec;
            gbc.gridy = riadok;
            kategoriePanel.add(karta, gbc);

            stlpec++;
            if (stlpec >= stlpcov) {
                stlpec = 0;
                riadok++;
            }

            index++;
        }

        // Pridame scrollpane pre pripad vela kategorii
        JScrollPane scrollPane = new JScrollPane(kategoriePanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(scrollPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Kontroluje, či je kategória odomknutá na základe úrovne používateľa
     * @param index Index kategórie
     * @param uroven Úroveň používateľa
     * @return true ak je kategória odomknutá, inak false
     */
    private boolean kontrolujOdomknutieKategorie(int index, int uroven) {
        int kategoriaRiadok = index / 6;

        // Skontrolovať, či má používateľ dostatočnú úroveň pre túto kategóriu
        boolean jeOdomknuta = true;
        if (kategoriaRiadok == 1 && uroven < 1) {
            jeOdomknuta = false; // 2. riadok - potrebná úroveň 1
        } else if (kategoriaRiadok == 2 && uroven < 2) {
            jeOdomknuta = false; // 3. riadok - potrebná úroveň 2
        } else if (kategoriaRiadok == 3 && uroven < 3) {
            jeOdomknuta = false; // 4. riadok - potrebná úroveň 3
        }

        return jeOdomknuta;
    }

    /**
     * Vytvori kartu pre kategoriu
     * @param kategoria Kategoria pre ktoru sa vytvara karta
     * @param jeOdomknuta Či je kategória odomknutá
     * @param index Index kategórie
     * @return Panel predstavujuci kartu
     */
    private JPanel vytvorKartuKategorie(KategoriaTrivia kategoria, boolean jeOdomknuta, int index) {
        JPanel karta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vyplnime pozadie farbou kategorie
                if (jeOdomknuta) {
                    g2.setColor(kategoria.getFarba());
                } else {
                    // Pre zamknuté kategórie použijeme šedú farbu
                    g2.setColor(new Color(150, 150, 150));
                }
                g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);

                // Pre zamknuté kategórie pridáme ikonu zámku
                if (!jeOdomknuta) {
                    // Lepšia ikona zámku
                    int lockSize = 24;
                    int x = this.getWidth() / 2 - lockSize / 2;
                    int y = this.getHeight() / 2 - lockSize / 2 - 10; // Posunúť trochu vyššie

                    // Telo zámku
                    g2.setColor(new Color(70, 70, 70));
                    g2.fillRoundRect(x, y + lockSize / 3, lockSize, lockSize * 2 / 3, 5, 5);

                    // Oblúk zámku
                    g2.setStroke(new BasicStroke(3));
                    g2.setColor(new Color(70, 70, 70));
                    g2.drawArc(x + lockSize / 4, y - lockSize / 3, lockSize / 2, lockSize * 2 / 3, 0, 180);

                    // Dierka na kľúč
                    g2.setColor(new Color(120, 120, 120));
                    g2.fillOval(x + lockSize / 2 - 3, y + lockSize / 2, 6, 6);
                }

                g2.dispose();
            }
        };

        karta.setLayout(new BorderLayout());
        karta.setPreferredSize(new Dimension(200, 120));
        karta.setOpaque(false);

        if (jeOdomknuta) {
            karta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            karta.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        // Text kategorie - biely na farebnom pozadi
        JLabel nazovLabel = getJLabel(kategoria, jeOdomknuta, index);

        karta.add(nazovLabel, BorderLayout.CENTER);

        // Hover efekt a kliknutie len pre odomknuté kategórie
        if (jeOdomknuta) {
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
        }

        return karta;
    }

    private static JLabel getJLabel(KategoriaTrivia kategoria, boolean jeOdomknuta, int index) {
        JLabel nazovLabel = new JLabel(kategoria.getNazov(), SwingConstants.CENTER);
        nazovLabel.setForeground(Color.WHITE);
        nazovLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nazovLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Pre zamknuté kategórie pridáme informáciu o potrebnej úrovni
        if (!jeOdomknuta) {
            int potrebnaUroven = 0;
            int potrebneXP = 0;
            int kategoriaRiadok = index / 6;

            if (kategoriaRiadok == 1) {
                potrebnaUroven = 1;
                potrebneXP = 30;
            } else if (kategoriaRiadok == 2) {
                potrebnaUroven = 2;
                potrebneXP = 50;
            } else if (kategoriaRiadok == 3) {
                potrebnaUroven = 3;
                potrebneXP = 80;
            }

            nazovLabel.setText("<html><center>" + kategoria.getNazov() +
                    "<br><br><font size='2'><i>Potrebná úroveň: " +
                    potrebnaUroven + " (XP: " + potrebneXP + "+)</i></font></center></html>");
        } else {
            // Zalamovanie textu ak je prilis dlhy
            nazovLabel.setText("<html><center>" + kategoria.getNazov() + "</center></html>");
        }
        return nazovLabel;
    }
}