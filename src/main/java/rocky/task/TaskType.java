package rocky.task;

public enum TaskType {
    TODO("todo", "T"),
    DEADLINE("deadline", "D"),
    EVENT("event", "E");

    private final String keyword;
    private final String icon;

    TaskType(String keyword, String icon) {
        this.keyword = keyword;
        this.icon = icon;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getIcon() {
        return icon;
    }

    public static TaskType fromKeyword(String word) {
        for (TaskType type : values()) {
            if (type.keyword.equals(word)) {
                return type;
            }
        }
        return null;
    }
}
