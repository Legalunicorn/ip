package bingus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import bingus.storage.Storage;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the state changes performed by {@link DeleteCommand}.
 */
class DeleteCommandTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that deleting a one-based task number keeps the remaining tasks in order and saves them.
     */
    @Test
    void execute_deletesSelectedTaskAndPersistsRemainingTaskOrder() {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        new DeleteCommand(2).execute(tasks, new Ui(), storage);

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(1).getDescription());
        List<Task> savedTasks = storage.loadTasks();
        assertEquals(2, savedTasks.size());
        assertEquals("first", savedTasks.get(0).getDescription());
        assertEquals("third", savedTasks.get(1).getDescription());
    }
}
