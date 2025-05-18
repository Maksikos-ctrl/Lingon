package sk.uniza.fri.lingon.db;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.uniza.fri.lingon.gui.IZadanie;
import sk.uniza.fri.lingon.core.KategoriaTrivia;
import sk.uniza.fri.lingon.core.ObsahujeStrategia;
import sk.uniza.fri.lingon.pouzivatel.lekcia.ParovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VpisovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VyberovaOtazka;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Trieda pre nacitanie otazok z JSON API.
 * Demonstruje pouzitie polymorfizmu pri vytvarani roznych typov otazok.
 */
public final class OtazkyLoader {

    // Open Trivia Database API URLs
    private static final String API_BASE_URL = "https://opentdb.com/api.php";
    private static final String API_CATEGORIES_URL = "https://opentdb.com/api_category.php";
    private static final Random RANDOM = new Random();

    // Množina pre uchovávanie už použitých textov otázok
    private static final Set<String> POUZITE_OTAZKY = new HashSet<>();

    // Mapa farieb pre kategorie
    private static final Map<Integer, Color> CATEGORY_COLORS = new HashMap<>();

    static {
        CATEGORY_COLORS.put(9, new Color(65, 105, 225));    // General Knowledge
        CATEGORY_COLORS.put(10, new Color(139, 0, 139));    // Entertainment: Books
        CATEGORY_COLORS.put(11, new Color(255, 69, 0));     // Entertainment: Film
        CATEGORY_COLORS.put(12, new Color(50, 205, 50));    // Entertainment: Music
        CATEGORY_COLORS.put(13, new Color(255, 20, 147));   // Entertainment: Musicals
        CATEGORY_COLORS.put(14, new Color(148, 0, 211));    // Entertainment: Television
        CATEGORY_COLORS.put(15, new Color(255, 140, 0));    // Entertainment: Video Games
        CATEGORY_COLORS.put(16, new Color(0, 139, 139));    // Entertainment: Board Games
        CATEGORY_COLORS.put(17, new Color(34, 139, 34));    // Science & Nature
        CATEGORY_COLORS.put(18, new Color(70, 130, 180));   // Science: Computers
        CATEGORY_COLORS.put(19, new Color(46, 139, 87));    // Science: Mathematics
        CATEGORY_COLORS.put(20, new Color(160, 82, 45));     // Mythology
        CATEGORY_COLORS.put(21, new Color(255, 165, 0));     // Sports
        CATEGORY_COLORS.put(22, new Color(0, 128, 128));     // Geography
        CATEGORY_COLORS.put(23, new Color(139, 69, 19));    // History
        CATEGORY_COLORS.put(24, new Color(75, 0, 130));      // Politics
        CATEGORY_COLORS.put(25, new Color(219, 112, 147));   // Art
        CATEGORY_COLORS.put(26, new Color(189, 183, 107));   // Celebrities
        CATEGORY_COLORS.put(27, new Color(143, 188, 143));   // Animals
        CATEGORY_COLORS.put(28, new Color(105, 105, 105));   // Vehicles
        CATEGORY_COLORS.put(29, new Color(220, 20, 60));     // Entertainment: Comics
        CATEGORY_COLORS.put(30, new Color(72, 61, 139));     // Science: Gadgets
        CATEGORY_COLORS.put(31, new Color(255, 99, 71));     // Entertainment: Anime
        CATEGORY_COLORS.put(32, new Color(32, 178, 170));    // Entertainment: Cartoon
    }

    private OtazkyLoader() {
        // Private constructor to prevent instantiation
    }


