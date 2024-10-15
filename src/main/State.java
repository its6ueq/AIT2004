package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static main.GamePanel.WHITE;
import static main.GamePanel.BLACK;
import static main.AI.count;

public class State  implements Comparable<State> {

    char[][] board;
    int[][] blackChecked;
    int[][] whiteChecked;

    int color;
    int WKingX, WKingY;
    int BKingX, BKingY;

    Boolean castled;
    Boolean kingMoved;
    Boolean rook1Moved;
    Boolean rook2Moved;

    int score;
    Boolean endGame;

    public static int[][] pawnPoint ;
    public static int[][] knightPoint ;
    public static int[][] bishopPoint ;
    public static int[][] rookPoint;
    public static int[][] queenPoint ;
    public static int[][] kingMidPoint ;
    public static int[][] kingEndPoint ;

    public State(char[][] board) {
        this.board = board;
        color = BLACK;

        whiteChecked = new int[8][8];
        blackChecked = new int[8][8];

        castled = false;
        kingMoved = false;
        rook1Moved = false;
        rook2Moved = false;
        score = 0;
        endGame = false;

        pawnPoint = new int[][]{
                {900,900,900,900,900,900,900,900},
                { 50, 50, 50, 50, 50, 50, 50, 50},
                { 10, 10, 20, 30, 30, 20, 10, 10},
                {  5,  5, 10, 25, 25, 10,  5,  5},
                {  0,  0,  0, 20, 20,  0,  0,  0},
                {  5, -5,-10,  0,  0,-10, -5,  5},
                {  5, 10, 10,-20,-20, 10, 10,  5},
                {  0,  0,  0,  0,  0,  0,  0,  0}
        };

        knightPoint = new int[][]{
                {-50, -40, -30, -30, -30, -30, -40, -50},
                {-40, -20,   0,   0,   0,   0, -20, -40},
                {-30,   0,  10,  15,  15,  10,   0, -30},
                {-30,   5,  15,  20,  20,  15,   5, -30},
                {-30,   0,  15,  20,  20,  15,   0, -30},
                {-30,   5,  10,  15,  15,  10,   5, -30},
                {-40, -20,   0,   5,   5,   0, -20, -40},
                {-50, -40, -30, -30, -30, -30, -40, -50}
        };

        bishopPoint = new int[][]{
                {-20, -10, -10, -10, -10, -10, -10, -20},
                {-10,   0,   0,   0,   0,   0,   0, -10},
                {-10,   0,   5,  10,  10,   5,   0, -10},
                {-10,   5,   5,  10,  10,   5,   5, -10},
                {-10,   0,  10,  10,  10,  10,   0, -10},
                {-10,  10,  10,  10,  10,  10,  10, -10},
                {-10,   5,   0,   0,   0,   0,   5, -10},
                {-20, -10, -10, -10, -10, -10, -10, -20}
        };

        rookPoint = new int[][]{
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 5, 10, 10, 10, 10, 10, 10,  5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                { 0,  0,  0,  5,  5,  0,  0,  0}
        };

        queenPoint = new int[][]{
                {-20, -10, -10,  -5,  -5,-10, -10, -20},
                {-10,   0,   0,   0,   0,  0,   0, -10},
                {-10,   0,   5,   5,   5,  5,   0, -10},
                { -5,   0,   5,   5,   5,  5,   0,  -5},
                {  0,   0,   5,   5,   5,  5,   0,  -5},
                {-10,   5,   5,   5,   5,  5,   0, -10},
                {-10,   0,   5,   0,   0,  0,   0, -10},
                {-20, -10, -10,  -5,  -5, -10,-10, -20}
        };

        kingMidPoint = new int[][]{
                {-30, -40, -40, -50, -50, -40, -40, -30},
                {-30, -40, -40, -50, -50, -40, -40, -30},
                {-30, -40, -40, -50, -50, -40, -40, -30},
                {-30, -40, -40, -50, -50, -40, -40, -30},
                {-20, -30, -30, -40, -40, -30, -30, -20},
                {-10, -20, -20, -20, -20, -20, -20, -10},
                { 20,  20,   0,   0,   0,   0,  20,  20},
                { 20,  30,  10,   0,   0,  10,  30,  20}
        };

        kingEndPoint = new int[][]{
                {-50, -40, -30, -20, -20, -30, -40, -50},
                {-30, -20, -10,   0,   0, -10, -20, -30},
                {-30, -10,  20,  30,  30,  20, -10, -30},
                {-30, -10,  30,  40,  40,  30, -10, -30},
                {-30, -10,  30,  40,  40,  30, -10, -30},
                {-30, -10,  20,  30,  30,  20, -10, -30},
                {-30, -30,   0,   0,   0,   0, -30, -30},
                {-50, -30, -30, -30, -30, -30, -30, -50}
        };

        setupState();
    }

