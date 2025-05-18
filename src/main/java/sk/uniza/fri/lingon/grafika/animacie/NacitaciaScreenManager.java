package sk.uniza.fri.lingon.grafika.animacie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Manager pre nacitaciu obrazovku, ktory riadi process nacitania dat
 */
public class NacitaciaScreenManager {
    private final NacitaciaObrazovka obrazovka;
    private final Runnable loadingTask;
    private final Runnable onLoadingComplete;
    private final ExecutorService executorService;
    private boolean loadingStarted = false;

    /**
     * Vytvori novy manager pre nacitaciu obrazovku
     * @param obrazovka Nacitacia obrazovka
     * @param loadingTask Uloha, ktora sa ma vykonat na pozadi
     * @param onComplete Callback po dokonceni nacitania
     */
    public NacitaciaScreenManager(NacitaciaObrazovka obrazovka, Runnable loadingTask, Runnable onComplete) {
        this.obrazovka = obrazovka;
        this.loadingTask = loadingTask;
        this.onLoadingComplete = onComplete;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Spusti nacitavanie dat na pozadi
     */
    public void startLoading() {
        if (this.loadingStarted) {
            return; // Zabranime viacnasobnemu spusteniu
        }

        this.loadingStarted = true;

        // Spustenie nacitavania dat na pozadi
        this.executorService.submit(() -> {
            try {
                this.loadingTask.run();

                // Po dokonceni spustime callback v EDT
                SwingUtilities.invokeLater(() -> {
                    this.obrazovka.stopAnimations();
                    this.obrazovka.updateProgress(100, "Načítavanie dokončené!");

                    // Kratke oneskorenie pred prepnutim obrazovky
                    Timer delayTimer = new Timer(1000, _ -> {
                        if (this.onLoadingComplete != null) {
                            this.onLoadingComplete.run();
                        }
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                });
            } catch (Exception e) {
                // V pripade chyby zobrazime chybu
                SwingUtilities.invokeLater(() -> {
                    this.obrazovka.stopAnimations();
                    this.obrazovka.handleError(e.getMessage(), this.loadingTask, this.onLoadingComplete);
                });
            }
        });
    }

}