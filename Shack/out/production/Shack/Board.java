import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class Board extends JPanel  {
    static final int TILESIZE = 60;

    final private PieceColor orientation;
        boolean orientationAsWhite () {return orientation == PieceColor.White;}
    final private Tile [] [] tileTable = new Tile[8][8];
        Tile tile(TilePosition pos) {return tileTable[pos.x][pos.y];}
        Piece inhabitant (TilePosition pos) {return tile(pos).getInhabitant();}
        boolean isControlled(TilePosition pos){
        return tile(pos).isControlled();
    }
        boolean isControlledByOpponent(TilePosition pos){
        return tile(pos).isControlledByOpponent();
    }
        boolean isFree(TilePosition pos) {return tile(pos).isFree();}
    final private ArrayList<Tile> allTiles = new ArrayList();
    final private Player thePlayer;
        boolean getFogOfWar() {return thePlayer.getFogOfWar();}
    final private ComputeControl theComputeControl = new ComputeControl();
    final private MyMouseListener theMouseListener = new MyMouseListener();
    final private Semaphore lock = new Semaphore(1);
        private void waitForLock() {
        lock.acquireUninterruptibly();
    }
        private void releaseLock() {
        lock.release();
    }

    private Piece currentlyDraggedPiece;
        private boolean isDragging(){
            return currentlyDraggedPiece != null;
        }
    private King myKing, opponentKing;
    private Move opponentMove = null;
        private boolean opponentHasMoved() {return opponentMove != null;}
    private Move myMove = null;
        private boolean iHaveMoved() {return myMove != null;}
    private boolean listeningForMoves = false;

    Board(PieceColor myColor, Player thePlayer) {
        this.thePlayer = thePlayer;
        this.orientation = myColor;
        setPreferredSize(new Dimension(TILESIZE * 8, TILESIZE * 8));
        setUpControlShowToggle();
        setUpTiles();
        createStartPosition();
        //createAlternateStartPosition();   // for debugging purposes
        //createPos();
        //createAlternateStartPositionTestPromotions();
        recalculateControlValues();
        repaint();
        startGettingMoves();
    }

    private void setUpControlShowToggle(){
            addKeyListener(
                    new KeyAdapter() {
                        @Override
                        public void keyReleased(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_SPACE && !thePlayer.getFogOfWar()) {
                                for (Tile tile : allTiles) {
                                    tile.toggleShowControl();
                                }
                                repaint();
                            }
                        }
                    }
            );
    }

    private void setUpTiles() {
        for (int fileNumber = 0; fileNumber < 8; fileNumber++) {
            for (int rowNumber = 0; rowNumber < 8; rowNumber++) {
                Tile tile = new Tile(new TilePosition(fileNumber, rowNumber), tileName(fileNumber, rowNumber));
                tileTable[fileNumber][rowNumber] = tile;
                allTiles.add(tile);
            }
        }
    }

    private String tileName(int x, int y) {
        final int file = orientationAsWhite() ? x : 7-x;
        final int row = orientationAsWhite() ? y : 7-y;
        final String rows = "87654321";
        final String files = "abcdefgh";
        String name = String.valueOf(files.charAt(file))
                + rows.charAt(row);
        return name;
    }

    private void createStartPosition() {
        PieceColor opponentColor = orientation.opposite();
        PieceType[] initialRow = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        int kingFile = 4;
        if (! orientationAsWhite()) {
            initialRow[3] = PieceType.KING;
            initialRow[4] = PieceType.QUEEN;
            kingFile = 3;
        }
        int file = 0;
        for (PieceType type : initialRow) {
            Piece.pieceFactory(this, new TilePosition( file,0), type, opponentColor);
            Piece.pieceFactory(this, new TilePosition( file,1), PieceType.PAWN, opponentColor);
            Piece.pieceFactory(this, new TilePosition( file,6), PieceType.PAWN, orientation);
            Piece.pieceFactory(this, new TilePosition( file,7), type, orientation);
            file++;
        }
        myKing = (King) inhabitant(new TilePosition(kingFile,7));
        opponentKing = (King) inhabitant(new TilePosition(kingFile,0));
    }
