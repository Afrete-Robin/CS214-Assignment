// Comparator provides the sorting rule.
import java.util.Comparator;
// Collections contains Java's built-in sorting method.
import java.util.Collections;
// ArrayList stores temporary merge-sort data.
import java.util.ArrayList;
// List allows the algorithms to work with different list types.
import java.util.List;

public class SortAlgorithms {
    // Counts comparisons and data movements during a sort.
    public static class SortCounter {
        // Number of value comparisons.
        private long comparisons;
        // Number of swaps or writes.
        private long swaps;

        // Adds one comparison.
        public void comparison() { comparisons++; }
        // Adds one swap or write.
        public void swap() { swaps++; }
        // Returns the comparison count.
        public long getComparisons() { return comparisons; }
        // Returns the swap count.
        public long getSwaps() { return swaps; }
        // Returns both counts together.
        public long getOperations() { return comparisons + swaps; }
    }

    // Sorts a list using insertion sort.
    public <T> void insertionSort(List<T> list, Comparator<T> comparator){
        insertionSort(list, comparator, (SortCounter) null);
    }

    // Insertion sort version that records its work.
    public <T> void insertionSort(List<T> list, Comparator<T> comparator, SortCounter counter){
        insertionSortInternal(list, comparator, counter, null);
    }

    // Insertion sort version that reports progress.
    public <T> void insertionSort(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        insertionSortInternal(list, comparator, null, progress);
    }

