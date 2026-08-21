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

    public Ui() {
        scanner = new Scanner(System.in);
    }

    public boolean hasNextCommand(){
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

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

    public void showError(String message) {
        System.out.println(INDENT + message);
        showLine();
    }

    public void showTaskAdded(Task t, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + INDENT +  t.getTaskString());
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(INDENT + "Here are the tasks in your list:");
        for (int id = 0; id < tasks.size(); id++) {
            System.out.println(INDENT + (id + 1) + "." + tasks.get(id).getTaskString());
        }
        System.out.println(LINE);
    }

    public void showDeleteTask(Task t, int taskCount) {
        System.out.println(INDENT + "Noted! I've removed this task: ");
        System.out.println(INDENT + INDENT + t.getTaskString());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showTaskMarked(Task task) {
        System.out.println(INDENT + "Nice! I've marked this task as done : ) ");
        System.out.println(INDENT + INDENT + task.getTaskString());
        System.out.println(LINE);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println(INDENT + "OK, I've marked this task as not done yet: ");
        System.out.println(INDENT + INDENT + task.getTaskString());
        System.out.println(LINE);
    }

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

    public void showByeMessage() {
        System.out.println(INDENT + "Bye! Hope you visit me again :> ");
        showLine();
    }
}
