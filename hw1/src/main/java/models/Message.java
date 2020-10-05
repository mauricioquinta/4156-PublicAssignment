package models;

public class Message {

  private boolean moveValidity;

  private int code;

  private String message;
  
  /**
   * constructor for a message.
   * @param v the validity of move
   * @param c the code for the move
   * @param m the message attached to the move
   */
  public Message(boolean v, int c, String m) {
    this.moveValidity = v;
    this.code = c;
    this.message = m;
  }
  
  // set and get move validity 
  public boolean getMoveValidity() {
    return this.moveValidity;
  }
  
  public void setMoveValidity(boolean tf) {
    this.moveValidity = tf;
  }
  
  //set and get message code 
  public int getCode() {
    return this.code;
  }
  
  public void setCode(int c) {
    this.code = c;
  }
  
  //set and get message 
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(String m) {
    this.message = m;
  }

}
