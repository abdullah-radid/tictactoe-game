import javax.swing.*;
import java.awt.*;

// This panel shows the 3x3 board, chat area, and status labels.
public class GamePage extends JPanel {

    public interface GamePageListener {
        void onSendChat(String text);
        void onCellClicked(int row, int col);
    }

    private JButton[][] buttons = new JButton[3][3];
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel symbolLabel;
    private JLabel turnLabel;

    private boolean myTurn = false;
    private GamePageListener listener;

    public GamePage(GamePageListener listener) {
        this.listener = listener;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // top panel with labels
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        symbolLabel = new JLabel("You are: ?");
        turnLabel = new JLabel("Waiting for game to start...");
        infoPanel.add(symbolLabel);
        infoPanel.add(turnLabel);
        add(infoPanel, BorderLayout.NORTH);

        // center board with 3x3 buttons
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        Font btnFont = new Font("Arial", Font.BOLD, 40);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final int row = r;
                final int col = c;

                JButton btn = new JButton("");
                btn.setFont(btnFont);
                btn.addActionListener(e -> handleCellClick(row, col));

                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // right side: chat area
        JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);
        chatPanel.add(scroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        chatInput = new JTextField();
        JButton sendButton = new JButton("Send");
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.EAST);

        sendButton.addActionListener(e -> sendChat());
        chatInput.addActionListener(e -> sendChat());
    }

    private void handleCellClick(int row, int col) {
        if (!myTurn) {
            appendChat("SERVER: It is not your turn.");
            return;
        }
        if (!buttons[row][col].getText().isEmpty()) {
            appendChat("SERVER: That cell is already taken.");
            return;
        }
        if (listener != null) {
            listener.onCellClicked(row, col);
        }
    }

    private void sendChat() {
        String text = chatInput.getText().trim();
        if (text.isEmpty()) return;
        if (listener != null) {
            listener.onSendChat(text);
        }
        chatInput.setText("");
    }

    public void updateCell(int row, int col, String symbol) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            buttons[row][col].setText(symbol);
        }
    }

    public void appendChat(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void setSymbolLabel(String text) {
        symbolLabel.setText(text);
    }

    public void setTurnLabel(String text) {
        turnLabel.setText(text);
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public void setUsername(String username) {
        appendChat("You are: " + username);
    }
}
