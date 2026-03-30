package ca.ucalgary.seng300.interfaces;


/**
 * Value Object returned after a move attempt.
 * Acts as the communication channel between the engine and the caller —
 * rather than a simple boolean, it provides a specific ValidationCode
 * explaining exactly why a move succeeded or failed.
 */


public class MoveResult {

    private final boolean isValid;
    private final String message;
    private final ValidationCode errorCode;


//Made it a private constructor to restrict class instatiation outside of this class
    private MoveResult(boolean isValid, String message, ValidationCode errorCode){
        this.isValid = isValid;
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     *Factory method to create an instance of the 'success' move result
     */
    public static MoveResult success(){

        return new MoveResult(true, "Move is valid", ValidationCode.SUCCESS);
    }

    /**
     * Factory method to create an instance of a 'failed' move result
     */
    public static MoveResult failure(ValidationCode errorCode, String message){
        return new MoveResult(false, message, errorCode);
    }

    //Getters for error Codes, game session manager will need to call on these
    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    public ValidationCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("MoveResult[isValid=%b, errorCode=%s, message='%s']",
                isValid, errorCode, message);
    }















}
