package ca.ucalgary.seng300;

import com.mservicetech.business.validation.ValidationCode;

/**
 * Value Object returned after a move attempt.
 * Acts as the communication channel between the engine and the caller —
 * rather than a simple boolean, it provides a specific ValidationCode
 * explaining exactly why a move succeeded or failed.
 * The UI team reads getMessage() and getErrorCode() to display feedback.
 */
public class MoveResult {

    private final boolean isValid;
    private final String message;
    private final ValidationCode errorCode;

    private MoveResult(boolean isValid, String message, ValidationCode errorCode) {
        this.isValid = isValid;
        this.message = message;
        this.errorCode = errorCode;
    }

    /** Factory method for a successful move. */
    public static MoveResult success() {
        return new MoveResult(true, "Move accepted.", ValidationCode.SUCCESS);
    }

    /**
     * Factory method for a failed move with a custom message.
     * Use when you need a specific context-aware message.
     */
    public static MoveResult failure(ValidationCode errorCode, String message) {
        return new MoveResult(false, message, errorCode);
    }

    /**
     * Factory method for a failed move with an auto-generated UI-ready message.
     * Preferred — ensures consistent messages are sent to the UI team.
     */
    public static MoveResult failure(ValidationCode errorCode) {
        String message = switch (errorCode) {
            case CELL_OCCUPIED     -> "That cell is already taken. Choose an empty cell.";
            case OUT_OF_BOUNDS     -> "That position is outside the board. Try again.";
            case WRONG_TURN        -> "It's not your turn. Please wait.";
            case GAME_ALREADY_OVER -> "The game has already ended. No more moves allowed.";
            default                -> "Invalid move. Please try again.";
        };
        return new MoveResult(false, message, errorCode);
    }

    public boolean isValid()              { return isValid; }
    public String getMessage()            { return message; }
    public ValidationCode getErrorCode()  { return errorCode; }

    @Override
    public String toString() {
        return String.format("MoveResult[isValid=%b, errorCode=%s, message='%s']",
                isValid, errorCode, message);
    }
}