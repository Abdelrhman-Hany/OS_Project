import java.util.*;

public class RRAlgorithm {

    static class InternalProcess {
        int id;
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int completionTime;
        int turnaroundTime;
        int waitingTime;
        int responseTime;
        boolean started;
    }

    public static SchedulingResult execute(List<ProcessData> inputProcesses, int quantum) {
        int n = inputProcesses.size();
        List<InternalProcess> processes = new ArrayList<>();

        // Convert input to internal format
        for (ProcessData pd : inputProcesses) {
            InternalProcess p = new InternalProcess();
            p.id = pd.id;
            p.arrivalTime = pd.arrivalTime;
            p.burstTime = pd.burstTime;
            p.remainingTime = pd.burstTime;
            p.started = false;
            processes.add(p);
        }

        Queue<Integer> readyQueue = new LinkedList<>();
        boolean[] inQueue = new boolean[n];
        List<GanttSlot> gantt = new ArrayList<>();
        int currentTime = 0;
        int completed = 0;

        // Add processes that arrive at time 0
        for (int i = 0; i < n; i++) {
            if (processes.get(i).arrivalTime == 0) {
                readyQueue.add(i);
                inQueue[i] = true;
            }
        }

        while (completed < n) {
            // Handle idle time
            if (readyQueue.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                int nextIdx = -1;
                for (int i = 0; i < n; i++) {
                    if (!inQueue[i] && processes.get(i).remainingTime > 0
                            && processes.get(i).arrivalTime < nextArrival) {
                        nextArrival = processes.get(i).arrivalTime;
                        nextIdx = i;
                    }
                }
                gantt.add(new GanttSlot(0, currentTime, nextArrival));
                currentTime = nextArrival;
                readyQueue.add(nextIdx);
                inQueue[nextIdx] = true;
            }

            int idx = readyQueue.poll();
            int runTime = Math.min(quantum, processes.get(idx).remainingTime);
            int slotStart = currentTime;

            // Set response time on first execution
            if (!processes.get(idx).started) {
                processes.get(idx).responseTime = currentTime - processes.get(idx).arrivalTime;
                processes.get(idx).started = true;
            }

            currentTime += runTime;
            processes.get(idx).remainingTime -= runTime;
            gantt.add(new GanttSlot(processes.get(idx).id, slotStart, currentTime));

            // Add newly arrived processes to queue
            for (int i = 0; i < n; i++) {
                if (!inQueue[i]
                        && processes.get(i).arrivalTime <= currentTime
                        && processes.get(i).remainingTime > 0) {
                    readyQueue.add(i);
                    inQueue[i] = true;
                }
            }

            // Re-add current process if not finished
            if (processes.get(idx).remainingTime > 0) {
                readyQueue.add(idx);
            } else {
                processes.get(idx).completionTime = currentTime;
                completed++;
            }
        }

        // Calculate turnaround and waiting times
        List<ProcessData> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            InternalProcess p = processes.get(i);
            ProcessData result = new ProcessData(p.id, p.arrivalTime, p.burstTime);
            result.completionTime = p.completionTime;
            result.turnaroundTime = p.completionTime - p.arrivalTime;
            result.waitingTime = result.turnaroundTime - p.burstTime;
            result.responseTime = p.responseTime;
            results.add(result);
        }

        return new SchedulingResult(results, gantt);
    }
}
