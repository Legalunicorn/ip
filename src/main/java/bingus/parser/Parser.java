package bingus.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import bingus.command.AddCommand;
import bingus.command.Command;
import bingus.command.DeleteCommand;
import bingus.command.ExitCommand;
import bingus.command.FindCommand;
import bingus.command.ListCommand;
import bingus.command.MarkCommand;
import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.TaskList;
import bingus.task.Todo;

/**
 * Parses command arguments into validated tasks and values.
 */
public class Parser {

    /** Strict format accepted for deadline and event date/time input. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Strict format accepted when filtering tasks by date. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);


    /**
     * Converts one command line into the corresponding command object.
     *
     * @param userInput complete command line entered by the user
     * @param tasks current task list, used to validate task numbers
     * @return command represented by the input
     * @throws BingusException if the command or its arguments are invalid
     */
    public Command parse(String userInput, TaskList tasks) throws BingusException {
        String[] parts = splitCommand(userInput);
        String commandWord = parts[0];
        switch (commandWord) {
            case "bye":
                return new ExitCommand();
            case "list":
                return parts.length == 1
                        ? new ListCommand()
                        : new ListCommand(parseListDate(parts[1]));
            case "todo":
                return new AddCommand(parseTodo(parts));
            case "deadline":
                return new AddCommand(parseDeadline(parts));
            case "event":
                return new AddCommand(parseEvent(parts));
            case "mark":
                return new MarkCommand(parseRequiredTaskId(parts, tasks.size(), "Mark"), true);
            case "unmark":
                return new MarkCommand(parseRequiredTaskId(parts, tasks.size(), "Unmark"), false);
            case "delete":
                return new DeleteCommand(parseDeleteTaskId(parts, tasks.size()));
            case "find":
                return new FindCommand(parseFindKeyword(parts));
            default:
                throw new BingusException("I don't recognise this command :/ ");
        }
    }

