package ca.ucalgary.seng300.connect4engine;

/**
 * Board Class
 *
 * This is the actual 6x7 grid that pieces get dropped into.
 * It handles two key responsibilities:
 *   1. GRAVITY - when a piece is dropped into a column, it falls to the lowest empty row
 *   2. WIN DETECTION - after each piece is placed, check if that piece created a 4-in-a-row
 *
 * The grid is stored as char[6][7]:
 *   - 6 rows (row 0 = top, row 5 = bottom)
 *   - 7 columns (column 0 = leftmost, column 6 = rightmost)
 *   - '.' means empty
 *   - 'R' means Player 1's piece
 *   - 'Y' means Player 2's piece
 *
 * NOTE: Board is mutable. Connect4State holds a reference to a Board.
 * When applyMove is called in Connect4Game, we deep-copy the board first
 * so we never mutate the previous state.
 */
public class Board {

    public static final int ROWS = 6;
    public static final int COLS = 7;
    private static final char EMPTY = '.';

    private final char[][] grid;

    /**
     * Creates a fresh empty board.
     */
    public Board() {
        grid = new char[ROWS][COLS];
        // Fill every cell with the EMPTY marker
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = EMPTY;
            }
        }
    }

    /**
     * Copy constructor - creates a deep copy of an existing board.
     * This is essential so applyMove can return a NEW state without
     * modifying the old one. Immutability is a key design principle here.
     */
    public Board(Board other) {
        grid = new char[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            // System.arraycopy is efficient for copying arrays
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, COLS);
        }
    }

    /**
     * Drops a player's piece into a column.
     *
     * GRAVITY LOGIC: We scan from the BOTTOM row upward and place the piece
     * in the first empty cell we find. This simulates the piece falling down.
     *
     * Returns the row where the piece landed (needed for win detection).
     * Returns -1 if the column is full (shouldn't happen if validateMove was called first).
     *
     * @param column - which column (0-6) to drop into
     * @param player - the player dropping the piece
     * @return the row index where the piece landed, or -1 if column is full
     */
    public int dropPiece(int column, Player player) {
        // Start from the bottom row (row 5) and scan upward
        for (int row = ROWS - 1; row >= 0; row--) {
            if (grid[row][column] == EMPTY) {
                grid[row][column] = player.getSymbol();
                return row;  // Return the row where piece landed
            }
        }
        return -1; // Column is full - shouldn't reach here if validation was done
    }

    /**
     * Checks whether a specific column is completely full.
     * A column is full when the TOP cell (row 0) is not empty.
     *
     * @param column - which column to check
     * @return true if the column is full (no more pieces can be dropped here)
     */
    public boolean isColumnFull(int column) {
        return grid[0][column] != EMPTY;
    }

    /**
     * Checks whether the ENTIRE board is full (no empty cells anywhere).
     * Used to detect a DRAW - if the board is full and no winner, it's a draw.
     *
     * @return true if every cell is occupied
     */
    public boolean isFull() {
        // If top row has any empty cell, board is not full
        for (int c = 0; c < COLS; c++) {
            if (grid[0][c] == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the piece at (row, col) has created a 4-in-a-row WIN.
     *
     * We check all 4 directions FROM the newly placed piece:
     *   1. Horizontal (same row, varying column)
     *   2. Vertical (same column, varying row)
     *   3. Diagonal down-right (row+, col+)
     *   4. Diagonal up-right (row-, col+)
     *
     * For each direction, we count how many consecutive same-symbol pieces
     * exist on BOTH SIDES of the just-placed piece. If total >= 4, it's a win.
     *
     * @param row    - row where the piece was just placed
     * @param column - column where the piece was just placed
     * @return true if this placement results in a 4-in-a-row
     */
    public boolean checkWin(int row, int column) {
        char symbol = grid[row][column];
        if (symbol == EMPTY) return false;

        // Check all 4 directions
        return countInDirection(row, column, symbol, 0, 1)  +  // Horizontal
                countInDirection(row, column, symbol, 0, -1) >= 3
                || countInDirection(row, column, symbol, 1, 0)  >= 3  // Vertical (only down needed)
                || countInDirection(row, column, symbol, 1, 1)  +  // Diagonal down-right
                countInDirection(row, column, symbol, -1, -1) >= 3
                || countInDirection(row, column, symbol, 1, -1) +  // Diagonal up-right
                countInDirection(row, column, symbol, -1, 1) >= 3;
    }

    /**
     * Helper: counts consecutive matching symbols in one direction.
     *
     * Starting from (row, col), walks in the direction (rowDir, colDir)
     * and counts how many consecutive cells have the same symbol.
     * We count UP TO 3 (since the placed piece itself is the 4th).
     *
     * @param row    - starting row
     * @param col    - starting column
     * @param symbol - the symbol we're looking to match
     * @param rowDir - row direction (-1, 0, or 1)
     * @param colDir - column direction (-1, 0, or 1)
     * @return number of consecutive matching pieces in that direction (not counting origin)
     */
    private int countInDirection(int row, int col, char symbol, int rowDir, int colDir) {
        int count = 0;
        int r = row + rowDir;
        int c = col + colDir;

        while (r >= 0 && r < ROWS && c >= 0 && c < COLS && grid[r][c] == symbol) {
            count++;
            r += rowDir;
            c += colDir;
        }
        return count;
    }

    /**
     * Returns a copy of the grid (used by Connect4State for display/serialization).
     */
    public char[][] getGrid() {
        char[][] copy = new char[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(grid[r], 0, copy[r], 0, COLS);
        }
        return copy;
    }

    /**
     * Human-readable board display - useful for debugging.
     * Prints the board top-to-bottom with column numbers.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("0 1 2 3 4 5 6\n");
        sb.append("-------------\n");
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                sb.append(grid[r][c]);
                if (c < COLS - 1) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}