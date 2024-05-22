import java.util.ArrayList;
class Pawn extends Piece{
    final int initialRow;

    Pawn(Board board, TilePosition pos, PieceColor pc) {
        super(board, pos, pc);
        initialRow = isMine() ? 6 : 1;
    }

    boolean justMovedTwoSquares = false;

    @Override
    void moveTo(TilePosition pos) {
        if (Math.abs(this.getTilePosition().y  - pos.y) > 1) justMovedTwoSquares = true;
        super.moveTo(pos);
    }

    void resetJustMovedTwoSquares () { justMovedTwoSquares = false;}

    ArrayList<TilePosition> computeAttacks() {
        final int row = getTilePosition().y;
        final int file = getTilePosition().x;
        final int rowInFront = isMine() ? row - 1 : row + 1;
        final int twoRowsInFront = isMine() ? row - 2 : row + 2;
        final ArrayList<TilePosition> result = new ArrayList();

        final TilePosition frontPos =      new TilePosition(file,       rowInFront);
        final TilePosition leftFrontPos =  new TilePosition(file - 1, rowInFront);
        final TilePosition rightFrontPos = new TilePosition(file + 1, rowInFront);
        final TilePosition leftSidePos =   new TilePosition(file - 1, row);
        final TilePosition rightSidePos =  new TilePosition(file + 1, row);
        final TilePosition twoInFrontPos = new TilePosition(file,        twoRowsInFront);

        if (isFree(frontPos)) result.add(frontPos);
        if (isLegitimateTarget(leftFrontPos) && !isFree(leftFrontPos)) result.add(leftFrontPos);
        if (isLegitimateTarget(rightFrontPos) && !isFree(rightFrontPos)) result.add(rightFrontPos);
        if (canBeCapturedEP(leftSidePos)) result.add(leftFrontPos);
        if (canBeCapturedEP(rightSidePos)) result.add(rightFrontPos);
        if (row == initialRow && isFree(twoInFrontPos) && isFree(frontPos)) result.add(twoInFrontPos);

        return result;
    }
}


