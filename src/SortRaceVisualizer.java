import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

public class SortRaceVisualizer {
    private JFrame frame;
    private JPanel panel;
    private List<JProgressBar> bars = new ArrayList<>();
    private List<JLabel> labels = new ArrayList<>();

    public static class ProgressTracker {
        private final Map<String, Integer> progressByTask = new HashMap<>();

        public synchronized void update(String taskName, int progress, JProgressBar progressBar) {
            int previous = progressByTask.getOrDefault(taskName, -1);
            if (progress <= previous) return;
            progressByTask.put(taskName, progress);
            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
        }

        public synchronized Map<String, Integer> snapshot() {
            return new HashMap<>(progressByTask);
        }
    }

    public SortRaceVisualizer() {
        try {
            SwingUtilities.invokeAndWait(this::buildUI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public JProgressBar addRunner(String name) {
        JPanel row = new JPanel(new BorderLayout(8,8));
        JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(220, 20));
        Color runnerColor = colorForRunner(name);
        JProgressBar bar = new CarProgressBar(runnerColor);
        label.setForeground(runnerColor.darker());
        bar.setPreferredSize(new Dimension(600, 64));
        row.add(label, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        panel.add(row);
        panel.revalidate();
        bars.add(bar);
        labels.add(label);
        return bar;
    }

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

    private static class CarProgressBar extends JProgressBar {
        private final Color carColor;

        CarProgressBar(Color carColor) {
            super(0, 100);
            this.carColor = carColor;
            setOpaque(false);
            setStringPainted(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            int width = getWidth();
            int height = getHeight();
            int trackTop = 12;
            int trackHeight = height - 24;
            int carWidth = 48;
            int carHeight = 26;
            int carX = 12 + (int) ((width - carWidth - 28) * (getValue() / 100.0));
            int carY = trackTop + (trackHeight - carHeight) / 2;

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
            g.setColor(Color.WHITE);
            String status = getString().isEmpty() ? getValue() + "%" : getString();
            g.drawString(status, 12, height - 2);
            g.dispose();
        }
    }

    public void runRace(List<SortTask> tasks) throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(tasks.size());
        for (SortTask t : tasks) ex.submit(t);
        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.MINUTES);
    }
}
