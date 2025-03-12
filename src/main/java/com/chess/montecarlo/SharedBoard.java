package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;

/**
 * Singleton class to maintain a shared ChessBoard instance.
 */
public class SharedBoard {
    private static final ChessBoard board = new ChessBoard();

    private SharedBoard() {} // Prevent instantiation

    public static ChessBoard getBoard() {
        return board;
    }
}
