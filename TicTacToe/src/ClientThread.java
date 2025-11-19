import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread implements Runnable{

    /* 
     * Socket represents connection between server/ClientThread and client/user.
     * Each socket has both an input string and output string.
     */


    //arraylist of every instance of this class
    //static means it belongs to the class, NOT each obj of the class
    public static ArrayList<ClientThread> ClientThreads = new ArrayList<>();

    //the socket passed from the server class; used to establish connection between server and client
    private Socket mySocket;
    //used to read messages from client
    public BufferedReader myBufferedReader;
    //send data, specifically messages to client
    public BufferedWriter myBufferedWriter;
    //store usernames
    private String clientUsername;


    private Server myServer;

    //keeps track of how many threads there are
    //private int threadCounter;

    //if you go back to Server.java and see the ClientThread call, you can see
    //that we pass in a socket from there in startSocket()
    public ClientThread(Socket mySocket, Server myServer) {
        try {
            this.mySocket = mySocket;
            this.myServer = myServer;
            /*
             * In java, there are byte streams and character streams.
             * Here we want a character stream bcs we're sending messages.
             * Char streams end with the word writer
             * Byte streams end with the word stream
             * 
             * OutputstreamWriter is a char  stream, getOutputStream is a byte stream
             * We're wrapping our byte stream in a char string bcs we want to send chars
             */
            //this stream to send things.
            this.myBufferedWriter = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream()));

            //same process for BufferedReader. this stream to read things
            this.myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));

            //read line sent by client using bufferedReader
            this.clientUsername = myBufferedReader.readLine(); //this readline is essentially waiting on Client.java

            //add client to arraylist so they can be part of the chat
            //this represents a client handler object
            ClientThreads.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");

            if (ClientThreads.size() == 1) {
                broadcastMessage("SERVER: It's " + clientUsername + " turn!");
            }

        } catch (IOException e) {
            //this will close socket and all our streams
            closeEverything(mySocket, myBufferedReader, myBufferedWriter);
        }
    }

    public String getUsername() {
        return this.clientUsername;
    }
        
    public void makeMove(String inputSymbol, String locationPair) {

        GameManager game = myServer.getGame();
        switch (locationPair) {
            case "0,0":
                game.getBoard()[0][0] = inputSymbol;
                break;
            case "0,1":
                game.getBoard()[0][1] = inputSymbol;
                break;
            case "0,2":
                game.getBoard()[0][2] = inputSymbol;
                break;
            case "1,0":
                game.getBoard()[1][0] = inputSymbol;
                break;
            case "1,1":
                game.getBoard()[1][1] = inputSymbol;
                break;
            case "1,2":
                game.getBoard()[1][2] = inputSymbol;
                break;
            case "2,0":
                game.getBoard()[2][0] = inputSymbol;
                break;
            case "2,1":
                game.getBoard()[2][1] = inputSymbol;
                break;
            case "2,2":
                game.getBoard()[2][2] = inputSymbol;
                break;
        }
        
    }

    public Boolean checkWin() {
        GameManager game = myServer.getGame();

        for (int i = 0; i < 3; i++) {
            if ((game.getBoard()[i][0] == "X" && game.getBoard()[i][1] == "X" && game.getBoard()[i][2] == "X")
                || (game.getBoard()[i][0] == "Y" && game.getBoard()[i][1] == "Y" && game.getBoard()[i][2] == "Y")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if ((game.getBoard()[0][i] == "X" && game.getBoard()[1][i] == "X" && game.getBoard()[2][i] == "X")
                || (game.getBoard()[0][i] == "Y" && game.getBoard()[1][i] == "Y" && game.getBoard()[2][i] == "Y")) {
                return false;
            }
        }

                // Check diagonals
        if ((game.getBoard()[0][0] == "X" && game.getBoard()[1][1] == "X" && game.getBoard()[2][2] == "X")
                || (game.getBoard()[0][0] == "Y" && game.getBoard()[1][1] == "Y" && game.getBoard()[2][2] == "Y")) {
            return true;
        }
        if ((game.getBoard()[0][2] == "X" && game.getBoard()[1][1] == "X" && game.getBoard()[2][0] == "X")
                || (game.getBoard()[0][2] == "Y" && game.getBoard()[1][1] == "Y" && game.getBoard()[2][0] == "Y")) {
            return true;
        }

        return false;
    }

    /* 
     * Everything on this run method is run on a separate thread.
     * On separate threads, we want to listen for messages.
     */
    @Override
    public void run() {
        String messageFromClient;
        //want to listen to messages from client while there is an active connection with client
        while (mySocket.isConnected()) {
            try {
                //continously read messages from client
                messageFromClient = myBufferedReader.readLine();
                if (!(messageFromClient.equals("help"))) {

                    if (messageFromClient.contains("MOVE")) {

                        GameManager game = myServer.getGame();

                       /*
                        * These print the same thing:
                        broadcastMessage(game.getCurrentPlayer());
                        broadcastMessage(getUsername());

                        */                       

                        broadcastMessage(messageFromClient);
                        broadcastMessage(game.getCurrentPlayer() + " has decided to make a move!");
                        broadcastMessage("This is the current board: ");

                        //we use sleep to prevent overload here
                        for (String row : game.displayBoard()) {
                            broadcastMessage(row);
                            try { Thread.sleep(10); } catch (Exception e) {}
                        }

                        broadcastMessage("Now pick 2 numbers consecutively separated by a comma, like(1,2)");
                        String locationPair = myBufferedReader.readLine();

                        if (locationPair.matches("^[0-9],[ ]?[0-9]$")) {

                            broadcastMessage(locationPair);
                            broadcastMessage("Now pick a symbol (X or O)");
                            String inputSymbol = myBufferedReader.readLine();

                            if (inputSymbol.equalsIgnoreCase("X") || inputSymbol.equalsIgnoreCase("O")) {

                                broadcastMessage(inputSymbol);
                                makeMove(inputSymbol, locationPair);
                                broadcastMessage("The updated table is: ");

                                for (String row : game.displayBoard()) {
                                    broadcastMessage(row);
                                    try { Thread.sleep(10); } catch (Exception e) {}
                                }


                                if (checkWin()) {
                                    broadcastMessage(game.getCurrentPlayer() + " has won!");
                                    broadcastMessage("Goodbye!");
                                }
                                game.switchTurn();

                                //player 1 has the turn
                                //want to switch to player 2
                                //game.setCurrentPlayer(ClientThreads.get(1).getUsername());
                                broadcastMessage("It's " + game.getCurrentPlayer() + "'s turn now!");

                            }

                        }
                    } else {
                        broadcastMessage(messageFromClient);
                    }
                        
                    } 
                    else {
                        broadcastMessage(messageFromClient);
                    }
                    
                
                
            } catch (IOException e) {
                closeEverything(mySocket, myBufferedReader, myBufferedWriter);
                break; //when the client dc's, break out of this while loop
            }
        }
    }
    
    public void broadcastMessage(String messageToSend) {
        //myClientThread represents each client handler in arraylist through each iteration
        for (ClientThread myClientThread: ClientThreads) {
            try {
                //broadcast message to everyone except the user who sent it
                //if it doesn't equal, send msg to that client
                // if (!myClientThread.clientUsername.equals(clientUsername)) {
                    myClientThread.myBufferedWriter.write(messageToSend);
                    myClientThread.myBufferedWriter.newLine(); //newLine says im done waiting for data, no need to send any more data over for me
                    //flush bufferedWriter
                    //our messages probably won't be begin enough to fill the buffer, so we manually flush it
                    //basically send prematurely before buffer is actually full
                    myClientThread.myBufferedWriter.flush();
                //    }
            } catch (IOException e) {
                closeEverything(mySocket, myBufferedReader, myBufferedWriter);
            }
        }
    }

    //when a user Dc's, we're basically removing a ClientThread 
    public void removeClientThread() {
        //remove ClientThread from ArrayList
        //remove current client handler 
        ClientThreads.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    //when this is called, error occured or user left so we remove them
    public void closeEverything(Socket mySocket, BufferedReader myBufferedReader, 
    BufferedWriter myBufferedWriter) {
        removeClientThread();
        try {
            //done so that if we call the close method, we don't get a
            //nullpointer exception error
            if (myBufferedReader != null) {
                myBufferedReader.close();
            }

            if (myBufferedWriter != null) {
                myBufferedWriter.close();
            }

            //note: the underlying streams are closed when we close the wrappers for streams

            //closing a socket, will close its input and output streams
            if (mySocket != null) {
                mySocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}
