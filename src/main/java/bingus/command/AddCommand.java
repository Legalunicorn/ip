package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Add one task to the task list
 */
public class AddCommand extends Command{

    private final Task task;
    public AddCommand(Task task) {
        this.task = task;
    }

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
