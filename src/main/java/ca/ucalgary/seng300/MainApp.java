package ca.ucalgary.seng300;

import ca.ucalgary.seng300.screens.WelcomePage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // for now just launching the welcome page first so we can style it and test it easier
        Scene scene = new Scene(new WelcomePage(), 1386, 781);
        stage.setScene(scene);
        // just making the window title match the screen we are looking at rn
        stage.setTitle("Welcome Page - Haris");
        stage.show();
    }

    public static void main(String[] args) {
        // normal javafx launch point
        launch(args);
    }
}
