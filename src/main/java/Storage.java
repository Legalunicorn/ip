import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Utility class for persistent storage using files
 */
public class Storage {

    private final Path saveFile;

    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }


    /**
     * Loads all tasks from the save file when it is valid. If the file cannot
     * be read or contains invalid data, leaves the task list empty.
     */
    public List<Task> loadTasks() throws BingusException {
        if (Files.notExists(saveFile)) {
            return new ArrayList<>();
        }
        try {
            List<Task> loadedTasks = new ArrayList<>();
            for (String savedTask : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
                loadedTasks.add(fromSaveRecord(savedTask));
            }
            return loadedTasks;
        } catch (IOException | IllegalArgumentException | IllegalStateException | SecurityException e) {
            throw new BingusException("Uh-oh.. I couldn't load your saved tasks, sorry! ");
        }
    }

    /**
     * Writes the complete task list to disk. Text fields are Base64 encoded so
     * the record separator cannot conflict with text entered by the user.
     */
    public void saveTasks(List<Task> tasks) throws BingusException {
        List<String> savedTasks = new ArrayList<>();
        for (Task task : tasks) {
            savedTasks.add(toSaveRecord(task));
        }

        Path temporaryFile = null;
        try {
            Files.createDirectories(saveFile.getParent());
            temporaryFile = Files.createTempFile(saveFile.getParent(), "bingus-", ".tmp");
            Files.write(temporaryFile, savedTasks, StandardCharsets.UTF_8);
            moveSaveFile(temporaryFile);
        } catch (IOException | SecurityException e) {
            throw new BingusException("I couldn't save your tasks. Your task list was not changed.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // A later run can safely remove an unused temporary file.
                }
            }
        }
    }

    /**
     * Replaces the save file with a complete temporary file, avoiding a
     * partially written save file if writing is interrupted.
     *
     * @param temporaryFile complete temporary save file
     * @throws IOException if the file cannot be replaced
     */
    private void moveSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one task to a delimiter-safe record for the save file.
     *
     * @param task task to convert
     * @return save-file record
     */
    private static String toSaveRecord(Task task) {
        String done = task.isDone() ? "1" : "0";
        String description = encode(task.getDescription());
        switch (task.getType()) {
            case TODO:
                return "T|" + done + "|" + description;
            case DEADLINE:
                Deadline deadline = (Deadline) task;
                return "D|" + done + "|" + description + "|" + encode(deadline.getBy().toString());
            case EVENT:
                Event event = (Event) task;
                return "E|" + done + "|" + description + "|" + encode(event.getFrom().toString())
                        + "|" + encode(event.getTo().toString());
            default:
                throw new IllegalStateException("Unsupported task type: " + task.getType());
        }
    }

    /**
     * Encodes arbitrary task text for safe storage in a pipe-separated record.
     *
     * @param value text to encode
     * @return Base64 representation of {@code value}
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes text that was Base64 encoded before being saved.
     *
     * @param value Base64 text from the save file
     * @return original task text
     */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /**
     * Reconstructs a task from one delimiter-safe save-file record.
     *
     * @param savedTask task record from the save file
     * @return reconstructed task
     */
    private static Task fromSaveRecord(String savedTask) {
        String[] fields = savedTask.split("\\|", -1);
        Task task;
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3, savedTask);
                task = new Todo(decode(fields[2]));
                break;
            case "D":
                requireFieldCount(fields, 4, savedTask);
                task = new Deadline(decode(fields[2]), LocalDateTime.parse(decode(fields[3])));
                break;
            case "E":
                requireFieldCount(fields, 5, savedTask);
                task = new Event(decode(fields[2]), LocalDateTime.parse(decode(fields[3])),
                        LocalDateTime.parse(decode(fields[4])));
                break;
            default:
                throw new IllegalStateException("Unknown task type in save file: " + fields[0]);
        }

        if (fields[1].equals("1")) {
            task.mark();
        } else if (!fields[1].equals("0")) {
            throw new IllegalStateException("Invalid completion status in save file: " + fields[1]);
        }
        return task;
    }

    /**
     * Ensures a record has the expected number of fields before it is decoded.
     *
     * @param fields fields in the record
     * @param expectedCount expected number of fields
     * @param savedTask original save-file record
     */
    private static void requireFieldCount(String[] fields, int expectedCount, String savedTask) {
        if (fields.length != expectedCount) {
            throw new IllegalStateException("Invalid task record in save file: " + savedTask);
        }
    }
}
