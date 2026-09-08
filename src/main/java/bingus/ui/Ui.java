package bingus.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import bingus.task.Task;
import bingus.task.TaskList;

/**
 * Formats user-interface responses.
 */
public class Ui {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /**
     * Returns confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount total number of tasks after the addition
     * @return task-added message
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it! I added this task:\n"
                + task.getTaskString()
                + "\nYou now have " + getTaskCountDescription(taskCount) + " in your list.";
    }

    /**
     * Returns every task in the task list.
     *
     * @param tasks task list to display
     * @return task-list message
     */
    public String getTaskListMessage(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your task list is empty. Time for a cat nap!";
        }

        StringBuilder message = new StringBuilder("Here are the tasks I am watching:");
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
        return "Done! I removed this task:\n"
                + task.getTaskString()
                + "\nYou now have " + getTaskCountDescription(taskCount) + " in your list.";
    }

    /**
     * Returns confirmation that a task was marked complete.
     *
     * @param task task whose completion status changed
     * @return task-marked message
     */
    public String getTaskMarkedMessage(Task task) {
        return "Purrfect! This task is complete:\n" + task.getTaskString();
    }

    /**
     * Returns confirmation that a task was marked incomplete.
     *
     * @param task task whose completion status changed
     * @return task-unmarked message
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "No problem! This task is active again:\n" + task.getTaskString();
    }

    /**
     * Returns confirmation that a task was updated.
     *
     * @param task updated task
     * @return task-updated message
     */
    public String getTaskUpdatedMessage(Task task) {
        return "Got it! I updated this task:\n" + task.getTaskString();
    }

    /**
     * Returns tasks associated with a specified date.
     *
     * @param tasks task list to filter
     * @param date date used to filter tasks
     * @return filtered task-list message
     */
    public String getFilteredTaskListMessage(TaskList tasks, LocalDate date) {
        String formattedDate = date.format(DISPLAY_DATE_FORMAT);
        StringBuilder message = new StringBuilder(
                "Here are the tasks I am watching on " + formattedDate + ":");
        boolean hasMatchingTasks = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.matchesDate(date)) {
                hasMatchingTasks = true;
                message.append("\n")
                        .append(index + 1)
                        .append(". ")
                        .append(task.getTaskString());
            }
        }

        if (!hasMatchingTasks) {
            return "I am not watching any tasks on " + formattedDate + ".";
        }
        return message.toString();
    }

    /**
     * Returns the farewell message.
     *
     * @return farewell message
     */
    public String getByeMessage() {
        return "Bingus is curling up for a nap. See you soon!";
    }

    /**
     * Returns tasks whose descriptions match a search word.
     *
     * @param tasks matching tasks
     * @param word word used to search for tasks
     * @return matching-tasks message
     */
    public String getMatchingTasksMessage(List<Task> tasks, String word) {
        if (tasks.isEmpty()) {
            return "I could not find anything matching `" + word + "`.";
        }

        StringBuilder message = new StringBuilder("I found these matching tasks:");
        for (int index = 0; index < tasks.size(); index++) {
            message.append("\n")
                    .append(index + 1)
                    .append(". ")
                    .append(tasks.get(index).getTaskString());
        }
        return message.toString();
    }

    /**
     * Returns a task count with the correct singular or plural noun.
     *
     * @param taskCount number of tasks
     * @return formatted task count
     */
    private String getTaskCountDescription(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

}
