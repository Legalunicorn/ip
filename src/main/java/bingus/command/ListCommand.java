package bingus.command;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

import java.time.LocalDate;

/**
 * Displays all tasks, or only tasks related to a specified date.
 */
public class ListCommand  extends Command{
   
    private final LocalDate date;

    /**
     * Creates a command that displays every task.
     */
    public ListCommand()  {
        this.date = null;
    }
    
    /**
     * Creates a command that displays tasks associated with a date.
     *
     * @param date date used to filter tasks
     */
    public ListCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the complete or date-filtered task list.
     *
     * @param tasks task list to display
     * @param ui user interface used to display the list
     * @param storage persistent task storage, which is not changed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        if (date == null) {
            ui.showTaskList(tasks);
        } else {
            ui.showFilteredTaskList(tasks, date);
        }
    }
}
