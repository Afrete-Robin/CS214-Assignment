# CS214 Assignment 1 - Sorting Algorithms

Design and Analysis of Algorithms, University of the South Pacific, Semester 2, 2026.

## Requirements

- Java 17 or newer
- `World University Rankings 2023-Cleaned.csv` in the project folder
- A terminal opened in the project folder


## How to run 

Compile all Java files from the project folder:

```Command Prompt
javac -d classes src\*.java
```

Start the main menu:

```Command Prompt
java -cp classes Main
```

To run the Part 4 growth analysis separately:

```powershell
java -cp classes GrowthAnalysis
```

This creates `complexity_growth.csv` and `complexity_growth.svg` in the project
folder. Open the SVG file in a browser to view the graph.

## Menu Options

1. Print every CSV record in a table.
2. Load the data into an `ArrayList` and a `LinkedList`.
3. Run insertion sort by rank or name.
4. Show that the generic insertion sort works with integers.
5. Run bubble sort on both list types.
6. Run merge sort and Java's built-in sort.
7. Run the empirical test 30 times for each algorithm and list type.
8. Open the Swing sorting race visualizer.
0. Exit the program.

## Files

- `Main.java` contains the menu and connects the program features.
- `University.java` stores one university record and its ranking values.
- `CsvReader.java` reads the CSV into an `ArrayList` or `LinkedList`.
- `SortAlgorithms.java` contains insertion sort, bubble sort, merge sort, and
	Java's built-in sort. `SortCounter` records comparisons and writes/swaps.
- `SortTask.java` runs one sorting algorithm for the race window.
- `SortRaceVisualizer.java` creates the Swing window and progress bars.
- `EmpiricalTestHarness.java` runs the 30-run benchmark and writes
	`empirical_results.csv`.
- `GrowthAnalysis.java` measures operation growth at several input sizes and
	writes the CSV and SVG graph.
- `TestReader.java`, `TestSort.java`, and `TestSortGeneric.java` are small test
	programs for the reader and insertion sort.

## Benchmark Details

The empirical test runs these seven combinations:

| Algorithm      | List type     |
|----------------|---------------|
| Insertion sort | `ArrayList`   |
| Insertion sort | `LinkedList`  |
| Bubble sort    | `ArrayList`   |
| Bubble sort    | `LinkedList`  |
| Merge sort     | `ArrayList`   |
| Merge sort     | `LinkedList`  |
| Built-in sort  | `ArrayList`   |

Each run starts with a newly shuffled copy of the data. The program reports the
best, mean, median, and worst values for comparisons, writes/swaps, and time.
Built-in sort writes/swaps are shown as zero because Java's `Collections.sort`
does not expose those internal operations to the counter.

## Complexity Summary

`GrowthAnalysis` sorts reverse-ordered input at sizes 100, 200, 400, 800, 1200,
and 1600 when those sizes are available in the CSV file.

| Algorithm      | ArrayList  | LinkedList      |
|----------------|------------|-----------------|
| Insertion sort | O(n^2)     | O(n^2)          |
| Bubble sort    | O(n^2)     | O(n^2)          |
| Merge sort     | O(n log n) | O(n log n)      |
| Built-in sort  | O(n log n) | Not benchmarked |

## CSV Data Handling

`CsvReader` handles the following input cases:

- Student numbers with commas inside quoted values, such as `"20,965"`.
- Empty location values.
- Overall scores written as ranges, using the midpoint of the range.
- Empty international student percentages, stored as `0`.
- Female-to-male ratios stored as text because that field is not used for sorting.