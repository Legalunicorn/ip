import java.time.LocalDate;
import java.util.Scanner;

/**
 * Starts Bingus and handles simple commands entered at the terminal.
 */
public class Bingus {

    private static final Storage storage = new Storage("data/bingus.txt");

    /** Format used when showing the selected date in a filtered task list. */

    private static String loadErrorMessage;

    private static TaskList tasks = new TaskList();
    private static final Parser parser = new Parser();
    private static final Ui ui = new Ui();


    /**
     * Displays the welcome message and starts processing user commands.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (BingusException e) {
            loadErrorMessage = "I couldn't load your save tasks, Sorry! Starting with an empty list";
            tasks = new TaskList();
        }

        ui.showWelcome();
        if (loadErrorMessage != null) {
            ui.showError(loadErrorMessage);
        }
        startTaskLoop();
    }

    /**
     * Read the command input from user and delegate to respective helper methods
     */
    private static void startTaskLoop() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            try {
                String[] parts = parser.splitCommand(userInput);
                String command = parts[0];
                ui.showLine();
                switch (command) {
                    case "bye":
                        exitChat();
                        return;
                    case "list":
                        handleList(parts);
                        break;
                    case "mark": {
                        handleMark(parts, true);
                        break;
                    }
                    case "unmark": {
                        handleMark(parts, false);
                        break;
                    }
                    case "todo": {
                        handleTodo(parts);
                        break;
                    }
                    case "deadline": {
                        handleDeadline(parts);
                        break;
                    }
                    case "event": {
                        handleEvent(parts);
                        break;
                    }
                    case "delete" : {
                        handleDelete(parts);
                        break;
                    }
                    default:
                        throw new BingusException("I don't recognise this command :/ ");
                }
            } catch (BingusException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Stores a task and displays its add confirmation.
     *
     * @param t task to store
     */
    private static void addTask(Task t) throws BingusException {
        tasks.add(t);
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(t, tasks.size());
    }

    /**
     * Displays the farewell message.
     */
    private static void exitChat() {
        ui.showByeMessage();
    }

    /**
     * Displays all tasks currently stored in the list.
     */
    private static void listTasks() {
        ui.showTaskList(tasks);
    }

    /**
     * Lists all tasks, or dated tasks occurring on a requested date.
     *
     * @param parts command and optional date filter
     * @throws BingusException if the requested date is invalid
     */
    private static void handleList(String[] parts) throws BingusException {
        if (parts.length == 1) {
            listTasks();
            return;
        }

        LocalDate date = parser.parseListDate(parts[1]);
        listTasksOn(date);
    }

    /**
     * Displays deadlines due or events occurring on the specified date.
     *
     * @param date calendar date used to filter dated tasks
     */
    private static void listTasksOn(LocalDate date) {
        ui.showFilteredTaskList(tasks, date);
    }

    /**
     * Marks the task with the given user-facing number as complete.
     *
     * @param inputId one-based task number entered by the user
     */
    private static void markTask(int inputId) throws BingusException {
        // TODO: consider invalid inputId, to be done in future level
        Task task = tasks.get(inputId - 1);
        task.mark();
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            task.unmark();
            throw e;
        }
        ui.showTaskMarked(task);
    }

    /**
     * Handle input validation before marking/unmarking a task
     *
     * @param parts  command + parameters as an array
     * @param isMark true if request to mark, false if request to unmark
     * @throws BingusException if taskId is invalid range or not a number
     */
    private static void handleMark(String[] parts, boolean isMark) throws BingusException {
        if (parts.length < 2){
            String action = (isMark ? "Mark": "Unmark");
            throw new BingusException(action+" must be followed by a number!");
        }
        int taskId = parser.parseTaskId(parts[1], tasks.size());
        if (isMark) {
            markTask(taskId);
        } else {
            unmarkTask(taskId);
        }
    }

    /**
     * Marks the task with the given user-facing number as incomplete.
     *
     * @param inputId one-based task number entered by the user
     */
    private static void unmarkTask(int inputId) throws BingusException {
        Task task = tasks.get(inputId - 1);
        task.unmark();
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            task.mark();
            throw e;
        }
        ui.showTaskUnmarked(task);
    }

    private static void handleTodo(String[] parts) throws BingusException {
        addTask(parser.parseTodo(parts));
    }

    private static void handleDeadline(String[] parts) throws BingusException {
        addTask(parser.parseDeadline(parts));
    }

    private static void handleEvent(String[] parts) throws BingusException {
        addTask(parser.parseEvent(parts));
    }

    private static void handleDelete(String[] parts) throws BingusException {
        if (parts.length < 2) {
            throw new BingusException("Missing delete number! Usage `delete [TASK_NUMBER]`. ");
        }
        int taskId = parser.parseTaskId(parts[1], tasks.size());
        delete(taskId);
    }

    private static void delete(int pos) throws BingusException {
        int id = pos - 1;
        Task t = tasks.remove(id);
        try {
            storage.saveTasks(tasks.getAllTasks());
        } catch (BingusException e) {
            tasks.add(id, t);
            throw e;
        }
        ui.showDeleteTask(t, tasks.size());
    }
}
