package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;

public class TicTacToeFX extends Application {

    private static final char player = 'O';
    private static final char ai = 'X';
    private static final char EMPTY = ' ';
    private int humanWins = 0;
    private int compWins = 0;
    private int gamesPlayed = 0;
    private String seriesWinner = "";
    private Label humanWinsLabel = new Label("Human Win 0");
    private Label compWinsLabel = new Label("Computer Win 0");

    private char currentPlayer = player;
    private Button[][] buttons = new Button[3][3];
    private static char[][] board = new char[3][3];

    static {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.add(humanWinsLabel, 0, 3, 2, 1);
        grid.add(compWinsLabel, 1, 3, 2, 1);

        Random random = new Random();
        currentPlayer = (random.nextBoolean()) ? player : ai;

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

        if (currentPlayer == ai) {
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
            currentPlayer = (currentPlayer == player) ? ai : player;
            if (currentPlayer == ai) {
                aiTurn();
            }
        }
    }




    private void aiTurn() {
        TicTacToe.Move bestMove = minimax(0, ai);

        board[bestMove.row][bestMove.col] = ai;

        buttons[bestMove.row][bestMove.col].setText(String.valueOf(ai));

        if (wins(ai)) {
            announceWinner(ai);
        } else if (isGameOver()) {
            announceDraw();
        } else {
            currentPlayer = player;
        }
    }

    private void announceWinner(char player) {
        gamesPlayed++;

        if (player == TicTacToeFX.player) {
            humanWins++;
            humanWinsLabel.setText("Human Wins: " + humanWins);
        } else {
            compWins++;
            compWinsLabel.setText("Computer Wins: " + compWins);
        }

        showAlert("Round Over", (player == TicTacToeFX.player) ? "You won!" : "Computer won ");

        if (humanWins == 3 || compWins == 3) {
            seriesWinner = (humanWins == 3) ? "Human" : "Computer";
            showAlert("Series Over", seriesWinner + " wins the series!");
            resetGame();
            return;
        }

        if (gamesPlayed == 5 && seriesWinner.isEmpty()) {
            showAlert("Series Over", "it's a tie!");
            resetGame();
            return;
        }

        resetGame();
    }


    private void announceDraw() {
        showAlert("Game Over", "draw!");
        resetGame();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetGame() {
        if (!seriesWinner.isEmpty() || gamesPlayed == 5) {
            String finalMessage = seriesWinner.isEmpty() ? "tie!" : seriesWinner + " wins ";
            showAlert("Series Over", finalMessage);

            Alert confirmDialog = new Alert(AlertType.CONFIRMATION, "another series?", ButtonType.YES, ButtonType.NO);
            confirmDialog.setTitle("Play Again?");
            confirmDialog.setHeaderText(null);

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                humanWins = 0;
                compWins = 0;
                gamesPlayed = 0;
                seriesWinner = "";
            } else {
                System.exit(0);
            }
        }

        humanWinsLabel.setText("Human Wins: " + humanWins);
        compWinsLabel.setText("Computer Wins: " + compWins);

        currentPlayer = player;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
                buttons[i][j].setText("");
            }
        }

        Random random = new Random();
        currentPlayer = (random.nextBoolean()) ? player : ai;
        if (currentPlayer == ai) {
            aiTurn();
        }
    }

    public static TicTacToe.Move minimax(int depth, char player) {
        List<TicTacToe.Move> emptyCells = getEmptyCells();

        TicTacToe.Move bestMove = new TicTacToe.Move();
        if (player == ai) {
            int bestScore = Integer.MIN_VALUE;
            for (TicTacToe.Move move : emptyCells) {
                board[move.row][move.col] = ai;
                int score = minimax(depth + 1, TicTacToeFX.player).score;
                board[move.row][move.col] = EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (TicTacToe.Move move : emptyCells) {
                board[move.row][move.col] = TicTacToeFX.player;
                int score = minimax(depth + 1, ai).score;
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
        if (wins(ai)) {
            return 1;
        }
        if (wins(player)) {
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
        return wins(player) || wins(ai) || isBoardFull();
    }

    public static boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
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

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
