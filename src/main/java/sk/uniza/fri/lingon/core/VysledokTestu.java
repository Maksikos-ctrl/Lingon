package sk.uniza.fri.lingon.core;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

/**
 * Trieda reprezentujúca výsledok testu
 */
public class VysledokTestu implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String kategoriaNazov;
    private final int pocetOtazok;
    private int spravneOdpovede;
    private int nespravneOdpovede;
    private LocalDateTime casUkoncenia;
    private double uspesnost;
    private boolean ulozeny = false;

    private String pouzivatelEmail;

    /**
     * Konštruktor pre vytvorenie výsledku testu
     */
    public VysledokTestu(String kategoriaId, String kategoriaNazov, int pocetOtazok) {
        this.kategoriaNazov = kategoriaNazov;
        this.pocetOtazok = pocetOtazok;
        this.spravneOdpovede = 0;
        this.nespravneOdpovede = 0;
        this.casUkoncenia = null;
        this.pouzivatelEmail = null;
    }

    /**
     * Nastaví email používateľa
     * @param email Email používateľa
     */
    public void setPouzivatelEmail(String email) {
        this.pouzivatelEmail = email;
    }

    /**
     * Vráti email používateľa
     * @return Email používateľa
     */
    public String getPouzivatelEmail() {
        return this.pouzivatelEmail;
    }


    /**
     * Pridá správnu odpoveď
     */
    public void pridajSpravnuOdpoved() {
        this.spravneOdpovede++;
    }

    /**
     * Pridá nesprávnu odpoveď
     */
    public void pridajNespravnuOdpoved() {
        this.nespravneOdpovede++;
    }

    /**
     * Ukončí test a nastaví čas ukončenia
     */
    public void ukonciTest() {
        if (this.casUkoncenia == null) {
            this.casUkoncenia = LocalDateTime.now();
            this.uspesnost = (double)this.spravneOdpovede / this.pocetOtazok * 100;
        }
    }

    /**
     * Vráti formátovaný čas ukončenia
     */
    public String getFormatovanyCas() {
        if (this.casUkoncenia == null) {
            return "Neukončený";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return this.casUkoncenia.format(formatter);
    }

    /**
     * Označí výsledok ako uložený
     */
    public void setUlozeny(boolean ulozeny) {
        this.ulozeny = ulozeny;
    }

    /**
     * Zistí, či bol výsledok už uložený
     */
    public boolean isUlozeny() {
        return this.ulozeny;
    }

    // Gettery
    public String getKategoriaNazov() {
        return this.kategoriaNazov;
    }
    public int getPocetOtazok() {
        return this.pocetOtazok;
    }
    public int getSpravneOdpovede() {
        return this.spravneOdpovede;
    }
    public int getNespravneOdpovede() {
        return this.nespravneOdpovede;
    }
    public LocalDateTime getCasUkoncenia() {
        return this.casUkoncenia;
    }
    public double getUspesnost() {
        return this.uspesnost;
    }
}