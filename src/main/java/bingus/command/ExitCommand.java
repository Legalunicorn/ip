package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Ends the Bingus application after showing a farewell message.
 */
public class ExitCommand extends Command {

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        ui.showByeMessage();
    }
}