    /**
     * Parses the required search word for a find command.
     *
     * @param parts command and arguments split into at most two parts
     * @return validated search word
     * @throws BingusException if the search word is missing
     */
    private String parseFindKeyword(String[] parts) throws BingusException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BingusException("Please provide a keyword. Usage `find [KEYWORD]`.");
        }

        return parts[1].trim();
    }

    private int parseRequiredTaskId(String[] parts, int taskCount, String action) throws BingusException {
        if (parts.length < 2) {
            throw new BingusException(action + " must be followed by a number!");
        }
        return parseTaskId(parts[1], taskCount);
    }

    /**
     * Parses a required task number for a delete command.
     *
     * @param parts command and arguments split into at most two parts
     * @param taskCount current number of tasks
     * @return validated one-based task number
     * @throws BingusException if the number is missing or invalid
     */
    private int parseDeleteTaskId(String[] parts, int taskCount) throws BingusException {
        if (parts.length < 2) {
            throw new BingusException("Missing delete number! Usage `delete [TASK_NUMBER]`.");
        }
        return parseTaskId(parts[1], taskCount);
    }


    /**
     * Splits a user command into its command word and remaining arguments.
     *
     * @param userInput complete command line entered by the user
     * @return command and arguments split into at most two parts
     */
    public String[] splitCommand(String userInput) {
        return userInput.split("\\s+", 2);
    }

    /**
     * Parses the arguments of a todo command.
     *
     * @param parts command and arguments split into at most two parts
     * @return validated todo task
     * @throws BingusException if the description is missing or empty
     */
    public Todo parseTodo(String[] parts) throws BingusException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BingusException("Please give your todo a task description! Usage `todo [DESCRIPTION]`.");
        }
        return new Todo(parts[1].trim());
    }

    /**
     * Parses the arguments of a deadline command.
     *
     * @param parts command and arguments split into at most two parts
     * @return validated deadline task
     * @throws BingusException if the command format or date/time is invalid
     */
    public Deadline parseDeadline(String[] parts) throws BingusException {
        String correctFormatMessage = "Please use `deadline [DESCRIPTION] /by [DATETIME]`.";
        if (parts.length < 2) {
            throw new BingusException("Missing command arguments :( " + correctFormatMessage);
        }

        String[] deadlineParts = parts[1].split("/by");
        if (deadlineParts.length != 2) {
            throw new BingusException("Wrong format for deadline :( " + correctFormatMessage);
        }

        String description = deadlineParts[0].trim();
        String by = deadlineParts[1].trim();
        if (description.isEmpty()) {
            throw new BingusException("bingus.task.Task description cannot be empty. " + correctFormatMessage);
        }
        if (by.isEmpty()) {
            throw new BingusException("bingus.task.Deadline cannot be empty. " + correctFormatMessage);
        }

        try {
            LocalDateTime deadlineDateTime = LocalDateTime.parse(by, INPUT_DATE_TIME_FORMAT);
            return new Deadline(description, deadlineDateTime);
        } catch (DateTimeParseException e) {
            throw new BingusException("Invalid deadline date/time. Please use yyyy-MM-dd HHmm, "
                    + "e.g. 2019-12-02 1800.");
        }
    }

    /**
     * Parses the arguments of an event command.
     *
     * @param parts command and arguments split into at most two parts
     * @return validated event task
     * @throws BingusException if the command format or date/time is invalid
     */
    public Event parseEvent(String[] parts) throws BingusException {
        String correctFormatMessage = "Please use `event [DESCRIPTION] /from [FROM_DATE] /to [TO_DATE]`";
        if (parts.length < 2) {
            throw new BingusException("Missing event arguments :( " + correctFormatMessage);
        }

        String[] descriptionAndTimes = parts[1].split("/from");
        if (descriptionAndTimes.length != 2) {
            throw new BingusException(correctFormatMessage);
        }

        String[] startAndEnd = descriptionAndTimes[1].split("/to");
        if (startAndEnd.length != 2) {
            throw new BingusException(correctFormatMessage);
        }

        String description = descriptionAndTimes[0].trim();
        String from = startAndEnd[0].trim();
        String to = startAndEnd[1].trim();
        if (from.isEmpty()) {
            throw new BingusException("From cannot be empty! " + correctFormatMessage);
        }
        if (to.isEmpty()) {
            throw new BingusException("`To` cannot be empty! " + correctFormatMessage);
        }
        if (description.isEmpty()) {
            throw new BingusException("`Description` of event cannot be empty! " + correctFormatMessage);
        }

        try {
            LocalDateTime fromDateTime = LocalDateTime.parse(from, INPUT_DATE_TIME_FORMAT);
            LocalDateTime toDateTime = LocalDateTime.parse(to, INPUT_DATE_TIME_FORMAT);
            if (!toDateTime.isAfter(fromDateTime)) {
                throw new BingusException("bingus.task.Event end date/time must be after its start date/time.");
            }
            return new Event(description, fromDateTime, toDateTime);
        } catch (DateTimeParseException e) {
            throw new BingusException("Invalid event date/time. Please use yyyy-MM-dd HHmm, "
                    + "e.g. 2019-12-02 1800.");
        }
    }

    /**
     * Parses a one-based task number and verifies that it refers to a task.
     *
     * @param input task number text
     * @param taskCount current number of tasks
     * @return validated one-based task number
     * @throws BingusException if the number is not an integer or is out of range
     */
    public int parseTaskId(String input, int taskCount) throws BingusException {
        try {
            int taskId = Integer.parseInt(input.trim());
            if (taskId < 1 || taskId > taskCount) {
                throw new BingusException("Given task number does not exist in your list :(");
            }
            return taskId;
        } catch (NumberFormatException e) {
            throw new BingusException("bingus.task.Task number must be a whole number.");
        }
    }

    /**
     * Parses a date used to filter the task list.
     *
     * @param input date text in yyyy-MM-dd format
     * @return validated calendar date
     * @throws BingusException if the date is invalid
     */
    public LocalDate parseListDate(String input) throws BingusException {
        try {
            return LocalDate.parse(input.trim(), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BingusException("Invalid list date. Please use yyyy-MM-dd, e.g. 2019-12-02.");
        }
    }
}
