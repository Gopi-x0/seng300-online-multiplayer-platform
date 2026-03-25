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
        //Title
        Label title = new Label("Tic-Tac-Toe");
        title.setStyle("-fx-text-fill : white; -fx-font-size: 48px; -fx-font-weight: bold;");

        //Sub-Title
        Label subTitle = new Label("Your turn");
        title.setStyle("-fx-text-fill : white; -fx-font-size: 48px; -fx-font-weight: bold;");

        //Labels Vbox
        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(5);

        //Grid VBox
        GridPane grid = createGrid();

        //Root
        StackPane root = new StackPane(titleBox, grid);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Toe");
        primaryStage.resizableProperty().setValue(true);
        primaryStage.show();

        //DYNAMIC RESIZING
        primaryStage.widthProperty().addListener((o,old,newW) -> root.requestLayout());
        primaryStage.heightProperty().addListener((o,old,newH) -> root.requestLayout());

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
