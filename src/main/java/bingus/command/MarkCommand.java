package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Marks or unmarks a task and persists the updated list.
 */
public class MarkCommand extends Command{

    private final int  taskId;
    private final boolean isToMark;

    /**
     * Creates a command that changes the completion status of a task.
     *
     * @param taskId one-based number of the task to update
     * @param isToMark whether the task should be marked complete
     */
    public MarkCommand(int taskId, boolean isToMark) {
        this.taskId = taskId;
        this.isToMark = isToMark;
    }

    /**
     * Updates the task's completion status, saves the list, and displays the result.
     *
     * @param tasks task list containing the task to update
     * @param ui user interface used to display the result
     * @param storage persistent task storage
     * @throws BingusException if the updated list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        Task task = tasks.get(taskId - 1);
        if (isToMark) {
            task.mark();
        } else {
            task.unmark();
        }

        // Maybe consider rollbacker/storage to handle roll backs
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            if (isToMark) {
                task.unmark(); // reverse of mark
            } else {
                task.mark(); // reverse of unmark
            }
            throw e;
        }

        if (isToMark) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
    }
}