    /**
     * Nacita kategorie z API.
     * @return Zoznam kategorii
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    public static List<KategoriaTrivia> nacitajKategorie() throws IOException {
        List<KategoriaTrivia> kategorie = new ArrayList<>();
        String jsonData = getDataFromApi(API_CATEGORIES_URL);
        JSONObject root = new JSONObject(jsonData);
        JSONArray triviaCategories = root.getJSONArray("trivia_categories");

        for (int i = 0; i < triviaCategories.length(); i++) {
            JSONObject item = triviaCategories.getJSONObject(i);
            int id = item.getInt("id");
            String nazov = item.getString("name");
            Color farba = CATEGORY_COLORS.getOrDefault(id, new Color(100, 100, 100));
            kategorie.add(new KategoriaTrivia(id, nazov, farba));
        }

        return kategorie;
    }

    /**
     * Nacita otazky pre specificku kategoriu.
     * @param kategoriaId ID kategorie
     * @return Zoznam zadani/otazok
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    public static List<IZadanie> nacitajOtazkyPreKategoriu(int kategoriaId) throws IOException {
        String url = API_BASE_URL + "?amount=15&category=" + kategoriaId + "&type=multiple";
        return nacitajOtazkyZUrl(url);
    }

    /**
     * Nacita otazky z API (vsetky kategorie).
     * @return Zoznam zadani/otazok
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    public static List<IZadanie> nacitajOtazky() throws IOException {
        String url = API_BASE_URL + "?amount=15&type=multiple";
        return nacitajOtazkyZUrl(url);
    }

    /**
     * Kontroluje, či je otázka vhodná pre všetky typy otázok
     * @param textOtazky Text otázky
     * @return true ak je otázka vhodná, false ak nie
     */
    private static boolean jeVhodnaOtazka(String textOtazky) {
        // Ak je otázka už použitá, nie je vhodná
        if (POUZITE_OTAZKY.contains(textOtazky)) {
            return false;
        }

        // Otázky s príliš dlhým textom nie sú vhodné pre párovanie
        return textOtazky.length() <= 150;
    }

    /**
     * Nacita otazky z konkretnej URL.
     * @param url URL adresa API
     * @return Zoznam zadani/otazok
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    private static List<IZadanie> nacitajOtazkyZUrl(String url) throws IOException {
        List<IZadanie> otazky = new ArrayList<>();
        String jsonData = getDataFromApi(url);
        JSONObject root = new JSONObject(jsonData);
        JSONArray results = root.getJSONArray("results");

        // Vytvoriť zoznam všetkých textov otázok a odpovedí z výsledkov
        List<JSONObject> vsetkyOtazky = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            String text = decodeHtml(item.getString("question"));

            // Kontrola, či nebola otázka už použitá
            if (!POUZITE_OTAZKY.contains(text) && jeVhodnaOtazka(text)) {
                vsetkyOtazky.add(item);
                POUZITE_OTAZKY.add(text); // Označíme otázku ako použitú
            }
        }

        // Vytvoriť maximálne 10 otázok
        int pocetOtazok = Math.min(10, vsetkyOtazky.size());
        for (int i = 0; i < pocetOtazok; i++) {
            JSONObject item = vsetkyOtazky.get(i);
            String text = decodeHtml(item.getString("question"));
            String spravnaOdpoved = decodeHtml(item.getString("correct_answer"));

            // Pre VpisovaciaOtazka skontrolujeme, či nejde o "Which" otázku
            int typOtazky = RANDOM.nextInt(3); // 0, 1, 2 = 3 typy otazok

            if (typOtazky == 1 && !VpisovaciaOtazka.jeVhodnaOtazka(text)) {
                // Ak otázka začína "Which" a mal by to byť typ VpisovaciaOtazka,
                // zmeníme typ na VyberovaOtazka
                typOtazky = 0;
            }

            // Vytvorenie otázky podľa typu
            switch (typOtazky) {
                case 0:
                    otazky.add(createVyberovaOtazka(item, text, spravnaOdpoved));
                    break;
                case 1:
                    otazky.add(createVpisovaciaOtazka(text, spravnaOdpoved));
                    break;
                case 2:
                    otazky.add(createParovaciaOtazka(vsetkyOtazky, text, spravnaOdpoved, i));
                    break;
                default:
                    break;
            }
        }

        return otazky;
    }

    private static VyberovaOtazka createVyberovaOtazka(JSONObject item, String text, String spravnaOdpoved) {
        JSONArray nespravneOdpovede = item.getJSONArray("incorrect_answers");
        List<String> vsetkyMoznosti = new ArrayList<>();
        vsetkyMoznosti.add(spravnaOdpoved);

        for (int j = 0; j < nespravneOdpovede.length(); j++) {
            vsetkyMoznosti.add(decodeHtml(nespravneOdpovede.getString(j)));
        }

        return new VyberovaOtazka(text, vsetkyMoznosti, spravnaOdpoved);
    }

    private static VpisovaciaOtazka createVpisovaciaOtazka(String text, String spravnaOdpoved) {
        VpisovaciaOtazka vpisovaciaOtazka = new VpisovaciaOtazka(text, spravnaOdpoved);
        if (RANDOM.nextBoolean()) {
            vpisovaciaOtazka.setStrategia(new ObsahujeStrategia());
        }
        return vpisovaciaOtazka;
    }

    private static ParovaciaOtazka createParovaciaOtazka(List<JSONObject> vsetkyOtazky, String text,
                                                         String spravnaOdpoved, int currentIndex) {
        Map<String, String> pary = new HashMap<>();
        pary.put(skratText(text), spravnaOdpoved);

        //int countPairs = Math.min(3, vsetkyOtazky.size() - 1);
        List<Integer> usedIndexes = new ArrayList<>();
        usedIndexes.add(currentIndex);

        while (pary.size() < 4 && usedIndexes.size() < vsetkyOtazky.size()) {
            int randomIndex = RANDOM.nextInt(vsetkyOtazky.size());
            if (!usedIndexes.contains(randomIndex)) {
                usedIndexes.add(randomIndex);
                JSONObject pairItem = vsetkyOtazky.get(randomIndex);
                String pairQuestion = decodeHtml(pairItem.getString("question"));
                String pairAnswer = decodeHtml(pairItem.getString("correct_answer"));

                pary.put(skratText(pairQuestion), pairAnswer);
            }
        }

        return new ParovaciaOtazka("Spárujte správne dvojice:", pary);
    }

    /**
     * Skracuje text pre párovanie a upravuje ho pre lepšie zobrazenie
     */
    private static String skratText(String text) {
        // Odstránime HTML tagy a zbytočné biele znaky
        text = text.replaceAll("<[^>]*>", "").trim();

        // Skrátime text, ak je príliš dlhý
        if (text.length() > 80) {
            // Skúsime nájsť vhodné miesto na zalamenie (napr. medzera)
            int cutIndex = text.lastIndexOf(' ', 77);
            if (cutIndex > 50) {
                return text.substring(0, cutIndex) + "...";
            }
            return text.substring(0, 77) + "...";
        }
        return text;
    }


