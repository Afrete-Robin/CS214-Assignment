import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javax.swing.JProgressBar;

/**
 * Console menu driver for the CS214 Assignment 1 sorting project.
 * Ties together each member's work behind one entry point so the whole
 * assignment can be demoed without switching between files.
 *
 * Provides CSV loading, sorting demonstrations, the empirical harness, and
 * the Swing race visualizer for the complete assignment.
 */
public class Main {

    private static final String CSV_FILE = "World University Rankings 2023-Cleaned.csv";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readChoice();
            switch (choice) {
                case 1 -> printAllRecords();
                case 2 -> readIntoBothLists();
                case 3 -> runInsertionSortDemo();
                case 4 -> runGenericSortProof();
                case 5 -> runBubbleSortDemo();
                case 6 -> runMergeAndBuiltInDemo();
                case 7 -> runEmpiricalTest();
                case 8 -> runRaceVisualizer();
                case 0 -> {
                    running = false;
                    System.out.println("Goodbye.");
                }
                default -> System.out.println("Invalid option, try again.\n");
            }
        }
        scanner.close();
    }

    // Helper: check entire list is sorted according to comparator using iterator
    private static <T> boolean isSorted(List<T> list, java.util.Comparator<T> comparator) {
        java.util.Iterator<T> it = list.iterator();
        if (!it.hasNext()) return true;
        T prev = it.next();
        while (it.hasNext()) {
            T cur = it.next();
            if (comparator.compare(prev, cur) > 0) return false;
            prev = cur;
        }
        return true;
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" CS214 Assignment 1 - Sorting Algorithms");
        System.out.println("=========================================");
        System.out.println(" 1. Print ALL records from CSV (neat output)");
        System.out.println(" 2. Load dataset into ArrayList + LinkedList (print counts)");
        System.out.println(" 3. Run Insertion Sort demo ");
        System.out.println(" 4. Prove generic sort works on List<Integer>");
        System.out.println(" 5. Run Bubble Sort demo");
        System.out.println(" 6. Run Merge Sort / built-in sort demo");
        System.out.println(" 7. Run full empirical test harness (30 runs)");
        System.out.println(" 8. Run sorting race visualizer (Swing)");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option: ");
    }

    private static int readChoice() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // falls through to "Invalid option" in the switch
        }
    }

    private static void notYetImplemented(String feature) {
        System.out.println("-> " + feature + " isn't wired in yet.");
        System.out.println("   Drop the relevant class into the project and hook it up here once it's ready.\n");
    }

    // --- Option 1: load + preview ---
    // Option 1: print all records in the CSV in a neat output
    private static void printAllRecords() {
        CsvReader reader = new CsvReader();
        List<University> list = reader.loadAsArrayList(CSV_FILE);
        if (list.isEmpty()) {
            System.out.println("No records found or CSV could not be read.\n");
            return;
        }

        System.out.println("\nPrinting " + list.size() + " records from " + CSV_FILE + ":");

        // Print a neat table: aligned columns with limited name width and formatted numbers
        System.out.printf("%-5s  %-35s  %-15s  %10s  %10s  %6s  %7s%n",
                "Rank", "Name", "Location", "Students", "Stud/Staff", "Intl%", "Overall");
        for (int i = 0; i < 110; i++) System.out.print('-');
        System.out.println();

        for (University u : list) {
            String name = u.getName() == null ? "" : u.getName();
            if (name.length() > 35) name = name.substring(0, 32) + "...";
            String loc = u.getLocation() == null ? "" : u.getLocation();

            System.out.printf("%-5d  %-35s  %-15s  %10d  %10.1f  %6.1f  %7.2f%n",
                    u.getRank(), name, loc, u.getNumStudents(),
                    u.getStudentPerStaff(), u.getInternationalStudentPercent(), u.getOverallScore());
        }
        System.out.println();
    }

    // Option 2: read into both an ArrayList and a LinkedList and print counts
    private static void readIntoBothLists() {
        CsvReader reader = new CsvReader();
        List<University> arrayList = reader.loadAsArrayList(CSV_FILE);
        List<University> linkedList = reader.loadAsLinkedList(CSV_FILE);

        System.out.println("\nArrayList loaded: " + arrayList.size() + " rows");
        System.out.println("LinkedList loaded: " + linkedList.size() + " rows\n");
    }

    // --- Option 2: Insertion Sort demo, lets the user pick the sort key ---
    private static void runInsertionSortDemo() {
        // 1) Small sample test to confirm generic insertion sort works
        SortAlgorithms sorter = new SortAlgorithms();
        List<Integer> sample = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        System.out.println("\nRunning small-sample test (before -> after):");
        System.out.println("Before: " + sample);
        sorter.insertionSort(sample, Comparator.naturalOrder());
        System.out.println("After:  " + sample);
        List<Integer> expected = new ArrayList<>(List.of(1,2,3,5,8,9));
        if (sample.equals(expected)) {
            System.out.println("Sample test: PASS\n");
        } else {
            System.out.println("Sample test: FAIL (expected " + expected + ")\n");
        }

        // 2) Full dataset test: load into both ArrayList and LinkedList, sort both, and print first 5
        CsvReader reader = new CsvReader();
        List<University> arrayList = reader.loadAsArrayList(CSV_FILE);
        List<University> linkedList = reader.loadAsLinkedList(CSV_FILE);

        System.out.print("Sort by (1) Rank or (2) Name? ");
        String pick = scanner.nextLine().trim();
        Comparator<University> comparator = pick.equals("2")
            ? Comparator.comparing(University::getName)
            : Comparator.comparingInt(University::getRank);

        // Shuffle to ensure we aren't accidentally already sorted
        Collections.shuffle(arrayList);
        Collections.shuffle(linkedList);

        long t1 = System.nanoTime();
        sorter.insertionSort(arrayList, comparator);
        long t2 = System.nanoTime();

        long t3 = System.nanoTime();
        sorter.insertionSort(linkedList, comparator);
        long t4 = System.nanoTime();

        // Print entire sorted ArrayList
        System.out.println("\nAll records after sorting (ArrayList):");
        System.out.printf("%-5s  %-35s  %-15s  %10s  %10s  %6s  %7s%n",
            "Rank", "Name", "Location", "Students", "Stud/Staff", "Intl%", "Overall");
        for (int i = 0; i < 100; i++) System.out.print('-');
        System.out.println();
        for (int i = 0; i < arrayList.size(); i++) {
            University u = arrayList.get(i);
            String name = u.getName() == null ? "" : u.getName();
            if (name.length() > 35) name = name.substring(0, 32) + "...";
            String loc = u.getLocation() == null ? "" : u.getLocation();
            System.out.printf("%-5d  %-35s  %-15s  %10d  %10.1f  %6.1f  %7.2f%n",
                u.getRank(), name, loc, u.getNumStudents(),
                u.getStudentPerStaff(), u.getInternationalStudentPercent(), u.getOverallScore());
        }

        // Print entire sorted LinkedList
        System.out.println("\nAll records after sorting (LinkedList):");
        System.out.printf("%-5s  %-35s  %-15s  %10s  %10s  %6s  %7s%n",
            "Rank", "Name", "Location", "Students", "Stud/Staff", "Intl%", "Overall");
        for (int i = 0; i < 100; i++) System.out.print('-');
        System.out.println();
        for (int i = 0; i < linkedList.size(); i++) {
            University u = linkedList.get(i);
            String name = u.getName() == null ? "" : u.getName();
            if (name.length() > 35) name = name.substring(0, 32) + "...";
            String loc = u.getLocation() == null ? "" : u.getLocation();
            System.out.printf("%-5d  %-35s  %-15s  %10d  %10.1f  %6.1f  %7.2f%n",
                u.getRank(), name, loc, u.getNumStudents(),
                u.getStudentPerStaff(), u.getInternationalStudentPercent(), u.getOverallScore());
        }

        System.out.println();

        // Summary and validation
        System.out.println("\nFull-dataset sort completed:");
        System.out.println("ArrayList: " + arrayList.size() + " rows, sort time: " + ((t2 - t1)/1_000_000) + " ms");
        System.out.println("LinkedList: " + linkedList.size() + " rows, sort time: " + ((t4 - t3)/1_000_000) + " ms\n");
        // Validate entire lists are sorted
        boolean arraySorted = isSorted(arrayList, comparator);
        boolean linkedSorted = isSorted(linkedList, comparator);
        System.out.println("ArrayList fully sorted: " + (arraySorted ? "PASS" : "FAIL"));
        System.out.println("LinkedList fully sorted: " + (linkedSorted ? "PASS" : "FAIL") + "\n");
    }

        private static void runBubbleSortDemo() {
        CsvReader reader = new CsvReader();
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        SortAlgorithms sorter = new SortAlgorithms();
        List<University> arrayList = new ArrayList<>(source);
        List<University> linkedList = new java.util.LinkedList<>(source);
        Collections.shuffle(arrayList);
        Collections.shuffle(linkedList);
        SortAlgorithms.SortCounter arrayCounter = new SortAlgorithms.SortCounter();
        SortAlgorithms.SortCounter linkedCounter = new SortAlgorithms.SortCounter();

        sorter.bubbleSortArrayList(arrayList, byRank, arrayCounter);
        sorter.bubbleSortLinkedList(linkedList, byRank, linkedCounter);
        System.out.println("\nBubble sort complete: " + isSorted(arrayList, byRank)
            + " / " + isSorted(linkedList, byRank));
        System.out.println("ArrayList comparisons/swaps: " + arrayCounter.getComparisons()
            + " / " + arrayCounter.getSwaps());
        System.out.println("LinkedList comparisons/swaps: " + linkedCounter.getComparisons()
            + " / " + linkedCounter.getSwaps() + "\n");
        }

        private static void runMergeAndBuiltInDemo() {
        CsvReader reader = new CsvReader();
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        SortAlgorithms sorter = new SortAlgorithms();
        List<University> mergeArray = new ArrayList<>(source);
        List<University> mergeLinked = new java.util.LinkedList<>(source);
        List<University> builtIn = new ArrayList<>(source);
        Collections.shuffle(mergeArray);
        Collections.shuffle(mergeLinked);
        Collections.shuffle(builtIn);
        SortAlgorithms.SortCounter mergeArrayCounter = new SortAlgorithms.SortCounter();
        SortAlgorithms.SortCounter mergeLinkedCounter = new SortAlgorithms.SortCounter();
        SortAlgorithms.SortCounter builtInCounter = new SortAlgorithms.SortCounter();

        sorter.mergeSortArrayList(mergeArray, byRank, mergeArrayCounter);
        sorter.mergeSortLinkedList(mergeLinked, byRank, mergeLinkedCounter);
        sorter.builtInSort(builtIn, byRank, builtInCounter);
        System.out.println("\nMerge/built-in sort results:");
        System.out.println("Merge ArrayList sorted: " + isSorted(mergeArray, byRank));
        System.out.println("Merge LinkedList sorted: " + isSorted(mergeLinked, byRank));
        System.out.println("Built-in ArrayList sorted: " + isSorted(builtIn, byRank));
        System.out.println("Merge ArrayList comparisons/swaps: " + mergeArrayCounter.getComparisons()
            + " / " + mergeArrayCounter.getSwaps());
        System.out.println("Merge LinkedList comparisons/swaps: " + mergeLinkedCounter.getComparisons()
            + " / " + mergeLinkedCounter.getSwaps());
        System.out.println("Built-in sort comparisons/swaps: " + builtInCounter.getComparisons()
            + " / " + builtInCounter.getSwaps() + "\n");
        }

        private static void runEmpiricalTest() {
        CsvReader reader = new CsvReader();
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        if (source.isEmpty()) {
            System.out.println("CSV empty or not found.");
            return;
        }
        new EmpiricalTestHarness().run(source);
        }

    // Option 8: Build and run a Swing race visualizer using multiple sorting tasks
    private static void runRaceVisualizer() {
        CsvReader reader = new CsvReader();
        List<University> original = reader.loadAsArrayList(CSV_FILE);
        if (original.isEmpty()) {
            System.out.println("CSV empty or not found.");
            return;
        }
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);

        // Create a single shuffled dataset and give each runner its own copy
        List<University> shuffled = new ArrayList<>(original);
        Collections.shuffle(shuffled);

        SortRaceVisualizer viz = new SortRaceVisualizer();
        SortAlgorithms alg = new SortAlgorithms();

        String[] names = {
            "Insertion Sort (ArrayList)", "Insertion Sort (LinkedList)",
            "Bubble Sort (ArrayList)", "Bubble Sort (LinkedList)",
            "Merge Sort (ArrayList)", "Merge Sort (LinkedList)",
            "Built-in Sort (ArrayList)"
        };
        SortTask.Algorithm[] algorithms = {
            SortTask.Algorithm.INSERTION_ARRAY, SortTask.Algorithm.INSERTION_LINKED,
            SortTask.Algorithm.BUBBLE_ARRAY, SortTask.Algorithm.BUBBLE_LINKED,
            SortTask.Algorithm.MERGE_ARRAY, SortTask.Algorithm.MERGE_LINKED,
            SortTask.Algorithm.BUILT_IN_ARRAY
        };

        // Prepare tasks (each gets its own copy of the same shuffled input)
        java.util.List<SortTask> tasks = new java.util.ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            List<University> data = i % 2 == 1
                ? new java.util.LinkedList<>(shuffled)
                : new java.util.ArrayList<>(shuffled);
            JProgressBar progressBar = viz.addRunner(names[i]);
            tasks.add(new SortTask(names[i], data, byRank, alg, progressBar, algorithms[i]));
        }

        try {
            viz.runRace(tasks);
            System.out.println("Race finished — see GUI for timings.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // --- Option 3: proves the generic method works on a non-University type ---
    private static void runGenericSortProof() {
        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        System.out.println("\nBefore: " + numbers);

        SortAlgorithms sorter = new SortAlgorithms();
        sorter.insertionSort(numbers, Comparator.naturalOrder());

        System.out.println("After:  " + numbers + "\n");
    }
}
