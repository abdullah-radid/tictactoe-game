// This class just keeps track of the board and whose turn it is.
// I put all the tic tac toe logic here so the server code is a bit cleaner.
public class GameManager {

    private String player1;
    private String player2;
    private String currentPlayer;
    private String[][] board;

    public GameManager(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;

        // 3x3 board, "_" means empty
        board = new String[][]{
            {"_", "_", "_"},
            {"_", "_", "_"},
            {"_", "_", "_"}
        };

        // start with player1 by default
        currentPlayer = player1;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String name) {
        currentPlayer = name;
    }

    // swap the current player between player1 and player2
    public void switchTurn() {
        if (currentPlayer.equals(player1)) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public String[][] getBoard() {
        return board;
    }

    // helper to put a symbol on the board
    // returns true if move was ok, false if out of bounds or already taken
    public boolean makeMove(int row, int col, String symbol) {
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }
        if (!board[row][col].equals("_")) {
            return false;
        }
        board[row][col] = symbol;
        return true;
    }

    // check if a particular symbol ("X" or "O") has won
    public boolean checkWinFor(String symbol) {
        // check all rows
        for (int r = 0; r < 3; r++) {
            if (board[r][0].equals(symbol) &&
                board[r][1].equals(symbol) &&
                board[r][2].equals(symbol)) {
                return true;
            }
        }

        // check all columns
        for (int c = 0; c < 3; c++) {
            if (board[0][c].equals(symbol) &&
                board[1][c].equals(symbol) &&
                board[2][c].equals(symbol)) {
                return true;
            }
        }

        // main diagonal
        if (board[0][0].equals(symbol) &&
            board[1][1].equals(symbol) &&
            board[2][2].equals(symbol)) {
            return true;
        }

        // other diagonal
        if (board[0][2].equals(symbol) &&
            board[1][1].equals(symbol) &&
            board[2][0].equals(symbol)) {
            return true;
        }

        return false;
    }

    // check if there are no "_" left on the board
    public boolean isBoardFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].equals("_")) {
                    return false;
                }
            }
        }
        return true;
    }

    // I kept this just so we can also print a text version of the board if we want
    public String[] displayBoard() {
        String[] s = new String[6];
        s[0] = board[0][0] + " | " + board[0][1] + " | " + board[0][2];
        s[1] = "- + - + -";
        s[2] = board[1][0] + " | " + board[1][1] + " | " + board[1][2];
        s[3] = "- + - + -";
        s[4] = board[2][0] + " | " + board[2][1] + " | " + board[2][2];
        s[5] = "- + - + -";
        return s;
    }
}
