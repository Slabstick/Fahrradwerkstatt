import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
ToDo: Weitere Verfahren (nur Jobs unter bestimmter Dauer sortieren, nur an Freitagen kurze Jobs abarbeiten),
Mehrere Mitarbeiter?
Schaltjahre beachten
Möglichkeit Urlaub zu nehmen?
*/
public class Werkstatt {

    public static void main(String[] args) {
        String[] files = new String[] {
                "./resources/fahrradwerkstatt0.txt",
                "./resources/fahrradwerkstatt1.txt",
                "./resources/fahrradwerkstatt2.txt",
                "./resources/fahrradwerkstatt3.txt",
                "./resources/fahrradwerkstatt4.txt",
                "./resources/fahrradwerkstatt5.txt",
        };
        workEverything(files);
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
        int time = 0;
        int daysInYear = 366; // Schaltjahrabfrage einbauen;
        Job activeJob = null;
        int timeWasted = 0;
        int timeWorked = 0;
        int freeTime = 0;
        int totalWorkTime = 0;

        for(int day = 1; day <= daysInYear; day++) { // Tage
            if(((day+1) % 7 != 0) && (day % 7 != 0)){ // Wochenenden ausgeschlossen
                for(int hour = 0; hour < 24; hour++) { // Stunden
                    if((hour >= 9) && (hour < 17)){ // Arbeitszeit von 9-17 (16:59 letzte Arbeitsminute)
                        for(int minute = 0; minute < 60; minute++) { // Minuten
                            if (activeJob != null) { // ein Auftrag ist aktiv
                                activeJob.chip(); // streiche eine Minute von der verbleibenden Arbeitszeit des Auftrags
                                time++; // addiere eine Minute zur tatsächlichen Zeit
                                timeWorked++;
                                totalWorkTime++;
                                if(activeJob.getWorkLeft() < 1) { // ist verbleibende Arbeitszeit kleiner als 1 Minute?
                                    activeJob.setTimeFinished(time); // speichere im Job Objekt die genaue Abschlusszeit
                                    endOfYearList.add(activeJob);
                                    activeJob = null; // lösche abgearbeiteten Auftrag aus der Variable activeJob
                                }
                            } else { // kein Auftrag aktiv, nächster Auftrag wird begonnen
                                if (!queue.isEmpty()) { // Warteliste hat Einträge
                                    if (sortedQueue) { // falls Warteliste sortiert werden soll
                                        Collections.sort(queue);
                                    }
                                    activeJob = queue.get(0); // ersten Eintrag der Warteliste als aktiven Job setzen.
                                    queue.remove(0); // aktiven Job von Warteliste streichen
                                }

                                // Warteliste wird abgesucht nach nächstem Auftrag
                                List<Job> tempList = new LinkedList<>(yearList); // shallow copy, um Elemente löschen zu können
                                for (Job job : tempList) { // Liste absuchen nach inaktiven Jobs, die anstehen und zur Warteliste hinzufügen
                                    if (job.getArrivalTime() < time) { // job inaktiv, aber fällig
                                        queue.add(job);
                                        yearList.remove(job); // job wird von der originalen Liste entfernt
                                    } else if (job.getArrivalTime() > time) { // Punkt in der Liste erreicht, der in der Zukunft liegt
                                        break;
                                    }

                                }
                                timeWasted++;
                                totalWorkTime++;
                                time = time + 1; // Arbeitszeit läuft minutiös ab, da Werktag zwischen 9 und 17 Uhr
                            }
                        }
                    } else {
                        freeTime += 60;
                        time = time + 60; // Werktag, aber außerhalb Arbeitszeit läuft stündlich ab
                    }
                }
            } else {
                freeTime += 1440;
                time = time + 1440; // Wochenendtag, daher progressed Zeit um einen ganzen Tag.

            }
        }
        // Aufräumarbeiten: Alle ausstehenden Aufträge in Liste eintragen
        if(activeJob != null) {
            endOfYearList.add(activeJob);
        }
        if(!queue.isEmpty()) {
            endOfYearList.addAll(queue);
        }
        if(!yearList.isEmpty()) {
            endOfYearList.addAll(yearList);
        }
        return new WorkYear(endOfYearList, time, timeWorked, timeWasted, freeTime, totalWorkTime, sortedQueue);
    }

    public static void analyzeYear(WorkYear year){
        List<Job> list = year.finishedYear();
        int longestWaitingTime = 0;
        Job longestWaitingJob = list.get(0);
        int cumulatedWaitingTime = 0;
        int averageWaitingTime;
        int numberFinishedJobs = 0;
        int numberUnfinishedJobs = 0;
        int workForNextYear = 0;

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

