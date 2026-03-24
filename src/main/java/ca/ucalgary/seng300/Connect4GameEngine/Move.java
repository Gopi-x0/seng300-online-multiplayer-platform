package ca.ucalgary.seng300.Connect4GameEngine;

/**
 * Move Class
 *
 * Represents a single player action in Connect 4.
 *
 * Connect 4 is beautifully simple from an input perspective:
 * the ONLY thing a player decides is which COLUMN to drop their piece into.
 * Gravity does the rest — the piece always falls to the lowest open row.
 *
 * So this class only needs one field: column (0-indexed, so 0–6 for a 7-column board).
 *
 * This is sent from the GUI → PlatformCore → GameSessionManager → GameEngine.
 * The GameEngine never needs to know WHO submitted it (that's tracked in Connect4State).
 */
public class Move {

    private final int column;  // Which column (0–6) the player is dropping into

    public Move(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Move{column=" + column + "}";
    }
}