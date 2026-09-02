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

    @Override
    public CommandType getType() {
        return CommandType.FIND;
    }

    /**
     * Finds matching tasks and returns them as a message.
     *
     * @param tasks task list to search
     * @param ui user interface used to format results
     * @param storage persistent task storage, which is not changed
     * @return matching-tasks message
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        return ui.getMatchingTasksMessage(tasks.findByWord(word), word);
    }
}
