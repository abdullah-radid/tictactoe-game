import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class MainFrame {
    //jframe is a gui window to add components to
    public static void main(String[] args) {
        

        //Jlabel = A GUI display area for a string of text, image, or both
        JLabel label = new JLabel();
        label.setText("Bro do u even code??"); //could also put it in the Jlabel("bro do u...")

        ImageIcon labelimg = new ImageIcon("C:\\Users\\abdul\\Desktop\\tictactoe-game\\TicTacToe\\LabelImage.png");
        Image label1 = labelimg.getImage();
        Image newIcon = label1.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
        labelimg = new ImageIcon(newIcon);

        label.setIcon(labelimg);

        Border borderFactory = BorderFactory.createLineBorder(Color.green, 3);

        label.setHorizontalTextPosition(JLabel.CENTER); //set text LEFT, CENTER, or RIGHT of labelimg
        label.setVerticalTextPosition(JLabel.TOP);//set text to TOP, CENTER, or BOTTOM of image
        label.setForeground(Color.green); //set font color of text
        label.setFont(new Font("MV Boli", Font.BOLD, 20)); //set font of text
        label.setIconTextGap(-25); //set gap of text to image
        label.setBackground(new Color(228,213,183)); //set background color
        label.setOpaque(true); //display background color
        label.setBorder(borderFactory);
        label.setVerticalAlignment(JLabel.CENTER); //set vertical pos of icon+text within label
        label.setHorizontalAlignment(JLabel.CENTER); //set horizontal pos of icon+text within label
        //label.setBounds(100, 100, 250, 250); //set x and y positions within frame, as well as dimensions




        JFrame frame = new JFrame();
        
        //frame.setSize(500,500);
        //frame.setLayout(null);


        frame.setTitle("TicTacToe");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);


        //frame.setResizable(false); //prevent frame resizing for now
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //hitting x button now exits out of app
        frame.setVisible(true); //makes frame visible

        ImageIcon img = new ImageIcon("C:\\Users\\abdul\\Desktop\\tictactoe-game\\TicTacToe\\logo.jpg"); //creates image icon
        frame.setIconImage(img.getImage()); //change icon of frame

        // frame.getContentPane().setBackground(new Color(255, 255, 255)); //change color of background

        //MyFrame myFrame = new MyFrame();
        //could also do "new MyFrame()" if we don't need a name


        
        frame.add(label);
        frame.pack(); //resizes label automatically, needs to be after adding the label(s)


    }
}
