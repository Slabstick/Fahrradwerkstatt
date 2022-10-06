import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Benchmark {
    public static void main(String[] args) {
        Date dateStart = new Date();
        String[] files = new String[] {
                "./resources/fahrradwerkstatt0.txt",
                "./resources/fahrradwerkstatt1.txt",
                "./resources/fahrradwerkstatt2.txt",
                "./resources/fahrradwerkstatt3.txt",
                "./resources/fahrradwerkstatt4.txt"
        };

        WorkYear workYear = Werkstatt.workThroughYear(Werkstatt.readJobList(files[0]), true);

        Werkstatt.analyzeYear(workYear);

        Date dateEnd = new Date();
        long timeToEnd = dateEnd.getTime() - dateStart.getTime();

        System.out.println("Das Programm hat " + timeToEnd + " Millisekunden gebraucht!");
    }

}
