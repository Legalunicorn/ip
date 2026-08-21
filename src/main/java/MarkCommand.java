/**
 * Marks or unmarks a task
 */
public class MarkCommand extends Command{

    private final int  taskId;
    private final boolean isToMark;

    public MarkCommand(int taskId, boolean isToMark) {
        this.taskId = taskId;
        this.isToMark = isToMark;
    }

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
    }
}
