package ca.ucalgary.seng300;

import ca.ucalgary.seng300.screens.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * MainApp is the dev entry point and navigation test harness.
 * The toolbar lets you jump directly to any screen for testing.
 * In the final product the toolbar will be removed and navigation
 * will be driven by buttons inside each screen.
 *
 * Screens covered:
 *   WelcomePage, AuthenticationScreen, HomePage, MainGameDashboard,
 *   ProfileDashboard, SettingsScreen, InstructionsTTT, InstructionsC4,
 *   WinScreen (TTT win/lose, C4 win/lose)
 *
 * @author Muhammad Ahmad
 * @email muham.ahmad@ucalgary.ca
 *
 * Note: GameboardTTT and GameboardC4 extend Application so they launch
 * their own Stage. They are accessible via the toolbar buttons below.
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane appShell;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        appShell = new BorderPane();
        appShell.setTop(buildDevToolbar());

        // Default screen on launch
        showWelcomePage();

        Scene scene = new Scene(appShell, 1100, 900);
        stage.setScene(scene);
        stage.setTitle("SGP - UI Navigation Test");
        stage.show();

    }

    // Dev Toolbar

    private ToolBar buildDevToolbar() {
        // Auth flow
        Button welcomeBtn  = new Button("Welcome");
        Button authBtn     = new Button("Authentication");

        // Main screens
        Button homeBtn      = new Button("Home");
        Button dashboardBtn = new Button("Dashboard");
        Button profileBtn   = new Button("Profile");
        Button settingsBtn  = new Button("Settings");

        // Game instructions (embedded, no separate window)
        Button instrTTTBtn = new Button("Instructions TTT");
        Button instrC4Btn  = new Button("Instructions C4");

        // Win/Lose overlays
        Button winTTTBtn  = new Button("Win (TTT)");
        Button loseTTTBtn = new Button("Lose (TTT)");
        Button winC4Btn   = new Button("Win (C4)");
        Button loseC4Btn  = new Button("Lose (C4)");

        // Wire actions
        welcomeBtn.setOnAction(e   -> showWelcomePage());
        authBtn.setOnAction(e      -> showAuthenticationScreen());
        homeBtn.setOnAction(e      -> showHomePage());
        dashboardBtn.setOnAction(e -> showMainGameDashboard());
        profileBtn.setOnAction(e   -> showProfileDashboard());
        settingsBtn.setOnAction(e  -> showSettingsScreen());
        instrTTTBtn.setOnAction(e  -> showInstructionsTTT());
        instrC4Btn.setOnAction(e   -> showInstructionsC4());
        winTTTBtn.setOnAction(e    -> showOverlay(true,  WinScreen.GameType.TTT));
        loseTTTBtn.setOnAction(e   -> showOverlay(false, WinScreen.GameType.TTT));
        winC4Btn.setOnAction(e     -> showOverlay(true,  WinScreen.GameType.C4));
        loseC4Btn.setOnAction(e    -> showOverlay(false, WinScreen.GameType.C4));

        Button launchTTT = new Button("Launch TTT");
        Button launchC4  = new Button("Launch C4");

        launchTTT.setOnAction(e -> {
            try {
                new GameboardTTT().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        launchC4.setOnAction(e -> {
            try {
                new GameboardC4().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ToolBar toolBar = new ToolBar(
                welcomeBtn, authBtn,
                new Separator(),
                homeBtn, dashboardBtn, profileBtn, settingsBtn,
                new Separator(),
                instrTTTBtn, instrC4Btn,
                new Separator(),
                winTTTBtn, loseTTTBtn, winC4Btn, loseC4Btn,
                new Separator(),
                launchTTT, launchC4
        );
        toolBar.setPadding(new Insets(5));

        return toolBar;
    }

    // Screen methods

    private void showWelcomePage() {
        WelcomePage screen = new WelcomePage();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Welcome");
    }

    private void showAuthenticationScreen() {
        AuthenticationScreen screen = new AuthenticationScreen();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Authentication");
    }

    private void showHomePage() {
        HomePage screen = new HomePage();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Home");
    }

    private void showMainGameDashboard() {
        MainGameDashboard screen = new MainGameDashboard();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Dashboard");
    }

    private void showProfileDashboard() {
        ProfileDashboard screen = new ProfileDashboard();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Profile");
    }

    private void showSettingsScreen() {
        SettingsScreen screen = new SettingsScreen();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Settings");
    }

    /**
     * InstructionsTTT extends Application and would normally open its own Stage.
     * To embed it in the main window for testing, we instantiate it as a VBox-like
     * layout by building the scene root manually and placing it in appShell.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     *
     * Note: For P3 integration, InstructionsTTT should be refactored to extend VBox
     * so it can be embedded directly without this workaround.
     */
    private void showInstructionsTTT() {
        // Build the screen content as a standalone Stage-less scene root
        // by re-using the same layout logic InstructionsTTT would use.
        // Since InstructionsTTT extends Application, we use a lightweight wrapper here.
        InstructionsTTT instructions = new InstructionsTTT();
        Stage tempStage = new Stage();
        try {
            instructions.start(tempStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        primaryStage.setTitle("SGP - Instructions (Tic-Tac-Toe)");
    }

    /**
     * Same approach as showInstructionsTTT above.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    private void showInstructionsC4() {
        InstructionsC4 instructions = new InstructionsC4();
        Stage tempStage = new Stage();
        try {
            instructions.start(tempStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        primaryStage.setTitle("SGP - Instructions (Connect 4)");
    }

    /**
     * Shows the WinScreen overlay on top of a HomePage background.
     * In the real game, the background would be the actual game board.
     * The onDismiss callback navigates back to HomePage.
     *
     * @author Muhammad Ahmad
     * @email muham.ahmad@ucalgary.ca
     */
    private void showOverlay(boolean isWin, WinScreen.GameType gameType) {
        HomePage homePage = new HomePage();
        StackPane gameRoot = new StackPane(homePage);

        if (isWin) {
            WinScreen.showWin(gameRoot, this::showHomePage, gameType);
            primaryStage.setTitle("SGP - Win Overlay (" + gameType + ")");
        } else {
            WinScreen.showLose(gameRoot, this::showHomePage, gameType);
            primaryStage.setTitle("SGP - Lose Overlay (" + gameType + ")");
        }

        appShell.setCenter(gameRoot);
    }

    // Entry point

    public static void main(String[] args) {
        launch(args);
    }
}