    public void goMove(int row, int col, int newRow, int newCol) {
        if(board[row][col] == 'k' && abs(newCol - col) == 2)
            goCastle(row, col, newRow, newCol);
        else {
            updateScore(row, col, newRow, newCol);
            updateBoard(row, col, newRow, newCol);
        }

//        System.out.println("going move: " + row + "," + col + " -> " + newRow + "," + newCol);
//        printBoard();

        changeColor();
        if(color == BLACK && blackChecked[WKingX][WKingY] != 0) score -= 100000;
        count++;

    }

    public void undoMove(int row, int col, int newRow, int newCol, char tempPiece){
        if(board[newRow][newCol] == 'k' && abs(newCol - col) == 2) {
//            System.out.println("undoing");
            undoCastle(row, col, newRow, newCol);
        }
        else {
            undoBoard(row, col, newRow, newCol, tempPiece);
        }
//        System.out.println("Undoing move: " + row + "," + col + " -> " + newRow + "," + newCol);

//        printBoard();

        changeColor();
        count--;

    }

    private void updateScore(int row, int col, int newRow, int newCol){

        score -= calculatePosPoint(board[newRow][newCol], newRow, newCol);
        score -= calculatePosPoint(board[row][col], row, col);
        score += calculatePosPoint(board[row][col], newRow, newCol);
        score -= getPieceValue(board[newRow][newCol]);
    }


    public Boolean addCastle(int a){
        if(a == 1){
            if(!rook1Moved && !kingMoved && !castled && board[0][0] == 'r' && board[0][4] == 'k'){
                for(int i = 1; i <= 3; i++){
                    if(board[0][i] != ' ') return false;
                }
                for(int i = 2; i <= 4; i++){
                    if(whiteChecked[0][i] != 0) return false;
                }
                board[0][0] = ' ';
                board[0][2] = 'k';
                board[0][3] = 'r';
                board[0][4] = ' ';
                castled = true;
                rook1Moved = true;
                kingMoved = true;
                setupState();
                score += 100;
                return true;
            }
        } else {
            if(!rook2Moved && !kingMoved && !castled && board[0][7] == 'r' && board[0][4] == 'k'){
                for(int i = 5; i <= 6; i++){
                    if(board[0][i] != ' ') return false;
                }
                for(int i = 4; i <= 6; i++){
                    if(whiteChecked[0][i] != 0) return false;
                }
                board[0][4] = ' ';
                board[0][5] = 'r';
                board[0][6] = 'k';
                board[0][7] = ' ';
                castled = true;
                rook2Moved = true;
                kingMoved = true;
                setupState();
                score += 100;
                return true;
            }
        }
        return false;
    }



