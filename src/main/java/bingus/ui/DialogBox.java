// Adapted from the SE-EDU JavaFX Tutorial, Part 4 & 5;
// https://se-education.org/guides/tutorials/javaFxPart4.html
// https://se-education.org/guides/tutorials/javaFxPart5.html

package bingus.ui;

import java.io.IOException;
import java.util.Collections;

import bingus.command.CommandType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load the dialog box layout.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box for a user message.
     *
     * @param text message text
     * @param image image representing the user
     * @return dialog box for the user message
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a flipped dialog box for a Bingus response.
     *
     * @param text response text
     * @param image the image representing Bingus
     * @param commandType type of command that produced the response
     * @return dialog box for the Bingus response
     */
    public static DialogBox getBingusDialog(String text, Image image, CommandType commandType) {
        DialogBox bingusDialog = new DialogBox(text, image);
        bingusDialog.flip();
        bingusDialog.changeDialogStyle(commandType);
        return bingusDialog;
    }

    /** Applies a response style based on the command type. */
    private void changeDialogStyle(CommandType commandType) {
        switch (commandType) {
            case ADD:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                // Use the default reply style.
        }
    }
}
