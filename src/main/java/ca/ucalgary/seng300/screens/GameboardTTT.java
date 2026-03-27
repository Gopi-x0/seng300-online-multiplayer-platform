package ca.ucalgary.seng300.screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class GameboardTTT extends Application {


    //Logic
    private int ROWS = 3;
    private int COLS = 3;
    private int CELLS = ROWS * COLS;
    private int CIRCLERADIUS = 60;
    private StackPane[][] board = new StackPane[ROWS][COLS];

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

        //User Streaks - X and O
        VBox streakX = createPlayerCurrentStreakX(ROWS);
        streakX.setAlignment(Pos.BOTTOM_LEFT);

        VBox streakO = createPlayerCurrentStreakO(ROWS, CIRCLERADIUS);
        streakO.setAlignment(Pos.BOTTOM_RIGHT);

        //Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(#00d3f3, #0088a3);");
        BorderPane.setMargin(titleBox, new Insets(30, 40, 40, 40));

        //Top
        mainLayout.setTop(titleBox);

        //Center
        mainLayout.setCenter(grid);

        //Left & Right --> User Streaks
        mainLayout.setLeft(streakX);
        mainLayout.setRight(streakO);

        //Root & Scene
        StackPane root = new  StackPane(mainLayout);
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
        gameBoard.setMaxSize(600, 600);
        gameBoard.setPadding(new Insets(20));
        gameBoard.setStyle("-fx-background-color: black; -fx-padding: 2;");
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);
        gameBoard.setStyle("-fx-background-color: transparent;");

        for(int i = 0; i < ROWS; i++){
            //resizes dynamically the grid with the page.
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.33);
            gameBoard.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(33.33);
            gameBoard.getRowConstraints().add(row);
        }

        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                StackPane cell = new StackPane();
                cell.setPrefSize(150, 150);
                cell.setStyle("-fx-background-color: white; -fx-padding: 2;");
                gameBoard.add(cell, i, j);
                board[i][j] = cell;
            }

        }
        return gameBoard;
    }

    private VBox createPlayerCurrentStreakO(int tiles, int radius){
        VBox streak = new VBox();
        float space = 9.50f;
        streak.setSpacing(space);

        for(int j = 0; j < tiles; j++){
            Circle circle = new Circle(radius);
            circle.setFill(Color.GREY);
            StackPane cell = new StackPane(circle);
            cell.setPrefSize(space, space );
            streak.getChildren().add(cell);
        }
        return streak;
    }

    private VBox createPlayerCurrentStreakX(int tiles){
        VBox streak = new VBox();
        float space = 9.50f;
        streak.setSpacing(space);

        for(int j = 0; j < tiles; j++){
            StackPane X = createX();
            streak.getChildren().add(X);
        }
        return streak;
    }

    private StackPane createX(){
        double half = (double) 80 / 2;

        Line l1 = new Line(-half, -half, half, half);
        l1.setStroke(Color.GREY);
        l1.setStrokeWidth(4);

        Line l2 = new Line(half, -half, -half, half);
        l2.setStroke(Color.GREY);
        l2.setStrokeWidth(4);

        StackPane pane = new StackPane(l1, l2);
        pane.setPrefSize(80, 80);
        return pane;
    }

}
