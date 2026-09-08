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
import bingus.command.UpdateCommand;
import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
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

    /** Usage guidance for deadline commands. */
    private static final String DEADLINE_USAGE = "Try `deadline [DESCRIPTION] /by [DATETIME]`.";

    /** Usage guidance for event commands. */
    private static final String EVENT_USAGE =
            "Try `event [DESCRIPTION] /from [FROM_DATETIME] /to [TO_DATETIME]`.";

    /** Usage guidance for updating supported task details. */
    private static final String UPDATE_USAGE = "Try `update [TASK_NUMBER] /desc [DESCRIPTION]`, "
            + "`update [TASK_NUMBER] /by [DATETIME]`, `update [TASK_NUMBER] /from [DATETIME]`, "
            + "or `update [TASK_NUMBER] /to [DATETIME]`.";

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
                requireNoArguments(parts, "Bingus cannot use extra details with `bye`.\nTry `bye`.");
                return new ExitCommand();
            case "list":
                return parts.length == 1
                        ? new ListCommand()
                        : new ListCommand(parseSingleListDate(parts[1]));
            case "todo":
                return new AddCommand(parseTodo(parts));
            case "deadline":
                return new AddCommand(parseDeadline(parts));
            case "event":
                return new AddCommand(parseEvent(parts));
            case "mark":
                return new MarkCommand(parseRequiredTaskId(
                        parts, tasks.size(), "Which task should I mark?\nTry `mark [TASK_NUMBER]`.",
                        "I can only mark one task at a time.\nTry `mark [TASK_NUMBER]`."), true);
            case "unmark":
                return new MarkCommand(parseRequiredTaskId(
                        parts, tasks.size(), "Which task should I unmark?\nTry `unmark [TASK_NUMBER]`.",
                        "I can only unmark one task at a time.\nTry `unmark [TASK_NUMBER]`."), false);
            case "delete":
                return new DeleteCommand(parseRequiredTaskId(
                        parts, tasks.size(), "Which task should I remove?\nTry `delete [TASK_NUMBER]`.",
                        "I can only remove one task at a time.\nTry `delete [TASK_NUMBER]`."));
            case "find":
                return new FindCommand(parseFindKeyword(parts));
            case "update":
                return parseUpdateCommand(parts, tasks);
            default:
                throw new BingusException("Hmm, I do not recognise that command.\n"
                        + "Try `todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`, "
                        + "`find`, `update`, or `bye`.");
        }
    }

    /**
     * Parses an update command that replaces a supported task detail.
     *
     * @param parts command and arguments split into at most two parts
     * @param tasks current task list, used to find the task being updated
     * @return command containing the updated replacement task
     * @throws BingusException if the command arguments are missing or invalid
     */
    private UpdateCommand parseUpdateCommand(String[] parts, TaskList tasks) throws BingusException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BingusException("Tell me which task to update and what to change.\n" + UPDATE_USAGE);
        }

        String[] updateParts = parts[1].trim().split("\\s+", 3);
        int taskId = parseTaskId(updateParts[0], tasks.size());
        if (updateParts.length < 2) {
            throw new BingusException("Tell me what to change for that task.\n" + UPDATE_USAGE);
        }
        String updateField = updateParts[1];
        if (!updateField.equals("/desc")
                && !updateField.equals("/by")
                && !updateField.equals("/from")
                && !updateField.equals("/to")) {
            throw new BingusException("I cannot update that field.\n" + UPDATE_USAGE);
        }
        if (updateParts.length < 3 || updateParts[2].trim().isEmpty()) {
            if (updateField.equals("/desc")) {
                throw new BingusException("I need a new description.\n" + UPDATE_USAGE);
            }
            throw new BingusException("I need a new date and time.\n" + UPDATE_USAGE);
        }

        String updatedValue = updateParts[2].trim();
        Task original = tasks.get(taskId - 1);
        Task updatedTask;
        if (updateField.equals("/desc")) {
            updatedTask = createTaskWithUpdatedDescription(original, updatedValue);
        } else if (updateField.equals("/by")) {
            updatedTask = createDeadlineWithUpdatedDateTime(original, updatedValue);
        } else {
            updatedTask = createEventWithUpdatedDateTime(original, updateField, updatedValue);
        }
        return new UpdateCommand(taskId, updatedTask);
    }

    /**
     * Creates a task of the same type with a new description and unchanged type-specific details.
     *
     * @param originalTask task whose type-specific details should be preserved
     * @param updatedDescription replacement task description
     * @return replacement task with the updated description
     */
    private Task createTaskWithUpdatedDescription(Task originalTask, String updatedDescription) {
        switch (originalTask.getType()) {
            case TODO:
                return new Todo(updatedDescription);
            case DEADLINE:
                Deadline deadline = (Deadline) originalTask;
                return new Deadline(updatedDescription, deadline.getBy());
            case EVENT:
                Event event = (Event) originalTask;
                return new Event(
                        updatedDescription,
                        event.getFrom(),
                        event.getTo());
            default:
                throw new IllegalStateException(
                        "Unsupported task type: " + originalTask.getType());
        }
    }

    /**
     * Creates a deadline with a new due date and an unchanged description.
     *
     * @param originalTask deadline whose description should be preserved
     * @param updatedDateTime replacement deadline date and time
     * @return replacement deadline with the updated due date
     * @throws BingusException if the task is not a deadline or the date and time is invalid
     */
    private Deadline createDeadlineWithUpdatedDateTime(Task originalTask, String updatedDateTime)
            throws BingusException {
        if (!(originalTask instanceof Deadline)) {
            throw new BingusException("I can only use `/by` with deadlines.\n" + UPDATE_USAGE);
        }

        String invalidDateTimeMessage = "That deadline date and time are not valid.\n"
                + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";
        LocalDateTime updatedBy = parseDateTime(updatedDateTime, invalidDateTimeMessage);
        return new Deadline(originalTask.getDescription(), updatedBy);
    }

    /**
     * Creates an event with one updated endpoint and all other details unchanged.
     *
     * @param originalTask event whose existing details should be preserved
     * @param updateField {@code /from} to update the start or {@code /to} to update the end
     * @param updatedDateTime replacement event date and time
     * @return replacement event with the updated endpoint
     * @throws BingusException if the task is not an event, the date and time is invalid, or the range is invalid
     */
    private Event createEventWithUpdatedDateTime(
            Task originalTask, String updateField, String updatedDateTime) throws BingusException {
        assert updateField.equals("/from") || updateField.equals("/to")
                : "Event update field must be /from or /to";
        if (!(originalTask instanceof Event)) {
            throw new BingusException("I can only use `/from` and `/to` with events.\n" + UPDATE_USAGE);
        }

        Event originalEvent = (Event) originalTask;
        String invalidDateTimeMessage = "That event date and time are not valid.\n"
                + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";
        LocalDateTime updatedDate = parseDateTime(updatedDateTime, invalidDateTimeMessage);
        LocalDateTime updatedFrom = updateField.equals("/from") ? updatedDate : originalEvent.getFrom();
        LocalDateTime updatedTo = updateField.equals("/to") ? updatedDate : originalEvent.getTo();
        if (!updatedTo.isAfter(updatedFrom)) {
            throw new BingusException("The event must end after it starts.");
        }
        return new Event(originalEvent.getDescription(), updatedFrom, updatedTo);
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
            throw new BingusException("What should I search for?\nTry `find [KEYWORD]`.");
        }

        return parts[1].trim();
    }

    /**
     * Parses a required task number using the specified missing-argument message.
     *
     * @param parts command and arguments split into at most two parts
     * @param taskCount current number of tasks
     * @param missingArgumentMessage message to show when the task number is missing
     * @return validated one-based task number
     * @throws BingusException if the number is missing or invalid
     */
    private int parseRequiredTaskId(
            String[] parts, int taskCount, String missingArgumentMessage, String extraArgumentMessage)
            throws BingusException {
        if (parts.length < 2) {
            throw new BingusException(missingArgumentMessage);
        }
        String[] taskIds = parts[1].trim().split("\\s+");
        if (taskIds.length != 1) {
            throw new BingusException(extraArgumentMessage);
        }
        return parseTaskId(taskIds[0], taskCount);
    }

    /**
     * Rejects details supplied to a command that does not accept them.
     *
     * @param parts command and arguments split into at most two parts
     * @param errorMessage message to show when unused details are supplied
     * @throws BingusException if the command has unused details
     */
    private void requireNoArguments(String[] parts, String errorMessage) throws BingusException {
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            throw new BingusException(errorMessage);
        }
    }

    /**
     * Parses the optional date argument for a list command.
     *
     * @param input date argument supplied to the list command
     * @return validated calendar date
     * @throws BingusException if more than one date is supplied or the date is invalid
     */
    private LocalDate parseSingleListDate(String input) throws BingusException {
        String[] dates = input.trim().split("\\s+");
        if (dates.length != 1) {
            throw new BingusException("I can only use one date to filter the list.\n"
                    + "Try `list [yyyy-MM-dd]`.");
        }
        return parseListDate(dates[0]);
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
            throw new BingusException("I need a description for that task.\nTry `todo [DESCRIPTION]`.");
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
        if (parts.length < 2) {
            throw new BingusException("I need a description and date for that deadline.\n" + DEADLINE_USAGE);
        }

        if (countOccurrences(parts[1], "/by") > 1) {
            throw new BingusException("I can only use one `/by` date for a deadline.\n" + DEADLINE_USAGE);
        }

        String[] deadlineParts = parts[1].split("/by");
        if (deadlineParts.length != 2) {
            throw new BingusException("I could not understand that deadline.\n" + DEADLINE_USAGE);
        }

        String description = deadlineParts[0].trim();
        String by = deadlineParts[1].trim();
        requireNonEmpty(description, "I need a description for that deadline.\n" + DEADLINE_USAGE);
        requireNonEmpty(by, "I need a date and time for that deadline.\n" + DEADLINE_USAGE);

        String invalidDateTimeMessage = "That deadline date and time are not valid.\n"
                + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";
        LocalDateTime deadlineDateTime = parseDateTime(by, invalidDateTimeMessage);
        return new Deadline(description, deadlineDateTime);
    }

    /**
     * Parses the arguments of an event command.
     *
     * @param parts command and arguments split into at most two parts
     * @return validated event task
     * @throws BingusException if the command format or date/time is invalid
     */
    public Event parseEvent(String[] parts) throws BingusException {
        if (parts.length < 2) {
            throw new BingusException("I need a description, start, and end for that event.\n" + EVENT_USAGE);
        }

        if (countOccurrences(parts[1], "/from") > 1) {
            throw new BingusException("I can only use one `/from` date for an event.\n" + EVENT_USAGE);
        }
        if (countOccurrences(parts[1], "/to") > 1) {
            throw new BingusException("I can only use one `/to` date for an event.\n" + EVENT_USAGE);
        }

        String[] descriptionAndTimes = parts[1].split("/from");
        if (descriptionAndTimes.length != 2) {
            throw new BingusException("I could not understand that event.\n" + EVENT_USAGE);
        }

        String[] startAndEnd = descriptionAndTimes[1].split("/to");
        if (startAndEnd.length != 2) {
            throw new BingusException("I could not understand that event.\n" + EVENT_USAGE);
        }

        String description = descriptionAndTimes[0].trim();
        String from = startAndEnd[0].trim();
        String to = startAndEnd[1].trim();
        requireNonEmpty(from, "I need a start date and time for that event.\n" + EVENT_USAGE);
        requireNonEmpty(to, "I need an end date and time for that event.\n" + EVENT_USAGE);
        requireNonEmpty(description, "I need a description for that event.\n" + EVENT_USAGE);

        String invalidDateTimeMessage = "That event date and time are not valid.\n"
                + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";
        LocalDateTime fromDateTime = parseDateTime(from, invalidDateTimeMessage);
        LocalDateTime toDateTime = parseDateTime(to, invalidDateTimeMessage);
        if (!toDateTime.isAfter(fromDateTime)) {
            throw new BingusException("The event must end after it starts.");
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    /**
     * Parses a date and time using the command input format.
     *
     * @param input date-time text to parse
     * @param errorMessage message to show when the text is invalid
     * @return parsed date and time
     * @throws BingusException if the text is not a valid date and time
     */
    private LocalDateTime parseDateTime(String input, String errorMessage) throws BingusException {
        try {
            return LocalDateTime.parse(input, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BingusException(errorMessage);
        }
    }

    /**
     * Ensures that a required command value is not empty.
     *
     * @param value command value to validate
     * @param errorMessage message to show when the value is empty
     * @throws BingusException if the value is empty
     */
    private void requireNonEmpty(String value, String errorMessage) throws BingusException {
        if (value.isEmpty()) {
            throw new BingusException(errorMessage);
        }
    }

    /**
     * Counts non-overlapping occurrences of a marker in text.
     *
     * @param text text to inspect
     * @param marker marker to count
     * @return number of marker occurrences in the text
     */
    private int countOccurrences(String text, String marker) {
        int occurrenceCount = 0;
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            occurrenceCount++;
            markerIndex = text.indexOf(marker, markerIndex + marker.length());
        }
        return occurrenceCount;
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
                throw new BingusException("I could not find that task number in your list.");
            }
            return taskId;
        } catch (NumberFormatException e) {
            throw new BingusException("The task number must be a whole number.");
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
            throw new BingusException("That list date is not valid.\n"
                    + "Use yyyy-MM-dd, for example 2019-12-02.");
        }
    }
}
