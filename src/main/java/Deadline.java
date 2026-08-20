import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {

    /** Formatter used to present a deadline in task output. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu, h:mm a");

    /** Date and time by which this task must be completed. */
    protected LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description task description
     * @param by date and time by which the task is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the date and time by which this task is due.
     *
     * @return deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns this deadline in the format used for display.
     *
     * @return formatted deadline task text
     */
    @Override
    public String getTaskString() {
        return super.getTaskString() + " (by: " + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
