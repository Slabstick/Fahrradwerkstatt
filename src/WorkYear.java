import java.util.List;

public class WorkYear {
    private final List<Job> finishedYear;
    private final int totalTime;
    private final int timeWorked;
    private final int timeWasted;
    private final int freeTime;
    private final int totalWorkTime;
    private final boolean sorted;

    public WorkYear(List<Job> finishedYear, int totalTime, int timeWorked, int timeWasted, int freeTime, int totalWorkTime, boolean sorted) {
        this.finishedYear = finishedYear;
        this.totalTime = totalTime;
        this.timeWorked = timeWorked;
        this.timeWasted = timeWasted;
        this.freeTime = freeTime;
        this.totalWorkTime = totalWorkTime;
        this.sorted = sorted;
    }

    public List<Job> getFinishedYear() {
        return finishedYear;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalWorkTime() {
        return totalWorkTime;
    }

    public int getTimeWorked() {
        return timeWorked;
    }

    public int getTimeWasted() {
        return timeWasted;
    }

    public int getFreeTime() {
        return freeTime;
    }

    public boolean isSorted() {
        return sorted;
    }
}
