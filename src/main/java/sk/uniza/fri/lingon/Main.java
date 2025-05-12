package sk.uniza.fri.lingon;

import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;

/*
*
* Ок, отлично, давай теперь добавим результаты квиза, а именно после того как дойдём до 10 вопроса, то потом в конце у нас будет отображено Целковый результат теста, где будет написано на сколько я овтетил правильно а на сколько нет, и в конце я смогу вернуться назад и потом в меню мы добавим ещё одну кнопку типо как "История теста" где будут отображаться тесты которые я прошёл, время когда я прошёл, надо будет подумать как это реализовать, посоветуй, ах да, давай уберём кнопку подтвердить, давай сделаем так что если я уже вписал или выбрал ответ без подтверждения оно автоматически уже защитает, можно это как-то реализовать?
* 
*
* */

/**
 * Hlavna trieda aplikacie Lingon
 * Sluzi ako vstupny bod aplikacie
 */
public class Main {
    /**
     * Hlavna metoda - vstupny bod aplikacie
     * @param args Argumenty prikazoveho riadku
     */
    public static void main(String[] args) {
        // Nastavenie vzhľadu aplikácie
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Spustenie aplikacie v EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            vytvorGUI();
        });
    }

    /**
     * Vytvori graficke rozhranie aplikacie
     */
    private static void vytvorGUI() {
        // Vytvorenie hlavneho okna
        JFrame hlavneOkno = new JFrame("Lingon - Učenie jazykov");
        hlavneOkno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hlavneOkno.setSize(800, 600);
        hlavneOkno.setMinimumSize(new Dimension(640, 480));

        // Vytvorenie a nastavenie ovladaca hlavneho okna
        OvladacHlavnehoOkna ovladac = new OvladacHlavnehoOkna(hlavneOkno);

        // Centrovanie okna na obrazovke
        hlavneOkno.setLocationRelativeTo(null);

        // Zobrazenie okna
        hlavneOkno.setVisible(true);

        // Zobrazenie uvodnej obrazovky
        ovladac.zobrazUvodnuObrazovku();
    }
}