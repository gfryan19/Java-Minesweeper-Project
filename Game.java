/*----------------------------------------------------------------
 *  Author:   K. Walsh
 *  Email:    kwalsh@holycross.edu
 *  Written:  7/13/2015
 *  
 *  Minesweeper game. This class implements the game window and most
 *  of the game logic.
 *----------------------------------------------------------------*/

import GUI.*;

/**
 * A <i>Game</i> object manages all information about a minesweeper game as it
 * is being played and displayed on the screen. This includes information about
 * all of the cells (this is stored in a 2-D array of Cell objects), how many
 * flags have been planted, how many mines have been deployed, etc. Game extends
 * Window, so it can be drawn on the screen. It also extends EventListener so it
 * can respond to user interaction.
 */
public class Game extends Window implements EventListener {

    /**
     * Number of cells tall the game board will be.
     */
    public static final int NUM_ROWS = 20;

    /**
     * Number of cells wide the game board will be.
     */
    public static final int NUM_COLS = 30;

    // Example game screen layout:
    // +---------------------------------------------------------+
    // |      M A R G I N = 50                                   |
    // | M  + - - - - - - - - - - - - - - - - - - - - - - - + M  |
    // | A  |                                               | A  |
    // | R  |                                               | R  |
    // | G  |                Grid of Cells                  | G  |
    // | I  |                                               | I  |
    // | N  |                                               | N  |
    // | =  |       600 = NUM_COLS * Cell.SIZE wide         | =  |
    // | 50 |                      by                       | 50 |
    // |    |       400 = NUM_ROWS * Cell.SIZE tall         |    |
    // |    |                                               |    |
    // |    |                                               |    |
    // |    |                                               |    |
    // |    + - - - - - - - - - - - - - - - - - - - - - - - +    |
    // |            SPACE     S   SPACE   S    SPACE             |
    // |    + - - - - - - - + P + - - - + P + - - - - - - - +    |
    // |    |    Status     | A | Timer | A |     Help      |    |
    // |    |       Box     | C |       | C |      Box      |    |
    // |    + - - - - - - - + E + - - - + E + - - - - - - - +    |
    // |     M A R G I N = 50                                    |
    // +-- ------------------------------------------------------+

    /**
     * Width of the game window, in pixels.
     * Equal to 2*MARGIN + GRID_WIDTH
     * or 2*MARGIN + 2*SPACE + StatusBox.WIDTH, Timer.WIDTH, HelpBox.WIDTH,
     * whichever is larger.
     */
    public static final int WIDTH = 700;

    /**
     * Height of the game window, in pixels.
     * Equal to 2*MARGIN + SPACE
     *     + GRID_HEIGHT
     *     + max(StatusBox.Height, Timer.HEIGHT, HelpBox.HEIGHT)
     */
    public static final int HEIGHT = 600; 

    /**
     * Width of the grid part of the window, in pixels.
     * Equal to NUM_COLS * Cell.SIZE.
     */
    public static final int GRID_WIDTH = NUM_COLS * Cell.SIZE;

    /**
     * Height of the grid part of the window, in pixels.
     * Equal to NUM_ROWS * Cell.SIZE.
     */
    public static final int GRID_HEIGHT = NUM_ROWS * Cell.SIZE;

    /**
     * Margin around the edges of the canvas.
     */
    private static final int MARGIN = 50;

    /**
     * Space between elements on the canvas.
     */
    private static final int SPACE = 25;

    // A 2-D array of Cell objects to keep track of the board state.
    private Cell[][] cells = new Cell[NUM_ROWS][NUM_COLS];

    private int numMines = 0;    // number of mines deployed
    private int numRevealed = 0; // number of cells revealed so far
    private int numFlags = 0; // number of flags placed
    private int minesFlagged = 0; // number of mines correctly flagged

    // Whether or not the game has been won.
    private boolean gameWon = false;

    // Whether or not the game has been lost
    private boolean gameLost = false;

    // Name of the user playing the game.
    private String username;

    // The difficulty level of the game, used for tracking top scores.
    private String difficulty;

    // The status box that appears in the top left.
    private StatusBox status;

    // The timer that appears in the top middle.
    private Timer timer;

    // The help box that appears in the top right.
    private HelpBox help;

