
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestSortGeneric {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);
        numbers.add(3);
        numbers.add(8);
        numbers.add(1);

        System.out.println("Before" + numbers);

        Comparator<Integer> byValue = (a,b) -> Integer.compare(a, b);
        SortAlgorithms sorter = new SortAlgorithms();
        sorter.insertionSort(numbers, byValue);

        System.out.println("After" + numbers);
    }
}
