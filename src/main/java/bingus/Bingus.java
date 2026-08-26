package bingus;

import bingus.command.Command;
import bingus.exception.BingusException;
import bingus.parser.Parser;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Coordinates command parsing, task storage, and terminal interaction.
 */
public class Bingus {

    private final Storage storage;
    private String loadErrorMessage;
    private String commandType;
    private TaskList tasks;
    private final Parser parser;
    private final Ui ui;

    /**
     * Creates the application using the specified task save file.
     *
     * @param filePath path of the task save file
     */
    public Bingus(String filePath) {
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (BingusException e) {
            loadErrorMessage = "I couldn't load your save tasks, Sorry! Starting with an empty list";
            tasks = new TaskList();
        }
        parser = new Parser();
        ui = new Ui();

    }

    /**
     * Starts the user interface and processes commands until the session ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadErrorMessage != null) {
            ui.showError(loadErrorMessage);
        }
        startTaskLoop();
    }

    /**
     * Displays the welcome message and starts processing user commands.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Bingus("data/bingus.txt").run();
    }

    /**
     * Repeatedly read command from user and execute them.
     * This is used for the CLI Bingus program.
     */
    private void startTaskLoop() {
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String userInput = ui.readCommand();
                ui.showLine();
                Command c = parser.parse(userInput, tasks);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (BingusException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Return a response based on a command. This is used for the GUI program.
     * @param input by the user.
     * @return String to output by the program
     */
    public String getResponse(String input) {
        try {
            Command c = parser.parse(input, tasks);
            commandType = c.getClass().getSimpleName();
            return c.execute(tasks, ui, storage);
        } catch (BingusException e) {
            commandType = "";
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Return the type of the latest command.
     * @return String of the command type
     */
    public String getCommandType() {
        return commandType;
    }

}
