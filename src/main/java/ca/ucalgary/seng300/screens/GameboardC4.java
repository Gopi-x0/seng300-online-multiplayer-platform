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



public class GameboardC4 extends Application {

    int ROWS = 6;
    int COLS = 7;
    int CAPACITY = ROWS * COLS;
    int CELLSPACING = 75;
    int CIRCLERADIUS = 30;


    public GameboardC4(){

    }


    private VBox createGrid(int rows, int cols, int spacing, int rad, int colour){
        //Create an outer vbox to stack the rows vertically
        VBox outer = new  VBox();

        //For loop that iterates through the rows, creates an HBox for rows, then nested for loop to create a circle in a stackplane for it.
        for(int i = 0; i <= rows; i++){
            HBox row = new HBox();
            for(int j = 0; j <= cols; j++){
                Circle circle = new Circle(rad);
                circle.setFill(Color.GREY); //Grey because we are going to make it empty by default
                StackPane cell = new StackPane(circle);
                cell.setPrefSize(spacing, spacing);

            }
        }

        return outer;
    }


}