package piece;
import main.GamePanel;
import main.Type;

import static main.GamePanel.enpassantX;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;
        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-pawn");
            symbol = 'P';
        } else {
            image = getImage("/piece/b-pawn");
            symbol = 'p';
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            int moveValue;
            if (color == GamePanel.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }
            hittingP = getHittingP(targetCol, targetRow);
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
                return true;
            }
            if (((preRow == 1 && color == GamePanel.BLACK) || (preRow == 6 && color == GamePanel.WHITE)) && targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && !pieceIsOnStraightLine(targetCol, targetRow)) {
                return true;
            }
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color) {
                return true;
            }

            //en passant
            if (targetCol == enpassantX && targetRow == preRow + moveValue && hittingP == null && Math.abs(preCol - enpassantX) == 1) {
                return true;
            }
        }
        return false;
    }
}