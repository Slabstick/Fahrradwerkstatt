public class Testsuite {
    public static void main(String[] args) {
        Mitarbeiter marc = new Mitarbeiter("Marc",4);
        for(int i = 1; i <= 14; i++){
            System.out.println(i + ": " + marc.isWorkday(i));
        }
    }
}
