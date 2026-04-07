package ca.ucalgary.seng300.model;

// Represents the current state of a game including type, status, active player, and board
public class GameState {

    // Variables for the type of game, the status of it, the current player, and the board state
    private String gameType;
    private GameStatus gameStatus;
    private int activePlayerIndex;
    private Object boardPayLoad;

    // Constructor to set up the game state with given values
    public GameState(String gameType, GameStatus gameStatus, int activePlayerIndex, Object boardPayLoad) {
        this.gameType = gameType;
        this.gameStatus = gameStatus;
        this.activePlayerIndex = activePlayerIndex;
        this.boardPayLoad = boardPayLoad;
    }


    // Getters and setters
    public String getGameType() {
        return gameType;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public Object getBoardPayLoad() {
        return boardPayLoad;
    }
}
