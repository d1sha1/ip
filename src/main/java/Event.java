public class Event extends Task{
    private String date;

    public Event(String description, String startDate, String endDate){
        super(description, false);
        this.date = date;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + "(from: " + this.date + ")";
    }
}
