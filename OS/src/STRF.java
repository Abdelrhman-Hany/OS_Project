import java.util.*;

public class STRF {

    static int n;
    static String[] pid;
    static int[] arrival, burst, remaining, finishTime, responseTime;
    static boolean[] started, completed;
    static List<int[]> gantt = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        n = sc.nextInt();

        pid = new String[n];
        arrival = new int[n];
        burst = new int[n];
        remaining = new int[n];
        finishTime = new int[n];
        responseTime = new int[n];
        started = new boolean[n];
        completed = new boolean[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter PID for process " + (i + 1) + ": ");
            pid[i] = sc.next();
            System.out.print("Enter arrival time for " + pid[i] + ": ");
            arrival[i] = sc.nextInt();
            System.out.print("Enter burst time for " + pid[i] + ": ");
            burst[i] = sc.nextInt();
            remaining[i] = burst[i];
        }

        simulate();
        printResults();
        printGantt();
    }

    static void simulate() {
        int t = 0;
        int done = 0;
        int lastSelected = -1;
        int sliceStart = 0;

        while (done < n) {
            int selected = -1;

            for (int i = 0; i < n; i++) {
                if (!completed[i] && arrival[i] <= t) {
                    if (selected == -1 || remaining[i] < remaining[selected]) {
                        selected = i;
                    }
                }
            }

            if (selected == -1) {
                if (lastSelected != -1) {
                    gantt.add(new int[]{lastSelected, sliceStart, t});
                    lastSelected = -1;
                }
                t++;
                continue;
            }

            if (selected != lastSelected) {
                if (lastSelected != -1) {
                    gantt.add(new int[]{lastSelected, sliceStart, t});
                }
                sliceStart = t;
                lastSelected = selected;
            }

            if (!started[selected]) {
                responseTime[selected] = t - arrival[selected];
                started[selected] = true;
            }

            remaining[selected]--;
            t++;

            if (remaining[selected] == 0) {
                finishTime[selected] = t;
                completed[selected] = true;
                done++;
                gantt.add(new int[]{selected, sliceStart, t});
                lastSelected = -1;
                sliceStart = t;
            }
        }
    }

    static void printResults() {
        System.out.println();
        System.out.printf("%-6s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                "PID", "Arrival", "Burst", "CT", "TAT", "WT", "RT");

        double totalTAT = 0, totalWT = 0, totalRT = 0;

        for (int i = 0; i < n; i++) {
            int tat = finishTime[i] - arrival[i];
            int wt  = tat - burst[i];
            int rt  = responseTime[i];

            totalTAT += tat;
            totalWT  += wt;
            totalRT  += rt;

            System.out.printf("%-6s %-8d %-8d %-8d %-8d %-8d %-8d%n",
                    pid[i], arrival[i], burst[i], finishTime[i], tat, wt, rt);
        }

        System.out.println();
        System.out.printf("Average TAT: %.2f%n", totalTAT / n);
        System.out.printf("Average WT:  %.2f%n", totalWT  / n);
        System.out.printf("Average RT:  %.2f%n", totalRT  / n);
    }

    static void printGantt() {
        System.out.println();
        System.out.println("Gantt Chart:");
        System.out.println();

        StringBuilder top    = new StringBuilder();
        StringBuilder middle = new StringBuilder();
        StringBuilder bottom = new StringBuilder();
        StringBuilder times  = new StringBuilder();

        top.append("+");
        middle.append("|");
        bottom.append("+");

        for (int[] slot : gantt) {
            int index     = slot[0];
            int sliceStart = slot[1];
            int sliceEnd   = slot[2];
            int width      = Math.max((sliceEnd - sliceStart) * 2, pid[index].length() + 2);
            String label   = pid[index];

            top.append("-".repeat(width)).append("+");
            middle.append(center(label, width)).append("|");
            bottom.append("-".repeat(width)).append("+");
        }

        System.out.println(top);
        System.out.println(middle);
        System.out.println(bottom);

        times.append(gantt.get(0)[1]);
        for (int[] slot : gantt) {
            int sliceStart = slot[1];
            int sliceEnd   = slot[2];
            int width      = Math.max((sliceEnd - sliceStart) * 2, pid[slot[0]].length() + 2);
            String endStr  = String.valueOf(sliceEnd);
            int padding    = width - String.valueOf(sliceStart).length();
            times.append(" ".repeat(Math.max(0, padding))).append(endStr);
        }

        System.out.println(times);
        System.out.println();
    }

    static String center(String text, int width) {
        int padding = width - text.length();
        int left    = padding / 2;
        int right   = padding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}