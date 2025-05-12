package sk.uniza.fri.lingon.grafika.hlavny;

import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;
import sk.uniza.fri.lingon.grafika.obrazovky.HlavneMenu;
import sk.uniza.fri.lingon.grafika.spravcovia.*;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 * Hlavný ovládač aplikácie
 * Koordinuje prácu ostatných správcov
 */
public class OvladacHlavnehoOkna {
    private JFrame hlavneOkno;
    private UIKontajner kontajner;
    private SpravcaKvizu spravcaKvizu;
    private SpravcaPouzivatela spravcaPouzivatela;
    private SpravcaObrazoviek spravcaObrazoviek;
    private SpravcaMenu spravcaMenu;
    private SpravcaXP spravcaXP;

    /**
     * Konštruktor pre vytvorenie ovládača hlavného okna
     * @param hlavneOkno Hlavné okno aplikácie
     */
    public OvladacHlavnehoOkna(JFrame hlavneOkno) {
        this.hlavneOkno = hlavneOkno;
        this.kontajner = new UIKontajner();

        // Inicializácia správcov
        this.spravcaPouzivatela = new SpravcaPouzivatela(this);
        this.spravcaKvizu = new SpravcaKvizu(this);
        this.spravcaXP = new SpravcaXP();
        this.spravcaObrazoviek = new SpravcaObrazoviek(this);
        this.spravcaMenu = new SpravcaMenu(this);

        // Inicializácia hlavného okna
        this.hlavneOkno.getContentPane().removeAll();
        this.hlavneOkno.setLayout(new BorderLayout());
        this.hlavneOkno.add(this.kontajner, BorderLayout.CENTER);

        // Vytvorenie menu
        this.spravcaMenu.vytvorMenu();

        // Revalidácia okna
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Zobrazí úvodnú obrazovku
     */
    public void zobrazUvodnuObrazovku() {
        Pouzivatel aktualny = spravcaPouzivatela.getAktualnyPouzivatel();
        if (aktualny != null) {
            zobrazHlavneMenu();
        } else {
            spravcaObrazoviek.zobrazUvodnuObrazovku();
        }
    }

    /**
     * Zobrazí hlavné menu
     */
    public void zobrazHlavneMenu() {
        // Odstránime navigačné panely
        spravcaObrazoviek.odstranNavigaciuPanel();

        HlavneMenu hlavneMenu = new HlavneMenu(this);
        kontajner.pridajKomponent(hlavneMenu);
    }

    /**
     * Zobrazí otázku
     */
    public void zobrazOtazku() {
        spravcaKvizu.zobrazOtazku();
    }

    /**
     * Načíta otázky
     */
    public void nacitajOtazky() {
        spravcaKvizu.nacitajOtazky();
    }

    // Gettery
    public JFrame getHlavneOkno() { return hlavneOkno; }
    public UIKontajner getKontajner() { return kontajner; }
    public SpravcaPouzivatela getSpravcaPouzivatela() { return spravcaPouzivatela; }
    public SpravcaKvizu getSpravcaKvizu() { return spravcaKvizu; }
    public SpravcaXP getSpravcaXP() { return spravcaXP; }

    public Pouzivatel getAktualnyPouzivatel() {
        return spravcaPouzivatela.getAktualnyPouzivatel();
    }

    /**
     * Pridá XP body aktuálnemu používateľovi
     * @param xp Počet XP bodov
     */
    public void pridajXP(int xp) {
        Pouzivatel aktualny = spravcaPouzivatela.getAktualnyPouzivatel();
        if (aktualny != null) {
            spravcaXP.pridajXP(xp, aktualny);
        }
    }
}