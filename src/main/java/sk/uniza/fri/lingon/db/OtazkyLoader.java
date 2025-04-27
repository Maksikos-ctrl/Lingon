package sk.uniza.fri.lingon.db;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.uniza.fri.lingon.GUI.IZadanie;
import sk.uniza.fri.lingon.core.ObsahujeStrategia;
import sk.uniza.fri.lingon.pouzivatel.lekcia.ParovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VpisovaciaOtazka;
import sk.uniza.fri.lingon.pouzivatel.lekcia.VyberovaOtazka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Trieda pre nacitanie otazok z JSON API
 * Demonstruje pouzitie polymorfizmu pri vytvarani roznych typov otazok
 */
public class OtazkyLoader {
    // Open Trivia Database API - verejne dostupna API pre kvizove otazky
    private static final String API_URL = "https://opentdb.com/api.php?amount=10&type=multiple";
    private static final Random RANDOM = new Random();

    /**
     * Nacita otazky z API
     * @return Zoznam zadani/otazok
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    public static List<IZadanie> nacitajOtazky() throws IOException {
        List<IZadanie> otazky = new ArrayList<>();

        String jsonData = getDataFromApi();
        JSONObject root = new JSONObject(jsonData);
        JSONArray results = root.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);

            // Dekodovanie HTML entit v texte
            String text = decodeHtml(item.getString("question"));
            String spravnaOdpoved = decodeHtml(item.getString("correct_answer"));

            // Ziskat kategoriu
            String kategoria = item.getString("category");

            // Rozhodnutie o type otazky - pre demo urobime to nahodne
            int typOtazky = RANDOM.nextInt(3); // 0, 1, 2 = 3 typy otazok

            switch (typOtazky) {
                case 0:
                    // Vyberova otazka
                    JSONArray nespravneOdpovede = item.getJSONArray("incorrect_answers");
                    List<String> vsetkyMoznosti = new ArrayList<>();
                    vsetkyMoznosti.add(spravnaOdpoved);

                    for (int j = 0; j < nespravneOdpovede.length(); j++) {
                        vsetkyMoznosti.add(decodeHtml(nespravneOdpovede.getString(j)));
                    }

                    otazky.add(new VyberovaOtazka(text, vsetkyMoznosti, spravnaOdpoved));
                    break;

                case 1:
                    // Vpisovacia otazka
                    VpisovaciaOtazka vpisovaciaOtazka = new VpisovaciaOtazka(text, spravnaOdpoved);

                    // Niekedy pouzijeme strategiu "obsahuje" namiesto presnej zhody
                    if (RANDOM.nextBoolean()) {
                        vpisovaciaOtazka.setStrategia(new ObsahujeStrategia());
                    }

                    otazky.add(vpisovaciaOtazka);
                    break;

                case 2:
                    // Parovacia otazka
                    // Pre parovaciu otazku potrebujeme viac otazok, vytvorime pary
                    Map<String, String> pary = new HashMap<>();

                    // Pridame par z aktualnej otazky
                    pary.put(text, spravnaOdpoved);

                    // Pridame pary z ostatnych otazok v results
                    int countPairs = Math.min(3, results.length() - 1);
                    List<Integer> usedIndexes = new ArrayList<>();
                    usedIndexes.add(i); // Aktualna otazka uz bola pouzita

                    while (pary.size() < 4 && usedIndexes.size() < results.length()) {
                        int randomIndex = RANDOM.nextInt(results.length());
                        if (!usedIndexes.contains(randomIndex)) {
                            usedIndexes.add(randomIndex);
                            JSONObject pairItem = results.getJSONObject(randomIndex);
                            String pairQuestion = decodeHtml(pairItem.getString("question"));
                            String pairAnswer = decodeHtml(pairItem.getString("correct_answer"));

                            // Skratime otazku ak je prilis dlha
                            if (pairQuestion.length() > 50) {
                                pairQuestion = pairQuestion.substring(0, 47) + "...";
                            }

                            pary.put(pairQuestion, pairAnswer);
                        }
                    }

                    // Ak nemame dostatok parov, pridame niekolko umelych
                    String[] dummyQuestions = {
                            "Hlavné mesto Slovenska",
                            "Hlavné mesto Česka",
                            "Hlavné mesto Francúzska"
                    };

                    String[] dummyAnswers = {
                            "Bratislava",
                            "Praha",
                            "Paríž"
                    };

                    for (int j = 0; j < dummyQuestions.length && pary.size() < 4; j++) {
                        pary.put(dummyQuestions[j], dummyAnswers[j]);
                    }

                    otazky.add(new ParovaciaOtazka("Spárujte správne dvojice:", pary));
                    break;
            }
        }

        return otazky;
    }

    /**
     * Nacita demo otazky (pre pripad, ze API nie je dostupne)
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

        // Pre jednu otazku pouzijeme inu strategiu na ukazku polymorfizmu
        otazka2.setStrategia(new ObsahujeStrategia());

        otazky.add(otazka1);
        otazky.add(otazka2);

        // Demo parovacia otazka
        Map<String, String> pary1 = new HashMap<>();
        pary1.put("Hlavné mesto Slovenska", "Bratislava");
        pary1.put("Hlavné mesto Česka", "Praha");
        pary1.put("Hlavné mesto Francúzska", "Paríž");
        pary1.put("Hlavné mesto Nemecka", "Berlín");

        otazky.add(new ParovaciaOtazka("Spárujte hlavné mestá s krajinami:", pary1));

        Map<String, String> pary2 = new HashMap<>();
        pary2.put("Pes", "Dog");
        pary2.put("Mačka", "Cat");
        pary2.put("Kôň", "Horse");
        pary2.put("Myš", "Mouse");

        otazky.add(new ParovaciaOtazka("Spárujte slovenské a anglické názvy zvierat:", pary2));

        return otazky;
    }

    /**
     * Ziska data z API
     * @return JSON data z API
     * @throws IOException ak nastane chyba pri komunikacii s API
     */
    private static String getDataFromApi() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            connection.disconnect();
        }

        return response.toString();
    }

    /**
     * Dekoduje HTML entity v texte (napr. &quot; na ")
     * @param html Text s HTML entitami
     * @return Dekodovany text
     */
    private static String decodeHtml(String html) {
        return html
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&#039;", "'");
    }
}