// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus.ui;

import bingus.Bingus;
import bingus.command.CommandType;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
    private static final Duration REPLY_DELAY = Duration.millis(250);
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    private static final double DIALOG_START_OFFSET = 16;
    private static final Duration SLIDE_DURATION = Duration.millis(180);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Bingus bingus;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image bingusImage = new Image(this.getClass().getResourceAsStream("/images/DaBingus.png"));

    /**
     * Initializes automatic scrolling for new chat messages.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "Scroll pane must be injected from FXML";
        assert dialogContainer != null : "Dialog container must be injected from FXML";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bingus instance used to process user commands.
     *
     * @param bingus Bingus instance to use
     */
    public void setBingus(Bingus bingus) {
        this.bingus = bingus;

        String loadErrorMessage = bingus.getLoadErrorMessage();
        if (loadErrorMessage != null) {
            DialogBox errorDialog = DialogBox.getBingusDialog(
                    loadErrorMessage,
                    bingusImage,
                    CommandType.INVALID);
            dialogContainer.getChildren().add(errorDialog);
        }
    }

    /**
     * Creates and displays a user dialog followed by the corresponding Bingus reply.
     */
    @FXML
    private void handleUserInput() {
        assert bingus != null : "Bingus must be injected before users can submit commands";

        String input = userInput.getText();
        userInput.setPromptText("Type here...");
        String response = bingus.getResponse(input);
        CommandType commandType = bingus.getCommandType();
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox botDialog = DialogBox.getBingusDialog(response, bingusImage, commandType);

        addDialogWithAnimation(userDialog);

        PauseTransition replyDelay = new PauseTransition(REPLY_DELAY);
        replyDelay.setOnFinished(event -> {
            addDialogWithAnimation(botDialog);
            if (commandType == CommandType.EXIT) {
                PauseTransition closeDelay = new PauseTransition(EXIT_DELAY);
                closeDelay.setOnFinished(closeEvent -> Platform.exit());
                closeDelay.play();
            }
        });
        replyDelay.play();

        userInput.clear();
    }

    private void addDialogWithAnimation(DialogBox dialogBox) {
        dialogBox.setTranslateY(DIALOG_START_OFFSET);

        dialogContainer.getChildren().add(dialogBox);

        TranslateTransition slideUp = new TranslateTransition(SLIDE_DURATION, dialogBox);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);
        slideUp.play();
    }
}
