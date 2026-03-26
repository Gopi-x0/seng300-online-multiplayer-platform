package ca.ucalgary.seng300.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * AuthenticationScreen displays the login and registration UI side by side.
 * Implements the AuthenticationScreen contract from the UI & Navigation Layer UML (Diagram 04).
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class AuthenticationScreen extends StackPane {

    public AuthenticationScreen() {
        initializeLayout();
    }

    // Layout Setup

    private void initializeLayout() {
        // Dark background matching platform style
        setStyle("-fx-background-color: #020b1c;");
        setPadding(new Insets(40));

        VBox root = new VBox(36);
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);

        root.getChildren().add(buildLogoBlock());
        root.getChildren().add(buildCardRow());

        getChildren().add(root);
    }

    // Logo

    private ImageView buildLogoBlock() {
        // Load the shared SGP logo from resources
        Image image = new Image(
                getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png")
        );
        ImageView logo = new ImageView(image);
        logo.setPreserveRatio(true);
        logo.setFitWidth(280);
        logo.setSmooth(true);
        return logo;
    }

    // Card Row (Register + Login side by side)

    private HBox buildCardRow() {
        VBox registerCard = buildRegisterCard();
        VBox loginCard = buildLoginCard();

        // Both cards grow equally
        HBox.setHgrow(registerCard, Priority.ALWAYS);
        HBox.setHgrow(loginCard, Priority.ALWAYS);

        HBox row = new HBox(24, registerCard, loginCard);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(860);
        return row;
    }

    // UML Method: displayRegistrationScreen()
    // Builds the Register card

    /**
     * Displays the registration panel.
     * Per TA feedback: registration must include its own submit action for consistency.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void displayRegistrationScreen() {
        // Stub TODO: registration display logic will connect to backend in future iteration
        System.out.println("AuthenticationScreen: displayRegistrationScreen() called");
    }

    private VBox buildRegisterCard() {
        Label title = cardTitle("Register");

        // Email field
        Label emailLabel = fieldLabel("Email");
        TextField emailField = styledTextField("balamia@gmail.com");

        // Password field with show/hide toggle
        Label passLabel = fieldLabel("Create Password");
        Label forgotLabel = forgotLabel();
        HBox passHeader = new HBox(passLabel, spacer(), forgotLabel);
        passHeader.setAlignment(Pos.CENTER_LEFT);

        PasswordField passField = styledPasswordField("Enter your password");
        TextField passVisible = styledTextField("Enter your password");
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        Button eyeBtn = eyeToggleButton(passField, passVisible);

        StackPane passStack = new StackPane(passField, passVisible);
        HBox passRow = new HBox(6, passStack, eyeBtn);
        passRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passStack, Priority.ALWAYS);

        // Submit button - calls displayRegistrationScreen()
        Button submitBtn = primaryButton("Submit Registration");
        submitBtn.setOnAction(e -> {
            displayRegistrationScreen();
            System.out.println("Register attempt — email: " + emailField.getText());
        });

        VBox content = new VBox(10,
                title,
                emailLabel, emailField,
                passHeader, passRow,
                submitBtn
        );
        content.setPadding(new Insets(30));
        content.setSpacing(10);
        content.setMaxWidth(Double.MAX_VALUE);
        styleCard(content);
        return content;
    }


}