import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerGUI extends JFrame {

    // Input components
    private JTextField numProcessesField;
    private JPanel processInputPanel;
    private JTextField quantumField;
    private java.util.List<JTextField> pidFields;
    private java.util.List<JTextField> arrivalFields;
    private java.util.List<JTextField> burstFields;
    private JButton addProcessButton;
    private JButton removeProcessButton;

    // Result components
    private JTable rrTable;
    private JTable srtfTable;
    private JPanel rrGanttPanel;
    private JPanel srtfGanttPanel;
    private JTextArea comparisonArea;

    // Data
    private SchedulingResult rrResult;
    private SchedulingResult srtfResult;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Algorithm Comparator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        pidFields = new ArrayList<>();
        arrivalFields = new ArrayList<>();
        burstFields = new ArrayList<>();

        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Top: Input Section
        add(createInputPanel(), BorderLayout.NORTH);

        // Center: Results and Gantt Charts
        add(createResultsPanel(), BorderLayout.CENTER);

        // Bottom: Comparison Section
        add(createComparisonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Input Section"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Time Quantum (RR):"));
        quantumField = new JTextField("2", 5);
        topPanel.add(quantumField);

        topPanel.add(Box.createHorizontalStrut(20));
        addProcessButton = new JButton("Add Process");
        removeProcessButton = new JButton("Remove Process");
        topPanel.add(addProcessButton);
        topPanel.add(removeProcessButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Process input panel with scroll
        processInputPanel = new JPanel();
        processInputPanel.setLayout(new BoxLayout(processInputPanel, BoxLayout.Y_AXIS));

        // Add header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        headerPanel.add(new JLabel("Process ID", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Arrival Time", SwingConstants.CENTER));
        headerPanel.add(new JLabel("Burst Time", SwingConstants.CENTER));
        headerPanel.setMaximumSize(new Dimension(400, 30));
        processInputPanel.add(headerPanel);

        // Add 3 default processes
        for (int i = 0; i < 3; i++) {
            addProcessRow();
        }

        JScrollPane scrollPane = new JScrollPane(processInputPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton runRRBtn = new JButton("Run RR");
        JButton runSRTFBtn = new JButton("Run SRTF");
        JButton compareBothBtn = new JButton("Compare Both");

        runRRBtn.addActionListener(e -> runRR());
        runSRTFBtn.addActionListener(e -> runSRTF());
        compareBothBtn.addActionListener(e -> runBoth());

        buttonPanel.add(runRRBtn);
        buttonPanel.add(runSRTFBtn);
        buttonPanel.add(compareBothBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add/Remove button actions
        addProcessButton.addActionListener(e -> {
            addProcessRow();
            processInputPanel.revalidate();
            processInputPanel.repaint();
        });

        removeProcessButton.addActionListener(e -> {
            if (pidFields.size() > 1) {
                removeLastProcessRow();
                processInputPanel.revalidate();
                processInputPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Must have at least one process!");
            }
        });

        return mainPanel;
    }

    private void addProcessRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 3, 5, 5));

        JTextField pidField = new JTextField(String.valueOf(pidFields.size() + 1));
        JTextField arrivalField = new JTextField("0");
        JTextField burstField = new JTextField("5");

        rowPanel.add(pidField);
        rowPanel.add(arrivalField);
        rowPanel.add(burstField);

        pidFields.add(pidField);
        arrivalFields.add(arrivalField);
        burstFields.add(burstField);

        rowPanel.setMaximumSize(new Dimension(400, 30));
        processInputPanel.add(rowPanel);
    }

    private void removeLastProcessRow() {
        if (!pidFields.isEmpty()) {
            processInputPanel.remove(processInputPanel.getComponentCount() - 1);
            pidFields.remove(pidFields.size() - 1);
            arrivalFields.remove(arrivalFields.size() - 1);
            burstFields.remove(burstFields.size() - 1);
        }
    }

    private JPanel createResultsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Round Robin Results
        JPanel rrPanel = new JPanel(new BorderLayout(5, 5));
        rrPanel.setBorder(BorderFactory.createTitledBorder("Round Robin Results"));

        String[] columns = {"PID", "Arrival", "Burst", "Completion", "TAT", "WT", "RT"};
        rrTable = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane rrScrollPane = new JScrollPane(rrTable);
        rrScrollPane.setPreferredSize(new Dimension(600, 200));

        rrGanttPanel = new JPanel();
        rrGanttPanel.setPreferredSize(new Dimension(600, 100));
        rrGanttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        rrPanel.add(rrScrollPane, BorderLayout.CENTER);
        rrPanel.add(rrGanttPanel, BorderLayout.SOUTH);

        // SRTF Results
        JPanel srtfPanel = new JPanel(new BorderLayout(5, 5));
        srtfPanel.setBorder(BorderFactory.createTitledBorder("SRTF Results"));

        srtfTable = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane srtfScrollPane = new JScrollPane(srtfTable);
        srtfScrollPane.setPreferredSize(new Dimension(600, 200));

        srtfGanttPanel = new JPanel();
        srtfGanttPanel.setPreferredSize(new Dimension(600, 100));
        srtfGanttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        srtfPanel.add(srtfScrollPane, BorderLayout.CENTER);
        srtfPanel.add(srtfGanttPanel, BorderLayout.SOUTH);

        mainPanel.add(rrPanel);
        mainPanel.add(srtfPanel);

        return mainPanel;
    }

    private JPanel createComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Comparison"));

        comparisonArea = new JTextArea(4, 50);
        comparisonArea.setEditable(false);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(comparisonArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private List<ProcessData> getInputProcesses() {
        List<ProcessData> processes = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        for (int i = 0; i < pidFields.size(); i++) {
            try {
                int pid = Integer.parseInt(pidFields.get(i).getText().trim());
                int arrival = Integer.parseInt(arrivalFields.get(i).getText().trim());
                int burst = Integer.parseInt(burstFields.get(i).getText().trim());

                // Validation
                if (usedIds.contains(pid)) {
                    throw new IllegalArgumentException("Duplicate PID: " + pid);
                }
                if (arrival < 0) {
                    throw new IllegalArgumentException("Arrival time cannot be negative for PID " + pid);
                }
                if (burst <= 0) {
                    throw new IllegalArgumentException("Burst time must be positive for PID " + pid);
                }

                usedIds.add(pid);
                processes.add(new ProcessData(pid, arrival, burst));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in process " + (i + 1));
            }
        }

        if (processes.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one process");
        }

        return processes;
    }

    private int getQuantum() {
        try {
            int quantum = Integer.parseInt(quantumField.getText().trim());
            if (quantum <= 0) {
                throw new IllegalArgumentException("Time quantum must be positive");
            }
            return quantum;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time quantum format");
        }
    }

    private void runRR() {
        try {
            List<ProcessData> processes = getInputProcesses();
            int quantum = getQuantum();
            rrResult = RRAlgorithm.execute(processes, quantum);
            displayResults(rrResult, rrTable, rrGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runSRTF() {
        try {
            List<ProcessData> processes = getInputProcesses();
            srtfResult = SRTFAlgorithm.execute(processes);
            displayResults(srtfResult, srtfTable, srtfGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runBoth() {
        try {
            List<ProcessData> processes = getInputProcesses();
            int quantum = getQuantum();

            rrResult = RRAlgorithm.execute(processes, quantum);
            srtfResult = SRTFAlgorithm.execute(processes);

            displayResults(rrResult, rrTable, rrGanttPanel);
            displayResults(srtfResult, srtfTable, srtfGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(SchedulingResult result, JTable table, JPanel ganttPanel) {
        // Update table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (ProcessData p : result.processes) {
            model.addRow(new Object[]{
                p.id,
                p.arrivalTime,
                p.burstTime,
                p.completionTime,
                p.turnaroundTime,
                p.waitingTime,
                p.responseTime
            });
        }

        // Update Gantt chart
        ganttPanel.removeAll();
        ganttPanel.setLayout(new BorderLayout());
        ganttPanel.add(new GanttChartPanel(result.ganttChart), BorderLayout.CENTER);
        ganttPanel.revalidate();
        ganttPanel.repaint();
    }

    private void updateComparison() {
        if (rrResult == null || srtfResult == null) {
            comparisonArea.setText("Run both algorithms to see comparison.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-25s %-15s %-15s%n", "Metric", "Round Robin", "SRTF"));
        sb.append("=".repeat(55)).append("\n");
        sb.append(String.format("%-25s %-15.2f %-15.2f", "Avg Turnaround Time",
            rrResult.avgTurnaroundTime, srtfResult.avgTurnaroundTime));
        sb.append(rrResult.avgTurnaroundTime < srtfResult.avgTurnaroundTime ? " ← Better" :
                 (rrResult.avgTurnaroundTime > srtfResult.avgTurnaroundTime ? " → Better" : " (Tie)"));
        sb.append("\n");

        sb.append(String.format("%-25s %-15.2f %-15.2f", "Avg Waiting Time",
            rrResult.avgWaitingTime, srtfResult.avgWaitingTime));
        sb.append(rrResult.avgWaitingTime < srtfResult.avgWaitingTime ? " ← Better" :
                 (rrResult.avgWaitingTime > srtfResult.avgWaitingTime ? " → Better" : " (Tie)"));
        sb.append("\n");

        sb.append(String.format("%-25s %-15.2f %-15.2f", "Avg Response Time",
            rrResult.avgResponseTime, srtfResult.avgResponseTime));
        sb.append(rrResult.avgResponseTime < srtfResult.avgResponseTime ? " ← Better" :
                 (rrResult.avgResponseTime > srtfResult.avgResponseTime ? " → Better" : " (Tie)"));

        comparisonArea.setText(sb.toString());
    }

    // Inner class for Gantt Chart visualization
    private class GanttChartPanel extends JPanel {
        private List<GanttSlot> ganttChart;
        private Color[] processColors = {
            new Color(255, 179, 186),
            new Color(255, 223, 186),
            new Color(255, 255, 186),
            new Color(186, 255, 201),
            new Color(186, 225, 255),
            new Color(220, 186, 255),
            new Color(255, 186, 255),
            new Color(186, 255, 255)
        };

        public GanttChartPanel(List<GanttSlot> ganttChart) {
            this.ganttChart = ganttChart;
            setPreferredSize(new Dimension(600, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (ganttChart == null || ganttChart.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int maxTime = ganttChart.get(ganttChart.size() - 1).endTime;
            int width = getWidth() - 40;
            int height = 50;
            int y = 20;

            for (GanttSlot slot : ganttChart) {
                int x = 20 + (slot.startTime * width / maxTime);
                int slotWidth = ((slot.endTime - slot.startTime) * width / maxTime);

                // Draw slot
                if (slot.processId == 0) {
                    g2d.setColor(Color.LIGHT_GRAY);
                } else {
                    g2d.setColor(processColors[slot.processId % processColors.length]);
                }
                g2d.fillRect(x, y, slotWidth, height);

                // Draw border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, slotWidth, height);

                // Draw label
                String label = slot.processId == 0 ? "IDLE" : "P" + slot.processId;
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                if (slotWidth >= labelWidth + 4) {
                    g2d.drawString(label, x + (slotWidth - labelWidth) / 2, y + height / 2 + 5);
                }

                // Draw time markers
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String timeStr = String.valueOf(slot.startTime);
                g2d.drawString(timeStr, x - fm.stringWidth(timeStr) / 2, y + height + 15);
            }

            // Draw final time marker
            int finalX = 20 + width;
            String finalTime = String.valueOf(maxTime);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(finalTime, finalX - fm.stringWidth(finalTime) / 2, y + height + 15);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchedulerGUI gui = new SchedulerGUI();
            gui.setVisible(true);
        });
    }
}
