import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestSort {
    public static void main(String[] args) {
        CsvReader reader = new CsvReader();
        List<University> arraylist = reader.loadAsArrayList("World University Rankings 2023-Cleaned.csv");
        List<University> linkedlist = reader.loadAsLinkedList("World University Rankings 2023-Cleaned.csv");
        
        Collections.shuffle(linkedlist);
        System.out.println("Before sorting: ");
        printlist(linkedlist);

        Comparator<University> byRank = (a, b) -> Integer.compare(a.getRank(),b.getRank());
        Comparator<University> byName = (a,b) -> a.getName().compareTo(b.getName());
        SortAlgorithms sorter = new SortAlgorithms();

        sorter.insertionSort(linkedlist, byName);
        System.out.println("After sorting: ");
        printlist(linkedlist);
    }

    private static void printlist(List<University> list){
    for(int i = 0; i<5; i++){
        University u = list.get(i);
           System.out.println(
            "Rank: " + u.getRank() +
            ", Name: " + u.getName() +
            ", Students: " + u.getNumStudents() +
            ", OverallScore: " + u.getOverallScore() +
            ", Locations: " + u.getLocation()
           );
        }
    }
    
}
