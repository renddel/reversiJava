package reversi;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class InstructionPane extends JPanel {
    // all of the cosmetics for the instruction pane
    private URL url = this.getClass().getResource("images/othello_game.jpg");
    private ImageIcon icon = new ImageIcon(url);
    private JLabel pictureLab;
    private JTextArea text;
    private Font font;
    private float size;
    private JButton backButton;

    /**
     * constructor for instruction pane
     */
    public InstructionPane(){
        this.setLayout(null);
        pictureLab = new JLabel();
        pictureLab.setIcon(icon);
        pictureLab.setSize(600, 500);
        backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        text = new JTextArea(5, 20);
        font = text.getFont();
        size = font.getSize() + 1.0f;
        text.setFont(font.deriveFont(size));
        text.setBounds(100, 130, 395, 225);
        text.setEditable(false);
        text.setBackground(new Color(238,238,238)); 
        text.setBorder(new TitledBorder("Instructions"));
        text.append(instTextArea());
        backButton.setBounds(250, 380, 100, 40);
        pictureLab.setLayout(null);
        pictureLab.add(text, new GridBagConstraints());
        pictureLab.add(backButton, new GridBagConstraints());
        add(pictureLab);
        setSize(600,527);
        setVisible(true);
    }
    
    /**
     * Action listener
     * @param a1 - element of back button
     */
    public void addBackListener(ActionListener a1){
        backButton.addActionListener(a1);
    }
    
    /**
     * A string of words for the instruction pane, could have been better
     * but no time
     * @return text
     */
    public String instTextArea(){
        return "Reversi is a strategy board game for two players,"
            + " played on an 8*8 \nuncheckered board. There are sixty-fourn"
            + " identical game pieces \ncalled disks (often spelled \"discs\"),"
            + " which are light on one side and \ndark on the other. Players"
            + " take turns placing disks on the board \nwith their assigned"
            + " color facing up. During a play, any disks of the \nopponent's"
            + " color that are in a straight line and bounded by the disk"
            + " \njust placed and another disk of the current player's color"
            + " are turned \nover to the current player's color." +
            "\n\n" +
            "The object of the game is to have the majority of disks turned "
            + "to \ndisplay your color when the last playable empty square is filled.\n";
    }
}
