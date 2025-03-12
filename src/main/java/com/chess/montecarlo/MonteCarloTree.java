package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Monte Carlo Tree for random game simulations with a 5-second time limit.
 */
public class MonteCarloTree {

    private final ChessBoard board;
    private final Random random;
    private final Map<String, Integer> moveScores; // Track scores per move
    private static final long TIME_LIMIT_MS = 5000; // 5 seconds

    /**
     * Constructor initializes the Monte Carlo Tree using a **copy** of the shared board.
     */
    public MonteCarloTree() {
        this.board = SharedBoard.getBoard().copy(); // ✅ Use board.copyBoard() for safe simulations
        this.random = new Random();
        this.moveScores = new HashMap<>();
    }

    /**
     * Simulates multiple random games within a time limit.
     */
    public void runSimulation() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIME_LIMIT_MS) {
            simulateSingleGame();
        }
    }

    /**
     * Simulates a single random game to a fixed depth.
     */
    private void simulateSingleGame() {
        ChessBoard simulationBoard = board.copy(); 
        ChessBoard.Player originalPlayer = simulationBoard.currentPlayer();
        List<int[]> legalMoves = simulationBoard.getAllLegalMoves(originalPlayer);

        if (legalMoves.isEmpty()) {
            return; // No moves possible
        }

        int[] firstMove = null;
        int evaluation = 0;

        for (int i = 0; i < 8; i++) {
            legalMoves = simulationBoard.getAllLegalMoves(simulationBoard.currentPlayer());
            if (legalMoves.isEmpty()) {
                break;
            }

            int[] chosenMove = legalMoves.get(random.nextInt(legalMoves.size()));
            if (i == 0) {
                firstMove = chosenMove;
            }

            simulationBoard.movePiece(chosenMove[0], chosenMove[1], chosenMove[2], chosenMove[3]);
            simulationBoard.nextMove();
            evaluation += MoveRating.evaluate(simulationBoard);
        }

        if (firstMove != null) {
            String moveKey = moveToString(firstMove);
            moveScores.put(moveKey, moveScores.getOrDefault(moveKey, 0) + evaluation);
        }
    }

    /**
     * Returns the best move based on the highest rating.
     *
     * @return The best move as [fromRow, fromCol, toRow, toCol].
     */
    public int[] getBestMove() {
        if (moveScores.isEmpty()) {
            // If Monte Carlo fails, pick a random move instead of returning -1s
            List<int[]> fallbackMoves = board.getAllLegalMoves(board.currentPlayer());
            if (!fallbackMoves.isEmpty()) {
                return fallbackMoves.get(random.nextInt(fallbackMoves.size()));  // Pick a random valid move
            }

            return new int[]{-1, -1, -1, -1}; // No valid move found
        }

        int[] scores = moveScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> stringToMove(entry.getKey()))
                .orElse(new int[]{-1, -1, -1, -1});
        return scores;
    }

    /**
     * Converts a move to a string format.
     */
    private String moveToString(int[] move) {
        return move[0] + "," + move[1] + "," + move[2] + "," + move[3];
    }

    /**
     * Converts a string back to a move array.
     */
    private int[] stringToMove(String moveStr) {
        String[] parts = moveStr.split(",");
        return new int[]{
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[3])
        };
    }
}
