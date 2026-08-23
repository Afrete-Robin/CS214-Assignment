import java.util.List;
public class TestReader{
    public static void main(String[] args) {
        CsvReader reader = new CsvReader();
        List<University> universities = reader.loadAsArrayList("World University Rankings 2023-Cleaned.csv");

        List<University> arrayList = reader.loadAsArrayList("World University Rankings 2023-Cleaned.csv");
        System.out.println("ArrayList total: " + arrayList.size());
        System.out.println(arrayList.get(0));
        
        List<University> linkedList = reader.loadAsLinkedList("World University Rankings 2023-Cleaned.csv");
        System.out.println("LinkedList total: " + linkedList.size());
        System.out.println(linkedList.get(0));
    }
}