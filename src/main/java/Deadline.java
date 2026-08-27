import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate date;

    public Deadline(String description, String date) throws RockyException {
        super(description, false);
        try {
            this.date = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new RockyException(
                    "Please give the date as yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    public String getDate() {
        return this.date.toString();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + "(by: " + this.date.format(OUTPUT_FORMAT) + ")";
    }
}
