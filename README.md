# CS214-Assignment

## How to run

From the project folder in PowerShell:

```powershell
javac -d out src\*.java
java -cp out Main
```

Choose option `8` from the menu to open the sorting race. Keep the CSV file in
the project folder because the program loads its fixed filename.

Menu option `5` demonstrates bubble sort, option `6` demonstrates merge sort
and Java's built-in sort, and option `7` runs all seven algorithm/data-structure
combinations 30 times. Every benchmark run starts with a newly shuffled copy
of the dataset.

`SortAlgorithms.java` contains the shared insertion/bubble APIs and counters;
`MergeSort.java` contains the dedicated merge-sort implementation for both
list structures; `EmpiricalTestHarness.java` runs and summarizes the benchmark.

The benchmark reports comparison and swap counts as best/mean/median/worst.
Its theoretical summary is: insertion sort and bubble sort are O(n^2) on
randomized data, while merge sort and Java's built-in object sort are O(n log n)
in typical and worst-case comparison behavior. ArrayList generally performs
better for indexed insertion and bubble operations; merge sort remains close
to O(n log n) for both structures because it sorts through array-backed work
lists.