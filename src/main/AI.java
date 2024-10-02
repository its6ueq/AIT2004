package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AI {
    private static final int DEPTH = 4;
    private Map<String, Integer> transpositionTable = new HashMap<>();
    GamePanel gp;

    public AI(GamePanel gp) {
        this.gp = gp;
    }

    public int  alphaBetaMax(int alpha, int beta, int depth, char[][] board) {
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

        if (depth == 0 || isGameOver(board)) {
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    System.out.print(board[i][j] + " ");
//                }
//                System.out.println();
//            }
//            System.out.println("Score: " + evaluateBoard(board));
            return evaluateBoard(board);
        }

        int bestValue = Integer.MIN_VALUE;
        List<char[][]> possibleMoves = getAllPossibleMoves(board, true);
        for (char[][] move : possibleMoves) {
            int score = alphaBetaMin(alpha, beta, depth - 1, move);
            bestValue = Math.max(bestValue, score);
            alpha = Math.max(alpha, score);
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }

    public int  alphaBetaMin(int alpha, int beta, int depth, char[][] board) {
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
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        int bestValue = Integer.MAX_VALUE;
        List<char[][]> possibleMoves = getAllPossibleMoves(board, false);
        for (char[][] move : possibleMoves) {
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

    public int evaluateBoard(char[][] board) {
        String boardString = boardToString(board);
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                System.out.print(board[i][j] + " ");
//            }
//            System.out.println();
//        }

        int score = 0;
        for (char[] row : board) {
            for (char piece : row) {
                score += getPieceValue(piece);
            }
        }

//        System.out.println("Score: " + score);
        return score;
    }

    private int getPieceValue(char piece) {
        return switch (piece) {
            case 'p' -> -100;
            case 'n' -> -320;
            case 'b' -> -330;
            case 'r' -> -500;
            case 'q' -> -900;
            case 'k' -> -100000;

            case 'P' -> +100;
            case 'N' -> +320;
            case 'B' -> +330;
            case 'R' -> +500;
            case 'Q' -> +900;
            case 'K' -> +100000;
            default -> 0;
        };
    }

    public List<char[][]> getAllPossibleMoves(char[][] board, boolean isWhite) {
        List<char[][]> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (isWhite && Character.isUpperCase(piece) || !isWhite && Character.isLowerCase(piece)) {
                    //System.out.println("Generating moves for " + piece + " at " + i + ", " + j);
                    possibleMoves.addAll(generateMovesForPiece(board, i, j, piece));
                }
            }
        }
        return possibleMoves;
    }

    private List<char[][]> generateMovesForPiece(char[][] board, int row, int col, char piece) {
        List<char[][]> moves = new ArrayList<>();
        switch (Character.toLowerCase(piece)) {
            case 'p', 'P':
                generatePawnMoves(board, row, col, piece, moves);
                break;
            case 'n', 'N':
                generateKnightMoves(board, row, col, piece, moves);
                break;
            case 'b', 'B':
                generateBishopMoves(board, row, col, piece, moves);
                break;
            case 'r', 'R':
                generateRookMoves(board, row, col, piece, moves);
                break;
            case 'q', 'Q':
                generateQueenMoves(board, row, col, piece, moves);
                break;
            case 'k', 'K':
                generateKingMoves(board, row, col, piece, moves);
                break;
        }
        return moves;
    }

    private void generatePawnMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        int direction = Character.isUpperCase(piece) ? -1 : 1;
        int startRow = Character.isUpperCase(piece) ? 6 : 1;

        // Move forward
        if (isWithinBoard(row + direction, col) && board[row + direction][col] == ' ') {
            addMove(board, row, col, row + direction, col, moves, Character.isUpperCase(piece));

            // Double move from starting position
            if (row == startRow && board[row + 2 * direction][col] == ' ') {
                addMove(board, row, col, row + 2 * direction, col, moves, Character.isUpperCase(piece));
            }
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            if (isWithinBoard(row + direction, col + i) &&
                    isOpponentPiece(board[row + direction][col + i], piece)) {
                addMove(board, row, col, row + direction, col + i, moves, Character.isUpperCase(piece));
            }
        }
    }

    private void generateKnightMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isWithinBoard(newRow, newCol) &&
                    (board[newRow][newCol] == ' ' || isOpponentPiece(board[newRow][newCol], piece))) {
                addMove(board, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
            }
        }
    }

    private void generateBishopMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        generateSlidingMoves(board, row, col, piece, moves, directions);
    }

    private void generateRookMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        generateSlidingMoves(board, row, col, piece, moves, directions);
    }

    private void generateQueenMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        generateBishopMoves(board, row, col, piece, moves);
        generateRookMoves(board, row, col, piece, moves);
    }

    private void generateKingMoves(char[][] board, int row, int col, char piece, List<char[][]> moves) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        boolean isWhite = Character.isUpperCase(piece);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isWithinBoard(newRow, newCol) &&
                    (board[newRow][newCol] == ' ' || isOpponentPiece(board[newRow][newCol], piece))) {
                addMove(board, row, col, newRow, newCol, moves, isWhite);
            }
        }
        // TODO: Castling
    }

    private void generateSlidingMoves(char[][] board, int row, int col, char piece,
                                      List<char[][]> moves, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isWithinBoard(newRow, newCol)) {
                if (board[newRow][newCol] == ' ') {
                    addMove(board, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
                } else {
                    if (isOpponentPiece(board[newRow][newCol], piece)) {
                        addMove(board, row, col, newRow, newCol, moves, Character.isUpperCase(piece));
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

    private boolean isGameOver(char[][] board) {
        boolean whiteKingExists = false;
        boolean blackKingExists = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 'K') whiteKingExists = true;
                if (board[i][j] == 'k') blackKingExists = true;
            }
        }

        if (!whiteKingExists || !blackKingExists) {
            return true;
        }

        boolean whiteHasMoves = !getAllPossibleMoves(board, true).isEmpty();
        boolean blackHasMoves = !getAllPossibleMoves(board, false).isEmpty();

        return !(whiteHasMoves && blackHasMoves);
    }

    private void addMove(char[][] board, int row, int col, int newRow, int newCol, List<char[][]> moves, boolean isWhite) {
        char[][] newBoard = new char[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
        }
        newBoard[newRow][newCol] = newBoard[row][col];
        newBoard[row][col] = ' ';
        int check = (isWhite) ? 1 : 0;
        //if (gp.checkingP != null && gp.checkingP.color == check) return;
        moves.add(newBoard);
        //System.out.println("Added move: " + row + ", " + col + " to " + newRow + ", " + newCol);
    }

}
