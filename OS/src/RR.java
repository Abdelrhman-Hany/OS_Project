import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class RR {

    static class Process {
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

    static class GanttSlot {
        int processId;
        int startTime;
        int endTime;
        GanttSlot(int pid, int s, int e) { processId = pid; startTime = s; endTime = e; }
    }

    static void printQueue(Queue<Integer> queue, List<Process> processes) {
        if (queue.isEmpty()) { System.out.println("[ Empty ]"); return; }
        System.out.print("[ ");
        List<Integer> temp = new ArrayList<>(queue);
        for (int i = 0; i < temp.size(); i++) {
            System.out.print("P" + processes.get(temp.get(i)).id);
            if (i < temp.size() - 1) System.out.print(", ");
        }
        System.out.println(" ]");
    }

    static void printGantt(List<GanttSlot> gantt) {
        System.out.println("\n--- Gantt Chart ---");
        System.out.print("+");
        for (GanttSlot slot : gantt) {
            int width = (slot.endTime - slot.startTime) * 2 + 1;
            for (int i = 0; i < width; i++) System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
        System.out.print("|");
        for (GanttSlot slot : gantt) {
            int width = (slot.endTime - slot.startTime) * 2 + 1;
            String label = slot.processId == 0 ? "IDLE" : "P" + slot.processId;
            int pad = width - label.length();
            int left = pad / 2;
            int right = pad - left;
            for (int i = 0; i < left; i++) System.out.print(" ");
            System.out.print(label);
            for (int i = 0; i < right; i++) System.out.print(" ");
            System.out.print("|");
        }
        System.out.println();
        System.out.print("+");
        for (GanttSlot slot : gantt) {
            int width = (slot.endTime - slot.startTime) * 2 + 1;
            for (int i = 0; i < width; i++) System.out.print("-");
            System.out.print("+");
        }
        System.out.println();
        System.out.print(gantt.get(0).startTime);
        for (GanttSlot slot : gantt) {
            int width = (slot.endTime - slot.startTime) * 2 + 1;
            String t = String.valueOf(slot.endTime);
            int spaces = width + 1 - t.length();
            for (int i = 0; i < spaces; i++) System.out.print(" ");
            System.out.print(t);
        }
        System.out.println();
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Time Quantum: ");
        int quantum = sc.nextInt();
        if (quantum <= 0) {                                          // Quantum must be positive
            System.out.println("Error: Time quantum must be > 0.");
            sc.close();
            return;
        }

        System.out.print("Number of Processes: ");
        int n = sc.nextInt();
        if (n <= 0) {                                                // Must have at least one process
            System.out.println("Error: Number of processes must be > 0.");
            sc.close();
            return;
        }

        List<Process> processes = new ArrayList<>();
        List<Integer> usedIds   = new ArrayList<>();                 // Tracks IDs already entered

        for (int i = 0; i < n; i++) {
            Process p = new Process();

            System.out.print("PID for process " + (i + 1) + ": ");
            int pid = sc.nextInt();

            if (usedIds.contains(pid)) {                            // Duplicate PID check
                System.out.println("Error: PID " + pid + " already exists. Each process must have a unique ID.");
                sc.close();
                return;
            }
            usedIds.add(pid);
            p.id = pid;

            System.out.print("Arrival Time for P" + p.id + ": ");
            int arrival = sc.nextInt();
            if (arrival < 0) {                                       // Negative arrival time check
                System.out.println("Error: Arrival time cannot be negative (P" + p.id + ").");
                sc.close();
                return;
            }
            p.arrivalTime = arrival;

            System.out.print("Burst Time for P" + p.id + ": ");
            int burst = sc.nextInt();
            if (burst <= 0) {                                        // Burst time must be positive check
                System.out.println("Error: Burst time must be > 0 (P" + p.id + ").");
                sc.close();
                return;
            }
            p.burstTime     = burst;
            p.remainingTime = burst;
            p.started       = false;
            processes.add(p);
        }

        Queue<Integer> readyQueue = new LinkedList<>();
        boolean[] inQueue = new boolean[n];
        List<GanttSlot> gantt = new ArrayList<>();
        int currentTime = 0;
        int completed   = 0;

        for (int i = 0; i < n; i++) {
            if (processes.get(i).arrivalTime == 0) {
                readyQueue.add(i);
                inQueue[i] = true;
            }
        }

        System.out.println("\nQueue updates:");
        printQueue(readyQueue, processes);

        while (completed < n) {

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
                printQueue(readyQueue, processes);
            }

            int idx       = readyQueue.poll();
            int runTime   = Math.min(quantum, processes.get(idx).remainingTime);
            int slotStart = currentTime;

            if (!processes.get(idx).started) {
                processes.get(idx).responseTime = currentTime - processes.get(idx).arrivalTime;
                processes.get(idx).started = true;
            }

            currentTime += runTime;
            processes.get(idx).remainingTime -= runTime;
            gantt.add(new GanttSlot(processes.get(idx).id, slotStart, currentTime));

            for (int i = 0; i < n; i++) {
                if (!inQueue[i]
                        && processes.get(i).arrivalTime <= currentTime
                        && processes.get(i).remainingTime > 0) {
                    readyQueue.add(i);
                    inQueue[i] = true;
                }
            }

            if (processes.get(idx).remainingTime > 0) {
                readyQueue.add(idx);
            } else {
                processes.get(idx).completionTime = currentTime;
                completed++;
            }

            printQueue(readyQueue, processes);
        }

        printGantt(gantt);

        System.out.println("\nProcess  Arrival  Burst  Completion  Turnaround  Waiting  Response");

        int totalTAT = 0, totalWT = 0, totalRT = 0;

        for (int i = 0; i < n; i++) {
            Process p        = processes.get(i);
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime    = p.turnaroundTime - p.burstTime;
            totalTAT += p.turnaroundTime;
            totalWT  += p.waitingTime;
            totalRT  += p.responseTime;
            System.out.printf("P%-7d %-8d %-6d %-11d %-11d %-8d %d%n",
                    p.id, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnaroundTime, p.waitingTime, p.responseTime);
        }

        System.out.printf("%nAverage Turnaround Time : %.1f%n", (float) totalTAT / n);
        System.out.printf("Average Waiting Time    : %.1f%n",   (float) totalWT  / n);
        System.out.printf("Average Response Time   : %.1f%n",   (float) totalRT  / n);

        sc.close();
    }
}