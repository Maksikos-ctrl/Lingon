package sk.uniza.fri.lingon.grafika.hlavny;

import sk.uniza.fri.lingon.core.UIKontajner;
import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaHistorie;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaKvizu;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaMenu;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaObrazoviek;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaPouzivatela;
import sk.uniza.fri.lingon.grafika.spravcovia.SpravcaXP;
import sk.uniza.fri.lingon.pouzivatel.Pouzivatel;
import sk.uniza.fri.lingon.grafika.obrazovky.HlavneMenu;
import sk.uniza.fri.lingon.grafika.obrazovky.VysledkyObrazovka;
import sk.uniza.fri.lingon.grafika.obrazovky.HistoriaObrazovka;

import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 * Hlavný ovládač aplikácie
 * Koordinuje prácu ostatných správcov
 */
public class  OvladacHlavnehoOkna {
    private final JFrame hlavneOkno;
    private final UIKontajner kontajner;
    private final SpravcaKvizu spravcaKvizu;
    private final SpravcaPouzivatela spravcaPouzivatela;
    private final SpravcaObrazoviek spravcaObrazoviek;
    private final SpravcaXP spravcaXP;
    private final SpravcaHistorie spravcaHistorie;

    /**
     * Konštruktor pre vytvorenie ovládača hlavného okna
     * @param hlavneOkno Hlavné okno aplikácie
     */
    public OvladacHlavnehoOkna(JFrame hlavneOkno) {
        this.hlavneOkno = hlavneOkno;
        this.kontajner = new UIKontajner();
        this.kontajner.setOvladac(this); // Set the ovladac reference

        // Inicializácia správcov
        this.spravcaPouzivatela = new SpravcaPouzivatela(this);
        this.spravcaKvizu = new SpravcaKvizu(this);
        this.spravcaXP = new SpravcaXP();
        this.spravcaObrazoviek = new SpravcaObrazoviek(this);
        SpravcaMenu spravcaMenu = new SpravcaMenu(this);

        // Inicializácia hlavného okna
        this.hlavneOkno.getContentPane().removeAll();
        this.hlavneOkno.setLayout(new BorderLayout());
        this.hlavneOkno.add(this.kontajner, BorderLayout.CENTER);

        // Nastavenie vzhľadu
        this.spravcaHistorie = new SpravcaHistorie(this);

        // Vytvorenie menu
        spravcaMenu.vytvorMenu();

        // Revalidácia okna
        this.hlavneOkno.revalidate();
        this.hlavneOkno.repaint();
    }

    /**
     * Zobrazí úvodnú obrazovku
     */
    public void zobrazUvodnuObrazovku() {
        Pouzivatel aktualny = this.spravcaPouzivatela.getAktualnyPouzivatel();
        if (aktualny != null) {
            this.zobrazHlavneMenu();
        } else {
            this.spravcaObrazoviek.zobrazUvodnuObrazovku();
        }
    }

    /**
     * Zobrazí hlavné menu
     */
    public void zobrazHlavneMenu() {
        // 🔄 Obnovíme aktuálneho používateľa pred zobrazením menu
        this.spravcaPouzivatela.obnovAktualnehoPozivatela();

        // Odstránime navigačné panely
        this.spravcaObrazoviek.odstranNavigaciuPanel();

        HlavneMenu hlavneMenu = new HlavneMenu(this);
        this.kontajner.pridajKomponent(hlavneMenu);
    }

    /**
     * Zobrazí otázku
     */
    public void zobrazOtazku() {
        this.spravcaKvizu.zobrazOtazku();
    }

    /**
     * Zobrazí výsledky testu
     * @param vysledok Výsledok testu
     */
    public void zobrazVysledky(VysledokTestu vysledok) {
        System.out.println("🎯 Zobrazujem výsledky testu...");

        // Ukončíme test, ak ešte nebol ukončený
        if (vysledok.getCasUkoncenia() == null) {
            vysledok.ukonciTest();
        }

        // ✅ Uložíme výsledok do databázy (tu sa automaticky vypočíta a uloží XP)
        this.spravcaHistorie.ulozVysledok(vysledok);

        // ✅ OBNOVÍME AKTUÁLNEHO POUŽÍVATEĽA S NOVÝMI XP Z DATABÁZY
        this.spravcaPouzivatela.obnovAktualnehoPozivatela();

        // Odstránime navigačné panely a zobrazíme výsledky
        this.spravcaObrazoviek.odstranNavigaciuPanel();
        VysledkyObrazovka vysledkyObrazovka = new VysledkyObrazovka(this, vysledok);
        this.kontajner.pridajKomponent(vysledkyObrazovka);

        System.out.println("✅ Výsledky testu zobrazené");
    }

    /**
     * Zobrazí históriu testov
     */
    public void zobrazHistoriu() {
        this.spravcaObrazoviek.odstranNavigaciuPanel();
        HistoriaObrazovka historiaObrazovka = new HistoriaObrazovka(this);
        this.kontajner.pridajKomponent(historiaObrazovka);
    }

    // Gettery
    public JFrame getHlavneOkno() {
        return this.hlavneOkno;
    }
    public UIKontajner getKontajner() {
        return this.kontajner;
    }
    public SpravcaPouzivatela getSpravcaPouzivatela() {
        return this.spravcaPouzivatela;
    }
    public SpravcaKvizu getSpravcaKvizu() {
        return this.spravcaKvizu;
    }
    public SpravcaXP getSpravcaXP() {
        return this.spravcaXP;
    }
    public SpravcaHistorie getSpravcaHistorie() {
        return this.spravcaHistorie;
    }

    public Pouzivatel getAktualnyPouzivatel() {
        return this.spravcaPouzivatela.getAktualnyPouzivatel();
    }

    /**
     * Pridá XP body aktuálnemu používateľovi
     * @param xp Počet XP bodov
     */
    @Deprecated
    public void pridajXP(int xp) {
//        Pouzivatel aktualny =  this.spravcaPouzivatela.getAktualnyPouzivatel();
//        if (aktualny != null) {
//            this.spravcaXP.pridajXP(xp, aktualny);
//        }
    }
}