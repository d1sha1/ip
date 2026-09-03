import java.util.ArrayList;

/**
 * Wraps the in-memory list of tasks and provides the operations the rest
 * of the app needs: adding, removing, and looking up tasks by index.
 * Callers are expected to have already validated that an index is in
 * range (see Rocky#parseIndex) before calling get()/delete().
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Wraps an existing list of tasks, e.g. one just loaded from disk. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given 0-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at the given 0-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns true if the list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the underlying list of tasks, for callers (Ui, Storage) that
     * need to iterate over every task rather than address one by index.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
