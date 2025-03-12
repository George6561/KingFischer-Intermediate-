package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;
import java.util.List;
import java.util.Random;

/**
 * Monte Carlo Move Generator.
 * Uses the shared ChessBoard instance for move simulations.
 */
public class MonteCarloMoves {

    private ChessBoard board;
    private Random random;
    private static final int MAX_PLY = 8; // 8 half-moves (4 full moves)
    private static final long TIME_LIMIT_MS = 5000; // 5 seconds

    /**
     * Constructor initializes with the shared board.
     */
    public MonteCarloMoves() {
        this.board = SharedBoard.getBoard(); // Use the shared board
        this.random = new Random();
    }

    /**
     * Runs Monte Carlo simulations and returns the best move.
     * @return The best move as [fromRow, fromCol, toRow, toCol].
     */
    public int[] getBestMonteCarloMove() {
        long startTime = System.currentTimeMillis();
        MonteCarloTree tree = new MonteCarloTree(board);

        while (System.currentTimeMillis() - startTime < TIME_LIMIT_MS) {
            tree.simulateGame(MAX_PLY);
        }

        return tree.getBestMove();
    }

    /**
     * Generates a random move for Black using Monte Carlo.
     * @return The move as [fromRow, fromCol, toRow, toCol], or null if no move is available.
     */
    public int[] getRandomMoveForBlack() {
        List<int[]> legalMoves = board.getAllLegalMoves(ChessBoard.Player.BLACK);
        if (legalMoves.isEmpty()) {
            return null; // No legal moves available
        }
        return legalMoves.get(random.nextInt(legalMoves.size())); // Pick a random move
    }
}
