package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String word;

    /**
     * Creates a command that finds tasks containing the specified word.
     *
     * @param word word to search for
     */
    public FindCommand(String word) {
        this.word = word;
    }

    /**
     * Finds matching tasks and displays them to the user.
     *
     * @param tasks task list to search
     * @param ui user interface used to display results
     * @param storage persistent task storage, which is not changed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        ui.showMatchingTasks(tasks.findByWord(word), word);
    }
}
