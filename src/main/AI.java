package main;

import java.util.*;

import static main.GamePanel.*;
import static main.State.*;

public class AI {
    private Map<String, Integer> transpositionTable = new HashMap<>();
    GamePanel gp;

    public static int a = 0;
    public static int count = 0;

    public AI(GamePanel gp) {
        this.gp = gp;
    }

    public int  alphaBetaMax(int alpha, int beta, int depth, State currState) {
//        System.out.println("max");
//        a++;
//        if(a== 100) System.exit(0);
//        System.out.println("Max Depth: " + depth);
        if (depth == 0 || currState.endGame) {
            return switch (endGame(currState)) {
                case 1 -> -10000;
                case 2 -> 10000;
                case 3 -> 0;
                default -> currState.getScore();
            };
        }

        int bestValue = Integer.MIN_VALUE;
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = new LinkedList<>();
        getAllPossibleMoves(currState, possibleMoves);
//        System.out.println("Possible Moves: " + possibleMoves.size());
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
//            if(a > 0) a--;
//            else System.exit(0);
            char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
            int tempScore = currState.score;
            Boolean tempCastled = currState.castled;
            Boolean tempKingMoved = currState.kingMoved;
            Boolean tempRook1Moved = currState.rook1Moved;
            Boolean tempRook2Moved = currState.rook2Moved;

            currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());

            if(currState.validState()){
                int score = alphaBetaMin(alpha, beta, depth - 1, currState);
//                System.out.println("Score: "+ score + " Alpha: " + alpha + " Beta: " + beta + " Depth: " + depth);
                bestValue = Math.max(bestValue, score);
                alpha = Math.max(alpha, score);

//                System.out.println("Score: "+ score + " Alpha: " + alpha + " Beta: " + beta + " Depth: " + depth);
//                currState.printBoard();
                if (beta <= alpha) {
//                    System.out.println("Cutting");
                    currState.score = tempScore;
                    currState.castled = tempCastled;
                    currState.kingMoved = tempKingMoved;
                    currState.rook1Moved = tempRook1Moved;
                    currState.rook2Moved = tempRook2Moved;

                    currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
                    break;
                }
            }

            currState.score = tempScore;
            currState.castled = tempCastled;
            currState.kingMoved = tempKingMoved;
            currState.rook1Moved = tempRook1Moved;
            currState.rook2Moved = tempRook2Moved;

            currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);




        }

        if(bestValue == -2147483648) {
            return currState.score;
//            for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
//                char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
//                int tempScore = currState.score;
//                Boolean tempCastled = currState.castled;
//                Boolean tempKingMoved = currState.kingMoved;
//                Boolean tempRook1Moved = currState.rook1Moved;
//                Boolean tempRook2Moved = currState.rook2Moved;
//                currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());
//                currState.printBoard();
//                currState.score = tempScore;
//                currState.castled = tempCastled;
//                currState.kingMoved = tempKingMoved;
//                currState.rook1Moved = tempRook1Moved;
//                currState.rook2Moved = tempRook2Moved;
//
//                currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
//            }
//            System.out.println("Best Value: " + bestValue);
//            System.exit(0);

        }
        return bestValue;
    }

    public int  alphaBetaMin(int alpha, int beta, int depth, State currState) {
//        System.out.println("Min Depth: " + depth);
        if (depth == 0 || currState.endGame) {
            return switch (endGame(currState)) {
                case 1 -> -10000;
                case 2 -> 10000;
                case 3 -> 0;
                default -> currState.getScore();
            };
        }

        int bestValue = Integer.MAX_VALUE;
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = new LinkedList<>();
        getAllPossibleMoves(currState, possibleMoves);
//        System.out.println("Possible Moves: " + possibleMoves.size());
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
            char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
            int tempScore = currState.score;
            Boolean tempCastled = currState.castled;
            Boolean tempKingMoved = currState.kingMoved;
            Boolean tempRook1Moved = currState.rook1Moved;
            Boolean tempRook2Moved = currState.rook2Moved;

            currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());

            if(currState.validState()) {

                int score = alphaBetaMax(alpha, beta, depth - 1, currState);

//                System.out.println("Score: "+ score + " Alpha: " + alpha + " Beta: " + beta + " Depth: " + depth);

                bestValue = Math.min(bestValue, score);
                beta = Math.min(beta, score);

//                System.out.println("Score: "+ score + " Alpha: " + alpha + " Beta: " + beta + " Depth: " + depth);
//                currState.printBoard();

                if (beta <= alpha) {
//                    System.out.println("Cutting min");
                    currState.score = tempScore;
                    currState.castled = tempCastled;
                    currState.kingMoved = tempKingMoved;
                    currState.rook1Moved = tempRook1Moved;
                    currState.rook2Moved = tempRook2Moved;

                    currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
                    break;
                }
            }
            currState.score = tempScore;
            currState.castled = tempCastled;
            currState.kingMoved = tempKingMoved;
            currState.rook1Moved = tempRook1Moved;
            currState.rook2Moved = tempRook2Moved;

            currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);

        }
        if(bestValue == 2147483647) {
            return currState.score;
//            for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {
//                char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
//                int tempScore = currState.score;
//                Boolean tempCastled = currState.castled;
//                Boolean tempKingMoved = currState.kingMoved;
//                Boolean tempRook1Moved = currState.rook1Moved;
//                Boolean tempRook2Moved = currState.rook2Moved;
//                currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());
//                currState.printBoard();
//                currState.score = tempScore;
//                currState.castled = tempCastled;
//                currState.kingMoved = tempKingMoved;
//                currState.rook1Moved = tempRook1Moved;
//                currState.rook2Moved = tempRook2Moved;
//
//                currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
//            }
//            System.out.println("Best Value: " + bestValue);
//            System.exit(0);

        }
        return bestValue;
    }



    public void  getAllPossibleMoves(State currState, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves) {
        boolean isWhite = currState.isWhite();
//        if(isWhite) System.out.println("da trang");
//        else System.out.println("daden");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = currState.getIndex(i, j);
                if (isWhite && Character.isUpperCase(piece) || !isWhite && Character.isLowerCase(piece)) {
//                    System.out.println("Generating moves for " + piece + " at " + i + ", " + j);
                    generateMovesForPiece(currState, i, j, piece, possibleMoves);
                }
            }

        }
