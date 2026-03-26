package ca.ucalgary.seng300.Connect4GameEngine;

/**
 * Player Class
 *
 * Represents a player in the Connect 4 game.
 * Each player has:
 *   - an ID (their username/session ID from the platform, e.g. "alice")
 *   - a symbol (the character placed on the board: 'R' for Red, 'Y' for Yellow)
 *
 * The symbol is what actually gets stamped onto the Board's grid when a piece drops.
 * The ID is used to communicate back to the platform who did what.
 */
public class Player {

    private final String id;      // Platform-assigned player identifier
    private final char symbol;    // 'R' (Red/Player1) or 'Y' (Yellow/Player2)

    public Player(String id, char symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Player{id='" + id + "', symbol=" + symbol + "}";
    }
}