package sk.uniza.fri.lingon.core;

/**
 * Implementacia strategie pre kontrolu presnej zhody odpovedi
 */
public class PresnaZhodaStrategia implements IOdpovedovaStrategia {
    /**
     * Validuje ci vstup od uzivatela presne zodpoveda ocakavanej odpovedi
     * @param vstup Vstup od uzivatela
     * @param ocakavany Ocakavana odpoved
     * @return true ak sa vstup presne zhoduje s ocakavanou odpovedou, inak false
     */
    @Override
    public boolean validuj(String vstup, Object ocakavany) {
        if (ocakavany == null || vstup == null) {
            return false;
        }

        return vstup.trim().equalsIgnoreCase(ocakavany.toString().trim());
    }

}