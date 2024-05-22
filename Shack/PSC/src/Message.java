import java.io.Serializable;

class Message implements Serializable {
    TilePosition source;
    TilePosition target;
    PieceType promotionType;
    boolean offerDraw, acceptDraw, rejectDraw, resign, loseOnTime;
    String time;

    Message(TilePosition source, TilePosition target, PieceType promotionType) {
        this.source = source;
        this.target = target;
        this.promotionType = promotionType;
    }

    Message () {};

    Message(String time) {
        this.time = time;
    }

    void offerDraw() {
        offerDraw = true;
    }

    void acceptDraw() {
        acceptDraw = true;
    }

    void rejectDraw() {
        rejectDraw = true;
    }

    void resign() {
        resign = true;
    }

    void loseOnTime () {loseOnTime = true;}

    public String toString() { // for debugging
        String result = "from "+source+" to "+target;
        return result;
    }
}
