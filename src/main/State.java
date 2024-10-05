package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static main.GamePanel.WHITE;
import static main.GamePanel.BLACK;

public class State {
    char[][] board;
    int[][] blackChecked;
    int[][] whiteChecked;

    int color;
    int WKingX, WKingY;
    int BKingX, BKingY;

    State parent;
    ArrayList<State> children;

    public State(char[][] board) {
        this.board = board;
        whiteChecked = new int[8][8];
        blackChecked = new int[8][8];
    }

    public State (State prevState, int row, int col, int newRow, int newCol, Boolean isWhite){
        parent = prevState;

        //copy Board
        board = new char[8][8];
        char[][] tempBoard = this.parent.getBoard();

        for(int i = 0; i < 8; i++){
            System.arraycopy(tempBoard[i], 0, board[i], 0, 8);
        }

        //copy King Pos
        this.WKingX = parent.WKingX;
        this.WKingY = parent.WKingY;
        this.BKingX = parent.BKingX;
        this.BKingY = parent.BKingY;

        //copy color
        if(isWhite){
            this.color = WHITE;
        } else {
            this.color = BLACK;
        }

        //copy white and black checked
        this.whiteChecked = new int[8][8];
        this.blackChecked = new int[8][8];

        int[][] prevWhiteChecked = prevState.getWhiteChecked();
        int[][] prevBlackChecked = prevState.getBlackChecked();

        for(int i = 0; i < 8; i++){
            System.arraycopy(prevWhiteChecked[i], 0, whiteChecked[i], 0, 8);
            System.arraycopy(prevBlackChecked[i], 0, blackChecked[i], 0, 8);
        }

        //update white and black checked
        updateBoard(row, col, newRow, newCol);

    }

    private void updateBoard(int row, int col, int newRow, int newCol){
//        System.out.println(1);
//        if(row == 2 && col == 0 && newRow == 1 && newCol == 1)
//            printBoard();

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

        if(board[newRow][newCol] != ' '){

        }

        char temp = board[row][col];



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

        if(row == WKingX && col == WKingY) {
            WKingX = newRow;
            WKingY = newCol;
        } else if (row == BKingX && col == BKingY) {
            BKingX = newRow;
            BKingY = newCol;
        }



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
//            System.out.println(board[pair.getL()][pair.getR()]);
        }

//        if(row == 0 && col == 0) {
//            printBoard();
//            System.out.println(row + " " + col + " " + newRow + " " + newCol);
//        }

//        if(row == 2 && col == 0 && newRow == 1 && newCol == 1)
//            printBoard();

//        System.out.println(10);
//        printBoard();

//        System.out.println("updateddd");



    }

    private int getColor(Pair<Integer, Integer> pair){
        if(Character.isLowerCase(board[pair.getL()][pair.getR()]))
            return BLACK;
        return WHITE;
    }

    public void setupState(){
        color = WHITE;
        this.whiteChecked = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                filterPiece(board[i][j], i, j, 1);
                if(board[i][j] == 'k'){
                    BKingX = i;
                    BKingY = j;
                } else if (board[i][j] == 'K'){
                    WKingX = i;
                    WKingY = j;
                }
            }
        }

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

    public boolean validState(){

        if(color == BLACK && whiteChecked[BKingX][BKingY] != 0) return false;
//        if(color == WHITE && blackChecked[WKingX][WKingY] != 0) return false;
        return true;
    }


    public void printBoard() {
        System.out.println(color == 1 ? "Black" : "White");
        System.out.println(whiteChecked[BKingX][BKingY]);
        System.out.println("Whiteking: " + WKingX + " " + WKingY);
        System.out.println("Blackking: " + BKingX + " " + BKingY);
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


        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(blackChecked[i][j] < 0) System.exit(0);
            }
        }
    }
}
