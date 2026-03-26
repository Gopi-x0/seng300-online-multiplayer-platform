package ca.ucalgary.seng300;

import ca.ucalgary.seng300.screens.AuthenticationScreen;
import ca.ucalgary.seng300.screens.HomePage;
import ca.ucalgary.seng300.screens.WinScreen;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane appShell;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        appShell = new BorderPane();
        appShell.setTop(buildDevToolbar());

        // Default screen temp
        showAuthenticationScreen();

        Scene scene = new Scene(appShell, 1100, 700);
        stage.setScene(scene);
        stage.setTitle("SGP - UI Navigation Test");
        stage.show();
    }

    private ToolBar buildDevToolbar() {
        Button authBtn       = new Button("Authentication");
        Button homeBtn       = new Button("Home");
        Button winTTTBtn     = new Button("Win (TTT)");
        Button loseTTTBtn    = new Button("Lose (TTT)");
        Button winC4Btn      = new Button("Win (C4)");
        Button loseC4Btn     = new Button("Lose (C4)");

        authBtn.setOnAction(e    -> showAuthenticationScreen());
        homeBtn.setOnAction(e    -> showHomePage());
        winTTTBtn.setOnAction(e  -> showOverlay(true,  WinScreen.GameType.TTT));
        loseTTTBtn.setOnAction(e -> showOverlay(false, WinScreen.GameType.TTT));
        winC4Btn.setOnAction(e   -> showOverlay(true,  WinScreen.GameType.C4));
        loseC4Btn.setOnAction(e  -> showOverlay(false, WinScreen.GameType.C4));

        ToolBar toolBar = new ToolBar(
                authBtn, homeBtn,
                winTTTBtn, loseTTTBtn,
                winC4Btn, loseC4Btn
        );
        toolBar.setPadding(new Insets(5));
        return toolBar;
    }

    private void showAuthenticationScreen() {
        AuthenticationScreen screen = new AuthenticationScreen();
        appShell.setCenter(screen);
        primaryStage.setTitle("SGP - Authentication");
    }

    private void showHomePage() {
        HomePage homePage = new HomePage();
        appShell.setCenter(homePage);
        primaryStage.setTitle("SGP - Home");
    }

    private void showOverlay(boolean isWin, WinScreen.GameType gameType) {
        // Put a HomePage behind the overlay so the background is visible
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

    public static void main(String[] args) {
        launch(args);
    }
}