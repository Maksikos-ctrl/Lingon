package sk.uniza.fri.lingon.grafika;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Trieda pre animacie na nacitacej obrazovke
 */
public class NacitaciaAnimacie {
    private final JLabel statusLabel;
    private final AnimovanyProgressBar progressBar;
    private final JLabel percentLabel;
    private Timer animationTimer;
    private Timer progressTimer;

    /**
     * Vytvori novy objekt pre animacie nacitacej obrazovky
     * @param statusLabel Label pre zobrazenie stavu
     * @param progressBar Progress bar pre zobrazenie postupu
     * @param percentLabel Label pre zobrazenie percent
     */
    public NacitaciaAnimacie(JLabel statusLabel, AnimovanyProgressBar progressBar, JLabel percentLabel) {
        this.statusLabel = statusLabel;
        this.progressBar = progressBar;
        this.percentLabel = percentLabel;
    }

    /**
     * Spusti textovu animaciu nacitania
     */
    public void startTextAnimation() {
        String[] loadingTexts = {
                "Pripájam sa k API...",
                "Sťahujem otázky...",
                "Spracovávam dáta...",
                "Pripravujem otázky..."
        };

        final int[] counter = {0};
        this.animationTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NacitaciaAnimacie.this.statusLabel.setText(loadingTexts[counter[0] % loadingTexts.length]);
                counter[0]++;
            }
        });
        this.animationTimer.start();
    }

    /**
     * Spusti animaciu progress baru
     */
    public void startProgressAnimation() {
        final int[] progress = {0};
        this.progressTimer = new Timer(80, new ActionListener() {
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
                    NacitaciaAnimacie.this.progressBar.setValue(progress[0]);
                    NacitaciaAnimacie.this.percentLabel.setText(progress[0] + "%");
                }
            }
        });
        this.progressTimer.start();
    }

    /**
     * Zastavi vsetky animacie
     */
    public void stopAnimations() {
        if (this.animationTimer != null && this.animationTimer.isRunning()) {
            this.animationTimer.stop();
        }

        if (this.progressTimer != null && this.progressTimer.isRunning()) {
            this.progressTimer.stop();
        }
    }

    /**
     * Vykresli "svetelne luce" v pozadi
     * @param g2d Graphics2D objekt pre kreslenie
     * @param width Sirka oblasti pre kreslenie
     * @param height Vyska oblasti pre kreslenie
     */
    public static void drawLightRays(Graphics2D g2d, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 3;

        // Posun na zaklade casu pre animaciu
        long time = System.currentTimeMillis() / 30;
        double angle = Math.toRadians(time % 360);

        // Vykreslenie niekolko lucov
        for (int i = 0; i < 12; i++) {
            double rayAngle = angle + Math.toRadians(i * 30);
            int rayLength = Math.min(width, height) / 2;

            int endX = centerX + (int)(Math.cos(rayAngle) * rayLength);
            int endY = centerY + (int)(Math.sin(rayAngle) * rayLength);

            // Gradient od bieleho stredu k priehladnemu koncu
            GradientPaint rayGradient = new GradientPaint(
                    centerX, centerY, new Color(255, 255, 255, 15),
                    endX, endY, new Color(255, 255, 255, 0)
            );

            g2d.setPaint(rayGradient);
            g2d.setStroke(new BasicStroke(20 + (float)Math.sin(time / 10.0 + i) * 5));
            g2d.drawLine(centerX, centerY, endX, endY);
        }
    }

    /**
     * Nakresli gradient pozadia
     * @param g2d Graphics2D objekt pre kreslenie
     * @param width Sirka oblasti pre kreslenie
     * @param height Vyska oblasti pre kreslenie
     */
    public static void drawBackground(Graphics2D g2d, int width, int height) {
        // Tmavo modry gradient
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(27, 32, 44),
                0, height, new Color(13, 17, 23)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}