package ca.ucalgary.seng300;

import java.util.List;

public class GameSessionManager {
    static String engine; //String is placeholder for GameEngine class

    static GameState currentState;

    static List<String> players; //String is placeholder for PlayerProfiles class

    public static <PlayerProfile> GameState startGame(String gameType, PlayerProfile p1, PlayerProfile p2){
        return null;
    }

    public static <MoveResult> MoveResult submitMove(String playerID, Object movePayload){
        return null;
    }

    public static void switchActivePlayer(){

    }

}
