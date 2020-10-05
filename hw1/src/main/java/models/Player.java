package models;

public class Player {

  private char type;
  private int id;
  
  //constructor: takes in player number p = ( 1 || 2) and type t = (X||O)
  public Player(int p, char t) {
    this.type = t;
    this.id = p; 
  }
  
  // get/set type 
  public char getType() {
    return this.type;
  }
  
  public void setType(char t) {
    this.type = t;
  }
  
  // get/set id 
  public int getId() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }

}
