import java.util.ArrayList;

abstract class LinePiece extends Piece {
    TilePosition[] bishopDirections =
            {p(1,1), p(1,-1), p(-1,1), p(-1,-1)};
    TilePosition[] rookDirections =
            {p(0,1), p(0,-1), p(-1,0), p(1,0)};

    LinePiece(Board board, TilePosition pos, PieceColor pc) {
        super(board, pos, pc);
    }

    abstract TilePosition[] getDirections();

    private ArrayList<TilePosition> attacksInOneDirection(TilePosition direction) {
        ArrayList<TilePosition> result = new ArrayList<TilePosition>();
        TilePosition nextPos = new TilePosition(getTilePosition().x, getTilePosition().y);
        while (true) {
            nextPos = nextPos.add(direction);
            if (!nextPos.isLegal()) break;
            if (!isFree(nextPos)) {
                if (hasOpposingPiece(nextPos)) result.add(nextPos);
                break;
            }
            else result.add(nextPos);
        }
        return result;
    }

    ArrayList<TilePosition> computeAttacks() {
        ArrayList<TilePosition> result = new ArrayList<TilePosition>();
        for (TilePosition direction: getDirections()) {
            result.addAll(attacksInOneDirection(direction));
        }
        return result;
    }
}
