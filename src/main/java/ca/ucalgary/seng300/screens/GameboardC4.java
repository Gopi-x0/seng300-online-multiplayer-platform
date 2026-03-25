package ca.ucalgary.seng300.screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;



public class GameboardC4 extends Application{

    private int ROWS = 6;
    private int COLS = 7;
    private int CAPACITY = ROWS * COLS;
    private int CELLSPACING = 75;
    private int CIRCLERADIUS = 60;

    private StackPane[][] cells = new StackPane[ROWS][COLS];

    public void start(Stage primaryStage){
        VBox grid = createGrid(ROWS, COLS, CELLSPACING, CIRCLERADIUS, cells);
        StackPane root = new StackPane(grid);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.resizableProperty().setValue(true);
        primaryStage.show();

        //DYNAMIC RESIZING
        primaryStage.widthProperty().addListener((o,old,newW) -> root.requestLayout());
        primaryStage.heightProperty().addListener((o,old,newH) -> root.requestLayout());

    }

    static void main(String[] args){
        launch(args);
    }

    private VBox createGrid(int rows, int cols, int spacing, int rad, StackPane[][] grid){
        //Create an outer vbox to stack the rows vertically

        VBox outer = new  VBox();
        float space = 9.50f;
        outer.setAlignment(Pos.CENTER);
        outer.setSpacing(space);

        //For loop that iterates through the rows, creates an HBox for rows, then nested for loop to create a circle in a stackplane for it.
        for(int i = 0; i < rows; i++){
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            for(int j = 0; j < cols; j++){
                Circle circle = new Circle(rad);
                circle.setFill(Color.GREY); //Grey because we are going to make it empty by default
                StackPane cell = new StackPane(circle);
                cell.setPrefSize(spacing, spacing);

                row.getChildren().add(cell); //add the cells to HBox row

                grid[i][j] = cell; // Map our visual grid numerically in this 2D StackPane

            }
            outer.getChildren().add(row); //add cells to outer
        }


        return outer;
    }


}