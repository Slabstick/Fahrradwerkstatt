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

public class Mitarbeiter {
    private String name;
    private int numberWeekdays;
    private Job currentJob;

    public Mitarbeiter(String name, int numberWeekdays) {
        this.name = name;
        setNumberWeekdays(numberWeekdays);
    }

    public Mitarbeiter(String name) {
        new Mitarbeiter(name, 5);
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



}
