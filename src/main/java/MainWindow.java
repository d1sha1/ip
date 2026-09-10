import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The chat window: a scrolling transcript of dialog boxes above a text field
 * where the user types the same commands the text UI accepts, e.g.
 * "todo read book" or "mark 2".
 */
public class MainWindow extends VBox {
    private static final double WINDOW_WIDTH = 420;
    private static final double WINDOW_HEIGHT = 600;
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    private final VBox dialogContainer = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    /** Builds the chat window, loads any saved tasks, and shows the greeting. */
    public MainWindow() {
        dialogContainer.setPadding(new Insets(8));
        dialogContainer.setSpacing(4);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Type a command, e.g. todo read book");
        userInput.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);
        sendButton.setOnAction(event -> handleUserInput());

        HBox inputRow = new HBox(8, userInput, sendButton);
        inputRow.setPadding(new Insets(8));

        setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        getChildren().addAll(scrollPane, inputRow);

        Rocky.initialize();
        dialogContainer.getChildren().add(DialogBox.getRockyDialog(Rocky.getGreeting()));
    }

    /**
     * Answers whatever the user typed, adds both the command and the reply to
     * the transcript, and clears the input field. Blank input is ignored.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = Rocky.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getRockyDialog(response));
        userInput.clear();

        if (Rocky.isExitCommand(input)) {
            exitAfterFarewell();
        }
    }

    /**
     * Stops accepting input and closes the window a moment later, so the
     * user can still read the farewell after typing "bye".
     */
    private void exitAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
