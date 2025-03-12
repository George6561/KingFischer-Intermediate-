package com.chess.stockfish;

import com.chess.montecarlo.MonteCarloMoves;
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
    private MonteCarloMoves monteCarlo;     // Random move generator for Black
    private List<String> rawMoves;          // Move history
    private boolean isWhiteToMove = true;   // Track turns

    /**
     * Constructor initializes the game with the ChessWindow.
     *
     * @param chessWindow The UI representation of the chessboard.
     */
    public ChessGame(ChessWindow chessWindow) {
        this.chessWindow = chessWindow;
        this.stockfish = new StockfishConnector(); // White plays with Stockfish
        this.monteCarlo = new MonteCarloMoves(chessWindow.getBoard()); // Black plays with MonteCarlo
        this.rawMoves = new ArrayList<>();
    }

    /**
     * Starts the chess game loop where White uses Stockfish and Black plays
     * random moves.
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
     * Main game loop where Stockfish plays White and MonteCarloMoves plays
     * Black.
     *
     * @throws IOException If an error occurs while generating moves.
     * @throws InterruptedException If the thread is interrupted during UI
     * updates.
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
                    latch.countDown();  // Ensure UI update is complete before continuing
                }
            });

            latch.await();  // Wait until the move is fully applied before switching turns

            // Check for checkmate **after move execution**
            if (chessWindow.getBoard().isCheckmate(chessWindow.getBoard().currentPlayer())) {
                System.out.println("Checkmate detected! Resetting board and starting a new game...");

                Platform.runLater(() -> {
                    chessWindow.getBoard().resetBoard();
                    rawMoves.clear();  // Clear move history
                });

                isWhiteToMove = true;  // Ensure White always starts
                Thread.sleep(2000);  // Give a short delay before restarting
                playOneGame();  // Restart the game loop
                return;
            }

            Thread.sleep(500);  // Small delay for readability
            isWhiteToMove = !isWhiteToMove;  // Flip turn only AFTER the move is fully processed
        }
    }

    /**
     * Keeps playing multiple games in a loop until the user manually stops it.
     *
     * @throws IOException If an error occurs in communication with Stockfish.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void playMultipleGames() throws IOException, InterruptedException {
        System.out.println("CALLING PLAY MULTIPLE GAMES FUNCTIONS");
        
        while (true) {
            System.out.println("Starting a new game...");

            // Reset the board properly before playing the next game
            Platform.runLater(() -> {
                chessWindow.getBoard().resetBoard();  // Reset the board
                rawMoves.clear();                    // Clear move history
                isWhiteToMove = true;                 // Ensure White starts first
            });

            Thread.sleep(2000);  // Wait for UI update to complete before starting

            playOneGame();  // Start a new game
        }
    }

    /**
     * Generates a move for White using Stockfish.
     *
     * @return The best move suggested by Stockfish in UCI notation.
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
     *
     * @return The randomly selected legal move in algebraic notation.
     */
    private String makeMonteCarloMoveForBlack() {
        return monteCarlo.getRandomMoveForBlack();
    }

    /**
     * Updates the move history with the given move.
     *
     * @param move The move in UCI notation to add to the history.
     */
    public void updateMoveHistory(String move) {
        rawMoves.add(move);
    }

    /**
     * Retrieves the move history as a single formatted string.
     *
     * @return The move history in UCI notation.
     */
    public String getMoveHistory() {
        return String.join(" ", rawMoves);
    }
}
