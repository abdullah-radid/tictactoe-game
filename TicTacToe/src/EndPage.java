import javax.swing.*;
import java.awt.*;

// Very simple "end screen" that shows who won + a thank you message.
public class EndPage extends JPanel {

    public interface EndPageListener {
        void onExitRequested();
    }

    private EndPageListener listener;

    // I keep two labels: one for the result, one for the thank-you text.
    private JLabel resultLabel;
    private JLabel thanksLabel;

    public EndPage(EndPageListener listener) {
        this.listener = listener;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // center panel with result + thanks stacked vertically
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        resultLabel = new JLabel("Game over.", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 26));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        thanksLabel = new JLabel("Thank you for playing", SwingConstants.CENTER);
        thanksLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(resultLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(thanksLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        add(centerPanel, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            if (listener != null) {
                listener.onExitRequested();
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(exitButton);
        add(bottom, BorderLayout.SOUTH);
    }

    // MainFrame will call this when it knows who won.
    public void setResultMessage(String message) {
        resultLabel.setText(message);
    }
}
