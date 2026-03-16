package ca.ucalgary.seng300;

public class GameState {
    private String gameType;
    private GameStatus gameStatus;
    private int activePlayerIndex;
    private Object boardPayLoad;

    public GameState(String gameType, GameStatus gameStatus, int activePlayerIndex, Object boardPayLoad) {
        this.gameType = gameType;
        this.gameStatus = gameStatus;
        this.activePlayerIndex = activePlayerIndex;
        this.boardPayLoad = boardPayLoad;
    }

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
