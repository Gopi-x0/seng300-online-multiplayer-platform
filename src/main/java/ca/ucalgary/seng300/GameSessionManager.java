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

    public static void switchActivePlayer(){

    }

}
