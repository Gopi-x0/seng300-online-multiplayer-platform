package ca.ucalgary.seng300;

/**
 * This interface defines the core contract for the two games running on the platform
 * This is for the R&V team to write the classes and implement it
 */

public interface GameEngine {

    /**
     * Generates the setup phase for the match
     * * @return A fresh GameState object containing the empty board setup
     */

    GameState createInitialState();

    /**
     * Checks if a requested move is valid based on the current board state
     * * @param state The current state of the game
     * @param movePayload The move data based on either of the two games
     * @return true if the move is valid, false if it is invalid
     */

    boolean validateMove(GameState state, Object movePayload);

    /**
     * Modifies the board by officially placing the player's piece if the move is valid
     * * @param state The current state of the game
     * @param movePayload The move data based on either of the two games
     * @return The newly updated GameState reflecting the applied move
     */

    GameState applyMove(GameState state, Object movePayload);

    /**
     * Checks to see fi the match is concluded
     * * @param state The updated state of the game
     * @return The new status of the game (Ongoing, Win, or Draw)
     */

    GameStatus evaluateEnd(GameState state);


}