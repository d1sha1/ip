import java.util.ArrayList;


public class Rocky {
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "duke.txt";
    private static final Storage storage = new Storage(DATA_DIR, DATA_FILE);
    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        // Load any previously saved tasks before greeting the user.
        tasks.addAll(storage.load());

        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();
            String commandWord = input.split(" ", 2)[0];
            ui.showLine();

            try {
                if (input.equals("bye")) {
                    ui.showGoodbye();
                    ui.showLine();
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
                ui.showError(e.getMessage());
            }

            ui.showLine();
        }

        ui.close();
    }

    private static void printList() {
        ui.showTaskList(tasks);
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
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void deleteTask(String input) throws RockyException {
        int index = parseIndex(input);
        Task removed = tasks.remove(index);
        storage.save(tasks);
        ui.showTaskRemoved(removed, tasks.size());
    }

    private static void setDone(String input, boolean done) throws RockyException {
        int index = parseIndex(input);
        Task task = tasks.get(index);

        if (done) {
            task.mark();
        } else {
            task.unmark();
        }
        storage.save(tasks);

        if (done) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
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

}
