package sk.uniza.fri.lingon.GUI;

import java.util.List;

/**
 * Rozhranie pre lekcie
 */
public interface ILekcia {
    /**
     * Vrati nazov lekcie
     * @return Nazov lekcie
     */
    String getNazov();

    /**
     * Vrati zoznam zadani v lekcii
     * @return Zoznam zadani
     */
    List<IZadanie> getZadania();

    /**
     * Vrati identifikator lekcie
     * @return ID lekcie
     */
    int getId();
}