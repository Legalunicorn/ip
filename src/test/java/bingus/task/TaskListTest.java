package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests keyword searching in {@link TaskList}.
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
}
