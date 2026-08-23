

---

DATA LAYER & INSERTION SORT — MEMBER A

FILES
- University.java — data class representing one row of the dataset (13 fields matching all CSV columns)
- CsvReader.java — loads World University Rankings 2023-Cleaned.csv into either an ArrayList<University> or LinkedList<University>
- SortAlgorithms.java — contains insertionSort(), a generic sorting method
- TestSort.java / TestSortGeneric.java — test files demonstrating correctness

HOW TO RUN
javac -d out src\*.java
java -cp out Main

Choose option 8 from the menu to open the sorting race. The race now pauses
briefly after each progress update so the sorting activity is easier to see.

Option 5 demonstrates bubble sort. Option 6 demonstrates merge sort on both
list structures and Java's built-in sort. Option 7 runs all seven required
algorithm/data-structure combinations 30 times, shuffling the data before
every run, and reports comparison and swap counts as best/mean/median/worst.

SortAlgorithms.java contains the shared insertion/bubble APIs and counters.
MergeSort.java contains merge sort for both list structures.
EmpiricalTestHarness.java runs and summarizes the 30-run benchmark.

Theoretical summary: insertion sort and bubble sort are O(n^2) on randomized
data. Merge sort and Java's built-in object sort are O(n log n) in typical and
worst-case comparison behavior. ArrayList generally performs better for
indexed operations, while merge sort stays close to O(n log n) for both list
structures because it uses array-backed working lists.

For the original insertion-sort test only:
javac University.java CsvReader.java SortAlgorithms.java TestSort.java
java TestSort

Make sure the CSV file is named exactly "World University Rankings 2023-Cleaned.csv" and is placed in the same folder as the compiled .class files — the loader path is hardcoded to this filename, so an exact match (including spaces, capitalization, and no underscores) is required.

UNIVERSITY CLASS
Represents one university with all 13 dataset fields (rank, name, location, student counts, staff ratio, international student %, female:male ratio, and all 6 score categories). Implements Comparable<University> (default ordering by rank) and includes a toString() for quick debugging output.

CSVREADER
Reads the dataset and returns either data structure via loadAsArrayList(filePath) or loadAsLinkedList(filePath). Both methods share the same underlying parsing logic (loadInto()), so behavior is guaranteed identical between the two structures.

Known data-quality issues handled during parsing:
- Student counts wrapped in quotes with embedded commas (e.g. "20,965") — parsed using a quote-aware CSV split regex
- 151 rows with missing Location — stored as an empty string, does not crash the loader
- Female:Male Ratio — source data is inconsistently formatted (some rows use "40 : 60", others were corrupted by spreadsheet software into time-duration values). Stored as a raw, unparsed String since it is not used as a sort key
- OverAllScore — for ranks below ~200, the source data provides a score range instead of an exact value (e.g. "51.2–54.3"). These are converted to their midpoint to remain a usable, comparable double
- International Student percentage — a small number of rows have this field missing entirely; defaults to 0.0 in that case

SORTALGORITHMS — INSERTION SORT
public <T> void insertionSort(List<T> list, Comparator<T> comparator)

A generic, in-place Insertion Sort. Accepts List<T> (not ArrayList or LinkedList specifically), satisfying the assignment's polymorphism requirement — the same method works unmodified on either structure. Accepts a Comparator<T> rather than relying on a type's own compareTo(), so it can sort by any field or criterion, and works on any data type, not only University.

VERIFICATION PERFORMED
- Loaded and confirmed all 1,697 rows on both ArrayList and LinkedList
- Shuffled and correctly re-sorted the full dataset by rank on both structures
- Observed the LinkedList version runs noticeably slower than the ArrayList version for the same sort — consistent with LinkedList's O(n) indexed access vs ArrayList's O(1), useful evidence for Part 4's complexity analysis
- Correctly re-sorted by an alternate field (university name, alphabetically) by swapping the comparator, with no changes to insertionSort() itself
- Correctly sorted a List<Integer> (unrelated to University), confirming the method genuinely works on any comparable type
