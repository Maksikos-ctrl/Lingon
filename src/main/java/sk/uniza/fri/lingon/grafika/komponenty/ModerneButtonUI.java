package sk.uniza.fri.lingon.grafika.komponenty;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Vlastný štýl pre tlačidlá
 */
public class ModerneButtonUI extends BasicButtonUI {
    private Color pozadie;
    private Color pozadieHover;
    private Color pozadieStlacene;

    /**
     * Konštruktor pre moderné tlačidlo
     * @param pozadie Základná farba pozadia
     */
    public ModerneButtonUI(Color pozadie) {
        this.pozadie = pozadie;
        this.pozadieHover = pozadie.brighter();
        this.pozadieStlacene = pozadie.darker();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton)c;
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton)c;
        Graphics2D g2 = (Graphics2D)g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Zvolíme farbu podľa stavu
        Color farba;
        if (button.getModel().isPressed()) {
            farba =  this.pozadieStlacene;
        } else if (button.getModel().isRollover()) {
            farba =  this.pozadieHover;
        } else {
            farba =  this.pozadie;
        }

        // Vykreslíme pozadie
        g2.setColor(farba);
        g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 10, 10);

        // Vykreslíme text
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.setFont(button.getFont());

        String text = button.getText();
        int x = (button.getWidth() - fm.stringWidth(text)) / 2;
        int y = (button.getHeight() + fm.getAscent()) / 2 - 2;

        g2.drawString(text, x, y);

        g2.dispose();
    }

    /**
     * Vytvorí moderné tlačidlo
     * @param text Text tlačidla
     * @param farba Farba pozadia
     * @return Moderné tlačidlo
     */
    public static JButton vytvorModerneTlacidlo(String text, Color farba) {
        JButton button = new JButton(text);
        button.setUI(new ModerneButtonUI(farba));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 45));
        return button;
    }
}