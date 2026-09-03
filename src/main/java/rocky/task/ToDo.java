package rocky.task;

public class ToDo extends Task{

    public ToDo(String description){
        super(description, false);
    }

    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
