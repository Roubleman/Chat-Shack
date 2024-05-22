import java.awt.*;

class Tile {
    private final int SIZE;
    private final TilePosition pos;
    private final String name;
      String getName() {return name;}
    private Color color;

    private int controlValue;
      boolean isControlled() {return controlValue > 0;}
      boolean isControlledByOpponent(){return controlValue < 0;}
      void addControl (int increment) { controlValue += increment;}
      void setZeroControl() { controlValue = 0;}

    private boolean isVisible;
       void makeVisible() {isVisible = true;}
       void makeInvisible() {isVisible = false;}

    private boolean markedForLastMove = false;
       void markForLastMove() {
           markedForLastMove = true;}
       void eraseMarkForLastMove() {
           markedForLastMove = false;}

    private boolean showMyControls = true;
       void toggleShowControl() {showMyControls = ! showMyControls;}

    private Piece inhabitant;
        Piece getInhabitant() {return inhabitant;}
        void setInhabitant(Piece piece) {inhabitant = piece;}

    private final boolean isDark;
    private final static Color lightContrColor = new Color(255, 230, 204);
    private final static Color darkContrColor = new Color(153, 79, 0);
    private final static Color lightColor = new Color(166, 166, 166);
    private final static Color darkColor = new Color(102, 102, 102);
    private final static Color markColor = Color.BLUE;
    private final static Color alertColor = Color.RED;
    private final static int markWidth = 6;

    Tile(TilePosition pos, String name) {
        this.SIZE = Board.TILESIZE;
        this.pos = pos;
        this.name = name;
        this.isDark = (pos.x + pos.y) % 2 == 1;
    }

    boolean isFree() {
        return inhabitant == null;
    }

    boolean hasMovablePiece() {
        return !isFree() && !isControlledByOpponent() && inhabitant.isMine();
    }

    void paint(Graphics g) {
        if (!isVisible) return;
        if (isDark) color = darkColor;
        else color = lightColor;

        if ( (isControlled() && showMyControls) ||
                (isControlledByOpponent() &&  !showMyControls) ) {
            if (isDark) color = darkContrColor;
            else color = lightContrColor;
        }

        if  (inhabitant != null && !inhabitant.isMobile())
            color = alertColor;

        if (markedForLastMove) {
            g.setColor(markColor);
            g.fillRect(pos.x * SIZE, pos.y * SIZE, SIZE, SIZE);
            g.setColor(color);
            g.fillRect(pos.x * SIZE + markWidth, pos.y  * SIZE + markWidth,
                    SIZE - 2*markWidth, SIZE- 2*markWidth);
        }
        else {
            g.setColor(color);
            g.fillRect(pos.x * SIZE, pos.y * SIZE, SIZE, SIZE);
        }

        if (!isFree()) {
            inhabitant.paint(g);
        }
    }
}
