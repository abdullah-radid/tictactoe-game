// Tiny interface so the Client can call back into the GUI (MainFrame)
// whenever a new line comes from the server.
public interface ClientEventListener {
    void onServerMessage(String message);
}
