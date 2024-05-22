import java.io.*;

public class Communicator {

    private ObjectInputStream inputFromOpponent;
    private ObjectOutputStream outputToOpponent;
    private final Player thePlayer;
    private MoveListener myMoveListener = new MoveListener();

    Communicator(ObjectInputStream is, ObjectOutputStream os, Player thePlayer) {
        this.thePlayer = thePlayer;
        this. inputFromOpponent = is;
        this.outputToOpponent = os;
    }

    PieceColor getColor() {
        try {
            PieceColor color = (PieceColor)inputFromOpponent.readObject();
            return color;
        }
        catch (Exception e) {
            e.printStackTrace();
            thePlayer.commError(" setting up colors");
        }
        return null;
    }

    void sendColor(PieceColor color) {
        try {
            outputToOpponent.writeObject(color);
        }
        catch (IOException e)  {
            System.out.println(e.getMessage());
            thePlayer.commError(" sending color");
        }
    }
    boolean getFogOfWar() {
        try {
            boolean fow = (boolean)inputFromOpponent.readObject();
            return fow;
        }
        catch (Exception e) {
            e.printStackTrace();
            thePlayer.commError(" getting fogOfWar");
        }
        return false;
    }

    void sendFogOfWar(boolean fow) {
        try {
            outputToOpponent.writeObject(fow);
        }
        catch (IOException e)  {
            System.out.println(e.getMessage());
            thePlayer.commError(" sending fogOfWar");
        }
    }


    void sendMove(Message message) {
        try {
            outputToOpponent.writeObject(message);
        }
        catch (IOException e)  {
            System.out.println(e.getMessage());
            thePlayer.commError(" sending move");
        }
    }

    void startToListenForMoves() {
        myMoveListener.start();
    }

    void stopListening() {myMoveListener.stopListening();}

    private class MoveListener extends Thread {
        boolean running = true;

        public void run() {
            Message message = null;
            while (running) {
                try {
                    try {
                        message = (Message) inputFromOpponent.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        running = false;
                    } catch (EOFException e) {
                        running = false;
                    }
                    if (running && message != null) {
                        thePlayer.getMessageFromOpponent(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    running = false;
                }
            }
        }

        void stopListening() {running = false;}
    }
}
