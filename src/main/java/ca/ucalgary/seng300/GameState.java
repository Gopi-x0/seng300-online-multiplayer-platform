package ca.ucalgary.seng300;


public class GameState {

    // Variables for the type of game, the status of it, the current player, and the board state
    private String gameType;
    private GameStatus gameStatus;
    private int activePlayerIndex;
    private Object boardPayLoad;

    // GameState object
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

    public void setBoardPayLoad(Object boardPayLoad) {
        this.boardPayLoad = boardPayLoad;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setActivePlayerIndex(int activePlayerIndex) {
        this.activePlayerIndex = activePlayerIndex;
    }
}
