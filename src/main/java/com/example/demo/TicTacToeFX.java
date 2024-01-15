package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.demo.TicTacToe.isGameOver;
import static com.example.demo.TicTacToe.wins;

public class TicTacToeFX extends Application {

    private static final char HUMAN = 'O';
    private static final char COMP = 'X';
    private static final char EMPTY = ' ';
    private char currentPlayer = HUMAN;
    private Button[][] buttons = new Button[3][3];
    private static char[][] board = new char[3][3];

    static {
        // Initialize board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();

        // Determine the initial player randomly
        Random random = new Random();
        currentPlayer = (random.nextBoolean()) ? HUMAN : COMP;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button();
                btn.setMinSize(100, 100);
                int finalI = i;
                int finalJ = j;
                btn.setOnAction(e -> handleMove(finalI, finalJ, btn));
                buttons[i][j] = btn;
                grid.add(btn, j, i);
            }
        }

        Scene scene = new Scene(grid, 300, 300);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);
        primaryStage.show();

        // If the initial player is COMP, start the game by calling aiTurn
        if (currentPlayer == COMP) {
            aiTurn();
        }
    }




    private void handleMove(int row, int col, Button button) {
        if (board[row][col] != EMPTY) {
            return;
        }

        board[row][col] = currentPlayer;
        button.setText(String.valueOf(currentPlayer));

        if (wins(currentPlayer)) {
            announceWinner(currentPlayer);
        } else if (isGameOver()) {
            announceDraw();
        } else {
            currentPlayer = (currentPlayer == HUMAN) ? COMP : HUMAN;
            if (currentPlayer == COMP) {
                aiTurn();
            }
        }
    }




    private void aiTurn() {
        // Find the best move for the AI using minimax
        TicTacToe.Move bestMove = minimax(0, COMP);

        // Update the board with the AI's move
        board[bestMove.row][bestMove.col] = COMP;

        // Update the corresponding button text
        buttons[bestMove.row][bestMove.col].setText(String.valueOf(COMP));

        // Check if the AI has won
        if (wins(COMP)) {
            announceWinner(COMP);
        } else if (isGameOver()) {
            announceDraw();
        } else {
            // Switch to the human player's turn
            currentPlayer = HUMAN;
        }
    }

    private void announceWinner(char player) {
        String message = (player == HUMAN) ? "You have won!" : "Computer has won!";
        showAlert("Game Over", message);
        resetGame();
    }

    private void announceDraw() {
        showAlert("Game Over", "It's a draw!");
        resetGame();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetGame() {
        // Clear the board and reset the current player to the human player
        currentPlayer = HUMAN;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
                buttons[i][j].setText("");
            }

        }
        Random random = new Random();
        currentPlayer = (random.nextBoolean()) ? HUMAN : COMP;
        if (currentPlayer == COMP) {
            aiTurn();
        }

        System.out.println (currentPlayer );
    }

    public static TicTacToe.Move minimax(int depth, char player) {
        List<TicTacToe.Move> emptyCells = getEmptyCells();

        TicTacToe.Move bestMove = new TicTacToe.Move();
        if (player == COMP) {
            int bestScore = Integer.MIN_VALUE;
            for (TicTacToe.Move move : emptyCells) {
                board[move.row][move.col] = COMP;
                int score = minimax(depth + 1, HUMAN).score;
                board[move.row][move.col] = EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (TicTacToe.Move move : emptyCells) {
                board[move.row][move.col] = HUMAN;
                int score = minimax(depth + 1, COMP).score;
                board[move.row][move.col] = EMPTY;
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }
        bestMove.score = evaluate();
        return bestMove;
    }

    public static int evaluate() {
        if (wins(COMP)) {
            return 1;
        }
        if (wins(HUMAN)) {
            return -1;
        }
        return 0;
    }
    public static List<TicTacToe.Move> getEmptyCells() {
        List<TicTacToe.Move> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    TicTacToe.Move move = new TicTacToe.Move();
                    move.row = i;
                    move.col = j;
                    emptyCells.add(move);
                }
            }
        }
        return emptyCells;
    }
    public static boolean isGameOver() {
        return wins(HUMAN) || wins(COMP) || isBoardFull();
    }

    public static boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == EMPTY) {
                    return false; // There is an empty cell, the game is not over
                }
            }
        }
        return true; // All cells are filled, the game is a draw
    }

    public static boolean wins(char player) {
        // Check horizontal rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        // Check vertical columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }

        return false; // No winning condition found
    }







// Implement other methods from your original TicTacToe class here
// Such as minimax, wins, isGameOver, etc.

    public static void main(String[] args) {
        launch(args);
    }
}

