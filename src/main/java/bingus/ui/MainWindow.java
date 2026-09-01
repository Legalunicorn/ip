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
    @FXML
    private Button sendButton;

    private Bingus bingus;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image bingusImage = new Image(this.getClass().getResourceAsStream("/images/DaBingus.png"));

    /**
     * Initializes automatic scrolling for new chat messages.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bingus instance used to process user commands.
     *
     * @param bingus Bingus instance to use
     */
    public void setBingus(Bingus bingus) {
        this.bingus = bingus;
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
        DialogBox botDialog = DialogBox.getBingusDialog(response, bingusImage, commandType);

        addDialogWithAnimation(userDialog);

        PauseTransition replyDelay = new PauseTransition(REPLY_DELAY);
        replyDelay.setOnFinished(event -> {
            addDialogWithAnimation(botDialog);
            if ("ExitCommand".equals(commandType)) {
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
