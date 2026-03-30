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
    private int CELLSPACING = 75;
    private int CIRCLERADIUS = 60;

    //Grid
    private StackPane[][] cells = new StackPane[ROWS][COLS];

    //Distances
    private int diameter = CIRCLERADIUS * 2;
    private int length = (diameter * COLS) + CELLSPACING;
    private int width = (diameter * ROWS) + CELLSPACING;

    //Logic
    private int CAPACITY = ROWS * COLS;
    private int CONSECUTIVE = 4;

    @Override
    public void start(Stage primaryStage){
        //Setting window size
        primaryStage.setWidth(1400);
        primaryStage.setHeight(1100);

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

        //Create both user's consecutive streak they need to win for visual clarity
        VBox streakUserLeft = createPlayerCurrentTiles(CONSECUTIVE, CIRCLERADIUS);
        streakUserLeft.setAlignment(Pos.BOTTOM_LEFT);
        VBox streakUserRight = createPlayerCurrentTiles(CONSECUTIVE, CIRCLERADIUS);
        streakUserRight.setAlignment(Pos.BOTTOM_RIGHT);

        //BorderPane - multi-dimensional VBox
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleBox);
        BorderPane.setAlignment(titleBox, Pos.CENTER);

        //Add Left and Right User Streaks
        mainLayout.setLeft(streakUserLeft);
        BorderPane.setAlignment(streakUserLeft,  Pos.BOTTOM_RIGHT);

        mainLayout.setRight(streakUserRight);
        BorderPane.setAlignment(streakUserRight,  Pos.BOTTOM_CENTER);

        //Place the grid in the main middle
        mainLayout.setCenter(grid);
        mainLayout.setPadding(new Insets(20));

        //Stackpane Root for centering and resizing
        StackPane root = new StackPane(mainLayout);
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

    /**
     * This displays the current consecutive tiles streak the player has ( x in a row), default at 0 tiles filled.
     * @return a VBox to display with root in the BorderPane
     */
    private VBox createPlayerCurrentTiles(int tiles, int radius){
        VBox streak = new VBox();
        float space =  9.50f;
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



    static void main(String[] args){
        launch(args);
    }

}