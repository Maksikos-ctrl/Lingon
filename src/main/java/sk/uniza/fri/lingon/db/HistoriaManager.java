package sk.uniza.fri.lingon.db;

import sk.uniza.fri.lingon.core.VysledokTestu;
import java.util.List;

/**
 * Správca histórie testov - deleguje operácie na DatabaseManager
 */
public class HistoriaManager {

    /**
     * Uloží výsledok testu do databázy
     * @param vysledok Výsledok testu
     */
    public static void ulozVysledok(VysledokTestu vysledok) {
        // Delegujeme operáciu na DatabaseManager
        DatabaseManager.ulozVysledok(vysledok);
    }

    /**
     * Načíta históriu z databázy pre konkrétneho používateľa
     * @param email Email používateľa
     * @return Zoznam výsledkov testov
     */
    public static List<VysledokTestu> nacitajHistoriuPouzivatela(String email) {
        // Delegujeme operáciu na DatabaseManager
        return DatabaseManager.nacitajHistoriuPouzivatela(email);
    }

    /**
     * Vymaže históriu testov konkrétneho používateľa
     * @param email Email používateľa
     */
    public static void vymazHistoriuPouzivatela(String email) {
        // Delegujeme operáciu na DatabaseManager
        DatabaseManager.vymazHistoriuPouzivatela(email);
    }

}