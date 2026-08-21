package bingus.ui;

import bingus.task.Task;
import bingus.task.TaskList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Represents the layer that iterate with the users
 * through receiving input and printing output
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INDENT = "    ";
    private final Scanner scanner;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu");

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
        System.out.println(INDENT + INDENT +  t.getTaskString());
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
}
