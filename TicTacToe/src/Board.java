public class Board {
    
    private int[][] board =        {{1, 2, 3}, 
                                   {4, 5, 6}, 
                                   {7, 8, 9}};

    public void printBoard() {
        
        System.out.println(board[0][0] + " | " + board[0][1] + " | " + board[0][2]);
        System.out.println("- + - + -");
        System.out.println(board[1][0] + " | " + board[1][1] + " | " + board[1][2]);
        System.out.println("- + - + -");
        System.out.println(board[2][0] + " | " + board[2][1] + " | " + board[2][2]);
        System.out.println("- + - + -");
    }

    /*
     *  TO DO:
     * 1. IMPLEMENT TURN SYSTEM FOR 2 PLAYERS
     * 2. RESTRICT GAME LOBBIES TO 2 PLAYERS ONLY
     * 3. HOW TO GET MULTIPLE GAMES RUNNING AT ONCE
     * 4. IMPLEMENT BASIC GUI
     * 5. MAKE IT INTO AN EXECUTABLE
     * 6. EXTRA IF TIME: FIGURE OUT HOW TO USE OUTSIDE OF LAN
     * 7. EXTRA IF TIME: MAKE GUI NICER, ADD EFFECTS, MUSIC ETC.
     * 
     * 
     * 
     * 
     * 
     */
}
