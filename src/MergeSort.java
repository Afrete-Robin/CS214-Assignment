import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MergeSort {
    private MergeSort() {
    }

    public static <T> void sortArrayList(List<T> list, Comparator<T> comparator,
                                         SortAlgorithms.SortCounter counter) {
        sortArrayList(list, comparator, counter, null);
    }

    public static <T> void sortArrayList(List<T> list, Comparator<T> comparator,
                                         SortAlgorithms.SortCounter counter,
                                         java.util.function.IntConsumer progress) {
        List<T> working = new ArrayList<>(list);
        ProgressState state = new ProgressState(list.size(), progress);
        sort(working, comparator, counter, state);
        for (int i = 0; i < working.size(); i++) {
            list.set(i, working.get(i));
            if (counter != null) counter.swap();
        }
        if (progress != null) progress.accept(100);
    }

    public static <T> void sortLinkedList(List<T> list, Comparator<T> comparator,
                                          SortAlgorithms.SortCounter counter) {
        sortLinkedList(list, comparator, counter, null);
    }

    public static <T> void sortLinkedList(List<T> list, Comparator<T> comparator,
                                          SortAlgorithms.SortCounter counter,
                                          java.util.function.IntConsumer progress) {
        List<T> working = new ArrayList<>(list);
        ProgressState state = new ProgressState(list.size(), progress);
        sort(working, comparator, counter, state);
        list.clear();
        list.addAll(working);
        if (counter != null) {
            for (int i = 0; i < working.size(); i++) counter.swap();
        }
        if (progress != null) progress.accept(100);
    }

    private static <T> void sort(List<T> list, Comparator<T> comparator,
                                 SortAlgorithms.SortCounter counter, ProgressState state) {
        if (list.size() < 2) return;
        int middle = list.size() / 2;
        List<T> left = new ArrayList<>(list.subList(0, middle));
        List<T> right = new ArrayList<>(list.subList(middle, list.size()));
        sort(left, comparator, counter, state);
        sort(right, comparator, counter, state);

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

    private static class ProgressState {
        private final int totalWrites;
        private final java.util.function.IntConsumer progress;
        private int writes;

        ProgressState(int size, java.util.function.IntConsumer progress) {
            int levels = 32 - Integer.numberOfLeadingZeros(Math.max(1, size));
            totalWrites = Math.max(1, size * levels);
            this.progress = progress;
        }

        void reportWrite() {
            if (progress != null) {
                writes++;
                progress.accept(Math.min(99, (writes * 100) / totalWrites));
            }
        }
    }
}