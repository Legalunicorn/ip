package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between task types and their symbols.
 */
class TaskTypeTest {

    @Test
    void fromSymbol_knownSymbols_returnsCorrespondingTaskTypes() {
        assertEquals(TaskType.TODO, TaskType.fromSymbol("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromSymbol("D"));
        assertEquals(TaskType.EVENT, TaskType.fromSymbol("E"));
    }

    @Test
    void fromSymbol_unknownSymbol_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromSymbol("X"));
    }
}
