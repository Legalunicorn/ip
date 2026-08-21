/**
 * Deletes one task from the task list
 */
public class DeleteCommand extends Command{
    private final int taskId;

    public DeleteCommand(int taskId) {
        this.taskId = taskId;
    }

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
