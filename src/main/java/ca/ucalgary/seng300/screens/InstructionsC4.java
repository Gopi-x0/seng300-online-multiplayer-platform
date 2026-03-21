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

        VBox titleBox = new VBox(title, subTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(4);

        VBox rulesCard = createCard("Rules",
                "\u2022  Each player takes turns dropping one disc at a time into the grid.",
                "\u2022  Discs fall to the lowest available position in the chosen column.",
                "\u2022  You may only place a disc in a column that is not full."
        );

        VBox objectiveCard = createCard("Objective",
                "\u2022 Be the first to connect FOUR of your tiles in a row.",
                "\u2022 Connections can be horizontal, vertical, or diagonal.",
                "\u2022 If there are no four consecutive connections by either sides, then the game ends in a draw."
        );

        VBox playCard = createCard("Play",
                "\u2022 1. Click on a column to drop your tile.",
                "\u2022 2. Your disc will fall to the lowest open spot.",
                "\u2022 3. Alternate turns with your opponent."
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

        HBox buttonBox = new HBox(playButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(); //Initialize a VBox
        root.setSpacing(10); // Set the spacing of the root
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #020b1c;");
        root.getChildren().addAll(titleBox, rulesCard, objectiveCard, playCard, buttonBox);

        Scene c4 = new Scene(root ,1000 , 1000);

        stage.setTitle("Connect 4 : Instructions & Rules");
        stage.setScene(c4);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private VBox createCard(String title, String... lines){
        Label mainTitle = new Label(title);
        mainTitle.setStyle("-fx-text-fill: #00d3f3; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: linear-gradient(#0f172b, #1d293d);" + "-fx-background-radius: 10;");
        card.getChildren().add(mainTitle);

        for (String line : title.split("\n")) {
            Label subtext = new Label(line);
            subtext.setStyle("-fx-text-fill: #c8d6e5; -fx-font-size: 14px;");
            subtext.setWrapText(true);
            card.getChildren().add(subtext);
        }

        return card;
    }
}
