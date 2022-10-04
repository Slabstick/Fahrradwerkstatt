public class Job implements Comparable<Job>{
    private final int arrivalTime;
    private final int duration;
    private final int index;
    private int workLeft;
    private int timeFinished;
    private int waitingTime;

    public Job(int arrivalTime, int duration, int index) {
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.index = index;
        this.workLeft = duration;
        this.timeFinished = -1;
        this.waitingTime = 0;
    }

    public int getIndex() {
        return index;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDuration() {
        return duration;
    }


    public void chip() {
        this.workLeft = this.workLeft - 1;
    }

    public int getWorkLeft() {
        return workLeft;
    }

    public void setTimeFinished(int timeFinished) {
        this.timeFinished = timeFinished;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime() {
        this.waitingTime = this.timeFinished - this.arrivalTime;
    }

    @Override
    public int compareTo(Job o) {
        if(this.duration == o.getDuration()) {
            return 0;
        }
        if(this.duration > o.getDuration()) {
            return 1;
        } else {
            return -1;
        }
    }
}
