import java.util.Scanner;

public class Rocky {
    private static final String LINE =
            "    ____________________________________________________________";
    private static final Task[] tasks = new Task[100];
    private static int count = 0;

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
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < count; i++) {
            System.out.println("     " + (i + 1) + "." + tasks[i]);
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

        if (count >= tasks.length) {
            throw new RockyException("My list is full, I can't take any more tasks.");
        }

        tasks[count] = task;
        count++;
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + count
                + (count == 1 ? " task" : " tasks") + " in the list.");
    }

    private static void setDone(String input, boolean done) throws RockyException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new RockyException(
                    "Which task? Give me a number, like: " + parts[0] + " 2");
        }

        int index;
        try {
            index = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new RockyException(
                    "\"" + parts[1].trim() + "\" isn't a number I can work with.");
        }

        if (index < 0 || index >= count) {
            throw new RockyException(count == 0
                    ? "Your list is empty, so there's nothing to " + parts[0] + "."
                    : "You only have " + count + " task(s), so there's no #"
                    + (index + 1) + ".");
        }

        Task task = tasks[index];

        if (done) {
            task.mark();
            System.out.println("     Nice! I've marked this task as done:");
        } else {
            task.unmark();
            System.out.println("     OK, I've marked this task as not done yet:");
        }
        System.out.println("       " + task);
    }
}