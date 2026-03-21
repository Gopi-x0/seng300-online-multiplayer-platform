package ca.ucalgary.seng300.screens;

/**
 * This is the instructions screen for Connect 4, this prompts/plays beforehand
 * for users to familiarize themselfves with the game.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructionsC4 extends Application {

    @Override
    public void start(Stage stage) {
        //Title Label
        Label title = new Label("Connect 4");
        title.setStyle("-fx-text-fill : #00d3f3; - fx-font-size: 36px; -fx-font-weight: bold;");

        Label subTitle = new Label("Instructions & Rules");
        subTitle.setStyle("fx-text-fill : #90a1b9; -fx-font-size: 16px;");


        //Create a new label this will be the primary sense of instructions and objectives the user can read.
        Label displayInstructions = new Label("WELCOME TO CONNECT 4!" +
                "RULES : You may only place one tile at a time of your corresponding colour" +
                "RULES : You may only place a tile in a physically possible spot on the grid." +
                "OBJECTIVE : You must place FOUR consecutive tiles before your opponent either : diagonally, horizontally, or vertically.");

        VBox root = new VBox(); //Initialize a VBox
        root.setSpacing(10); // Set the spacing of the root
        root.getChildren().add(displayInstructions);

        Scene c4 = new Scene(root ,400 , 200);

        stage.setTitle("Connect 4 : Instructions & Rules");
        stage.setScene(c4);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
