import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
ToDo:
    Weitere Verfahren (nur Jobs unter bestimmter Dauer sortieren, nur an Freitagen kurze Jobs abarbeiten)
    Möglichkeit Urlaub zu nehmen?
*/
public class Werkstatt {
    static List<Mitarbeiter> alleMitarbeiter = new ArrayList<>();
    public static void main(String[] args) {
        Date dateStart = new Date();
        long timeStarted = dateStart.getTime();

        Mitarbeiter marc = new Mitarbeiter("Marc");
        alleMitarbeiter.add(marc);
        Mitarbeiter benno = new Mitarbeiter("Benno");
        alleMitarbeiter.add(benno);
        Mitarbeiter jakob = new Mitarbeiter("Jakob", 2);
        alleMitarbeiter.add(jakob);

        String[] files = new String[] {
                "./resources/fahrradwerkstatt0.txt",
                "./resources/fahrradwerkstatt1.txt",
                "./resources/fahrradwerkstatt2.txt",
                "./resources/fahrradwerkstatt3.txt",
                "./resources/fahrradwerkstatt4.txt"
        };
        workEverything(files);
        Date dateEnd = new Date();
        long timeEnded = dateEnd.getTime();
        System.out.println("Das Programm hat " + (timeEnded - timeStarted) + " Millisekunden gebraucht.");
    }

    public static void workEverything(String[] files) {
        for(String file : files){
            WorkYear finishedYear = workThroughYear(readJobList(file), false);
            analyzeYear(finishedYear);
            finishedYear = workThroughYear(readJobList(file), true);
            analyzeYear(finishedYear);
        }
    }

    public static WorkYear workThroughYear(List<Job> jobList, boolean sortedQueue) {
        List<Job> queue = new LinkedList<>();
        List<Job> endOfYearList = new LinkedList<>();
        int daysInYear = 365;
        int time = 0;
        int timeWasted = 0, timeWorked = 0; // -> Mitarbeiter Klasse




        // Wenn der letzte Eintrag nach dem 365sten Tag ankommt -> Schaltjahr
        if (jobList.get(jobList.size() - 1).getArrivalTime() > 525000) {
            daysInYear = 366;
        }


        for (int day = 1; day <= daysInYear; day++) {
            for (int hour = 0; hour < 24; hour++) {
                for (int minute = 0; minute < 60; minute++) {
                    queueNewJobs(jobList, queue, time);
                    if (sortedQueue) {
                        Collections.sort(queue);
                    }
                    for (Mitarbeiter mitarbeiter : alleMitarbeiter) {
                        if (!queue.isEmpty()){
                            Job nextJob = queue.get(0);
                            if (mitarbeiter.work(time, day, hour, nextJob) == nextJob) { // wenn der Job an dem der MA gerade arbeitet gleich dem ersten Eintrag der Queue ist
                                queue.remove(nextJob); // entferne den ersten Eintrag, da an diesem zu arbeiten begonnen wurde
                                nextJob.setWorkedBy(mitarbeiter.getName()); // speichere Namen des Bearbeiters im Job
                            }
                        } else {
                            mitarbeiter.work(time, day, hour, null); // kein nächster Eintrag in der Warteliste
                        }
                    }
                    time++;
                }
            }
        }
        // Aufräumarbeiten: Alle ausstehenden Aufträge in Liste eintragen
        for (Mitarbeiter mitarbeiter : alleMitarbeiter) {
            if (!mitarbeiter.getFinishedJobs().isEmpty()) {
                endOfYearList.addAll(mitarbeiter.getFinishedJobs());
            }
            if (mitarbeiter.getCurrentJob() != null) {
                endOfYearList.add(mitarbeiter.getCurrentJob());
            }
            timeWorked += mitarbeiter.getTimeWorked();
            timeWasted += mitarbeiter.getTimeWasted();
            System.out.println("--------------------------------------------------------------------------");
            System.out.println(mitarbeiter.getName() + ": Jobs erledigt: " + mitarbeiter.getNumberFinishedJobs());
            mitarbeiter.cleanUp();
        }

        if (!queue.isEmpty()) {
            endOfYearList.addAll(queue);
        }
        return new WorkYear(endOfYearList, time, timeWorked, timeWasted, sortedQueue);
    }

