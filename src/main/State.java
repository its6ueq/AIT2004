package main;

import static main.GamePanel.*;

public class State {
    char[][] board;
    int[][] blackChecked;
    int[][] whiteChecked;
    public State(char[][] board) {
        this.board = board;
        whiteChecked = new int[8][8];
        blackChecked = new int[8][8];
    }

    public State (char[][] prevBoard, int[][] prevWhiteChecked, int[][] prevBlackChecked, int row, int col, int newRow, int newCol){
        whiteChecked = new int[8][8];
        blackChecked = new int[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.arraycopy(prevWhiteChecked[i], 0, whiteChecked[i], 0, 8);
                System.arraycopy(prevBlackChecked[j], 0, blackChecked[j], 0, 8);
            }
        }
    }

    public void setupState(){
        this.whiteChecked = new int[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                filterPiece(board[i][j], null, 0, 0, i, j);
            }
        }

        System.out.println("Setup");
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print(whiteChecked[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Setup");
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print(blackChecked[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void filterPiece(char c, char[][] prevBoard, int row, int col, int newRow, int newCol ){
        switch(c){
            case 'p':
                updateCheckedByPamn(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'P':
                updateCheckedByPamn(null, 0, 0, newRow, newCol, WHITE);
                break;
            case 'b':
                updateCheckedByBishop(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'B':
                updateCheckedByBishop(null, 0, 0, newRow, newCol, WHITE);
                break;
            case 'n':
                updateCheckedByKnight(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'N':
                updateCheckedByKnight(null, 0, 0, newRow, newCol, WHITE);
                break;
            case 'r':
                updateCheckedByRock(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'R':
                updateCheckedByRock(null, 0, 0, newRow, newCol, WHITE);
                break;
            case 'q':
                updateCheckedByQueen(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'Q':
                updateCheckedByQueen(null, 0, 0, newRow, newCol, WHITE);
                break;
            case 'k':
                updateCheckedByKing(null, 0, 0, newRow, newCol, BLACK);
                break;
            case 'K':
                updateCheckedByKing(null, 0, 0, newRow, newCol, WHITE);
                break;
        }
    }

    private boolean isWithinBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void updateCheckedByPiece(int row, int col, int color, int add){
        if(!isWithinBoard(row, col)) return;
        if(color == BLACK) blackChecked[row][col] += add;
        else whiteChecked[row][col] += add;
    }

    private void updateCheckedByPamn(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
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
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        if(prevBoard != null) {
            slidingMove(directions, row, col, color, -1);
        }
        slidingMove(directions, newRow, newCol, color, 1);
    }

    private void updateCheckedByRock(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        if(prevBoard != null) {
            slidingMove(directions, row, col, color, -1);
        }
        slidingMove(directions, newRow, newCol, color, 1);
    }

    private void updateCheckedByQueen(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
        updateCheckedByRock(prevBoard, row, col, newRow, newCol, color);
        updateCheckedByBishop(prevBoard, row, col, newRow, newCol, color);
    }

    private void updateCheckedByKing(char[][] prevBoard, int row, int col, int newRow, int newCol, int color){
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
        for (int[] dir : directions) {
            int x = row + dir[0];
            int y = col + dir[1];
            while (isWithinBoard(x, y)) {
                System.out.println(dir[0] + " " + dir[1] + " " + x + " " + y + " " + color);
                updateCheckedByPiece(x, y, color, add);
                if (board[x][y] != ' ') {
                    break;
                }
                x += dir[0];
                y += dir[1];
            }
        }
    }
}
