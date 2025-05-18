package sk.uniza.fri.lingon.core;

/**
 * Implementacia strategie pre kontrolu, ci odpoved obsahuje klucove slovo
 * Demonštruje polymorfizmus v kontrole odpovedi
 */
public class ObsahujeStrategia implements IOdpovedovaStrategia {
    /**
     * Validuje ci vstup od uzivatela obsahuje ocakavanu odpoved
     * @param vstup Vstup od uzivatela
     * @param ocakavany Ocakavana odpoved
     * @return true ak vstup obsahuje ocakavanu odpoved, inak false
     */
    @Override
    public boolean validuj(String vstup, Object ocakavany) {
        if (ocakavany == null || vstup == null) {
            return false;
        }

        return vstup.toLowerCase().contains(ocakavany.toString().toLowerCase());
    }

}