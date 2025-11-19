import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {
    
    private ServerSocket myServerSocket;
    private GameManager myGame;
    private HashMap<Thread, String> gameLobby;

    public Server(ServerSocket myServerSocket) {
        this.myServerSocket = myServerSocket;
    }


    public void startGame() {
        String[][] board = {{"_", "_", "_"}, 
                            {"_", "_", "_"}, 
                            {"_", "_", "_"}};

        if (ClientThread.ClientThreads.size() == 2 ) {
            myGame = new GameManager
            (ClientThread.ClientThreads.get(0).getUsername(), 
            ClientThread.ClientThreads.get(1).getUsername(), 
            board);

            myGame.setCurrentPlayer(ClientThread.ClientThreads.get(0).getUsername());
            //System.out.println("SERVER: It's " + newGame.getCurrentPlayer() + " turn!");
        }        
    }

    public GameManager getGame() {
        return this.myGame;
    }

    public void startServer() {
        try {

            System.out.println("The game has begun.");
            //we want to keep the socket running as long as it's not closed
            while (!myServerSocket.isClosed()) {
                //waiting for client to connect
                Socket mySocket = myServerSocket.accept();
                //Client connection
                System.out.println("A new user has connected.");

                //each obj of this class is responsible for communicating with a client
                /*
                 * Client Handler will implement runnable interfance, which will make the 
                 * instances of the class be handles by separate threads.
                 * 
                 * A thread is a sequence of instructions within a program that can be executed
                 * independently of other code. 
                 * 
                 * Threads share a memory space. 
                 * 
                 * When you launch an exectuable, it is running a thread within the process.
                 */
                ClientThread myClientThread = new ClientThread(mySocket, this);

                Thread myThread = new Thread(myClientThread);
                //this is the code that actually runs the thread, not main.
                myThread.start(); //start thread.
                startGame();
            }

        } catch (IOException e) { //IO = input/output exception
            
        }
    } 

    //closes server socket
    public void closeServerSocket() {
        try {
            if (myServerSocket != null) {
                myServerSocket.close();
            }
        } catch (IOException e) {
            //stack trace is list of method calls that app was in middle of when exception was thrown
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        /* 
         * A computer program becomes a process when it is loaded from the computer's memory
         * and begins execution.
         * 
         * A process can be executed by a processor or a set of processors.
         * 
         * A process description in memory contains info such as the program counter (which
         * information is currently being executed), registers, variable stores, file handlers,
         * signals, etc.
         * 
         */

         //server listening for clients that make a connection on this port number
         ServerSocket mainServerSocket = new ServerSocket(1234);
         //server
         Server myServer = new Server(mainServerSocket);

         //start the server
         myServer.startServer();
    }

}
