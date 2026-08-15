/**
 * Represents a task with a description that can be marked as complete or incomplete.
 */
public class Task {
    /** Description displayed for this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;
    private final TaskType type;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description displayed for this task
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        // Default task to be uncompleted
        isDone = false;
    }

    /**
     * Returns this task in the format used for display.
     *
     * @return formatted task text
     */
    public String getTaskString() {
        return "[" + type.getSymbol() + "]" + "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the symbol representing this task's completion status.
     *
     * @return {@code "X"} if complete, or a space if incomplete
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
       isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }
}
