package reversi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import reversi.Model.Board;

/**
 *
 * @author Rimantas Silka (100094676)
 */
public class Controller implements Observer{
    private Model model;
    private View view;
    // Using a set for the next possible moves because it should not
    // incude dublicates
    private Set<Integer> possibleMoves = new HashSet<>();
    // initial state of move colour where 0 is black and 1 is white
    private int moveColour = 2;
    
    // a to hold how much available moves are on each colour if the
    // count hits 2 then the game is finished
    private int invalidCount = 0;
    // current depth of the tree
    private int level = 0;
    // boolean value to check if there is only a change in leafs
    // on the last level
    private boolean leafChange = false;
    // a boolean value to generate the nodes past leaf value
    private boolean ultimateCheck = false;
    // boolean to run while loop
    private boolean run = true;
    // max depth of the tree
    private int depth = 3;
    // a value to return back to initial depth in case it was
    // changed during the creation of the tree
    private int initialDepth = 3;
    // a placeholder for the computer move to id position
    // initialised to negative since there is a position of 0
    private int computerMoveId = -60;
    // the colour of AI player
    private int initialColour = 1;
    private int difficulty = 0;
    private Icon posIcon = createPosIcon(Color.GREEN);
    private Icon original = createPosIcon(new Color(89,89,89));
    Model.Board board;
    Cell cell;
    
    /**
     *
     * @param model serves as a database in the project
     * @param view the initial view frame
     */
    public Controller(Model model, View view){
        this.model = model;
        this.view = view;
        view.add(model.getMain());
        model.getMain().addExitListener(new ExitListener());
        model.getMain().addNewGameListener(new NewGameListener());
        model.getMain().addInstructionListener(new InstructionListener());
        model.getInstPane().addBackListener(new BackListener());
        model.getSelectDifficulty().easyDifficultyListener(new easyDifficultyListener());
        model.getSelectDifficulty().normalDifficultyListener(new normalDifficultyListener());
        model.getSelectDifficulty().hardDifficultyListener(new hardDifficultyListener());
        cell = new Cell(0);
    }
   
    // method for swapping between colours
    private void setColour(){
        moveColour = moveColour == 1 ? 2 : 1;
    }
    
    // method to set the colour
    private void setColour(int colour){
        moveColour = colour;
    }
    
    // method to retrieve value of colour
    private int getColour(){
        return moveColour;
    }

    
    private void setColourPossibleMove(Set possibleMoves,Board board){
        for (Object item: possibleMoves) {
            board.getCellList().get((int)item).setIcon(posIcon);
        }
    }
    private void resetBorder(Set possibleMoves,Board board){
        for (Object item: possibleMoves) {
            board.getCellList().get((int)item).setIcon(original);
        }
    }


    /**
     * move method
     * @param o observable object
     * @param o1 observee to be changed with
     */
    @Override
    public void update(Observable o, Object o1) {
        if(o.getClass().equals(model.getClass())){
            // getting current board value
            Model.Board board = model.getBoard();
            // initialising the cell where the move was made
            Cell cell = (Cell)o1;
            // checking if the game has not ended
            resetBorder(possibleMoves,board);
            if(invalidCount<2){
                // checking if next playes possible move list is not empty
                if(!possibleMoves.isEmpty()){
                    // if we get here then reset the count
                    invalidCount = 0;
                    // checking if the clicked cell is possible move list
                    if(possibleMoves.contains(cell.getId())){
                        // if a person has a move then we reset the count
                        invalidCount=0;
                        // making a move
                        move(cell,board,true);
                        // clearing and generating new ones to check if AI has 
                        // any valid moves
                        possibleMoves.clear();
                        for (int i = 0; i < 64; i++) {
                            cell = board.getCellList().get(i);
                            move(cell,board,false);
                        }
                        // we dont need to reset the counter because 
                        // it is 2 valid moves in a row if the previous
                        // one wasn't valid then this part wouldnt be possible
                        if(!possibleMoves.isEmpty()){
                            invalidCount =0;
                            // show icon for any impossible moves
                            showLoadIcon();
                            try{
                                computerMove(board);
                            }catch(IOException e){
                                System.out.println("an error with computer move");
                            }
                            // making a move for an AI
                            cell = board.getCellList().get(computerMoveId);
                            move(cell,board,true);
                            // cleaing the list, then generating it to check
                            // if the next person has any moves
                            possibleMoves.clear();
                            for (int i = 0; i < 64; i++) {
                                cell = board.getCellList().get(i);
                                move(cell,board,false);
                            }
                            // if the list is empty for human player, then run
                            // this method again, and it will instantly go to else
                            if(possibleMoves.isEmpty()){
                                printDialog(invalidCount, board);
                                setColour();
                                update(o,o1);
                            }else{
                                // if the next player has any moves then reset 
                                // the count for impossible moves
                                setColourPossibleMove(possibleMoves,board);
                                invalidCount =0;
                            }
                        }else{
                            // if the computer doesn't have any moves then the
                            // counter is increased
                            setColour();
                            invalidCount++;
                            // then changing to other players colour and checking
                            // if he has any moves left
                            for (int i = 0; i < 64; i++) {
                                cell = board.getCellList().get(i);
                                move(cell,board,false);
                            }

                            // if he doesn't have any moves, then invalid count
                            // should be 2 at this point and the end game screen
                            // shown
                            if(possibleMoves.isEmpty()){
                                invalidCount++;
                                setColour();
                                printDialog(invalidCount, board);
                            }
                            
                            // if there are moves, then the game will proceed,
                            // and the impossible move counter reset
                            else{
                                setColourPossibleMove(possibleMoves,board);
                                invalidCount =0;
                                printDialog(invalidCount, board);
                                setColour();
                            }
                        }
                    }
                    // in case the wrong cell is clicked then print the screen
                    // to notify the player
                    else{
                        JOptionPane.showMessageDialog(view,
                        "Incorrect move",
                        "",JOptionPane.PLAIN_MESSAGE);
                        setColourPossibleMove(possibleMoves,board);
                    }
                }
                // if the code gets here, human doesn't have any moves left
                else{
                    // increasing impossible move count for the skipped turn
                    invalidCount++;
                    setColour();
                    // generating new moves
                    for (int i = 0; i < 64; i++) {
                        cell = board.getCellList().get(i);
                        move(cell,board,false);
                    }
                    /*
                     * checking if the computer has any moves left, if not then
                     * neither the player nor AI has any moves therefore end
                     *  game screen can be shown
                    */
                    if(possibleMoves.isEmpty()){
                        invalidCount++;
                        printDialog(invalidCount, board);
                        setColour();
                    }
                    // if the computer move list is not empty then computer
                    // can execute his move again, since he can play
                    else{
                        // reset count
                        invalidCount=0;
                        showLoadIcon();
                        try{
                            computerMove(board);
                        }catch(IOException e){
                            System.out.println("eee");
                        }
                        //taking cell which is coputer move
                        cell = board.getCellList().get(computerMoveId);
                        move(cell,board,true);
                        
                        // if computer move succeeds then checking if other player
                        // has any moves
                        possibleMoves.clear();
                        for (int i = 0; i < 64; i++) {
                            cell = board.getCellList().get(i);
                            move(cell,board,false);
                        }
                        // if no moves are found then checking if the impossible
                        // move count isn't at the limit, if its 2 then end screen
                        // can be shown, otherwise this method has to be run for
                        // the computer player to make a move again
                        if(possibleMoves.isEmpty()){
                            if(invalidCount<2){
                                printDialog(invalidCount, board);
                                setColour();
                                update(o,o1); 
                            }else{
                                printDialog(invalidCount, board);
                                setColour();
                            }
                        }
                        // if the list is not empty for human player - reset
                        else{
                            setColourPossibleMove(possibleMoves,board);
                            invalidCount = 0;
                        }
                    }
                }
            }
        }
    }
    
