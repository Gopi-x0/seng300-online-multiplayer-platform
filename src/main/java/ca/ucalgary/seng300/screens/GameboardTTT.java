package ca.ucalgary.seng300.screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class GameboardTTT extends Application {


    //Logic
    private int ROWS = 3;
    private int COLS = 3;
    private int CELLS = ROWS * COLS;

    @Override
    public void start(Stage primaryStage){
        //Label






    }

    private GridPane createGrid(){
        GridPane gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);

        gameBoard.setStyle("-fx-background-color: black; -fx-padding: 2;");
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);

        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                StackPane cell = new StackPane();
                cell.setPrefSize(150, 150);
                cell.setStyle("-fx-background-color: white; -fx-padding: 2;");
                gameBoard.add(cell, i, j);
            }
        }
        return gameBoard;
    }

}
