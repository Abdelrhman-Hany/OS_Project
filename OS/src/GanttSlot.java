public class GanttSlot {
    public int processId;
    public int startTime;
    public int endTime;

    public GanttSlot(int processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
