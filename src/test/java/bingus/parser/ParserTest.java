package bingus.parser;

import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Todo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {


    @Test
    void parseDeadline_inputsLengthOne_throwsException() {
        Parser p = new Parser();
        String[] parts = {"deadline"};
        assertThrows(BingusException.class, () -> p.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithoutDelimiter_throwsException(){
       Parser p = new Parser();
       String[] parts = {"deadline", "no-delimiter"};
       assertThrows(BingusException.class, () -> p.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithDelimiterAndLocalDateTime_returnsDeadline() {
        Parser p = new Parser();
        String[] properParts = {"deadline", "soup /by 1212-12-12 1212"};
        Deadline d = p.parseDeadline(properParts);
        assertEquals("soup", d.getDescription());
        assertEquals(
                LocalDateTime.of(1212, 12, 12, 12, 12),
                d.getBy());
    }


    @Test
    void parseDeadline_emptyDescription_throwsException() {
        Parser p = new Parser();
        String[] parts = {"deadline", " /by 2026-08-30 2359"};

        assertThrows(BingusException.class, () -> p.parseDeadline(parts));
    }

    @Test
    void parseDeadline_emptyDateTime_throwsException() {
        Parser p = new Parser();
        String[] parts = {"deadline", "submit report /by "};

        assertThrows(BingusException.class, () -> p.parseDeadline(parts));
    }

    @Test
    void parseDeadline_invalidCalendarDate_throwsException() {
        Parser p = new Parser();
        String[] parts = {"deadline", "submit report /by 2026-02-30 1200"};

        assertThrows(BingusException.class, () -> p.parseDeadline(parts));
    }

    @Test
    void parseTodo_descriptionWithSurroundingWhitespace_returnsTrimmedTodo() {
        Parser p = new Parser();
        String[] parts = {"todo", "  read JUnit guide  "};

        Todo todo = p.parseTodo(parts);

        assertEquals("read JUnit guide", todo.getDescription());
    }

    @Test
    void parseTodo_blankDescription_throwsException() {
        Parser p = new Parser();
        String[] parts = {"todo", "   "};

        assertThrows(BingusException.class, () -> p.parseTodo(parts));
    }

    @Test
    void parseEvent_validTimes_returnsEventWithCorrectValues() {
        Parser p = new Parser();
        String[] parts = {"event", "camp /from 2026-08-20 0900 /to 2026-08-22 1800"};

        Event event = p.parseEvent(parts);

        assertEquals("camp", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 20, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 22, 18, 0), event.getTo());
    }

    @Test
    void parseEvent_endNotAfterStart_throwsException() {
        Parser p = new Parser();
        String[] parts = {"event", "meeting /from 2026-08-20 0900 /to 2026-08-20 0900"};

        assertThrows(BingusException.class, () -> p.parseEvent(parts));
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

        assertThrows(BingusException.class, () -> parser.parseTaskId("two", 3));
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
