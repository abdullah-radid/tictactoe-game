import javax.swing.*;
import java.awt.*;

/*
 * This is the first screen where the user types their username and server host.
 * After connecting, we can also show a simple countdown before the game starts.
 */
public class StartPage extends JPanel {

    // MainFrame implements this so it can react when the user presses Connect
    public interface StartPageListener {
        void onConnectRequested(String username, String host);
    }

    private JTextField usernameField;
    private JTextField hostField;
    private JLabel statusLabel;
    private JLabel countdownLabel;

    private StartPageListener listener;

    // fields for countdown stuff
    private javax.swing.Timer countdownTimer;
    private int countdownSeconds;

    public StartPage(StartPageListener listener) {
        this.listener = listener;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // username label + field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // host label + field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Server host:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        hostField = new JTextField("localhost", 15);
        add(hostField, gbc);

        // connect button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton connectButton = new JButton("Connect");
        add(connectButton, gbc);

        // status label (for messages like "Connected..." or errors)
        gbc.gridy = 3;
        statusLabel = new JLabel(" ");
        add(statusLabel, gbc);

        // countdown label (starts empty, used only when game is about to start)
        gbc.gridy = 4;
        countdownLabel = new JLabel(" ");
        add(countdownLabel, gbc);

        connectButton.addActionListener(e -> handleConnect());
    }

    private void handleConnect() {
        String username = usernameField.getText().trim();
        String host = hostField.getText().trim();

        if (username.isEmpty()) {
            setStatusText("Please enter a username.");
            return;
        }
        if (host.isEmpty()) {
            host = "localhost";
        }

        if (listener != null) {
            listener.onConnectRequested(username, host);
        }
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    // This method is called by MainFrame when the server says "START ..."
    // It shows "Game starting in 3..." then 2, 1, then calls the callback.
    public void startCountdown(Runnable onFinished) {
        // if a previous countdown is running for some reason, stop it first
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        countdownSeconds = 3;
        countdownLabel.setText("Game starting in " + countdownSeconds + "...");

        countdownTimer = new javax.swing.Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds > 0) {
                countdownLabel.setText("Game starting in " + countdownSeconds + "...");
            } else {
                countdownLabel.setText("Game starting now!");
                countdownTimer.stop();
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        countdownTimer.start();
    }
}
