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
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Bingus bingus = new Bingus("data/bingus.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(367);
            stage.setMinWidth(467);
            fxmlLoader.<MainWindow>getController().setDuke(bingus);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
