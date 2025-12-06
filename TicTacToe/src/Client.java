import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

// This is the network client. It connects to the server and sends/receives text lines.
public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private ClientEventListener listener;

    public Client(String host, int port, String username, ClientEventListener listener) throws IOException {
        this.username = username;
        this.listener = listener;

        // open TCP connection to server
        socket = new Socket(host, port);

        // wrap input/output streams for convenient reading/writing of lines
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // first thing we send to server is the username (so it can label us)
        writer.write(username);
        writer.newLine();
        writer.flush();
    }

    // background thread that just sits and waits for messages from server
    public void startListening() {
        Thread t = new Thread(() -> {
            try {
                String line;
                while (socket.isConnected() && (line = reader.readLine()) != null) {
                    if (listener != null) {
                        listener.onServerMessage(line);
                    }
                }
            } catch (IOException e) {
                // if we get here, connection probably closed, just clean up
                closeEverything();
            }
        });
        t.start();
    }

    // send chat text to the server (server will broadcast it)
    public void sendChat(String text) {
        try {
            writer.write(text);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    // send a MOVE command to server with row and column
    public void sendMove(int row, int col) {
        try {
            writer.write("MOVE " + row + " " + col);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
