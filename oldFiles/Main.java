import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Job> yearOne = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt0.txt"));
        Map<Integer, Job> yearTwo = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt1.txt"));
        Map<Integer, Job> yearThree = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt2.txt"));
        Map<Integer, Job> yearFour = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt3.txt"));
        Map<Integer, Job> yearFive = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt4.txt"));
        Map<Integer, Job> yearOwn = new LinkedHashMap<>(readJobMap("resources/fahrradwerkstatt5.txt"));
        List<Job> listOne = new LinkedList<>(readJobList("resources/fahrradwerkstatt0.txt"));
        List<Job> listTwo = new LinkedList<>(readJobList("resources/fahrradwerkstatt1.txt"));
        List<Job> listThree = new LinkedList<>(readJobList("resources/fahrradwerkstatt2.txt"));
        List<Job> listFour = new LinkedList<>(readJobList("resources/fahrradwerkstatt3.txt"));
        List<Job> listFive= new LinkedList<>(readJobList("resources/fahrradwerkstatt4.txt"));
        List<Job> listOwn = new LinkedList<>(readJobList("resources/fahrradwerkstatt5.txt"));

        Date startTime = new Date();

        System.out.println("Liste 0:");
        byArrival(yearOne);
        byDuration(listOne);
        System.out.println("Liste 1:");
        byArrival(yearTwo);
        byDuration(listTwo);
        System.out.println("Liste 2:");
        byArrival(yearThree);
        byDuration(listThree);
        System.out.println("Liste 3:");
        byArrival(yearFour);
        byDuration(listFour);
        System.out.println("Liste 4:");
        byArrival(yearFive);
        byDuration(listFive);
        System.out.println("Liste 5:");
        byArrival(yearOwn);
        byDuration(listOwn);

        Date finishedTime = new Date();

        long duration = finishedTime.getTime() - startTime.getTime();

        System.out.println("Das Programm lief " + duration + " millisekunden lang."); // 1231 ohne Verbesserung

    }

    public static void byArrival(Map<Integer, Job> data) {

        int completion = 0;
        int maxWaitingTime = 0;
        int cumulatedWaitingTime = 0;
        int numberJobs = 0;
        int maxWaitingTimeJobIndex = 0;

        for (Map.Entry<Integer, Job> job : data.entrySet()) { // Loop durch alle Jobs
            int arrivalTime = job.getValue().getArrivalTime(); // Auftragseingang aus job einlesen
            int duration = job.getValue().getDuration(); // Auftragsdauer aus job einlesen
            if (completion < arrivalTime) { // Zeit der letzten Fertigstellung ist kleiner als die Eingangszeit (Letzter Auftrag ist abgeschlossen)
                completion = arrivalTime + duration; // Fertigstellung = Eingangszeit plus Dauer (ersetzt alte Fertigstellungszeit)
            } else { // Zeit der letzten Fertigstellung ist gleich oder größer als die Eingangszeit (letzter Auftrag dauert noch an)
                completion += duration; // Fertigstellung plus Dauer ist neue Fertigstellung, da erst nach letzter Fertigstellung angefangen werden kann.
            }
            int waitingTime = completion - arrivalTime; // Wartezeit = Fertigstellung minus Eingangszeit
            if (waitingTime > maxWaitingTime) { // Wartezeit des aktuellen Jobs ist länger als bisherige längste Wartezeit
                maxWaitingTime = waitingTime;
                maxWaitingTimeJobIndex = job.getKey();
            }
            cumulatedWaitingTime += waitingTime; // Wartezeit kumuliert für alle bisherigen Aufträge
            numberJobs++; // Anzahl der Aufträge plus 1
        }
        int averageWaitingTime = cumulatedWaitingTime/numberJobs; // Nach dem Loop wird die durchschnittliche Wartezeit berechnet

        System.out.println("NACH EINGANG:");
        System.out.println("Die Anzahl aller Aufträge betrug " + numberJobs + ".");
        System.out.println("Die längste Wartezeit hatte Job #" + (maxWaitingTimeJobIndex+1) + " mit " + maxWaitingTime + " Minuten. (~" + maxWaitingTime/60 + " Stunden)");
        System.out.println("Die durchschnittliche Wartezeit für alle Aufträge betrug: " + averageWaitingTime + " Minuten. (~" + averageWaitingTime/60 + " Stunden)");

    }

    public static void byDuration(List<Job> list) {
        int time = 0;
        List<Job> queue = new LinkedList<>();
        int longestWaitingJob = 0;
        int longestWaitingTime = 0;
        int cumulatedWaitingTime = 0;
        int numberJobs = 0;
        List<Job> markedForRemoval = new LinkedList<>();
        while (time < 527000) {

//            for(Job job : list) {
//                if(job.isFinished()) {
//                    markedForRemoval.add(job);
//                }
//            }

//            if(!markedForRemoval.isEmpty()) {
//                for(Job job : markedForRemoval) {
//                    list.remove(job);
//                }
//            }
//            markedForRemoval.clear();

            if(!list.isEmpty()) {
                for (Job job : list) {
                    if (job.getArrivalTime() == time) { // Eingang ist exakt gleich Zeit
                        time += job.getDuration(); // Auftrag wird sofort abgearbeitet
                        int waitingTime = job.getDuration(); // Wartezeit beträgt die Auftragsdauer, da sofort ausgeführt
                        cumulatedWaitingTime += waitingTime;
                        if (longestWaitingTime < waitingTime) {
                            longestWaitingTime = waitingTime;
                            longestWaitingJob = job.getIndex();
                        }
                        job.setFinished(true);
                        numberJobs++;
                    }
                    if ((job.getArrivalTime() < time) && !job.isFinished()) { // Zeit ist später als Eingangszeit und Auftrag ist noch nicht abgeschlossen
                        queue.add(job); // mindestens ein Auftrag wurde übersprungen
                    }

                    if (job.getArrivalTime() > time) { // Eingang ist nach aktueller Zeit.
                        break; // spart 50% Zeit ein, da die Schleife nicht nutzlos weiter laufen muss
                    }

                }


                while (!queue.isEmpty()) { // Solange sich Aufträge in der Warteschlange befinden:
                    Collections.sort(queue); // Sortiere nach Dauer
                    time += queue.get(0).getDuration(); // Addiere die Dauer des kürzesten zur Zeit (abarbeiten)
                    list.get(queue.get(0).getIndex()).setFinished(true); // Stelle den Auftrag auf finished
                    numberJobs++;
                    int waitingTime = time - queue.get(0).getArrivalTime(); //Wartezeit = aktuelle Zeit - Eingangszeit
                    cumulatedWaitingTime += waitingTime; // Addiere Wartezeit zur kumulierten Wartezeit
                    if (longestWaitingTime < waitingTime) {
                        longestWaitingTime = waitingTime;
                        longestWaitingJob = queue.get(0).getIndex();
                    }
                    queue.remove(0);
                }
            }
            time++;
        }

        int averageWaitingTime = cumulatedWaitingTime / list.size(); // kumulierte Wartezeit durch Anzahl der Aufträge

        System.out.println("NACH DAUER:");
        System.out.println("Die Anzahl aller Aufträge betrug: " + list.size() + " = " + numberJobs);
        System.out.println("Die längste Wartezeit hatte Auftrag #" + (longestWaitingJob + 1) + " mit " + longestWaitingTime + " Minuten! (~" + longestWaitingTime/60 + " Stunden)");
        System.out.println("Die durchschnittliche Wartezeit betrug: " + averageWaitingTime + " Minuten! (~" + averageWaitingTime/60 + " Stunden)");
        System.out.println("___________________________________________________");
    }

    public static Map<Integer, Job> readJobMap(String filename) {
        Map<Integer, Job> outputList = new LinkedHashMap<>();
        try (BufferedReader dataFile = new BufferedReader(new FileReader(filename))) {
            String LineInput;
            int jobIndex = 0;
            while ((LineInput = dataFile.readLine()) != null) {
                String[] LineArray = LineInput.split(" ");
                Job job = new Job(Integer.parseInt(LineArray[0]), Integer.parseInt(LineArray[1]), jobIndex);
                outputList.put(jobIndex, job);
                jobIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputList;
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
}