package sk.uniza.fri.lingon.pouzivatel;

import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.gui.IZadanie;

/**
 * Trieda reprezentujuca pouzivatela aplikacie
 */
public class Pouzivatel {
    private String meno;
    private String email;
    private int celkoveXP;
    private int spravneOdpovede;
    private int nespravneOdpovede;

    /**
     * Konstruktor pre vytvorenie noveho pouzivatela
     * @param meno Meno pouzivatela
     * @param email Email pouzivatela
     */
    public Pouzivatel(String meno, String email) {
        this.meno = meno;
        this.email = email;
        this.celkoveXP = 0;
        this.spravneOdpovede = 0;
        this.nespravneOdpovede = 0;
    }

    /**
     * Prida XP body pouzivatelovi
     * @param xp Pocet XP bodov na pridanie
     */
    public void pridajXP(int xp) {
        if (xp > 0) {
            this.celkoveXP += xp;
        }
    }

    /**
     * Zaznamená správnu odpoveď a pridá XP
     * @param typOtazky Typ otázky, ktorá bola zodpovedaná
     * @return Počet získaných XP
     */
    public int zaznamenajSpravnuOdpoved(String typOtazky) {
        this.spravneOdpovede++;

        // Rôzne typy otázok majú rôzne XP hodnoty
        int ziskaneXP = 0;

        switch (typOtazky) {
            case "Výberová otázka":
                ziskaneXP = 5;
                break;
            case "Vpisovacia otázka":
                ziskaneXP = 10;
                break;
            case "Párovacia otázka":
                ziskaneXP = 15;
                break;
            default:
                ziskaneXP = 5;
        }

        // Pridáme XP používateľovi
        this.pridajXP(ziskaneXP);

        return ziskaneXP;
    }

    /**
     * Zaznamená nesprávnu odpoveď
     */
    public void zaznamenajNespravnuOdpoved() {
        this.nespravneOdpovede++;
    }

    /**
     * Prida lekciu uzivatelovi
     * @param lekcia Lekcia na pridanie
     */
    public void pridajLekciu(IZadanie lekcia) {
        // Tu by sme mohli pridat lekciu do zoznamu absolvovanych lekcii
        // alebo aktualizovat stav ucenia pouzivatela
        System.out.println("Uzivatel " + this.meno + " pridal lekciu: " + lekcia.getTypOtazky());
    }

    /**
     * Vrati meno pouzivatela
     * @return Meno pouzivatela
     */
    public String getMeno() {
        return this.meno;
    }

    /**
     * Vrati email pouzivatela
     * @return Email pouzivatela
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Vrati celkove XP body pouzivatela
     * @return Celkove XP body
     */
    public int getCelkoveXP() {
        return this.celkoveXP;
    }

    /**
     * Vrati počet správnych odpovedí
     * @return Počet správnych odpovedí
     */
    public int getSpravneOdpovede() {
        return this.spravneOdpovede;
    }

    /**
     * Vrati počet nesprávnych odpovedí
     * @return Počet nesprávnych odpovedí
     */
    public int getNespravneOdpovede() {
        return this.nespravneOdpovede;
    }


    /**
     * Vrati úspešnosť používateľa v percentách
     * @return Percentuálna úspešnosť
     */
    public int getUspesnost() {
        int celkovyPocet = this.spravneOdpovede + this.nespravneOdpovede;
        if (celkovyPocet == 0) {
            return 0;
        }
        return (int)((double)this.spravneOdpovede / celkovyPocet * 100);
    }

    /**
     * Aktualizuje úspešnosť na základe výsledku testu
     * @param vysledok Výsledok testu
     */
    public void aktualizujUspesnost(VysledokTestu vysledok) {
        // Pridáme správne a nesprávne odpovede z testu
        this.spravneOdpovede += vysledok.getSpravneOdpovede();
        this.nespravneOdpovede += vysledok.getNespravneOdpovede();

        // Aktualizujeme aj XP (voliteľné)
        this.pridajXP((int)vysledok.getUspesnost() / 10); // 10 XP za každých 100% úspešnosti
    }

    /**
     * Nastavi XP body pouzivatela
     * @param xp Hodnota XP bodov
     */
    public void setCelkoveXP(int xp) {
        this.celkoveXP = xp;
    }

    /**
     * Nastavi pocet spravnych odpovedi pouzivatela
     * @param pocet Pocet spravnych odpovedi
     */
    public void setSpravneOdpovede(int pocet) {
        this.spravneOdpovede = pocet;
    }

    /**
     * Nastavi pocet nespravnych odpovedi pouzivatela
     * @param pocet Pocet nespravnych odpovedi
     */
    public void setNespravneOdpovede(int pocet) {
        this.nespravneOdpovede = pocet;
    }



}