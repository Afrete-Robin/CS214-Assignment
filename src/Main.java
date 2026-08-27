// ArrayList stores data in a resizable list.
import java.util.ArrayList;
// Collections provides list operations such as shuffle.
import java.util.Collections;
// Comparator decides how two values are ordered.
import java.util.Comparator;
// List lets the program use different list types.
import java.util.List;
// Scanner reads choices typed by the user.
import java.util.Scanner;
// JProgressBar displays sorting progress in the race window.
import javax.swing.JProgressBar;

public class Main {

    // Name of the input data file.
    private static final String CSV_FILE = "World University Rankings 2023-Cleaned.csv";
    // Reads input from the console.
    private static final Scanner scanner = new Scanner(System.in);

    // Starts the menu program.
    public static void main(String[] args) {
        // Keeps the menu open until the user chooses 0.
        boolean running = true;
        while (running) {
            printMenu();
            // Stores the menu number entered by the user.
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

    // Checks that a list is sorted.
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

    // Prints the available menu choices.
    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" CS214 Assignment 1 - Sorting Algorithms");
        System.out.println("=========================================");
        System.out.println(" 1. Print ALL records from CSV ");
        System.out.println(" 2. Load dataset into ArrayList + LinkedList");
        System.out.println(" 3. Run Insertion Sort");
        System.out.println(" 4. Prove generic sort works on List<Integer>");
        System.out.println(" 5. Run Bubble Sort demo");
        System.out.println(" 6. Run Merge Sort / built-in sort demo");
        System.out.println(" 7. Run full empirical test harness (30 runs)");
        System.out.println(" 8. Run sorting race visualizer (Swing)");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option: ");
    }