    /**
     * Constructor: Initializes a new game, but does not deploy any mines, plant
     * any flags, etc. The difficulty is either "easy", "medium", or "hard", and
     * will be used to load the proper top scores file. Name is used as the
     * user's name.
     */
    public Game(String name, String difficulty) {
        super("Minesweeper!", WIDTH, HEIGHT);

        this.username = name;
        this.difficulty = difficulty;

        // Create the background
        setBackgroundColor(Canvas.DARK_RED);

        // Create a border around the grid
        Box border = new Box(MARGIN-1.5, MARGIN-1.5, GRID_WIDTH+3, GRID_HEIGHT+3);
        border.setBackgroundColor(Canvas.DARK_GRAY);
        border.setBorderColor(Canvas.BLACK);
        add(border);

        // Create info boxes
        help = new HelpBox( WIDTH - MARGIN - HelpBox.WIDTH, HEIGHT - MARGIN - HelpBox.HEIGHT);
        add(help);
        
        timer = new Timer(MARGIN*5 + MARGIN/2, HEIGHT - MARGIN - HelpBox.HEIGHT);
        add(timer);
        
        status = new StatusBox(this, MARGIN, HEIGHT - MARGIN - StatusBox.HEIGHT);
        add(status);

        // Generates all of the cells in the grid
        for (int row = 0; row < NUM_ROWS; row++){
            for (int col = 0; col < NUM_COLS; col++){
                cells[row][col] = new Cell(MARGIN+Cell.SIZE*col, MARGIN+Cell.SIZE*row);
                add(cells[row][col]);
            }
        }
    }

    /**
     * Get the number of mines that are deployed.
     */
    public int getNumMinesDeployed() {
        return numMines;
    }

    /**
     * Get the number of hidden cells remaining to be revealed.
     */
    public int getNumCellsRemaining() {
        return NUM_ROWS * NUM_COLS - numRevealed;
    }

    /**
     * Deploy the given number of mines. This gets called once during game
     * setup. The game doesn't actually begin officially until the user clicks
     * a cell, so the timer should not start yet.
     */
    public void deployMines(int mines) {
        // Deploys mines randomly throughout the game board
        int x, y;
        while (numMines < mines){
            x = StdRandom.uniform(0, NUM_ROWS-1);
            y = StdRandom.uniform(0, NUM_COLS-1);
            if(!(cells[x][y].isMine())){
                cells[x][y].makeMine();
                for (int a = x-1; a < x+2; a++){
                    for (int b = y-1; b < y+2; b++){
                        if (a == -1 || b == -1 || a == NUM_ROWS || b == NUM_COLS){
                            //do nothing
                        }
                        else if(!cells[a][b].isMine()){
                            cells[a][b].incrementNeighborMineCount();  
                        }
                    }
                }
                numMines++;
            }
           
        }
        
    }