    /**
     * Nacita demo otazky (pre pripad, ze API nie je dostupne).
     * @return Zoznam demo zadani/otazok
     */
    public static List<IZadanie> getDemoOtazky() {
        List<IZadanie> otazky = new ArrayList<>();

        // Demo vyberove otazky
        otazky.add(new VyberovaOtazka(
                "Ako sa povie 'Dobrý deň' po anglicky?",
                Arrays.asList("Hello", "Good day", "Good morning", "Hi"),
                "Good day"
        ));

        otazky.add(new VyberovaOtazka(
                "Ako sa povie 'Ďakujem' po anglicky?",
                Arrays.asList("Please", "Sorry", "Thank you", "Excuse me"),
                "Thank you"
        ));

        // Demo vpisovacie otazky
        VpisovaciaOtazka otazka1 = new VpisovaciaOtazka(
                "Ako sa povie 'Ahoj' po anglicky?",
                "Hello"
        );

        VpisovaciaOtazka otazka2 = new VpisovaciaOtazka(
                "Ako sa povie 'Prosím' po anglicky?",
                "Please"
        );
        otazka2.setStrategia(new ObsahujeStrategia());

        otazky.add(otazka1);
        otazky.add(otazka2);

        // Demo parovacia otazka
        otazky.add(createDemoParovaciaOtazka1());
        otazky.add(createDemoParovaciaOtazka2());

        return otazky;
    }

    private static ParovaciaOtazka createDemoParovaciaOtazka1() {
        Map<String, String> pary1 = new HashMap<>();
        pary1.put("Hlavné mesto Slovenska", "Bratislava");
        pary1.put("Hlavné mesto Česka", "Praha");
        pary1.put("Hlavné mesto Francúzska", "Paríž");
        pary1.put("Hlavné mesto Nemecka", "Berlín");
        return new ParovaciaOtazka("Spárujte hlavné mestá s krajinami:", pary1);
    }

    private static ParovaciaOtazka createDemoParovaciaOtazka2() {
        Map<String, String> pary2 = new HashMap<>();
        pary2.put("Pes", "Dog");
        pary2.put("Mačka", "Cat");
        pary2.put("Kôň", "Horse");
        pary2.put("Myš", "Mouse");
        return new ParovaciaOtazka("Spárujte slovenské a anglické názvy zvierat:", pary2);
    }

    /**
     * Ziska data z API.
     * @param apiUrl URL adresa API
     * @return JSON data z API
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    private static String getDataFromApi(String apiUrl) throws IOException {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("API request was interrupted", e);
        }
    }

    /**
     * Dekoduje HTML entity v texte (napr. &quot; na ").
     * @param html Text s HTML entitami
     * @return Dekodovany text
     */
    private static String decodeHtml(String html) {
        return html.replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#039;", "'");
    }
}