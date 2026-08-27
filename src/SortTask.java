// Swing updates the progress bar.
import javax.swing.*;
// List stores the data being sorted.
import java.util.List;
// Atomic values are safe to update from the sorting thread.
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SortTask implements Runnable {
    // Names the sorting method used by a task.
    public enum Algorithm {
        INSERTION_ARRAY, INSERTION_LINKED,
        BUBBLE_ARRAY, BUBBLE_LINKED,
        MERGE_ARRAY, MERGE_LINKED,
        BUILT_IN_ARRAY
    }
    // Small delay that makes progress visible in the window.
    private static final long RACE_STEP_DELAY_MS = 12;

    // Name shown for this runner.
    private final String name;
    // Data copied for this runner.
    private final List<University> dataCopy;
    // Rule used to compare universities.
    private final java.util.Comparator<University> comparator;
    // Object that performs the sort.
    private final SortAlgorithms algorithms;
    // Progress bar for this runner.
    private final JProgressBar progressBar;
    // Sort selected for this runner.
    private final Algorithm algorithm;
    // Shared progress tracker.
    private final SortRaceVisualizer.ProgressTracker progressTracker;
    // Time taken by the sort.
    private long durationMs;

    // Creates a task without a shared progress tracker.
    public SortTask(String name, List<University> dataCopy, java.util.Comparator<University> comparator,
                    SortAlgorithms algorithms, JProgressBar progressBar, Algorithm algorithm) {
        this(name, dataCopy, comparator, algorithms, progressBar, algorithm, null);
    }

    // Creates a task with all sorting and display settings.
    public SortTask(String name, List<University> dataCopy, java.util.Comparator<University> comparator,
                    SortAlgorithms algorithms, JProgressBar progressBar, Algorithm algorithm,
                    SortRaceVisualizer.ProgressTracker progressTracker) {
        this.name = name;
        this.dataCopy = dataCopy;
        this.comparator = comparator;
        this.algorithms = algorithms;
        this.progressBar = progressBar;
        this.algorithm = algorithm;
        this.progressTracker = progressTracker;
    }

    // Sorts the data and updates the progress bar.
    @Override
    public void run() {
        // Start time before sorting begins.
        long t0 = System.nanoTime();
        // Prevents repeated progress values.
        AtomicInteger lastProgress = new AtomicInteger(-1);
        // Removes the display delay from the measured time.
        AtomicLong visualDelayNanos = new AtomicLong();
        // Sends progress updates to the visualizer.
        java.util.function.IntConsumer reporter = p -> {
            int previous = lastProgress.get();
            if (p <= previous || !lastProgress.compareAndSet(previous, p)) return;
            if (progressTracker != null) {
                progressTracker.update(name, p, progressBar);
            } else {
                SwingUtilities.invokeLater(() -> progressBar.setValue(p));
            }
            // Measures the artificial delay used for the animation.
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
            // End time after the sort finishes.
            long t1 = System.nanoTime();
            durationMs = Math.max(0, (t1 - t0 - visualDelayNanos.get()) / 1_000_000);
            SwingUtilities.invokeLater(() -> progressBar.setString(name + " - " + durationMs + " ms"));
            SwingUtilities.invokeLater(() -> progressBar.setValue(100));
        }
    }

}
