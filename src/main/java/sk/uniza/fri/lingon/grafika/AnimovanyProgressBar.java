package sk.uniza.fri.lingon.grafika;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Animovany progress bar s efektom zaplnenia
 */
public class AnimovanyProgressBar extends JComponent {
    private int minimumValue = 0;
    private int maximumValue = 100;
    private int currentValue = 0;
    private int targetValue = 0;
    private Color backgroundColor = new Color(40, 40, 40);
    private Color foregroundColor = new Color(76, 175, 80);
    private Color highlightColor = new Color(129, 199, 132);
    private int arcSize = 15;
    private Timer animationTimer;
    private static final int ANIMATION_SPEED = 3;

    /**
     * Konstruktor pre animovany progress bar
     */
    public AnimovanyProgressBar() {
        setPreferredSize(new Dimension(400, 30));

        // Timer pre animaciu
        this.animationTimer = new Timer(20, e -> {
            if (this.currentValue < this.targetValue) {
                this.currentValue = Math.min(this.currentValue + ANIMATION_SPEED, this.targetValue);
                repaint();
            } else if (this.currentValue > this.targetValue) {
                this.currentValue = Math.max(this.currentValue - ANIMATION_SPEED, this.targetValue);
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
    }

    /**
     * Nastavi hodnotu progress baru
     * @param value Nova hodnota
     */
    public void setValue(int value) {
        if (value < this.minimumValue) {
            value = this.minimumValue;
        }
        if (value > this.maximumValue) {
            value = this.maximumValue;
        }

        this.targetValue = value;
        if (!this.animationTimer.isRunning()) {
            this.animationTimer.start();
        }
    }

    /**
     * Nastavi farbu popredia
     * @param color Nova farba
     */
    public void setForegroundColor(Color color) {
        this.foregroundColor = color;
        this.highlightColor = new Color(
                Math.min(color.getRed() + 30, 255),
                Math.min(color.getGreen() + 30, 255),
                Math.min(color.getBlue() + 30, 255)
        );
        repaint();
    }

    /**
     * Nastavi farbu pozadia
     * @param color Nova farba
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    /**
     * Nastavi nevizualny progress bar
     * @param indeterminate true ak ma byt nevizualny
     */
    public void setIndeterminate(boolean indeterminate) {
        if (indeterminate) {
            if (!this.animationTimer.isRunning()) {
                this.targetValue = this.maximumValue;
                this.animationTimer.start();
            }
        } else {
            if (this.animationTimer.isRunning() && this.currentValue == this.maximumValue) {
                this.targetValue = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Vykreslenie pozadia
        g2d.setColor(this.backgroundColor);
        g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, this.arcSize, this.arcSize));

        if (this.currentValue > 0) {
            // Vypocet sirky progress baru
            double progressWidth = (double)this.currentValue / this.maximumValue * width;

            // Vykreslenie progress baru
            g2d.setColor(this.foregroundColor);
            g2d.fill(new RoundRectangle2D.Double(0, 0, progressWidth, height, this.arcSize, this.arcSize));

            // Vykreslenie gradientu na vrchu
            GradientPaint gradient = new GradientPaint(
                    0, 0, this.highlightColor,
                    0, (float)height / 2, this.foregroundColor
            );
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Double(0, 0, progressWidth, height / 2, this.arcSize, this.arcSize));

            // Pridanie blikajucich svetielok
            if (progressWidth > 10) {
                g2d.setColor(new Color(255, 255, 255, 100));
                double glowPosition = ((double)System.currentTimeMillis() / 10) % (width * 2);
                if (glowPosition > width) {
                    glowPosition = width * 2 - glowPosition;
                }
                if (glowPosition < progressWidth) {
                    g2d.fillRect((int)glowPosition - 5, 0, 10, height);
                }
            }
        }

        g2d.dispose();
    }
}