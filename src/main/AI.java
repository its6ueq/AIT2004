package main;

import java.util.*;

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

        if (depth == 0) {
            return currState.getScore();
        }

        int bestValue = Integer.MIN_VALUE;
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = getAllPossibleMoves(currState);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
            char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
            int tempScore = currState.score;
            Boolean tempEndGame = currState.endGame;
            Boolean tempCastled = currState.castled;
            Boolean tempKingMoved = currState.kingMoved;
            Boolean tempRook1Moved = currState.rook1Moved;
            Boolean tempRook2Moved = currState.rook2Moved;

            currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());

            int score = alphaBetaMin(alpha, beta, depth - 1, currState);

            bestValue = Math.max(bestValue, score);
            alpha = Math.max(alpha, score);

            if (beta <= alpha) {
                break;
            }

            currState.score = tempScore;
            currState.endGame = tempEndGame;
            currState.castled = tempCastled;
            currState.kingMoved = tempKingMoved;
            currState.rook1Moved = tempRook1Moved;
            currState.rook2Moved = tempRook2Moved;

            currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
        }
        return bestValue;
    }

    public int  alphaBetaMin(int alpha, int beta, int depth, State currState) {

        if (depth == 0) {
            return currState.getScore();
        }

        int bestValue = Integer.MAX_VALUE;
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = getAllPossibleMoves(currState);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
            char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
            int tempScore = currState.score;
            Boolean tempEndGame = currState.endGame;
            Boolean tempCastled = currState.castled;
            Boolean tempKingMoved = currState.kingMoved;
            Boolean tempRook1Moved = currState.rook1Moved;
            Boolean tempRook2Moved = currState.rook2Moved;

            currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());

            int score = alphaBetaMax(alpha, beta, depth - 1, currState);
            bestValue = Math.min(bestValue, score);
            beta = Math.min(beta, score);
            if (beta <= alpha) {
                break;
            }

            currState.score = tempScore;
            currState.endGame = tempEndGame;
            currState.castled = tempCastled;
            currState.kingMoved = tempKingMoved;
            currState.rook1Moved = tempRook1Moved;
            currState.rook2Moved = tempRook2Moved;

            currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
        }
        return bestValue;
    }



    public LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllPossibleMoves(State prevState) {
        Boolean isWhite = prevState.isWhite();
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = new LinkedList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = prevState.getIndex(i, j);
                if (isWhite && Character.isUpperCase(piece) || !isWhite && Character.isLowerCase(piece)) {
                    //System.out.println("Generating moves for " + piece + " at " + i + ", " + j);
                    if(piece == 'p' || piece == 'P') possibleMoves.addAll(generateMovesForPiece(prevState, i, j, piece));
                    else possibleMoves.addAll(0, generateMovesForPiece(prevState, i, j, piece));
                }
            }

        }


        return possibleMoves;
    }

    private LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> generateMovesForPiece(State prevState, int row, int col, char piece) {
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> states = new LinkedList<>();

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

    private void generatePawnMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int direction = Character.isUpperCase(piece) ? -1 : 1;
        int startRow = Character.isUpperCase(piece) ? 6 : 1;

        // Move forward
        if (isWithinBoard(row + direction, col) && prevState.getIndex(row + direction, col) == ' ') {
            addMove(row, col, row + direction, col, moves, Character.isUpperCase(piece), true);

            // Double move from starting position
            if (row == startRow && prevState.getIndex(row + 2 * direction, col) == ' ') {
                addMove(row, col, row + 2 * direction, col, moves, Character.isUpperCase(piece), true);
            }
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            if (isWithinBoard(row + direction, col + i) &&
                    isOpponentPiece(prevState.getIndex(row + direction, col + i), piece)) {
                addMove(row, col, row + direction, col + i, moves, Character.isUpperCase(piece), false);
            }
        }
    }

    private void generateKnightMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isWithinBoard(newRow, newCol) &&
                    (prevState.getIndex(newRow, newCol) == ' ' || isOpponentPiece(prevState.getIndex(newRow, newCol), piece))) {
                addMove(row, col, newRow, newCol, moves, Character.isUpperCase(piece), false);
            }
        }
    }

    private void generateBishopMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        generateSlidingMoves(prevState, row, col, piece, moves, directions);
    }

    private void generateRookMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        generateSlidingMoves(prevState, row, col, piece, moves, directions);
    }

    private void generateQueenMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        generateBishopMoves(prevState, row, col, piece, moves);
        generateRookMoves(prevState, row, col, piece, moves);
    }

    private void generateKingMoves(State prevstate, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> currState) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        boolean isWhite = Character.isUpperCase(piece);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isWithinBoard(newRow, newCol) &&
                    (prevstate.getIndex(newRow, newCol) == ' ' || isOpponentPiece(prevstate.getIndex(newRow, newCol), piece))) {
                addMove(row, col, newRow, newCol, currState, isWhite, false);
            }
        }

    }

    private void generateSlidingMoves(State prevState, int row, int col, char piece,
                                      LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isWithinBoard(newRow, newCol)) {
                if (prevState.getIndex(newRow, newCol) == ' ') {
                    addMove(row, col, newRow, newCol, moves, Character.isUpperCase(piece), false);
                } else {
                    if (isOpponentPiece(prevState.getIndex(newRow, newCol), piece)) {
                        addMove(row, col, newRow, newCol, moves, Character.isUpperCase(piece), false);
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

    private void addMove(int row, int col, int newRow, int newCol, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves, boolean isWhite, boolean isPawn) {

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> tempMoves = new Pair<>(new Pair<>(row, col), new Pair<>(newRow, newCol));
        if(isPawn) moves.addLast(tempMoves);
        else moves.addFirst(tempMoves);

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
