import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GrowthAnalysis {
    private static final String INPUT_FILE = "World University Rankings 2023-Cleaned.csv";
    private static final String CSV_FILE = "complexity_growth.csv";
    private static final String SVG_FILE = "complexity_growth.svg";
    private static final String THEORETICAL_CSV_FILE = "theoretical_complexity.csv";
    private static final String THEORETICAL_SVG_FILE = "theoretical_complexity.svg";
    private static final int[] REQUESTED_SIZES = {100, 200, 400, 800, 1200, 1600};

    private enum Algorithm {
        INSERTION_ARRAY("Insertion ArrayList", false),
        INSERTION_LINKED("Insertion LinkedList", true),
        BUBBLE_ARRAY("Bubble ArrayList", false),
        BUBBLE_LINKED("Bubble LinkedList", true),
        MERGE_ARRAY("Merge ArrayList", false),
        MERGE_LINKED("Merge LinkedList", true),
        BUILT_IN_ARRAY("Built-in ArrayList", false),
        BUILT_IN_LINKED("Built-in LinkedList", true);

        private final String label;
        private final boolean linked;

        Algorithm(String label, boolean linked) {
            this.label = label;
            this.linked = linked;
        }
    }

    private static class Point {
        private final int size;
        private final long operations;

        // Stores one input size and its operation count.
        Point(int size, long operations) {
            this.size = size;
            this.operations = operations;
        }
    }

    // Runs the growth test and creates the output files.
    public static void main(String[] args) throws IOException {
        CsvReader reader = new CsvReader();
        List<University> source = reader.loadAsArrayList(INPUT_FILE);
        if (source.isEmpty()) {
            System.out.println("CSV empty or not found: " + INPUT_FILE);
            return;
        }

        // Sort the source in reverse rank order for the test input.
        Comparator<University> byRank = Comparator.comparingInt(University::getRank);
        List<University> descending = new ArrayList<>(source);
        descending.sort(byRank.reversed());
        int[] sizes = usableSizes(descending.size());
        // Test each algorithm with each usable input size.
        List<List<Point>> series = new ArrayList<>();
        for (Algorithm algorithm : Algorithm.values()) {
            List<Point> points = new ArrayList<>();
            for (int size : sizes) {
                List<University> data = algorithm.linked
                        ? new LinkedList<>(descending.subList(0, size))
                        : new ArrayList<>(descending.subList(0, size));
                SortAlgorithms.SortCounter counter = new SortAlgorithms.SortCounter();
                sort(algorithm, data, byRank, counter);
                if (!isSorted(data, byRank)) {
                    throw new IllegalStateException(algorithm.label + " failed for n=" + size);
                }
                points.add(new Point(size, counter.getOperations()));
            }
            series.add(points);
        }

        writeCsv(series, sizes);
        writeSvg(series, sizes);
        writeTheoreticalCsv(sizes);
        writeTheoreticalSvg(sizes);
        System.out.println("Part 4 growth analysis complete.");
        System.out.println("Input: descending rank order; sizes: " + Arrays.toString(sizes));
        System.out.println("Measured operations written to " + CSV_FILE);
        System.out.println("Measured operation graph written to " + SVG_FILE);
        System.out.println("Theoretical operations written to " + THEORETICAL_CSV_FILE);
        System.out.println("Theoretical graph written to " + THEORETICAL_SVG_FILE);
    }

    // Keeps only input sizes that exist in the source data.
    private static int[] usableSizes(int sourceSize) {
        int count = 0;
        for (int size : REQUESTED_SIZES) {
            if (size <= sourceSize) count++;
        }
        return Arrays.copyOf(REQUESTED_SIZES, count);
    }

    // Runs the selected sorting algorithm.
    private static void sort(Algorithm algorithm, List<University> data,
                            Comparator<University> comparator,
                            SortAlgorithms.SortCounter counter) {
        SortAlgorithms sorter = new SortAlgorithms();
        switch (algorithm) {
            case INSERTION_ARRAY, INSERTION_LINKED -> sorter.insertionSort(data, comparator, counter);
            case BUBBLE_ARRAY -> sorter.bubbleSortArrayList(data, comparator, counter);
            case BUBBLE_LINKED -> sorter.bubbleSortLinkedList(data, comparator, counter);
            case MERGE_ARRAY -> sorter.mergeSortArrayList(data, comparator, counter);
            case MERGE_LINKED -> sorter.mergeSortLinkedList(data, comparator, counter);
            case BUILT_IN_ARRAY, BUILT_IN_LINKED -> sorter.builtInSort(data, comparator, counter);
        }
    }

    // Returns a simple worst-case operation estimate for each combination.
    private static long theoreticalOperations(Algorithm algorithm, int size) {
        long n = size;
        long nSquared = n * n;
        int log = 64 - Long.numberOfLeadingZeros(Math.max(1, n));
        return switch (algorithm) {
            case INSERTION_ARRAY, BUBBLE_ARRAY -> nSquared;
            case INSERTION_LINKED, BUBBLE_LINKED -> nSquared * n;
            case MERGE_ARRAY, BUILT_IN_ARRAY, BUILT_IN_LINKED -> n * log;
            case MERGE_LINKED -> nSquared * log;
        };
    }

    // Checks whether the list is sorted.
    private static <T> boolean isSorted(List<T> list, Comparator<T> comparator) {
        for (int i = 1; i < list.size(); i++) {
            if (comparator.compare(list.get(i - 1), list.get(i)) > 0) return false;
        }
        return true;
    }

    // Writes operation counts to a CSV file.
    private static void writeCsv(List<List<Point>> series, int[] sizes) throws IOException {
        StringBuilder output = new StringBuilder("Algorithm,Size,Operations\n");
        Algorithm[] algorithms = Algorithm.values();
        for (int i = 0; i < algorithms.length; i++) {
            for (Point point : series.get(i)) {
                output.append(algorithms[i].label).append(',')
                        .append(point.size).append(',')
                        .append(point.operations).append('\n');
            }
        }
        Files.writeString(Path.of(CSV_FILE), output.toString());
    }

    // Creates the SVG line graph.
    private static void writeSvg(List<List<Point>> series, int[] sizes) throws IOException {
        int width = 1100;
        int height = 700;
        int left = 90;
        int top = 55;
        int right = 260;
        int bottom = 70;
        long maxOperations = 1;
        for (List<Point> points : series) {
            for (Point point : points) maxOperations = Math.max(maxOperations, point.operations);
        }
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
                .append(width).append("\" height=\"").append(height)
                .append("\" viewBox=\"0 0 ").append(width).append(' ').append(height).append("\">\n")
                .append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n")
                .append("<text x=\"90\" y=\"30\" font-family=\"sans-serif\" font-size=\"20\" font-weight=\"bold\">Measured operations: descending input</text>\n");

        int chartWidth = width - left - right;
        int chartHeight = height - top - bottom;
        svg.append("<line x1=\"").append(left).append("\" y1=\"").append(top + chartHeight)
                .append("\" x2=\"").append(left + chartWidth).append("\" y2=\"").append(top + chartHeight)
                .append("\" stroke=\"#333\"/>\n")
                .append("<line x1=\"").append(left).append("\" y1=\"").append(top)
                .append("\" x2=\"").append(left).append("\" y2=\"").append(top + chartHeight)
                .append("\" stroke=\"#333\"/>\n")
                .append("<text x=\"").append(left + chartWidth / 2).append("\" y=\"").append(height - 20)
                .append("\" text-anchor=\"middle\" font-family=\"sans-serif\">Input size (n)</text>\n")
                .append("<text transform=\"translate(20 ").append(top + chartHeight / 2)
                .append(") rotate(-90)\" text-anchor=\"middle\" font-family=\"sans-serif\">Comparisons + writes/swaps</text>\n");

        for (int i = 0; i < sizes.length; i++) {
            int x = left + (sizes.length == 1 ? chartWidth / 2 : i * chartWidth / (sizes.length - 1));
            svg.append("<line x1=\"").append(x).append("\" y1=\"").append(top)
                    .append("\" x2=\"").append(x).append("\" y2=\"").append(top + chartHeight)
                    .append("\" stroke=\"#e5e7eb\"/>\n")
                    .append("<text x=\"").append(x).append("\" y=\"").append(top + chartHeight + 22)
                    .append("\" text-anchor=\"middle\" font-family=\"sans-serif\" font-size=\"12\">")
                    .append(sizes[i]).append("</text>\n");
        }

        String[] colors = {"#2563eb", "#1d4ed8", "#dc2626", "#b91c1c", "#059669", "#047857", "#7c3aed", "#6d28d9"};
        for (int i = 0; i < series.size(); i++) {
            StringBuilder points = new StringBuilder();
            for (int j = 0; j < series.get(i).size(); j++) {
                Point point = series.get(i).get(j);
                double x = left + (sizes.length == 1 ? chartWidth / 2.0 : j * chartWidth / (double) (sizes.length - 1));
                double y = top + chartHeight - point.operations * chartHeight / (double) maxOperations;
                points.append(String.format(java.util.Locale.ROOT, "%.1f,%.1f ", x, y));
            }
            svg.append("<polyline fill=\"none\" stroke=\"").append(colors[i])
                    .append("\" stroke-width=\"3\" points=\"").append(points).append("\"/>\n");
            int legendY = top + i * 28;
            svg.append("<line x1=\"").append(left + chartWidth + 20).append("\" y1=\"").append(legendY)
                    .append("\" x2=\"").append(left + chartWidth + 48).append("\" y2=\"").append(legendY)
                    .append("\" stroke=\"").append(colors[i]).append("\" stroke-width=\"3\"/>\n")
                    .append("<text x=\"").append(left + chartWidth + 58).append("\" y=\"").append(legendY + 5)
                    .append("\" font-family=\"sans-serif\" font-size=\"12\">").append(Algorithm.values()[i].label).append("</text>\n");
        }
        svg.append("</svg>\n");
        Files.writeString(Path.of(SVG_FILE), svg.toString());
    }

    // Writes the theoretical operation estimates to a CSV file.
    private static void writeTheoreticalCsv(int[] sizes) throws IOException {
        StringBuilder output = new StringBuilder("Algorithm,Size,TheoreticalOperations,Complexity\n");
        for (Algorithm algorithm : Algorithm.values()) {
            for (int size : sizes) {
                output.append(algorithm.label).append(',')
                        .append(size).append(',')
                        .append(theoreticalOperations(algorithm, size)).append(',')
                        .append(complexityName(algorithm)).append('\n');
            }
        }
        Files.writeString(Path.of(THEORETICAL_CSV_FILE), output.toString());
    }

    // Returns the Big-O label for one algorithm and list type.
    private static String complexityName(Algorithm algorithm) {
        return switch (algorithm) {
            case INSERTION_ARRAY, BUBBLE_ARRAY -> "O(n^2)";
            case INSERTION_LINKED, BUBBLE_LINKED -> "O(n^3) in this indexed implementation";
            case MERGE_ARRAY, BUILT_IN_ARRAY, BUILT_IN_LINKED -> "O(n log n)";
            case MERGE_LINKED -> "O(n^2 log n) in this indexed implementation";
        };
    }

    // Creates the theoretical worst-case Big-O graph.
    private static void writeTheoreticalSvg(int[] sizes) throws IOException {
        int width = 1150;
        int height = 700;
        int left = 90;
        int top = 55;
        int right = 300;
        int bottom = 70;
        long maxOperations = 1;
        for (Algorithm algorithm : Algorithm.values()) {
            for (int size : sizes) {
                maxOperations = Math.max(maxOperations, theoreticalOperations(algorithm, size));
            }
        }

        int chartWidth = width - left - right;
        int chartHeight = height - top - bottom;
        String[] colors = {"#2563eb", "#1d4ed8", "#dc2626", "#b91c1c", "#059669", "#047857", "#7c3aed", "#6d28d9"};
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
                .append(width).append("\" height=\"").append(height)
                .append("\" viewBox=\"0 0 ").append(width).append(' ').append(height).append("\">\n")
                .append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n")
                .append("<text x=\"90\" y=\"30\" font-family=\"sans-serif\" font-size=\"20\" font-weight=\"bold\">Theoretical Part 4: Worst-Case Big-O</text>\n")
                .append("<text x=\"90\" y=\"48\" font-family=\"sans-serif\" font-size=\"12\">Estimated operations, not real running time</text>\n")
                .append("<line x1=\"").append(left).append("\" y1=\"").append(top + chartHeight)
                .append("\" x2=\"").append(left + chartWidth).append("\" y2=\"").append(top + chartHeight).append("\" stroke=\"#333\"/>\n")
                .append("<line x1=\"").append(left).append("\" y1=\"").append(top)
                .append("\" x2=\"").append(left).append("\" y2=\"").append(top + chartHeight).append("\" stroke=\"#333\"/>\n")
                .append("<text x=\"").append(left + chartWidth / 2).append("\" y=\"").append(height - 20)
                .append("\" text-anchor=\"middle\" font-family=\"sans-serif\">Input size (n)</text>\n")
                .append("<text transform=\"translate(20 ").append(top + chartHeight / 2)
                .append(") rotate(-90)\" text-anchor=\"middle\" font-family=\"sans-serif\">Theoretical operations</text>\n");

        for (int i = 0; i < sizes.length; i++) {
            int x = left + (sizes.length == 1 ? chartWidth / 2 : i * chartWidth / (sizes.length - 1));
            svg.append("<line x1=\"").append(x).append("\" y1=\"").append(top)
                    .append("\" x2=\"").append(x).append("\" y2=\"").append(top + chartHeight)
                    .append("\" stroke=\"#e5e7eb\"/>\n")
                    .append("<text x=\"").append(x).append("\" y=\"").append(top + chartHeight + 22)
                    .append("\" text-anchor=\"middle\" font-family=\"sans-serif\" font-size=\"12\">")
                    .append(sizes[i]).append("</text>\n");
        }

        for (int i = 0; i < Algorithm.values().length; i++) {
            Algorithm algorithm = Algorithm.values()[i];
            StringBuilder points = new StringBuilder();
            for (int j = 0; j < sizes.length; j++) {
                double x = left + (sizes.length == 1 ? chartWidth / 2.0 : j * chartWidth / (double) (sizes.length - 1));
                double y = top + chartHeight - theoreticalOperations(algorithm, sizes[j]) * chartHeight / (double) maxOperations;
                points.append(String.format(java.util.Locale.ROOT, "%.1f,%.1f ", x, y));
            }
            svg.append("<polyline fill=\"none\" stroke=\"").append(colors[i])
                    .append("\" stroke-width=\"3\" points=\"").append(points).append("\"/>\n");
            int legendY = top + i * 28;
            svg.append("<line x1=\"").append(left + chartWidth + 20).append("\" y1=\"").append(legendY)
                    .append("\" x2=\"").append(left + chartWidth + 48).append("\" y2=\"").append(legendY)
                    .append("\" stroke=\"").append(colors[i]).append("\" stroke-width=\"3\"/>\n")
                    .append("<text x=\"").append(left + chartWidth + 58).append("\" y=\"").append(legendY + 5)
                    .append("\" font-family=\"sans-serif\" font-size=\"12\">").append(algorithm.label)
                    .append(" (" ).append(complexityName(algorithm)).append(")</text>\n");
        }
        svg.append("</svg>\n");
        Files.writeString(Path.of(THEORETICAL_SVG_FILE), svg.toString());
    }
}
