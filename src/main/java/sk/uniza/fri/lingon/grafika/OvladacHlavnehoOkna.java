package sk.uniza.fri.lingon.grafika;

import sk.uniza.fri.lingon.db.OtazkyLoader;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;
import sk.uniza.fri.lingon.GUI.IZadanie;
import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.pouzivatel.lekcia.ParovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VpisovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VyberovaOtazka;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.List;

/**
 * Ovladac hlavneho okna aplikacie
 * Riadi zobrazovanie a prepinanie obrazoviek
 */
public class OvladacHlavnehoOkna {
    private JFrame hlavneOkno;
    private UIKontajner kontajner;
    private Pouzivatel aktualnyPouzivatel;
    private List<IZadanie> otazky;
    private int aktualnaOtazka;

    /**
     * Konstruktor pre vytvorenie ovladaca hlavneho okna
     * @param hlavneOkno Hlavne okno aplikacie
     */
    public OvladacHlavnehoOkna(JFrame hlavneOkno) {
        this.hlavneOkno = hlavneOkno;
        this.kontajner = new UIKontajner();
        this.aktualnaOtazka = 0;

        // Inicializacia hlavneho okna - zabezpečíme, že layout je nastavený
        this.hlavneOkno.getContentPane().removeAll();
        this.hlavneOkno.setLayout(new BorderLayout());
        this.hlavneOkno.add(this.kontajner, BorderLayout.CENTER);

        // Vytvorenie menu
        this.vytvorMenu();

        // Revalidácia okna po pridaní komponentov
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Vytvori menu aplikacie
     */
    private void vytvorMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Aplikacia
        JMenu aplikaciaMenu = new JMenu("Aplikácia");
        JMenuItem novyPouzivatelItem = new JMenuItem("Nový používateľ");
        novyPouzivatelItem.addActionListener(e -> this.zobrazDialogNovehoPouzivatela());
        aplikaciaMenu.add(novyPouzivatelItem);

        JMenuItem koniecItem = new JMenuItem("Koniec");
        koniecItem.addActionListener(e -> System.exit(0));
        aplikaciaMenu.add(koniecItem);

        // Menu Lekcie
        JMenu lekcieMenu = new JMenu("Lekcie");
        JMenuItem nacitajLekcieItem = new JMenuItem("Načítaj lekcie");
        nacitajLekcieItem.addActionListener(e -> this.nacitajOtazky());
        lekcieMenu.add(nacitajLekcieItem);

        // Pridanie menu do menu baru
        menuBar.add(aplikaciaMenu);
        menuBar.add(lekcieMenu);

        this.hlavneOkno.setJMenuBar(menuBar);
    }

    /**
     * Zobrazi uvodnu obrazovku
     */
    public void zobrazUvodnuObrazovku() {
        // Vyčistíme prípadné navigačné tlačidlá dole
        this.odstranNavigaciuPanel();

        // Hlavný panel s gradient pozadím
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Gradient pozadie - tmavo modrá navrchu, svetlejšia dole
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();

                // Farby pre gradient - tmavo modrá až svetlo modrá
                Color color1 = new Color(41, 65, 114);  // Tmavšia modrá
                Color color2 = new Color(84, 125, 190); // Svetlejšia modrá

                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };

        // Priestor pre informácie o užívateľovi v pravom hornom rohu
        if (this.aktualnyPouzivatel != null) {
            JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            userInfoPanel.setOpaque(false);

            JButton accountButton = new JButton(this.aktualnyPouzivatel.getMeno() + " (" + this.aktualnyPouzivatel.getCelkoveXP() + " XP)");
            accountButton.setFont(new Font("Arial", Font.BOLD, 14));
            accountButton.setForeground(Color.WHITE);
            accountButton.setBackground(new Color(59, 89, 152, 150)); // Polopriehľadná modrá
            accountButton.setBorderPainted(false);
            accountButton.setFocusPainted(false);
            accountButton.addActionListener(e -> zobrazProfilPouzivatela());

            userInfoPanel.add(accountButton);
            mainPanel.add(userInfoPanel, BorderLayout.NORTH);
        }

        // Stredový panel s titulkom a tlačidlom
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        centerPanel.setOpaque(false);

