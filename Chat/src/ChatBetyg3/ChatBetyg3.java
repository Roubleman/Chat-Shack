package ChatBetyg3;

import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

/**
 * Chatprojekt - Karl Johansson, Pontus Fredstam
 * December 2022
 * Betyg 3
 */


public class ChatBetyg3 {

    public static void main(String[] args) {
        final int port = 4000;
        System.out.println("Starting Client");
        try {
            new Client(port);
        }
        catch (IOException e) {
            System.out.println("Client failed, Hosting Server instead");
            new Server(port);
        }
    }
}
class Client {

    public Client(int port) throws IOException {
        try{
            Socket socket = new Socket( InetAddress.getLocalHost().getHostAddress() , port );
            new ChatParticipant(socket);

        }
        catch (IOException e) {
            throw new IOException("Client not working");
        }


    }


}

class Server {

    public Server(int port) {
        try{
            ServerSocket myServer = new ServerSocket(port);
            Socket socket = myServer.accept();
            myServer.close();
           new ChatParticipant(socket);
        }
        catch (IOException e) {
            System.out.println("Funkar inte i Server");
        }
    }



}

class ChatParticipant extends JPanel implements KeyListener, ObjectStreamListener {

   JFrame frame;

   ObjectOutputStream myObjectOutputStream;
   ObjectInputStream myObjectInputStream;
   InputStream myInputStream;
   OutputStream myOutputStream;
   Socket mySocket;

   JTextArea textArea;
   JTextField textField;
   final String EXITMESSAGE = "Exit_Secret_Code_Message_For_JDialog-alsödjasdjpoqweiaäsölczäölcq+¨¨eoqwaklsnf+120eiqsdnlac@@£< aslödkasöd wa öekwqoke qwöas'åfalfåqkr´´2i1+0jdaoödk +102ej dåqaskfc+02rå2jk1dwqxpo 12k+r0opdj0+ k10+24i1k312+0rjpo12+04årjowgnorsjlfknmo+jr3f20pi2 ";
   final String EXITMESSAGE2 = "Exit_Secret_Code_Message_For_JDialog-alsödjasdjpoqweiaäsölczäölcq+¨¨eoqwaklsnf+120eiqsdnlac@@£< aslödkasöd wa öekwqoke qwöas'åfalfåqkr SECRETMESSAGE2 ´´2i1+0jdaoödk +102ej dåqaskfc+02rå2jk1dwqxpo 12k+r0opdj0+ k10+24i1k312+0rjpo12+04årjowgnorsjlfknmo+jr3f20pi2";


      ChatParticipant(Socket socket){
          createChatFrame();
          mySocket = socket;

          try {

               myOutputStream = socket.getOutputStream();

               myInputStream = socket.getInputStream();

               myObjectOutputStream = new ObjectOutputStream(myOutputStream);

               myObjectInputStream = new ObjectInputStream(myInputStream);

              new ObjectStreamManager(0, myObjectInputStream, this);
          }
          catch (IOException e){
              System.out.println("ChatParticipant is not working");
          }


    }

