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

    @Override
    public String toString() {
        return this.getCheckbox() + " " + this.description;
    }

}
