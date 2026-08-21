import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collections of Tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Adds a task at the specified zero-based position.
     *
     * @param id zero-based position at which to add the task
     * @param task task to add
     */
    public void add(int id, Task task) {
        this.tasks.add(id, task);
    }

    public Task get(int id) {
        return this.tasks.get(id);
    }

    public Task remove(int id) {
        return tasks.remove(id);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

}
