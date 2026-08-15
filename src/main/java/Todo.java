/**
 * A task with no deadlines attacked, matches behavior of parent class
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTaskString() {
        return "[T]" + super.getTaskString();
    }
}
