package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on {@link TaskList}.
 */
class TaskListTest {
    /**
     * Verifies that keyword searching ignores case and preserves task order.
     */
    @Test
    void findByWord_matchingDescriptions_returnsMatchingTasksInOrder() {
        Todo firstTask = new Todo("read book");
        Todo unrelatedTask = new Todo("buy groceries");
        Todo lastTask = new Todo("return BOOK");
        TaskList taskList = new TaskList(List.of(firstTask, unrelatedTask, lastTask));

        List<Task> matchingTasks = taskList.findByWord("book");

        assertEquals(List.of(firstTask, lastTask), matchingTasks);
    }

    /**
     * Verifies that replacing a task preserves the list's size and surrounding task order.
     */
    @Test
    void replace_existingTask_replacesTaskAndReturnsOriginalTask() {
        Todo firstTask = new Todo("read book");
        Todo originalTask = new Todo("buy groceries");
        Todo lastTask = new Todo("return book");
        Todo replacementTask = new Todo("buy fruit");
        TaskList taskList = new TaskList(List.of(firstTask, originalTask, lastTask));

        Task replacedTask = taskList.replace(1, replacementTask);

        assertEquals(originalTask, replacedTask);
        assertEquals(3, taskList.size());
        assertEquals(List.of(firstTask, replacementTask, lastTask), taskList.getAllTasks());
    }
}
