package ca.ucalgary.seng300;

import ca.ucalgary.seng300.screens.WelcomePage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new WelcomePage(), 1386, 781);
        stage.setScene(scene);
        stage.setTitle("Welcome Page - Haris");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
