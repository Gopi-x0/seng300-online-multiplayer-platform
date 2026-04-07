package ca.ucalgary.seng300.screens;

import ca.ucalgary.seng300.service.UserManager;
import ca.ucalgary.seng300.model.UserData;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * SettingsScreen allows the user to change username, email, password, and logout.
 * All changes are persisted via UserManager.
 */
public class SettingsScreen extends StackPane {

    private final Runnable onBackToHome;
    private final Runnable onLogout;

    // Sets up the scrollable settings page with all setting cards
    public SettingsScreen(Runnable onBackToHome, Runnable onLogout) {
        this.onBackToHome = onBackToHome;
        this.onLogout = onLogout;

        VBox content = new VBox();
        content.setSpacing(28);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #020b1c;");
        content.setFillWidth(true);

        content.getChildren().add(createTopBar());
        content.getChildren().add(createTitle());
        content.getChildren().add(createUsernameCard());
        content.getChildren().add(createEmailCard());
        content.getChildren().add(createPasswordCard());
        content.getChildren().add(createLogoutCard());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: #020b1c;" +
                "-fx-background-color: #020b1c;"
        );

        getChildren().add(scrollPane);
    }

    // Creates the top bar with back and credits buttons
    private HBox createTopBar() {
        // Button style variants for normal, hover, and pressed states
        String baseStyle =
                "-fx-text-fill: #00c8ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.07);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";

        String hoverStyle =
                "-fx-text-fill: #33d6ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.15);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";

        String pressedStyle =
                "-fx-text-fill: #66e0ff;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-background-color: rgba(0,200,255,0.2);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 14 4 14;";

        Button back = new Button("\u2190 Back to Home");
        back.setStyle(baseStyle);
        back.setOnMouseEntered(e -> {
            back.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), back);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        back.setOnMouseExited(e -> {
            back.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), back);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        back.setOnMousePressed(e -> {
            back.setStyle(pressedStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), back);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        back.setOnMouseReleased(e -> {
            back.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), back);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        back.setOnAction(e -> {
            System.out.println("SettingsScreen: navigating to Home");
            if (onBackToHome != null) onBackToHome.run();
        });

        Button credits = new Button("Credits");
        credits.setStyle(baseStyle);
        credits.setOnMouseEntered(e -> {
            credits.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), credits);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        credits.setOnMouseExited(e -> {
            credits.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), credits);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        credits.setOnMousePressed(e -> {
            credits.setStyle(pressedStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), credits);
            st.setToX(0.96); st.setToY(0.96); st.play();
        });
        credits.setOnMouseReleased(e -> {
            credits.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), credits);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        credits.setOnAction(e -> showCreditsPopup());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(back, spacer, credits);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // Shows a modal popup listing all student contributors
    private void showCreditsPopup() {
        Pane scrim = new Pane();
        scrim.setStyle("-fx-background-color: rgba(0, 0, 0, 0.58);");
        scrim.setMaxWidth(Double.MAX_VALUE);
        scrim.setMaxHeight(Double.MAX_VALUE);

        Label icon = new Label("\uD83D\uDC65");
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        icon.setAlignment(Pos.CENTER);

        Label heading = new Label("Student Contributors");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setAlignment(Pos.CENTER);
        heading.setStyle("-fx-text-alignment: center;");

        String[] names = {
                "Muhammad Ahmad", "Bilal Ahmed", "Muhammad Waqar", "Logan Fedoroshyn",
                "Jack", "Esha Esha", "Dima",
                "Olivia Park", "Shayan Shaikh", "Haris Naveed",
                "Mohammed Syed", "Prantap Sharma", "Landon Liknes",
                "Gurparvesh Golan", "Rishav Gautam", "Jimmy La", "Zonaira Omar"
        };

        VBox namesList = new VBox(5);
        namesList.setAlignment(Pos.CENTER_LEFT);
        namesList.setPadding(new Insets(8, 20, 8, 20));
        for (String name : names) {
            Label nameLabel = new Label(name);
            nameLabel.setTextFill(Color.web("#cad5e2"));
            nameLabel.setFont(Font.font("Arial", 14));
            namesList.getChildren().add(nameLabel);
        }

        // Dismiss button styling
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

        Button dismissBtn = new Button("Dismiss");
        dismissBtn.setPrefWidth(160);
        dismissBtn.setPrefHeight(40);
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
        VBox card = new VBox(18, icon, heading, namesList, dismissBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30, 40, 30, 40));
        card.setPrefWidth(420);
        card.setMinWidth(420);
        card.setMaxWidth(420);
        card.setMaxHeight(Region.USE_PREF_SIZE);
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

    // Creates the "Settings" title label
    private Label createTitle() {
        Label title = new Label("Settings");
        title.setStyle("-fx-text-fill:white; -fx-font-size:34px; -fx-font-weight:bold;");
        return title;
    }

    // Helper to create a styled field label
    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill:#cad5e2; -fx-font-size:15px;");
        return label;
    }

    // Helper to create a styled text input field
    private TextField styledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
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

    // Helper to create a styled password input field
    private PasswordField styledPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
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

    // Helper to create a styled blue primary button with hover effects
    private Button styledPrimaryButton(String text) {
        Button btn = new Button(text);
        String baseStyle =
                "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;";
        String hoverStyle =
                "-fx-background-color: linear-gradient(to right, #2563eb, #3b82f6);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;";
        String pressStyle =
                "-fx-background-color: #1e40af;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;";

        btn.setStyle(baseStyle);
        btn.setPrefHeight(40);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btn.setOnMousePressed(e -> {
            btn.setStyle(pressStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.97); st.setToY(0.97); st.play();
        });
        btn.setOnMouseReleased(e -> {
            btn.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.03); st.setToY(1.03); st.play();
        });
        return btn;
    }

    // Creates a hidden label used to show success or error messages
    private Label feedbackLabel() {
        Label label = new Label();
        label.setStyle("-fx-font-size: 13px;");
        label.setWrapText(true);
        label.setManaged(false);
        label.setVisible(false);
        return label;
    }

    // Makes a feedback label visible with the given message and color
    private void showFeedback(Label label, String message, boolean success) {
        label.setText(message);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (success ? "#00e676;" : "#ff5555;"));
        label.setManaged(true);
        label.setVisible(true);
    }

    // Wraps content inside a styled card with a title
    private VBox createCard(String titleText, VBox content) {
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold;");
        VBox card = new VBox(title, content);
        card.setSpacing(16);
        card.setPadding(new Insets(24));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(34,54,107,0.92), rgba(10,20,40,0.96));" +
                "-fx-background-radius:10;"
        );
        card.setMaxWidth(Double.MAX_VALUE);
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(20, Color.web("#00c8ff", 0.5))));
        card.setOnMouseExited(e -> card.setEffect(null));
        return card;
    }

    // Creates the card for changing the username
    private VBox createUsernameCard() {
        UserData user = UserManager.getCurrentUser();
        TextField usernameField = styledTextField("Username");
        if (user != null) usernameField.setText(user.getUsername());

        Label feedback = feedbackLabel();
        Button saveBtn = styledPrimaryButton("Save Username");
        saveBtn.setOnAction(e -> {
            String newName = usernameField.getText().trim();
            if (newName.isEmpty()) {
                showFeedback(feedback, "Username cannot be empty.", false);
                return;
            }
            if (newName.contains("|")) {
                showFeedback(feedback, "Username cannot contain the '|' character.", false);
                return;
            }
            boolean success = UserManager.changeUsername(newName);
            if (success) {
                showFeedback(feedback, "Username updated to: " + newName, true);
            } else {
                showFeedback(feedback, "Username already taken or invalid.", false);
            }
        });

        VBox content = new VBox(fieldLabel("Username"), usernameField, saveBtn, feedback);
        content.setSpacing(10);
        return createCard("Change Username", content);
    }

    // Creates the card for changing the email
    private VBox createEmailCard() {
        UserData user = UserManager.getCurrentUser();
        TextField emailField = styledTextField("Email Address");
        if (user != null) emailField.setText(user.getEmail());

        Label feedback = feedbackLabel();
        Button saveBtn = styledPrimaryButton("Save Email");
        saveBtn.setOnAction(e -> {
            String newEmail = emailField.getText().trim();
            if (newEmail.isEmpty() || !newEmail.contains("@")) {
                showFeedback(feedback, "Please enter a valid email address.", false);
                return;
            }
            if (newEmail.contains("|")) {
                showFeedback(feedback, "Email cannot contain the '|' character.", false);
                return;
            }
            boolean success = UserManager.changeEmail(newEmail);
            if (success) {
                showFeedback(feedback, "Email updated to: " + newEmail, true);
            } else {
                showFeedback(feedback, "Email already in use by another account.", false);
            }
        });

        VBox content = new VBox(fieldLabel("Email Address"), emailField, saveBtn, feedback);
        content.setSpacing(10);
        return createCard("Change Email", content);
    }

    // Creates the card for changing the password
    private VBox createPasswordCard() {
        PasswordField current = styledPasswordField("Current Password");
        PasswordField newPass = styledPasswordField("New Password");
        PasswordField confirm = styledPasswordField("Confirm New Password");

        Label feedback = feedbackLabel();
        Button changeBtn = styledPrimaryButton("Change Password");
        changeBtn.setOnAction(e -> {
            if (newPass.getText().isEmpty()) {
                showFeedback(feedback, "New password cannot be empty.", false);
                return;
            }
            if (newPass.getText().length() < 3) {
                showFeedback(feedback, "Password must be at least 3 characters.", false);
                return;
            }
            if (newPass.getText().contains("|")) {
                showFeedback(feedback, "Password cannot contain the '|' character.", false);
                return;
            }
            if (!newPass.getText().equals(confirm.getText())) {
                showFeedback(feedback, "Passwords do not match.", false);
                return;
            }
            boolean success = UserManager.changePassword(current.getText(), newPass.getText());
            if (success) {
                showFeedback(feedback, "Password changed successfully.", true);
                current.clear();
                newPass.clear();
                confirm.clear();
            } else {
                showFeedback(feedback, "Current password is incorrect.", false);
            }
        });

        VBox content = new VBox(
                fieldLabel("Current Password"), current,
                fieldLabel("New Password"), newPass,
                fieldLabel("Confirm New Password"), confirm,
                changeBtn, feedback
        );
        content.setSpacing(10);
        return createCard("Change Password", content);
    }

    // Creates the card with the logout button
    private VBox createLogoutCard() {
        Label desc = new Label("End your current session and return to the welcome screen.");
        desc.setStyle("-fx-text-fill:#90a1b9; -fx-font-size:14px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            System.out.println("SettingsScreen: logging out");
            UserManager.logout();
            if (onLogout != null) onLogout.run();
        });

        // Red logout button styling
        String baseStyle =
                "-fx-background-color:#e7000b;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:6;" +
                "-fx-cursor: hand;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;";

        String hoverStyle =
                "-fx-background-color:#ff1a1a;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:6;" +
                "-fx-cursor: hand;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;";

        String pressedStyle =
                "-fx-background-color:#b80000;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:6;" +
                "-fx-cursor: hand;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;";

        logoutBtn.setStyle(baseStyle);
        logoutBtn.setPrefHeight(40);
        logoutBtn.setOnMouseEntered(e -> {
                logoutBtn.setStyle(hoverStyle);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), logoutBtn);
                st.setToX(1.03); st.setToY(1.03); st.play();
        });
        logoutBtn.setOnMouseExited(e -> {
                logoutBtn.setStyle(baseStyle);
                ScaleTransition st = new ScaleTransition(Duration.millis(150), logoutBtn);
                st.setToX(1.0); st.setToY(1.0); st.play();
        });
        logoutBtn.setOnMousePressed(e -> {
                logoutBtn.setStyle(pressedStyle);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), logoutBtn);
                st.setToX(0.97); st.setToY(0.97); st.play();
        });
        logoutBtn.setOnMouseReleased(e -> {
                logoutBtn.setStyle(hoverStyle);
                ScaleTransition st = new ScaleTransition(Duration.millis(80), logoutBtn);
                st.setToX(1.03); st.setToY(1.03); st.play();
        });

        VBox content = new VBox(desc, logoutBtn);
        content.setSpacing(12);
        return createCard("Logout", content);
    }
}
