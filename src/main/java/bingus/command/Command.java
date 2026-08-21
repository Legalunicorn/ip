package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Represents a user command that can be executed by bingus.Bingus.
 */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException;
    public boolean isExit() {
        return false;
    }
}
