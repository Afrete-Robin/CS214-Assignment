
// Provides the resizable list used in the test.
import java.util.ArrayList;
// Provides sorting comparison rules.
import java.util.Comparator;
// Allows the code to use the List type.
import java.util.List;

// Tests the generic sorting method with integers.
public class TestSortGeneric {
    // Starts the test program.
    public static void main(String[] args) {
        // Creates a list of integers.
        List<Integer> numbers = new ArrayList<>();
        // Adds the first number.
        numbers.add(5);
        // Adds the second number.
        numbers.add(3);
        // Adds the third number.
        numbers.add(8);
        // Adds the fourth number.
        numbers.add(1);

        // Prints the list before sorting.
        System.out.println("Before" + numbers);

        // Creates the sorting object.
        SortAlgorithms sorter = new SortAlgorithms();
        // Sorts the numbers from smallest to largest.
        sorter.insertionSort(numbers, Comparator.naturalOrder());

        // Prints the list after sorting.
        System.out.println("After" + numbers);
    }
}
