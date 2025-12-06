import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

// This is the server program. Run this first.
// It waits for up to 2 players, then starts one game between them.
public class Server {

    private ServerSocket serverSocket;
    private GameManager game; // keeps track of board and turns

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public GameManager getGame() {
        return game;
    }

    // helper: called when we have 2 players connected
    private void startGameIfReady() {
        if (ClientThread.clientThreads.size() == 2 && game == null) {

            ClientThread p1 = ClientThread.clientThreads.get(0);
            ClientThread p2 = ClientThread.clientThreads.get(1);

            game = new GameManager(p1.getUsername(), p2.getUsername());

            // assign X and O
            p1.setSymbol('X');
            p2.setSymbol('O');

            // "old style" messages that you wanted to keep
            p1.sendToThisClient("MESSAGE Welcome " + p1.getUsername() + "!");
            p1.sendToThisClient("MESSAGE Game started! You are X.");

            p2.sendToThisClient("MESSAGE Welcome " + p2.getUsername() + "!");
            p2.sendToThisClient("MESSAGE Game started! You are O.");

            // used by GUI to show symbol
            p1.sendToThisClient("START X");
            p2.sendToThisClient("START O");

            // send turn messages for the first time
            sendTurnMessages();
        }
    }

    // sends YOUR_TURN / OPPONENT_TURN + TURN <name> to everyone
    public void sendTurnMessages() {
        if (game == null) return;

        String current = game.getCurrentPlayer();

        // send YOUR_TURN / OPPONENT_TURN
        for (ClientThread ct : ClientThread.clientThreads) {
            if (ct.getUsername().equals(current)) {
                ct.sendToThisClient("YOUR_TURN");
            } else {
                ct.sendToThisClient("OPPONENT_TURN");
            }
        }

        // also broadcast TURN <name> so GUI can double check
        if (!ClientThread.clientThreads.isEmpty()) {
            ClientThread any = ClientThread.clientThreads.get(0);
            any.broadcastMessage("TURN " + current);
            any.broadcastMessage("MESSAGE It is " + current + "'s turn.");
        }
    }

    public void startServer() {
        System.out.println("Server started. Waiting for players...");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                // only allow 2 players max
                if (ClientThread.clientThreads.size() >= 2) {
                    BufferedWriter tempOut = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
                    tempOut.write("SERVER: Lobby is full. Try again later.");
                    tempOut.newLine();
                    tempOut.flush();
                    socket.close();
                    continue;
                }

                System.out.println("A new player has connected.");

                ClientThread clientThread = new ClientThread(socket, this);
                Thread t = new Thread(clientThread);
                t.start();

                startGameIfReady();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // run this to start server
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);
        Server server = new Server(ss);
        server.startServer();
    }
}
