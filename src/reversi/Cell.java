package reversi;

import java.awt.Graphics;
import javax.swing.JButton;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Cell extends JButton{
    // the position of the cell
    private int id;
    // boolean value which indicates whether the cell is takne
    private boolean taken = false;
    // the color of the disk on the cell
    private int color=0;
    // A placeholder for the postion value (strategy 3)
    private int positionValue = 0;
    
    /**
     * Cell constructor
     * @param id - placeholder for position
     */
    public Cell(int id){
        this.id = id;
    }
    
    /**
     * Constructor when the value is passed
     * @param other parameters of the other cell.
     */
    public Cell(Cell other){
        id = other.getId();
        taken = other.getTaken();
        color = other.getColor();
    }
    
    /**
     * Mutator for id
     * @param id
     */
    public void setId(int id){
        this.id = id;
    }
    
    /**
     * Accessor for id
     * @return id
     */
    public int getId(){
        return id;
    }

    /**
     * Accessor for taken
     * @return taken
     */
    public boolean getTaken(){
        return taken;
    }
    
    /**
     * Mutator for taken
     */
    public void setTaken(){
        taken = true;
    }
    
    /**
     * Method for reseting the cell values
     */
    public void resetCell(){
        taken = false;
        color = 0;
    }
    
    /**
     * Mutator for taken
     * @param taken
     */
    public void setTaken(Boolean taken){
        this.taken = taken;
    }
    
    /**
     * Method to allow the painting of the component
     * @param g - current graphics
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);     
    }
    
    /**
     * Accessor method for colour
     * @return colour
     */
    public int getColor(){
        return color;
    }
    
    /**
     * Mutator for colour
     * @param color
     */
    public void setColor(int color){
        this.color = color;
    }
    
    /**
     * Method for the value of the position (strategy 3)
     * @return position value
     */
    public int getPositionValue(){
        return positionValue;
    }
    
    /**
     * Mutator method for position
     * @param positionValue the position value to set to
     */
    public void setPositionValue(int positionValue){
        this.positionValue = positionValue;
    }
}
