package reversi;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class MainWindow extends JPanel {
    // cosmetics for the main menu frame
    private JButton newGame;
    private JButton instructions;
    private JButton exitGame;
    private JLabel pictureLab;
    private URL url = this.getClass().getResource("images/othello_game.jpg");
    private ImageIcon icon = new ImageIcon(url);
    InstructionPane instPane;

    /**
     * constructor of elements for  main menu frame
     */
    public MainWindow(){
        this.setLayout(null);
        newGame = new JButton("New game");
        instructions = new JButton("Instructions");
        exitGame = new JButton("Exit");
        newGame.setFocusPainted(false);
        instructions.setFocusPainted(false);
        exitGame.setFocusPainted(false);
        pictureLab = new JLabel();
        pictureLab.setIcon(icon);
        pictureLab.setSize(600, 500);
        newGame.setBounds(225, 120,150, 40);
        instructions.setBounds(225, 220, 150, 40);
        exitGame.setBounds(225, 320, 150, 40);
        pictureLab.setLayout(null );
        pictureLab.add(newGame, new GridBagConstraints());
        pictureLab.add(instructions, new GridBagConstraints());
        pictureLab.add(exitGame, new GridBagConstraints());
        add(pictureLab);
        setSize(600,500);
        setVisible(true);
    }
    
    /**
     * Method to pass down action listener "new game"
     * @param a2 - the button
     */
    public void addNewGameListener(ActionListener a2){
        newGame.addActionListener(a2);
    }
    
    /**
     * Method to pass down action listener "Exit"
     * @param al - the button
     */
    public void addExitListener(ActionListener al){
        exitGame.addActionListener(al);
    }
    
    /**
     * Method to pass down action listener "instructions"
     * @param a3 - the button
     */
    public void addInstructionListener(ActionListener a3){
        instructions.addActionListener(a3);
    }
}
