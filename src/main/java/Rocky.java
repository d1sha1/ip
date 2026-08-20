import java.util.ArrayList;
import java.util.Scanner;

public class Rocky {
    private static final String LINE =
            "    ____________________________________________________________";
    private static final ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        String banner =
                " ____   ___   ____ _  ______   __\n"
                        + "|  _ \\ / _ \\ / ___| |/ /\\ \\ / /\n"
                        + "| |_) | | | | |   | ' /  \\ V / \n"
                        + "|  _ <| |_| | |___| . \\   | |  \n"
                        + "|_| \\_\\\\___/ \\____|_|\\_\\  |_|  \n";

        System.out.println(LINE);
        System.out.println(banner);
        System.out.println("     Hello! I'm Rocky.");
        System.out.println("     What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim();
            String commandWord = input.split(" ", 2)[0];
            System.out.println(LINE);

            try {
                if (input.equals("bye")) {
                    System.out.println("     Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;
                } else if (input.equals("list")) {
                    printList();
                } else if (commandWord.equals("mark")) {
                    setDone(input, true);
                } else if (commandWord.equals("unmark")) {
                    setDone(input, false);
                } else if (commandWord.equals("delete")) {
                    deleteTask(input);
                } else {
                    TaskType type = TaskType.fromKeyword(commandWord);
                    if (type != null) {
                        addTask(type, input);
                    } else {
                        throw new RockyException(
                                "Hmm, \"" + commandWord + "\" isn't a command I know.");
                    }
                }
            } catch (RockyException e) {
                System.out.println("     " + e.getMessage());
            }

            System.out.println(LINE);
        }

        scanner.close();
    }

    private static void printList() {
        if (tasks.isEmpty()) {
            System.out.println("     Your list is empty. Add something!");
            return;
        }
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    private static void addTask(TaskType type, String input) throws RockyException {
        String body = input.length() > type.getKeyword().length()
                ? input.substring(type.getKeyword().length()).trim()
                : "";

        if (body.isEmpty()) {
            throw new RockyException(
                    "A " + type.getKeyword() + " needs a description. Try again?");
        }

        Task task;

        switch (type) {
            case TODO:
                task = new ToDo(body);
                break;
            case DEADLINE: {
                String[] parts = body.split(" /by ", 2);
                if (parts.length < 2 || parts[0].trim().isEmpty()
                        || parts[1].trim().isEmpty()) {
                    throw new RockyException(
                            "A deadline needs a due date. Format: "
                                    + "deadline <task> /by <when>");
                }
                task = new Deadline(parts[0].trim(), parts[1].trim());
                break;
            }
            case EVENT: {
                String[] fromParts = body.split(" /from ", 2);
                if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
                    throw new RockyException(
                            "An event needs a start time. Format: "
                                    + "event <task> /from <start> /to <end>");
                }
                String[] toParts = fromParts[1].split(" /to ", 2);
                if (toParts.length < 2 || toParts[0].trim().isEmpty()
                        || toParts[1].trim().isEmpty()) {
                    throw new RockyException(
                            "An event needs an end time. Format: "
                                    + "event <task> /from <start> /to <end>");
                }
                task = new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
                break;
            }
            default:
                return;
        }

        tasks.add(task);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + describeCount() + " in the list.");
    }

    private static void deleteTask(String input) throws RockyException {
        int index = parseIndex(input);
        Task removed = tasks.remove(index);
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + removed);
        System.out.println("     Now you have " + describeCount() + " in the list.");
    }

    private static void setDone(String input, boolean done) throws RockyException {
        int index = parseIndex(input);
        Task task = tasks.get(index);

        if (done) {
            task.mark();
            System.out.println("     Nice! I've marked this task as done:");
        } else {
            task.unmark();
            System.out.println("     OK, I've marked this task as not done yet:");
        }
        System.out.println("       " + task);
    }

    /** Extracts and validates a 1-based task number, returning a 0-based index. */
    private static int parseIndex(String input) throws RockyException {
        String[] parts = input.split(" ", 2);
        String command = parts[0];

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new RockyException(
                    "Which task? Give me a number, like: " + command + " 2");
        }

        int index;
        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new RockyException(
                    "\"" + parts[1].trim() + "\" isn't a number I can work with.");
        }

        if (index < 0 || index >= tasks.size()) {
            throw new RockyException(tasks.isEmpty()
                    ? "Your list is empty, so there's nothing to " + command + "."
                    : "You only have " + tasks.size() + " task(s), so there's no #"
                    + (index + 1) + ".");
        }

        return index;
    }

    private static String describeCount() {
        int n = tasks.size();
        return n + (n == 1 ? " task" : " tasks");
    }
}