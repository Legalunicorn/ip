// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus;

import java.io.IOException;

import bingus.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Represents the Bingus JavaFX application using FXML.
 */
public class Main extends Application {

    private final Bingus bingus = new Bingus(Bingus.DEFAULT_SAVE_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());
            fxmlLoader.<MainWindow>getController().setBingus(bingus);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load the main window layout.", e);
        }
    }
}
