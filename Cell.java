/*----------------------------------------------------------------
 *  Authors:   K. Walsh and Greg Ryan
 *  Email:    kwalsh@holycross.edu, gfryan19@g.holycross.edu
 *  Written:  12/3/2015
 *  
 *  Each Cell object manages information about and draws a
 *  single "cell" of the game grid. 
 *----------------------------------------------------------------*/

import GUI.*;
import java.awt.Color;

/**
 * A <i>Cell</i> object holds all information about the state of a single cell
 * of the minesweeper game board. This includes:
 *   - whether a mine is hidden in this cell or not
 *   - how many of its neighboring cells contain mines
 *   - whether it has been revealed yet or is still hidden
 *   - whether it has been flagged or not
 * Each Cell object knows how to draw itself in a graphical window, and it will
 * draw itself in different styles depending on all the above state information.
 */
public class Cell extends Widget {

    /**
     * Size of one cell when it is drawn on the screen, in pixels.
     */
    public static final int SIZE = 20;

    /**
     * Whether a mine is hidden in this cell or not.
     */
    protected boolean isMine;

    // Whether or not the mine is the one clicked on
    protected boolean redMine = false;

    //Whether or not a cell has been flagged as a mine
    protected boolean flagged = false;

    /**
     * Whether this cell is "revealed" or not.
     */
    protected boolean isRevealed;

    /**
     * Count of how many neighboring cells have mines.
     */
    protected int neighborMineCount;

    /**
     * Constructor: Initialize a cell to be drawn at the given x, y coordinates
     * on the screen. The cell will be blank. That is, it will not be a mine,
     * and it will have no neighboring mines so a neighbor mine count of zero.
     */
    public Cell(int x, int y) {
        super(x, y, SIZE, SIZE);
        this.isMine = false;
        this.isRevealed = false;
        this.neighborMineCount = 0;
    }

    /**
     * Hide a mine in this cell
     */
    public void plantMine() {
        isMine = true;
    }

    /**
     * Returns true if a mine is hidden in this cell, otherwise returns false.
     */
    public boolean isMine() {
        return isMine;
    }

    // Sets this cell to be the mine that was clicked on
    public void setRedMine(){
        redMine = true;
    }

    // Returns whether or not this cell is the mine that was clicked on
    public boolean redMine(){
        return redMine;
    }

    // Sets this cell to be flagged 
    public void setFlag(){
        flagged = true;
    }

    // Sets this cell to be unflagged
    public void removeFlag(){
        flagged = false;
    }

    // Returns whether or not the cell has been flagged
    public boolean flag(){
        return flagged;
    }

    /**
     * Increment the neighbor mine count variable by one. 
     */
    public void incrementNeighborMineCount() {
        neighborMineCount++;
    }

    /**
     * Sets the neighbor mine count variable.
     */
    public void setNeighborMineCount(int count) {
        neighborMineCount = count;
    }

    /**
     * Returns the value of the neighbor mine count variable.
     */
    public int getNeighborMineCount() {
        return neighborMineCount;
    }

    /**
     * Change this cell so that it is "revealed" 
     */
    public void reveal() {
        isRevealed = true;
    }

    /**
     * Returns true if this cell is "revealed", otherwise returns false.
     */
    public boolean isRevealed() {
        return isRevealed;
    }

    /**
     * Hide a mine in this cell
     */
    public void makeMine() {
        isMine = true;
    }

    /**
     * Change this cell so that it shows the mine that is hiding in it.
     */
    public void showMine() {
        if (isMine)
            isRevealed = true;
    }

    /**
     * Check whether there are neighboring mines.
     */
    public boolean coastIsClear() {
        return (neighborMineCount == 0);
    }

    /**
     * Paint this cell on the canvas. Don't call this directly, it is called by
     * the GUI system automatically. This function should draw something on the
     * canvas. Usually the drawing should stay within the bounds (x, y, width,
     * height) which are protected member variables of GUI.Widget, which this
     * class extends.
     * @param canvas the canvas on which to draw.
     */
    public void repaint(GUI.Canvas canvas) {
        // Draws the gameEnding mine
        if ((isRevealed) && (isMine) && (redMine == true)){
            canvas.picture(x, y, "PMGameOver.png", SIZE, SIZE);  
        } // Reveals all remaining mines when the game is lost
        else if((isRevealed) && (isMine) && (redMine == false) && (flagged == false)){
            canvas.picture(x, y, "PoisonMushroom.png", SIZE, SIZE);
        } // Draws all cells that are unrevealed and not flagged
        else if (!isRevealed && (flagged == false)){
            canvas.setPenColor(Canvas.GRAY);
            canvas.raisedBevelRectangle(x, y, width, height, 4.0);
        } // Draws flags
        else if (!isRevealed && (flagged == true)){
            canvas.setPenColor(Canvas.GRAY);
            canvas.raisedBevelRectangle(x, y, width, height, 4.0);
            canvas.picture(x, y, "flag.png", SIZE, SIZE);
        } // Draws a blank rectangle for cells not touching any mines
        else if(isRevealed && !isMine && flagged == false){
            if (neighborMineCount == 0){
                canvas.setPenColor(Canvas.BLACK);
                canvas.setPenRadius(1.0);
                canvas.setPenColor(Canvas.DARK_GRAY);
                canvas.raisedBevelRectangle(x, y, width, height);
            } // Displays the neighborMineCount of this cell
            else if (neighborMineCount == 1){
                canvas.setPenColor(Canvas.BLUE);
                canvas.setFont();
                canvas.text(x+10, y+10, "1");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 2){
                canvas.setPenColor(Canvas.DARK_GREEN);
                canvas.setFont();
                canvas.text(x+10, y+10, "2");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 3){
                canvas.setPenColor(Canvas.RED);
                canvas.setFont();
                canvas.text(x+10, y+10, "3");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 4){
                canvas.setPenColor(Canvas.DARK_BLUE);
                canvas.setFont();
                canvas.text(x+10, y+10, "4");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 5){
                canvas.setPenColor(Canvas.DARK_RED);
                canvas.setFont();
                canvas.text(x+10, y+10, "5");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 6){
                canvas.setPenColor(Canvas.CYAN);
                canvas.setFont();
                canvas.text(x+10, y+10, "6");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 7){
                canvas.setPenColor(Canvas.YELLOW);
                canvas.setFont();
                canvas.text(x+10, y+10, "7");
            } // Displays the neighborMineCount of this cell
            else  if(neighborMineCount == 8){
                canvas.setPenColor(Canvas.PINK);
                canvas.setFont();
                canvas.text(x+10, y+10, "8");
            }
        }
        
        
        
    }

}
