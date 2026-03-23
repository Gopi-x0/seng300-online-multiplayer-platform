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
    public void start(Stage stage) {
        //Title Label
        Label title = new Label("Tic-Tac-Toe");
        title.setStyle("-fx-text-fill : white; -fx-font-size: 48px; -fx-font-weight: bold;");

        Label subTitle = new Label("Instructions & Rules");
        subTitle.setStyle("-fx-text-fill : white; -fx-font-size: 32px;");

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(4);

        VBox rulesCard = createCard("Rules",
                "•  Each player takes turns filling in a tile at a time on the grid.",
                "•  You may place your corresponding symbol anywhere on the grid, as long as it is not occupied.",
                "•  The symbols are 'X' and 'O'."
        );

        VBox objectiveCard = createCard("Objective",
                "• Be the first to connect THREE of your symbols in a row.",
                "• Connections can be horizontal, vertical, or diagonal.",
                "• If there are no three consecutive connections at all, then the game ends in a draw."
        );

        VBox playCard = createCard("Play",
                " 1. Click on a tile to place your symbol.",
                " 2. Your symbol will then be placed on that spot.",
                " 3. Alternate turns with your opponent."
        );

        Button playButton = new Button("PLAY");
        playButton.setStyle("-fx-background-color: linear-gradient(#00d3f3, #0088a3);"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12 40;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-border-width: 2;");

        playButton.setOnMouseEntered(e -> playButton.setStyle(
                "-fx-background-color: linear-gradient(#33e0f7, #00a8c9);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 40;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
        playButton.setOnMouseExited(e -> playButton.setStyle(
                "-fx-background-color: linear-gradient(#00d3f3, #0088a3);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 40;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));


    }

    private VBox createCard(String title, String... lines){
        Label mainTitle = new Label(title);
        mainTitle.setStyle("-fx-text-fill: #00d3f3; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: linear-gradient(#0f172b, #1d293d);" + "-fx-background-radius: 10;");
        card.getChildren().add(mainTitle);

        for (String line : lines) {
            Label subtext = new Label(line);
            subtext.setStyle("-fx-text-fill: #c8d6e5; -fx-font-size: 14px;");
            subtext.setWrapText(true);
            card.getChildren().add(subtext);
        }

        return card;
    }


}









}
