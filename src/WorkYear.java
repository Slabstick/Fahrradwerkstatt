import java.util.List;

public record WorkYear(List<Job> finishedYear, int totalTime, int timeWorked, int timeWasted, int freeTime,
                       int totalWorkTime, boolean sorted) {
}
