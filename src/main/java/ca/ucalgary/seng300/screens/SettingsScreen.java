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

    /**
     * Creates styled label for form fields
     */
    private Label fieldLabel(String text) {

        // label used for inputs like username, password
        Label label = new Label(text);

        // soft gray-blue color for readability
        label.setStyle("-fx-text-fill:#cad5e2; -fx-font-size:15px;");

        return label;
    }

    /**
     * Creates styled text input field
     */
    private TextField styledTextField(String placeholder) {

        TextField field = new TextField();
        field.setPromptText(placeholder);

        // dark themed input styling
        field.setStyle(
                "-fx-background-color:#020618;" +
                        "-fx-text-fill:white;" +
                        "-fx-prompt-text-fill:#90a1b9;" +
                        "-fx-background-radius:8;" +
                        "-fx-border-color:#314158;" +
                        "-fx-border-radius:8;" +
                        "-fx-font-size:15px;"
        );

        field.setPrefHeight(40); // makes it easier to click

        return field;
    }

    /**
     * Creates styled password input field
     */
    private PasswordField styledPasswordField(String placeholder) {

        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);

        // same styling as text field for consistency
        field.setStyle(
                "-fx-background-color:#020618;" +
                        "-fx-text-fill:white;" +
                        "-fx-prompt-text-fill:#90a1b9;" +
                        "-fx-background-radius:8;" +
                        "-fx-border-color:#314158;" +
                        "-fx-border-radius:8;" +
                        "-fx-font-size:15px;"
        );

        field.setPrefHeight(40);

        return field;
    }

    /**
     * Creates primary action button
     */
    private Button styledPrimaryButton(String text) {

        Button btn = new Button(text);

        // blue themed button for primary actions
        btn.setStyle(
                "-fx-background-color:#0092b8;" +
                        "-fx-text-fill:white;" +
                        "-fx-background-radius:6;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;"
        );

        btn.setPrefHeight(40);

        return btn;
    }

}