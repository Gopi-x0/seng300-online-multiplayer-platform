package ca.ucalgary.seng300;

import ca.ucalgary.seng300.screens.MainGameDashboard;
import ca.ucalgary.seng300.screens.WelcomePage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // temporary switch so we can preview the welcome page again
        Scene scene = new Scene(new MainGameDashboard(), 1386, 781);
        stage.setScene(scene);
        // matching the title to the screen we are previewing
        stage.setTitle("Welcome Page - Haris");
        stage.show();
    }

    public static void main(String[] args) {
        // normal javafx launch point
        launch(args);
    }
}