        // Logo a nadpis
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);

        // Nadpis
        JLabel nadpisLabel = new JLabel("LINGON");
        nadpisLabel.setFont(new Font("Arial", Font.BOLD, 60));
        nadpisLabel.setForeground(Color.WHITE);
        nadpisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(nadpisLabel);

        // Podnadpis
        JLabel podnadpisLabel = new JLabel("Učenie jazykov");
        podnadpisLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        podnadpisLabel.setForeground(new Color(255, 255, 255, 220));
        podnadpisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(podnadpisLabel);

        centerPanel.add(logoPanel);
        centerPanel.add(Box.createVerticalStrut(70));

        // Tlacidlo Hrat - výraznejšie
        JButton hratButton = new JButton("PLAY");
        hratButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hratButton.setFont(new Font("Arial", Font.BOLD, 28));
        hratButton.setPreferredSize(new Dimension(220, 80));
        hratButton.setMaximumSize(new Dimension(220, 80));

        // Moderný vzhľad tlačidla s gradient pozadím
        hratButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();

                // Gradient pozadie tlačidla - zelené
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(76, 175, 80),
                        0, h, new Color(56, 142, 60)
                );

                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);

                // Efekt svetlého okraja hore
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillRoundRect(0, 0, w, h/2, 20, 20);

                // Text
                FontMetrics fm = g2d.getFontMetrics(c.getFont());
                String text = "PLAY";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                g2d.setColor(Color.WHITE);
                g2d.setFont(c.getFont());
                g2d.drawString(text, (w - textWidth) / 2, h/2 + textHeight/4);

                g2d.dispose();
            }
        });

        hratButton.addActionListener(e -> {
            if (this.aktualnyPouzivatel == null) {
                this.zobrazDialogNovehoPouzivatela();
            } else {
                this.zobrazOtazku();
            }
        });

        // Odstránenie borderu a focus efektu
        hratButton.setBorderPainted(false);
        hratButton.setFocusPainted(false);
        hratButton.setContentAreaFilled(false);

        // Efekt pri najetí myšou
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

        centerPanel.add(hratButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer s informáciami
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 Lingon - Projekt pre výuku jazykov");
        footerLabel.setForeground(new Color(255, 255, 255, 180));
        footerPanel.add(footerLabel);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        this.kontajner.pridajKomponent(mainPanel);

        // Revalidácia a prekreslenie hlavného okna
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Zobrazí profil používateľa
     */
    private void zobrazProfilPouzivatela() {
        // Vyčistíme prípadné navigačné tlačidlá dole
        this.odstranNavigaciuPanel();

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
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        infoPanel.setOpaque(false);

        // Avatar - kruh s iniciálami
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Kruh
                g2d.setColor(new Color(59, 89, 152));
                g2d.fillOval(x, y, size, size);

                // Iniciály
                String text = String.valueOf(aktualnyPouzivatel.getMeno().charAt(0)).toUpperCase();
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                g2d.drawString(text, x + (size - textWidth) / 2,
                        y + size / 2 + textHeight / 4);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }
        };

        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(avatarPanel);
        infoPanel.add(Box.createVerticalStrut(20));

        // Meno
        JLabel menoLabel = new JLabel("Meno: " + this.aktualnyPouzivatel.getMeno());
        menoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        menoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(menoLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Email
        JLabel emailLabel = new JLabel("Email: " + this.aktualnyPouzivatel.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(20));

        // XP body
        JLabel xpLabel = new JLabel("Získané XP body: " + this.aktualnyPouzivatel.getCelkoveXP());
        xpLabel.setFont(new Font("Arial", Font.BOLD, 22));
        xpLabel.setForeground(new Color(76, 175, 80));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(xpLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Tlačidlá
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton spatButton = new JButton("Späť do menu");
        spatButton.setFont(new Font("Arial", Font.BOLD, 14));
        spatButton.setBackground(new Color(59, 89, 152));
        spatButton.setForeground(Color.WHITE);
        spatButton.setFocusPainted(false);
        spatButton.addActionListener(e -> zobrazUvodnuObrazovku());
        buttonPanel.add(spatButton);

        JButton pokracovatButton = new JButton("Pokračovať v teste");
        pokracovatButton.setFont(new Font("Arial", Font.BOLD, 14));
        pokracovatButton.setBackground(new Color(76, 175, 80));
        pokracovatButton.setForeground(Color.WHITE);
        pokracovatButton.setFocusPainted(false);
        pokracovatButton.addActionListener(e -> zobrazOtazku());
        buttonPanel.add(pokracovatButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        this.kontajner.pridajKomponent(panel);

        // Revalidácia a prekreslenie hlavného okna
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Zobrazi dialog pre vytvorenie noveho pouzivatela
     */
    private void zobrazDialogNovehoPouzivatela() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Meno:"));
        JTextField menoField = new JTextField(20);
        panel.add(menoField);

        panel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        panel.add(emailField);

        int vysledok = JOptionPane.showConfirmDialog(
                this.hlavneOkno,
                panel,
                "Nový používateľ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (vysledok == JOptionPane.OK_OPTION) {
            String meno = menoField.getText().trim();
            String email = emailField.getText().trim();

            if (!meno.isEmpty() && !email.isEmpty()) {
                this.aktualnyPouzivatel = new Pouzivatel(meno, email);

                // Pokračujeme načítaním otázok so zobrazením loadingu
                this.nacitajOtazky();
            } else {
                JOptionPane.showMessageDialog(
                        this.hlavneOkno,
                        "Prosím, vyplňte meno a email.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Nacita otazky z API alebo demo otazky
     * Zobrazuje nacitaciu obrazovku pocas nacitavania
     */
    public void nacitajOtazky() {
        // Odstránime prípadné navigačné tlačidlá pred zobrazením loadingu
        this.odstranNavigaciuPanel();

        // Vytvorenie nacitacej obrazovky
        NacitaciaObrazovka nacitaciaObrazovka = new NacitaciaObrazovka(
                // Uloha, ktora sa ma vykonat na pozadi
                () -> {
                    try {
                        // Simulacia dlhsieho nacitavania pre ukazku obrazovky (v realnej aplikacii odstranit)
                        Thread.sleep(2000);

                        // Nacitanie otazok z API
                        this.otazky = OtazkyLoader.nacitajOtazky();
                        this.aktualnaOtazka = 0;
                    } catch (Exception e) {
                        System.out.println("Chyba pri načítaní otázok z API: " + e.getMessage());
                        this.otazky = OtazkyLoader.getDemoOtazky();
                        this.aktualnaOtazka = 0;
                    }
                },
                // Callback po dokonceni nacitavania
                () -> {
                    this.zobrazOtazku();
                }
        );

        // Najprv zobrazime loading screen, potom az zacneme nacitavat data
        // To zabezpeci, ze loading screen sa zobrazi okamzite
        SwingUtilities.invokeLater(() -> {
            // Zobrazenie nacitacej obrazovky v EDT
            this.kontajner.pridajKomponent(nacitaciaObrazovka);

            // Revalidácia a prekreslenie hlavného okna
            this.hlavneOkno.validate();
            this.hlavneOkno.repaint();

            // Oneskorene spustenie nacitavania, aby sa stihol zobrazit loading screen
            Timer timer = new Timer(100, e -> {
                // Toto spusti nacitavanie az po zobrazeni loading screenu
                nacitaciaObrazovka.startLoading();
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Odstráni navigačný panel z hlavného okna, ak existuje
     */
    private void odstranNavigaciuPanel() {
        Component[] komponenty = this.hlavneOkno.getContentPane().getComponents();
        for (Component komponent : komponenty) {
            if (komponent instanceof JPanel && komponent != this.kontajner) {
                this.hlavneOkno.remove(komponent);
            }
        }
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Zobrazi aktualnu otazku
     */
    public void zobrazOtazku() {
        if (this.otazky == null || this.otazky.isEmpty()) {
            this.nacitajOtazky();
            return;
        }

        // Odstranenie existujuceho panelu navigacie, ak existuje
        this.odstranNavigaciuPanel();

        // Vyčistenie kontajnera
        this.kontajner.vymazObsah();

        // Hlavný panel pre otázku s Border Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));

        // Panel pre horné informácie - číslo otázky a XP
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(59, 89, 152));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Číslo otázky
        JLabel otazkaInfoLabel = new JLabel("Otázka " + (this.aktualnaOtazka + 1) + " z " + this.otazky.size());
        otazkaInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        otazkaInfoLabel.setForeground(Color.WHITE);
        topPanel.add(otazkaInfoLabel, BorderLayout.WEST);

        // XP a meno používateľa
        JPanel xpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        xpPanel.setOpaque(false);

        JLabel xpLabel = new JLabel("XP: " + this.aktualnyPouzivatel.getCelkoveXP());
        xpLabel.setFont(new Font("Arial", Font.BOLD, 16));
        xpLabel.setForeground(Color.WHITE);

        JButton profilButton = new JButton(this.aktualnyPouzivatel.getMeno());
        profilButton.setFont(new Font("Arial", Font.BOLD, 14));
        profilButton.setForeground(Color.WHITE);
        profilButton.setBackground(new Color(59, 89, 152));
        profilButton.setBorderPainted(false);
        profilButton.setFocusPainted(false);
        profilButton.addActionListener(e -> zobrazProfilPouzivatela());

        xpPanel.add(xpLabel);
        xpPanel.add(profilButton);

        topPanel.add(xpPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel pre obsah otázky
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);

        if (this.aktualnaOtazka < this.otazky.size()) {
            IZadanie zadanie = this.otazky.get(this.aktualnaOtazka);

            // Vytvoríme nový custom kontajner pre otázku
            UIKontajner otazkaKontajner = new UIKontajner();

            // Vytvoríme wrapper pre výsledok odpovede
            final boolean[] jeSpravna = {false};
            final boolean[] bolOdpovedany = {false};

            // Panel pre výsledok odpovede (skrytý na začiatku)
            JPanel vysledokPanel = new JPanel();
            vysledokPanel.setLayout(new BoxLayout(vysledokPanel, BoxLayout.Y_AXIS));
            vysledokPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
            vysledokPanel.setVisible(false);

            JLabel vysledokLabel = new JLabel();
            vysledokLabel.setFont(new Font("Arial", Font.BOLD, 16));
            vysledokLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel xpZiskaneLabel = new JLabel();
            xpZiskaneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            xpZiskaneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            vysledokPanel.add(vysledokLabel);
            vysledokPanel.add(Box.createVerticalStrut(5));
            vysledokPanel.add(xpZiskaneLabel);

            // Nastavenie delegáta pre kontrolu odpovede
            OdpovedDelegate odpovedelegate = new OdpovedDelegate() {
                @Override
                public void spracujOdpoved(String odpoved, boolean spravna) {
                    if (!bolOdpovedany[0]) {  // Zabránime viacnásobnému pripočítaniu bodov
                        bolOdpovedany[0] = true;
                        jeSpravna[0] = spravna;

                        if (spravna) {
                            int ziskaneXP = aktualnyPouzivatel.zaznamenajSpravnuOdpoved(zadanie.getTypOtazky());
                            vysledokLabel.setText("Správna odpoveď!");
                            vysledokLabel.setForeground(new Color(76, 175, 80));
                            xpZiskaneLabel.setText("Získali ste " + ziskaneXP + " XP bodov!");
                            xpZiskaneLabel.setForeground(new Color(76, 175, 80));
                            xpLabel.setText("XP: " + aktualnyPouzivatel.getCelkoveXP());
                        } else {
                            aktualnyPouzivatel.zaznamenajNespravnuOdpoved();
                            vysledokLabel.setText("Nesprávna odpoveď!");
                            vysledokLabel.setForeground(new Color(244, 67, 54));
                            xpZiskaneLabel.setText("Skúste to znova pri ďalšej otázke.");
                            xpZiskaneLabel.setForeground(new Color(120, 120, 120));
                        }

                        vysledokPanel.setVisible(true);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                }
            };

            // Nastavíme delegáta na aktuálnu otázku
            if (zadanie instanceof VyberovaOtazka) {
                ((VyberovaOtazka) zadanie).setOdpovedDelegate(odpovedelegate);
            } else if (zadanie instanceof VpisovaciaOtazka) {
                ((VpisovaciaOtazka) zadanie).setOdpovedDelegate(odpovedelegate);
            } else if (zadanie instanceof ParovaciaOtazka) {
                ((ParovaciaOtazka) zadanie).setOdpovedDelegate(odpovedelegate);
            }

            // Zobrazíme otázku
            zadanie.zobrazGrafiku(otazkaKontajner);

            contentPanel.add(otazkaKontajner, BorderLayout.CENTER);
            contentPanel.add(vysledokPanel, BorderLayout.SOUTH);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Pridanie navigacnych tlacidiel
        JPanel navigaciaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navigaciaPanel.setBackground(new Color(240, 240, 240));
        navigaciaPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton dalsiaButton = new JButton("Ďalšia otázka");
        dalsiaButton.setFont(new Font("Arial", Font.BOLD, 14));
        dalsiaButton.setBackground(new Color(66, 103, 178));
        dalsiaButton.setForeground(Color.WHITE);
        dalsiaButton.setFocusPainted(false);
        dalsiaButton.addActionListener(e -> {
            this.aktualnaOtazka = (this.aktualnaOtazka + 1) % this.otazky.size();
            this.zobrazOtazku();
        });
        navigaciaPanel.add(dalsiaButton);

        JButton menuButton = new JButton("Menu");
        menuButton.setFont(new Font("Arial", Font.BOLD, 14));
        menuButton.setBackground(new Color(240, 240, 240));
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> this.zobrazUvodnuObrazovku());
        navigaciaPanel.add(menuButton);

        mainPanel.add(navigaciaPanel, BorderLayout.SOUTH);

        // Pridanie hlavného panelu do kontajnera
        this.kontajner.pridajKomponent(mainPanel);

        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Interface pre delegovanie kontroly odpovede
     */
    public interface OdpovedDelegate {
        void spracujOdpoved(String odpoved, boolean spravna);
    }

    /**
     * Getter pre kontajner UI
     * @return UIKontajner
     */
    public UIKontajner getKontajner() {
        return this.kontajner;
    }
}