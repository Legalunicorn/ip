/**
 * Represents a user command that can be executed by Bingus.
 */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException;
    public boolean isExit() {
        return false;
    }
}
