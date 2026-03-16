package ca.ucalgary.seng300;

/**
 * Enumeration of different Game Status
 * @author: Mohammed Syed - Platform Core
 * @date: 16/3/2026
 * @version: 1.0
 */
public enum GameStatus {
    SETUP,     // Game created but not started yet
    ONGOING,   // Players are taking turns
    WIN,       // A player has won
    DRAW       // No moves left and no winner
}


