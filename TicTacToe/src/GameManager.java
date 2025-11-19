public class GameManager {
    private String player1;
    private String player2;
    private String currentPlayer;
    private String[][] board;

    
    public GameManager(String player1, String player2, String[][] board) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
    }   

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void switchTurn() {
        if (getCurrentPlayer() == player1) {
            this.currentPlayer = player2;
        }

        else if (getCurrentPlayer() == player2) {
            this.currentPlayer = player1;
        }
    }

    public String[][] getBoard() {
        return this.board;
    }

    public String[] displayBoard() {

        String[] s = new String[6]; 
        s[0] = (board[0][0] + " | " + board[0][1] + " | " + board[0][2]);
        s[1] = ("- + - + -");
        s[2] = (board[1][0] + " | " + board[1][1] + " | " + board[1][2]);
        s[3] = ("- + - + -");
        s[4] = (board[2][0] + " | " + board[2][1] + " | " + board[2][2]);
        s[5] = ("- + - + -");

        return s;
    }

}
