import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

// One instance of this class runs on the server for each connected player.
// It runs in its own thread and handles communication with that player.
public class ClientThread implements Runnable {

    // static list = all connected players in this one lobby
    public static ArrayList<ClientThread> clientThreads = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    private Server server;
    private char symbol;  // 'X' or 'O'

    public ClientThread(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // first line sent from client is username
            username = reader.readLine();

            clientThreads.add(this);
            broadcastMessage("SERVER: " + username + " has entered the lobby.");

        } catch (IOException e) {
            closeEverything();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setSymbol(char s) {
        symbol = s;
    }

    public char getSymbol() {
        return symbol;
    }

    // send a line only to THIS client
    public void sendToThisClient(String msg) {
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    // send a line to everyone in the lobby
    public void broadcastMessage(String msg) {
        for (ClientThread ct : clientThreads) {
            try {
                ct.writer.write(msg);
                ct.writer.newLine();
                ct.writer.flush();
            } catch (IOException e) {
                ct.closeEverything();
            }
        }
    }

    @Override
    public void run() {
        try {
            String line;

            // keep listening as long as the socket is alive
            while (socket.isConnected() && (line = reader.readLine()) != null) {

                // user clicked on a cell on the GUI
                if (line.startsWith("MOVE")) {
                    GameManager game = server.getGame();
                    if (game == null) {
                        sendToThisClient("SERVER: Game not started yet. Wait for another player.");
                        continue;
                    }

                    // make sure it's actually this person's turn
                    if (!game.getCurrentPlayer().equals(username)) {
                        sendToThisClient("SERVER: It is not your turn.");
                        continue;
                    }

                    // MOVE row col
                    String[] parts = line.split(" ");
                    if (parts.length != 3) {
                        sendToThisClient("SERVER: Invalid MOVE format. Use: MOVE row col");
                        continue;
                    }

                    int row, col;
                    try {
                        row = Integer.parseInt(parts[1]);
                        col = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException ex) {
                        sendToThisClient("SERVER: Row/col must be numbers between 0 and 2.");
                        continue;
                    }

                    String symbolStr = String.valueOf(symbol);
                    boolean ok = game.makeMove(row, col, symbolStr);
                    if (!ok) {
                        sendToThisClient("SERVER: Invalid move. Cell is taken or out of range.");
                        continue;
                    }

                    // tell both clients to update that button on their boards
                    broadcastMessage("BOARD " + row + " " + col + " " + symbolStr);

                    // optional: also show text version of board in chat
                    //for (String rowText : game.displayBoard()) {
                    //    broadcastMessage("MESSAGE " + rowText);
                    //}

                    // check for win/draw
                    if (game.checkWinFor(symbolStr)) {
                        broadcastMessage("RESULT WIN " + username);
                        broadcastMessage("MESSAGE " + username + " has won!");
                        server.shutdownServerNow();
                    } else if (game.isBoardFull()) {
                        broadcastMessage("RESULT DRAW");
                        broadcastMessage("MESSAGE Game is a draw.");
                        server.shutdownServerNow();
                    } else {
                        // no winner yet, switch to the other player
                        game.switchTurn();
                        server.sendTurnMessages();
                    }

                } else {
                    // anything else typed is treated as chat message
                    broadcastMessage("MESSAGE " + username + ": " + line);
                }
            }

        } catch (IOException e) {
            // if we get an exception reading, just close and remove this client
        } finally {
            closeEverything();
        }
    }

    public void removeClientThread() {
        clientThreads.remove(this);
        broadcastMessage("MESSAGE " + username + " has left the game.");
    }

    public void closeEverything() {
        removeClientThread();
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
