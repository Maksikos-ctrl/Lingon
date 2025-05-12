package sk.uniza.fri.lingon.core;

/**
 * Interface pre spracovanie odpovede na otazku
 */
public interface OdpovedDelegate {
    /**
     * Spracuje odpoved pouzivatela
     * @param odpoved Text odpovede
     * @param jeSpravna Ci bola odpoved spravna
     */
    void spracujOdpoved(String odpoved, boolean jeSpravna, String typOtazky);
}