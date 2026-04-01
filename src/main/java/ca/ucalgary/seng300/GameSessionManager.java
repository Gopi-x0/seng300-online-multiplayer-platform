package ca.ucalgary.seng300;

import ca.ucalgary.seng300.Connect4GameEngine.Connect4Game;
import ca.ucalgary.seng300.TicTacToeEngine.TicTacToeEngine;
import ca.ucalgary.seng300.interfaces.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameSessionManager {
    static GameEngine engine;

    static GameState currentState;

    static List<PlayerProfile> players = new ArrayList<>();
    /**
     *
     * @param gameType Connect-4 OR Tic-tac-toe
     * @param p1 Player 1
     * @param p2 Player 2
     * @return created game state
     */
    public static GameState startGame(String gameType, PlayerProfile p1, PlayerProfile p2){
        GameStatus status = GameStatus.SETUP;

        int player1Id = Integer.parseInt(p1.getPlayerId());
        players.add(p1);
        players.add(p2);
        currentState = new GameState(gameType, status, player1Id, null);
        if (Objects.equals(gameType, "connect4")){
            engine = new Connect4Game();
        } else if (Objects.equals(gameType,"tictactoe")) {
            engine = new TicTacToeEngine();
        }

        return currentState;
    }

    /**
     * Run to end game and return final results
     * @param gameState gameState
     */
    public static void endGame(GameState gameState){
        if (gameState.getGameStatus() == GameStatus.IN_PROGRESS){
            return; //should not occur
        }

        if (gameState.getGameStatus() == GameStatus.P1_WINS){
            return;
        } else if (gameState.getGameStatus() == GameStatus.P2_WINS){
            return;
        } else if (gameState.getGameStatus() == GameStatus.DRAW){
            return;
        }

    }

    public static Object submitMove(String playerID, Object movePayload) {
        // validating player
        if (currentState == null || !playerID.equals(currentState.getActivePlayerIndex())) {
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

        // 4. Update and Return
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
        int player1Id = Integer.parseInt(players.get(0).getPlayerId());
        int player2Id = Integer.parseInt(players.get(1).getPlayerId());

        // swap the active index in the GameState.
        if (currentState.getActivePlayerIndex() == player1Id) {
            currentState.setActivePlayerIndex(player2Id);
        } else {
            currentState.setActivePlayerIndex(player1Id);
        }
    }

    public static GameState getCurrentState() {
        return currentState;
    }

}
