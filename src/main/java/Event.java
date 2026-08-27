public class Event extends Task {
    private String startDate;
    private String endDate;

    public Event(String description, String startDate, String endDate) {
        super(description, false);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Returns the start time, used when saving to disk. */
    public String getStartDate() {
        return this.startDate;
    }

    /** Returns the end time, used when saving to disk. */
    public String getEndDate() {
        return this.endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.startDate
                + " to: " + this.endDate + ")";
    }
}