/**
 * Base class for anything Rocky's task list can hold: a description, and
 * whether it's been marked done. Subclasses ({@link ToDo}, {@link Deadline},
 * {@link Event}) add whatever extra details their kind of task needs (e.g.
 * dates), and customise how they print via {@link #toString()}.
 */
public abstract class Task {
    private String description;
    private Boolean isDone;

    /**
     * Creates a task with the given description and completion state.
     *
     * @param description what the task is, e.g. "read book".
     * @param isDone whether the task starts out already marked done.
     */
    public Task(String description, Boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /** Marks this task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the "[x]"/"[ ]" checkbox text shown before the description.
     *
     * @return "[x]" if this task is done, "[ ]" otherwise.
     */
    public String getCheckbox() {
        return this.isDone ? "[x]" : "[ ]";
    }

    /**
     * Returns the task's description, e.g. "read book".
     *
     * @return the description passed to the constructor.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns true if this task has been marked done.
     *
     * @return true if {@link #mark()} was called more recently than {@link #unmark()}
     *     (or the task was constructed already done); false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /** Returns the checkbox followed by the description, e.g. "[x] read book". */
    @Override
    public String toString() {
        return this.getCheckbox() + " " + this.description;
    }

}
