package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.model.UserData;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * AuthenticationScreen displays the login and registration UI side by side.
 * Register uses email (primary key) + password. Default username = email prefix.
 * Login accepts email OR username + password.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class AuthenticationScreen extends StackPane {

    private final Runnable onAuthSuccess;

    // Constructor - stores the callback that runs after successful login/register
    // @author Muhammad Ahmad
    public AuthenticationScreen(Runnable onAuthSuccess) {
        this.onAuthSuccess = onAuthSuccess;
        initializeLayout();
    }

    // Sets up the dark background, logo, and the register/login card row
    // @author Muhammad Ahmad
    private void initializeLayout() {
        setStyle("-fx-background-color: #020b1c;");
        setPadding(new Insets(40));

        VBox root = new VBox(36);
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);

        root.getChildren().add(buildLogoBlock());
        root.getChildren().add(buildCardRow());

        getChildren().add(root);
    }

    // Loads and returns the app logo image from resources
    // @author Muhammad Ahmad
    private ImageView buildLogoBlock() {
        ImageView logo = new ImageView();
        java.io.InputStream stream = getClass().getResourceAsStream("/ca/ucalgary/seng300/images/sgp-logo.png");
        if (stream != null) {
            logo.setImage(new Image(stream));
        }
        logo.setPreserveRatio(true);
        logo.setFitWidth(280);
        logo.setSmooth(true);
        return logo;
    }

    // Builds the horizontal row containing register card on the left and login card on the right
    // @author Muhammad Ahmad
    private HBox buildCardRow() {
        VBox registerCard = buildRegisterCard();
        VBox loginCard = buildLoginCard();

        HBox.setHgrow(registerCard, Priority.ALWAYS);
        HBox.setHgrow(loginCard, Priority.ALWAYS);

        HBox row = new HBox(24, registerCard, loginCard);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(860);
        return row;
    }

    // Builds the registration card with email field, password field, and submit button
    // @author Muhammad Ahmad
    private VBox buildRegisterCard() {
        Label title = cardTitle("Register");

        Label emailLabel = fieldLabel("Email");
        TextField emailField = styledTextField("you@example.com");

        Label passLabel = fieldLabel("Create Password");

        PasswordField passField = styledPasswordField("Enter your password");
        TextField passVisible = styledTextField("Enter your password");
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        Button eyeBtn = eyeToggleButton(passField, passVisible);

        StackPane passStack = new StackPane(passField, passVisible);
        HBox passRow = new HBox(6, passStack, eyeBtn);
        passRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passStack, Priority.ALWAYS);

        Label errorLabel = errorLabel();

        Button submitBtn = primaryButton("Submit Registration");
        submitBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = getPasswordText(passField, passVisible);

            // Validate email and password before attempting registration
            if (email.isEmpty() || !email.contains("@")) {
                showError(errorLabel, "Please enter a valid email address.");
                return;
            }
            if (email.contains("|")) {
                showError(errorLabel, "Email cannot contain the '|' character.");
                return;
            }
            if (pass.isEmpty()) {
                showError(errorLabel, "Password cannot be empty.");
                return;
            }
            if (pass.length() < 3) {
                showError(errorLabel, "Password must be at least 3 characters.");
                return;
            }
            if (pass.contains("|")) {
                showError(errorLabel, "Password cannot contain the '|' character.");
                return;
            }

            UserData result = UserManager.register(email, pass);
            if (result == null) {
                showError(errorLabel, "Email already registered. Please login instead.");
                return;
            }
            System.out.println("Registered: " + email + " (username: " + result.getUsername() + ")");
            if (onAuthSuccess != null) onAuthSuccess.run();
        });

        VBox content = new VBox(10,
                title,
                emailLabel, emailField,
                passLabel, passRow,
                submitBtn, errorLabel
        );
        content.setPadding(new Insets(30));
        content.setSpacing(10);
        content.setMaxWidth(Double.MAX_VALUE);
        styleCard(content);
        return content;
    }

    // Builds the login card with email/username field, password field, and forgot link
    // @author Muhammad Ahmad
    private VBox buildLoginCard() {
        Label title = cardTitle("Login");

        Label emailLabel = fieldLabel("Email or Username");
        TextField emailField = styledTextField("Email or username");

        Label passLabel = fieldLabel("Password");
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

        Label errorLabel = errorLabel();

        Button submitBtn = primaryButton("Submit Login");
        submitBtn.setOnAction(e -> {
            String identifier = emailField.getText().trim();
            String pass = getPasswordText(passField, passVisible);

            if (identifier.isEmpty()) {
                showError(errorLabel, "Please enter your email or username.");
                return;
            }
            if (pass.isEmpty()) {
                showError(errorLabel, "Password cannot be empty.");
                return;
            }

            UserData result = UserManager.login(identifier, pass);
            if (result == null) {
                showError(errorLabel, "Invalid email/username or password.");
                return;
            }
            System.out.println("Login successful: " + result.getUsername());
            if (onAuthSuccess != null) onAuthSuccess.run();
        });

        VBox content = new VBox(10,
                title,
                emailLabel, emailField,
                passHeader, passRow,
                submitBtn, errorLabel
        );
        content.setPadding(new Insets(30));
        content.setSpacing(10);
        content.setMaxWidth(Double.MAX_VALUE);
        styleCard(content);
        return content;
    }

    // Returns the visible password text depending on which field is shown
    // @author Muhammad Ahmad
    private String getPasswordText(PasswordField hidden, TextField visible) {
        return visible.isVisible() ? visible.getText() : hidden.getText();
    }

    // UI Helpers

    // Creates a hidden error label - shown when validation fails
    // @author Muhammad Ahmad
    private Label errorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 12px;");
        label.setWrapText(true);
        label.setManaged(false);
        label.setVisible(false);
        return label;
    }

    // Makes an error label visible and sets its text
    // @author Muhammad Ahmad
    private void showError(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
        label.setVisible(true);
    }

    // Creates a styled card title label (white, bold, 22px)
    // @author Muhammad Ahmad
    private Label cardTitle(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        return label;
    }

    // Creates a styled field label (light blue, 13px)
    // @author Muhammad Ahmad
    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #cad5e2; -fx-font-size: 13px;");
        return label;
    }

    // Creates the "Forgot ?" link that opens the forgot password popup
    // @author Muhammad Ahmad
    private Label forgotLabel() {
        Label label = new Label("Forgot ?");
        label.setStyle(
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 12px;" +
                "-fx-cursor: hand;"
        );
        label.setOnMouseClicked(e -> showForgotPopup());
        return label;
    }

    // Shows a popup overlay telling the user to contact the admin for password help
    // @author Muhammad Ahmad
    private void showForgotPopup() {
        Pane scrim = new Pane();
        scrim.setStyle("-fx-background-color: rgba(0, 0, 0, 0.58);");
        scrim.setMaxWidth(Double.MAX_VALUE);
        scrim.setMaxHeight(Double.MAX_VALUE);

        Label icon = new Label("\u2139");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        icon.setTextFill(Color.web("#00c8ff"));
        icon.setAlignment(Pos.CENTER);

        Label message = new Label("Contact Platform Admin for Support!");
        message.setTextFill(Color.WHITE);
        message.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-text-alignment: center;");
        message.setWrapText(false);

        Button dismissBtn = new Button("Dismiss");
        dismissBtn.setPrefWidth(160);
        dismissBtn.setPrefHeight(40);

        // Dismiss button styling - base, hover, press states
        String dismissBase =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String dismissHover =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
        String dismissPress =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        dismissBtn.setStyle(dismissBase);
        dismissBtn.setOnMouseEntered(e -> {
            dismissBtn.setStyle(dismissHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), dismissBtn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        dismissBtn.setOnMouseExited(e -> {
            dismissBtn.setStyle(dismissBase);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), dismissBtn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        dismissBtn.setOnMousePressed(e -> {
            dismissBtn.setStyle(dismissPress);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), dismissBtn);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        dismissBtn.setOnMouseReleased(e -> {
            dismissBtn.setStyle(dismissHover);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), dismissBtn);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });

        // Popup card styling
        VBox card = new VBox(24, icon, message, dismissBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36, 50, 36, 50));
        card.setPrefWidth(500);
        card.setMinWidth(500);
        card.setMaxWidth(500);
        card.setPrefHeight(280);
        card.setMinHeight(280);
        card.setMaxHeight(280);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0d1f40, #162d55);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(80, 140, 220, 0.55);" +
                "-fx-border-radius: 18;" +
                "-fx-border-width: 1.5;"
        );

        StackPane overlay = new StackPane(scrim, card);
        overlay.setAlignment(Pos.CENTER);
        overlay.setOpacity(0);

        // Dismiss button fades out the overlay and removes it
        dismissBtn.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> getChildren().remove(overlay));
            fadeOut.play();
        });

        getChildren().add(overlay);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // Creates a styled dark text field with placeholder text
    // @author Muhammad Ahmad
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

    // Creates a styled dark password field with placeholder text
    // @author Muhammad Ahmad
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

    // Creates the eye toggle button to show/hide the password field
    // @author Muhammad Ahmad
    private Button eyeToggleButton(PasswordField hidden, TextField visible) {
        Button btn = new Button("\uD83D\uDC41");
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
                hidden.setText(visible.getText());
                hidden.setVisible(true);
                hidden.setManaged(true);
                visible.setVisible(false);
                visible.setManaged(false);
            } else {
                visible.setText(hidden.getText());
                visible.setVisible(true);
                visible.setManaged(true);
                hidden.setVisible(false);
                hidden.setManaged(false);
            }
        });
        return btn;
    }

    // Creates a blue gradient primary button with hover/press animations
    // @author Muhammad Ahmad
    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        // Button styling - base and hover states
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
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btn.setOnMousePressed(e -> {
            btn.setStyle(
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.97); st.setToY(0.97); st.play();
        });
        btn.setOnMouseReleased(e -> {
            btn.setStyle(hover);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        return btn;
    }

    // Applies the dark gradient card style to a VBox
    // @author Muhammad Ahmad
    private void styleCard(VBox card) {
        card.setStyle(
                "-fx-background-color: linear-gradient(#0f172b, #1d293d);" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: rgba(72, 114, 170, 0.35);" +
                "-fx-border-radius: 14;"
        );
    }

    // Creates a flexible spacer region for HBox layouts
    // @author Muhammad Ahmad
    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }
}
