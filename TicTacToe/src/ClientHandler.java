import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    /* 
     * Socket represents connection between server/clientHandler and client/user.
     * Each socket has both an input string and output string.
     */


    //arraylist of every instance of this class
    //static means it belongs to the class, NOT each obj of the class
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    //the socket passed from the server class; used to establish connection between server and client
    private Socket mySocket;
    //used to read messages from client
    public BufferedReader myBufferedReader;
    //send data, specifically messages to client
    public BufferedWriter myBufferedWriter;
    //store usernames
    private String clientUsername;

    //if you go back to Server.java and see the ClientHandler call, you can see
    //that we pass in a socket from there in startSocket()
    public ClientHandler(Socket mySocket) {
        try {
            this.mySocket = mySocket;
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
            clientHandlers.add(this);

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");

        } catch (IOException e) {
            //this will close socket and all our streams
            closeEverything(mySocket, myBufferedReader, myBufferedWriter);
        }
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
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(mySocket, myBufferedReader, myBufferedWriter);
                break; //when the client dc's, break out of this while loop
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        //myClientHandler represents each client handler in arraylist through each iteration
        for (ClientHandler myClientHandler: clientHandlers) {
            try {
                //broadcast message to everyone except the user who sent it
                //if it doesn't equal, send msg to that client
                if (!myClientHandler.clientUsername.equals(clientUsername)) {
                    myClientHandler.myBufferedWriter.write(messageToSend);
                    myClientHandler.myBufferedWriter.newLine(); //newLine says im done waiting for data, no need to send any more data over for me
                    //flush bufferedWriter
                    //our messages probably won't be begin enough to fill the buffer, so we manually flush it
                    //basically send prematurely before buffer is actually full
                    myClientHandler.myBufferedWriter.flush();
                    }
            } catch (IOException e) {
                closeEverything(mySocket, myBufferedReader, myBufferedWriter);
            }
        }
    }

    //when a user Dc's, we're basically removing a clientHandler 
    public void removeClientHandler() {
        //remove clientHandler from ArrayList
        //remove current client handler 
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    //when this is called, error occured or user left so we remove them
    public void closeEverything(Socket mySocket, BufferedReader myBufferedReader, 
    BufferedWriter myBufferedWriter) {
        removeClientHandler();
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
