package reversi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Model extends Observable{
    // passing the board
    Board board;
    // the main window object
    MainWindow mainWin;
    // instruction pane
    InstructionPane instWin;
    // difficulty screen placeholder
    SelectDifficulty difScreen;
    
    /**
     * Constructor to set everything
     */
    public Model(){
        mainWin = new MainWindow();
        board = new Board();
        instWin = new InstructionPane();
        difScreen = new SelectDifficulty();
    }
    
    /**
     * Method to create new board
     * @return returning the new board
     */
    public Board genNewBoard(){
        board = new Board();
        return board;
    }
    
    /**
     * Method to swap screens
     * @return difficulty pane
     */
    public SelectDifficulty getSelectDifficulty(){
        return difScreen;
    }
    
    /**
     * Accessor method for board
     * @return board
     */
    public Board getBoard(){
        return board;
    }
    
    /**
     * Accessor method for main window
     * @return main window
     */
    public MainWindow getMain(){
        return mainWin;
    }
    
    /**
     * Accessor method for instruction window
     * @return instruction window
     */
    public InstructionPane getInstPane(){
        return instWin;
    }
    
    // method to pass the cell
    void shoot(Cell cell){
        setChanged();
        notifyObservers(cell);
    }
    
    /**
     * Board inner class
     */
    public class Board extends JPanel {
        
        // board dimention size
        private final int sizeOfBoard = 8;
        // size of icon
        private static final int ICON_LENGTH = 60;
        // black icon
        private Icon blankIcon;
        // white icon
        private Icon whiteIcon;
        // empty icon
        private Icon blackIcon;
        // the id placeholder
        private int id =0;
        // a list to hold all of the cell info
        private ArrayList<Cell> cellList=new ArrayList<>();
        // all values to be used for strategy 3
        private int[][] possitionValues ={{5000,-1000, 100, 80, 80,100,-1000,5000},
                                            {-1000,-1500,-40,-50,-50,-40,-700,-1000},
                                            {100,-40,3,1,1,3,-40,100},
                                            {120,-50,1,5,5,1,-50,120},
                                            {120,-50,1,5,5,1,-50,120},
                                            {100,-40,3,1,1,3,-40,100},
                                            {-1000,-1500,-40,-50,-50,-40,-1500,-1000},
                                            {5000,-1000, 100, 80, 80,100,-1000,5000}};
        // initial board value
        private Integer boardValue=null;
        // cell id placeholder for computer
        private int cellMoveId =0;
        // alpha value for alpha beta pruning
        private Double alpha = Double.NEGATIVE_INFINITY;
        // beta value for alpha beta pruning
        private Double beta = Double.POSITIVE_INFINITY;
        // cell to be taken, could have been set to negative infinity
        // but integer is simpler
        private int cellToBeTaken = -100;
        
        /**
         * passing the board
         * @param other the board to be copied
         */
        public Board(Board other){
            other.getCellList().forEach((cell) -> {
                this.cellList.add(new Cell(cell));
            });
            
            // setting grid layout for java buttons
            setLayout(new GridLayout(sizeOfBoard,sizeOfBoard));
            
            // creating each color for icons
            blankIcon = createIcon(new Color(0, 0, 0, 0));
            blackIcon = createIcon(Color.BLACK);
            whiteIcon = createIcon(Color.WHITE);
            
            // setting the layout of the board
            for (int i = 0; i < 64; i++) {
                Cell cell = cellList.get(i);
                cell.setBackground(Color.GRAY.darker());

                // adding a cell to the grid
                add(cell);
                
                // checking if the colour is 1 then white
                if(cell.getColor() == 1){
                    cell.setIcon(whiteIcon);
                    // if 2 then black
                }else if(cell.getColor() == 2){
                    cell.setIcon(blackIcon);
                    // else is blank
                }else{
                    cell.setIcon(blankIcon);
                }
                                
                cell.setFocusPainted(false);
                // action listener attached to every cell
                cell.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        shoot(cell);
                    }
                });
            }
            
            int cellNumber =0;
            for (int i = 0; i < sizeOfBoard; i++) {
                for (int j = 0; j < sizeOfBoard; j++) {
                    cellList.get(cellNumber).setPositionValue(possitionValues[i][j]);
                    cellNumber++;
                }
            }
            setSize(600,500);
            setVisible(true);
        }

        /**
         * constructor for brand new board
         */
        public Board(){
            // exacly the same as before layout, setting colours to icons
            setLayout(new GridLayout(sizeOfBoard,sizeOfBoard));
            blankIcon = createIcon(new Color(0, 0, 0, 0));
            blackIcon = createIcon(Color.BLACK);
            whiteIcon = createIcon(Color.WHITE);

            // creating a new board setup
            for (int i = 0; i < sizeOfBoard; i++) {
                for (int j = 0; j < sizeOfBoard; j++) {
                    Cell cell = new Cell(id++);
                    cell.setBackground(Color.GRAY.darker());
                    cellList.add(cell);
                    
                    // adding to cell list
                    for (int k = 0; k < cellList.size(); k++) {
                        add(cellList.get(k));
                    }
                    // placing black and white disks accordingly
                    if( cell.getId()== 27 || cell.getId()== 36){
                       cell.setIcon(whiteIcon);
                       cell.setTaken(Boolean.TRUE);
                        cell.setColor(1);
                        cellList.get(cell.getId()).setColor(1);
                    }
                    else if(cell.getId()== 28  || cell.getId()== 35)
                    {
                        cell.setIcon(blackIcon);
                        cell.setTaken(Boolean.TRUE);
                        cell.setColor(2);
                        cellList.get(cell.getId()).setColor(2);
                    }else{
                        cell.setIcon(blankIcon);
                    }

                    // and obviously attaching action listener to every cell
                    cell.setFocusPainted(false);
                    cell.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            shoot(cell);
                        }
                    });
                }
            }
            
            int cellNumber = 0;
            for (int i = 0; i < sizeOfBoard; i++) {
                for (int j = 0; j < sizeOfBoard; j++) {
                    cellList.get(cellNumber).setPositionValue(possitionValues[i][j]);
                    cellNumber++;
                }
            }
            setSize(600,500);
            setVisible(true);
        }
        
        /**
         * Method to set the cell that was taken on this board
         * @param id the id of the taken cell
         */
        public void setCellToBeTaken(int id){
            this.cellToBeTaken = id;
        }
        
        /**
         * Accessor method for the cell to be taken
         * @return cell to be taken
         */
        public int getCellToBeTaken(){
            return cellToBeTaken;
        }
        
        /**
         * @return the blankIcon
         */
        public Icon getBlankIcon() {
            return blankIcon;
        }
        
        /**
         * Method that uses maths to determine the value of the position
         * @param value - the position
         * @return the value of position
         */
        public int getPositionValue(int value){
            int x = value/8;
            int y = value%8;
            return possitionValues[x][y];
        }
        /**
         * @return the whiteIcon
         */
        public Icon getWhiteIcon() {
            return whiteIcon;
        }

        /**
         * @return the blackIcon
         */
        public Icon getBlackIcon() {
            return blackIcon;
        }
        
        /**
         * Accessor for the cell list
         * @return cell list
         */
        public ArrayList<Cell> getCellList(){
            return cellList;
        }

        /**
         * Method to add to existing value another value
         * @param passVal another value
         */
        public void addToValue(int passVal){
            if(boardValue==null){
                boardValue = passVal;
            }else{
                boardValue = boardValue+passVal;
            }
        }
        
        /**
         * Accessor method for board value
         * @return board value
         */
        public Integer getBoardValue(){
            return boardValue;
        }
        
        /**
         * Mutator method for board value
         * @param boardValue
         */
        public void setBoardValue(int boardValue){
            this.boardValue = boardValue;
        }
        
        /**
         * Mutator method for the move
         * @param id - the location on board
         */
        public void setCellMoveId(int id){
            this.id = id;
        }
        
        /**
         * Accessor method for cell move
         * @return
         */
        public int getCellMoveId(){
            return cellMoveId;
        }
        
        /**
         * Accessor method for alpha value
         * @return alpha value
         */
        public double getAlpha(){
            return alpha;
        }
        
        /**
         * Mutator method for alpha value
         * @param alpha value on the board
         */
        public void setAlpha(double alpha){
            this.alpha = alpha;
        }
        
        /**
         * Accessor method for beta value
         * @return beta value
         */
        public double getBeta(){
            return beta;
        }
        
        /**
         * Mutator method for beta value
         * @param beta value of the board
         */
        public void setBeta(double beta){
            this.beta = beta;
        }
        
        /**
         * Mutator for the value of the board
         * @param boardValue
         */
        public void setValue(int boardValue){
            this.boardValue = boardValue;
        }
        
        // setting the Icon on the cell using g2 graphics
        private Icon createIcon(Color color) {
            BufferedImage img = new BufferedImage(ICON_LENGTH,ICON_LENGTH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            // largest gap
            int gap = 5;
            // rotation
            int w = ICON_LENGTH - 2 * gap;
            int h = w;
            g2.fillOval(gap, gap, w, h);
            g2.dispose();
            return new ImageIcon(img);
        }
    }
}
