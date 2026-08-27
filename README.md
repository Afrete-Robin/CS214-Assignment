# CS214-Assignment

## How to run

From the project folder in PowerShell:

```powershell
javac -d classes src\*.java
java -cp classes Main
```

Choose option `8` from the menu to open the sorting race. Keep the CSV file in
the project folder because the program loads its fixed filename.

Menu option `5` demonstrates bubble sort, option `6` demonstrates merge sort
and Java's built-in sort, and option `7` runs all seven algorithm/data-structure
combinations 30 times. Option `7` prints the current algorithm and run number
while the benchmark is running, followed by the complete results summary.
Every benchmark run starts with a newly shuffled copy of the dataset.

`SortAlgorithms.java` contains the insertion, bubble, merge, and built-in sort
APIs with their comparison/swap counters; `EmpiricalTestHarness.java` runs and
summarizes the benchmark.

Part 4 growth analysis is implemented in `GrowthAnalysis.java`. Run it with:

```powershell
java -cp classes GrowthAnalysis
```

It sorts descending inputs at increasing sizes and writes `complexity_growth.csv`
and `complexity_growth.svg`, plotting comparisons plus writes/swaps for all
seven algorithm/data-structure combinations.

The benchmark reports comparison, swap, and elapsed-time values as
best/mean/median/worst in the console and CSV output. It randomizes the dataset
before every run, so the 30-run results provide repeated empirical observations
for all seven algorithm/data-structure combinations.
Built-in sort swaps are reported as not measured, not as a genuine zero, because
`Collections.sort` does not expose its internal write/swap operations.
Its theoretical summary is: insertion sort and bubble sort are O(n^2) on
randomized data, while merge sort and Java's built-in object sort are O(n log n)
in typical and worst-case comparison behavior. ArrayList generally performs
better for indexed insertion and bubble operations; merge sort remains close
to O(n log n) for both structures because it sorts through array-backed work
lists.

| Algorithm | ArrayList | LinkedList |
|---|---|---|
| Insertion sort | O(n^2) | O(n^2) |
| Bubble sort | O(n^2) | O(n^2) |
| Merge sort | O(n log n) | O(n log n) |
| Built-in sort | O(n log n) | Not benchmarked |