    private static void queueNewJobs(List<Job> jobList, List<Job> queue, int time) {
        List<Job> jobListCopy = new LinkedList<>(jobList);
        for(Job job : jobListCopy) {
            if(job.getArrivalTime() < time) {
                queue.add(job);
                jobList.remove(job);
            } else if(job.getArrivalTime() > time) {
                break;
            }
        }
    }

    public static void analyzeYear(WorkYear year){
        List<Job> list = year.finishedYear();
        StringBuilder unfinishedJobsIndizes = new StringBuilder();
        Job longestWaitingJob = list.get(0);
        int longestWaitingTime = 0, cumulatedWaitingTime = 0, averageWaitingTime, numberFinishedJobs = 0,
            numberUnfinishedJobs = 0, workForNextYear = 0;

        for (Job job : list) {
            if (job.getWorkLeft() < 1) { // Auftrag wurde beendet
                job.setWaitingTime(); // Methode um im Objekt die Wartezeit zu berechnen und setzen
                numberFinishedJobs++; // Addiere 1 zu abgeschlossenen Aufträgen
                cumulatedWaitingTime += job.getWaitingTime(); // Totale Summe aller Wartezeiten um später den Durchschnitt zu berechnen
                if (longestWaitingTime < job.getWaitingTime()) { // Ist diese Wartezeit als die bisherige längste Wartezeit?
                    longestWaitingTime = job.getWaitingTime(); // Ersetze bisherige Wartezeit mit dieser
                    longestWaitingJob = job; // dieser Job hatte die längste Wartezeit
                }
            } else { // Auftrag wurde nicht beendet
                numberUnfinishedJobs++; // Addiere 1 zu nicht abgeschlossenen Aufträgen
                workForNextYear += job.getWorkLeft(); // Addiere ausstehende Arbeitszeit zur Summe für nächstes Jahr
                unfinishedJobsIndizes.append(job.getIndex()+1).append(" ");
            }
        }

        averageWaitingTime = cumulatedWaitingTime/list.size();

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Verfahren: " + (year.sorted() ? "Sortiert" : "Unsortiert"));
        System.out.println("Anzahl Aufträge: " + list.size());
        System.out.println("Erledigte Aufträge: " + numberFinishedJobs);
        System.out.println("Unerledigte Aufträge: " + numberUnfinishedJobs);
        System.out.println("Indizes unerledigter Aufträge: " + unfinishedJobsIndizes);
        System.out.println("Dauer unerledigter Aufträge: " + toHours(workForNextYear));
        System.out.println("Tatsächlich gearbeitet: " + toHours(year.timeWorked()));
        System.out.println("Zeit verschwendet: " + toHours(year.timeWasted()));
        System.out.println("Wartezeit im Schnitt: " + toHours(averageWaitingTime));
        System.out.println("Längste Wartezeit: " + toHours(longestWaitingTime));
        System.out.println("Job mit längster Wartezeit: #" + longestWaitingJob.getIndex());
        System.out.println("Gesamtzeit: " + toHours(year.totalTime()));
    }

    public static List<Job> readJobList(String filename) {
        List<Job> outputList = new LinkedList<>();
        try (BufferedReader dataFile = new BufferedReader(new FileReader(filename))) {
            String LineInput;
            int jobIndex = 0;
            while ((LineInput = dataFile.readLine()) != null) {
                String[] LineArray = LineInput.split(" ");
                Job job = new Job(Integer.parseInt(LineArray[0]), Integer.parseInt(LineArray[1]), jobIndex);
                outputList.add(job);
                jobIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputList;
    }

    public static String toHours(int minutes) {
        int hours = minutes/60;
        int minutesRest = minutes%60;

        return hours + " Stunden und " + minutesRest + " Minuten";
    }
}

