package piece;
import main.GamePanel;
import main.Type;

public class King extends Piece {
    public King (int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;
        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-king");
            symbol = 'K';
        }
        else {
            image = getImage("/piece/b-king");
            symbol = 'k';
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            if (Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
                    Math.abs (targetCol - preCol) * Math.abs (targetRow - preRow) == 1) {
                if(isValidSquare (targetCol, targetRow)){
                    return true;
                }
            }
            if (!moved) {
                if (targetCol == preCol + 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
                            GamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }
                if (targetCol == preCol - 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)) {
                    for (Piece piece : GamePanel.simPieces) {
                        Piece p[] = new Piece[2];
                        if (piece.col == preCol - 3 && piece.row == preRow && !piece.moved) {
                            p[0] = piece;
                        }
                        if (piece.col == preCol - 4 && piece.row == preRow && !piece.moved) {
                            p[1] = piece;
                        }
                        if (p[0] == null && p[1] != null && !p[1].moved) {
                            GamePanel.castlingP = p[1];
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
