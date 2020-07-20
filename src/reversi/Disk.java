package reversi;

import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Disk extends JComponent{

    // method to allow the coloring of the component
    @Override
    public void paintComponent ( Graphics g ) {
        super.paintComponent(g);
        g.drawOval(50,50,50,50);
    }
}
