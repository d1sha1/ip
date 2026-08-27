import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task {
    /** How the start/end dates are shown to the user, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates an event. Both dates are expected in ISO format
     * (yyyy-mm-dd, e.g. 2019-10-15); anything else raises a RockyException.
     */
    public Event(String description, String startDate, String endDate) throws RockyException {
        super(description, false);
        try {
            this.startDate = LocalDate.parse(startDate);
            this.endDate = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            throw new RockyException(
                    "Please give event dates as yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    /** Returns the start date in ISO format (yyyy-mm-dd), used when saving to disk. */
    public String getStartDate() {
        return this.startDate.toString();
    }

    /** Returns the end date in ISO format (yyyy-mm-dd), used when saving to disk. */
    public String getEndDate() {
        return this.endDate.toString();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.startDate.format(OUTPUT_FORMAT)
                + " to: " + this.endDate.format(OUTPUT_FORMAT) + ")";
    }
}
