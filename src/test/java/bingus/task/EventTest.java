package bingus.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for {@link Event} tasks.
 */
public class EventTest {

    @Test
    void matchesDate_dateWithinMultipleDayEvent_returnsTrue() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));
        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 21)));
    }

    @Test
    void matchesDate_dateBeforeEvent_returnsFalse() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));
        assertFalse(event.matchesDate(LocalDate.of(2026, 7, 21)));
    }

    @Test
    void matchesDate_startAndEndDate_returnsTrue() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 20)));
        assertTrue(event.matchesDate(LocalDate.of(2026, 8, 22)));
    }

    @Test
    void matchesDate_dateAfterEvent_returnsFalse() {
        Event event = new Event(
                "camp",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        assertFalse(event.matchesDate(LocalDate.of(2026, 8, 23)));
    }
}
