// Provides the shuffle method.
import java.util.Collections;
// Defines how universities are compared.
import java.util.Comparator;
// Stores the university records.
import java.util.List;

// Tests insertion sort with university names.
public class TestSort {
    // Starts the test program.
    public static void main(String[] args) {
        // Creates the CSV reader.
        CsvReader reader = new CsvReader();
        // Loads the records into a LinkedList.
        List<University> linkedlist = reader.loadAsLinkedList("World University Rankings 2023-Cleaned.csv");

        // Gives the list a random starting order.
        Collections.shuffle(linkedlist);
        // Marks the start of the unsorted output.
        System.out.println("Before sorting: ");
        // Prints the first five records.
        printlist(linkedlist);

        // Compares universities by name.
        Comparator<University> byName = (a,b) -> a.getName().compareTo(b.getName());
        // Creates the sorting object.
        SortAlgorithms sorter = new SortAlgorithms();

        // Sorts the LinkedList by university name.
        sorter.insertionSort(linkedlist, byName);
        // Marks the start of the sorted output.
        System.out.println("After sorting: ");
        // Prints the first five sorted records.
        printlist(linkedlist);
    }

    // Prints the first five records in a list.
    private static void printlist(List<University> list){
    // Repeats the print code five times.
    for(int i = 0; i<5; i++){
        // Gets the record at the current position.
        University u = list.get(i);
           // Prints the main values of the university.
           System.out.println(
            // Prints the university rank.
            "Rank: " + u.getRank() +
            // Prints the university name.
            ", Name: " + u.getName() +
            // Prints the number of students.
            ", Students: " + u.getNumStudents() +
            // Prints the overall score.
            ", OverallScore: " + u.getOverallScore() +
            // Prints the university location.
            ", Locations: " + u.getLocation()
           );
        }
    }
    
}