    // Reads and converts the user's menu choice.
    private static int readChoice() {
        // Stores the line typed in the console.
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Prints all records in a table.
    private static void printAllRecords() {
        // Reads the CSV data.
        CsvReader reader = new CsvReader();
        // Holds the records to print.
        List<University> list = reader.loadAsArrayList(CSV_FILE);
        if (list.isEmpty()) {
            System.out.println("No records found or CSV could not be read.\n");
            return;
        }

        System.out.println("\nPrinting " + list.size() + " records from " + CSV_FILE + ":");

        System.out.printf("%-5s  %-35s  %-15s  %10s  %10s  %6s  %7s%n",
                "Rank", "Name", "Location", "Students", "Stud/Staff", "Intl%", "Overall");
        for (int i = 0; i < 110; i++) System.out.print('-');
        System.out.println();

        for (University u : list) {
            // Keeps the name short enough for the table.
            String name = u.getName() == null ? "" : u.getName();
            if (name.length() > 35) name = name.substring(0, 32) + "...";
            // Uses an empty value when the location is missing.
            String loc = u.getLocation() == null ? "" : u.getLocation();

            System.out.printf("%-5d  %-35s  %-15s  %10d  %10.1f  %6.1f  %7.2f%n",
                    u.getRank(), name, loc, u.getNumStudents(),
                    u.getStudentPerStaff(), u.getInternationalStudentPercent(), u.getOverallScore());
        }
        System.out.println();
    }

    // Loads the data into both list types.
    private static void readIntoBothLists() {
        CsvReader reader = new CsvReader();
        List<University> arrayList = reader.loadAsArrayList(CSV_FILE);
        List<University> linkedList = reader.loadAsLinkedList(CSV_FILE);

        System.out.println("\nArrayList loaded: " + arrayList.size() + " rows");
        System.out.println("LinkedList loaded: " + linkedList.size() + " rows\n");
    }

    // Demonstrates insertion sort on numbers and universities.
    private static void runInsertionSortDemo() {
        // Creates the sorting object.
        SortAlgorithms sorter = new SortAlgorithms();
        // Small list used to test the sort.
        List<Integer> sample = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        System.out.println("\nRunning small-sample test (before -> after):");
        System.out.println("Before: " + sample);
        sorter.insertionSort(sample, Comparator.naturalOrder());
        System.out.println("After:  " + sample);
        // Expected result of sorting the sample list.
        List<Integer> expected = new ArrayList<>(List.of(1,2,3,5,8,9));
        if (sample.equals(expected)) {
            System.out.println("Sample test: PASS\n");
        } else {
            System.out.println("Sample test: FAIL (expected " + expected + ")\n");
        }

        // Reads the university records.
        CsvReader reader = new CsvReader();
        // Copies stored in the two list types.
        List<University> arrayList = reader.loadAsArrayList(CSV_FILE);
        List<University> linkedList = reader.loadAsLinkedList(CSV_FILE);

        System.out.print("Sort by (1) Rank or (2) Name? ");
        // Stores the selected sort field.
        String pick = scanner.nextLine().trim();
        // Sorts by rank unless the user chooses name.
        Comparator<University> comparator = pick.equals("2")
            ? Comparator.comparing(University::getName)
            : Comparator.comparingInt(University::getRank);

        Collections.shuffle(arrayList);
        Collections.shuffle(linkedList);

        // Records the ArrayList start and end times.
        long t1 = System.nanoTime();
        sorter.insertionSort(arrayList, comparator);
        long t2 = System.nanoTime();

        // Records the LinkedList start and end times.
        long t3 = System.nanoTime();
        sorter.insertionSort(linkedList, comparator);
        long t4 = System.nanoTime();

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

        System.out.println("\nFull-dataset sort completed:");
        System.out.println("ArrayList: " + arrayList.size() + " rows, sort time: " + ((t2 - t1)/1_000_000) + " ms");
        System.out.println("LinkedList: " + linkedList.size() + " rows, sort time: " + ((t4 - t3)/1_000_000) + " ms\n");
        // Shows whether each final list is sorted.
        boolean arraySorted = isSorted(arrayList, comparator);
        boolean linkedSorted = isSorted(linkedList, comparator);
        System.out.println("ArrayList fully sorted: " + (arraySorted ? "PASS" : "FAIL"));
        System.out.println("LinkedList fully sorted: " + (linkedSorted ? "PASS" : "FAIL") + "\n");
    }

    // Demonstrates bubble sort on both list types.
    // Uses a counter to record comparisons and swaps.
        private static void runBubbleSortDemo() {
        CsvReader reader = new CsvReader();
        // Original records used to make fresh test lists.
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        // Sorts universities by rank.
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        SortAlgorithms sorter = new SortAlgorithms();
        List<University> arrayList = new ArrayList<>(source);
        List<University> linkedList = new java.util.LinkedList<>(source);
        Collections.shuffle(arrayList);
        Collections.shuffle(linkedList);
        // Counts work done by each sort.
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

    // Demonstrates merge sort and Java's built-in sort.
        private static void runMergeAndBuiltInDemo() {
        CsvReader reader = new CsvReader();
        // Original records used for each test.
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        // Sorts all lists by rank.
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        SortAlgorithms sorter = new SortAlgorithms();
        List<University> mergeArray = new ArrayList<>(source);
        List<University> mergeLinked = new java.util.LinkedList<>(source);
        List<University> builtIn = new ArrayList<>(source);
        Collections.shuffle(mergeArray);
        Collections.shuffle(mergeLinked);
        Collections.shuffle(builtIn);
        // Counts work done by each sort.
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

    // Runs the 30-run timing test.
        private static void runEmpiricalTest() {
        CsvReader reader = new CsvReader();
        List<University> source = reader.loadAsArrayList(CSV_FILE);
        if (source.isEmpty()) {
            System.out.println("CSV empty or not found.");
            return;
        }
        new EmpiricalTestHarness().run(source);
        }

    // Opens the sorting race window.
    private static void runRaceVisualizer() {
        // Reads the original records.
        CsvReader reader = new CsvReader();
        List<University> original = reader.loadAsArrayList(CSV_FILE);
        if (original.isEmpty()) {
            System.out.println("CSV empty or not found.");
            return;
        }
        // Sorts every runner by rank.
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);

        // Gives every runner the same random starting order.
        List<University> shuffled = new ArrayList<>(original);
        Collections.shuffle(shuffled);

        // Creates the race window and progress tracker.
        SortRaceVisualizer viz = new SortRaceVisualizer();
        SortRaceVisualizer.ProgressTracker progressTracker = new SortRaceVisualizer.ProgressTracker();
        SortAlgorithms alg = new SortAlgorithms();

        // Names shown beside the progress bars.
        String[] names = {
            "Insertion Sort (ArrayList)", "Insertion Sort (LinkedList)",
            "Bubble Sort (ArrayList)", "Bubble Sort (LinkedList)",
            "Merge Sort (ArrayList)", "Merge Sort (LinkedList)",
            "Built-in Sort (ArrayList)"
        };
        // Sort type used by each runner.
        SortTask.Algorithm[] algorithms = {
            SortTask.Algorithm.INSERTION_ARRAY, SortTask.Algorithm.INSERTION_LINKED,
            SortTask.Algorithm.BUBBLE_ARRAY, SortTask.Algorithm.BUBBLE_LINKED,
            SortTask.Algorithm.MERGE_ARRAY, SortTask.Algorithm.MERGE_LINKED,
            SortTask.Algorithm.BUILT_IN_ARRAY
        };

        // Holds all sorting tasks.
        java.util.List<SortTask> tasks = new java.util.ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            // Gives each task its own copy of the input.
            List<University> data = i % 2 == 1
                ? new java.util.LinkedList<>(shuffled)
                : new java.util.ArrayList<>(shuffled);
            JProgressBar progressBar = viz.addRunner(names[i]);
            tasks.add(new SortTask(names[i], data, byRank, alg, progressBar, algorithms[i], progressTracker));
        }

        try {
            viz.runRace(tasks);
            System.out.println("Race finished — see GUI for timings.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Shows that the generic sort also works with integers.
    private static void runGenericSortProof() {
        // Numbers to sort.
        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        System.out.println("\nBefore: " + numbers);

        SortAlgorithms sorter = new SortAlgorithms();
        sorter.insertionSort(numbers, Comparator.naturalOrder());

        System.out.println("After:  " + numbers + "\n");
    }
}
