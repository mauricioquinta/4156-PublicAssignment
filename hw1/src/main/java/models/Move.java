package models;

public class Move {

  private Player player;
  private int moveX;
  private int moveY;

  /**
   * constructor for a move.
   * @param p player who's moving
   * @param x value of move on board (0,2)
   * @param y value of move on board (0,2)
   */
  public Move(Player p, int x, int y) {
    this.player = p;
    this.moveX = x;
    this.moveY = y;
  }
  
  // get/set player 
  public Player getPlayer() {
    return this.player;
  }
  
  public void setPlayer(Player p) {
    this.player = p;
  }
  
  //get and set for move variables
  public int getMoveX() {
    return this.moveX;
  } 
  
  public void setMoveX(int x) {
    this.moveX = x;
  }
  
  public int getMoveY() {
    return this.moveY;
  }
  
  public void setMoveY(int y) {
    this.moveY = y;
  }
 

}