package bingus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the GUI-facing command-response API of {@link Bingus}.
 */
class BingusTest {
    /** Directory created by JUnit for isolated task save files. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Creates a Bingus instance with a test-specific save file.
     *
     * @return Bingus instance backed by a temporary save file
     */
    private Bingus createBingus() {
        return new Bingus(temporaryDirectory.resolve("data/bingus.txt").toString());
    }

    /**
     * Verifies that adding a task produces a response and lists an incomplete task icon.
     */
    @Test
    void getResponse_todoThenList_returnsAddedIncompleteTask() {
        Bingus bingus = createBingus();

        String addResponse = bingus.getResponse("todo read book");
        String listResponse = bingus.getResponse("list");

        assertTrue(addResponse.contains("read book"));
        assertEquals("ListCommand", bingus.getCommandType());
        assertTrue(listResponse.contains("○"));
        assertTrue(listResponse.contains("read book"));
    }

    /**
     * Verifies that marking a task changes its displayed status icon.
     */
    @Test
    void getResponse_markThenList_returnsCompletedTask() {
        Bingus bingus = createBingus();
        bingus.getResponse("todo read book");

        String markResponse = bingus.getResponse("mark 1");
        String listResponse = bingus.getResponse("list");

        assertTrue(markResponse.contains("✓"));
        assertTrue(listResponse.contains("✓"));
        assertTrue(listResponse.contains("read book"));
    }

    /**
     * Verifies that an invalid command returns an error and clears the command type used by the GUI.
     */
    @Test
    void getResponse_invalidCommand_returnsErrorAndClearsCommandType() {
        Bingus bingus = createBingus();

        String response = bingus.getResponse("nonsense");

        assertTrue(response.startsWith("Error:"));
        assertEquals("", bingus.getCommandType());
    }

    /**
     * Verifies that the bye command produces a farewell response for the GUI to display.
     */
    @Test
    void getResponse_bye_returnsFarewellAndExitCommandType() {
        Bingus bingus = createBingus();

        String response = bingus.getResponse("bye");

        assertTrue(response.contains("Bye!"));
        assertEquals("ExitCommand", bingus.getCommandType());
    }

    /**
     * Verifies that tasks added through the response API are loaded by a later Bingus instance.
     */
    @Test
    void getResponse_addedTask_isLoadedByNewInstance() {
        Bingus firstBingus = createBingus();
        firstBingus.getResponse("todo persist task");

        Bingus reloadedBingus = createBingus();
        String listResponse = reloadedBingus.getResponse("list");

        assertTrue(listResponse.contains("persist task"));
    }
}