    /**
     * Respond to a mouse click. This function will be called each time the user
     * clicks on the game window. The x, y parameters indicate the screen
     * coordinates where the user has clicked, and the button parameter
     * indicates which mouse button was clicked (either "left", "middle", or
     * "right"). The function should update the game state according to what the
     * user has clicked.
     * @param x the x coordinate where the user clicked, in pixels.
     * @param y the y coordinate where the user clicked, in pixels.
     * @param button either "left", "middle", or "right".
     */
    public void mouseClicked(double x, double y, String button) {
        // User clicked the mouse, see what they want to do.

        // If game is over, then ignore the mouse click.
        if (gameWon || gameLost)
            return;

        // If the user middle-clicked, ignore it.
        if (!button.equals("left") && !button.equals("right"))
            return;

        // If the user clicked outside of the game grid, ignore it.
        if (x < MARGIN || y < MARGIN
            || x >= MARGIN + GRID_WIDTH || y >= MARGIN + GRID_HEIGHT) {
            return;
        }

        // Calculate which cell the user clicked.
        int row = (int)((y - MARGIN) / Cell.SIZE);
        int col = (int)((x - MARGIN) / Cell.SIZE);

        // React to mouse clicks.
        if (button.equals("left") && cells[row][col].flag() == true){
            return;
        }

        if (button.equals("left") && !cells[row][col].isRevealed){
            cells[row][col].reveal(); // reveales unrevealed cell
            numRevealed++;
        }
        StdOut.printf("You clicked row %d column %d with button %s\n", row, col, button);


        if (button.equals("right") && cells[row][col].isRevealed){
            // do nothing
        }
        else if(button.equals("right") && !cells[row][col].isRevealed){
            if (cells[row][col].flag() == false){
                cells[row][col].setFlag();// sets flags
                numFlags++;
            }
            else if (cells[row][col].flag() == true){
                cells[row][col].removeFlag();// removes flags
                numFlags--;
            }
        }
        if (button.equals("left") || button.equals("right")){
            timer.startCounting();// starts timer
        }
        if ((cells[row][col].isRevealed) && (cells[row][col].isMine)){ // Clicking on a mine results in the 
            timer.stopCounting();// stops timer                           game ending as a loss
            cells[row][col].setRedMine();// sets clicked mine to red
            gameLost = true;
            for (int i = 0; i < NUM_ROWS; i++){
                for (int j = 0; j < NUM_COLS; j++){
                    if ((!cells[i][j].isRevealed()) && (cells[i][j].isMine())){
                        cells[i][j].reveal();
                    }
                }
            }
            Label lose = new Label(WIDTH - MARGIN*7, MARGIN/2,"Game Over");
            lose.setFont("SansSerif Bold", 24);
            lose.setForegroundColor(Canvas.RED);
            lose.setBackgroundColor(Canvas.BLACK);
            lose.setBorderColor(Canvas.BLACK);
            add(lose);
        }
        if (((600 - numRevealed) == numMines) && !cells[row][col].isMine()){ //Clearing all cells that are not mines results in a win
            timer.stopCounting();
            gameWon = true;
            Label win = new Label(WIDTH - MARGIN*7, MARGIN/2,"You Win!");
            win.setFont("SansSerif Bold", 24);
            win.setForegroundColor(Canvas.RED);
            win.setBackgroundColor(Canvas.BLACK);
            win.setBorderColor(Canvas.BLACK);
            add(win);
        }
        if (numFlags == numMines){                   // if all mines are correctly flagged
            for (int a = 0; a < NUM_ROWS; a++){      // and there are no excess flags
                for (int b = 0; b < NUM_COLS; b++){  // then the game has been won
                    if (cells[a][b].isMine() && (cells[a][b].flag() == true)){
                        minesFlagged++;
                    }
                    if (numMines == numFlags && numMines == minesFlagged){
                        timer.stopCounting();
                        gameWon = true;
                        Label win = new Label(WIDTH - MARGIN*7, MARGIN/2,"You Win!");
                        win.setFont("SansSerif Bold", 24);
                        win.setForegroundColor(Canvas.RED);
                        win.setBackgroundColor(Canvas.BLACK);
                        win.setBorderColor(Canvas.BLACK);
                        add(win);
                    }
                }
            }
        }
            

        if (cells[row][col].getNeighborMineCount() == 0 && !cells[row][col].isMine()){
            autoComplete(row, col);
        }
        
    }
    // Autocomplete function
    public void autoComplete(int x, int y){ //reveals any cell surrounding a
        for (int a = x-1; a < x+2; a++){    //cell with a neighborMineCount of zero
                    for (int b = y-1; b < y+2; b++){
                        if (a == -1 || b == -1 || a == NUM_ROWS || b == NUM_COLS){
                            //do nothing
                        }
                        else if(cells[a][b].isRevealed == false){
                            cells[a][b].reveal();
                            numRevealed++;
                            if ((600 - numRevealed) == numMines){ // Allows a win if the first click on Easy mode
                                timer.stopCounting();             // Clears every cell excep the mines
                                gameWon = true;
                                Label win = new Label(WIDTH - MARGIN*7, MARGIN/2,"You Win!");
                                win.setFont("SansSerif Bold", 24);
                                win.setForegroundColor(Canvas.RED);
                                win.setBackgroundColor(Canvas.BLACK);
                                win.setBorderColor(Canvas.BLACK);
                                add(win);
                            }
                        
                            if(cells[a][b].getNeighborMineCount() == 0 && !cells[a][b].isMine()){ // Recursion
                                autoComplete(a, b);                                               // If one of the cleared cells has a
                            }                                                                     // neighborMineCount of zero
                        }
                    }
                }

    }
    

    /**
     * Respond to key presses. This function will be called each time the user
     * presses a key. The parameter indicates the character the user pressed.
     * The function should update the game state according to what character the
     * user has pressed. 
     * @param c the character that was typed.
     */
    public void keyTyped(char c)
    {
        String[] restart = new String[0];
        // User pressed a key, see what they want to do.
        switch (c) {
        case 'q': 
        case 'Q': 
            hide(); // user wants to quit
        break;
        default:
            break; // anything else is ignored
        }
    }

    /**
     * Paint the background for this window on the canvas. Don't call this
     * directly, it is called by the GUI system automatically. This function
     * should draw something on the canvas, if you like. Or the background can
     * be blank.
     * @param canvas the canvas on which to draw.
     */
    public void repaintWindowBackground(GUI.Canvas canvas) {

    }
}
