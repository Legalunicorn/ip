package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Ends the Bingus application after showing a farewell message.
 */
public class ExitCommand extends Command{

    /**
     * Returns that this command ends the application.
     *
     * @return always {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }

    /**
     * Displays the farewell message.
     *
     * @param tasks current task list, which is not changed
     * @param ui user interface used to display the message
     * @param storage persistent task storage, which is not changed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        ui.showByeMessage();
    }
}
