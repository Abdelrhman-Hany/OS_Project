import java.util.*;

public class SRTFAlgorithm {

    public static SchedulingResult execute(List<ProcessData> inputProcesses) {
        int n = inputProcesses.size();

        int[] arrival = new int[n];
        int[] burst = new int[n];
        int[] remaining = new int[n];
        int[] finishTime = new int[n];
        int[] responseTime = new int[n];
        boolean[] started = new boolean[n];
        boolean[] completed = new boolean[n];
        List<GanttSlot> gantt = new ArrayList<>();

        // Convert input to arrays
        for (int i = 0; i < n; i++) {
            arrival[i] = inputProcesses.get(i).arrivalTime;
            burst[i] = inputProcesses.get(i).burstTime;
            remaining[i] = burst[i];
        }

        int t = 0;
        int done = 0;
        int lastSelected = -1;
        int sliceStart = 0;

        while (done < n) {
            int selected = -1;

            // Find process with shortest remaining time
            for (int i = 0; i < n; i++) {
                if (!completed[i] && arrival[i] <= t) {
                    if (selected == -1 || remaining[i] < remaining[selected]) {
                        selected = i;
                    }
                }
            }

            // Handle idle time
            if (selected == -1) {
                if (lastSelected != -1) {
                    gantt.add(new GanttSlot(inputProcesses.get(lastSelected).id, sliceStart, t));
                    lastSelected = -1;
                }
                t++;
                continue;
            }

            // If switching to a different process, save previous gantt slot
            if (selected != lastSelected) {
                if (lastSelected != -1) {
                    gantt.add(new GanttSlot(inputProcesses.get(lastSelected).id, sliceStart, t));
                }
                sliceStart = t;
                lastSelected = selected;
            }

            // Set response time on first execution
            if (!started[selected]) {
                responseTime[selected] = t - arrival[selected];
                started[selected] = true;
            }

            remaining[selected]--;
            t++;

            // Check if process completed
            if (remaining[selected] == 0) {
                finishTime[selected] = t;
                completed[selected] = true;
                done++;
                gantt.add(new GanttSlot(inputProcesses.get(selected).id, sliceStart, t));
                lastSelected = -1;
                sliceStart = t;
            }
        }

        // Build results
        List<ProcessData> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ProcessData result = new ProcessData(
                inputProcesses.get(i).id,
                arrival[i],
                burst[i]
            );
            result.completionTime = finishTime[i];
            result.turnaroundTime = finishTime[i] - arrival[i];
            result.waitingTime = result.turnaroundTime - burst[i];
            result.responseTime = responseTime[i];
            results.add(result);
        }

        return new SchedulingResult(results, gantt);
    }
}
