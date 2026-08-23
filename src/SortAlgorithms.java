import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SortAlgorithms {
    public static class SortCounter {
        private long comparisons;
        private long swaps;

        public void comparison() { comparisons++; }
        public void swap() { swaps++; }
        public long getComparisons() { return comparisons; }
        public long getSwaps() { return swaps; }
        public long getOperations() { return comparisons + swaps; }
    }

    public <T> void insertionSort(List<T> list, Comparator<T> comparator){
        insertionSort(list, comparator, (SortCounter) null);
    }

    public <T> void insertionSort(List<T> list, Comparator<T> comparator, SortCounter counter){
        insertionSortInternal(list, comparator, counter, null);
    }

    // Overload with progress callback (reports 0..100)
    public <T> void insertionSort(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        insertionSortInternal(list, comparator, null, progress);
    }

    private <T> void insertionSortInternal(List<T> list, Comparator<T> comparator,
                                            SortCounter counter, java.util.function.IntConsumer progress){
        if (!(list instanceof java.util.RandomAccess)) {
            List<T> working = new ArrayList<>(list);
            insertionSortInternal(working, comparator, counter, progress);
            ListIterator<T> destination = list.listIterator();
            for (T value : working) {
                destination.next();
                destination.set(value);
            }
            return;
        }
        int n = list.size();
        for (int i = 1; i < n; i++) {
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

    // Bubble sort optimized for RandomAccess lists (ArrayList)
    public <T> void bubbleSortArrayList(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        bubbleSortArrayList(list, comparator, progress, null);
    }

    public <T> void bubbleSortArrayList(List<T> list, Comparator<T> comparator, SortCounter counter){
        bubbleSortArrayList(list, comparator, null, counter);
    }

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

    // Bubble sort for LinkedList: copy to array, bubble, copy back (keeps simple semantics)
    public <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator, java.util.function.IntConsumer progress){
        bubbleSortLinkedList(list, comparator, progress, null);
    }

    public <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator, SortCounter counter){
        bubbleSortLinkedList(list, comparator, null, counter);
    }

    private <T> void bubbleSortLinkedList(List<T> list, Comparator<T> comparator,
                                          java.util.function.IntConsumer progress, SortCounter counter){
        java.util.ArrayList<T> temp = new java.util.ArrayList<>(list);
        bubbleSortArrayList(temp, comparator, progress, counter);
        list.clear();
        list.addAll(temp);
    }

    public <T> void mergeSortArrayList(List<T> list, Comparator<T> comparator, SortCounter counter){
        MergeSort.sortArrayList(list, comparator, counter);
    }

    public <T> void mergeSortArrayList(List<T> list, Comparator<T> comparator,
                                       java.util.function.IntConsumer progress){
        MergeSort.sortArrayList(list, comparator, null, progress);
    }

    public <T> void mergeSortLinkedList(List<T> list, Comparator<T> comparator, SortCounter counter){
        MergeSort.sortLinkedList(list, comparator, counter);
    }

    public <T> void mergeSortLinkedList(List<T> list, Comparator<T> comparator,
                                        java.util.function.IntConsumer progress){
        MergeSort.sortLinkedList(list, comparator, null, progress);
    }

    public <T> void builtInSort(List<T> list, Comparator<T> comparator, SortCounter counter){
        Collections.sort(list, (left, right) -> {
            if (counter != null) counter.comparison();
            return comparator.compare(left, right);
        });
    }

    public <T> void builtInSort(List<T> list, Comparator<T> comparator,
                                java.util.function.IntConsumer progress){
        if (progress != null) progress.accept(0);
        Collections.sort(list, comparator);
        if (progress != null) progress.accept(100);
    }
}