    /*
    *  Method for creation of the circuling wait icon 
    */
    private void showLoadIcon(){
        BDialog asd = new BDialog();
        Timer timer = new Timer(1000, (ActionEvent ae) -> {
            asd.setVisible(false);
            asd.dispose();
        });
        timer.setRepeats(false);
        timer.start();

        asd.setVisible(true);
    }
    // method for either creating a dialong box for end game or notifies
    // who has skipped a turn
    private void printDialog(int invalidCount, Board board){
        // if the missed move counter is higher than 1 means, that nobody can 
        // make a turn, therefore end game screen is created
        if(invalidCount > 1){
            // just initialising the string
            String winner = "ERROR";
            // array to hold the values for number of white pieces and number of 
            // black pieces on the current board
            int[] winCount = {0,0};
            
            // going through all elements in the board and increamenting the 
            // value for each piece
            for(Cell temp : board.getCellList()){
                if(temp.getColor() == 1){
                    winCount[0]++;
                }else if(temp.getColor()==2){
                    winCount[1]++;
                }
            }
            // recreating the string to print who won (could be draw)
            winner = winCount[0] > winCount[1] ? "white" : winCount[0] == winCount[1] ? "draw" : "black";
            // creating dialog box to output the information
            JOptionPane.showMessageDialog(view,
                "Game Over, " +winner + " wins!"+"\n"
                  +"Number of white pieces: " + winCount[0]+"\n"  
                  +"Number of black pieces: " + winCount[1],
                "",
                JOptionPane.PLAIN_MESSAGE);
            view.remove(model.getBoard());
            view.add(model.getMain());
            view.revalidate();
            view.repaint();
        }
        // if the counter is higher than 1, then the game continues, some player
        // just skipped a turn
        else{
            setColour();
            String strCol;
            // deciding which colour missed a turn
            setColour();
            if(moveColour == 2){
                strCol = "white";
            }else{
                strCol = "black";
            }
            setColour();
            // creating a dialog box to print who doesn't have any moves for 
            // upcomming turn
            JOptionPane.showMessageDialog(view,
            "No "+ strCol +" moves available, swapping players",
            "",
            JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    // a move method
    private void move(Cell cell, Model.Board board,boolean actualMove){
        // bolean value to see if its an actual move
        boolean wasChanged = false;

            // check if the given cell is actually occupied or not
            if(cell.getTaken() == false){
                //an arraylist to hold all of the possible positions in the checks
                ArrayList<Integer> temp=new ArrayList<>();
                
                // setting the colour to the cell for white
                if(moveColour == 1){
                    cell.setColor(1);
                    board.getCellList().get(cell.getId()).setColor(1);
                }
                // setting the colour to the cell for black
                else{
                    cell.setColor(2);
                    board.getCellList().get(cell.getId()).setColor(2);
                }
                
                // changing the value on the board of elements
                board.getCellList().get(cell.getId()).setTaken();
                
                // clicked cell position on the board
                int position = cell.getId();
                // colour for the next move
                int nextColor;
                
                // swapping colour appropriately to the given colour
                if(board.getCellList().get(position).getColor() == 1){
                    nextColor = 2;
                }else{
                    nextColor = 1;
                }
                                
                // checking all the positions that are to the left from the 
                // given position
                try{
                    // variable to hold the maximal value of the lane
                    int onlyWholeValue = position/8;
                    // variable to hold the minimal value in the lane 
                    int lowerCoefficient = onlyWholeValue *8;
                    // adding the postion to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements in the lane to left from given position
                    for (int i = position; i > lowerCoefficient; i--) {
                        // if the colour of the next element is the opposite
                        // then it can be added to the list
                        if (board.getCellList().get(i-1).getColor() == nextColor) {
                            temp.add(board.getCellList().get(i-1).getId());
                        }
                        // if an element of the same colour is hit, the flipping can begin
                        else if(board.getCellList().get(i-1).getColor() == board.getCellList().get(position).getColor()){
                            // the size of swapped elements should be greater than
                            // one otherwise there is nothing to flip inbetween
                            if(temp.size() > 1){
                                // a loop to go through all of the items to be flipped
                                for (Integer item : temp) {
                                    // if the element in placed position is white
                                    if(board.getCellList().get(position).getColor()==1){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to whites colour
                                            board.getCellList().get(item).setColor(1);
                                            // placing white icon on the board
                                            board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                        // if the element in placed position is black
                                    }else if(board.getCellList().get(position).getColor()==2){
                                        // checking if the move is real otherwise
                                        // it is only to gather possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to black colour
                                            board.getCellList().get(item).setColor(2);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getBlackIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                }
                                // if the move is possible add it to the possible move list
                                possibleMoves.add(cell.getId());
                                // exit the loop
                                i=0;
                            }
                            // exit the loop
                            i=0;
                            // delete all the items from temporary storage
                            temp.clear();   
                        }
                        else{
                            // exit the loop
                            i=-1;
                        } 
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
                
                // checking all the positions that are to the right from the 
                // given position
                try{
                    /* usage of linear algerbra to get higher boundry of the line
                     * for instance, the element is placed in position of id 3 
                     * then, ((3/8)+1)*8 = (0+1)*8 = 8, thefore highest value in
                     * the lane is 8
                     */
                    int valueLine = ((position/8)+1)*8;
                    // adding the postion to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements in the lane to the right from given postion
                    for (int i = position; i < valueLine-1; i++) {
                        // if the colour of the next element is the opposite
                        // then it can be added to the list
                        if(board.getCellList().get(i+1).getColor() == nextColor){
                            temp.add(board.getCellList().get(i+1).getId());
                        }
                        // if an element of the same colour is hit, the flipping can begin
                        else if(board.getCellList().get(i+1).getColor() == board.getCellList().get(position).getColor()){
                            // the size of swapped elements should be greater than
                            // one otherwise there is nothing to flip inbetween
                            if(temp.size() > 1){
                                // a loop to go through all of the items to be flipped
                                for (Integer item : temp) {
                                    // if the element in placed position is white
                                    if(board.getCellList().get(position).getColor()==1){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to whites colour
                                            board.getCellList().get(item).setColor(1);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                    // if the element in placed position is black
                                    else if(board.getCellList().get(position).getColor()==2){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to black colour
                                            board.getCellList().get(item).setColor(2);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getBlackIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                }
                                // exit the loop
                                i=65;
                                // if the move is possible add it to the possible move list
                                possibleMoves.add(cell.getId());
                            }
                            // exit the loop
                            i=65;
                            // delete all the items from temporary storage
                            temp.clear();   
                        }
                        else{
                            // exit the loop
                            i=64;
                        } 
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();

                
                // checking all the positions that are to the top from given position
                try{
                    // iterating through all elements to the top of the board from
                    // the given position
                    for (int i = position; i >=0; i--) {
                        // adding the position to the list
                        temp.add(board.getCellList().get(position).getId());
                        // checking if the next top cell contains opposite colour piece
                        // if yes, then it can be added to the list for flipping
                        if(board.getCellList().get(i-8).getColor() == nextColor){
                            temp.add(board.getCellList().get(i-8).getId());
                        }
                        // checking if next top position is the same colour
                        else if(board.getCellList().get(i-8).getColor() == board.getCellList().get(position).getColor()){
                            // the size of swapped elements should be greater than
                            // one otherwise there is nothing to flip inbetween
                            if(temp.size() > 1){
                                // a loop to go through all of the items to be flipped
                                for (Integer item : temp) {
                                    // if the element in placed position is white
                                    if(board.getCellList().get(position).getColor()==1){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to white colour
                                            board.getCellList().get(item).setColor(1);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                            // changing boolean to acknowledge the change
                                            wasChanged = true;
                                        }
                                    // if the element in placed position is black
                                    }else if(board.getCellList().get(position).getColor()==2){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to black colour
                                            board.getCellList().get(item).setColor(2);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getBlackIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                }
                                // if the move is possible add it to the possible move list
                                possibleMoves.add(cell.getId());
                                // exit the loop
                                i=-1;
                            }
                            // exit the loop
                            i=-1;
                            // delete all the items from temporary storage
                            temp.clear();   
                        }
                        else{
                            // exit the loop
                            i=0;
                        }
                        // the next item is 8 positions lower, -1 will be subtracted
                        // once it enters the loop again
                        i=i-7;
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
                
                
                // checking all the positions that are to the bottom from given position
                try{
                    // iterating through all elements to the top of the board from
                    // the given position
                    for (int i = position; i <64; i++) {
                        // adding the position to the list
                        temp.add(board.getCellList().get(position).getId());
                        // checking if the next bottom cell contains opposite colour piece
                        // if yes, then it can be added to the list for flipping
                        if(board.getCellList().get(i+8).getColor() == nextColor){
                            temp.add(board.getCellList().get(i+8).getId());
                        }
                        // checking if next bottom position is the same colour
                        else if(board.getCellList().get(i+8).getColor() == board.getCellList().get(position).getColor()){
                            // the size of swapped elements should be greater than
                            // one otherwise there is nothing to flip inbetween
                            if(temp.size() > 1){
                              // a loop to go through all of the items to be flipped
                                for (Integer item : temp) {
                                    // if the element in placed position is white
                                    if(board.getCellList().get(position).getColor()==1){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to white colour
                                            board.getCellList().get(item).setColor(1);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                    // if the element in placed position is black
                                    else if(board.getCellList().get(position).getColor()==2){
                                        // if the move is real, otherwise it is only
                                        // gathering possible moves
                                        if(actualMove){
                                            // setting the pieces in-between to black colour
                                            board.getCellList().get(item).setColor(2);
                                            // placing an icon on the board
                                            board.getCellList().get(item).setIcon(board.getBlackIcon());
                                            // changing boolean to acknowledge the change 
                                            wasChanged = true;
                                        }
                                    }
                                }
                                // exit the loop
                                i=65;
                                // if the move is possible add it to the possible move list
                                possibleMoves.add(cell.getId());
                            }
                            // exit the loop
                            i=65;
                            // delete all the items from temporary storage
                            temp.clear();   
                        }
                        else{
                            // exit the loop
                            i=64;
                        }
                    // the next item is 8 positions higher, +1 will be added
                    // once it enters the loop again
                    i=i+7;
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
            
                
                // checking all the positions that are diagnoly from the right bottom
                // to the left top from given position
                try{
                    // adding the position to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements diagnoly from the right
                    // to the left from given position
                    for (int i = position; i >=0; i--) {
                        /* this can be done better, since i did not know how to
                         * approach the boundries of the board, this is the best
                         * i came up with in short period of time
                         */
                        if(i!=0 && i!=1 && i!=2 && i!=3 && i!=4 && i!=5 && i!=6 &&
                                    i!=8 && i!=16 && i!=24 && i!=32 && i!=40 && i!=48&& i!=56){
                            // checking if the next diagonal cell contains opposite colour piece
                            // if yes, then it can be added to the list for flipping
                            if(board.getCellList().get(i-9).getColor() == nextColor){
                                temp.add(board.getCellList().get(i-9).getId());
                            }
                            // checking if next diagonal position is the same colour
                            else if(board.getCellList().get(i-9).getColor() == board.getCellList().get(position).getColor()){
                                // the size of swapped elements should be greater than
                                // one otherwise there is nothing to flip inbetween
                                if(temp.size() > 1){
                                    // a loop to go through all of the items to be flipped
                                    for (Integer item : temp) {
                                        // if the element in placed position is white
                                        if(board.getCellList().get(position).getColor()==1){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to white colour
                                                board.getCellList().get(item).setColor(1);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                        // if the element in placed position is black
                                        else if(board.getCellList().get(position).getColor()==2){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to black colour
                                                board.getCellList().get(item).setColor(2);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getBlackIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                    }
                                    // exit the loop
                                    i=-1;
                                    // if the move is possible add it to the possible move list
                                    possibleMoves.add(cell.getId());
                                }
                                // exit the loop
                                i=-1;
                                // delete all the items from temporary storage
                                temp.clear();
                            }else{
                                // exit the loop
                                i = -1;
                            }
                            // the next item is 9 positions lower, 1 will be subtracted
                            // once it enters the loop again
                            i=i-8;
                        }else{
                            // exit the loop
                            i=-1;
                        }
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
                
                // checking all the positions that are diagnoly from left top
                // to the right bottom from given position
                try{
                    // adding the position to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements diagnoly from the left top
                    // to the right bottom from given position
                    for (int i = position; i <=63; i++) {
                        /* this can be done better, since i did not know how to
                         * approach the boundries of the board, this is the best
                         * i came up with in short period of time
                         */
                        if(i!=7 && i!=15 && i!=23 && i!=31 && i!=39 && i!=47 && i!=55 && i!=63 &&
                                i!=62 && i!=61 && i!=60 && i!=59 && i!=58 && i!=57 && i!=56){
                            // checking if the next diagonal cell contains opposite colour piece
                            // if yes, then it can be added to the list for flipping
                            if(board.getCellList().get(i+9).getColor() == nextColor){
                                temp.add(board.getCellList().get(i+9).getId());
                            }
                            // checking if next diagonal position is the same colour
                            else if(board.getCellList().get(i+9).getColor() == board.getCellList().get(position).getColor()){
                                // the size of swapped elements should be greater than
                                // one otherwise there is nothing to flip in-between
                                if(temp.size() > 1){
                                    // a loop to go through all of the items to be flipped
                                    for (Integer item : temp) {
                                        // if the element in placed position is white
                                        if(board.getCellList().get(position).getColor()==1){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to white colour
                                                board.getCellList().get(item).setColor(1);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                        // if the element in placed position is black
                                        else if(board.getCellList().get(position).getColor()==2){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to black colour
                                                board.getCellList().get(item).setColor(2);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getBlackIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                    }
                                    // exit the loop
                                    i=64;
                                    // if the move is possible add it to the possible move list
                                    possibleMoves.add(cell.getId());
                                }
                                // exit the loop
                                i=64;
                                // delete all the items from temporary storage
                                temp.clear();
                            }else{
                                // exit the loop
                                i = 64;
                            }
                            // the next item is 9 positions higher, 1 will be added
                            // once it enters the loop again
                            i=i+8;
                        }else{
                            // exit the loop
                            i=64;
                        }
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
                
                // checking all the positions that are diagnoly from the right top
                // to the left bottom from given position
                try{
                    // adding the position to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements diagnoly from the right top
                    // to the left bottom from given position
                    for (int i = position; i <=63; i++) {
                        /* this can be done better, since i did not know how to
                         * approach the boundries of the board, this is the best
                         * i came up with in short period of time
                         */
                        if(i!=0 && i!=8 && i!=16 && i!=24 && i!=32 && i!=40 && i!=48 && i!=56 &&
                                i!=57 && i!=58 && i!=59 && i!=60 && i!=61 && i!=62 && i!=63){
                            // checking if the next diagonal cell contains opposite colour piece
                            // if yes, then it can be added to the list for flipping
                            if(board.getCellList().get(i+7).getColor() == nextColor){
                                temp.add(board.getCellList().get(i+7).getId());
                            }
                            // checking if next diagonal position is the same colour
                            else if(board.getCellList().get(i+7).getColor() == board.getCellList().get(position).getColor()){
                                // the size of swapped elements should be greater than
                                // one otherwise there is nothing to flip in-between
                                if(temp.size() > 1){
                                    // a loop to go through all of the items to be flipped
                                    for (Integer item : temp) {
                                        // if the element in placed position is white
                                        if(board.getCellList().get(position).getColor()==1){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to white colour
                                                board.getCellList().get(item).setColor(1);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                        // if the element in placed position is black
                                        else if(board.getCellList().get(position).getColor()==2){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to black colour
                                                board.getCellList().get(item).setColor(2);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getBlackIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                    }
                                    // exit the loop
                                    i=64;
                                    // if the move is possible add it to the possible move list
                                    possibleMoves.add(cell.getId());
                                }
                                // exit the loop
                                i=64;
                                // delete all the items from temporary storage
                                temp.clear();
                            }else{
                                // exit the loop
                                i = 64;
                            }
                            // the next cell is 7 positions lower, 1 will be added
                            // once it enters the loop again
                            i=i+6;
                        }else{
                            // exit the loop
                            i=64;
                        }
                    }
                }
                // if iteration goes wrong error is caught. Used mainly for testing
                catch(IndexOutOfBoundsException e){}
                // when not going deeper into the loop elements are not cleared
                temp.clear();
                
                
                // checking all the positions that are diagnoly from the left bottom
                // to the right top from given position
                try{
                    // adding the position to the list
                    temp.add(board.getCellList().get(position).getId());
                    // iterating through all elements diagnoly from the left bottom
                    // to the right top from given position
                    for (int i = position; i > 0; i--) {
                        /* this can be done better, since i did not know how to
                         * approach the boundries of the board, this is the best
                         * i came up with in short period of time
                         */
                        if(i!=1 && i!=2 && i!=3 && i!=4 && i!=5 && i!=6 && i!=7 && i!=15 &&
                                i!=23 && i!=31 && i!=39 && i!=47 && i!=55&& i!=63){
                            // checking if the next diagonal cell contains opposite colour piece
                            // if yes, then it can be added to the list for flipping
                            if(board.getCellList().get(i-7).getColor() == nextColor){
                                temp.add(board.getCellList().get(i-7).getId());
                            }
                            // checking if next diagonal position is the same colour
                            else if(board.getCellList().get(i-7).getColor() == board.getCellList().get(position).getColor()){
                                // the size of swapped elements should be greater than
                                // one otherwise there is nothing to flip in-between
                                if(temp.size() > 1){
                                    // a loop to go through all of the items to be flipped
                                    for (Integer item : temp) {
                                        // if the element in placed position is white
                                        if(board.getCellList().get(position).getColor()==1){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to white colour
                                                board.getCellList().get(item).setColor(1);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getWhiteIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }else if(board.getCellList().get(position).getColor()==2){
                                            // if the move is real, otherwise it is only
                                            // gathering possible moves
                                            if(actualMove){
                                                // setting the pieces in-between to black colour
                                                board.getCellList().get(item).setColor(2);
                                                // placing an icon on the board
                                                board.getCellList().get(item).setIcon(board.getBlackIcon());
                                                // changing boolean to acknowledge the change 
                                                wasChanged = true;
                                            }
                                        }
                                    }
                                    // exit the loop
                                    i=0;
                                    // if the move is possible add it to the possible move list
                                    possibleMoves.add(cell.getId());
                                }
                                // exit the loop
                                i=0;
                                // delete all the items from temporary storage
                                temp.clear();
                            }else{
                                // exit the loop
                                i = 0;
                            }
                            // the next item is 7 positions lower, 1 will be subtracted
                            // once it enters the loop again
                            i=i-6;
                        }else{
                            // exit the loop
                            i=0;
                    }
                }
            }
            // if iteration goes wrong error is caught. Used mainly for testing
            catch(IndexOutOfBoundsException e){}
            // when not going deeper into the loop elements are not cleared
            temp.clear();
            
            // checking if its an actual move
            if(actualMove==false){
                // reseting the values, since it wasnt an actual move
                wasChanged =false;
            }
            // reseting cell if it wasn't an actual move
            if(wasChanged == false){
                cell.resetCell();
                board.getCellList().get(position).resetCell();
            }
            // if it was a move not only for generation change to opposite colour
            else{
                setColour();
            }
        }
    }
    
    /*
    * A method to calculate computer move
    * @param board the - current board
    */
    private void computerMove(Board board) throws IOException{
        // copying board
        Model.Board tempBoard = model.new Board(board);
        try{
            // setting current depth to 0
            level = 0;
            // check for the leaf change primary set to false
            leafChange = false;
            // copy of the board
            Model.Board brd = model.new Board(board);
            // setting board to root node
            Node root = new Node(brd);
            // set has needs an iterator to traverse through elements
            // simpler would be to use an array
            Object[] moveArray;
            // setting first node to be root
            Node nextNode = root;
            
            // a loop to traverse all items
            while(run){
                // creating next level of tree nodes
                if(depth != level){
                    if(!leafChange){
                        possibleMoves.clear();
                        // creating possible moves
                        genPossibleMoves(brd);
                        // copying possible moves
                        moveArray = possibleMoves.toArray();
                        // generating other node boards
                        genNodeBoards(brd, moveArray, nextNode);
                        // traversing leaf only once
                        leafChange=false;
                    }
                    
                    // setting the colour 
                    setColour();
                    // incrementing level
                    level++;
                    
                    // change from already generated nodes
                    if(ultimateCheck){
                        leafChange = false;
                        ultimateCheck = false;
                    }
                    
                    // setting the node alpha and beta values 
                    if(nextNode.getCounter() != nextNode.getChildren().size()){
                        nextNode = nextNode.getChildren().get(nextNode.getCounter());
                        nextNode.getBoard().setAlpha(nextNode.getParent().getBoard().getAlpha());
                        nextNode.getBoard().setBeta(nextNode.getParent().getBoard().getBeta());
                    }else{
                        // if changing leafe only get the parent node
                        leafChange = true;
                        nextNode = nextParent(nextNode);
                    }
                    // swapping board reference
                    brd = nextNode.getBoard();

                }else{
                    // if the level equals depth then just get next parent 
                    leafChange=true;
                    nextNode = nextParent(nextNode);
                }
            }
        }catch(Exception e){
            // just in case exception happened
            if(depth != 0){
                // lower the depth limit
                Model.Board brd = model.new Board(board);
                // decrease the depth
                depth--;
                // clearing possible moves
                possibleMoves.clear();
                // setting back to initial move colour
                setColour(initialColour);
                // generating new boards
                genPossibleMoves(tempBoard);
                // recursivelly call the same method
                computerMove(tempBoard);
            }else{
                // just reset colour
                setColour(initialColour);
            }
        }
        
        // resetting everything back to its correct form
        run = true;
        ultimateCheck = false;
        leafChange = false;
        setColour(initialColour);
        depth = initialDepth;
    }

    /**
     * A method to determine next parent from the tree
     * @param node - passing a node from the tree
     * @return either null either next parent (or recursively call the same method)
     */
    public Node nextParent(Node node){
        //checking if its root node, which obviously doesn't have a parent
        if(node.getCounter()==node.getChildren().size() && node.getParent() == null){
            // setting all the values to be equal to nothing therefore knowing
            // that we got the result
            int moveId = 0;
            int temp = -1000000;
            int index=0;
            
            // grabbing the biggest value when we got back to the root node
            for (int i = 0; i < node.getChildren().size(); i++) {
                if(node.getChildren().get(i).getBoard().getBoardValue()>temp){
                    temp = node.getChildren().get(i).getBoard().getBoardValue();
                    index = i;
                }
            }
            
            // the difference in boards is out computer move cell
            for (int i = 0; i < 64; i++) {
                if(node.getChildren().get(index).getBoard().getCellList().get(i).getTaken()
                        !=node.getBoard().getCellList().get(i).getTaken()){
                    moveId = i;
                }
            }
            // computer move id pass
            computerMoveId = moveId;
            // if we get here then terminate the while loop from generation
            run = false;
            return null;
        }else{
            // if its a leaf node assign value
            if(level == depth){
                assignValue(node.getBoard());
            }
            /* checking if the parents counter is not equal to the size othervise it means
            *  that we iterated through all of the nodes and we recurively call the same
            * method to get the parents parent
            */          
            if(node.getParent().getCounter()!= node.getParent().getChildren().size()-1){
                setColour();
                // we set this value to true, therefore when we go back we dont go back 
                // we don't visit the leaf anymore
                ultimateCheck =true;
                // when we go up the tree we decrement the levels
                level--;
                // checking for pruning
                alphaBetaNode(node,level);
                node = node.getParent();
                // increasing parents counter therefore we visit other child next time
                node.incCounter();
                // returning node
                return node;
            }else{
                // swapping colour
                setColour();
                // decrementing levels
                level--;
                // checking for pruning
                alphaBetaNode(node,level);
                // passing the reference of the parent node
                node = node.getParent();
                // incrementing the counter
                node.incCounter();
                // recursively calling the same method
                return nextParent(node);
            }
        }
    }
    
    /**
     * A method for alpha-beta pruning
     * @param node - node of consideration
     * @param level - current level of the board
     */
    public void alphaBetaNode(Node node, int level){
        // if the level division without remainder means that it is an even
        // max node
        if(level%2 ==0){
            // checking if already has value if already has then check if
            // parent node value is higher
            if(node.getParent().getBoard().getBoardValue()!=null){
                if(node.getParent().getBoard().getBoardValue()<node.getBoard().getBoardValue()){
                    node.getParent().getBoard().setBoardValue(node.getBoard().getBoardValue());
                    if(node.getParent().getBoard().getAlpha()<node.getBoard().getBoardValue()){
                       node.getParent().getBoard().setAlpha(node.getBoard().getBoardValue()); 
                    }
                }
                // check for pruning and if pruning then delete the possible nodes
                if(node.getParent().getBoard().getAlpha()>=node.getParent().getBoard().getBeta()){
                    // using iterator to delete the items, because otherwise
                    // lists are immutable
                    Iterator it = node.getParent().getChildren().iterator();
                    int index = 0;
                    // checking if iterator has next item
                    while(it.hasNext()){
                        it.next(); 
                        if(index > node.getParent().getCounter()){
                            it.remove();
                        }
                        // incrementing index for iterator
                        index++;
                    }
                }
            // if it is the first time for the node to be approached, just passing the value
            }else{
                // setting board value
                node.getParent().getBoard().setBoardValue(node.getBoard().getBoardValue());
                if(node.getParent().getBoard().getAlpha()<node.getBoard().getBoardValue()){
                    node.getParent().getBoard().setAlpha(node.getBoard().getBoardValue()); 
                }
                // if any pruning is possible prune
                if(node.getParent().getBoard().getAlpha()>=node.getParent().getBoard().getBeta()){
                    // using iterator to delete the items, because otherwise
                    // lists are immutable
                    Iterator it = node.getParent().getChildren().iterator();
                    int index = 0;
                    // checking if iterator has next item
                    while(it.hasNext()){
                        it.next(); 
                        if(index > node.getParent().getCounter()){
                            it.remove();
                        }
                        // incrementing index for iterator
                        index++;
                    }
                }
            }
        // if the level division without remainder means that it is an uneven
        // min node
        }else{
            // checking if node value is not null
            if(node.getParent().getBoard().getBoardValue()!=null){
                
                // checking if the parent value is larger than current nodes if 
                // yes then swap checking beta values to set
                if(node.getParent().getBoard().getBoardValue()>node.getBoard().getBoardValue()){
                    node.getParent().getBoard().setBoardValue(node.getBoard().getBoardValue());
                    if(node.getParent().getBoard().getBeta()>node.getBoard().getBoardValue()){
                        node.getParent().getBoard().setBeta(node.getBoard().getBoardValue());
                    }
                }
                // if any pruning is possible prune
                if(node.getParent().getBoard().getAlpha()>=node.getParent().getBoard().getBeta()){
                    // using iterator to delete the items, because otherwise
                    // lists are immutable
                    Iterator it = node.getParent().getChildren().iterator();
                    int index = 0;
                    // checking if iterator has next item
                    while(it.hasNext()){
                        it.next(); 
                        if(index > node.getParent().getCounter()){
                            it.remove();
                        }
                        // incrementing index for iterator
                        index++;
                    }
                }
            // if it is the first time for the node to be approached, just passing the value
            }else{
                // if this is the first node encountered then set the value
                node.getParent().getBoard().setBoardValue(node.getBoard().getBoardValue());
                if(node.getParent().getBoard().getBeta()>node.getBoard().getBoardValue()){
                    node.getParent().getBoard().setBeta(node.getBoard().getBoardValue());
                }
                // if any pruning is possible prune
                if(node.getParent().getBoard().getAlpha()>=node.getParent().getBoard().getBeta()){
                    // using iterator to delete the items, because otherwise
                    // lists are immutable
                    Iterator it = node.getParent().getChildren().iterator();
                    int index = 0;
                    // checking if iterator has next item
                    while(it.hasNext()){
                        it.next(); 
                        if(index > node.getParent().getCounter()){
                            it.remove();
                        }
                        // incrementing index for iterator
                        index++;
                    }
                }
            }
        }
    }
    
    /**
     * A method to generate other boards from the current board
     * @param brd - the board to be passed
     * @param moveArray - all of the possible moves
     * @param olderNode - parent node
     */
    public void genNodeBoards(Board brd, Object[] moveArray,Node olderNode){
        for (int i = 0; i < moveArray.length; i++) {
            // copy of the board
            Model.Board tempBoard = model.new Board(brd);
            // taking every cell item from the array
            Cell cell = new Cell((int)moveArray[i]);
            // setting the cell to be taken for strategy 3
            tempBoard.setCellMoveId((int)moveArray[i]);
            // performing a move
            move(cell,tempBoard,true);
            // setting a parent to the new node along with board
            Node newNode = new Node(olderNode,tempBoard);
            newNode.getBoard().setCellToBeTaken((int)moveArray[i]);
            // setting a child to a new node
            olderNode.addChild(newNode);
            // swapping colours
            setColour();
        } 
    }

    /**
     * Method  to generate all possible moves
     * @param board - The current board
     */
    public void genPossibleMoves(Board board){
        for (int i = 0; i < 64; i++) {
            Cell cell = board.getCellList().get(i);
            move(cell,board,false);
        }
    }
    
    /**
     * Method to assign the value to the board
     * @param board - current board
     */
    public void assignValue(Board board){
        // sepparating by difficulty
        if(getDifficulty() ==1){
            // swapping colours for assigment
            setColour();
            // creating a placeholder for 2 counters
            int[] values = {0,0};
            // for every cell of the colour increment the placeholder
            for (int i = 0; i < 64; i++) {
                if(board.getCellList().get(i).getColor()==1){
                    values[0]++;
                }else if(board.getCellList().get(i).getColor()==2){
                    values[1]++;
                }
            }
            // multiplying the counter by 25
            values[0] = values[0]*25;
            values[1] = values[1]*25;
            // assigning the value by approapriate 
            if(getColour() == 1){
                board.addToValue(values[0]);
            }else{
                board.addToValue(values[1]);
            }
            // setting colour colour
            setColour();
        
        // if difficulty set to 2
        }else if(getDifficulty() == 2){
            // change the current colour
            setColour();
            // creating a placeholder for 2 counters
            int[] values = {0,0};
            // for every cell of the colour increment the placeholder
            for (int i = 0; i < 64; i++) {
                if(board.getCellList().get(i).getColor()==1){
                    values[0]++;
                }else if(board.getCellList().get(i).getColor()==2){
                    values[1]++;
                }
            }
            // multiplying the counter by 25
            values[0] = values[0]*25;
            values[1] = values[1]*25;
            // assigning the value by approapriate 
            if(getColour() == 1){
                board.addToValue(values[0]);
            }else{
                board.addToValue(values[1]);
            }
            // returning the colour back
            setColour();
            // reacreating the possible moves
            possibleMoves.clear();
            // generating new moves
            genPossibleMoves(board);
            // multiplying the count of possible moves
            int temp = possibleMoves.size()*40;
            // adding to board value the count value
            board.addToValue(temp);
            // clearing the possible mvoes
            possibleMoves.clear();
        // if the number of difficulty is 3 
        }else if(getDifficulty() ==3){
            // add the value to be taken
            board.addToValue(board.getPositionValue(board.getCellToBeTaken()));
        }        
    }
    
    // Exit button action listener
    private class ExitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            System.exit(0);
        }
    }
    
    // new game listener, responsible for setting difficulty and new board, etc...
    private class NewGameListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.remove(model.getMain());
            view.add(model.getSelectDifficulty());
            view.revalidate();
            view.repaint();
        }
    }
    
    // instruction pane action listener, repaints the whole frame to instruction pane
    private class InstructionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.remove(model.getMain());
            view.addSomething(model.getInstPane());
            view.setSize(600, 527);
            view.revalidate();
            view.repaint();
            
        }
    }
    
    // back button action listener
    private class BackListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.remove(model.getInstPane());
            view.add(model.getMain());
            view.revalidate();
            view.repaint();
        }
        
    }

    /**
     * Method that creates the disc image on JButton
     * @param color - the colour disk should be painted
     * @return image icon
     */
    public Icon createPosIcon(Color color) {
            BufferedImage img = new BufferedImage(60,60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int gap = 27;
            int w = 60 - 2 * gap;
            int h = w;
            g2.fillOval(gap, gap, w, h);
            g2.dispose();
            return new ImageIcon(img);
    }
    
    /**
     * method responsible to recreate everything if new game will be started
     * ex. all of the values.
     */
    public void reinitialiseBoard(){
            cell = new Cell(0);
            possibleMoves = new HashSet<>();
            moveColour = 2;
            invalidCount = 0;
            level = 0;
            leafChange = false;
            ultimateCheck = false;
            run = true;
            depth = 7;
            initialDepth = 7;
            computerMoveId = -60;
            initialColour = 1;
            
            view.remove(model.getSelectDifficulty());
            
            board = model.genNewBoard();

            // generating the inicial valid moves
            for (int i = 0; i < 64; i++) {
                cell = board.getCellList().get(i);
                move(cell,board,false);
            }
            setColourPossibleMove(possibleMoves,board);
            view.add(board);
            view.revalidate();
            view.repaint();
    }
    
    /**
     * Method to set difficulty used in the game
     * @param difficulty - the selected difficulty
     */
    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }
    
    /**
     * Method to get difficulty
     * @return current difficulty
     */
    public int getDifficulty(){
        return difficulty;
    }
    
    // action listener setting the difficulty to easy
    private class easyDifficultyListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            setDifficulty(1);
            reinitialiseBoard();
        }

    }
    
    // action listener setting the difficulty to mid
    private class normalDifficultyListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            setDifficulty(2);
            reinitialiseBoard();
        }
    }
    
    // action listener for hard difficulty
    private class hardDifficultyListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            setDifficulty(3);
            reinitialiseBoard();
        }
    }
}