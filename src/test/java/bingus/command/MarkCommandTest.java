package bingus.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the state changes performed by {@link MarkCommand}.
 */
class MarkCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that marking and then unmarking a task changes and persists its completion status.
     */
    @Test
    void execute_markThenUnmark_updatesAndPersistsTaskStatus() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read JUnit guide");
        tasks.add(todo);

        new MarkCommand(1, true).execute(tasks, new Ui(), storage);

        assertTrue(todo.isDone());
        assertTrue(storage.loadTasks().get(0).isDone());

        new MarkCommand(1, false).execute(tasks, new Ui(), storage);

        assertFalse(todo.isDone());
        assertFalse(storage.loadTasks().get(0).isDone());
    }
}
