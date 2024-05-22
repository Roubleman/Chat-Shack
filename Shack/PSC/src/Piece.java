import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

abstract class Piece {
    private final Board board;
    private BufferedImage appearance;
    private final int SIZE;
    private Tile tile;
    private PieceColor color;
    private char designator;
       char getDesignator() {return designator;}

    PieceColor getColor () {return color;}

    private TilePosition pos;
       TilePosition getTilePosition() {return pos;}

    private DraggedPosition posWhenDragged;
       void setDraggedPosition(DraggedPosition pos) {posWhenDragged = pos;}

    private boolean hasMoved = false;
       boolean hasMoved() {return hasMoved;}

    private boolean isCurrentlyDragged = false;
       void makeDragged() {isCurrentlyDragged=true;}
       void makeNotDragged() {isCurrentlyDragged=false;}

    Piece (Board board, TilePosition pos, PieceColor color) {
        SIZE = board.TILESIZE;
        this.board = board;
        this.pos = pos;
        this.color = color;
        tile = board.tile(pos);
        tile.setInhabitant(this);
    }
    
    static BufferedImage getPieceImage (PieceColor color, char designator) {
         String name;
         BufferedImage image = null;
         name = (color == PieceColor.White) ? "W" : "B";
         name += designator;
         InputStream is = Main.class.getResourceAsStream("Pieces/"+name+".png");
           try {
             image = ImageIO.read(is);
         }
         catch (IOException e) {
             System.out.println("Failed loading piece image");
         }
         return image;
    }

    private void getImage() {
        appearance = getPieceImage(color, designator);
    }

    static Piece pieceFactory(Board board, TilePosition pos, PieceType type, PieceColor pc) {
           Piece newPiece = null;
           switch (type) {
                case KING: newPiece = new King(board,pos,pc); break;
                case PAWN: newPiece = new Pawn(board,pos,pc); break;
                case KNIGHT: newPiece = new Knight(board,pos,pc); break;
                case BISHOP: newPiece = new Bishop(board,pos,pc); break;
                case ROOK: newPiece = new Rook(board,pos,pc); break;
                case QUEEN: newPiece = new Queen(board,pos,pc); break;
           }
           newPiece.designator = type.designator;
           newPiece.getImage();
           return newPiece;
    }

    abstract ArrayList<TilePosition> computeAttacks();

    boolean isMine() {
        return (board.orientationAsWhite() ? PieceColor.White : PieceColor.Black) == color;
    }

    boolean isMobile() {
        if (isMine()) return !tile.isControlledByOpponent();
        return !tile.isControlled();
    }

    boolean isFree(TilePosition pos) {return board.isFree(pos);}

    boolean hasOpposingPiece(TilePosition pos) {return board.inhabitant(pos).isMine() != this.isMine();}

    boolean canBeCapturedEP(TilePosition pos) {
        if (!isLegitimateTarget(pos)) return false;
        Piece thePiece = board.inhabitant(pos);
        return thePiece instanceof Pawn && ((Pawn)thePiece).justMovedTwoSquares && !thePiece.isMobile();
    }

    boolean isLegitimateTarget(TilePosition pos) {return pos.isLegal() && (isFree(pos) || hasOpposingPiece(pos));}

    void moveTo(TilePosition pos) {
        this.pos = pos;
        tile.setInhabitant(null);
        tile = board.tile(pos);
        tile.setInhabitant(this);
        hasMoved = true;
    }

    ArrayList<TilePosition> possibleDestinations() {  // overridden by King for castle possibilities
        return computeAttacks();
    } // overridden by King

    boolean canGoTo(TilePosition pos) {
        return possibleDestinations().contains(pos) && board.isControlled(pos);
    }

    boolean canMoveAtAll() {
        if (!isMobile()) return false;
        for (TilePosition tilePosition : possibleDestinations()) {
            if (isMine() && board.isControlled(tilePosition)) return true;
            if (!isMine() && board.isControlledByOpponent(tilePosition)) return true;
        }
        return false;
    }

    void paint(Graphics g) {
        if (!isCurrentlyDragged) {
            g.drawImage(appearance, pos.x * SIZE, pos.y * SIZE, null);
        }
    }

    void paintWhileBeingDragged(Graphics g) {
        g.drawImage(appearance, posWhenDragged.x, posWhenDragged.y, null);
    }

    TilePosition p(int x, int y) {return new TilePosition(x,y);} // just an abbreviation used in subclasses

}

