package sk.uniza.fri.lingon.pouzivatel.lekcia;

import sk.uniza.fri.lingon.gui.ILekcia;
import sk.uniza.fri.lingon.gui.IZadanie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trieda reprezentujuca jednu lekciu v aplikacii
 */
public class Lekcia implements ILekcia {
    private String nazov;
    private List<IZadanie> zadania;
    private int id;

    /**
     * Konstruktor pre vytvorenie novej lekcie
     * @param nazov Nazov lekcie
     * @param zadania Zoznam zadani v lekcii
     * @param id Identifikator lekcie
     */
    public Lekcia(String nazov, List<IZadanie> zadania, int id) {
        this.nazov = nazov;
        this.zadania = new ArrayList<>(zadania);
        this.id = id;
    }

    /**
     * Vrati nazov lekcie
     * @return Nazov lekcie
     */
    @Override
    public String getNazov() {
        return this.nazov;
    }

    /**
     * Vrati zoznam zadani v lekcii
     * @return Zoznam zadani
     */
    @Override
    public List<IZadanie> getZadania() {
        return Collections.unmodifiableList(this.zadania);
    }

    /**
     * Vrati identifikator lekcie
     * @return ID lekcie
     */
    @Override
    public int getId() {
        return this.id;
    }
}