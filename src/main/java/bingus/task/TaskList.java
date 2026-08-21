package bingus.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collections of Tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks initially contained in the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Adds a task at the specified zero-based position.
     *
     * @param id Zero-based position at which to add the task.
     * @param task Task to add.
     */
    public void add(int id, Task task) {
        this.tasks.add(id, task);
    }

    /**
     * Returns the task at the specified position.
     *
     * @param id Zero-based position of the task.
     * @return Task at the specified position.
     */
    public Task get(int id) {
        return this.tasks.get(id);
    }

    /**
     * Removes and returns the task at the specified position.
     *
     * @param id Zero-based position of the task.
     * @return Removed task.
     */
    public Task remove(int id) {
        return tasks.remove(id);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks in this list.
     *
     * @return Tasks in their current order.
     */
    public List<Task> getAllTasks() {
        return List.copyOf(tasks);
    }

}
