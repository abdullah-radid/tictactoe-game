import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

//similar to ClientHandler.
public class Client {
    private Socket mySocket;
    private BufferedReader myBufferedReader;
    private BufferedWriter myBufferedWriter;
    private String clientUsername;

    public Client(Socket mySocket, String clientUsername) {
        try {
            this.mySocket = mySocket;
            this.myBufferedWriter = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream()));
            this.myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            this.clientUsername = clientUsername;

        } catch (IOException e) {
            closeEverything(mySocket, myBufferedReader, myBufferedWriter);
        }
    }

    //used to send messages to clientHandler 
    //(which is basically the connection the server has spawned to handle clients)
    public void sendMessage() {
        try {
            //client handler needs to identify us
            myBufferedWriter.write(clientUsername);
            myBufferedWriter.newLine();
            myBufferedWriter.flush();

            Scanner myScanner = new Scanner(System.in);
            while (mySocket.isConnected()) {
                /* 
                 * While the socket is connected, we will get what the user types into the console
                 * and then send it over
                 */
                String messageToSend = myScanner.nextLine();
                //then use buffered writer to write this over
                myBufferedWriter.write(clientUsername + ": " + messageToSend);
                myBufferedWriter.newLine();
                myBufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(mySocket, myBufferedReader, myBufferedWriter);
        }
    }
    

    /*
     * Concurrency (single core) vs Parallel Execution (multi core)
     * Concurrency - running two or more programs in overlapping time phases
     * (At any given time, there is only 1 process in execution)
     * 
     * Parallel execution - tasks performed by a process are broken down into sub-parts
     * and mutiple CPUs execute each sub-task at the same time
     * - At any given time, all processes are being executed
     * - Found in systems having multicore processes
     */
     
    //listening out for messages that have been broadcasted by users
    public void listenForMessage() {
        //create a new thread and pass a runnable object instead of implementing runnable
        new Thread(new Runnable() {
            @Override
            public void run() {
                String MsgFromGroupchat;

                //we only want to do this while still connected to the server
                while (mySocket.isConnected()) {
                    try {
                        MsgFromGroupchat = myBufferedReader.readLine(); //read the broadcasted msg
                        System.out.println(MsgFromGroupchat); //output what was sent from the server
                    } catch (IOException e) {
                        closeEverything(mySocket, myBufferedReader, myBufferedWriter);
                    }
                }
            }
        }).start(); //have to call start method on thread obj
    }

    public void closeEverything(Socket mySocket, BufferedReader myBufferedReader, 
    BufferedWriter myBufferedWriter) {
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

    public static void main(String[] args) throws IOException{
        //System.in bcs we're taking input from keyboard
        Scanner myScanner = new Scanner(System.in);

        System.out.print("Enter your username for the groupchat:");
        String clientUsername = myScanner.nextLine();
        //Server is listening on port 1234, client needs to make a connection to it
        Socket socket = new Socket("localhost", 1234); //we're using localhost bcs its on a local machine
        
        Client clientObj = new Client(socket, clientUsername);
        //run our 2 methods to listen to messages and send messages
        //note: these are separate threads, so they may run concurrently
        clientObj.listenForMessage();
        clientObj.sendMessage();
    }


}

