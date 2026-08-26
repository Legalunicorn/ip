// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus;


import javafx.application.Application;


/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
