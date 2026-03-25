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

public class GameboardC4 extends Application{

    //Circle Primitatives
    private int ROWS = 6;
    private int COLS = 7;
    private int CAPACITY = ROWS * COLS;
    private int CELLSPACING = 75;
    private int CIRCLERADIUS = 60;

    //Grid
    private StackPane[][] cells = new StackPane[ROWS][COLS];

    //Distances
    private int diameter = CIRCLERADIUS * 2;
    private int length = (diameter * COLS) + CELLSPACING;
    private int width = (diameter * ROWS) + CELLSPACING;

    @Override
    public void start(Stage primaryStage){
        //Title Label
        Label title = new Label("Connect 4");
        title.setStyle("-fx-text-fill : black; -fx-font-size: 48px; -fx-font-weight: bold;");

        //Subtitle Label
        Label subTitle = new Label("Your turn");
        subTitle.setStyle("-fx-text-fill : black; -fx-font-size: 32px;");

        //Vbox for label
        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(5);

        //Create the gameboard grid of circle tiles
        VBox grid = createGrid(ROWS, COLS, CELLSPACING, CIRCLERADIUS, cells, length, width);

        //UI Layout stacks both the title labels and the grid
        VBox layout = new VBox(titleBox, grid);
        layout.setAlignment(Pos.CENTER);

        //Stackpane Root for centering and resizing
        StackPane root = new StackPane(layout);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.resizableProperty().setValue(true);
        primaryStage.show();

        //Gameboard Border
        grid.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(15), Insets.EMPTY)));
        grid.setPadding(new Insets(CELLSPACING));
        grid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);


        //DYNAMIC RESIZING
        primaryStage.widthProperty().addListener((o,old,newW) -> root.requestLayout());
        primaryStage.heightProperty().addListener((o,old,newH) -> root.requestLayout());

    }

    private VBox createGrid(int rows, int cols, int spacing, int rad, StackPane[][] grid, int length, int width){
        //Create an outer vbox to stack the rows vertically

        VBox outer = new VBox();
        float space = 9.50f;
        outer.setAlignment(Pos.CENTER);
        outer.setSpacing(space);


        //For loop that iterates through the rows, creates an HBox for rows, then nested for loop to create a circle in a stackpane for it.
        for(int i = 0; i < rows; i++){
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            for(int j = 0; j < cols; j++){
                Circle circle = new Circle(rad);
                circle.setFill(Color.GHOSTWHITE); //Grey because we are going to make it empty by default
                StackPane cell = new StackPane(circle);
                cell.setPrefSize(spacing, spacing);

                row.getChildren().add(cell); //add the cells to HBox row

                grid[i][j] = cell; // Map our visual grid numerically in this 2D StackPane
            }
            outer.getChildren().add(row); //add cells to outer
        }


        return outer;
    }

    static void main(String[] args){
        launch(args);
    }

}