package sk.uniza.fri.lingon.pouzivatel;

/**
 * Trieda reprezentujuca pouzivatela aplikacie
 */
public class Pouzivatel {
    private final String meno;
    private final String email;
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

    /**
     * Vráti úroveň používateľa na základe získaných XP
     * @return Úroveň používateľa (0-3)
     */
    public int getUroven() {
        if (this.celkoveXP >= 80) {
            return 3;
        } else if (this.celkoveXP >= 50) {
            return 2;
        } else if (this.celkoveXP >= 30) {
            return 1;
        } else {
            return 0;
        }
    }



}