    private void updateBoard(int row, int col, int newRow, int newCol){
        if(board[row][col] == 'K') {
            WKingX = newRow;
            WKingY = newCol;
//            System.out.println(row + " " + col + " " + newRow + " " + newCol);
//            printBoard();
//            System.exit(0);
        }
        if (board[row][col] == 'k') {
            BKingX = newRow;
            BKingY = newCol;
        }

        if(row == 0 && col == 0) rook1Moved = true;
        if(row == 0 && col == 7) rook2Moved = true;
        if(row == 0 && col == 4) kingMoved = true;

        Set<Pair<Integer, Integer>> updateBishop = new HashSet<>();
        Set<Pair<Integer, Integer>> updateRook = new HashSet<>();

        int x, y;

        int[][] bishopDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : bishopDirections) {
            x = row + dir[0];
            y = col + dir[1];
            while (isWithinBoard(x, y) ) {
                if(x == row && y == col || x == newRow && y == newCol)
                    break;
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'b' || board[x][y] == 'B'){
                        updateBishop.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);
                        updateBishop.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        //        System.out.println("Bishop need to update: "+ updateBishop.size());
        int[][] rookDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : rookDirections) {
            x = row + dir[0];
            y = col + dir[1];
            while (isWithinBoard(x, y)) {
                if(x == row && y == col || x == newRow && y == newCol)
                    break;
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'r' || board[x][y] == 'R'){
                        updateRook.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);
                        updateRook.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        if(board[newRow][newCol] == ' '){
            for (int[] dir : bishopDirections) {
                x = newRow + dir[0];
                y = newCol + dir[1];
                while (isWithinBoard(x, y) ) {
                    if(x == row && y == col || x == newRow && y == newCol)
                        break;
                    if (board[x][y] != ' ') {
                        if(board[x][y] == 'b' || board[x][y] == 'B'){
                            updateBishop.add(new Pair<>(x, y));
                        }
                        if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);
                            updateBishop.add(new Pair<>(x, y));
                            updateRook.add(new Pair<>(x, y));
                        }
                        break;
                    }
                    x += dir[0];
                    y += dir[1];
                }
            }

            for (int[] dir : rookDirections) {
                x = newRow + dir[0];
                y = newCol + dir[1];
                while (isWithinBoard(x, y)) {
                    if(x == row && y == col || x == newRow && y == newCol)
                        break;
                    if (board[x][y] != ' ') {
                        if(board[x][y] == 'r' || board[x][y] == 'R'){
                            updateRook.add(new Pair<>(x, y));
                        }
                        if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);

                            updateBishop.add(new Pair<>(x, y));
                            updateRook.add(new Pair<>(x, y));
                        }
                        break;
                    }
                    x += dir[0];
                    y += dir[1];
                }
            }
        }


//        System.out.println(2 + " " + row + " " + col + " " + newRow + " " + newCol);

        filterPiece(board[newRow][newCol], newRow, newCol, -1);

//        System.out.println(3);

        for(Pair<Integer, Integer> pair : updateBishop){
            if(board[pair.getL()][pair.getR()] != ' ') {
                slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), -1);
            }
//            System.out.println(pair.getL() + " " + pair.getR());
        }

//        System.out.println(4);

        for(Pair<Integer, Integer> pair : updateRook){
            if(board[pair.getL()][pair.getR()] != ' ') {
            slidingMove(rookDirections, pair.getL(), pair.getR(), getColor(pair), -1);
            }
        }

//        System.out.println(5);

        filterPiece(board[row][col], row, col, -1);

//        System.out.println(6);

        board[newRow][newCol] = board[row][col];
        board[row][col] = ' ';

        filterPiece(board[newRow][newCol], newRow, newCol, 1);

//        System.out.println(7);





        for(Pair<Integer, Integer> pair : updateBishop){
            if(board[pair.getL()][pair.getR()] != ' '){
                slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), 1);
            }
        }

