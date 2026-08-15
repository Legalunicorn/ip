public class Task {
    private String description;
    private boolean isMarked;

    public Task(String description) {
        description = description;
        // Default task to be uncompleted
        isMarked = false;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void mark() {
       isMarked = true;
    }

    public void unmark() {
        isMarked = false;
    }


}
