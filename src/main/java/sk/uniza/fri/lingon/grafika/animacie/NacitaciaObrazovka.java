package sk.uniza.fri.lingon.grafika.animacie;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * Trieda reprezentujuca nacitaciu obrazovku pocas nacitania dat z API
 */
public class NacitaciaObrazovka extends JPanel {
    private JLabel statusLabel;
    private AnimovanyProgressBar progressBar;
    private JLabel percentLabel;
    private final NacitaciaAnimacie animacie;
    private final NacitaciaScreenManager manager;

    /**
     * Konstruktor pre vytvorenie nacitacej obrazovky
     *
     * @param loadingTask Uloha, ktora sa ma vykonat na pozadi
     * @param onComplete  Callback po dokonceni nacitania
     */
    public NacitaciaObrazovka(Runnable loadingTask, Runnable onComplete) {
        super(new BorderLayout());

        // Nastavenie pozadia
        this.setBackground(new Color(27, 32, 44));

        // Vytvorenie komponentov UI
        this.initializeUI();

        // Vytvorenie animacii
        this.animacie = new NacitaciaAnimacie(this.statusLabel, this.progressBar, this.percentLabel);

        // Vytvorenie managera pre nacitanie
        this.manager = new NacitaciaScreenManager(this, loadingTask, onComplete);

        // Spustenie animacii
        this.animacie.startTextAnimation();
        this.animacie.startProgressAnimation();
    }

    /**
     * Inicializuje UI komponenty
     */
    private void initializeUI() {
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
        this.statusLabel = new JLabel("Pripájam sa k API...");
        this.statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        this.statusLabel.setForeground(Color.WHITE);
        this.statusLabel.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(this.statusLabel, gbc);

        gbc.insets = new Insets(10, 20, 30, 20);

        // Animovany Progress bar
        this.progressBar = new AnimovanyProgressBar();
        this.progressBar.setPreferredSize(new Dimension(400, 30));
        this.progressBar.setForegroundColor(new Color(76, 175, 80)); // Zelena farba
        centerPanel.add(this.progressBar, gbc);

        // Pridame procenta pod progress bar
        this.percentLabel = new JLabel("0%");
        this.percentLabel.setForeground(Color.WHITE);
        this.percentLabel.setHorizontalAlignment(JLabel.CENTER);
        this.percentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.insets = new Insets(0, 20, 30, 20);
        centerPanel.add(this.percentLabel, gbc);

        this.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Spustí načítání dat na pozadí
     * Tato metoda se volá až po zobrazení nacitacej obrazovky
     */
    public void startLoading() {
        this.manager.startLoading();
    }

    /**
     * Zastavi vsetky animacie
     */
    public void stopAnimations() {
        this.animacie.stopAnimations();
    }

    /**
     * Aktualizuje progress bar a status
     * @param progress Hodnota postupu (0-100)
     * @param status Text statusu
     */
    public void updateProgress(int progress, String status) {
        this.progressBar.setValue(progress);
        this.percentLabel.setText(progress + "%");
        this.statusLabel.setText(status);
    }

    /**
     * Spracuje chybu pri nacitavani
     * @param errorMessage Chybova hlaska
     * @param loadingTask Uloha, ktora sa ma vykonat na pozadi
     * @param onComplete Callback po dokonceni nacitania
     */
    public void handleError(String errorMessage, Runnable loadingTask, Runnable onComplete) {
        this.statusLabel.setText("Chyba: " + errorMessage);
        this.statusLabel.setForeground(new Color(255, 87, 87)); // Cervena
        this.progressBar.setValue(0);
        this.percentLabel.setText("Chyba!");

        // Zobrazime tlacidlo pre opakovany pokus
        JButton retryButton = new JButton("Skúsiť znova");
        retryButton.setFont(new Font("Arial", Font.BOLD, 14));
        retryButton.setBackground(new Color(59, 89, 152)); // Facebook blue
        retryButton.setForeground(Color.WHITE);
        retryButton.setFocusPainted(false);
        retryButton.addActionListener(evt -> {
            this.removeAll();
            NacitaciaObrazovka newScreen = new NacitaciaObrazovka(loadingTask, onComplete);
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vykreslenie gradientneho pozadia
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Kreslenie pozadia a svetelnych lucov
        NacitaciaAnimacie.drawBackground(g2d, this.getWidth(), this.getHeight());
        NacitaciaAnimacie.drawLightRays(g2d, this.getWidth(), this.getHeight());

        g2d.dispose();
    }
}