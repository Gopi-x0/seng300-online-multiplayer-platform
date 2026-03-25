package ca.ucalgary.seng300.TicTacToeEngine;

/**
 * List of error types when an invalid move is made
 * Success status if a move is valid
 */

public enum ValidationCode {
    CELL_OCCUPIED,
    OUT_OF_BOUNDS,
    WRONG_TURN,
    GAME_ALREADY_OVER,
    SUCCESS;
}
