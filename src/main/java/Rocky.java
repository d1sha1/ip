import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Entry point and command loop for Rocky, a simple text-based chatbot for
 * tracking todos, deadlines, and events. Reads user commands from standard
 * input, keeps an in-memory list of tasks, and persists that list to a save
 * file under {@code data/} after every change.
 */
public class Rocky {
    private static final String LINE =
            "    ____________________________________________________________";
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = "duke.txt";

    /** Not meant to be instantiated; every member here is static. */
    private Rocky() {
    }

    /**
     * Loads any previously saved tasks, greets the user, and then repeatedly
     * reads and executes one command per line until the user types "bye".
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        // Load any previously saved tasks before greeting the user.
        load();

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
                } else if (commandWord.equals("find")) {
                    findTasks(input);
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

    /** Prints every task in the list, or a friendly message if it's empty. */
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

    /**
     * Finds and prints every task whose description contains the given
     * keyword (case-insensitive), e.g. "find book".
     *
     * @param input the full "find ..." command line.
     * @throws RockyException if no keyword is given.
     */
    private static void findTasks(String input) throws RockyException {
        String keyword = input.length() > "find".length()
                ? input.substring("find".length()).trim()
                : "";

        if (keyword.isEmpty()) {
            throw new RockyException(
                    "What should I search for? Try again, e.g.: find book");
        }

        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(task);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("     No matching tasks found.");
            return;
        }
        System.out.println("     Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println("     " + (i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Parses and adds a new todo/deadline/event task from a full command
     * line (e.g. "deadline return book /by 2019-12-01"), then saves and
     * confirms the addition to the user.
     *
     * @param type which kind of task the command word identified.
     * @param input the full, untrimmed-of-command-word user input line.
     * @throws RockyException if the description or required dates are
     *     missing, malformed, or in the wrong format.
     */
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
        save();
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + describeCount() + " in the list.");
    }

    /**
     * Removes the task named by a "delete N" command, saves, and confirms
     * the removal to the user.
     *
     * @param input the full "delete ..." command line.
     * @throws RockyException if no task number is given, it isn't a number,
     *     or it's out of range.
     */
    private static void deleteTask(String input) throws RockyException {
        int index = parseIndex(input);
        Task removed = tasks.remove(index);
        save();
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + removed);
        System.out.println("     Now you have " + describeCount() + " in the list.");
    }

    /**
     * Marks or unmarks the task named by a "mark N"/"unmark N" command,
     * saves, and confirms the change to the user.
     *
     * @param input the full "mark ..."/"unmark ..." command line.
     * @param done true to mark the task done, false to mark it not done.
     * @throws RockyException if no task number is given, it isn't a number,
     *     or it's out of range.
     */
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
        save();
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

    /** Returns "N task" or "N tasks" as appropriate for the current list size. */
    private static String describeCount() {
        int n = tasks.size();
        return n + (n == 1 ? " task" : " tasks");
    }

    /**
     * Loads previously saved tasks from {@code data/duke.txt} into
     * {@link #tasks}. Does nothing if the file doesn't exist yet (e.g. first
     * run). A line that can't be parsed is skipped with a warning, without
     * stopping the rest of the file from loading.
     */
    private static void load() {
        File file = new File(DATA_DIR, DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    try {
                        tasks.add(lineToTask(line));
                    } catch (RockyException e) {
                        System.out.println("     Warning: skipped an unreadable saved task.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("     Warning: couldn't read saved tasks.");
        }
    }

    /**
     * Saves {@link #tasks} to {@code data/duke.txt}, overwriting whatever
     * was there before. Creates the data folder/file first if they don't
     * exist yet.
     */
    private static void save() {
        try {
            File file = ensureDataFile(DATA_DIR, DATA_FILE);
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(taskToLine(task) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: couldn't save tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Converts a task to its one-line save-file representation, e.g.
     * {@code "D | 0 | return book | 2019-12-01"}.
     *
     * @param task the task to encode.
     * @return the line to write to the save file for this task.
     */
    private static String taskToLine(Task task) {
        String done = task.isDone() ? "1" : "0";
        String line = " | " + done + " | " + task.getDescription();

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + line + " | " + deadline.getDate();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E" + line + " | " + event.getStartDate() + " | " + event.getEndDate();
        } else {
            return "T" + line;
        }
    }

    /**
     * Parses one save-file line back into a Task.
     *
     * @param line a line previously produced by {@link #taskToLine}.
     * @return the reconstructed task, with its done state restored.
     * @throws RockyException if the line is malformed, e.g. missing fields
     *     or an unparsable date.
     */
    private static Task lineToTask(String line) throws RockyException {
        String[] parts = line.split("\\|");
        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task;
        switch (type) {
            case "D":
                task = new Deadline(description, parts[3].trim());
                break;
            case "E":
                task = new Event(description, parts[3].trim(), parts[4].trim());
                break;
            default:
                task = new ToDo(description);
                break;
        }

        if (isDone) {
            task.mark();
        }
        return task;
    }

    /**
     * Ensures the save folder and file exist, creating them if necessary.
     *
     * @param folderName the folder the save file should live in, e.g. "data".
     * @param fileName the save file's name, e.g. "duke.txt".
     * @return the (now guaranteed to exist) save file.
     * @throws IOException if the folder or file can't be created.
     */
    private static File ensureDataFile(String folderName, String fileName) throws IOException {
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

}