//        System.out.println("possible move: " + possibleMoves);

    }

    private LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> generateMovesForPiece(State prevState, int row, int col, char piece,LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>  states) {
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
            addMove(row, col, row + direction, col, moves, true);

            // Double move from starting position
            if (row == startRow && prevState.getIndex(row + 2 * direction, col) == ' ') {
                addMove(row, col, row + 2 * direction, col, moves, true);
            }
        }

        // Capture diagonally
        for (int i = -1; i <= 1; i += 2) {
            if (isWithinBoard(row + direction, col + i) &&
                    isOpponentPiece(prevState.getIndex(row + direction, col + i), piece)) {
                addMove(row, col, row + direction, col + i, moves, false);
            }
        }

        //En passant

    }

    private void generateKnightMoves(State prevState, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isWithinBoard(newRow, newCol)){
                if (prevState.getIndex(newRow, newCol) == ' ') {
                    addMove(row, col, newRow, newCol, moves, true);
                } else if (isOpponentPiece(prevState.getIndex(newRow, newCol), piece)) {
                    addMove(row, col, newRow, newCol, moves, false);
                }
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

    private void generateKingMoves(State prevstate, int row, int col, char piece, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves) {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isWithinBoard(newRow, newCol) &&
                    (prevstate.getIndex(newRow, newCol) == ' ' || isOpponentPiece(prevstate.getIndex(newRow, newCol), piece))) {
                if(prevstate.kingCanMove(row, col, newRow, newCol)){
                    addMove(row, col, newRow, newCol, moves, false);
                }
            }
        }

        switch (prevstate.canCastle()){
            case 1:
                addMove(0, 4, 0, 2, moves, false);
                break;
            case 2:
                addMove(0, 4, 0, 6, moves, false);
                break;
            case 3:
                addMove(0, 4, 0, 2, moves, false);
                addMove(0, 4, 0, 6, moves, false);
                break;
            default:
                break;
        }

    }

    private void generateSlidingMoves(State prevState, int row, int col, char piece,
                                      LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            while (isWithinBoard(newRow, newCol)) {
                if (prevState.getIndex(newRow, newCol) == ' ') {
                    addMove(row, col, newRow, newCol, moves, true);
                } else {
                    if (isOpponentPiece(prevState.getIndex(newRow, newCol), piece)) {
                        addMove(row, col, newRow, newCol, moves, false);
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

    private void addMove(int row, int col, int newRow, int newCol, LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves, boolean addLast) {

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> tempMoves = new Pair<>(new Pair<>(row, col), new Pair<>(newRow, newCol));
        if(addLast) moves.addLast(tempMoves);
        else moves.addFirst(tempMoves);

    }

    // 0: continue
    // 1: black win
    // 2: white win
    // 3: stalemate
    public int endGame(State currState){
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = new LinkedList<>();
        getAllPossibleMoves(currState, possibleMoves);
//        System.out.println("Current color: " + currState.color);
        if (possibleMoves.isEmpty()) {
//            System.out.println("No possible moves");
            if (currState.isKingInCheck(currState.color)) {
                //System.out.println("Checkmate");
                return currentColor == WHITE ? 1 : 2; // Checkmate
            } else {
                return 3; // Stalemate
            }
        }
        if(!canMove(currState)){
            if(currState.color == WHITE) return 1;
            else return 2;
        }
        return 0;
    }

    private boolean canMove(State currState) {
        LinkedList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> possibleMoves = new LinkedList<>();
        getAllPossibleMoves(currState, possibleMoves);
        if( currState.whiteChecked[currState.BKingX][currState.BKingY] == 0 && currState.blackChecked[currState.WKingX][currState.WKingY] == 0)
            return true;
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : possibleMoves) {

            char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
            int tempScore = currState.score;
            Boolean tempCastled = currState.castled;
            Boolean tempKingMoved = currState.kingMoved;
            Boolean tempRook1Moved = currState.rook1Moved;
            Boolean tempRook2Moved = currState.rook2Moved;

            currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());
            if(!currState.validState()){
                currState.score = tempScore;
                currState.castled = tempCastled;
                currState.kingMoved = tempKingMoved;
                currState.rook1Moved = tempRook1Moved;
                currState.rook2Moved = tempRook2Moved;
//                currState.printBoard();
                currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
                continue;
            }
            if ((currState.color == WHITE && currState.whiteChecked[currState.BKingX][currState.BKingY] == 0) ||
                    (currState.color == BLACK && currState.blackChecked[currState.WKingX][currState.WKingY] == 0)){
                currState.score = tempScore;
                currState.castled = tempCastled;
                currState.kingMoved = tempKingMoved;
                currState.rook1Moved = tempRook1Moved;
                currState.rook2Moved = tempRook2Moved;
//                currState.printBoard();
                currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
                return true;
            }

            currState.score = tempScore;
            currState.castled = tempCastled;
            currState.kingMoved = tempKingMoved;
            currState.rook1Moved = tempRook1Moved;
            currState.rook2Moved = tempRook2Moved;
            currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);

        }
        return false;
    }
}
