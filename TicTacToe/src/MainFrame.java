import javax.swing.*;
import java.awt.*;

/*
 * Main GUI window. It shows different "pages" using CardLayout:
 * - StartPage: enter username and host, connect to server
 * - GamePage: actual tic tac toe board + chat
 * - EndPage: shows result ("You won", "You lost", etc.) and exit
 */
public class MainFrame extends JFrame
        implements ClientEventListener,
                   StartPage.StartPageListener,
                   GamePage.GamePageListener,
                   EndPage.EndPageListener {

    private CardLayout cardLayout;
    private JPanel cards;

    private StartPage startPage;
    private GamePage gamePage;
    private EndPage endPage;

    private Client client;
    private String username;
    private char mySymbol = ' ';
    private boolean myTurn = false;

    public MainFrame() {
        super("TicTacToe");

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        startPage = new StartPage(this);
        gamePage = new GamePage(this);
        endPage = new EndPage(this);

        cards.add(startPage, "START");
        cards.add(gamePage, "GAME");
        cards.add(endPage, "END");

        add(cards);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        cardLayout.show(cards, "START");
    }

    // StartPageListener 
    @Override
    public void onConnectRequested(String username, String host) {
        try {
            this.username = username;

            // connect to server and start listening for messages
            client = new Client(host, 1234, username, this);
            client.startListening();

            // stay on the start page for now, just show that we are connected
            startPage.setStatusText("Connected to server. Waiting for both players...");
            // we will switch to the game page AFTER we get a START message from the server

        } catch (Exception e) {
            startPage.setStatusText("Connection failed: " + e.getMessage());
        }
    }

    // ===== GamePageListener =====
    @Override
    public void onSendChat(String text) {
        if (client != null) {
            client.sendChat(text);
        }
    }

    @Override
    public void onCellClicked(int row, int col) {
        if (client != null) {
            client.sendMove(row, col);
        }
    }

    // ===== EndPageListener =====
    @Override
    public void onExitRequested() {
        if (client != null) {
            client.closeEverything();
        }
        System.exit(0);
    }

    // ===== ClientEventListener =====
    @Override
    public void onServerMessage(String message) {
        // UI updates must run on Swing's event dispatch thread
        SwingUtilities.invokeLater(() -> handleServerMessage(message));
    }

    // interpret commands coming from the server and update UI
    private void handleServerMessage(String msg) {

        if (msg.startsWith("START")) {
            // "START X" or "START O" means: game is ready and server tells us our symbol
            String[] parts = msg.split(" ");
            if (parts.length >= 2) {
                mySymbol = parts[1].charAt(0);
                gamePage.setSymbolLabel("You are: " + mySymbol);
            }

            // let the user know both players are here now
            startPage.setStatusText("Both players connected!");

            // do the 3..2..1 countdown on the start page, then switch to the game page
            startPage.startCountdown(() -> {
                gamePage.setUsername(username);
                cardLayout.show(cards, "GAME");
            });

        } else if (msg.startsWith("BOARD")) {
            // "BOARD row col symbol" -> update that cell on our board
            String[] parts = msg.split(" ");
            if (parts.length == 4) {
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                String sym = parts[3];
                gamePage.updateCell(row, col, sym);
            }

        } else if (msg.equals("YOUR_TURN")) {
            myTurn = true;
            gamePage.setMyTurn(true);
            gamePage.setTurnLabel("Your turn");

        } else if (msg.startsWith("OPPONENT_TURN")) {
            myTurn = false;
            gamePage.setMyTurn(false);
            gamePage.setTurnLabel("Waiting for opponent...");

        } else if (msg.startsWith("TURN ")) {
            // extra safety: TURN username (we can double-check whose turn)
            String[] parts = msg.split(" ", 2);
            if (parts.length == 2) {
                String whose = parts[1];
                myTurn = whose.equals(username);
                gamePage.setMyTurn(myTurn);
                if (!myTurn) {
                    gamePage.setTurnLabel("Waiting for " + whose);
                }
            }

        } else if (msg.startsWith("RESULT")) {
            // "RESULT WIN username" or "RESULT DRAW"
            String[] parts = msg.split(" ");
            String resultText = "Game over.";
            if (parts.length >= 2) {
                if ("WIN".equals(parts[1]) && parts.length >= 3) {
                    String winner = parts[2];
                    if (winner.equals(username)) {
                        resultText = "You won!";
                        gamePage.appendChat("SERVER: You won!");
                    } else {
                        resultText = "You lost. Winner: " + winner;
                        gamePage.appendChat("SERVER: You lost. Winner: " + winner);
                    }
                } else if ("DRAW".equals(parts[1])) {
                    resultText = "Game is a draw.";
                    gamePage.appendChat("SERVER: Game is a draw.");
                }
            }

            // tell the end page what to display and then switch to it
            endPage.setResultMessage(resultText);
            cardLayout.show(cards, "END");

        } else if (msg.startsWith("MESSAGE ")) {
            // messages for the chat area
            gamePage.appendChat(msg.substring(8));

        } else {
            // any other messages that don't match a command just go into chat
            gamePage.appendChat(msg);
        }
    }

    // run this to start the GUI client (run two instances for 2 players)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
