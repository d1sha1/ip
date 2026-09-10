import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * One message in the chat transcript: a rounded bubble holding the text of
 * either something the user typed or Rocky's reply to it. User messages sit
 * on the right, Rocky's on the left.
 */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH = 280;
    private static final String USER_BUBBLE_STYLE =
            "-fx-background-color: #3a6b52;"
                    + "-fx-text-fill: white;"
                    + "-fx-background-radius: 12;"
                    + "-fx-padding: 8 12 8 12;";
    private static final String ROCKY_BUBBLE_STYLE =
            "-fx-background-color: #e7e5df;"
                    + "-fx-text-fill: #22261f;"
                    + "-fx-background-radius: 12;"
                    + "-fx-padding: 8 12 8 12;";

    private DialogBox(String text, String bubbleStyle, Pos alignment) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(MAX_BUBBLE_WIDTH);
        label.setStyle(bubbleStyle);

        setAlignment(alignment);
        setPadding(new Insets(4, 8, 4, 8));
        getChildren().add(label);
    }

    /**
     * Returns a dialog box showing what the user typed.
     *
     * @param text the command the user entered.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, USER_BUBBLE_STYLE, Pos.TOP_RIGHT);
    }

    /**
     * Returns a dialog box showing Rocky's reply.
     *
     * @param text the reply to display, which may span several lines.
     */
    public static DialogBox getRockyDialog(String text) {
        return new DialogBox(text, ROCKY_BUBBLE_STYLE, Pos.TOP_LEFT);
    }
}
