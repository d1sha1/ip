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
 * "todo read book" or "mark 2". It uses a Project Hail Mary theme on a black
 * background. The two sides are shown differently, because the user is giving
 * commands to an app rather than chatting with another person: each command is
 * echoed as a compact, terminal-style line, while Rocky's replies appear as
 * wider amber cards beside his picture, or as dark red alert cards when they report an error.
 */
public class MainWindow extends VBox {
    private static final double WINDOW_WIDTH = 420;
    private static final double WINDOW_HEIGHT = 600;
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);
    private static final String BACKGROUND_STYLE = "-fx-background-color: #000000;";
    // A ScrollPane paints its content area using -fx-background, so it needs both properties.
    private static final String SCROLL_PANE_STYLE =
            "-fx-background: #000000; -fx-background-color: #000000; -fx-background-insets: 0;";
    private static final String INPUT_ROW_STYLE = "-fx-background-color: #111111;";
    private static final String INPUT_FIELD_STYLE =
            "-fx-background-color: #1E1E1E; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #8A8A8A;"
                    + " -fx-background-radius: 8;";
    private static final String SEND_BUTTON_STYLE =
            "-fx-background-color: #E0A040; -fx-text-fill: #1A1208; -fx-font-weight: bold;"
                    + " -fx-background-radius: 8;";

    private final VBox dialogContainer = new VBox();
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    /** Builds the chat window, loads any saved tasks, and shows the greeting. */
    public MainWindow() {
        setStyle(BACKGROUND_STYLE);
        dialogContainer.setStyle(BACKGROUND_STYLE);
        dialogContainer.setPadding(new Insets(8));
        dialogContainer.setSpacing(4);

        scrollPane.setStyle(SCROLL_PANE_STYLE);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setStyle(INPUT_FIELD_STYLE);
        userInput.setPromptText("Type a command, e.g. todo read book");
        userInput.setOnAction(event -> handleUserInput());
        HBox.setHgrow(userInput, Priority.ALWAYS);
        sendButton.setStyle(SEND_BUTTON_STYLE);
        sendButton.setOnAction(event -> handleUserInput());

        HBox inputRow = new HBox(8, userInput, sendButton);
        inputRow.setStyle(INPUT_ROW_STYLE);
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

        Rocky.Reply reply = Rocky.getReply(input);
        DialogBox rockyDialog = reply.isError()
                ? DialogBox.getRockyErrorDialog(reply.text())
                : DialogBox.getRockyDialog(reply.text());
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), rockyDialog);
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
