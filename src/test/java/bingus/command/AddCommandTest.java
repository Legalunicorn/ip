package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests the state changes performed by {@link AddCommand}.
 */
class AddCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that adding a task updates both the in-memory list and saved task data.
     */
    @Test
    void execute_addsTaskAndPersistsIt() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read JUnit guide");

        new AddCommand(todo).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        List<Task> savedTasks = storage.loadTasks();
        Todo savedTodo = assertInstanceOf(Todo.class, savedTasks.get(0));
        assertEquals("read JUnit guide", savedTodo.getDescription());
    }
}
