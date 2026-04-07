package ca.ucalgary.seng300;

// Represents the result of attempting a move in the game
public class MoveResult {
    
    // Variables for if the move was valid, the reason, and the new state of the game
    public final boolean success;
    public final String reason;
    public final GameState updatedState;

    // Creates the MoveResult object
    public MoveResult(boolean success, String reason, GameState updatedState) {
        this.success = success;
        this.reason = reason;
        this.updatedState = updatedState;
    }

    // Getters and setters for MoveResult
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
