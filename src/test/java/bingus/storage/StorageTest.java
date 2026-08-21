package bingus.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
import bingus.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests saving and loading task data through {@link Storage}.
 */
class StorageTest {
    /** Directory created by JUnit for this test and removed after the test run. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that persistence retains each task's type, content, dates, and completion status.
     */
    @Test
    void saveTasks_thenLoadTasks_preservesTaskData() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        Todo todo = new Todo("buy milk | eggs");
        todo.mark();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 30, 23, 59));
        Event event = new Event("camp", LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 18, 0));

        storage.saveTasks(List.of(todo, deadline, event));

        List<Task> loadedTasks = storage.loadTasks();
        assertEquals(3, loadedTasks.size());
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));

        assertEquals("buy milk | eggs", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        assertEquals(LocalDateTime.of(2026, 8, 30, 23, 59), loadedDeadline.getBy());
        assertEquals(LocalDateTime.of(2026, 8, 20, 9, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 22, 18, 0), loadedEvent.getTo());
    }
}
