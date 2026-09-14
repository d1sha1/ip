import java.net.URL;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * One entry in the chat transcript. The two sides look different on purpose,
 * because the user is giving commands to an app rather than chatting with
 * another person: a command the user typed is echoed as a compact,
 * terminal-style line on the right beside a small picture of Ryland Grace,
 * while Rocky's reply is a wide amber card beside his larger picture, which
 * carries most of the information.
 */
public class DialogBox extends HBox {
    private static final double ROCKY_AVATAR_SIZE = 44;
    // The user's picture is smaller than Rocky's, so his replies still carry the visual weight.
    private static final double USER_AVATAR_SIZE = 28;
    private static final double MAX_COMMAND_WIDTH = 300;
    private static final String COMMAND_PROMPT = "> ";
    private static final String COMMAND_STYLE =
            "-fx-background-color: #1C1C1E; -fx-text-fill: #9DB7E8; -fx-background-radius: 12;"
                    + " -fx-padding: 4 10 4 10; -fx-font-family: 'Menlo', 'Consolas', monospace;";
    private static final String REPLY_CARD_STYLE =
            "-fx-background-color: #E0A040; -fx-text-fill: #1A1208;"
                    + " -fx-background-radius: 12; -fx-padding: 10 14 10 14;";
    private static final Color ROCKY_COLOR = Color.web("#E0A040");
    private static final Color USER_COLOR = Color.web("#2B5DAA");

    /** Loaded once and shared by every dialog box, instead of being re-read for each message. */
    private static final Image ROCKY_AVATAR = loadImage("/images/Rocky.png");
    private static final Image USER_AVATAR = loadImage("/images/RylandGrace.png");

    private DialogBox(Pos alignment, Node... children) {
        setAlignment(alignment);
        setSpacing(8);
        setPadding(new Insets(4, 8, 4, 8));
        getChildren().addAll(children);
    }

    /**
     * Returns a dialog box echoing the command the user typed, e.g. "> mark 2",
     * with a small picture of Ryland Grace after it.
     *
     * @param text the command the user entered.
     */
    public static DialogBox getUserDialog(String text) {
        Label command = new Label(COMMAND_PROMPT + text);
        command.setWrapText(true);
        command.setMaxWidth(MAX_COMMAND_WIDTH);
        command.setStyle(COMMAND_STYLE);
        return new DialogBox(Pos.CENTER_RIGHT, command,
                createAvatar(USER_AVATAR, USER_COLOR, USER_AVATAR_SIZE));
    }

    /**
     * Returns a dialog box showing one of Rocky's replies as a card beside his
     * picture, stretched across the rest of the row.
     *
     * @param text the reply to display, which may span several lines.
     */
    public static DialogBox getRockyDialog(String text) {
        Label card = new Label(text);
        card.setWrapText(true);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(REPLY_CARD_STYLE);
        HBox.setHgrow(card, Priority.ALWAYS);
        return new DialogBox(Pos.TOP_LEFT, createAvatar(ROCKY_AVATAR, ROCKY_COLOR, ROCKY_AVATAR_SIZE), card);
    }

    /**
     * Loads an image bundled from src/main/resources, e.g. "/images/Rocky.png".
     *
     * @return the image, or null if it isn't there, so the chat still works without it.
     */
    private static Image loadImage(String resourcePath) {
        URL url = DialogBox.class.getResource(resourcePath);
        return (url == null) ? null : new Image(url.toExternalForm());
    }

    /**
     * Returns a round profile picture cut from a square at the top middle of
     * the image, or a plain colored circle if the image couldn't be loaded.
     * The square starts at the top because in a portrait the face is usually
     * near the top; in a wide image the square covers the full height anyway.
     */
    private static Node createAvatar(Image image, Color placeholderColor, double size) {
        double radius = size / 2;
        if (image == null || image.isError()) {
            return new Circle(radius, placeholderColor);
        }

        double side = Math.min(image.getWidth(), image.getHeight());
        ImageView avatar = new ImageView(image);
        avatar.setViewport(new Rectangle2D((image.getWidth() - side) / 2, 0, side, side));
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(radius, radius, radius));
        return avatar;
    }
}
