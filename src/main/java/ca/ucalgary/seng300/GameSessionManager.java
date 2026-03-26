package ca.ucalgary.seng300;

import ca.ucalgary.seng300.interfaces.GameEngine;

import java.util.List;

public class GameSessionManager {
    static GameEngine engine; //String is placeholder for GameEngine class

    static GameState currentState;

    static List<String> players; //String is placeholder for PlayerProfiles class

    public static <Player> GameState startGame(String gameType, Player p1, Player p2){
        GameStatus status = GameStatus.SETUP;
        String player1Index = null; //p1.playerId; (get from p1)

        GameState gameState = new GameState();
        gameState.gameType = gameType;
        gameState.status = status;
        gameState.activePlayerIndex = player1Index;
        gameState.boardPayload = null; //placeholder


        return gameState;
    }

    public static <MoveResult> MoveResult submitMove(String playerID, Object movePayload){
        return null;
    }

    public static void switchActivePlayer() {
        //  make sure list exists and has 2 players
        if (players == null || players.size() < 2 || currentState == null) {
            return;
        }

        // get the IDs of two players who are playing
        String player1Id = players.get(0);
        String player2Id = players.get(1);

        // swap the active index in the GameState.
        if (currentState.activePlayerIndex.equals(player1Id)) {
            currentState.activePlayerIndex = player2Id;
        } else {
            currentState.activePlayerIndex = player1Id;
        }
    }

}
