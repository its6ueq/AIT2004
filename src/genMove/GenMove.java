package genMove;

import static main.GamePanel.current_board;

public class GenMove {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public int[] isWhiteChecked;
    public int[] isBlackChecked;
    public int[] simIsChecked;
    void setup(){
        isWhiteChecked = new int[65];
        isBlackChecked = new int[65];


    }

    void rockMove(int currColor, int preCol, int preRow, int currCol, int currRow){
        //if rock move, then delete all prev checked position, add new checked position
        rock (currColor, preCol, preRow, -1);
        rock (currColor, currCol, currRow, 1);
    }

    void rock(int currColor, int col, int row, int num){
        if(currColor == WHITE) System.arraycopy(isBlackChecked, 0, simIsChecked, 0, 65);
        if(currColor == BLACK) System.arraycopy(isWhiteChecked, 0, simIsChecked, 0, 65);
        for(int i = col; i < 8; i++){ //right
            simIsChecked[i * 8 + row] += num;
            if(current_board[i][row] != '0') break;
        }
        for(int i = col; i >= 0; i--){ //left
            simIsChecked[i * 8 + row] += num;
            if(current_board[i][row] != '0') break;
        }
        for(int i = row; i < 8; i++){ //up
            simIsChecked[col * 8 + i] += num;
            if(current_board[col][i] != '0') break;
        }
        for(int i = row; i >= 0; i--){ //down
            simIsChecked[col * 8 + i] += num;
            if(current_board[col][i] != '0') break;
        }
        if(currColor == WHITE) System.arraycopy(simIsChecked, 0, isBlackChecked, 0, 65);
        if(currColor == BLACK) System.arraycopy(simIsChecked, 0, isWhiteChecked, 0, 65);
    }
}
