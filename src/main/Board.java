package main;

import java.awt.*;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 90;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public void draw(Graphics2D g2) {
        int c = 0;
        for(int row = 0; row < MAX_ROW; row++){
            for(int col = 0; col < MAX_COL; col++){
                if(c == 0) {
                    g2.setColor (new Color (210, 165, 125));
                    c = 1;
                }
                else {
                    g2.setColor (new Color(175, 115, 70));
                    c = 0;
                }
                g2.fillRect (col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
            if(c == 0) c = 1;
            else c = 0;
        }

        //draw column and row numbers
        for (int i = 0; i < MAX_COL; i++) {
            if (i % 2 == 0) g2.setColor (new Color (175, 115, 70));
            else g2.setColor (new Color (210, 165, 125));
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString ("" + (i + 1), 5, i * SQUARE_SIZE + 20);

            if (i % 2 == 0) g2.setColor (new Color (210, 165, 125));
            else g2.setColor (new Color (175, 115, 70));
            g2.drawString ("" + (char)('A' + i), (i + 1) * SQUARE_SIZE - 20, 8 * SQUARE_SIZE - 10);
        }
    }
}
