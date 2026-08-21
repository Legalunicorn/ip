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
     * Executes this command using the application's current collaborators.
     *
     * @param tasks current task list
     * @param ui user interface used to display command output
     * @param storage persistent task storage
     * @throws BingusException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException;

    /**
     * Returns whether executing this command should end the application.
     *
     * @return {@code true} if the application should exit
     */
    public boolean isExit() {
        return false;
    }
}
