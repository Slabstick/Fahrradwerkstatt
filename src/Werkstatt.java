import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/*
ToDo:
    Weitere Verfahren (nur Jobs unter bestimmter Dauer sortieren, nur an Freitagen kurze Jobs abarbeiten)
    Mehrere Mitarbeiter?
    Möglichkeit Urlaub zu nehmen?
*/
public class Werkstatt {

    public static void main(String[] args) {
        Date dateStart = new Date();
        long timeStarted = dateStart.getTime();
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

    public static WorkYear workThroughYear(List<Job> yearList, boolean sortedQueue) {
        List<Job> queue = new LinkedList<>();
        List<Job> endOfYearList = new LinkedList<>();
//        Job activeJob = null; -> currentJob in Mitarbeiter Klasse
        int daysInYear = 365;
        int time = 0;
        int timeWasted = 0, timeWorked = 0, freeTime= 0, totalWorkTime = 0; // -> Mitarbeiter Klasse

        // Wenn der letzte Eintrag nach dem 365sten Tag ankommt -> Schaltjahr
        if (yearList.get(yearList.size() - 1).getArrivalTime() > 525000) {
            daysInYear = 366;
        }

        for (int day = 1; day <= daysInYear; day++) {

            for (int hour = 0; hour < 24; hour++) {

                for (int minute = 0; minute < 60; minute ++) {



                    time ++;


                }

            }


        }
        // Aufräumarbeiten: Alle ausstehenden Aufträge in Liste eintragen


        return new WorkYear(endOfYearList, time, timeWorked, timeWasted, freeTime, totalWorkTime, sortedQueue);
    }

    public static void analyzeYear(WorkYear year){
        List<Job> list = year.finishedYear();
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
            }
        }

        averageWaitingTime = cumulatedWaitingTime/list.size();

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Verfahren: " + (year.sorted() ? "Sortiert" : "Unsortiert"));
        System.out.println("Anzahl Aufträge: " + list.size());
        System.out.println("Erledigte Aufträge: " + numberFinishedJobs);
        System.out.println("Unerledigte Aufträge: " + numberUnfinishedJobs);
        System.out.println("Dauer unerledigter Aufträge: " + toHours(workForNextYear));
        System.out.println("Gesamte Arbeitszeit: " + toHours(year.totalWorkTime()));
        System.out.println("Tatsächlich gearbeitet: " + toHours(year.timeWorked()));
        System.out.println("Zeit verschwendet: " + toHours(year.timeWasted()));
        System.out.println("Wartezeit im Schnitt: " + toHours(averageWaitingTime));
        System.out.println("Längste Wartezeit: " + toHours(longestWaitingTime));
        System.out.println("Job mit längster Wartezeit: #" + longestWaitingJob.getIndex());
        System.out.println("Gesamtzeit: " + toHours(year.totalTime()));
        System.out.println("Freizeit: " + toHours(year.freeTime()));
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

