package sk.uniza.fri.lingon.core;

/**
 * Rozhranie pre strategie kontroly odpovedi
 * Implementacia vzoru Strategy pre rôzne typy kontroly odpovedi
 * Umoznuje polymorfne spracovanie roznych typov odpovedi
 */
public interface IOdpovedovaStrategia {
    /**
     * Validuje odpoved od uzivatela
     * @param vstup Vstup od uzivatela
     * @param ocakavany Ocakavana odpoved
     * @return true ak je odpoved spravna, inak false
     */
    boolean validuj(String vstup, Object ocakavany);

}