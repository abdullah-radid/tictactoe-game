import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

//similar to clientThread.
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

    public void makeMove(String inputSymbol, String locationPair) {

        switch (locationPair) {
            case "0,0":
                
            case "0,1":
                System.out.println();
            case "0,2":
                System.out.println();
            case "1,0":
                System.out.println();
            case "1,1":
                System.out.println();
            case "1,2":
                System.out.println();
            case "2,0":
                System.out.println();
            case "2,1":
                System.out.println();
            case "2,2":
                
        }
        
    }

    //used to send messages to clientThread 
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

                 
                System.out.print("> ");
                String messageToSend = myScanner.nextLine();

                if (messageToSend.equals("help")) {
                    StringBuilder helpMessage = new StringBuilder()
                        .append("Welcome to TicTacToe! In this game you are matched against another player. \n")
                        .append("The two of you will take turns inputting your moves! \n")
                        .append("There is a real time chat function as well, allowing you and your opponent to talk in real time. \n")
                        .append("To enter a chat message, simply typing anything after the \">\" will suffice. \n")
                        .append("In order to input a move on your turn, please wait for the message stating \"It's your turn!\" \n")
                        .append("Then, enter the command \"MOVE\" \n")
                        .append("In the MOVE Menu, please enter two numbers separated by a comma to select move location. \n")
                        .append("For example, I type \"1,2\" if I want to change the board at position [1][2]. (Note that the boards starts from [0][0] and ends at [3][3])")
                        .append("Then, select the symbol you want to input. Either \"O\" or \"X\" (both letters) \n")
                        .append("After selecting the symbol, your turn will be done! Wait for the game to update to proceed. \n");
                        System.out.print(helpMessage);
                }


                else {

                //then use buffered writer to write this over

                if (messageToSend.equalsIgnoreCase("MOVE")) {
                    myBufferedWriter.write("MOVE");

                }

                else if (messageToSend.matches("^[0-9],[ ]?[0-9]$")) {
                    myBufferedWriter.write(messageToSend);              
                }

                else if (messageToSend.equalsIgnoreCase("X") || 
                    messageToSend.equalsIgnoreCase("O")) {
                    myBufferedWriter.write(messageToSend);
                }

                else {
                    myBufferedWriter.write(clientUsername + ": " + messageToSend);
                }

                myBufferedWriter.newLine();
                myBufferedWriter.flush();
                }
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
                        System.out.print("> ");
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

        System.out.print("Enter your username for the groupchat: ");
        String clientUsername = myScanner.nextLine();
        System.out.println("Welcome to the game, " + clientUsername +"! For a list of instructions, please type \"help\"");

        //Server is listening on port 1234, client needs to make a connection to it
        Socket socket = new Socket("localhost", 1234); //we're using localhost bcs its on a local machine
        
        Client clientObj = new Client(socket, clientUsername);
        //run our 2 methods to listen to messages and send messages
        //note: these are separate threads, so they may run concurrently
        clientObj.listenForMessage();
        clientObj.sendMessage();
        myScanner.close();
    }


}