    // Shared insertion sort code.
    private <T> void insertionSortInternal(List<T> list, Comparator<T> comparator,
                                            SortCounter counter, java.util.function.IntConsumer progress){
        // Number of items in the list.
        int n = list.size();
        for (int i = 1; i < n; i++) {
            // Value currently being inserted.
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0) {
                if (counter != null) counter.comparison();
                if (comparator.compare(list.get(j), key) <= 0) break;
                list.set(j + 1, list.get(j));
                if (counter != null) counter.swap();
                j--;
            }
            list.set(j + 1, key);
            if (counter != null) counter.swap();
            if (progress != null) progress.accept((i * 100) / Math.max(1, n - 1));
        }
        if (progress != null) progress.accept(100);
    }

    // Sorts an ArrayList with bubble sort and reports progress.
    public <T> void bubbleSortArrayList(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        bubbleSortArrayList(list, comparator, progress, null);
    }

    // Sorts a list with bubble sort and records its work.
    public <T> void bubbleSortArrayList(List<T> list, Comparator<T> comparator, SortCounter counter){
        bubbleSortArrayList(list, comparator, null, counter);
    }

    // Shared bubble sort code.
    private <T> void bubbleSortArrayList(List<T> list, Comparator<T> comparator,
                                         java.util.function.IntConsumer progress, SortCounter counter){
        int n = list.size();
        boolean swapped;
        for(int i = 0; i < n-1; i++){
            swapped = false;
            for(int j = 0; j < n-1-i; j++){
                if (counter != null) counter.comparison();
                if(comparator.compare(list.get(j), list.get(j+1)) > 0){
                    T tmp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, tmp);
                    if (counter != null) counter.swap();
                    swapped = true;
                }
            }
            if(progress != null) progress.accept((i * 100) / Math.max(1, n-1));
            if(!swapped) break;
        }
        if(progress != null) progress.accept(100);
    }

    // Sorts a LinkedList with bubble sort and reports progress.
    public <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        bubbleSortLinkedList(list, comparator, progress, null);
    }

    // Sorts a LinkedList with bubble sort and records its work.
    public <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator, SortCounter counter){
        bubbleSortLinkedList(list, comparator, null, counter);
    }

    // Shared LinkedList bubble sort code.
    private <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator,
                                          java.util.function.IntConsumer progress, SortCounter counter){
        bubbleSortArrayList(list, comparator, progress, counter);
    }

    // Sorts an ArrayList with merge sort.
    public <T> void mergeSortArrayList(List<T> list, Comparator<T> comparator, SortCounter counter){
        mergeSort(list, comparator, counter, null, false);
    }

    // Merge sort version that reports progress.
    public <T> void mergeSortArrayList(List<T> list, Comparator<T> comparator,
                                       java.util.function.IntConsumer progress){
        mergeSort(list, comparator, null, progress, false);
    }

    // Sorts a LinkedList with merge sort.
    public <T> void mergeSortLinkedList(List<T> list, Comparator<T> comparator, SortCounter counter){
        mergeSort(list, comparator, counter, null, true);
    }

    // Merge sort version for a LinkedList with progress updates.
    public <T> void mergeSortLinkedList(List<T> list, Comparator<T> comparator,
                                        java.util.function.IntConsumer progress){
        mergeSort(list, comparator, null, progress, true);
    }

    // Starts merge sort with the chosen list type.
    private <T> void mergeSort(List<T> list, Comparator<T> comparator,
                               SortCounter counter, java.util.function.IntConsumer progress,
                               boolean linkedListPath) {
        ProgressState state = new ProgressState(list.size(), progress);
        mergeSortInternal(list, comparator, counter, state, linkedListPath);
        if (progress != null) progress.accept(100);
    }

    // Recursively splits and joins the list.
    private <T> void mergeSortInternal(List<T> list, Comparator<T> comparator,
                                       SortCounter counter, ProgressState state,
                                       boolean linkedListPath) {
        if (list.size() < 2) return;
        // Middle position used to split the list.
        int middle = list.size() / 2;
        List<T> left = linkedListPath
                ? new java.util.LinkedList<>(list.subList(0, middle))
                : new ArrayList<>(list.subList(0, middle));
        List<T> right = linkedListPath
                ? new java.util.LinkedList<>(list.subList(middle, list.size()))
                : new ArrayList<>(list.subList(middle, list.size()));
        mergeSortInternal(left, comparator, counter, state, linkedListPath);
        mergeSortInternal(right, comparator, counter, state, linkedListPath);

        // Positions of the next values in each half.
        int leftIndex = 0;
        int rightIndex = 0;
        int outputIndex = 0;
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (counter != null) counter.comparison();
            if (comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) {
                list.set(outputIndex++, left.get(leftIndex++));
            } else {
                list.set(outputIndex++, right.get(rightIndex++));
            }
            if (counter != null) counter.swap();
            state.reportWrite();
        }
        while (leftIndex < left.size()) {
            list.set(outputIndex++, left.get(leftIndex++));
            if (counter != null) counter.swap();
            state.reportWrite();
        }
        while (rightIndex < right.size()) {
            list.set(outputIndex++, right.get(rightIndex++));
            if (counter != null) counter.swap();
            state.reportWrite();
        }
    }

    // Tracks progress during merge sort.
    private static class ProgressState {
        // Total estimated writes in the sort.
        private final int totalWrites;
        // Function used to send progress updates.
        private final java.util.function.IntConsumer progress;
        // Writes completed so far.
        private int writes;

        // Creates a progress tracker for one sort.
        ProgressState(int size, java.util.function.IntConsumer progress) {
            int levels = 32 - Integer.numberOfLeadingZeros(Math.max(1, size));
            totalWrites = Math.max(1, size * levels);
            this.progress = progress;
        }

        // Sends the next progress update.
        void reportWrite() {
            if (progress != null) {
                writes++;
                progress.accept(Math.min(99, (writes * 100) / totalWrites));
            }
        }
    }

    // Uses Java's built-in sort and records comparisons.
    public <T> void builtInSort(List<T> list, Comparator<T> comparator, SortCounter counter){
        Collections.sort(list, (left, right) -> {
            if (counter != null) counter.comparison();
            return comparator.compare(left, right);
        });
    }

    // Uses Java's built-in sort and reports progress.
    public <T> void builtInSort(List<T> list, Comparator<T> comparator,
                                java.util.function.IntConsumer progress){
        if (progress != null) progress.accept(0);
        Collections.sort(list, comparator);
        if (progress != null) progress.accept(100);
    }
}
