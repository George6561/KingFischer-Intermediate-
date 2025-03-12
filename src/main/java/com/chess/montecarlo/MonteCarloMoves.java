package com.chess.montecarlo;

import com.chess.stockfish.ChessBoard;

/**
 * Monte Carlo Move Generator.
 * Uses Monte Carlo Tree Search to select the best move within 5 seconds.
 */
public class MonteCarloMoves {

    private final ChessBoard chessBoard;

    /**
     * Constructor for MonteCarloMoves.
     * Now always uses the shared ChessBoard.
     */
    public MonteCarloMoves() {
        this.chessBoard = SharedBoard.getBoard(); // Always use the shared board
    }

    /**
     * Runs Monte Carlo Tree Search and returns the best move.
     * @return The best move as [fromRow, fromCol, toRow, toCol].
     */
    public int[] getBestMonteCarloMove() {
        MonteCarloTree tree = new MonteCarloTree(); // No parameters
        tree.runSimulation(); // Runs for 5 seconds
        return tree.getBestMove();
    }

    /**
     * Gets a move specifically for Black using Monte Carlo Tree Search.
     * @return The best move for Black in [fromRow, fromCol, toRow, toCol] format.
     */
    public int[] getRandomMoveForBlack() {
        if (chessBoard.currentPlayer() == ChessBoard.Player.BLACK) {
            return getBestMonteCarloMove(); // Uses MCTS instead of random moves
        } else {
            System.out.println("It is not black's turn");
        }
        return null; // It's not Black's turn, return nothing
    }
}
