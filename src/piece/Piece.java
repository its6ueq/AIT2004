package piece;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import main.*;

import javax.imageio.ImageIO;

public class Piece {
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved = false;
    public Piece(int color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        x = getX(col);
        y = getY(row);
    }

    public BufferedImage getImage(String path) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getIndex(){
        for (int index = 0; index < GamePanel.simPieces.size(); index++){
            if (GamePanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }
    public void updatePosition() {
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
    public boolean isWithinBoard(int targetCol, int targetRow) {
        return (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7);
    }
    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece p : GamePanel.simPieces) {
            if (p.col == targetCol && p.row == targetRow && p != this) {
                return p;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) {
            return true;
        }
        else {
            if (hittingP.color != color) {
                return true;
            }
            else {
                hittingP = null;
            }
        }
        return false;
    }
    public boolean isSameSquare(int targetCol, int targetRow) {
        return (targetCol == col && targetRow == row);
    }
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        for (Piece p : GamePanel.simPieces) {
            if (p.col == targetCol && p.row > Math.min(targetRow, preRow) && p.row < Math.max(targetRow, preRow)) {
                return true;
            }
            if (p.row == targetRow && p.col > Math.min(targetCol, preCol) && p.col < Math.max(targetCol, preCol)) {
                return true;
            }
        }
        return false;
    }
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        if (targetRow < preRow) {
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(preCol - c);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(preCol - c);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow - diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if (targetRow > preRow) {
            for (int c = preCol - 1; c > targetCol; c--) {
                int diff = Math.abs(preCol - c);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }

            for (int c = preCol + 1; c < targetCol; c++) {
                int diff = Math.abs(preCol - c);
                for (Piece piece : GamePanel.simPieces) {
                    if (piece.col == c && piece.row == preRow + diff) {
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
