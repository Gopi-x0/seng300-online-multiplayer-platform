package ca.ucalgary.seng300;
public class MoveResult {
    
    public final boolean success;
    public final String reason;
    public final GameState updatedState;

    public MoveResult(boolean success, String reason, GameState updatedState) {
        this.success = success;
        this.reason = reason;
        this.updatedState = updatedState;
    }

    public static MoveResult ok(GameState updatedState) {
        return new MoveResult(true, "OK", updatedState);
    }

    public static MoveResult fail(String reason, GameState currentState) {
        return new MoveResult(false, reason, currentState);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }

    public GameState getUpdatedState() {
        return updatedState;
    }

}
