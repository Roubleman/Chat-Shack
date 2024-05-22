import java.util.ArrayList;

class King extends KingOrKnight {
    private boolean SETTHISTOTRUEONLYIFDEBUGGING = false;
    private int castleShortDirection;
    private Board board;

    King(Board board, TilePosition pos, PieceColor pc) {
        super(board, pos, pc);
        castleShortDirection = board.orientationAsWhite() ? 1 : -1;
        this.board = board;
    }

    private ArrayList<TilePosition> castlePossibilities () {
        ArrayList<TilePosition> result = new ArrayList<>();
        TilePosition newKingPos = castlePossibility(true);
        if (newKingPos != null) result.add(newKingPos);
        newKingPos = castlePossibility(false);
        if (newKingPos != null) result.add(newKingPos);
        return result;
    }

    private TilePosition castlePossibility(boolean castleShort) {
        if (this.hasMoved() || SETTHISTOTRUEONLYIFDEBUGGING) return null;
        int distanceToRook = castleShort ? 3 : 4;
        int direction = castleShort ? castleShortDirection : - castleShortDirection;
        int kingX = getTilePosition().x;
        int kingY = getTilePosition().y;

        Tile rookTile = board.tile(p(kingX + distanceToRook*direction, kingY));
        Piece theRook = rookTile.getInhabitant();
        Tile nextToKingTile = board.tile(p(kingX + direction, kingY));
        TilePosition kingDestinationPosition = p(kingX + 2*direction, kingY);
        Tile kingDestinationTile = board.tile(kingDestinationPosition);
        Tile nextToRookIfLongCastle = board.tile(p(kingX + 3*direction, kingY));
        if (theRook != null && !theRook.hasMoved() && theRook.isMobile() &&
                nextToKingTile.isFree() && kingDestinationTile.isFree() &&
                (castleShort || nextToRookIfLongCastle.isFree()) &&
                nextToKingTile.isControlled() && kingDestinationTile.isControlled())
            return kingDestinationPosition;
        return null;
    }

    ArrayList<TilePosition> computeAttacks() {
        TilePosition[] displacements = {p(1, 1), p(1, 0), p(1, -1),
                p(0, 1), p(0, -1),
                p(-1, 1), p(-1, 0), p(-1, -1)};
        return super.computeAttacks(displacements);
    }

    @Override
    ArrayList<TilePosition> possibleDestinations() {
        ArrayList<TilePosition> result = computeAttacks();
        result.addAll(castlePossibilities());
        return result;
    }
}
