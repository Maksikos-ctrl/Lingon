package sk.uniza.fri.lingon.gui;

import sk.uniza.fri.lingon.core.UIKontajner;

/**
 * Rozhranie pre vsetky typy zadani/otazok v aplikacii Lingon
 * Zakladny prvok polymorfizmu v aplikacii
 */
public interface IZadanie {
    /**
     * Vrati text otazky
     * @return Text otazky
     */
    String getText();

    /**
     * Zobrazi graficke rozhranie pre otazku
     * @param kontajner UI kontajner pre zobrazenie
     */
    void zobrazGrafiku(UIKontajner kontajner);

    /**
     * Skontroluje odpoved uzivatela
     * @param vstup Odpoved od uzivatela
     * @return true ak je odpoved spravna, inak false
     */
    boolean skontrolujOdpoved(String vstup);

    /**
     * Vrati typ otazky - umoznuje rozsirit system o nove typy otazok
     * @return Typ otazky ako String
     */
    String getTypOtazky();
}