package ca.ucalgary.seng300.model;

/**
 * GameStatus Enum
 *
 * Represents the possible outcomes/states of a Connect 4 game.
 * An enum is perfect here because the status can only ever be
 * one of these four fixed values - nothing else makes sense.
 *
 * This gets stored inside Connect4State so the GameSessionManager
 * always knows whether the game is still going or who won.
 */
public enum GameStatus {
    SETUP,

    /** Game is ongoing - no winner yet, board isn't full */
    IN_PROGRESS,

    /** Player 1 has connected four in a row */
    P1_WINS,

    /** Player 2 has connected four in a row */
    P2_WINS,

    /** Board is completely full with no winner */
    DRAW
}