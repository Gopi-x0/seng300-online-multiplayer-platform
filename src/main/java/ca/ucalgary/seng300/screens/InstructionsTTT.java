package ca.ucalgary.seng300.screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructionsTTT extends Application {

    @Override
    public void start(Stage stage){
        //Title Label
        Label title = new Label("Tic-Tac-Toe");
        title.setStyle("-fx-text-fill : white; -fx-font-size: 48px; -fx-font-weight: bold;");

        Label subTitle = new Label("Instructions & Rules");
        subTitle.setStyle("-fx-text-fill : white; -fx-font-size: 32px;");

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(4);
    }









}
