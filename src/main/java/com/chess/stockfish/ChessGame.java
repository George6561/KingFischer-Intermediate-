package com.chess.stockfish;

import com.chess.montecarlo.MonteCarloMoves;
import com.chess.montecarlo.SharedBoard;
import com.chess.window.ChessWindow;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ChessGame class where Stockfish plays White and MonteCarloMoves plays Black.
 */
public class ChessGame {

    private ChessWindow chessWindow;        // UI component for the chessboard
    private StockfishConnector stockfish;   // Stockfish AI for White
    private MonteCarloMoves monteCarlo;     // Monte Carlo for Black
    private List<String> rawMoves;          // Move history
    private boolean isWhiteToMove = true;   // Track turns

    /**
     * Constructor initializes the game with the ChessWindow.
     *
     * @param chessWindow The UI representation of the chessboard.
     */
    public ChessGame(ChessWindow chessWindow) {
        this.chessWindow = chessWindow;
        this.stockfish = new StockfishConnector();
        this.monteCarlo = new MonteCarloMoves(); // Use the shared board inside Monte Carlo
        this.rawMoves = new ArrayList<>();
    }

    /**
     * Starts the chess game loop where White uses Stockfish and Black plays
     * Monte Carlo moves.
     *
     * @throws IOException If Stockfish communication fails.
     * @throws InterruptedException If UI updates are interrupted.
     */
    public void startOneGame() throws IOException, InterruptedException {
        if (stockfish.startEngine()) {
            try {
                initializeStockfish();
                displayInitialBoard();
                playOneGame();
            } finally {
                stockfish.stopEngine();
            }
        } else {
            System.out.println("Failed to start Stockfish engine.");
        }
    }

    public void startMultipleGames() throws IOException, InterruptedException {
        if (stockfish.startEngine()) {
            try {
                initializeStockfish();
                displayInitialBoard();
                playMultipleGames();
            } finally {
                stockfish.stopEngine();
            }
        } else {
            System.out.println("Failed to start Stockfish engine.");
        }
    }

    /**
     * Initializes Stockfish for the game.
     *
     * @throws IOException If an error occurs during Stockfish communication.
     */
    private void initializeStockfish() throws IOException {
        stockfish.sendCommand("uci");
        stockfish.getResponse();
        stockfish.sendCommand("isready");
        stockfish.getResponse();
        stockfish.sendCommand("position startpos");
    }

    /**
     * Displays the initial board state.
     */
    private void displayInitialBoard() {
        Platform.runLater(() -> {
            try {
                chessWindow.displayChessPieces(-1, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Main game loop where Stockfish plays White and MonteCarloMoves plays Black.
     */
    private void playOneGame() throws IOException, InterruptedException {
        while (true) {
            String move;

            if (isWhiteToMove) {
                move = makeStockfishMove();  // White (Stockfish)
            } else {
                move = makeMonteCarloMoveForBlack();  // Black (MonteCarlo)
            }

            if (move == null || move.equals("0000")) {
                System.out.println("Game over detected. No legal moves available.");
                break;
            }

            // Apply the move and update UI before switching turns
            updateMoveHistory(move);
            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    chessWindow.movePiece(move);
                    chessWindow.displayChessPieces(-1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            latch.await(); // Ensure UI update is complete before continuing

            // Print the board to verify shared state
            System.out.println("Updated Board:");
            SharedBoard.getBoard().printBoardWithIndices();

            // Check for checkmate after move execution
            if (SharedBoard.getBoard().isCheckmate(SharedBoard.getBoard().currentPlayer())) {
                System.out.println("Checkmate detected! Resetting board and starting a new game...");

                Platform.runLater(() -> {
                    SharedBoard.getBoard().resetBoard();
                    rawMoves.clear();
                });

                isWhiteToMove = true;  // Ensure White always starts
                Thread.sleep(2000);
                playOneGame();  // Restart the game loop
                return;
            }

            Thread.sleep(500);
            isWhiteToMove = !isWhiteToMove;  // Flip turn after the move is processed
        }
    }

    /**
     * Keeps playing multiple games in a loop until manually stopped.
     */
    public void playMultipleGames() throws IOException, InterruptedException {
        System.out.println("CALLING PLAY MULTIPLE GAMES FUNCTIONS");

        while (true) {
            System.out.println("Starting a new game...");

            // Reset the board properly before playing the next game
            Platform.runLater(() -> {
                SharedBoard.getBoard().resetBoard();
                rawMoves.clear();
                isWhiteToMove = true;
            });

            Thread.sleep(2000);
            playOneGame();
        }
    }

    /**
     * Generates a move for White using Stockfish.
     */
    private String makeStockfishMove() throws IOException {
        stockfish.sendCommand("position startpos moves " + getMoveHistory());
        stockfish.sendCommand("go movetime 1000");

        String bestMove = stockfish.getBestMove();
        if (bestMove == null || bestMove.isEmpty()) {
            return "0000"; // No valid move found
        }

        return bestMove;
    }

    /**
     * Generates a random move for Black using MonteCarloMoves.
     */
    private String makeMonteCarloMoveForBlack() {
        int[] move = monteCarlo.getRandomMoveForBlack();
        if (move == null || move.length != 4) {
            return "0000"; // No move available, game over
        }

        // Apply the move to the shared board
        SharedBoard.getBoard().movePiece(move[0], move[1], move[2], move[3]);
        SharedBoard.getBoard().nextMove();

        return toChessNotation(move);
    }

    /**
     * Converts an int[] move to algebraic notation (e.g., "e2e4").
     */
    private String toChessNotation(int[] move) {
        char fromFile = (char) ('a' + move[1]);
        int fromRank = 8 - move[0];
        char toFile = (char) ('a' + move[3]);
        int toRank = 8 - move[2];

        return "" + fromFile + fromRank + toFile + toRank;
    }

    /**
     * Updates the move history with the given move.
     */
    public void updateMoveHistory(String move) {
        rawMoves.add(move);
    }

    /**
     * Retrieves the move history as a formatted string.
     */
    public String getMoveHistory() {
        return String.join(" ", rawMoves);
    }
}
