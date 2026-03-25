package ca.ucalgary.seng300;

public class GameState {
    public String sessionId;
    private String gameType;
    private GameStatus gameStatus;
    private int activePlayerIndex;
    private Object boardPayLoad;

    public GameState(String sessionId, String gameType, GameStatus gameStatus, int activePlayerIndex, Object boardPayLoad) {
        this.sessionId = sessionId;
        this.gameType = gameType;
        this.gameStatus = gameStatus;
        this.activePlayerIndex = activePlayerIndex;
        this.boardPayLoad = boardPayLoad;
    }

    public String getSessionId() {
        return sessionId;
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
