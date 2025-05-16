package sk.uniza.fri.lingon.core;

/**
 * Strategia ktora nekontroluje velke a male pismena
 * Implementacia rozhrania IOdpovedovaStrategia
 */
public class NerozlisujucaStrategia implements IOdpovedovaStrategia {

    /**
     * Kontroluje ci je vstup spravny bez ohľadu na veľké/malé písmená
     * @param vstup Odpoved od uzivatela
     * @param ocakavany Ocakavana odpoved
     * @return true ak su odpovede rovnake (case-insensitive)
     */
    @Override
    public boolean validuj(String vstup, Object ocakavany) {
        if (vstup == null || ocakavany == null) {
            return false;
        }

        String vstupTrim = vstup.trim();
        String ocakavanyTrim = ocakavany.toString().trim();

        return vstupTrim.equalsIgnoreCase(ocakavanyTrim);
    }

    /**
     * Vrati nazov strategie
     * @return Nazov strategie
     */
    @Override
    public String getNazovStrategie() {
        return "Nerozlišujúca stratégia (veľké/malé písmená)";
    }
}