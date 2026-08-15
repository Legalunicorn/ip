public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        // Default task to be uncompleted
        isDone = false;
    }

    public String getTaskString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void mark() {
       isDone = true;
    }

    public void unmark() {
        isDone = false;
    }
}
