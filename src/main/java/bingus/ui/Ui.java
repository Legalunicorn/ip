package bingus.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import bingus.task.Task;
import bingus.task.TaskList;

/**
 * Represents the user-interface layer that reads terminal input and formats responses.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INDENT = "    ";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu");
    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command line is available from standard input.
     *
     * @return {@code true} if a command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command line from standard input.
     *
     * @return command line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    // GUI response messages

    /**
     * Returns the welcome message for the chatbot.
     *
     * @return welcome message
     */
    public String getWelcomeMessage() {
        return "Hello! I'm Bingus.\nWhat can I do for you?";
    }

    /**
     * Returns an error message.
     *
     * @param message error description
     * @return formatted error message
     */
    public String getErrorMessage(String message) {
        return "Error: " + message;
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after the addition
     * @return task-added message
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n"
                + task.getTaskString()
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns every task in the task list.
     *
     * @param tasks task list to display
     * @return task-list message
     */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            message.append("\n")
                    .append(index + 1)
                    .append(". ")
                    .append(tasks.get(index).getTaskString());
        }
        return message.toString();
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task task that was deleted
     * @param taskCount total number of tasks after deletion
     * @return task-deleted message
     */
    public String getDeleteTaskMessage(Task task, int taskCount) {
        return "Noted! I've removed this task:\n"
                + task.getTaskString()
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task was marked complete.
     *
     * @param task task whose completion status changed
     * @return task-marked message
     */
    public String getTaskMarkedMessage(Task task) {
        return "Nice! I've marked this task as done:\n" + task.getTaskString();
    }

    /**
     * Returns confirmation that a task was marked incomplete.
     *
     * @param task task whose completion status changed
     * @return task-unmarked message
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "OK, I've marked this task as not done yet:\n" + task.getTaskString();
    }

    /**
     * Returns tasks associated with a specified date.
     *
     * @param tasks task list to filter
     * @param date date used to filter tasks
     * @return filtered task-list message
     */
    public String getFilteredTaskListMessage(TaskList tasks, LocalDate date) {
        StringBuilder message = new StringBuilder(
                "Here are the tasks on " + date.format(DISPLAY_DATE_FORMAT) + ":");
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.matchesDate(date)) {
                message.append("\n")
                        .append(index + 1)
                        .append(". ")
                        .append(task.getTaskString());
            }
        }
        return message.toString();
    }

    /**
     * Returns the farewell message.
     *
     * @return farewell message
     */
    public String getByeMessage() {
        return "Bye! Hope you visit me again :>";
    }

    /**
     * Returns tasks whose descriptions match a search word.
     *
     * @param tasks matching tasks
     * @param word word used to search for tasks
     * @return matching-tasks message
     */
    public String getMatchingTasksMessage(List<Task> tasks, String word) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            message.append("\n")
                    .append(index + 1)
                    .append(". ")
                    .append(tasks.get(index).getTaskString());
        }
        if (tasks.isEmpty()) {
            message.append("\nThere were no matches for `")
                    .append(word)
                    .append("`.");
        }
        return message.toString();
    }

    /**
     * Displays a separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays the welcome message.
     */
    public void showWelcome() {
        // This string was generated and formatted by Codex
        String banner = "  ____  _                       \n"
                + " | __ )(_)_ __   __ _ _   _ ___ \n"
                + " |  _ \\| | '_ \\ / _` | | | / __|\n"
                + " | |_) | | | | | (_| | |_| \\__ \\\n"
                + " |____/|_|_| |_|\\__, |\\__,_|___/\n"
                + "                |___/           \n";
        System.out.println(LINE);
        System.out.print(banner);
        System.out.println("Hello! I'm bingus.Bingus.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Displays an error message followed by a separator line.
     *
     * @param message error message to display
     */
    public void showError(String message) {
        System.out.println(INDENT + message);
        showLine();
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param t task that was added
     * @param taskCount total number of tasks after the addition
     */
    public void showTaskAdded(Task t, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + INDENT + t.getTaskString());
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Displays every task in the given task list.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(INDENT + "Here are the tasks in your list:");
        for (int id = 0; id < tasks.size(); id++) {
            System.out.println(INDENT + (id + 1) + "." + tasks.get(id).getTaskString());
        }
        System.out.println(LINE);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param t task that was deleted
     * @param taskCount total number of tasks after deletion
     */
    public void showDeleteTask(Task t, int taskCount) {
        System.out.println(INDENT + "Noted! I've removed this task: ");
        System.out.println(INDENT + INDENT + t.getTaskString());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Displays confirmation that a task was marked complete.
     *
     * @param task task whose completion status changed
     */
    public void showTaskMarked(Task task) {
        System.out.println(INDENT + "Nice! I've marked this task as done : ) ");
        System.out.println(INDENT + INDENT + task.getTaskString());
        System.out.println(LINE);
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task task whose completion status changed
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(INDENT + "OK, I've marked this task as not done yet: ");
        System.out.println(INDENT + INDENT + task.getTaskString());
        System.out.println(LINE);
    }

    /**
     * Displays tasks associated with the specified date.
     *
     * @param tasks task list to filter and display
     * @param date date used to filter tasks
     */
    public void showFilteredTaskList(TaskList tasks, LocalDate date) {
        System.out.println(INDENT + "Here are the tasks on " + date.format(DISPLAY_DATE_FORMAT) + ":");
        for (int id = 0; id < tasks.size(); id++) {
            Task task = tasks.get(id);
            if (task.matchesDate(date)) {
                System.out.println(INDENT + (id + 1) + "." + task.getTaskString());
            }
        }
        System.out.println(LINE);
    }

    /**
     * Displays the farewell message.
     */
    public void showByeMessage() {
        System.out.println(INDENT + "Bye! Hope you visit me again :> ");
        showLine();
    }

    /**
     * Displays tasks whose descriptions match a search word.
     *
     * @param tasks matching tasks to display
     * @param word word used to search for tasks
     */
    public void showMatchingTasks(List<Task> tasks, String word) {
        System.out.println(INDENT + "Here are the matching tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println(INDENT + (index + 1) + "." + tasks.get(index).getTaskString());
        }

        if (tasks.isEmpty()) {
            System.out.println(INDENT + "There were no matches for `" + word + "`.");
        }
        System.out.println(LINE);
    }
}
