/**
 * The three kinds of task Rocky understands, and the command keyword and
 * on-disk save-file icon that identify each one.
 */
public enum TaskType {
    /** A task with no date attached, e.g. "todo read book". */
    TODO("todo", "T"),
    /** A task with a single due date, e.g. "deadline return book /by ...". */
    DEADLINE("deadline", "D"),
    /** A task with a start and end time, e.g. "event meeting /from ... /to ...". */
    EVENT("event", "E");

    private final String keyword;
    private final String icon;

    TaskType(String keyword, String icon) {
        this.keyword = keyword;
        this.icon = icon;
    }

    /**
     * Returns the command word the user types to add this kind of task, e.g. "todo".
     *
     * @return this task type's command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the single-letter icon used in the save file, e.g. "T".
     *
     * @return this task type's single-letter save-file icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Looks up the TaskType whose command keyword matches the given word.
     *
     * @param word the first word of the user's input, e.g. "deadline".
     * @return the matching TaskType, or null if no TaskType uses that keyword.
     */
    public static TaskType fromKeyword(String word) {
        for (TaskType type : values()) {
            if (type.keyword.equals(word)) {
                return type;
            }
        }
        return null;
    }
}
