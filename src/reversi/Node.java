package reversi;

import java.util.ArrayList;
import reversi.Model.Board;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Node {
    // I dont rememeber but i think i didnt use this
    // but since i dont want the project to fail now lets leave it
    private boolean generatedCheck;
    // A list containing all of the children
    private ArrayList<Node> children;
    // A parent node for the given node
    private Node parent;
    // counter to check how many children are visited
    private int counter;
    // the board of the node
    private Board board;

    /**
     * Constructor when the node doesnt have a parent
     * @param board of the node
     */
    public Node(Board board){
        children = new ArrayList<>();
        counter = 0;
        this.board = board;
        parent = null;
    }
    
    /**
     * Constructor when the node has a parent
     * @param parent - parent node
     * @param board - the current board
     */
    public Node(Node parent, Board board){
        children = new ArrayList<>();
        counter = 0;
        this.parent = parent;
        this.board = board;
    }
    
    /**
     * Adding a child to the node
     * @param node - adding a node as a child of the node
     */
    public void addChild(Node node){
        children.add(node);
        
    }
    
    /**
     * Accessor method for the children of the node
     * @return node children
     */
    public ArrayList<Node> getChildren(){
        return children;
    }
    
    /**
     * Method to return next child
     * @return child by counter or null
     */
    public Node returnNextChild(){
        try{
            return children.get(counter);
        }catch(Exception ex){
            return null;
        }
    }
    
    /**
     * Accessor method for the board
     * @return board
     */
    public Board getBoard(){
        return board;
    }
    
    /**
     * Method to increment the counter
     */
    public void incCounter(){
        counter++;
    }
    
    /**
     * Accessor method for counter
     * @return counter
     */
    public int getCounter(){
        return counter;
    }
    
    /**
     * Method to set counter
     * @param number to change to
     */
    public void setCounter(int number){
        this.counter = number;
    }
    
    /**
     * Accessor method for parent
     * @return parent node
     */
    public Node getParent(){
        return parent;
    }
    
    /**
     * Accessor method for generated check
     * @return gen check
     */
    public boolean getGeneratedCheck(){
        return generatedCheck;
    }
    
    /**
     * Mutator method for generated check
     */
    public void setGeneratedCheck(){
        generatedCheck = !generatedCheck;
    }
    
    /**
     * To String method to get info out of node
     * @return node info
     */
    @Override
    public String toString(){
        return "the node has: " + children.size()
                + " the counter is " + counter; 
    }
}