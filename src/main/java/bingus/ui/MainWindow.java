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
 * Controls the main Bingus GUI.
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

    /**
     * Injects the Bingus instance used to process user commands.
     *
     * @param b Bingus instance to use
     */
    public void setDuke(Bingus b) {
        bingus = b;
    }

    /**
     * Creates and displays a user dialog followed by the corresponding Bingus reply.
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
