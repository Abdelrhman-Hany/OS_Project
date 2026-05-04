public class ProcessData {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int completionTime;
    public int turnaroundTime;
    public int waitingTime;
    public int responseTime;

    public ProcessData(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}
