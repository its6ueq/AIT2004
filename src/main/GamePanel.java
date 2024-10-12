package main;

import piece.*;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.sqrt;
import static main.AI.*;


public class GamePanel extends JPanel implements Runnable {
    private static final int TimeLimit = 30;

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board ();
    Mouse mouse = new Mouse ();
    AI ai = new AI (this);
    char[][] current_board = new char[8][8];
    State currState;
    public static ArrayList<Piece> pieces = new ArrayList<Piece> ();
    public static ArrayList<Piece> simPieces = new ArrayList<> ();
    ArrayList<Piece> promoPieces = new ArrayList<> ();
    Piece activeP, checkingP;
    public static Piece castlingP;
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    long startA, stopA;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    public GamePanel(){
        setPreferredSize (new Dimension (WIDTH, HEIGHT));
        setBackground (Color.BLACK);

        addMouseMotionListener (mouse);
        addMouseListener (mouse);


        setPieces ();
        copyPieces (pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start ();
    }

    public void setPieces() {
        pieces.add (new Pawn (WHITE, 0, 6));
        pieces.add (new Pawn (WHITE, 1, 6));
        pieces.add (new Pawn (WHITE, 2, 6));
        pieces.add (new Pawn (WHITE, 3, 6));
        pieces.add (new Pawn (WHITE, 4, 6));
        pieces.add (new Pawn (WHITE, 5, 6));
        pieces.add (new Pawn (WHITE, 6, 6));
        pieces.add (new Pawn (WHITE, 7, 6));
        pieces.add (new Rook(WHITE, 0, 7));
        pieces.add (new Rook(WHITE, 7, 7));
        pieces.add (new Knight (WHITE, 1, 7));
        pieces.add (new Knight (WHITE, 6, 7));
        pieces.add (new Bishop (WHITE, 2, 7));
        pieces.add (new Bishop (WHITE, 5, 7));
        pieces.add (new Queen (WHITE, 3, 7));
        pieces.add (new King (WHITE, 4, 7));

        pieces.add (new Pawn (BLACK, 0, 1));
        pieces.add (new Pawn (BLACK, 1, 1));
        pieces.add (new Pawn (BLACK, 2, 1));
        pieces.add (new Pawn (BLACK, 3, 1));
        pieces.add (new Pawn (BLACK, 4, 1));
        pieces.add (new Pawn (BLACK, 5, 1));
        pieces.add (new Pawn (BLACK, 6, 1));
        pieces.add (new Pawn (BLACK, 7, 1));
        pieces.add (new Rook(BLACK, 0, 0));
        pieces.add (new Rook(BLACK, 7, 0));
        pieces.add (new Knight (BLACK, 1, 0));
        pieces.add (new Knight (BLACK, 6, 0));
        pieces.add (new Bishop (BLACK, 2, 0));
        pieces.add (new Bishop (BLACK, 5, 0));
        pieces.add (new Queen (BLACK, 3, 0));
        pieces.add (new King (BLACK, 4, 0));

        updateBoard();
        currState = new State(current_board);
        currState.setupState();
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear ();
        target.addAll (source);
    }

    @Override
    public void run () {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime ();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime ();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update ();
                repaint ();
                delta--;
            }
        }
    }

    private void update(){
        if (promotion) {
            promoting();
        }
        else {
            if (currentColor == BLACK) {
                aiMove();
                return;
            }
            if(mouse.pressed){
                //System.out.println ("mouse: " + mouse.x + " " + mouse.y);
                if(activeP == null){
                    for(Piece piece : simPieces){
                        if(piece.color == currentColor &&
                                piece.col == mouse.x/Board.SQUARE_SIZE &&
                                piece.row == mouse.y/Board.SQUARE_SIZE){
                            activeP = piece;
                        }
                    }
                }
                else {
                    simulate();
                }
            }

            else{
                if(activeP != null){
                    if(validSquare){
                        copyPieces (simPieces, pieces);
                        activeP.updatePosition ();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }
                        updateBoard();
                        if (isKingInCheck()) {

                        }
                        if (canPromote()) {
                            promotion = true;
                        }
                        else {
                            changePlayer();
                        }
                    }
                    else{
                        copyPieces (pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }

    }

    private void updateBoard(){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                current_board[i][j] = ' ';
            }
        }
        for (Piece p : pieces){
            current_board[p.row][p.col] = p.symbol;
        }
        System.out.println ("=========================================");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(current_board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void updateBoardWithBestMove(char[][] bestMove) {
        // Update the current_board array
        current_board = bestMove;

        // Clear the pieces list
        pieces.clear();

        // Iterate through the current_board array and update the pieces list
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char pieceChar = current_board[row][col];
                if (pieceChar != ' ') {
                    Piece piece = createPieceFromChar(pieceChar, row, col);
                    pieces.add(piece);
                }
            }
        }
        copyPieces(pieces, simPieces);

//        System.out.println("Updated Pieces:");
//        for (Piece piece : pieces) {
//            System.out.println(piece);
//        }
        System.out.println("Current Value: " + currState.getScore());
    }

    private Piece createPieceFromChar(char pieceChar, int row, int col) {
        int color = Character.isUpperCase(pieceChar) ? WHITE : BLACK;
        switch (Character.toLowerCase(pieceChar)) {
            case 'p': return new Pawn(color, col, row);
            case 'r': return new Rook(color, col, row);
            case 'n': return new Knight(color, col, row);
            case 'b': return new Bishop(color, col, row);
            case 'q': return new Queen(color, col, row);
            case 'k': return new King(color, col, row);
            default: throw new IllegalArgumentException("Invalid piece character: " + pieceChar);
        }
    }

    private void aiMove() {
        startA = System.currentTimeMillis();

        int bestMoveValue = Integer.MAX_VALUE;
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestMove = null, bestMoveFinal = null;

        currState = new State(current_board);
        int sumPoint =  calculateTotalMaterial();
        currState.endGame = sumPoint <= 1800;
        System.out.println("Start");

        int DEPTH = 0;
        Boolean check = true;
        while(check){
            DEPTH++;
            for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move : ai.getAllPossibleMoves(currState)) {
//            System.out.println("step"+ move.getL().getL() + " " + move.getL().getR());
                char tempPiece = currState.board[move.getR().getL()][move.getR().getR()];
                int tempScore = currState.score;
                Boolean tempCastled = currState.castled;
                Boolean tempKingMoved = currState.kingMoved;
                Boolean tempRook1Moved = currState.rook1Moved;
                Boolean tempRook2Moved = currState.rook2Moved;

                currState.goMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR());

                int moveValue = ai.alphaBetaMax(Integer.MIN_VALUE, Integer.MAX_VALUE, DEPTH - 1, currState);

                System.out.println("Move Value: " + moveValue);
//            if(moveValue == 2147483647) currState.printBoard();
//            System.out.println("Best Value: " + bestMoveValue);
                if (moveValue < bestMoveValue) {
                    bestMoveValue = moveValue;
                    bestMove = move;
                }

                currState.score = tempScore;
                currState.castled = tempCastled;
                currState.kingMoved = tempKingMoved;
                currState.rook1Moved = tempRook1Moved;
                currState.rook2Moved = tempRook2Moved;
                currState.undoMove(move.getL().getL(), move.getL().getR(), move.getR().getL(), move.getR().getR(), tempPiece);
                stopA = System.currentTimeMillis();
                if(stopA - startA > TimeLimit * 1000){
                    check = false;
                    break;
                }
//            bestMove.printBoard();
            }
            bestMoveFinal = bestMove;
            System.out.println("Depth: " + DEPTH + " with best Move Value: " + bestMoveValue);
        }

