package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Monte Carlo Tree for random game simulations.
 */
public class MonteCarloTree {

    private ChessBoard board;
    private Random random;
    private Map<String, Integer> moveScores; // Track scores per move

    /**
     * Constructor initializes the Monte Carlo Tree.
     *
     * @param board The chessboard object.
     */
    public MonteCarloTree(ChessBoard board) {
        this.board = board; // Use the shared board, not a copy
        this.random = new Random();
        this.moveScores = new HashMap<>();
    }

    /**
     * Simulates a random game for a given ply depth.
     *
     * @param maxPly The maximum depth to simulate.
     */
    public void simulateGame(int maxPly) {
        // Instead of cloning, just store a reference to the shared board
        ChessBoard.Player originalPlayer = board.currentPlayer();

        List<int[]> legalMoves = board.getAllLegalMoves(originalPlayer);
        if (legalMoves.isEmpty()) {
            return; // No moves possible
        }

        int[] firstMove = null;
        int evaluation = 0;

        for (int i = 0; i < maxPly; i++) {
            legalMoves = board.getAllLegalMoves(board.currentPlayer());
            if (legalMoves.isEmpty()) {
                break;
            }

            int[] chosenMove = legalMoves.get(random.nextInt(legalMoves.size()));
            if (i == 0) {
                firstMove = chosenMove; // Track first move
            }

            board.movePiece(chosenMove[0], chosenMove[1], chosenMove[2], chosenMove[3]);
            board.nextMove(); // Switch turn

            evaluation += MoveRating.evaluate(board);
        }

        if (firstMove != null) {
            String moveKey = moveToString(firstMove);
            moveScores.put(moveKey, moveScores.getOrDefault(moveKey, 0) + evaluation);
        }
    }

    /**
     * Returns a random legal move for Black.
     *
     * @return A random move for Black as [fromRow, fromCol, toRow, toCol].
     */
    public int[] getRandomMoveForBlack() {
        if (board.currentPlayer() != ChessBoard.Player.BLACK) {
            return null; // It's not Black's turn
        }

        List<int[]> legalMoves = board.getAllLegalMoves(ChessBoard.Player.BLACK);
        if (legalMoves.isEmpty()) {
            return null; // No moves available
        }

        return legalMoves.get(random.nextInt(legalMoves.size())); // Pick a random move
    }

    /**
     * Restores the board to its original state.
     *
     * @param originalBoard The saved board state.
     * @param originalPlayer The saved player turn.
     */
    private void restoreBoard(int[][] originalBoard, ChessBoard.Player originalPlayer) {
        for (int row = 0; row < 8; row++) {
            System.arraycopy(originalBoard[row], 0, board.getBoard()[row], 0, 8);
        }
        while (board.currentPlayer() != originalPlayer) {
            board.nextMove();
        }
    }

    /**
     * Returns the best move based on the highest rating.
     *
     * @return The best move as [fromRow, fromCol, toRow, toCol].
     */
    public int[] getBestMove() {
        return moveScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> stringToMove(entry.getKey()))
                .orElse(new int[]{-1, -1, -1, -1}); // No valid move found
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
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]), Integer.parseInt(parts[3])};
    }
}
