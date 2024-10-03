package main;

import java.util.ArrayList;

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

        board = new char[8][8];
        char[][] tempBoard = this.parent.getBoard();

        for(int i = 0; i < 8; i++){
            System.arraycopy(tempBoard[i], 0, board[i], 0, 8);
        }


        this.WKingX = parent.WKingX;
        this.WKingY = parent.WKingY;
        this.BKingX = parent.BKingX;
        this.BKingY = parent.BKingY;
        if(isWhite){
            this.color = WHITE;
        } else {
            this.color = BLACK;
        }

        this.whiteChecked = new int[8][8];
        this.blackChecked = new int[8][8];

        int[][] prevWhiteChecked = prevState.getWhiteChecked();
        int[][] prevBlackChecked = prevState.getBlackChecked();

        for(int i = 0; i < 8; i++){
            System.arraycopy(prevWhiteChecked[i], 0, whiteChecked[i], 0, 8);
            System.arraycopy(prevBlackChecked[i], 0, blackChecked[i], 0, 8);
        }


        changeColor();

        updateBoard(row, col, newRow, newCol);

    }

    private void updateBoard(int row, int col, int newRow, int newCol){
        int[][] bishopDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] rockDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        ArrayList<Pair<Integer, Integer>> updateBishop = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> updateRock = new ArrayList<>();

        for (int[] dir : bishopDirections) {
            int x = row + dir[0];
            int y = col + dir[1];
            while (isWithinBoard(x, y)) {
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'b' || board[x][y] == 'B'){
                        updateBishop.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
                        updateBishop.add(new Pair<>(x, y));
                        updateRock.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        for (int[] dir : bishopDirections) {
            int x = newRow + dir[0];
            int y = newCol + dir[1];
            while (isWithinBoard(x, y)) {
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'b' || board[x][y] == 'B'){
                        updateBishop.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
                        updateBishop.add(new Pair<>(x, y));
                        updateRock.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

//        System.out.println("Bishop need to update: "+ updateBishop.size());
        for (int[] dir : rockDirections) {
            int x = row + dir[0];
            int y = col + dir[1];
            while (isWithinBoard(x, y)) {
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'r' || board[x][y] == 'R'){
                        updateRock.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
                        updateBishop.add(new Pair<>(x, y));
                        updateRock.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        for (int[] dir : rockDirections) {
            int x = newRow + dir[0];
            int y = newCol + dir[1];
            while (isWithinBoard(x, y)) {
                if (board[x][y] != ' ') {
                    if(board[x][y] == 'r' || board[x][y] == 'R'){
                        updateRock.add(new Pair<>(x, y));
                    }
                    if(board[x][y] == 'q' || board[x][y] == 'Q'){
                        updateBishop.add(new Pair<>(x, y));
                        updateRock.add(new Pair<>(x, y));
                    }
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }

        for(Pair<Integer, Integer> pair : updateBishop){
            slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), -1);
//            System.out.println(pair.getL() + " " + pair.getR());
        }

        for(Pair<Integer, Integer> pair : updateRock){
            slidingMove(rockDirections, pair.getL(), pair.getR(), getColor(pair), -1);
        }

        filterPiece(board[row][col], board, row, col, newRow, newCol);

        if(row == WKingX && col == WKingY) {
            WKingX = newRow;
            WKingY = newCol;
        } else if (row == BKingX && col == BKingY) {
            BKingX = newRow;
            BKingY = newCol;
        }


        board[newRow][newCol] = board[row][col];
        board[row][col] = ' ';

        for(Pair<Integer, Integer> pair : updateBishop){
            slidingMove(bishopDirections, pair.getL(), pair.getR(), getColor(pair), 1);
        }

        for(Pair<Integer, Integer> pair : updateRock){
            slidingMove(rockDirections, pair.getL(), pair.getR(), getColor(pair), 1);
        }

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
                filterPiece(board[i][j], null, 0, 0, i, j);
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

    private void filterPiece(char c, char[][] prevBoard, int row, int col, int newRow, int newCol ){

        switch(c){
            case 'p':
                updateCheckedByPawn(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'P':
                updateCheckedByPawn(prevBoard, row, col, newRow, newCol, WHITE);
                break;
            case 'b':
                updateCheckedByBishop(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'B':
                updateCheckedByBishop(prevBoard, row, col, newRow, newCol, WHITE);
                break;
            case 'n':
                updateCheckedByKnight(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'N':
                updateCheckedByKnight(prevBoard, row, col, newRow, newCol, WHITE);
                break;
            case 'r':
                updateCheckedByRock(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'R':
                updateCheckedByRock(prevBoard, row, col, newRow, newCol, WHITE);
                break;
            case 'q':
                updateCheckedByQueen(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'Q':
                updateCheckedByQueen(prevBoard, row, col, newRow, newCol, WHITE);
                break;
            case 'k':
                updateCheckedByKing(prevBoard, row, col, newRow, newCol, BLACK);
                break;
            case 'K':
                updateCheckedByKing(prevBoard, row, col, newRow, newCol, WHITE);
                break;
        }
    }

    private boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void updateCheckedByPiece(int row, int col, int color, int add){
        if(!isWithinBoard(row, col)) return;
//        System.out.println("aaa");
        if(color == BLACK) this.blackChecked[row][col] += add;
        else this.whiteChecked[row][col] += add;
//
//        System.out.println("updated piece " + row + " " + col + " " + add);
//        if(color == WHITE) System.out.println("WHITE");
//        printBoard();
//        if(this.blackChecked[row][col] < 0 || this.whiteChecked[row][col] < 0) System.exit(0);

    }

    private void updateCheckedByPawn(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("Pawn");
        int step;
        if(color == BLACK) step = 1;
        else step = -1;
        if(prevBoard != null){
            updateCheckedByPiece(row + step, col + 1, color, -1);
            updateCheckedByPiece(row + step, col - 1, color, -1);
        }
        updateCheckedByPiece(newRow + step, newCol + 1, color, 1);
        updateCheckedByPiece(newRow + step, newCol - 1, color, 1);
    }

    private void updateCheckedByKnight(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("Knight");
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        if(prevBoard != null) {
            for (int[] move : knightMoves) {
                int x = row + move[0];
                int y = col + move[1];
                if (isWithinBoard(x, y))
                    updateCheckedByPiece(x, y, color, -1);
            }
        }
        for (int[] move : knightMoves) {
            int x = newRow + move[0];
            int y = newCol + move[1];
            if (isWithinBoard(x, y))
                updateCheckedByPiece(x, y, color, 1);
        }
    }

    private void updateCheckedByBishop(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("Bishop");
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        if(prevBoard != null) {
            slidingMove(directions, row, col, color, -1);
        }
        slidingMove(directions, newRow, newCol, color, 1);
    }

    private void updateCheckedByRock(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("Rock");
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        if(prevBoard != null) {
            slidingMove(directions, row, col, color, -1);
        }
        slidingMove(directions, newRow, newCol, color, 1);
    }

    private void updateCheckedByQueen(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("Queen");
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        if(prevBoard != null) {
            slidingMove(directions, row, col, color, -1);
        }
        slidingMove(directions, newRow, newCol, color, 1);
    }

    private void updateCheckedByKing(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
//        System.out.println("King");
        int[][] kingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}, {0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        if(prevBoard != null) {
            for (int[] move : kingMoves) {
                int x = row + move[0];
                int y = col + move[1];
                if (isWithinBoard(x, y))
                    updateCheckedByPiece(x, y, color, -1);
            }
        }
        for (int[] move : kingMoves) {
            int x = newRow + move[0];
            int y = newCol + move[1];
            if (isWithinBoard(x, y))
                updateCheckedByPiece(x, y, color, 1);
        }
    }

    private void slidingMove(int[][] directions, int row, int col, int color, int add){
//        System.out.println("sliding");
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
            if(board[7][i] == 'P') board[0][i] = 'Q';
        }
    }

    public void changeColor(){
        if(this.color == WHITE) this.color = BLACK;
        else this.color = WHITE;
    }

    public boolean validState(){

//        if(color == BLACK && whiteChecked[BKingX][BKingY] != 0) return false;
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

    }
}
