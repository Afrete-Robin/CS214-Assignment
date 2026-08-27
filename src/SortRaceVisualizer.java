// Swing creates the window and progress bars.
import javax.swing.*;
// AWT provides colors, sizes, and drawing tools.
import java.awt.*;
// List stores the sorting tasks and UI items.
import java.util.List;
// These classes run the sorting tasks at the same time.
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
// Map stores progress for each task.
import java.util.HashMap;
import java.util.Map;

public class SortRaceVisualizer {
    // Main race window.
    private JFrame frame;
    // Panel that holds the progress bars.
    private JPanel panel;

    // Shares progress updates between sorting tasks.
    public static class ProgressTracker {
        // Current progress for each task name.
        private final Map<String, Integer> progressByTask = new HashMap<>();

        // Updates a task's progress bar.
        public synchronized void update(String taskName, int progress, JProgressBar progressBar) {
            int previous = progressByTask.getOrDefault(taskName, -1);
            if (progress <= previous) return;
            progressByTask.put(taskName, progress);
            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
        }

    }

    // Builds the race window.
    public SortRaceVisualizer() {
        try {
            SwingUtilities.invokeAndWait(this::buildUI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creates the frame and its main panel.
    private void buildUI() {
        frame = new JFrame("Sorting Race");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.setContentPane(new JScrollPane(panel));
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Adds one runner and returns its progress bar.
    public JProgressBar addRunner(String name) {
        // Holds the runner label and progress bar.
        JPanel row = new JPanel(new BorderLayout(8,8));
        // Shows the runner name.
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(220, 20));
        // Chooses a color based on the algorithm name.
        Color runnerColor = colorForRunner(name);
        JProgressBar bar = new CarProgressBar(runnerColor);
        label.setForeground(runnerColor.darker());
        bar.setPreferredSize(new Dimension(600, 64));
        row.add(label, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        panel.add(row);
        panel.revalidate();
        return bar;
    }

    // Chooses the color used for one runner.
    private Color colorForRunner(String name) {
        if (name.startsWith("Insertion Sort")) {
            return new Color(52, 152, 219);
        }
        if (name.startsWith("Merge Sort")) {
            return new Color(142, 68, 173);
        }
        if (name.startsWith("Built-in Sort")) {
            return new Color(192, 57, 43);
        }
        if (name.contains("ArrayList")) {
            return new Color(230, 126, 34);
        }
        if (name.contains("LinkedList")) {
            return new Color(39, 174, 96);
        }
        return new Color(127, 140, 141);
    }

    // Draws a progress bar with a moving car.
    private static class CarProgressBar extends JProgressBar {
        // Color of the car.
        private final Color carColor;

        // Creates a car progress bar.
        CarProgressBar(Color carColor) {
            super(0, 100);
            this.carColor = carColor;
            setOpaque(false);
            setStringPainted(false);
        }

        // Draws the track, car, and status text.
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            // Size and position of the track and car.
            int width = getWidth();
            int height = getHeight();
            int trackTop = 12;
            int trackHeight = height - 24;
            int carWidth = 48;
            int carHeight = 26;
            int carX = 12 + (int) ((width - carWidth - 28) * (getValue() / 100.0));
            int carY = trackTop + (trackHeight - carHeight) / 2;

            // Draw the track and lane marks.
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(45, 52, 61));
            g.fillRoundRect(4, trackTop, width - 8, trackHeight, 12, 12);
            g.setColor(new Color(238, 211, 91));
            for (int x = 16; x < width - 24; x += 34) {
                g.fillRect(x, trackTop + trackHeight / 2 - 1, 18, 2);
            }

            g.setColor(Color.WHITE);
            g.fillRect(width - 20, trackTop + 6, 3, trackHeight - 12);
            for (int y = trackTop + 7; y < trackTop + trackHeight - 7; y += 12) {
                g.setColor((y / 12) % 2 == 0 ? Color.WHITE : new Color(45, 52, 61));
                g.fillRect(width - 25, y, 5, 6);
            }

            // Draw the car windows and wheels.
            g.setColor(carColor);
            g.fillRoundRect(carX, carY + 5, carWidth, carHeight - 5, 8, 8);
            g.fillRoundRect(carX + 10, carY, 27, 14, 8, 8);
            g.setColor(new Color(210, 235, 245));
            g.fillRoundRect(carX + 15, carY + 3, 9, 7, 3, 3);
            g.fillRoundRect(carX + 26, carY + 3, 8, 7, 3, 3);
            g.setColor(new Color(25, 25, 25));
            g.fillOval(carX + 7, carY + carHeight - 5, 10, 10);
            g.fillOval(carX + carWidth - 17, carY + carHeight - 5, 10, 10);
            g.setColor(new Color(45, 52, 61));
            g.fillRect(4, height - 16, width - 8, 14);
            // Shows the percentage or finish time.
            g.setColor(Color.WHITE);
            String status = getString().isEmpty() ? getValue() + "%" : getString();
            g.drawString(status, 12, height - 2);
            g.dispose();
        }
    }

    // Runs all sorting tasks and waits for them to finish.
    public void runRace(List<SortTask> tasks) throws InterruptedException {
        // Creates one worker thread for each task.
        ExecutorService ex = Executors.newFixedThreadPool(tasks.size());
        for (SortTask t : tasks) ex.submit(t);
        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.MINUTES);
    }
}
