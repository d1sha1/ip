import java.net.URL;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * One message in the chat transcript: a round profile picture beside a
 * colored bubble holding the text. Rocky's replies sit on the left, with his
 * picture before an amber bubble; the user's messages (as Ryland Grace) sit
 * on the right, with Grace's picture after a blue bubble.
 */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH = 260;
    private static final double AVATAR_SIZE = 44;
    private static final String BUBBLE_SHAPE_STYLE = "-fx-background-radius: 12; -fx-padding: 8 12 8 12;";
    private static final String ROCKY_BUBBLE_STYLE =
            "-fx-background-color: #E0A040; -fx-text-fill: #1A1208;" + BUBBLE_SHAPE_STYLE;
    private static final String USER_BUBBLE_STYLE =
            "-fx-background-color: #2B5DAA; -fx-text-fill: #FFFFFF;" + BUBBLE_SHAPE_STYLE;
    private static final Color ROCKY_COLOR = Color.web("#E0A040");
    private static final Color USER_COLOR = Color.web("#2B5DAA");

    /** Loaded once and shared by every dialog box, instead of being re-read for each message. */
    private static final Image ROCKY_AVATAR = loadImage("/images/Rocky.png");
    private static final Image USER_AVATAR = loadImage("/images/RylandGrace.png");

    private DialogBox(String text, String bubbleStyle, Node avatar, boolean isFromUser) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(MAX_BUBBLE_WIDTH);
        bubble.setStyle(bubbleStyle);

        setSpacing(8);
        setPadding(new Insets(4, 8, 4, 8));
        if (isFromUser) {
            setAlignment(Pos.TOP_RIGHT);
            getChildren().addAll(bubble, avatar);
        } else {
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(avatar, bubble);
        }
    }

    /**
     * Returns a dialog box showing what the user typed, with Ryland Grace's picture.
     *
     * @param text the command the user entered.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, USER_BUBBLE_STYLE, createAvatar(USER_AVATAR, USER_COLOR), true);
    }

    /**
     * Returns a dialog box showing Rocky's reply, with Rocky's picture.
     *
     * @param text the reply to display, which may span several lines.
     */
    public static DialogBox getRockyDialog(String text) {
        return new DialogBox(text, ROCKY_BUBBLE_STYLE, createAvatar(ROCKY_AVATAR, ROCKY_COLOR), false);
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
    private static Node createAvatar(Image image, Color placeholderColor) {
        double radius = AVATAR_SIZE / 2;
        if (image == null || image.isError()) {
            return new Circle(radius, placeholderColor);
        }

        double side = Math.min(image.getWidth(), image.getHeight());
        ImageView avatar = new ImageView(image);
        avatar.setViewport(new Rectangle2D((image.getWidth() - side) / 2, 0, side, side));
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setSmooth(true);
        avatar.setClip(new Circle(radius, radius, radius));
        return avatar;
    }
}
