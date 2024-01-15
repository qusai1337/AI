package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacToe {

    static class Move {
        int row, col , score;
        ;
    }

    static char[][] board = new char[3][3];
    static final char HUMAN = 'O';
    static final char COMP = 'X';
    static final char EMPTY = ' ';

    // Initialize board
    static {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char hChoice = HUMAN;
        char cChoice = COMP;
        char first = ' ';

        // Main game loop
        while (!isGameOver()) {
            printBoard();
            if (first == ' ') {
                System.out.println("Do you want to start first? (Y/N): ");
                first = scanner.next().toUpperCase().charAt(0);
            }

            if (first == 'N') {
                aiTurn();
                first = 'Y';
            }

            humanTurn(scanner);
            aiTurn();
        }

        printBoard();
        if (wins(COMP)) {
            System.out.println("Computer has won");
        } else if (wins(HUMAN)) {
            System.out.println("You have won");
        } else {
            System.out.println("It's a draw");
        }
        scanner.close();
    }

    public static boolean isGameOver() {
        return wins(HUMAN) || wins(COMP) || getEmptyCells().isEmpty();
    }

    public static List<Move> getEmptyCells() {
        List<Move> emptyCells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    Move move = new Move();
                    move.row = i;
                    move.col = j;
                    emptyCells.add(move);
                }
            }
        }
        return emptyCells;
    }

    public static boolean wins(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        return board[0][2] == player && board[1][1] == player && board[2][0] == player;
    }

    public static void aiTurn() {
        Move bestMove = minimax(0, COMP);
        board[bestMove.row][bestMove.col] = COMP;
    }

    public static Move minimax(int depth, char player) {
        List<Move> emptyCells = getEmptyCells();

        Move bestMove = new Move();
        if (player == COMP) {
            int bestScore = Integer.MIN_VALUE;
            for (Move move : emptyCells) {
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
            for (Move move : emptyCells) {
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

    public static void humanTurn(Scanner scanner) {
        boolean validMove = false;
        do {
            System.out.println("Your move (row[1-3] column[1-3]): ");
            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;
            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == EMPTY) {
                board[row][col] = HUMAN;
                validMove = true;
            } else {
                System.out.println("Invalid move! Try again.");
            }
        } while (!validMove);
    }

    public static void printBoard() {
        System.out.println("-----------");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print("| " + board[i][j] + " ");
            }
            System.out.println("|");
            System.out.println("-----------");
        }
    }
}
