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

        //Add top bar and title first
        content.getChildren().add(createTopBar());
        content.getChildren().add(createTitle());
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

    /**
     * Creates the top navigation bar
     */
    private HBox createTopBar() {
        // basic styling for button
        String style =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 15px;" +
                        "-fx-cursor: hand;";

        Button back = new Button("← Back to Home");
        back.setStyle(style);
        // placeholder functionality
        back.setOnAction(e -> System.out.println("Go to Home"));
        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);

        return bar;
    }

    /**
     * Creates page title
     */
    private Label createTitle() {
        Label title = new Label("Settings");
        // big bold title
        title.setStyle("-fx-text-fill:white; -fx-font-size:34px; -fx-font-weight:bold;");
        return title;
    }

}