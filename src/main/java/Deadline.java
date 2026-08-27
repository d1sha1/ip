public class Deadline extends Task{
    private String date;

    public Deadline(String description, String date) {
        super(description, false);
        this.date = date;
    }

    /** Returns the due date, used when saving to disk. */
    public String getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + "(by: " + this.date + ")";
    }
}
