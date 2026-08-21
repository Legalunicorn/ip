package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Deletes one task from the task list and persists the updated list.
 */
public class DeleteCommand extends Command {
    private final int taskId;

    /**
     * Creates a command that deletes the specified task.
     *
     * @param taskId One-based number of the task to delete.
     */
    public DeleteCommand(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Removes the task, saves the list, and reports the result to the user.
     *
     * @param tasks task list to update
     * @param ui user interface used to display the result
     * @param storage persistent task storage
     * @throws BingusException if the updated list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        int idx = taskId - 1;
        Task deletedTask = tasks.remove(idx);

        // TODO: consider roll back for each commands to be part of save action
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            tasks.add(idx, deletedTask);
            throw e;
        }
        ui.showDeleteTask(deletedTask, tasks.size());

    }
}
