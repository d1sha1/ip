public abstract class Task {
    private String description;
    private Boolean isDone;

    public Task(String description, Boolean isDone){
        this.description = description;
        this.isDone = isDone;
    }

    public void mark(){
        this.isDone = true;
    }

    public void unmark(){
        this.isDone = false;
    }

    public String getCheckbox(){
        return this.isDone ? "[x]" : "[ ]";
    }

    /** Returns the task description, used when saving to disk. */
    public String getDescription(){
        return this.description;
    }

    /** Returns whether the task is done, used when saving to disk. */
    public boolean isDone(){
        return this.isDone;
    }

    @Override
    public String toString() {
        return this.getCheckbox() + " " + this.description;
    }

}
