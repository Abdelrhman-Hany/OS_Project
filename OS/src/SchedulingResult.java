import java.util.List;

public class SchedulingResult {
    public List<ProcessData> processes;
    public List<GanttSlot> ganttChart;
    public double avgTurnaroundTime;
    public double avgWaitingTime;
    public double avgResponseTime;

    public SchedulingResult(List<ProcessData> processes, List<GanttSlot> ganttChart) {
        this.processes = processes;
        this.ganttChart = ganttChart;
        calculateAverages();
    }

    private void calculateAverages() {
        int n = processes.size();
        int totalTAT = 0, totalWT = 0, totalRT = 0;

        for (ProcessData p : processes) {
            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;
            totalRT += p.responseTime;
        }

        avgTurnaroundTime = (double) totalTAT / n;
        avgWaitingTime = (double) totalWT / n;
        avgResponseTime = (double) totalRT / n;
    }
}
