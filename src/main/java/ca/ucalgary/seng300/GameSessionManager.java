package ca.ucalgary.seng300;

import ca.ucalgary.seng300.Connect4GameEngine.Move;
import ca.ucalgary.seng300.interfaces.GameEngine;
import ca.ucalgary.seng300.GameState;
import ca.ucalgary.seng300.Connect4GameEngine.Player;

import java.util.List;

public class GameSessionManager {
    static GameEngine engine;

    static GameState currentState;

    static List<String> players; //String is placeholder for PlayerProfiles class

    /**
     *
     * @param gameType Connect-4 OR Tic-tac-toe
     * @param p1 Player 1
     * @param p2 Player 2
     * @return created game state
     */
    public static <Player> GameState startGame(String gameType, Player p1, Player p2){
        GameStatus status = GameStatus.SETUP;
        String player1Id = null; //p1.playerId; (get from p1)

        GameState gameState = new GameState();
        gameState.gameType = gameType;
        gameState.status = status;
        gameState.activePlayerIndex = player1Id;
        gameState.boardPayload = null; //placeholder


        return gameState;
    }

    public static Object submitMove(String playerID, Object movePayload) {
        // check if player is valid
        if (currentState == null || !playerID.equals(currentState.activePlayerIndex)) {
            System.out.println("Wait your turn!");
            return null;
        }

        // create the Move
        ca.ucalgary.seng300.Connect4GameEngine.Move move;
        if (movePayload instanceof Integer) {
            move = new ca.ucalgary.seng300.Connect4GameEngine.Move((Integer) movePayload);
        } else {
            move = (ca.ucalgary.seng300.Connect4GameEngine.Move) movePayload;
        }


        ca.ucalgary.seng300.GameState nextState = (GameState) ((ca.ucalgary.seng300.Connect4GameEngine.Connect4Game) engine).applyMove(currentState, move);

        // update and return
        if (nextState != null) {
            currentState = nextState;
        }

        return currentState;
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
