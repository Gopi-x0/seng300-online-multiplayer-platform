package ca.ucalgary.seng300.connect4engine;

import ca.ucalgary.seng300.model.GameStatus;

/**
 * Connect4State Class
 *
 * This is the SNAPSHOT of the entire game at any given moment.
 * Think of it like a save file - it captures everything needed
 * to fully describe where the game is right now:
 *
 *   - board:         the current grid (who's piece is where)
 *   - player1:       always the 'R' (Red) player - moves first
 *   - player2:       always the 'Y' (Yellow) player - moves second
 *   - currentPlayer: whose turn it is RIGHT NOW
 *   - status:        is the game in progress, won, or drawn?
 *   - winner:        if someone won, who was it? (null if still in progress or draw)
 *
 * This is what gets passed between GameEngine methods, and what gets
 * sent back to the GameSessionManager so it can publish a GameStateUpdate
 * to the Event Bus (and ultimately to the GUI).
 *
 * IMMUTABILITY NOTE:
 * We don't mutate this object. Instead, applyMove creates a BRAND NEW
 * Connect4State each time. This keeps game history clean and bug-free.
 *
 * WHY STORE BOTH PLAYERS?
 * Storing both player1 and player2 here means Connect4Game can cleanly
 * swap turns without needing to reconstruct or guess who the other player is.
 */
public class Connect4State {

    private final Board board;
    private final Player player1;        // Red ('R') - always moves first
    private final Player player2;        // Yellow ('Y') - always moves second
    private final Player currentPlayer;  // Who moves NEXT
    private final GameStatus status;
    private final Player winner;         // null unless status is P1_WINS or P2_WINS

    public Connect4State(Board board,
                         Player player1,
                         Player player2,
                         Player currentPlayer,
                         GameStatus status,
                         Player winner) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.status = status;
        this.winner = winner;
    }

    public Board getBoard()           { return board; }
    public Player getPlayer1()        { return player1; }
    public Player getPlayer2()        { return player2; }
    public Player getCurrentPlayer()  { return currentPlayer; }
    public GameStatus getStatus()     { return status; }
    public Player getWinner()         { return winner; }

    /**
     * Convenience method: given one player, return the other.
     * Used by Connect4Game to swap turns cleanly after each valid move.
     */
    public Player getOtherPlayer(Player p) {
        return p.getId().equals(player1.getId()) ? player2 : player1;
    }

    @Override
    public String toString() {
        return "Connect4State{\n" +
                board.toString() +
                "currentPlayer=" + currentPlayer + "\n" +
                "status=" + status + "\n" +
                "winner=" + (winner != null ? winner.getId() : "none") + "\n" +
                "}";
    }
}
