import java.util.List;

public record WorkYear(List<Job> finishedYear, int totalTime, int timeWorked, int timeWasted,
                       boolean sorted) {
}
