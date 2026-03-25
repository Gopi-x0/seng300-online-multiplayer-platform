package ca.ucalgary.seng300.TicTacToeEngine;
/**
 * Value Object
 * Tracks the move attempt that was made by a player
 * Contains all the necessary data to instruct game engine to update board status including:
 * playerID, player's symbol(x,o), coordinates of the symbol being attempted to be placed
 */

public class Move {

    private final int row;
    private final int col;
    private final String playerId;
    private final Symbol symbol;


    public Move(int row, int col, String playerID, Symbol symbol){
        this.row = row;
        this.col = col;
        this.playerId = playerID;
        this.symbol = symbol;
    }

    //Make getters for Move:
    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    //TODO: Create method for playerID
    public String getPlayerId(){
        return playerId;
    }

    public Symbol getSymbol(){
        return symbol;
    }

    @Override
    public String toString() {
        return String.format("Move[player=%s, symbol=%s, row=%d, col=%d]",
                playerId, symbol, row, col);
    }

}
