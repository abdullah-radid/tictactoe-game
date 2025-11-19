import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MyFrame extends JFrame{
    /*
     * 
     */

     MyFrame() {
        
        //JFrame this = new JFrame();
        
        //this.setSize(500,500);

        this.setTitle("TicTacToe");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);


        this.setResizable(false); //prevent this resizing for now
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //hitting x button now exits out of app
        this.setVisible(true); //makes this visible

        ImageIcon img = new ImageIcon("C:\\Users\\abdul\\Desktop\\tictactoe-game\\TicTacToe\\logo.jpg"); //creates image icon
        this.setIconImage(img.getImage()); //change icon of this

        this.getContentPane().setBackground(new Color(255, 255, 255)); //change color of background
     }
}
