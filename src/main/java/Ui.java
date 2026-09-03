import java.util.ArrayList;
import java.util.Scanner;

/**
 * Deals with all interaction with the user: printing messages, reading
 * commands, and showing the task list. Nothing outside this class talks
 * to System.out or System.in directly.
 */
public class Ui {
    private static final String LINE =
            "    ____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the divider line used to bracket each turn of the conversation. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        String banner =
                " ____   ___   ____ _  ______   __\n"
                        + "|  _ \\ / _ \\ / ___| |/ /\\ \\ / /\n"
                        + "| |_) | | | | |   | ' /  \\ V / \n"
                        + "|  _ <| |_| | |___| . \\   | |  \n"
                        + "|_| \\_\\\\___/ \\____|_|\\_\\  |_|  \n";

        showLine();
        System.out.println(banner);
        System.out.println("     Hello! I'm Rocky.");
        System.out.println("     What can I do for you?");
        showLine();
    }

    /** Reads one line of user input, with leading/trailing whitespace trimmed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the farewell message shown when the user types "bye". */
    public void showGoodbye() {
        System.out.println("     Bye. Hope to see you again soon!");
    }

    /** Prints an error message, e.g. from a caught RockyException. */
    public void showError(String message) {
        System.out.println("     " + message);
    }

    /** Prints the task list, or a friendly message if it's empty. */
    public void showTaskList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("     Your list is empty. Add something!");
            return;
        }
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Confirms that a task was added, and reports the new list size. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + describeCount(taskCount) + " in the list.");
    }

    /** Confirms that a task was removed, and reports the new list size. */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + describeCount(taskCount) + " in the list.");
    }

    /** Confirms that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + task);
    }

    /** Confirms that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
    }

    /** Releases the input scanner. Call once, when the program is exiting. */
    public void close() {
        scanner.close();
    }

    private String describeCount(int n) {
        return n + (n == 1 ? " task" : " tasks");
    }
}
