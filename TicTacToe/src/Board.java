public class Board {

    private final char[][] board;

    public Board() {
        board = new char[3][3];
        // initialize all cells as empty
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = ' ';
            }
        }
    }

    public synchronized boolean isCellEmpty(int r, int c) {
        return board[r][c] == ' ';
    }

    public synchronized void setCell(int r, int c, char symbol) {
        board[r][c] = symbol;
    }

    public synchronized char[][] getBoard() {
        return board;
    }

    public synchronized boolean checkWin(char symbol) {
        // rows
        for (int r = 0; r < 3; r++) {
            if (board[r][0] == symbol && board[r][1] == symbol && board[r][2] == symbol) {
                return true;
            }
        }
        // cols
        for (int c = 0; c < 3; c++) {
            if (board[0][c] == symbol && board[1][c] == symbol && board[2][c] == symbol) {
                return true;
            }
        }
        // diagonals
        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) {
            return true;
        }
        if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) {
            return true;
        }
        return false;
    }

    public synchronized boolean isFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized String[] displayBoard() {
        String[] s = new String[5];
        s[0] = (board[0][0] + " | " + board[0][1] + " | " + board[0][2]);
        s[1] = "- + - + -";
        s[2] = (board[1][0] + " | " + board[1][1] + " | " + board[1][2]);
        s[3] = "- + - + -";
        s[4] = (board[2][0] + " | " + board[2][1] + " | " + board[2][2]);
        return s;
    }
}
