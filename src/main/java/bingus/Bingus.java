package bingus;

import bingus.command.Command;
import bingus.command.CommandType;
import bingus.exception.BingusException;
import bingus.parser.Parser;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Coordinates command parsing, task storage, and user-interface responses.
 */
public class Bingus {

    /** Default save file used by the CLI and GUI entry points. */
    static final String DEFAULT_SAVE_FILE_PATH = "data/bingus.txt";

    private final Storage storage;
    private String loadErrorMessage;
    private CommandType commandType = CommandType.NONE;
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
        new Bingus(DEFAULT_SAVE_FILE_PATH).run();
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
                Command command = parser.parse(userInput, tasks);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (BingusException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Returns a response for a command submitted through the GUI.
     *
     * @param input command entered by the user
     * @return response message for the user
     */
    public String getResponse(String input) {
        try {
            Command command = parser.parse(input, tasks);
            commandType = command.getType();
            return command.execute(tasks, ui, storage);
        } catch (BingusException e) {
            commandType = CommandType.INVALID;
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns the type of the latest command submitted through the GUI.
     *
     * @return type of the latest command
     */
    public CommandType getCommandType() {
        return commandType;
    }

}
