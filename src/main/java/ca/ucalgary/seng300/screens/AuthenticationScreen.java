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
 * Navigation: AuthenticationScreen --> WelcomePage
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
            System.out.println("Register attempt - email: " + emailField.getText());
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

    // UML Method: displayLoginScreen()
    // Builds the Login card

    /**
     * Displays the login panel.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void displayLoginScreen() {
        // Stub TODO: login display logic will connect to backend in future iteration
        System.out.println("AuthenticationScreen: displayLoginScreen() called");
    }

    private VBox buildLoginCard() {
        Label title = cardTitle("Login");

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

        // Submit button - calls submitLogin()
        Button submitBtn = primaryButton("Submit Login");
        submitBtn.setOnAction(e -> {
            submitLogin();
            System.out.println("Login attempt - email: " + emailField.getText());
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

    // UML Method: submitLogin()

    /**
     * Handles login form submission.
     * Will call PlatformCore authentication stub in a future iteration.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    public void submitLogin() {
        // Stub TODO: backend login call will replace this in iteration 3
        System.out.println("AuthenticationScreen: submitLogin() called - navigating to HomePage");
        // TODO: validate credentials via PlatformCore, then navigate to HomePage
    }

    // Reusable UI helpers

    /** Card title label */
    private Label cardTitle(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        return label;
    }

    /** Small field label */
    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #cad5e2; -fx-font-size: 13px;");
        return label;
    }

    /** "Forgot ?" link-style label */
    private Label forgotLabel() {
        Label label = new Label("Forgot ?");
        label.setStyle(
                "-fx-text-fill: #00c8ff;" +
                        "-fx-font-size: 12px;" +
                        "-fx-cursor: hand;"
        );
        label.setOnMouseClicked(e ->
                System.out.println("Forgot password clicked")
        );
        return label;
    }

    /** Styled text input */
    private TextField styledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle(
                "-fx-background-color: #0d1b2e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #607a99;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #1e3a5f;" +
                        "-fx-border-radius: 6;" +
                        "-fx-font-size: 13px;"
        );
        field.setPrefHeight(38);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    /** Styled password input */
    private PasswordField styledPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setStyle(
                "-fx-background-color: #0d1b2e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #607a99;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #1e3a5f;" +
                        "-fx-border-radius: 6;" +
                        "-fx-font-size: 13px;"
        );
        field.setPrefHeight(38);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    /**
     * Eye toggle button that swaps between hidden and visible password fields.
     * passField and passVisible are kept in a StackPane overlay.
     */
    private Button eyeToggleButton(PasswordField hidden, TextField visible) {
        Button btn = new Button("👁");
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #607a99;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0 4 0 4;"
        );
        btn.setOnAction(e -> {
            boolean showing = visible.isVisible();
            if (showing) {
                // Switch back to hidden
                hidden.setText(visible.getText());
                hidden.setVisible(true);
                hidden.setManaged(true);
                visible.setVisible(false);
                visible.setManaged(false);
            } else {
                // Switch to visible
                visible.setText(hidden.getText());
                visible.setVisible(true);
                visible.setManaged(true);
                hidden.setVisible(false);
                hidden.setManaged(false);
            }
        });
        return btn;
    }

    /** Primary blue action button */
    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        String base =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        String hover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnMousePressed(e -> btn.setStyle(
                "-fx-background-color: #1e40af;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseReleased(e -> btn.setStyle(hover));
        return btn;
    }

    /** Applies dark gradient card style to a VBox */
    private void styleCard(VBox card) {
        card.setStyle(
                "-fx-background-color: linear-gradient(#0f172b, #1d293d);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(72, 114, 170, 0.35);" +
                        "-fx-border-radius: 14;"
        );
    }

    /** Flexible spacer that pushes elements apart in an HBox */
    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}