package sk.uniza.fri.lingon.core;

import java.awt.Color;

/**
 * Reprezentuje kategoriu otazok v kvize
 */
public class KategoriaTrivia {
    private int id;
    private String nazov;
    private Color farba;

    /**
     * Konstruktor pre vytvorenie kategorie
     * @param id ID kategorie
     * @param nazov Nazov kategorie
     * @param farba Farba kategorie pre UI
     */
    public KategoriaTrivia(int id, String nazov, Color farba) {
        this.id = id;
        this.nazov = nazov;
        this.farba = farba;
    }

    /**
     * Getter pre ID kategorie
     * @return ID kategorie
     */
    public int getId() {
        return this.id;
    }

    /**
     * Getter pre nazov kategorie
     * @return Nazov kategorie
     */
    public String getNazov() {
        return this.nazov;
    }

    /**
     * Getter pre farbu kategorie
     * @return Farba kategorie
     */
    public Color getFarba() {
        return this.farba;
    }
}