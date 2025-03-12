package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;

/**
 * MoveRating class that evaluates chess positions using heuristics similar to Stockfish.
 */
public class MoveRating {
    
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;
    
    // Piece-Square Tables (simplified, mimicking Stockfish evaluation)
    private static final int[][] PAWN_TABLE = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {5, 5, 10, 25, 25, 10, 5, 5},
        {0, 0, 0, 20, 20, 0, 0, 0},
        {5, -5, -10, 0, 0, -10, -5, 5},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    // Constructor not needed anymore as evaluation is static
    private MoveRating() {}

    /**
     * Evaluates the chessboard position heuristically.
     * @param board The chessboard state to evaluate.
     * @return A score where positive is better for White, negative for Black.
     */
    public static int evaluate(ChessBoard board) {
        int score = 0;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board.getPieceAt(row, col);
                score += evaluatePiece(piece, row, col);
            }
        }
        
        return score;
    }

    /**
     * Evaluates a single piece on the board.
     * @param piece The piece identifier (+ for White, - for Black).
     * @param row The row index.
     * @param col The column index.
     * @return The evaluation score for that piece.
     */
    private static int evaluatePiece(int piece, int row, int col) {
        if (piece == 0) return 0;
        
        int value = 0;
        boolean isWhite = piece > 0;
        int pieceType = Math.abs(piece);
        
        switch (pieceType) {
            case 1 -> value = PAWN_VALUE + PAWN_TABLE[isWhite ? row : 7 - row][col];
            case 2 -> value = ROOK_VALUE;
            case 3 -> value = KNIGHT_VALUE;
            case 4 -> value = BISHOP_VALUE;
            case 5 -> value = QUEEN_VALUE;
            case 6 -> value = KING_VALUE;
        }
        
        return isWhite ? value : -value;
    }
}
