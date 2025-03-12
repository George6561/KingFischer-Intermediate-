package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;
import java.util.List;
import java.util.Random;

/**
 * A simple move generator that selects a random legal move for Black.
 */
public class MonteCarloMoves {

    private final ChessBoard chessBoard;
    private final Random random;

    /**
     * Constructor initializes the move generator with a given chessboard.
     *
     * @param chessBoard The chessboard object that maintains the game state.
     */
    public MonteCarloMoves(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.random = new Random();
    }

    /**
     * Retrieves a random legal move for Black.
     *
     * @return A string representing the randomly selected move in algebraic notation (e.g., "e2e4").
     */
    public String getRandomMoveForBlack() {
        List<int[]> legalMoves = chessBoard.getAllLegalMoves(ChessBoard.Player.BLACK);

        if (legalMoves.isEmpty()) {
            return "0000"; // No legal moves (checkmate or stalemate)
        }

        int[] randomMove = legalMoves.get(random.nextInt(legalMoves.size()));

        return convertMoveToAlgebraic(randomMove);
    }

    /**
     * Converts a move from [fromRow, fromCol, toRow, toCol] to algebraic notation.
     *
     * @param move The move as an integer array.
     * @return The move in algebraic notation (e.g., "e2e4").
     */
    private String convertMoveToAlgebraic(int[] move) {
        char fromFile = (char) ('a' + move[1]);
        char fromRank = (char) ('8' - move[0]);
        char toFile = (char) ('a' + move[3]);
        char toRank = (char) ('8' - move[2]);

        return "" + fromFile + fromRank + toFile + toRank;
    }
}
