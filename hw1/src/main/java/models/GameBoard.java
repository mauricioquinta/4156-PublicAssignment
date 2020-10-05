package models;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;
  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  /**
   * creates GameBoard; assumes first player starts game and is initiated with game.
   */
  public GameBoard() {
    this.gameStarted = false;
    this.turn = 1; 
    this.boardState = new char[3][3];
    this.winner = 0;
    this.isDraw = false;
    
  }
  
  // Set and Get Player variables
  public Player getP1() {
    return this.p1;
  }
  
  public void setP1(Player p) {
    this.p1 = p;
  }
  
  public Player getP2() {
    return this.p2;
  }
  
  public void setP2(Player p) {
    this.p2 = p;
  }
  
  // set & get gameStart state 
  public boolean getGameStarted() {
    return this.gameStarted;
  }
  
  public void setGameStarted(boolean state) {
    this.gameStarted = state;
  }
  
  // set and get turn 
  public int getTurn() {
    return this.turn;
  }
  
  public void setTurn(int i) {
    this.turn = i;
  }
  
  //set and get board state
  public char [][] getBoardState() {
    return this.boardState;
  }
  
  /**
   * this sets the board state taking in a move.
   * @param m move variable has all the info to change board state 
   */
  public void setBoardState(Move m) {
    int x = m.getMoveX();
    int y = m.getMoveY();
    char t = m.getPlayer().getType();
    this.boardState[x][y] = t;
  }
  
  public int getWinner() {
    return this.winner;
  }
  
  public void setWinner(int p) {
    this.winner = p;
  }
  
  public boolean getIsDraw() {
    return this.isDraw;
  }
  
  public void setIsDraw(boolean tf) {
    this.isDraw = tf;
  }
  
  
  
  
  
}