//        System.out.println(9);

        for(Pair<Integer, Integer> pair : updateRook){
            if(board[pair.getL()][pair.getR()] != ' ') {
                slidingMove(rookDirections, pair.getL(), pair.getR(), getColor(pair), 1);
            }
        }

    }




    private void undoBoard(int row, int col, int newRow, int newCol, char piece){
        if(board[newRow][newCol] == 'K') {
            WKingX = row;
            WKingY = col;
//            System.out.println(row + " " + col + " " + newRow + " " + newCol);
//            printBoard();
//            System.exit(0);
        }
        if(board[newRow][newCol] == 'k') {
            BKingX = row;
            BKingY = col;
        }
        if(piece == 'k'){
            BKingX = newRow;
            BKingY = newCol;
        }
        if(piece == 'K'){
            WKingX = newRow;
            WKingY = newCol;
        }
//        if(newRow == WKingX && newCol == WKingY) {
//            WKingX = row;
//            WKingY = col;
////            System.out.println(row + " " + col + " " + newRow + " " + newCol);
////            printBoard();
////            System.exit(0);
//        }
//        if (newRow == BKingX && newCol == BKingY) {
//            BKingX = row;
//            BKingY = col;
////            System.out.println(row + " " + col + " " + newRow + " " + newCol);
////            printBoard();
////            System.exit(0);
//        }
        Set<Pair<Integer, Integer>> updateBishop = new HashSet<>();
        Set<Pair<Integer, Integer>> updateRook = new HashSet<>();

        int x, y;

        int[][] bishopDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : bishopDirections) {
            x = row + dir[0];
            y = col + dir[1];
            while (isWithinBoard(x, y) ) {
                if(x == row && y == col || x == newRow && y == newCol)
                    break;
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'b' || board[x][y] == 'B'){
                        updateBishop.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);
                        updateBishop.add(new Pair<>(x, y));
                        updateRook.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        //        System.out.println("Bishop need to update: "+ updateBishop.size());
        int[][] rookDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : rookDirections) {
            x = row + dir[0];
            y = col + dir[1];
            while (isWithinBoard(x, y)) {
                if(x == row && y == col || x == newRow && y == newCol)
                    break;
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'r' || board[x][y] == 'R'){
                        updateRook.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);

                        updateBishop.add(new Pair<>(x, y));
                        updateRook.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        if(piece == ' '){
            for (int[] dir : bishopDirections) {
                x = newRow + dir[0];
                y = newCol + dir[1];
                while (isWithinBoard(x, y) ) {
                    if(x == row && y == col || x == newRow && y == newCol)
                        break;
                    if (board[x][y] != ' ') {
                        if(board[x][y] == 'b' || board[x][y] == 'B'){
                            updateBishop.add(new Pair<>(x, y));
                        }
                        if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);
                            updateBishop.add(new Pair<>(x, y));
                            updateRook.add(new Pair<>(x, y));
                        }
                        break;
                    }
                    x += dir[0];
                    y += dir[1];
                }
            }

            for (int[] dir : rookDirections) {
                x = newRow + dir[0];
                y = newCol + dir[1];
                while (isWithinBoard(x, y)) {
                    if(x == row && y == col || x == newRow && y == newCol)
                        break;
                    if (board[x][y] != ' ') {
                        if(board[x][y] == 'r' || board[x][y] == 'R'){
                            updateRook.add(new Pair<>(x, y));
                        }
                        if(board[x][y] == 'q' || board[x][y] == 'Q'){
//                        System.out.println("queen add for" + row + " " + col + " " + x + " " + y);

                            updateBishop.add(new Pair<>(x, y));
                            updateRook.add(new Pair<>(x, y));
                        }
                        break;
                    }
                    x += dir[0];
                    y += dir[1];
                }
            }
        }


//        System.out.println(2 + " " + row + " " + col + " " + newRow + " " + newCol);
//        System.out.println(3);

        for(Pair<Integer, Integer> pair : updateBishop){
            if(board[pair.getL()][pair.getR()] != ' ') {
                slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), -1);
            }
//            System.out.println(pair.getL() + " " + pair.getR());
        }

//        System.out.println(4);

        for(Pair<Integer, Integer> pair : updateRook){
            if(board[pair.getL()][pair.getR()] != ' ') {
                slidingMove(rookDirections, pair.getL(), pair.getR(), getColor(pair), -1);
            }
        }




//        System.out.println(5);

        filterPiece(board[newRow][newCol], newRow, newCol, -1);

//        System.out.println(6);

        board[row][col] = board[newRow][newCol];
        board[newRow][newCol] = piece;

        filterPiece(board[row][col], row, col, 1);
        filterPiece(board[newRow][newCol], newRow, newCol, 1);
//        System.out.println(7);





        for(Pair<Integer, Integer> pair : updateBishop){
            if(board[pair.getL()][pair.getR()] != ' '){
                slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), 1);
            }
        }

//        System.out.println(9);

        for(Pair<Integer, Integer> pair : updateRook){
            if(board[pair.getL()][pair.getR()] != ' ') {
                slidingMove(rookDirections, pair.getL(), pair.getR(), getColor(pair), 1);
            }
        }

    }



    private int getColor(Pair<Integer, Integer> pair){
        if(Character.isLowerCase(board[pair.getL()][pair.getR()]))
            return BLACK;
        return WHITE;
    }

    public void setupState(){
        this.whiteChecked = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                filterPiece(board[i][j], i, j, 1);
                if(board[i][j] == 'k') {
                    BKingX = i;
                    BKingY = j;
                } else if(board[i][j] == 'K'){
                    WKingX = i;
                    WKingY = j;
                }
            }
        }
        score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
