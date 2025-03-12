package com.chess.stockfish;

import com.chess.window.MainWindow;
import javafx.application.Application;

/*
 * Copyright (c) 2024
 * George Miller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ----------------------------------------------------------------------------
 *
 * Class: Main
 *
 * This class serves as the entry point for the chess application. It initializes
 * the JavaFX framework and launches the `MainWindow`, which acts as the primary
 * graphical user interface for the application.
 *
 * Dependencies:
 * - `MainWindow` for managing the application's primary UI.
 * - JavaFX `Application` for launching the graphical interface.
 *
 * Usage:
 * - Run this class to start the chess application.
 */
public class Main {

    /**
     * Main method to launch the chess application.
     *
     * This method initializes the JavaFX runtime and starts the `MainWindow` class,
     * which serves as the central UI for the chess game. The JavaFX application
     * lifecycle begins with this call, and the `MainWindow` manages subsequent user
     * interactions and game logic.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        Application.launch(MainWindow.class, args);
    }
}
