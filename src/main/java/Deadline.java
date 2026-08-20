/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {

    /** Deadline text displayed for this task. */
    protected String by;

    /**
     * Creates a deadline task.
     *
     * @param description task description
     * @param by deadline text supplied by the user
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the deadline text stored for this task.
     *
     * @return deadline text
     */
    public String getBy() {
        return by;
    }
}
