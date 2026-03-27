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

}
