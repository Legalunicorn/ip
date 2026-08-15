/**
 * Represents a task that starts and ends at specified times.
 */
public class Event extends Task {

    /** Start time text displayed for this event. */
    protected String from;

    /** End time text displayed for this event. */
    protected String to;


    /**
     * Creates an event task.
     *
     * @param description description of the event
     * @param from event start time text
     * @param to event end time text
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }
}
