package bingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.command.Command;
import bingus.command.FindCommand;
import bingus.command.UpdateCommand;
import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests command parsing and validation.
 */
public class ParserTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Creates storage backed by a test-specific save file.
     *
     * @return storage for executing parsed commands
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
    }

    @Test
    void parseUpdateDescription_todo_returnsExecutableUpdateCommand() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        Command command = parser.parse("update 1 /desc read two chapters", tasks);
        command.execute(tasks, new Ui(), createStorage());

        assertInstanceOf(UpdateCommand.class, command);
        Todo updatedTodo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("read two chapters", updatedTodo.getDescription());
    }

    @Test
    void parseUpdateDescription_deadline_preservesDueDate() {
        Parser parser = new Parser();
        LocalDateTime dueDate = LocalDateTime.of(2026, 9, 15, 23, 59);
        TaskList tasks = new TaskList(List.of(new Deadline("submit draft", dueDate)));

        Command command = parser.parse("update 1 /desc submit final report", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Deadline updatedDeadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("submit final report", updatedDeadline.getDescription());
        assertEquals(dueDate, updatedDeadline.getBy());
    }

    @Test
    void parseUpdateDescription_event_preservesStartAndEndTimes() {
        Parser parser = new Parser();
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 10, 16, 0);
        TaskList tasks = new TaskList(List.of(new Event("project meeting", startTime, endTime)));

        Command command = parser.parse("update 1 /desc weekly team meeting", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("weekly team meeting", updatedEvent.getDescription());
        assertEquals(startTime, updatedEvent.getFrom());
        assertEquals(endTime, updatedEvent.getTo());
    }

    @Test
    void parseUpdateDeadline_validDateTime_preservesDescriptionAndUpdatesDueDate() {
        Parser parser = new Parser();
        LocalDateTime originalDueDate = LocalDateTime.of(2026, 9, 15, 23, 59);
        TaskList tasks = new TaskList(List.of(new Deadline("submit report", originalDueDate)));

        Command command = parser.parse("update 1 /by 2026-09-20 1800", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Deadline updatedDeadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("submit report", updatedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 0), updatedDeadline.getBy());
    }

    @Test
    void parseUpdateDeadline_missingDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 23, 59))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by", tasks));
    }

    @Test
    void parseUpdateDeadline_invalidDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 23, 59))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-02-30 1200", tasks));
    }

    @Test
    void parseUpdateDeadline_todo_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-09-20 1800", tasks));
    }

    @Test
    void parseUpdateDeadline_event_throwsException() {
        Parser parser = new Parser();
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(new Event("project meeting", startTime, endTime)));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-09-20 1800", tasks));
    }

    @Test
    void parseUpdateEvent_validStartTime_preservesDescriptionAndEndTime() {
        Parser parser = new Parser();
        LocalDateTime originalStartTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime originalEndTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Event("project meeting", originalStartTime, originalEndTime)));

        Command command = parser.parse("update 1 /from 2026-09-20 1500", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("project meeting", updatedEvent.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 20, 15, 0), updatedEvent.getFrom());
        assertEquals(originalEndTime, updatedEvent.getTo());
    }

    @Test
    void parseUpdateEvent_validEndTime_preservesDescriptionAndStartTime() {
        Parser parser = new Parser();
        LocalDateTime originalStartTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime originalEndTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Event("project meeting", originalStartTime, originalEndTime)));

        Command command = parser.parse("update 1 /to 2026-09-20 1730", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("project meeting", updatedEvent.getDescription());
        assertEquals(originalStartTime, updatedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 20, 17, 30), updatedEvent.getTo());
    }

    @Test
    void parseUpdateEvent_missingDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from", tasks));
    }

    @Test
    void parseUpdateEvent_invalidDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-02-30 1200", tasks));
    }

    @Test
    void parseUpdateEvent_startNotBeforeEnd_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from 2026-09-20 1600", tasks));
    }

    @Test
    void parseUpdateEvent_endNotAfterStart_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-09-20 1400", tasks));
    }

    @Test
    void parseUpdateEvent_todo_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from 2026-09-20 1500", tasks));
    }

    @Test
    void parseUpdateEvent_deadline_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-09-20 1900", tasks));
    }

    @Test
    void parseUpdate_missingArguments_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parse("update", new TaskList()));
    }

    @Test
    void parseUpdate_missingField_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1", tasks));
    }

    @Test
    void parseUpdate_missingDescription_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /desc", tasks));
    }

    @Test
    void parseUpdate_unknownField_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /unknown new value", tasks));
    }

    @Test
    void parseUpdate_outOfRangeTaskId_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 2 /desc read two chapters", tasks));
    }

    @Test
    void parseFind_validKeyword_returnsFindCommand() {
        Parser parser = new Parser();

        assertInstanceOf(FindCommand.class, parser.parse("find book", new TaskList()));
    }

    @Test
    void parseFind_missingKeyword_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parse("find", new TaskList()));
    }

    @Test
    void parseDeadline_inputsLengthOne_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"deadline"};
        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithoutDelimiter_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"deadline", "no-delimiter"};
        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithDelimiterAndLocalDateTime_returnsDeadline() {
        Parser parser = new Parser();
        String[] properParts = {"deadline", "soup /by 1212-12-12 1212"};
        Deadline deadline = parser.parseDeadline(properParts);
        assertEquals("soup", deadline.getDescription());
        assertEquals(
                LocalDateTime.of(1212, 12, 12, 12, 12),
                deadline.getBy());
    }

    @Test
    void parseDeadline_emptyDescription_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"deadline", " /by 2026-08-30 2359"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseDeadline(parts));

        assertEquals("Task description cannot be empty. "
                + "Please use `deadline [DESCRIPTION] /by [DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseDeadline_emptyDateTime_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"deadline", "submit report /by "};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseDeadline(parts));

        assertEquals("Deadline date/time cannot be empty. "
                + "Please use `deadline [DESCRIPTION] /by [DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseDeadline_invalidCalendarDate_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"deadline", "submit report /by 2026-02-30 1200"};

        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseTodo_descriptionWithSurroundingWhitespace_returnsTrimmedTodo() {
        Parser parser = new Parser();
        String[] parts = {"todo", "  read JUnit guide  "};

        Todo todo = parser.parseTodo(parts);

        assertEquals("read JUnit guide", todo.getDescription());
    }

    @Test
    void parseTodo_blankDescription_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"todo", "   "};

        assertThrows(BingusException.class, () -> parser.parseTodo(parts));
    }

    @Test
    void parseEvent_validTimes_returnsEventWithCorrectValues() {
        Parser parser = new Parser();
        String[] parts = {"event", "camp /from 2026-08-20 0900 /to 2026-08-22 1800"};

        Event event = parser.parseEvent(parts);

        assertEquals("camp", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 20, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 22, 18, 0), event.getTo());
    }

    @Test
    void parseEvent_endNotAfterStart_throwsException() {
        Parser parser = new Parser();
        String[] parts = {"event", "meeting /from 2026-08-20 0900 /to 2026-08-20 0900"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseEvent(parts));

        assertEquals("Event end date/time must be after its start date/time.", exception.getMessage());
    }

    @Test
    void parseTaskId_validId_returnsId() {
        Parser parser = new Parser();
        assertEquals(2, parser.parseTaskId("2", 3));
    }

    @Test
    void parseTaskId_idWithSurroundingWhitespace_returnsId() {
        Parser parser = new Parser();

        assertEquals(3, parser.parseTaskId(" 3 ", 3));
    }

    @Test
    void parseTaskId_zero_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseTaskId("0", 3));
    }

    @Test
    void parseTaskId_idGreaterThanTaskCount_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseTaskId("4", 3));
    }

    @Test
    void parseTaskId_nonNumericInput_throwsException() {
        Parser parser = new Parser();

        BingusException exception = assertThrows(
                BingusException.class, () -> parser.parseTaskId("two", 3));

        assertEquals("Task number must be a whole number.", exception.getMessage());
    }

    @Test
    void parseListDate_validDate_returnsDate() {
        Parser parser = new Parser();

        assertEquals(LocalDate.of(2026, 8, 21), parser.parseListDate("2026-08-21"));
    }

    @Test
    void parseListDate_invalidCalendarDate_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parseListDate("2026-02-30"));
    }
}
