import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EmpiricalTestHarness {
    private static final int RUNS = 30;

    private enum Algorithm {
        INSERTION_ARRAY("Insertion Sort", "ArrayList"),
        INSERTION_LINKED("Insertion Sort", "LinkedList"),
        BUBBLE_ARRAY("Bubble Sort", "ArrayList"),
        BUBBLE_LINKED("Bubble Sort", "LinkedList"),
        MERGE_ARRAY("Merge Sort", "ArrayList"),
        MERGE_LINKED("Merge Sort", "LinkedList"),
        BUILT_IN_ARRAY("Built-in Sort", "ArrayList");

        private final String name;
        private final String structure;

        Algorithm(String name, String structure) {
            this.name = name;
            this.structure = structure;
        }

        @Override
        public String toString() {
            return name + " (" + structure + ")";
        }
    }

    private static class Result {
        private final Algorithm algorithm;
        private final List<Long> comparisons = new ArrayList<>();
        private final List<Long> swaps = new ArrayList<>();
        private final List<Long> times = new ArrayList<>();

        Result(Algorithm algorithm) {
            this.algorithm = algorithm;
        }

        void add(SortAlgorithms.SortCounter counter, long timeNs) {
            comparisons.add(counter.getComparisons());
            swaps.add(counter.getSwaps());
            times.add(timeNs / 1_000_000);
        }

        void print() {
            System.out.printf("%-30s %12s %12s %12s %12s %12s %12s%n",
                    algorithm, format(comparisons), format(swaps),
                    format(times), median(comparisons), median(swaps), median(times));
        }

        long mean(List<Long> values) {
            long total = 0;
            for (long value : values) total += value;
            return total / values.size();
        }

        long median(List<Long> values) {
            List<Long> sorted = new ArrayList<>(values);
            Collections.sort(sorted);
            return sorted.get(sorted.size() / 2);
        }

        long best(List<Long> values) {
            return Collections.min(values);
        }

        long worst(List<Long> values) {
            return Collections.max(values);
        }

        String format(List<Long> values) {
            return best(values) + "/" + mean(values) + "/" + median(values) + "/" + worst(values);
        }

        long meanTime() {
            return mean(times);
        }
    }

    public void run(List<University> source) {
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        SortAlgorithms sorter = new SortAlgorithms();
        List<Result> results = new ArrayList<>();

        for (Algorithm algorithm : Algorithm.values()) {
            Result result = new Result(algorithm);
            for (int run = 0; run < RUNS; run++) {
                List<University> data = copyFor(algorithm, source);
                Collections.shuffle(data);
                SortAlgorithms.SortCounter counter = new SortAlgorithms.SortCounter();
                long start = System.nanoTime();
                sort(algorithm, data, byRank, sorter, counter);
                long elapsed = System.nanoTime() - start;
                if (!isSorted(data, byRank)) {
                    throw new IllegalStateException(algorithm + " failed on run " + (run + 1));
                }
                result.add(counter, elapsed);
            }
            results.add(result);
        }

        System.out.println("\nEmpirical results (30 randomized runs; values are best/mean/median/worst)");
        System.out.printf("%-30s %12s %12s %12s %12s %12s %12s%n",
                " Algorithm ", " Comparisons ", " Swaps ", " Time (ms) ",
                " Cmp median ", " Swap median ", " Time median ");
        for (Result result : results) result.print();

        Result fastest = results.get(0);
        for (Result result : results) {
            if (result.meanTime() < fastest.meanTime()) fastest = result;
        }
        System.out.println("\nFastest average measured time: " + fastest.algorithm
                + " (" + fastest.meanTime() + " ms).");
        System.out.println("Merge sort stays near O(n log n), while insertion and bubble sort "
            + "are O(n^2) on randomized input. ArrayList avoids the indexed-access cost "
            + "that makes insertion sort especially expensive on LinkedList.");
    }

    private List<University> copyFor(Algorithm algorithm, List<University> source) {
        if (algorithm.structure.equals("LinkedList")) return new LinkedList<>(source);
        return new ArrayList<>(source);
    }

    private void sort(Algorithm algorithm, List<University> data, Comparator<University> comparator,
                      SortAlgorithms sorter, SortAlgorithms.SortCounter counter) {
        switch (algorithm) {
            case INSERTION_ARRAY, INSERTION_LINKED -> sorter.insertionSort(data, comparator, counter);
            case BUBBLE_ARRAY -> sorter.bubbleSortArrayList(data, comparator, counter);
            case BUBBLE_LINKED -> sorter.bubbleSortLinkedList(data, comparator, counter);
            case MERGE_ARRAY -> sorter.mergeSortArrayList(data, comparator, counter);
            case MERGE_LINKED -> sorter.mergeSortLinkedList(data, comparator, counter);
            case BUILT_IN_ARRAY -> sorter.builtInSort(data, comparator, counter);
        }
    }

    private <T> boolean isSorted(List<T> list, Comparator<T> comparator) {
        for (int i = 1; i < list.size(); i++) {
            if (comparator.compare(list.get(i - 1), list.get(i)) > 0) return false;
        }
        return true;
    }
}