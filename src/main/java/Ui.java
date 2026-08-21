/**
 * Represents the layer that iterate with the users
 * through receiving input and printing output
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INDENT = "    ";


    public Ui(){
    }

    public boolean hasNextCommand(){
        return false;
    }

    public String readCommand() {
        return "";
    }

    public void showWelcome() {

    }

    public void showError(String message) {
        return;
    }

    public void showTaskAdded(Task task, int taskCount) {
        return;
    }

    public void showTaskList(TaskList tasks) {
        return;
    }
}