//                System.out.println(score + " " + i + " " + j + " " + board[i][j]);
                score += getPieceValue(board[i][j]);
            }
        }
//        System.out.println("Score: " + score);
//        printBoard();

//
//        System.out.println("Setup");
//        for(int i = 0; i < 8; i++){
//            for(int j = 0; j < 8; j++){
//                System.out.print(whiteChecked[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("Setup");
//        for(int i = 0; i < 8; i++){
//            for(int j = 0; j < 8; j++){
//                System.out.print(blackChecked[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//        printBoard();
    }

    private void filterPiece(char c, int row, int col, int add){

        switch(c){
            case 'p':
                updateCheckedByPawn(row, col, BLACK, add);
                break;
            case 'P':
                updateCheckedByPawn(row, col, WHITE, add);
                break;
            case 'b':
                updateCheckedByBishop(row, col, BLACK, add);
                break;
            case 'B':
                updateCheckedByBishop(row, col, WHITE, add);
                break;
            case 'n':
                updateCheckedByKnight(row, col, BLACK, add);
                break;
            case 'N':
                updateCheckedByKnight(row, col, WHITE, add);
                break;
            case 'r':
                updateCheckedByRook(row, col, BLACK, add);
                break;
            case 'R':
                updateCheckedByRook(row, col, WHITE, add);
                break;
            case 'q':
                updateCheckedByQueen(row, col, BLACK, add);
                break;
            case 'Q':
                updateCheckedByQueen(row, col, WHITE, add);
                break;
            case 'k':
                updateCheckedByKing(row, col, BLACK, add);
                break;
            case 'K':
                updateCheckedByKing(row, col, WHITE, add);
                break;
            default:
                break;
        }
    }

    private boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void updateCheckedByPiece(int row, int col, int color, int add){
        if(!isWithinBoard(row, col)) return;
//        System.out.println("aaa");
        if(color == BLACK){
//            System.out.println("black add at" + row + " " +  col);
            this.blackChecked[row][col] += add;
        }
        else if(color == WHITE){
//            System.out.println("white add at" + row + " " +  col);
            this.whiteChecked[row][col] += add;
        }
//        else {
//            System.out.println("wrong color");
//            printBoard();
//            System.exit(0);
//        }

//            System.out.println("updated piece " + row + " " + col + " " + add);
//            if(color == WHITE) System.out.println("WHITE");
//            printBoard();


    }

    private void updateCheckedByPawn(int newRow, int newCol, int color, int add){
//        System.out.println("Pawn");
        int step;
        if(color == BLACK) step = 1;
        else step = -1;
        updateCheckedByPiece(newRow + step, newCol + 1, color, add);
        updateCheckedByPiece(newRow + step, newCol - 1, color, add);
    }

    private void updateCheckedByKnight(int newRow, int newCol, int color, int add){
//        System.out.println("Knight");
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int x = newRow + move[0];
            int y = newCol + move[1];
            if (isWithinBoard(x, y))
                updateCheckedByPiece(x, y, color, add);
        }
    }

    private void updateCheckedByBishop(int newRow, int newCol, int color, int add){
//        System.out.println("Bishop");
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        slidingMove(directions, newRow, newCol, color, add);
    }

    private void updateCheckedByRook(int newRow, int newCol, int color, int add){
//        System.out.println("Rook");
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        slidingMove(directions, newRow, newCol, color, add);
    }

    private void updateCheckedByQueen(int newRow, int newCol, int color, int add){
//        System.out.println("Queen");
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        slidingMove(directions, newRow, newCol, color, add);
    }

    private void updateCheckedByKing(int newRow, int newCol, int color, int add){
//        System.out.println("King");
        int[][] kingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        for (int[] move : kingMoves) {
            int x = newRow + move[0];
            int y = newCol + move[1];
            if (isWithinBoard(x, y))
                updateCheckedByPiece(x, y, color, add);
        }
    }

    private void slidingMove(int[][] directions, int row, int col, int color, int add){
//        System.out.println("sliding");
//        printBoard();
        for (int[] dir : directions) {
            int x = row + dir[0];
            int y = col + dir[1];
            while (isWithinBoard(x, y)) {
//                System.out.println(dir[0] + " " + dir[1] + " " + x + " " + y + " " + color);
                updateCheckedByPiece(x, y, color, add);
                if (board[x][y] != ' ') {
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }
//        printBoard();
    }

    public char[][] getBoard(){
        return this.board;
    }

    public char getIndex(int i, int j){
        return this.board[i][j];
    }

    public int[][] getWhiteChecked(){
        return this.whiteChecked;
    }

    public int[][] getBlackChecked(){
        return this.blackChecked;
    }

    public void queening(){
        for(int i = 0; i < 8; i++){
            if(board[7][i] == 'p') board[7][i] = 'q';
            if(board[0][i] == 'P') board[0][i] = 'Q';
        }
    }

    public int getScore(){
        return this.score;
    }

    public boolean validState(){

        if(color == WHITE && whiteChecked[BKingX][BKingY] != 0) return false;
        return true;
    }

    int calculatePosPoint(char c, int i, int j){
        if(c == ' ') return 0;
        if(c == 'r') return -rookPoint[7 - i][7 - j];
        if(c == 'R') return rookPoint[i][j];
        if(c == 'n') return -knightPoint[7 - i][7 - j];
        if(c == 'N') return knightPoint[i][j];
        if(c == 'b') return -bishopPoint[7 - i][7 - j];
        if(c == 'B') return bishopPoint[i][j];
        if(c == 'q') return -queenPoint[7 - i][7 - j];
        if(c == 'Q') return queenPoint[i][j];
        if(c == 'p') return -pawnPoint[7 - i][7 - j];
        if(c == 'P') return pawnPoint[i][j];
        if(endGame){
            if(c == 'k') return -kingEndPoint[7 - i][7 - j];
            if(c == 'K') return kingEndPoint[i][j];
        } else {
            if(c == 'k') return -kingMidPoint[7 - i][7 - j];
            if(c == 'K') return kingMidPoint[i][j];
        }
        return 0;
    }

    int getPieceValue(char piece) {
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

    private void changeColor(){
        if(this.color == WHITE) this.color = BLACK;
        else this.color = WHITE;
    }

    public int canCastle(){
        boolean castle1 = true, castle2 = true;
        if(!rook1Moved && !kingMoved && !castled && board[0][0] == 'r' && board[0][4] == 'k') {
            for (int i = 1; i <= 3; i++) {
                if (board[0][i] != ' ') castle1 = false;
            }
            for (int i = 2; i <= 4; i++) {
                if (whiteChecked[0][i] != 0) castle1 = false;
            }
            if (whiteChecked[0][0] != 0) castle1 = false;
        } else castle1 = false;
        if(!rook2Moved && !kingMoved && !castled && board[0][7] == 'r' && board[0][4] == 'k') {
            for (int i = 5; i <= 6; i++) {
                if (board[0][i] != ' ') castle2 = false;
            }
            for (int i = 4; i <= 7; i++) {
                if (whiteChecked[0][i] != 0) castle2 = false;
            }
        } else castle2 = false;
        if(castle1 && castle2) return 3;
        if(castle1) return 1;
        if(castle2) return 2;
        return 0;
    }

    public void goCastle(int row, int col, int newRow, int newCol){
        if(newCol - col < 0){
//            System.out.println("Castle1");
            filterPiece('k', 0, 4, -1);
            filterPiece('r', 0, 0, -1);
            score -= calculatePosPoint('k', 0, 4);
            score -= calculatePosPoint('r', 0, 0);
            board[0][0] = ' ';
            board[0][2] = 'k';
            board[0][3] = 'r';
            board[0][4] = ' ';
            filterPiece('k', 0, 2, 1);
            filterPiece('r', 0, 3, 1);
            score += calculatePosPoint('k', 0, 2);
            score += calculatePosPoint('r', 0, 3);
            castled = true;
            rook1Moved = true;
            kingMoved = true;
        } else {
//            System.out.println("Castle2");
            filterPiece('k', 0, 4, -1);
            filterPiece('r', 0, 7, -1);
            score -= calculatePosPoint('k', 0, 4);
            score -= calculatePosPoint('r', 0, 7);
            board[0][4] = ' ';
            board[0][5] = 'r';
            board[0][6] = 'k';
            board[0][7] = ' ';
            filterPiece('k', 0, 6, 1);
            filterPiece('r', 0, 5, 1);
            score += calculatePosPoint('k', 0, 6);
            score += calculatePosPoint('r', 0, 5);
            castled = true;
            rook2Moved = true;
            kingMoved = true;
        }
        score -= 100;
//        printBoard();
    }

    public void undoCastle(int row, int col, int newRow, int newCol){
        if(newCol - col < 0){
//            System.out.println("Undoc1");
            filterPiece('k', 0, 2, -1);
            filterPiece('r', 0, 3, -1);
            score -= calculatePosPoint('k', 0, 2);
            score -= calculatePosPoint('r', 0, 3);
            board[0][0] = 'r';
            board[0][2] = ' ';
            board[0][3] = ' ';
            board[0][4] = 'k';
            filterPiece('k', 0, 4, 1);
            filterPiece('r', 0, 0, 1);
            score += calculatePosPoint('k', 0, 4);
            score += calculatePosPoint('r', 0, 0);
        } else {
//            System.out.println("Undoc2");
            filterPiece('k', 0, 6, -1);
            filterPiece('r', 0, 5, -1);
            score -= calculatePosPoint('k', 0, 6);
            score -= calculatePosPoint('r', 0, 5);
            board[0][4] = 'k';
            board[0][5] = ' ';
            board[0][6] = ' ';
            board[0][7] = 'r';
            filterPiece('k', 0, 4, 1);
            filterPiece('r', 0, 7, 1);
            score += calculatePosPoint('k', 0, 4);
            score += calculatePosPoint('r', 0, 7);
        }
//        printBoard();
//        System.exit(0);
    }

    public boolean isWhite(){
        return this.color == WHITE;
    }

    public boolean kingCanMove(int row, int col, int newRow, int newCol){
        if(board[row][col] == 'k'){
            if(whiteChecked[newRow][newCol] != 0) return false;
        }
        if(board[row][col] == 'K'){
            if(blackChecked[newRow][newCol] != 0) return false;
        }
        return true;
    }

    public boolean isKingInCheck(int color) {
        if (color == BLACK) {
            return whiteChecked[BKingX][BKingY] != 0;
        } else {
            return blackChecked[WKingX][WKingY] != 0;
        }
    }


    public void printBoard() {
        if(validState()) System.out.println("valid state");
        else System.out.println("not valid");
        System.out.println(color == 1 ? "Black" : "White");
        System.out.println("Count: " + count);
        if(endGame) System.out.println("endgame");
        System.out.println("Whiteking: " + WKingX + " " + WKingY);
        System.out.println("Blackking: " + BKingX + " " + BKingY);
        System.out.println("Score: " + score);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(this.board[i][j] == ' ')
                    System.out.print("_ ");
                    else System.out.print(this.board[i][j] + " ");
            }
            System.out.print("        ");
            for (int j = 0; j < 8; j++) {
                if(this.blackChecked[i][j] == 0)
                    System.out.print("_ ");
                else System.out.print(this.blackChecked[i][j] + " ");
//                if(blackChecked[i][j] < 0) System.exit(0);
            }
            System.out.print("        ");for (int j = 0; j < 8; j++) {
                if(this.whiteChecked[i][j] == 0)
                    System.out.print("_ ");
                else System.out.print(this.whiteChecked[i][j] + " ");
            }
            System.out.print("        ");

            System.out.println();
        }
        System.out.println();
        System.out.println("=========================================");
        System.out.println();


//        for(int i = 0; i < 8; i++){
//            for(int j = 0; j < 8; j++){
//                if(blackChecked[i][j] < 0) System.exit(0);
//            }
//        }


    }

    @Override
    public int compareTo(State o) {
        return Integer.compare(this.score, o.score);
    }
}
