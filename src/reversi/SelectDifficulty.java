package reversi;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class SelectDifficulty extends JPanel{
    // difficulty pane cosmetics
    private URL url = this.getClass().getResource("images/othello_game.jpg");
    private ImageIcon icon = new ImageIcon(url);
    private JLabel pictureLab;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JTextArea text;
    private Font font;
    private float size;

    /**
     * constructor for difficulty screen
     */
    public SelectDifficulty(){
        this.setLayout(null);
        easyButton = new JButton("Easy");
        normalButton = new JButton("Normal");
        hardButton = new JButton("Hard");
        pictureLab = new JLabel();
        text = new JTextArea();
        easyButton.setBounds(120, 240, 100, 40);
        normalButton.setBounds(255, 240, 100, 40);
        hardButton.setBounds(390, 240, 100, 40);
        easyButton.setFocusPainted(false);
        normalButton.setFocusPainted(false);
        hardButton.setFocusPainted(false);
        pictureLab.setLayout(null);
        font = text.getFont();
        size = font.getSize() +50.0f;
        text.setFont(font.deriveFont(size));
        text.setBounds(100, 130, 420, 100);
        text.setEditable(false);
        text.setOpaque(false);
        text.setForeground(java.awt.Color.white);
        text.append("Select difficulty");
        pictureLab.add(text, new GridBagConstraints());
        pictureLab.add(easyButton, new GridBagConstraints());
        pictureLab.add(normalButton, new GridBagConstraints());
        pictureLab.add(hardButton, new GridBagConstraints());
        pictureLab.setIcon(icon);
        pictureLab.setSize(600, 500);
        add(pictureLab);
        setSize(600,500);
        setVisible(true);
    }
    
    /**
     * Method to pass down easy difficulty action listener
     * @param a2 - the button
     */
    public void easyDifficultyListener(ActionListener a2){
        easyButton.addActionListener(a2);
    }
    
    /**
     * Method to pass down normal difficulty action listener
     * @param al - the button
     */
    public void normalDifficultyListener(ActionListener al){
        normalButton.addActionListener(al);
    }
    
    /**
     * Method to pass down hard difficulty action listener
     * @param a3 - the button
     */
    public void hardDifficultyListener(ActionListener a3){
        hardButton.addActionListener(a3);
    }
}
