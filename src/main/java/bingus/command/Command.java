package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Represents a user command that can be executed by bingus.Bingus.
 */
public abstract class Command {
    /**
     * Executes this command using the current application collaborators.
     *
     * @param tasks Current task list.
     * @param ui User interface used to display command output.
     * @param storage Persistent task storage.
     * @throws BingusException If the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException;

    public boolean isExit() {
        return false;
    }
}
