package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.db.HistoriaManager;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import java.util.List;

/**
 * Správca histórie testov
 */
public class SpravcaHistorie {
    private OvladacHlavnehoOkna ovladac;

    /**
     * Konštruktor pre vytvorenie správcu histórie
     * @param ovladac Ovládač hlavného okna
     */
    public SpravcaHistorie(OvladacHlavnehoOkna ovladac) {
        this.ovladac = ovladac;
    }

    /**
     * Získa históriu testov pre aktuálneho používateľa
     * @return Zoznam výsledkov testov
     */
    public List<VysledokTestu> getHistoria() {
        if (this.ovladac.getAktualnyPouzivatel() != null) {
            return HistoriaManager.nacitajHistoriuPouzivatela(this.ovladac.getAktualnyPouzivatel().getEmail());
        } else {
            // Ak nemáme používateľa, vrátime prázdny zoznam
            return List.of();
        }
    }

    /**
     * Uloží výsledok testu do histórie
     * @param vysledok Výsledok testu
     */
    public void ulozVysledok(VysledokTestu vysledok) {
        // Uložíme len ak ešte nebol uložený
        if (!vysledok.isUlozeny()) {
            // Zabezpečíme, aby bol test ukončený pred uložením
            if (vysledok.getCasUkoncenia() == null) {
                vysledok.ukonciTest();
            }

            // Nastavíme email aktuálneho používateľa
            if (this.ovladac.getAktualnyPouzivatel() != null) {
                vysledok.setPouzivatelEmail(this.ovladac.getAktualnyPouzivatel().getEmail());
            } else {
                vysledok.setPouzivatelEmail("unknown");
            }

            HistoriaManager.ulozVysledok(vysledok);
            vysledok.setUlozeny(true);
            System.out.println("Výsledok testu úspešne uložený do databázy pre používateľa: " + vysledok.getPouzivatelEmail());
        } else {
            System.out.println("Výsledok testu už bol predtým uložený, preskakujem.");
        }
    }

    /**
     * Vymaže históriu testov aktuálneho používateľa
     */
    public void vymazHistoriu() {
        if (this.ovladac.getAktualnyPouzivatel() != null) {
            HistoriaManager.vymazHistoriuPouzivatela(this.ovladac.getAktualnyPouzivatel().getEmail());
        }
    }
}