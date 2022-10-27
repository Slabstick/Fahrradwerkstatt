/*
    Mitarbeiterklasse, um Mitarbeiter einzustellen
    Jeder Mitarbeiter braucht Namen, Arbeitszeit (Von wann bis wann er arbeitet - togglet evtl. durch %tualen Wert)
        100%: 5 Tage
        80%: 4 Tage
        60%: 3 Tage
        40%: 2 Tage
        20%: 1 Tag
        -> numberWeekdays (1-5)
    Schleife aus Werkstatt Klasse, die die Jobs abarbeitet muss hierhin verschoben werden.
    Werkstatt übergibt Zeit(? Zeit ist Teil der Schleife: Zeitlogik muss von Joblogik getrennt werden)
        und Jobs an Mitarbeiter
    Mitarbeiter übergibt fertige Jobs an Werkstatt
 */

import java.util.*;

public class Mitarbeiter {
    private final String name;
    private int numberWeekdays;
    private Job currentJob;
    private int timeWorked;
    private int timeWasted;
    private int numberFinishedJobs;
    private final List<Job> finishedJobs;

    public Mitarbeiter(String name, int numberWeekdays) {
        this.name = name;
        setNumberWeekdays(numberWeekdays);
        this.timeWorked = 0;
        this.timeWasted = 0;
        finishedJobs = new LinkedList<>();
        this.numberFinishedJobs = 0;
    }

    public Mitarbeiter(String name) {
        this(name, 5);
    }

    public List<Job> getFinishedJobs() {
        return finishedJobs;
    }

    public int getTimeWorked() {
        return timeWorked;
    }

    public int getTimeWasted() {
        return timeWasted;
    }


    public String getName() {
        return name;
    }

    public void setNumberWeekdays(int numberWeekdays) {
        if(numberWeekdays > 5) {
            this.numberWeekdays = 5;
        } else if(numberWeekdays < 1) {
            this.numberWeekdays = 1;
        } else {
            this.numberWeekdays = numberWeekdays;
        }
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public int getNumberFinishedJobs() {
        return numberFinishedJobs;
    }

    public void setNumberFinishedJobs(int numberFinishedJobs) {
        this.numberFinishedJobs = numberFinishedJobs;
    }

    public void cleanUp(){
        finishedJobs.clear();
        setCurrentJob(null);
        setNumberFinishedJobs(0);
    }

    public boolean isWorkday(int day) {
        if (day % 7 == 0) { // Sonntag
            return false;
        }
        if ((day + 1) % 7 == 0) { // Samstag
            return false;
        }
        if (((day + 2) % 7 == 0) && this.numberWeekdays <= 4) { // Freitag
            return false;
        }
        if (((day + 3) % 7 == 0) && this.numberWeekdays <= 3) { // Donnerstag
            return false;
        }
        if (((day + 4) % 7 == 0) && this.numberWeekdays <= 2) { // Mittwoch
            return false;
        }
        if (((day + 5) % 7 == 0) && this.numberWeekdays <= 1) { // Dienstag
            return false;
        }
        return true;
    }

    public Job work(int time, int day, int hour, Job nextJob) {
        if ((isWorkday(day)) && ((hour >= 9) && (hour < 17))) {
            if (this.currentJob != null) {
                this.currentJob.chip();
                timeWorked++;
                if(currentJob.getWorkLeft() < 1) {
                    currentJob.setTimeFinished(time);
                    finishedJobs.add(currentJob);
                    currentJob = null;
                    setNumberFinishedJobs(this.numberFinishedJobs+1);
                }
            } else if (nextJob != null){
                setCurrentJob(nextJob);

            } else { // currentJob und nextJob beide null (nix zu tun)
                timeWasted++;
            }
        }
        return currentJob;
    }
}
