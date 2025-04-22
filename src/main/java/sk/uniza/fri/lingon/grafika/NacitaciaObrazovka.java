package sk.uniza.fri.lingon.grafika;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Trieda reprezentujuca nacitaciu obrazovku pocas nacitania dat z API
 */
public class NacitaciaObrazovka extends JPanel {
    private JLabel statusLabel;
    private AnimovanyProgressBar progressBar;
    private Timer animationTimer;
    private Timer progressTimer;
    private Runnable onLoadingComplete;
    private Runnable loadingTask;
    private ExecutorService executorService;
    private boolean loadingStarted = false;

    /**
     * Konstruktor pre vytvorenie nacitacej obrazovky
     *
     * @param loadingTask Uloha, ktora sa ma vykonat na pozadi
     * @param onComplete  Callback po dokonceni nacitania
     */
    public NacitaciaObrazovka(Runnable loadingTask, Runnable onComplete) {
        super(new BorderLayout());
        this.onLoadingComplete = onComplete;
        this.loadingTask = loadingTask;
        this.executorService = Executors.newSingleThreadExecutor();

        // Nastavenie pozadia
        setBackground(new Color(27, 32, 44));

        // Vytvorenie komponentov
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Nadpis
        JLabel titleLabel = new JLabel("Lingon");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(titleLabel, gbc);

        // Podnadpis
        JLabel subtitleLabel = new JLabel("Načítavam otázky...");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(subtitleLabel, gbc);

        gbc.insets = new Insets(30, 20, 10, 20);

        // Status
        statusLabel = new JLabel("Pripájam sa k API...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(statusLabel, gbc);

        gbc.insets = new Insets(10, 20, 30, 20);

        // Animovany Progress bar
        progressBar = new AnimovanyProgressBar();
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setForegroundColor(new Color(76, 175, 80)); // Zelena farba
        centerPanel.add(progressBar, gbc);

        // Pridame procenta pod progress bar
        JLabel percentLabel = new JLabel("0%");
        percentLabel.setForeground(Color.WHITE);
        percentLabel.setHorizontalAlignment(JLabel.CENTER);
        percentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.insets = new Insets(0, 20, 30, 20);
        centerPanel.add(percentLabel, gbc);

        this.add(centerPanel, BorderLayout.CENTER);

        // Spustenie textovej animacie
        startTextAnimation();

        // Spustenie animacie progress baru
        startProgressAnimation(percentLabel);

        // DŮLEŽITÉ: Načítání dat zahájíme až při volání metody startLoading()
    }

    /**
     * Spustí načítání dat na pozadí
     * Tato metoda se volá až po zobrazení nacitacej obrazovky
     */
    public void startLoading() {
        if (loadingStarted) {
            return; // Zabráníme vícenásobnému spuštění
        }

        loadingStarted = true;

        // Spustenie nacitavania dat na pozadi
        executorService.submit(() -> {
            try {
                loadingTask.run();

                // Po dokonceni spustime callback v EDT
                SwingUtilities.invokeLater(() -> {
                    stopAnimations();

                    // Nastavime progress bar na 100%
                    progressBar.setValue(100);

                    // Získáme odkaz na percentLabel
                    Component[] components = ((JPanel) this.getComponent(0)).getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JLabel && ((JLabel) comp).getText().endsWith("%")) {
                            ((JLabel) comp).setText("100%");
                            break;
                        }
                    }

                    statusLabel.setText("Načítavanie dokončené!");

                    // Kratke oneskorenie pred prepnutim obrazovky
                    Timer delayTimer = new Timer(1000, e -> {
                        if (onLoadingComplete != null) {
                            onLoadingComplete.run();
                        }
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                });
            } catch (Exception e) {
                // V pripade chyby zobrazime chybu
                SwingUtilities.invokeLater(() -> {
                    stopAnimations();
                    statusLabel.setText("Chyba: " + e.getMessage());
                    statusLabel.setForeground(new Color(255, 87, 87)); // Cervena
                    progressBar.setValue(0);

                    // Aktualizujeme percentLabel
                    Component[] components = ((JPanel) this.getComponent(0)).getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JLabel && ((JLabel) comp).getText().endsWith("%")) {
                            ((JLabel) comp).setText("Chyba!");
                            break;
                        }
                    }

                    // Zobrazime tlacidlo pre opakovany pokus
                    JButton retryButton = new JButton("Skúsiť znova");
                    retryButton.setFont(new Font("Arial", Font.BOLD, 14));
                    retryButton.setBackground(new Color(59, 89, 152)); // Facebook blue
                    retryButton.setForeground(Color.WHITE);
                    retryButton.setFocusPainted(false);
                    retryButton.addActionListener(evt -> {
                        this.removeAll();
                        NacitaciaObrazovka newScreen = new NacitaciaObrazovka(loadingTask, onLoadingComplete);
                        this.add(newScreen);
                        this.revalidate();
                        this.repaint();
                        newScreen.startLoading();
                    });

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    buttonPanel.setOpaque(false);
                    buttonPanel.add(retryButton);

                    this.add(buttonPanel, BorderLayout.SOUTH);
                    this.revalidate();
                    this.repaint();
                });
            }
        });
    }

    /**
     * Spusti textovu animaciu nacitania
     */
    private void startTextAnimation() {
        String[] loadingTexts = {
                "Pripájam sa k API...",
                "Sťahujem otázky...",
                "Spracovávam dáta...",
                "Pripravujem otázky..."
        };

        final int[] counter = {0};
        animationTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(loadingTexts[counter[0] % loadingTexts.length]);
                counter[0]++;
            }
        });
        animationTimer.start();
    }

    /**
     * Spusti animaciu progress baru
     */
    private void startProgressAnimation(JLabel percentLabel) {
        final int[] progress = {0};
        progressTimer = new Timer(80, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulujeme nacitavanie - postupne spomalujeme ako sa blizime k 95%
                if (progress[0] < 95) {
                    if (progress[0] < 60) {
                        progress[0] += 1;
                    } else if (progress[0] < 80) {
                        progress[0] += (Math.random() > 0.5 ? 1 : 0);
                    } else {
                        progress[0] += (Math.random() > 0.8 ? 1 : 0);
                    }
                    progressBar.setValue(progress[0]);
                    percentLabel.setText(progress[0] + "%");
                }
            }
        });
        progressTimer.start();
    }

    /**
     * Zastavi vsetky animacie
     */
    private void stopAnimations() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }

        // Zastavenie executora
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vykreslenie gradientneho pozadia
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Tmavo modry gradient
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(27, 32, 44),
                0, getHeight(), new Color(13, 17, 23)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Pridame "svetelne luce" v pozadi
        drawLightRays(g2d);

        g2d.dispose();
    }

    /**
     * Vykresli "svetelne luce" v pozadi
     */
    private void drawLightRays(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 3;

        // Posun na zaklade casu pre animaciu
        long time = System.currentTimeMillis() / 30;
        double angle = Math.toRadians(time % 360);

        // Vykreslenie niekolko lucov
        for (int i = 0; i < 12; i++) {
            double rayAngle = angle + Math.toRadians(i * 30);
            int rayLength = Math.min(getWidth(), getHeight()) / 2;

            int endX = centerX + (int)(Math.cos(rayAngle) * rayLength);
            int endY = centerY + (int)(Math.sin(rayAngle) * rayLength);

            // Gradient od bieleho stredu k priehladnemu koncu
            GradientPaint rayGradient = new GradientPaint(
                    centerX, centerY, new Color(255, 255, 255, 15),
                    endX, endY, new Color(255, 255, 255, 0)
            );

            g2d.setPaint(rayGradient);
            g2d.setStroke(new BasicStroke(20 + (float) Math.sin(time / 10.0 + i) * 5));
            g2d.drawLine(centerX, centerY, endX, endY);
        }
    }
}