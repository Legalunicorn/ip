package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of {@link Todo} tasks.
 */
class TodoTest {
    /**
     * Verifies that a newly-created todo is initially incomplete.
     */
    @Test
    void getStatusIcon_newTodo_returnsIncompleteIcon() {
        Todo todo = new Todo("read the JUnit guide");

        assertEquals("○", todo.getStatusIcon());
    }

    @Test
    void getStatisIcon_todoMarked_returnsMarkedIcon() {
        Todo todo = new Todo("");
        todo.mark();
        assertEquals("✓", todo.getStatusIcon());
    }
}