////////////// for debugging purposes

    private void createAlternateStartPosition() {
        int kingFile = 7;
        if (! orientationAsWhite()) {
            kingFile = 0;
        }
        PieceColor opponentColor = orientation.opposite();
        Piece.pieceFactory(this, new TilePosition( kingFile,0), PieceType.KING, opponentColor);
        Piece.pieceFactory(this, new TilePosition( kingFile,7), PieceType.KING, orientation);
        myKing = (King) inhabitant(new TilePosition(kingFile,7));
        opponentKing = (King) inhabitant(new TilePosition(kingFile,0));
        Piece.pieceFactory(this, new TilePosition( 1,0), PieceType.PAWN, opponentColor);
        Piece.pieceFactory(this, new TilePosition( 6,7), PieceType.PAWN, orientation);
        }

    private void createAlternateStartPositionTestPromotions() {
        int kingFile = 4;
        int pawnFile = 1;
        if (! orientationAsWhite()) {
            kingFile = 3;
            pawnFile = 6;
        }
        PieceColor opponentColor = orientation.opposite();
        opponentKing = (King)Piece.pieceFactory(this, new TilePosition( kingFile,0), PieceType.KING, opponentColor);
        myKing = (King)Piece.pieceFactory(this, new TilePosition( kingFile,7), PieceType.KING, orientation);
        Piece.pieceFactory(this, new TilePosition( pawnFile,6), PieceType.PAWN, opponentColor);
        Piece.pieceFactory(this, new TilePosition( pawnFile,1), PieceType.PAWN, orientation);
    }

    private void createTestPos () {
        PieceColor opponentColor = orientation.opposite();
        TilePosition WKingPos = new TilePosition(2,6);
        TilePosition BKingPos = new TilePosition(0,7);
        TilePosition WQPos = new TilePosition(1,4);
        if (!orientationAsWhite()) {
            WKingPos = WKingPos.transpose();
            WQPos = WQPos.transpose();
            BKingPos = BKingPos.transpose();
            myKing = (King) Piece.pieceFactory(this, BKingPos,PieceType.KING, PieceColor.Black);
            opponentKing = (King) Piece.pieceFactory(this, WKingPos,PieceType.KING, PieceColor.White);
            Piece.pieceFactory (this, WQPos, PieceType.QUEEN, PieceColor.White);
        }
        else {
           myKing = (King) Piece.pieceFactory(this, WKingPos,PieceType.KING, PieceColor.White);
           opponentKing = (King) Piece.pieceFactory(this, BKingPos,PieceType.KING, PieceColor.Black);
           Piece.pieceFactory (this, WQPos, PieceType.QUEEN, PieceColor.White);
        }
    }

    ///////////////

    private void recalculateControlValues() {
        theComputeControl.computeControls();
    }

    void startGettingMoves() {
        if (!listeningForMoves) {
            addMouseListener(theMouseListener);
            addMouseMotionListener(theMouseListener);
            listeningForMoves = true;
        }
    }

    void stopGettingMoves() {
        removeMouseListener(theMouseListener);
        removeMouseMotionListener(theMouseListener);
        listeningForMoves = false;
    }

    void reactivate() {
        if (!iHaveMoved()) startGettingMoves();
    }

    private void removeCurrentlyDraggedPiece() {
        if (isDragging()) {
            currentlyDraggedPiece.makeNotDragged();
            currentlyDraggedPiece = null;
        }
    }

    private void record(Move move1, Move move2) {thePlayer.record(move1.twoMovesToString(move2));}

    void incomingMoveFromOpponent(Move theMove) {
        if (iHaveMoved()) {
            waitForLock();
            theMove.execute();
            record(myMove, theMove);
            myMove = null;
            recalculateControlValues();
            repaint();
            startGettingMoves();
            releaseLock();
        } else {
            opponentMove = theMove;
            thePlayer.startClock();
        }
    }

    void resetMoveMarks(TilePosition source, TilePosition target) {
        for (Tile tile : allTiles) {
            tile.eraseMarkForLastMove();
        }
        tile(source).markForLastMove();
        tile(target).markForLastMove();
    }

    void resetAllJustMovedTwoSquares(PieceColor color) {
        for (Tile tile : allTiles) {
            Piece thePiece = tile.getInhabitant();
            if (thePiece instanceof Pawn && color == thePiece.getColor()) {
                ((Pawn)thePiece).resetJustMovedTwoSquares();
            }
        }
    }

    private PieceType getPromotionType(TilePosition target) {
        PieceType promotionType = null;
        if (currentlyDraggedPiece instanceof Pawn && target.isFinalRank())
            promotionType = promotionChoice();
        return promotionType;
    }

    private PieceType promotionChoice() {
        final PieceType [] choices = {PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN};
        final int numberOfChoices = choices.length;
        final ImageIcon [] imageIcons = new ImageIcon[numberOfChoices];
        for (int possibleChoice=0; possibleChoice<numberOfChoices; possibleChoice++) {
            imageIcons [possibleChoice] = new ImageIcon(Piece.getPieceImage(orientation, choices[possibleChoice].designator));
        }
        int defaultChoice = 3;
        int selectedValue = JOptionPane.showOptionDialog(this,
                null, "Promotion", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(),
                imageIcons, imageIcons[defaultChoice]);
        if (selectedValue < 0) selectedValue = defaultChoice;
        return choices[selectedValue];
    }

    private void makeMyMove(TilePosition target) {
        if (currentlyDraggedPiece.canGoTo(target)) {
            PieceType promoteTo = getPromotionType(target);
            TilePosition source = currentlyDraggedPiece.getTilePosition();
            Move theMove = new Move(source, target, promoteTo ,this);
            thePlayer.sendMoveToOpponent(source.transpose(), target.transpose(), promoteTo);
            removeCurrentlyDraggedPiece();
            theMove.execute();
            if (opponentHasMoved()) {
                opponentMove.execute();
                record(theMove, opponentMove);
                opponentMove = null;
                thePlayer.stopClock();
                recalculateControlValues();
            }
            else {
                stopGettingMoves();
                myMove = theMove;
            }
        }
        else {
            removeCurrentlyDraggedPiece();
        }
        repaint();
    }

    void makeAllVisible() {
         for (Tile tile: allTiles) {
             tile.makeVisible();
         }
         repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Tile tile : allTiles) {
            tile.paint(g);
        }
        if (isDragging()) {
            currentlyDraggedPiece.paintWhileBeingDragged(g);
        }
        if (iHaveMoved()) {
            g.setColor( new Color(100,100,100, 100));
            g.fillRect(0,0, TILESIZE*8, TILESIZE*8);
        }
        requestFocus();
    }


    private class MyMouseListener implements MouseListener, MouseMotionListener {

        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {
            waitForLock();
            removeCurrentlyDraggedPiece();
            repaint();
            releaseLock();
        }

        public void mousePressed(MouseEvent mouseEvent) {
            waitForLock();
            Tile tile = getTile(mouseEvent);
            if (tile.hasMovablePiece()) {
                currentlyDraggedPiece = tile.getInhabitant();
                currentlyDraggedPiece.makeDragged();
                placeCurrentlyDraggedPiece(mouseEvent);
            }
            releaseLock();
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            waitForLock();
            if (isDragging()) {
                TilePosition target = getDraggedPosition(mouseEvent).tilePosition();
                makeMyMove(target);
            }
            releaseLock();
        }

        public void mouseDragged(MouseEvent mouseEvent) {
            waitForLock();
            placeCurrentlyDraggedPiece(mouseEvent);
            releaseLock();
        }

        private void placeCurrentlyDraggedPiece(MouseEvent mouseEvent) {
            if (isDragging()) {
                currentlyDraggedPiece.setDraggedPosition(
                        new DraggedPosition(mouseEvent.getX() - TILESIZE / 2,
                                            mouseEvent.getY() - TILESIZE / 2));
                repaint();
            }
        }

        private DraggedPosition getDraggedPosition (MouseEvent mouseEvent) {
            return new DraggedPosition(mouseEvent.getX(), mouseEvent.getY());
        }

        private Tile getTile(MouseEvent mouseEvent) {
            TilePosition pos = getDraggedPosition(mouseEvent).tilePosition();
            return tile(pos);
        }
    }


    private class ComputeControl {

        private boolean myKingIsAttacked, opponentKingIsAttacked;

        private void attackFrom(Tile tile) {
            if (!tile.isFree()) {
                Piece attackingPiece = tile.getInhabitant();
                ArrayList<TilePosition> attackedPositions = attackingPiece.computeAttacks();
                attackedPositions.add(attackingPiece.getTilePosition());  // attacking the square where it sits
                int increment = attackingPiece.isMine() ? 1 : -1;
                for (TilePosition attackedPos : attackedPositions) {
                        tile(attackedPos).addControl(increment);
                        if (tile(attackedPos).getInhabitant() instanceof King) {
                            if (attackingPiece.isMine() && !tile(attackedPos).getInhabitant().isMine()) opponentKingIsAttacked = true;
                            if (!attackingPiece.isMine() && tile(attackedPos).getInhabitant().isMine()) myKingIsAttacked = true;
                        }
                    if (attackingPiece.isMine()) tile(attackedPos).makeVisible();
                }
            }
        }

       private void computeControls() {
            myKingIsAttacked = false;
            opponentKingIsAttacked = false;
            for (Tile tile: allTiles) {
                tile.setZeroControl();
                if (thePlayer.getFogOfWar()) tile.makeInvisible();
                else tile.makeVisible();
            }
            for (Tile tile : allTiles) {
                attackFrom(tile);
            }
            appraise();
        }

        private boolean stalemated(boolean myPieces) {
            for (Tile tile : allTiles) {
                if (!tile.isFree()) {
                    Piece inhabitant = tile.getInhabitant();
                    if (inhabitant.isMine() == myPieces) {
                        if (inhabitant.canMoveAtAll()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void appraise() {
            boolean myKingLost = ! myKing.isMobile();
            boolean opponentKingLost = ! opponentKing.isMobile();
            boolean iAmStalemated = stalemated(true);
            boolean opponentIsStalemated = stalemated(false);
            boolean iWin = opponentKingLost || (opponentIsStalemated && opponentKingIsAttacked);
            boolean iLose = myKingLost || (iAmStalemated && myKingIsAttacked);
            boolean gameEnds = iWin || iLose || iAmStalemated || opponentIsStalemated;
            if (gameEnds) {
                makeAllVisible();
                removeCurrentlyDraggedPiece();
                repaint();
                if (iWin && iLose)
                    thePlayer.showDraw();
                else if (iLose)
                    thePlayer.showLose();
                else if (iWin)
                    thePlayer.showWin();
                else if (iAmStalemated || opponentIsStalemated)
                    thePlayer.showDraw();
            }
        }
    }
}


