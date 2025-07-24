package sk.uniza.fri.lingon.grafika.spravcovia;

import sk.uniza.fri.lingon.core.VysledokTestu;
import sk.uniza.fri.lingon.db.DatabaseManager;
import sk.uniza.fri.lingon.db.HistoriaManager;
import sk.uniza.fri.lingon.grafika.hlavny.OvladacHlavnehoOkna;

import java.util.List;

/**
 * Správca histórie testov
 */
public class SpravcaHistorie {
    private final OvladacHlavnehoOkna ovladac;

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
        System.out.println("📝 Ukladám výsledok testu do databázy...");

        // Zabezpečíme, aby bol test ukončený pred uložením
        if (vysledok.getCasUkoncenia() == null) {
            vysledok.ukonciTest();
        }

        // Nastavíme email aktuálneho používateľa
        if (this.ovladac.getAktualnyPouzivatel() != null) {
            vysledok.setPouzivatelEmail(this.ovladac.getAktualnyPouzivatel().getEmail());
        } else {
            vysledok.setPouzivatelEmail("unknown");
            System.err.println("⚠️ Aktuálny používateľ nie je nastavený!");
        }


        DatabaseManager.ulozVysledok(vysledok);

        System.out.println("✅ Výsledok testu úspešne uložený do databázy pre používateľa: " + vysledok.getPouzivatelEmail());
    }

}