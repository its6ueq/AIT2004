package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.State.*;

public class AI {
    private static final int DEPTH = 4;
    private Map<String, Integer> transpositionTable = new HashMap<>();
    GamePanel gp;



    public static int a = 0;


    public AI(GamePanel gp) {
        this.gp = gp;
    }

    public int  alphaBetaMax(int alpha, int beta, int depth, State currState) {
//        System.out.println("alpha beta max");
//        if (isGameOver(board)) {
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    System.out.print(board[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println("Score: " + evaluateBoard(board));
//            return evaluateBoard(board);
//        }

        if (depth == 0 || isGameOver(currState)) {
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    System.out.print(board[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println("Score: " + evaluateBoard(board));
            return currState.getScore();
        }

        int bestValue = Integer.MIN_VALUE;
        List<State> possibleMoves = getAllPossibleMoves(currState, true);
        for (State move : possibleMoves) {
            int score = alphaBetaMin(alpha, beta, depth - 1, move);
            bestValue = Math.max(bestValue, score);
            alpha = Math.max(alpha, score);
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }

    public int  alphaBetaMin(int alpha, int beta, int depth, State currState) {
//        System.out.println("alpha beta min");
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();

//        if (isGameOver(board)) {
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    System.out.print(board[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println("Score: " + evaluateBoard(board));
//            return evaluateBoard(board);
//        }
        if (depth == 0 || isGameOver(currState)) {
            return currState.getScore();
        }

        int bestValue = Integer.MAX_VALUE;
        List<State> possibleMoves = getAllPossibleMoves(currState, false);
        for (State move : possibleMoves) {
            int score = alphaBetaMax(alpha, beta, depth - 1, move);
            bestValue = Math.min(bestValue, score);
            beta = Math.min(beta, score);
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }

    private String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char piece : row) {
                sb.append(piece);
            }
        }
        return sb.toString();
    }

//    public int evaluateBoard(State prevState) {
//        String boardString = boardToString(prevState.getBoard());
////        for (int i = 0; i < 8; i++) {
////            for (int j = 0; j < 8; j++) {
////                System.out.print(board[i][j] + " ");
////            }
////            System.out.println();
////        }
//
//
//
//
////        System.out.println("Score: " + score);
//        return score;
//    }



    public List<State> getAllPossibleMoves(State prevState, boolean isWhite) {
        List<State> possibleMoves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = prevState.getIndex(i, j);
                if (isWhite && Character.isUpperCase(piece) || !isWhite && Character.isLowerCase(piece)) {
                    //System.out.println("Generating moves for " + piece + " at " + i + ", " + j);
                    possibleMoves.addAll(generateMovesForPiece(prevState, i, j, piece));
                }
            }
        }

        if (isWhite){
            State temp = new State (prevState.getBoard());
            if(temp.addCastle(1))
                possibleMoves.add(temp);
            temp = new State (prevState.getBoard());
            if(temp.addCastle(2))
                possibleMoves.add(temp);
        }
        return possibleMoves;
    }

    private List<State> generateMovesForPiece(State prevState, int row, int col, char piece) {
        List<State> states = new ArrayList<>();

        switch (Character.toLowerCase(piece)) {
            case 'p', 'P':
                generatePawnMoves(prevState, row, col, piece, states);
                break;
            case 'n', 'N':
                generateKnightMoves(prevState, row, col, piece, states);
                break;
            case 'b', 'B':
                generateBishopMoves(prevState, row, col, piece, states);
                break;
            case 'r', 'R':
                generateRookMoves(prevState, row, col, piece, states);
                break;
            case 'q', 'Q':
                generateQueenMoves(prevState, row, col, piece, states);
                break;
            case 'k', 'K':
                generateKingMoves(prevState, row, col, piece, states);
                break;
        }
        return states;
    }

    private void generatePawnMoves(State prevState, int row, int col, char piece, List<State> moves) {
        int direction = Character.isUpperCase(piece) ? -1 : 1;
        int startRow = Character.isUpperCase(piece) ? 6 : 1;

        // Move forward
        if (isWithinBoard(row + direction, col) && prevState.getIndex(row + direction, col) == ' ') {
            addMove(prevState, row, col, row + direction, col, moves, Character.isUpperCase(piece));

            // Double move from starting position
            if (row == startRow && prevState.getIndex(row + 2 * direction, col) == ' ') {
                addMove(prevState, row, col, row + 2 * direction, col, moves, Character.isUpperCase(piece));
            }
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            if (isWithinBoard(row + direction, col + i) &&
                    isOpponentPiece(prevState.getIndex(row + direction, col + i), piece)) {
                addMove(prevState, row, col, row + direction, col + i, moves, Character.isUpperCase(piece));
            }
        }
    }

    private void generateKnightMoves(State prevState, int row, int col, char piece, List<State> moves) {
        char[][] board = prevState.getBoard();
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isWithinBoard(newRow, newCol) &&
                    (board[newRow][newCol] == ' ' || isOpponentPiece(board[newRow][newCol], piece))) {
                addMove(prevState, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
            }
        }
    }

    private void generateBishopMoves(State prevState, int row, int col, char piece, List<State> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        generateSlidingMoves(prevState, row, col, piece, moves, directions);
    }

    private void generateRookMoves(State prevState, int row, int col, char piece, List<State> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        generateSlidingMoves(prevState, row, col, piece, moves, directions);
    }

    private void generateQueenMoves(State prevState, int row, int col, char piece, List<State> moves) {
        generateBishopMoves(prevState, row, col, piece, moves);
        generateRookMoves(prevState, row, col, piece, moves);
    }

    private void generateKingMoves(State prevstate, int row, int col, char piece, List<State> currState) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        boolean isWhite = Character.isUpperCase(piece);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isWithinBoard(newRow, newCol) &&
                    (prevstate.getIndex(newRow, newCol) == ' ' || isOpponentPiece(prevstate.getIndex(newRow, newCol), piece))) {
                addMove(prevstate, row, col, newRow, newCol, currState, isWhite);
            }
        }

    }

    private void generateSlidingMoves(State prevState, int row, int col, char piece,
                                      List<State> moves, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isWithinBoard(newRow, newCol)) {
                if (prevState.getIndex(newRow, newCol) == ' ') {
                    addMove(prevState, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
                } else {
                    if (isOpponentPiece(prevState.getIndex(newRow, newCol), piece)) {
                        addMove(prevState, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
                    }
                    break;
                }
                newRow += dir[0];
                newCol += dir[1];
            }
        }
    }

    private boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isOpponentPiece(char targetPiece, char piece) {
        return (Character.isUpperCase(piece) && Character.isLowerCase(targetPiece)) ||
                (Character.isLowerCase(piece) && Character.isUpperCase(targetPiece));
    }

    private boolean isGameOver(State prevState) {
        boolean whiteKingExists = false;
        boolean blackKingExists = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (prevState.getIndex(i, j) == 'K') whiteKingExists = true;
                if (prevState.getIndex(i, j) == 'k') blackKingExists = true;
            }
        }

        if (!whiteKingExists || !blackKingExists) {
            return true;
        }

        boolean whiteHasMoves = !getAllPossibleMoves(prevState, true).isEmpty();
        boolean blackHasMoves = !getAllPossibleMoves(prevState, false).isEmpty();

        return !(whiteHasMoves && blackHasMoves);
    }

    private void addMove(State prevState, int row, int col, int newRow, int newCol, List<State>moves, boolean isWhite) {

        State currState = new State(prevState, row, col, newRow, newCol, isWhite);

//        if(a <= 5){
//            currState.printBoard();
//            a++;
//        }



//        //state
//        char[][] newBoard = new char[8][8];
//        char[][] board = prevState.getBoard();
//        for (int i = 0; i < 8; i++) {
//            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
//        }
//        newBoard[newRow][newCol] = newBoard[row][col];
//        newBoard[row][col] = ' ';
//        int check = (isWhite) ? 1 : 0;

        //if (gp.checkingP != null && gp.checkingP.color == check) return;
        if(currState.validState()) moves.add(currState);
        //System.out.println("Added move: " + row + ", " + col + " to " + newRow + ", " + newCol);
    }



    private void addCastle(char[][] board){
        char[][] newBoard1 = new char[8][8];
        char[][] newBoard2 = new char[8][8];

        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard1[i], 0, 8);
            System.arraycopy(board[i], 0, newBoard2[i], 0, 8);
        }
    }
}
