package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * SettingsScreen represents the user settings page.
 */
public class SettingsScreen extends ScrollPane {

    public SettingsScreen() {

        // Main container that holds everything vertically
        VBox content = new VBox();
        content.setSpacing(28); //spacing between sections
        content.setPadding(new Insets(40)); // padding around edges
        content.setStyle("-fx-background-color: #020b1c;"); // dark background
        content.setFillWidth(true); // stretch children to full width


        // Put VBox inside scrollpane so we can scroll
        setContent(content);

        // makes content stretch horizontally
        setFitToWidth(true);

        // removes ugly white background from scrollpane
        setStyle(
                "-fx-background: #020b1c;" +
                        "-fx-background-color: #020b1c;"
        );
    }

}