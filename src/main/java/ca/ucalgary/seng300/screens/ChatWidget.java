package ca.ucalgary.seng300.screens;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Side-panel chat widget displayed alongside the game board during play.
 * Supports networked messaging via a send callback, system notifications,
 * and persists through win/lose/draw overlays.
 */
public class ChatWidget extends VBox {

    private static final int MAX_MESSAGE_LENGTH = 200;

    private final VBox messagesBox;
    private final ScrollPane scrollPane;
    private final String myUsername;
    private final Consumer<String> sendHandler;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
    private final TextField inputField;
    private boolean disabled = false;

    // Sets up the chat panel with header, scrollable message area, and input field
    public ChatWidget(String myUsername, Consumer<String> sendHandler) {
        this.myUsername = myUsername;
        this.sendHandler = sendHandler;

        setPrefWidth(280);
        setMinWidth(280);
        setMaxWidth(280);
        // Chat panel background styling
        setStyle(
                "-fx-background-color: linear-gradient(to bottom, #0a1628, #0f1d32);" +
                "-fx-border-color: rgba(0,200,255,0.15);" +
                "-fx-border-width: 0 0 0 1;"
        );

        Label title = new Label("Match Chat");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox header = new HBox(title);
        header.setPadding(new Insets(14, 14, 10, 14));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        messagesBox = new VBox(8);
        messagesBox.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Auto-scroll to bottom whenever new messages are added
        messagesBox.heightProperty().addListener((obs, old, val) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setStyle(
                "-fx-background-color: #020b1c;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #90a1b9;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #314158;" +
                "-fx-border-radius: 6;"
        );
        inputField.setPrefHeight(36);

        Label charCounter = new Label("0/" + MAX_MESSAGE_LENGTH);
        charCounter.setStyle("-fx-text-fill: #607a99; -fx-font-size: 10px;");

        // Enforce character limit and update the counter label
        inputField.textProperty().addListener((obs, old, val) -> {
            if (val.length() > MAX_MESSAGE_LENGTH) {
                inputField.setText(val.substring(0, MAX_MESSAGE_LENGTH));
            }
            charCounter.setText(inputField.getText().length() + "/" + MAX_MESSAGE_LENGTH);
        });

        inputField.setOnAction(e -> handleSend());

        HBox inputRow = new HBox(inputField);
        inputRow.setPadding(new Insets(8, 10, 10, 10));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        HBox counterRow = new HBox(charCounter);
        counterRow.setPadding(new Insets(0, 14, 4, 0));
        counterRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(header, scrollPane, counterRow, inputRow);

        addSystemMessage("Chat is now active. Say hello!");
    }

    // Sends the current input text and clears the field
    private void handleSend() {
        if (disabled) return;
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        addUserMessage(text);
        if (sendHandler != null) sendHandler.accept(text);
        inputField.clear();
    }

    // Called when a message arrives from the opponent over the network
    public void receiveMessage(String username, String text) {
        Platform.runLater(() -> addOpponentMessage(username, text));
    }

    // Displays a system notification message in the chat
    public void addSystemMessage(String text) {
        Platform.runLater(() -> addSystemMessageInternal(text));
    }

    // Disables the chat input and shows that the opponent left
    public void notifyOpponentLeft() {
        Platform.runLater(() -> {
            addSystemMessageInternal("Opponent has left the chat.");
            disabled = true;
            inputField.setDisable(true);
            inputField.setPromptText("Chat ended");
        });
    }

    // Creates a right-aligned message bubble for the current user
    private void addUserMessage(String text) {
        String time = LocalTime.now().format(timeFormatter);

        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setMaxWidth(200);
        msg.setStyle(
                "-fx-background-color: #00c8ff;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8 10 8 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13px;"
        );

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #90a1b9; -fx-font-size: 9px;");

        VBox messageBlock = new VBox(msg, timeLabel);
        messageBlock.setSpacing(2);
        messageBlock.setAlignment(Pos.CENTER_RIGHT);

        HBox container = new HBox(messageBlock);
        container.setAlignment(Pos.CENTER_RIGHT);
        container.setPadding(new Insets(1, 5, 1, 30));

        messagesBox.getChildren().add(container);
    }

    // Creates a left-aligned message bubble for the opponent
    private void addOpponentMessage(String username, String text) {
        String time = LocalTime.now().format(timeFormatter);

        Label name = new Label(username);
        name.setStyle("-fx-text-fill: #00e676; -fx-font-size: 10px;");

        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setMaxWidth(200);
        msg.setStyle(
                "-fx-background-color: #1a2a40;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8 10 8 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13px;"
        );

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: #90a1b9; -fx-font-size: 9px;");

        VBox messageBlock = new VBox(name, msg, timeLabel);
        messageBlock.setSpacing(2);
        messageBlock.setAlignment(Pos.CENTER_LEFT);

        HBox container = new HBox(messageBlock);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(1, 30, 1, 5));

        messagesBox.getChildren().add(container);
    }

    // Adds a centered italic system message to the chat area
    private void addSystemMessageInternal(String text) {
        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setMaxWidth(230);
        msg.setStyle(
                "-fx-text-fill: #89a4c6;" +
                "-fx-font-size: 11px;" +
                "-fx-font-style: italic;" +
                "-fx-padding: 4 8 4 8;" +
                "-fx-background-color: rgba(0,200,255,0.06);" +
                "-fx-background-radius: 6;"
        );
        HBox container = new HBox(msg);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(2, 5, 2, 5));
        messagesBox.getChildren().add(container);
    }
}
