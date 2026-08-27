// Imports the List type for storing universities.
import java.util.List;

// Tests the CSV reader.
public class TestReader{
    // Starts the test program.
    public static void main(String[] args) {
        // Creates the CSV reader.
        CsvReader reader = new CsvReader();
        // Loads the file into an ArrayList.
        List<University> arrayList = reader.loadAsArrayList("World University Rankings 2023-Cleaned.csv");
        // Prints the number of ArrayList records.
        System.out.println("ArrayList total: " + arrayList.size());
        // Prints the first ArrayList record.
        System.out.println(arrayList.get(0));

        // Loads the file into a LinkedList.
        List<University> linkedList = reader.loadAsLinkedList("World University Rankings 2023-Cleaned.csv");
        // Prints the number of LinkedList records.
        System.out.println("LinkedList total: " + linkedList.size());
        // Prints the first LinkedList record.
        System.out.println(linkedList.get(0));
    }
}