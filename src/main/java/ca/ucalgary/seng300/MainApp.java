package ca.ucalgary.seng300;

import ca.ucalgary.seng300.model.GameType;
import ca.ucalgary.seng300.network.GameClient;
import ca.ucalgary.seng300.network.GameServer;
import ca.ucalgary.seng300.screens.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main application entry point and screen navigation.
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane appShell;
    private Label titleLabel;

    // Window drag offset tracking
    private double dragOffsetX, dragOffsetY;
    private boolean maximized = false;
    private double savedX, savedY, savedW, savedH;

    // Edge resize state
    private boolean resizing = false;
    private Cursor resizeDir;
    private double resizeStartScreenX, resizeStartScreenY;
    private double resizeStartStageX, resizeStartStageY;
    private double resizeStartStageW, resizeStartStageH;

    /**
     * Sets up the main stage with an undecorated window and custom title bar.
     * @author Muhammad Ahmad
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);

        appShell = new BorderPane();
        appShell.setTop(buildCustomTitleBar());

        showWelcomePage();

        Scene scene = new Scene(appShell, 1300, 900);
        stage.setScene(scene);
        enableEdgeResize(scene);
        stage.show();
    }

    /**
     * Updates the window title and the title label in the custom title bar.
     * @author Muhammad Ahmad
     */
    private void setAppTitle(String title) {
        primaryStage.setTitle(title);
        if (titleLabel != null) titleLabel.setText(title);
    }

    // Custom Title Bar

    /**
     * Builds the custom title bar with minimize, maximize, close buttons and drag support.
     * @author Muhammad Ahmad
     */
    private HBox buildCustomTitleBar() {
        titleLabel = new Label("Student Gaming Platform");
        // Title label styling
        titleLabel.setStyle(
                "-fx-text-fill: #8fa4be;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );
        titleLabel.setPadding(new Insets(0, 0, 0, 6));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minBtn = titleBarButton("\u2014", "normal");
        Button maxBtn = buildMaximizeTitleBarButton();
        Button closeBtn = titleBarButton("\u2715", "close");

        minBtn.setOnAction(e -> primaryStage.setIconified(true));
        maxBtn.setOnAction(e -> toggleMaximize());
        closeBtn.setOnAction(e -> {
            GameServer.shutdownActive();
            GameClient.shutdownActive();
            primaryStage.close();
        });

        HBox buttons = new HBox(minBtn, maxBtn, closeBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        HBox bar = new HBox(titleLabel, spacer, buttons);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 0, 8));
        bar.setPrefHeight(34);
        // Dark background with a subtle bottom border
        bar.setStyle(
                "-fx-background-color: #070d1a;" +
                "-fx-border-color: transparent transparent rgba(72,114,170,0.2) transparent;"
        );

        // Drag to move the window
        bar.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragOffsetX = e.getSceneX();
                dragOffsetY = e.getSceneY();
            }
        });
        bar.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (maximized) {
                    double relX = e.getSceneX() / primaryStage.getWidth();
                    toggleMaximize();
                    dragOffsetX = savedW * relX;
                    dragOffsetY = e.getSceneY();
                }
                primaryStage.setX(e.getScreenX() - dragOffsetX);
                primaryStage.setY(e.getScreenY() - dragOffsetY);
            }
        });
        bar.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                toggleMaximize();
            }
        });

        return bar;
    }

    /**
     * Creates the maximize button with a custom glyph that is nudged up for alignment.
     * @author Muhammad Ahmad
     */
    private Button buildMaximizeTitleBarButton() {
        Label glyph = new Label("\u25A1");
        glyph.setStyle("-fx-text-fill: #8fa4be; -fx-font-size: 17px;");
        glyph.setTranslateY(-2.5);
        glyph.setMouseTransparent(true);

        Button btn = new Button();
        btn.setGraphic(glyph);
        btn.setText(null);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setAlignment(Pos.CENTER);
        btn.setPrefSize(46, 34);
        btn.setMinSize(46, 34);
        btn.setMaxSize(46, 34);

        // Hover styling for the maximize button
        String base = "-fx-background-color: transparent;" +
                "-fx-background-radius: 0;" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;";
        String hover = "-fx-background-color: rgba(255,255,255,0.08);" +
                "-fx-background-radius: 0;" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;";
        String glyphBase = "-fx-text-fill: #8fa4be; -fx-font-size: 17px;";
        String glyphHover = "-fx-text-fill: white; -fx-font-size: 17px;";

        btn.setStyle(base);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hover);
            glyph.setStyle(glyphHover);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(base);
            glyph.setStyle(glyphBase);
        });
        return btn;
    }

    /**
     * Creates a generic title bar button (minimize or close) with hover effects.
     * @author Muhammad Ahmad
     */
    private Button titleBarButton(String text, String type) {
        Button btn = new Button(text);
        btn.setPrefSize(46, 34);
        btn.setMinSize(46, 34);
        btn.setMaxSize(46, 34);
        btn.setAlignment(Pos.CENTER);
        btn.setContentDisplay(ContentDisplay.CENTER);

        String fontSize = "14px";
        String base, hover;

        // Close button is red, other buttons use the default style
        if (type.equals("close")) {
            base = "-fx-background-color: transparent;" +
                   "-fx-text-fill: #e81123;" +
                   "-fx-font-size: " + fontSize + ";" +
                   "-fx-background-radius: 0;" +
                   "-fx-padding: 0;" +
                   "-fx-cursor: hand;";
            hover = "-fx-background-color: #e81123;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: " + fontSize + ";" +
                    "-fx-background-radius: 0;" +
                    "-fx-padding: 0;" +
                    "-fx-cursor: hand;";
        } else {
            base = "-fx-background-color: transparent;" +
                   "-fx-text-fill: #8fa4be;" +
                   "-fx-font-size: " + fontSize + ";" +
                   "-fx-background-radius: 0;" +
                   "-fx-padding: 0;" +
                   "-fx-cursor: hand;";
            hover = "-fx-background-color: rgba(255,255,255,0.08);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: " + fontSize + ";" +
                    "-fx-background-radius: 0;" +
                    "-fx-padding: 0;" +
                    "-fx-cursor: hand;";
        }
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    /**
     * Toggles between maximized and restored window state.
     * @author Muhammad Ahmad
     */
    private void toggleMaximize() {
        if (maximized) {
            primaryStage.setX(savedX);
            primaryStage.setY(savedY);
            primaryStage.setWidth(savedW);
            primaryStage.setHeight(savedH);
            maximized = false;
        } else {
            savedX = primaryStage.getX();
            savedY = primaryStage.getY();
            savedW = primaryStage.getWidth();
            savedH = primaryStage.getHeight();
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            maximized = true;
        }
    }

    /**
     * Adds mouse event filters to the scene so the user can drag window edges to resize.
     * @author Muhammad Ahmad
     */
    private void enableEdgeResize(Scene scene) {
        final int M = 5;
        final double MIN_W = 1050, MIN_H = 600;

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if (maximized) { scene.setCursor(Cursor.DEFAULT); return; }
            double x = e.getX(), y = e.getY();
            double w = scene.getWidth(), h = scene.getHeight();
            boolean t = y < M, b = y > h - M, l = x < M, r = x > w - M;
            if      (t && l) scene.setCursor(Cursor.NW_RESIZE);
            else if (t && r) scene.setCursor(Cursor.NE_RESIZE);
            else if (b && l) scene.setCursor(Cursor.SW_RESIZE);
            else if (b && r) scene.setCursor(Cursor.SE_RESIZE);
            else if (t)      scene.setCursor(Cursor.N_RESIZE);
            else if (b)      scene.setCursor(Cursor.S_RESIZE);
            else if (l)      scene.setCursor(Cursor.W_RESIZE);
            else if (r)      scene.setCursor(Cursor.E_RESIZE);
            else             scene.setCursor(Cursor.DEFAULT);
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (scene.getCursor() != Cursor.DEFAULT && !maximized) {
                resizing = true;
                resizeDir = scene.getCursor();
                resizeStartScreenX = e.getScreenX();
                resizeStartScreenY = e.getScreenY();
                resizeStartStageX = primaryStage.getX();
                resizeStartStageY = primaryStage.getY();
                resizeStartStageW = primaryStage.getWidth();
                resizeStartStageH = primaryStage.getHeight();
                e.consume();
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!resizing) return;
            double dx = e.getScreenX() - resizeStartScreenX;
            double dy = e.getScreenY() - resizeStartScreenY;
            Cursor d = resizeDir;
            if (d == Cursor.E_RESIZE || d == Cursor.NE_RESIZE || d == Cursor.SE_RESIZE) {
                primaryStage.setWidth(Math.max(MIN_W, resizeStartStageW + dx));
            }
            if (d == Cursor.S_RESIZE || d == Cursor.SE_RESIZE || d == Cursor.SW_RESIZE) {
                primaryStage.setHeight(Math.max(MIN_H, resizeStartStageH + dy));
            }
            if (d == Cursor.W_RESIZE || d == Cursor.NW_RESIZE || d == Cursor.SW_RESIZE) {
                double newW = resizeStartStageW - dx;
                if (newW >= MIN_W) {
                    primaryStage.setX(resizeStartStageX + dx);
                    primaryStage.setWidth(newW);
                }
            }
            if (d == Cursor.N_RESIZE || d == Cursor.NW_RESIZE || d == Cursor.NE_RESIZE) {
                double newH = resizeStartStageH - dy;
                if (newH >= MIN_H) {
                    primaryStage.setY(resizeStartStageY + dy);
                    primaryStage.setHeight(newH);
                }
            }
            e.consume();
        });

        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (resizing) {
                resizing = false;
                e.consume();
            }
        });
    }

    // Screen Navigation

    /**
     * Shows the welcome/landing page.
     * @author Muhammad Ahmad
     */
    private void showWelcomePage() {
        WelcomePage screen = new WelcomePage(this::showAuthenticationScreen);
        appShell.setCenter(screen);
        setAppTitle("Student Gaming Platform - Welcome");
    }

    /**
     * Shows the login/register screen.
     * @author Muhammad Ahmad
     */
    private void showAuthenticationScreen() {
        AuthenticationScreen screen = new AuthenticationScreen(this::showHomePage);
        appShell.setCenter(screen);
        setAppTitle("Student Gaming Platform - Authentication");
    }

    /**
     * Shows the main home page with game selection options.
     * @author Muhammad Ahmad
     */
    private void showHomePage() {
        HomePage screen = new HomePage(
                this::showSettingsScreen,
                this::showInstructionsTTT,
                this::showInstructionsC4,
                this::showProfileDashboard
        );
        appShell.setCenter(screen);
        setAppTitle("Student Gaming Platform - Home");
    }

    /**
     * Shows the user's profile dashboard.
     * @author Muhammad Ahmad
     */
    private void showProfileDashboard() {
        ProfileDashboard screen = new ProfileDashboard(this::showHomePage);
        appShell.setCenter(screen);
        setAppTitle("Student Gaming Platform - Profile");
    }

    /**
     * Shows the settings screen where the user can change preferences or log out.
     * @author Muhammad Ahmad
     */
    private void showSettingsScreen() {
        SettingsScreen screen = new SettingsScreen(
                this::showHomePage,
                this::showWelcomePage
        );
        appShell.setCenter(screen);
        setAppTitle("Student Gaming Platform - Settings");
    }

    /**
     * Shows the Tic-Tac-Toe instructions screen.
     * @author Muhammad Ahmad
     */
    private void showInstructionsTTT() {
        InstructionsTTT instructions = new InstructionsTTT(() -> showLobby(GameType.TTT), this::showHomePage);
        appShell.setCenter(instructions);
        setAppTitle("Student Gaming Platform - Instructions (Tic-Tac-Toe)");
    }

    /**
     * Shows the Connect 4 instructions screen.
     * @author Muhammad Ahmad
     */
    private void showInstructionsC4() {
        InstructionsC4 instructions = new InstructionsC4(() -> showLobby(GameType.C4), this::showHomePage);
        appShell.setCenter(instructions);
        setAppTitle("Student Gaming Platform - Instructions (Connect 4)");
    }

    // Lobby Flow

    /**
     * Shows the lobby screen where the user can host or join a game.
     * @author Muhammad Ahmad
     */
    private void showLobby(GameType gameType) {
        String gameName = gameType == GameType.TTT ? "Tic Tac Toe" : "Connect 4";
        Lobby lobby = new Lobby(gameName, gameType,
                () -> showHostLobby(gameType),
                () -> showJoinLobby(gameType),
                this::showHomePage
        );
        appShell.setCenter(lobby);
        setAppTitle("Student Gaming Platform - " + gameName + " Lobby");
    }

    /**
     * Shows the host lobby screen and waits for a guest to connect.
     * @author Muhammad Ahmad
     */
    private void showHostLobby(GameType gameType) {
        String gameName = gameType == GameType.TTT ? "Tic Tac Toe" : "Connect 4";
        HostLobby hostLobby = new HostLobby(gameName, gameType,
                () -> showLobby(gameType),
                (p1, p2) -> showGameboard(gameType, p1, p2, true)
        );
        appShell.setCenter(hostLobby);
        setAppTitle("Student Gaming Platform - Hosting " + gameName);
    }

    /**
     * Shows the join lobby screen where the user enters a lobby code.
     * @author Muhammad Ahmad
     */
    private void showJoinLobby(GameType gameType) {
        String gameName = gameType == GameType.TTT ? "Tic Tac Toe" : "Connect 4";
        JoinLobby joinLobby = new JoinLobby(gameName, gameType,
                () -> showLobby(gameType),
                (p1, p2) -> showGameboard(gameType, p1, p2, false)
        );
        appShell.setCenter(joinLobby);
        setAppTitle("Student Gaming Platform - Join " + gameName);
    }

    /**
     * Opens the gameboard for the selected game type. Cleans up server/client on navigate home.
     * @author Muhammad Ahmad
     */
    private void showGameboard(GameType gameType, String p1Name, String p2Name, boolean isHost) {
        Runnable onNavigateHome = () -> {
            GameServer.shutdownActive();
            GameClient.shutdownActive();
            showHomePage();
        };

        if (gameType == GameType.TTT) {
            GameboardTTT board = new GameboardTTT(onNavigateHome, p1Name, p2Name, isHost);
            appShell.setCenter(board);
            setAppTitle("Student Gaming Platform - Tic-Tac-Toe");
        } else {
            GameboardC4 board = new GameboardC4(onNavigateHome, p1Name, p2Name, isHost);
            appShell.setCenter(board);
            setAppTitle("Student Gaming Platform - Connect 4");
        }
    }

    /**
     * Application entry point.
     * @author Muhammad Ahmad
     */
    public static void main(String[] args) {
        launch(args);
    }
}
