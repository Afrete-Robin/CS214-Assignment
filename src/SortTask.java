import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SortTask implements Runnable {
    public enum Algorithm {
        INSERTION_ARRAY, INSERTION_LINKED,
        BUBBLE_ARRAY, BUBBLE_LINKED,
        MERGE_ARRAY, MERGE_LINKED,
        BUILT_IN_ARRAY
    }
    private static final long RACE_STEP_DELAY_MS = 12;

    private final String name;
    private final List<University> dataCopy;
    private final java.util.Comparator<University> comparator;
    private final SortAlgorithms algorithms;
    private final JProgressBar progressBar;
    private final Algorithm algorithm;
    private long durationMs;

    public SortTask(String name, List<University> dataCopy, java.util.Comparator<University> comparator,
                    SortAlgorithms algorithms, JProgressBar progressBar, Algorithm algorithm) {
        this.name = name;
        this.dataCopy = dataCopy;
        this.comparator = comparator;
        this.algorithms = algorithms;
        this.progressBar = progressBar;
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        long t0 = System.nanoTime();
        AtomicInteger lastProgress = new AtomicInteger(-1);
        AtomicLong visualDelayNanos = new AtomicLong();
        java.util.function.IntConsumer reporter = p -> {
            int previous = lastProgress.get();
            if (p <= previous || !lastProgress.compareAndSet(previous, p)) return;
            SwingUtilities.invokeLater(() -> progressBar.setValue(p));
            long delayStart = System.nanoTime();
            try {
                Thread.sleep(RACE_STEP_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            visualDelayNanos.addAndGet(System.nanoTime() - delayStart);
        };
        try {
            switch (algorithm) {
                case INSERTION_ARRAY:
                case INSERTION_LINKED:
                    algorithms.insertionSort(dataCopy, comparator, reporter);
                    break;
                case BUBBLE_ARRAY:
                    algorithms.bubbleSortArrayList(dataCopy, comparator, reporter);
                    break;
                case BUBBLE_LINKED:
                    algorithms.bubbleSortLinkedList(dataCopy, comparator, reporter);
                    break;
                case MERGE_ARRAY:
                    algorithms.mergeSortArrayList(dataCopy, comparator, reporter);
                    break;
                case MERGE_LINKED:
                    algorithms.mergeSortLinkedList(dataCopy, comparator, reporter);
                    break;
                case BUILT_IN_ARRAY:
                    algorithms.builtInSort(dataCopy, comparator, reporter);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long t1 = System.nanoTime();
            durationMs = Math.max(0, (t1 - t0 - visualDelayNanos.get()) / 1_000_000);
            SwingUtilities.invokeLater(() -> progressBar.setString(name + " - " + durationMs + " ms"));
            SwingUtilities.invokeLater(() -> progressBar.setValue(100));
        }
    }

    public long getDurationMs() { return durationMs; }
    public String getName() { return name; }
}
