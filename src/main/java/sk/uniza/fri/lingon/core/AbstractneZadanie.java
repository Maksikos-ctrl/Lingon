package sk.uniza.fri.lingon.core;

import sk.uniza.fri.lingon.GUI.IZadanie;

/**
 * Abstraktna trieda pre vsetky typy zadani/otazok v aplikacii Lingon
 * Zakladna implementacia polymorfizmu pre rozne typy otazok
 */
public abstract class AbstractneZadanie implements IZadanie {
    private String text;
    private Object odpoved;
    private IOdpovedovaStrategia strategia;

    /**
     * Konstruktor pre vytvorenie noveho zadania
     * @param text Text otazky
     */
    public AbstractneZadanie(String text) {
        this.text = text;
    }

    /**
     * Vrati text otazky
     * @return Text otazky
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * Abstraktna metoda pre zobrazenie grafickeho rozhrania otazky
     * Kazdy typ otazky ma vlastnu implementaciu
     * @param kontajner UI kontajner pre zobrazenie
     */
    @Override
    public abstract void zobrazGrafiku(UIKontajner kontajner);

    /**
     * Skontroluje odpoved uzivatela
     * @param vstup Odpoved od uzivatela
     * @return true ak je odpoved spravna, inak false
     */
    @Override
    public boolean skontrolujOdpoved(String vstup) {
        if (this.strategia == null) {
            throw new IllegalStateException("Strategia pre kontrolu odpovedi nie je nastavena");
        }
        return this.strategia.validuj(vstup, this.odpoved);
    }

    /**
     * Nastavi strategiu pre kontrolu odpovedi
     * Umoznuje dynamicky menit sposob kontroly odpovedi
     * @param strategia Strategia pre kontrolu odpovedi
     */
    public void setStrategia(IOdpovedovaStrategia strategia) {
        this.strategia = strategia;
    }

    /**
     * Nastavi spravnu odpoved
     * @param odpoved Spravna odpoved
     */
    protected void setOdpoved(Object odpoved) {
        this.odpoved = odpoved;
    }

    /**
     * Getter pre aktualnu strategiu kontroly
     * @return Aktualna strategia
     */
    public IOdpovedovaStrategia getStrategia() {
        return this.strategia;
    }
}