package reversi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class BDialog extends JDialog {
    // cosmetics for custom made Jdialog box
    URL url = this.getClass().getResource("images/output_fcSb5d.gif");
    ImageIcon imageIcon = new ImageIcon(url);
    JLabel label = new JLabel(imageIcon);

    /**
     * constructor for JDialog box
     */
    public BDialog() {
        super(new JFrame(), true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        JLabel lab = new JLabel("computer is thinking of a move");
        label.setPreferredSize(new Dimension(30,30));
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(label, BorderLayout.EAST);
        add(lab);
        Ellipse2D a=new Ellipse2D.Double(0,0,200,90);
        setShape(a);
        setPreferredSize(new Dimension(200,90));
        pack();
        setLocationRelativeTo(null);
    }
}