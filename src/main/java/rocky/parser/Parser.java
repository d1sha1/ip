package rocky.parser;

import rocky.RockyException;
import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.TaskType;
import rocky.task.ToDo;

/**
 * Turns raw user input into the pieces the rest of the app needs: the
 * command word, a new Task for a todo/deadline/event command, and a
 * validated 0-based index for mark/unmark/delete.
 */
public class Parser {

    private Parser() {
        // Utility class; not meant to be instantiated.
    }

    /** Returns the first word of the input, e.g. "todo" from "todo read book". */
    public static String getCommandWord(String input) {
        return input.split(" ", 2)[0];
    }

    /**
     * Builds a new Task from a todo/deadline/event command, validating that
     * it has the description (and dates, for deadline/event) it needs.
     */
    public static Task parseNewTask(TaskType type, String input) throws RockyException {
        String body = input.length() > type.getKeyword().length()
                ? input.substring(type.getKeyword().length()).trim()
                : "";

        if (body.isEmpty()) {
            throw new RockyException(
                    "A " + type.getKeyword() + " needs a description. Try again?");
        }

        switch (type) {
            case TODO:
                return new ToDo(body);
            case DEADLINE: {
                String[] parts = body.split(" /by ", 2);
                if (parts.length < 2 || parts[0].trim().isEmpty()
                        || parts[1].trim().isEmpty()) {
                    throw new RockyException(
                            "A deadline needs a due date. Format: "
                                    + "deadline <task> /by <when>");
                }
                return new Deadline(parts[0].trim(), parts[1].trim());
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
                return new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
            }
            default:
                return null;
        }
    }

    /** Extracts and validates a 1-based task number, returning a 0-based index. */
    public static int parseIndex(String input, int taskCount) throws RockyException {
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

        if (index < 0 || index >= taskCount) {
            throw new RockyException(taskCount == 0
                    ? "Your list is empty, so there's nothing to " + command + "."
                    : "You only have " + taskCount + " task(s), so there's no #"
                    + (index + 1) + ".");
        }

        return index;
    }
}
