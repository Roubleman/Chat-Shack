class Move {
    private final TilePosition source, target;
    private final Board board;
    private final Piece thePiece;
    private final PieceType promoteTo;
    private final int castleShortDirection;
    String separator;

    Move(TilePosition source, TilePosition target, PieceType promoteTo, Board board)     {
        this.source = source;
        this.target = target;
        this.board = board;
        this.promoteTo = promoteTo;
        thePiece = board.inhabitant(source);
        castleShortDirection = board.orientationAsWhite() ? 1 : -1;
        separator = board.tile(target).isFree() ? "-" : "x";
    }

    private boolean isPromotion() {
        return promoteTo != null;
    }

    private void promotedPiece () {
        Piece.pieceFactory(board, target, promoteTo, thePiece.getColor());
    }

    private boolean isCastleShort() {
        return thePiece instanceof King &&
                source.x == target.x - 2*castleShortDirection;
    }

    private boolean isCastleLong() {
        return thePiece instanceof King &&
                source.x == target.x + 2*castleShortDirection;
    }

    private boolean isEnPassant() {
        return thePiece instanceof Pawn && target.x != thePiece.getTilePosition().x && board.isFree(target);
    }

    private void removePawnForEP () {
        Tile tile = board.tile(new TilePosition(target.x, thePiece.getTilePosition().y));
        separator = "x";
        tile.setInhabitant(null);
    }

    private void moveRookForCastle() {
        TilePosition posOfKing = source;
        int distanceToRook = isCastleShort() ? 3 : 4;
        int direction = isCastleShort()  ? castleShortDirection : - castleShortDirection;
        TilePosition rookPos = new TilePosition(posOfKing.x + distanceToRook*direction, posOfKing.y);
        TilePosition rookTargetPos = new TilePosition(posOfKing.x + direction, posOfKing.y);
        Rook theRook = (Rook) board.inhabitant(rookPos);
        theRook.moveTo(rookTargetPos);
    }

    void execute() {
        board.resetAllJustMovedTwoSquares(thePiece.getColor());
        if (isCastleShort() || isCastleLong()) {moveRookForCastle();}
        if (isEnPassant()) removePawnForEP();
        thePiece.moveTo(target);
        if (isPromotion()) {promotedPiece();}
        board.resetMoveMarks(source, target);
    }

    public String twoMovesToString(Move theMove) {
        String myMove = this.toString();
        String otherMove = theMove.toString();
        if (board.getFogOfWar()) otherMove = "-------";
        if (thePiece.getColor() == PieceColor.Black) {
            return otherMove + " "+myMove+"\n";
        }
        else return myMove+" "+otherMove+"\n";
    }

    public String toString() {
        if (isCastleShort()) {return "0-0   ";}
        if (isCastleLong()) {return "0-0-0 ";}

        String result = "";
        if (!(thePiece instanceof Pawn)) result += thePiece.getDesignator();
        result += board.tile(source).getName();
        result += separator;
        result += board.tile(target).getName();
        if (isPromotion()) result += promoteTo.designator;
        else if (thePiece instanceof Pawn) result += " ";
        return result;
    }
}
