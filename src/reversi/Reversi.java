package reversi;

import java.io.IOException;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Reversi {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // creating an MVC pattern we are suppose to follow
        Model model = new Model();
        View view = new View();
        
        Controller controller = new Controller(model,view);
        
        // adding observable object
        model.addObserver(controller);
        
        // invoking action listener
        java.awt.EventQueue.invokeLater(() -> {
            view.setVisible(true);
        });
    }
    
}
