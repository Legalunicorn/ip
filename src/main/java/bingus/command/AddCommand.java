package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Adds one task to the task list and persists the updated list.
 */
public class AddCommand extends Command{

    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the list, and reports the result to the user.
     *
     * @param tasks task list to update
     * @param ui user interface used to display the result
     * @param storage persistent task storage
     * @throws BingusException if the updated list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            tasks.remove(tasks.size() - 1); // undo the failed save
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