    int fivehundred = 500;
    public void createChatFrame(){
          setPreferredSize(new Dimension(fivehundred, fivehundred));
          frame = new JFrame("Chat");
          frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
          frame.setAlwaysOnTop(true);

          JButton exitbutton = new JButton("Exit Chat");

          frame.setLayout((new BorderLayout()));

          textField = new JTextField(25);
          textArea = new JTextArea(25, 25);
          textArea.setEditable(false);
          textArea.setLineWrap(true);

          frame.add(textField, BorderLayout.SOUTH);
          frame.add(textArea, BorderLayout.CENTER);
          frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

          textField.setFocusable(true);
          textField.addKeyListener(new KeyListener() {
              @Override
              public void keyTyped(KeyEvent e) {
                  if (e.getKeyChar() == KeyEvent.VK_ENTER){
                      send(textField.getText());
                      display(textField.getText(), you);
                      textField.setText("");
                  }
              }

              @Override
              public void keyPressed(KeyEvent e) {
                  if (e.getKeyCode() == KeyEvent.VK_SHIFT && e.getKeyCode() == KeyEvent.VK_BACK_SPACE){ // Shift och Backspace

                      String placeHolder = "";

                     int removeIndex = textField.getText().lastIndexOf(" ");

                      for (int i = 0; i == textField.getText().length(); i++) {
                          if (i == removeIndex) {
                              break;
                          } else {
                              char temp = textField.getText().charAt(i);
                              placeHolder += temp;
                          }
                      }
                      textField.setText(placeHolder);
                  }
              }

              @Override
              public void keyReleased(KeyEvent e) {

              }
          } );

          frame.add(exitbutton, BorderLayout.NORTH);
          exitbutton.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                  send(EXITMESSAGE);
                }
             } );

          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
    }

    boolean stringCheck(String message){
         String messageClone = message;
          messageClone.replace(" ", "");
          return (messageClone.length() == 0);
    }

    void send(String message){
          if (!stringCheck(message)){
             try {
                 myObjectOutputStream.writeObject(message);
             } catch (IOException e){
                 System.out.println("Sending the message did not work");
             }
          }
    }

    boolean them = true;
    boolean you = false;
    void display(String message, boolean youOrThem){
        if (youOrThem == them){
            textArea.append("Them:\n"+message+"\n\n");
        }
        else{
            textArea.append("You:\n"+message+"\n\n");
        }
    }

    int countDown = 5;
    int thousand = 1000;

    void exitDialog(){
        textField.setEditable(false);
        textField.setText("");

        JDialog exitMessage = new JDialog(frame, "Exit Message");

        exitMessage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        exitMessage.setAlwaysOnTop(true);

        JTextArea alone = new JTextArea("A chatter has left." +
                                        "\nLet everything be and the chat will shutdown on its own!");
        alone.setEditable(false);
        exitMessage.add(alone);

        exitMessage.setSize(new Dimension(400, 100));
        exitMessage.setResizable(false);
        exitMessage.setLocationRelativeTo(null);
        exitMessage.setVisible(true);
        send(EXITMESSAGE2);
        try{
        myOutputStream.close();
        myObjectOutputStream.close();
        myInputStream.close();
        myObjectInputStream.close();
        mySocket.close();
        Timer closingTimer = new Timer(thousand, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitMessage.setTitle("Closing in  " + countDown + "  seconds....");
                countDown--;
                if (countDown == -1){
                    frame.dispose();
                }
            }
        });
        closingTimer.setInitialDelay(thousand*3);
        closingTimer.start();
        }
        catch(IOException e){
            System.out.println("Closing the chat resulted in the following problem:\n" + e.getMessage());
        }
    }


    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        if (exception == null){
               String recievedMessage = (String) object;
               if (recievedMessage.equals(EXITMESSAGE)){
                   exitDialog();
               } else if (recievedMessage.equals(EXITMESSAGE2)) {
                   try{
                       myOutputStream.close();
                       myObjectOutputStream.close();
                       myInputStream.close();
                       myObjectInputStream.close();
                       mySocket.close();
                       frame.dispose();
                   }
                   catch(IOException e){
                       System.out.println("Closing the chat resulted in the following problem:\n" + e.getMessage());
                   }
               } else {
                   display(recievedMessage, them);

               }
        }
         else {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}












/**
 * @author joachimparrow 2010
 * This is to read from an input stream in a separate thread, and call a callback
 * when something arrives.
 **/
class ObjectStreamManager {
    private final ObjectInputStream    theStream;
    private final ObjectStreamListener theListener;
    private final int                  theNumber;
    private volatile boolean           stopped = false;

    /**
     *
     *  This creates and starts a stream manager for a stream. The manager
     * will continually read from the stream and forward objects through the
     * objectReceived() method of the ObjectStreamListener parameter

     *
     * @param   number   The number you give to the manager. It will be included in
     * all calls to readObject. That way you can have the same callback serving several managers
     * since for each received object you get the identity of the manager.
     * @param stream The stream on which the manager should listen.
     * @param listener The object that has the callback objectReceived()
     *
     *
     */
    public ObjectStreamManager(int number, ObjectInputStream stream, ObjectStreamListener listener) {
        theNumber =   number;
        theStream =   stream;
        theListener = listener;
        new InnerListener().start();  // start to listen on a new thread.
    }

    // This private method accepts an object/exception pair and forwards them
    // to the callback, including also the manager number. The forwarding is scheduled
    // on the Swing thread through an anonymous inner class.

    private void callback(final Object object, final Exception exception) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        if (!stopped) {
                            theListener.objectReceived(theNumber, object, exception);
                            if (exception != null) closeManager();
                        }
                    }
                });
    }

    // This is where the actual reading takes place.
    private class InnerListener extends Thread {

        @Override
        public void run() {
            while (!stopped) {                            // as long as no one stopped me
                try {
                    callback(theStream.readObject(), null); // read an object and forward it
                } catch (Exception e) {                 // if Exception then forward it
                    callback(null, e);
                }
            }
            try {                   // I have been stopped: close stream
                theStream.close();
            } catch (IOException e) {
            }

        }
    }

    /**
     * Stop the manager and close the stream.
     **/
    public void closeManager() {
        stopped = true;
    }
}      // end of ObjectStreamManager

/**
 *
 * @author joachimparrow

 * This is the interface for the listener. It must have a method
 * objectReceived. Whenever reading from the stream results in an object
 * being read or exception being thrown, the object or exception is
 * forwarded to the listener through objectReceived().
 *
 *
 */
interface ObjectStreamListener {
    /**
     * This method is called whenever an object is received or an exception is thrown.
     * @param number    The number of the manager as defined in its constructor
     * @param object  The object received on the stream
     * @param exception     The exception thrown when reading from the stream.
     *          Can be IOException or ClassNotFoundException.
     *          One of name and exception will always be null.
     *          In case of an exception the manager and stream are closed.
     **/
    public void objectReceived(int number, Object object, Exception exception);
}
