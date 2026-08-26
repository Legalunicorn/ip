// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus.ui;

import bingus.Bingus;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 * This code was taken from JavaFx tutorial and modified for bingus
 * URL to JavaFx tutorial: https://se-education.org/guides/tutorials/javaFxPart4.html
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bingus bingus;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaBingus.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setDuke(Bingus b) {
        bingus = b;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        userInput.setPromptText("Type here...");
        String response = bingus.getResponse(input);
        String commandType = bingus.getCommandType();
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox botDialog = DialogBox.getDukeDialog(response, dukeImage, commandType);

        addDialogWithAnimation(userDialog);

        PauseTransition replyDelay = new PauseTransition(Duration.millis(250));
        replyDelay.setOnFinished(event -> {
            addDialogWithAnimation(botDialog);
            if ("ExitCommand".equals(commandType)) {
                PauseTransition closeDelay = new PauseTransition(Duration.seconds(1));
                closeDelay.setOnFinished(closeEvent -> Platform.exit());
                closeDelay.play();
            }
        });
        replyDelay.play();

        userInput.clear();
        userInput.clear();
    }

    private void addDialogWithAnimation(DialogBox dialogBox) {
        dialogBox.setTranslateY(16);

        dialogContainer.getChildren().add(dialogBox);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(180), dialogBox);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);
        slideUp.play();
    }
}