//        System.exit(0);
//        for (int i = 0; i < 8; i++){
//            for (int j = 0; j < 8; j++){
//                System.out.print(bestMove[i][j] + " ");
//            }
//            System.out.println();
//        }

        currState.goMove(bestMoveFinal.getL().getL(), bestMoveFinal.getL().getR(), bestMoveFinal.getR().getL(), bestMoveFinal.getR().getR());

        for(int i = 0; i < 8; i++){
            if(currState.board[7][i] == 'p') currState.board[7][i] = 'q';
        }

        currState.printBoard();
        //System.out.println("Best Move: " + bestMoveValue);
        updateBoardWithBestMove(currState.getBoard());
        updateBoard();
        changePlayer();





    }

    private int calculateTotalMaterial() {
        int totalMaterial = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = current_board[i][j];
                if (piece != ' ' && Character.toLowerCase(piece) != 'k') {
                    totalMaterial += Math.abs(currState.getPieceValue(piece));
                }
            }
        }
        System.out.println("total point: " + totalMaterial);
        return totalMaterial;
    }


    private void simulate(){
        canMove = false;
        validSquare = false;

        copyPieces (pieces, simPieces);
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol (activeP.x);
        activeP.row = activeP.getRow (activeP.y);

        if(activeP.canMove (activeP.col, activeP.row)){
            canMove = true;

            if(activeP.hittingP != null){
                simPieces.remove (activeP.hittingP.getIndex ());
                simPieces.remove(activeP.hittingP);
            }
            checkCastling();
            if (isIllegal(activeP) || opponentCanCaptureKing()) {
                return;
            }
            validSquare = true;
        }
    }
    private boolean isIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false);
        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }
    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) castlingP.col += 3;
            else if (castlingP.col == 7) castlingP.col -= 2;
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private boolean isKingInCheck() {
        Piece king = getKing(true);
        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        }
        checkingP = null;
        return false;
    }
    private Piece getKing(boolean opponent) {
        Piece king = null;
        for (Piece piece : simPieces) {
            if (piece.type == Type.KING) {
                if (opponent && piece.color != currentColor) {
                    king = piece;
                    break;
                }
                else if (!opponent && piece.color == currentColor) {
                    king = piece;
                    break;
                }
            }

        }
        return king;
    }
    private void changePlayer(){
        if (currentColor == WHITE){
            currentColor = BLACK;
        }
        else {
            currentColor = WHITE;
        }
        activeP = null;
    }
    private boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Bishop(currentColor, 9, 3));
                promoPieces.add(new Knight(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }
    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                            break;
                        default:
                            break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent (g);

        Graphics2D g2 = (Graphics2D) g;
        board.draw (g2);

        for(Piece p : simPieces){
            p.draw (g2);
        }

        if(activeP != null) {
            if (canMove) {
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.GRAY);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                    activeP.draw(g2);
                }
                else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                    activeP.draw(g2);
                }
            }
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.WHITE);

        if (promotion) {
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else {
            if (currentColor == WHITE) {
                g2.drawString("White's turn", 850, 250);
                g2.drawString("Time: ", 825, 550);
                g2.drawString(String.valueOf((double)(stopA - startA)/1000), 825, 650);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.drawString("The King", 840, 350);
                    g2.drawString("is in check!", 840, 450);
                }
            }
            else {
                g2.drawString("Black's turn", 850, 150);
                g2.drawString("Thinking", 850, 250);

                if (checkingP != null && checkingP.color == WHITE) {
                    g2.drawString("The King", 840, 350);
                    g2.drawString("is in check!", 840, 450);
                }
            }
        }
    }
}
