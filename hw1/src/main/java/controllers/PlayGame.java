package controllers;

import com.google.gson.Gson; 
import io.javalin.Javalin;
import java.io.IOException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;

public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;
  
  public static GameBoard board;
  
  //Checks if the game is a draw after a move 
  public static boolean isDraw(GameBoard b) {
    for (char[] r : b.getBoardState()) {
      for (char a  : r) {
        if (a == '\u0000') {
          return false;
        }
      }
    }
    return true;
  }
  
  //Checks if the game is won after a move 
  public static boolean isWinningMove(GameBoard b) {
    char[][] bs = b.getBoardState();
    if ((bs[0][0] == bs[0][1] && bs[0][1] == bs[0][2] && bs[0][2] != 0)  //ROWS
        || (bs[1][0] == bs[1][1] && bs[1][1] == bs[1][2] && bs[1][2] != 0)
        || (bs[2][0] == bs[2][1] && bs[2][1] == bs[2][2] && bs[2][2] != 0)
        
        || (bs[0][0] == bs[1][0] && bs[1][0] == bs[2][0] && bs[2][0] != 0)  //COLLUMS
        || (bs[0][1] == bs[1][1] && bs[1][1] == bs[2][1] && bs[2][1] != 0) 
        || (bs[0][2] == bs[1][2] && bs[1][2] == bs[2][2] && bs[2][2] != 0) 
        
        || (bs[0][0] == bs[1][1] && bs[1][1] == bs[2][2] && bs[2][2] != 0) 
        || (bs[0][2] == bs[1][1] && bs[1][1] == bs[2][0] && bs[2][0] != 0) //diagonals
        
        
        ) {
      return true;
    }
    return false;
  }
  
  //Checks if move is valid according to board state
  public static boolean isValidMove(int x, int y, GameBoard b) {
    //Check to see if the move is valid and set variable 
    if (board.getBoardState()[x][y] != 0) {
      return false;
    } 
    return true;  
  }

  //Gets the player from the board acording to their ID = (1||2)
  public static Player getPlayerById(int id, GameBoard b) {
    if (id == 1) {
      return b.getP1();
    }
    return b.getP2();
  }
  
  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);


    app.get("/boardstate", ctx -> {
      //Change GameBoard object to JSON
      String jsonBoard = new Gson().toJson(board);
      
      //Send result back in JSON format 
      ctx.result(jsonBoard);
    });
    
    //Starts a new game
    app.get("/newgame", ctx -> {
      
      //create message to show new game is called/created
      Message msg = new Message(true, 200, "Game Start");
      
      //convert message to JSON; 
      String jsonMessage = new Gson().toJson(msg);
      ctx.result(jsonMessage);
      
      ctx.redirect("/tictactoe.html");
      
    });
    
    //Adds player 2 and starts game 
    app.post("/startgame", ctx -> {
      
      //Get the body response ie what symbol of p1 is format type=X
      String response = ctx.body();
      char moveType = response.charAt(response.length() - 1);   //extract type from last char
      
      //Create p1
      Player p1 = new Player(1, moveType);
     
      
      //Create a gameBoard
      board = new GameBoard();
      board.setP1(p1);
     
      //Change GameBoard object to JSON
      String jsonBoard = new Gson().toJson(board);
      
      //Send result back in JSON format 
      ctx.result(jsonBoard);
      
    });
    
    
    //handles the joining of player two 
    app.get("/joingame", ctx -> {
      
      //retrieve the moveType from board; set it opposite of opponent 
      char opType = board.getP1().getType();
      char moveType;
      
      if (opType == 'X') {
        moveType = 'O';
      } else { //opType == O
        moveType = 'X';
      }
      
      //create player two
      Player p2 = new Player(2, moveType);
      board.setP2(p2);
      board.setTurn(1);
      
      //start game
      board.setGameStarted(true);
      
      //create jason oard and send toplayers after redirecting 
      String jsonBoard = new Gson().toJson(board);
            
      ctx.redirect("/tictactoe.html?p=2");
      sendGameBoardToAllPlayers(jsonBoard);
      
      
    });
    
    
    //handles possible moves 
    app.post("/move/:playerId", ctx -> {
      
      //get player ID and response 
      String pidStr = ctx.pathParam("playerId");
      int playerId = Integer.parseInt(pidStr);
      String response = ctx.body();
      //get the x,y coordinates of move values hard coded since response is expected 
      int x = Integer.parseInt(String.valueOf(response.charAt(2)));
      int y = Integer.parseInt(String.valueOf(response.charAt(6)));
      
      //check if move is valid (ie taken up) and get player 
      boolean isValid = isValidMove(x, y, board);
      Player p = getPlayerById(playerId, board);
      
      //create move with the information 
      Move usrMove = new Move(p, x, y); 
      Message msg;
      
      
      
      //Case1:game is over and there's a winner or draw 
      if (!board.getGameStarted() && (board.getWinner() != 0 || board.getIsDraw()))  { 
        msg = new Message(false, 345, "you're still here? the game is over.. go home");
      
      //Case2: game has yet to start so no letting them move 
      } else if (!board.getGameStarted()) {  //game has not started
        msg = new Message(false, 340, "The game has not started, be patient.");
        
      //Case3: player is trying to go when not their turn 
      } else if ((board.getTurn() != playerId)) { //turn != ID 
        msg = new Message(false, 9000, "Please wait for opposing player to make move.");
        
      //Case4: valid move is made
      } else if (isValid) { 
        //MakeMove ~ change board state using the users move
        board.setBoardState(usrMove);
        
        //Case 4-1: if this is the winner then set message and end game 
        if (isWinningMove(board)) {
          msg = new Message(isValid, 101, "You've Totally Won Player: " + pidStr);
          board.setWinner(playerId);
          board.setGameStarted(false);
          
          //case4.2: there has occurred a tie 
        } else if (isDraw(board)) {
          board.setIsDraw(true);
          board.setGameStarted(false);
          msg = new Message(isValid, 389, "Cats Game; It is a Tie");
          
          //case 4-3: this is a valid move with no winner 
        } else {
          msg = new Message(isValid, 100, "");
        }
        
        //update who's turn it is on the board
        if (playerId == 1) {
          board.setTurn(2);
        } else { //turn == 2
          board.setTurn(1);
        }
        
      //case5: all good except someone marked there already 
      } else {
        msg = new Message(isValid, 270, "Error: move already played");
      }
      
     
      //convert board and message to JSON; send to user and update view 
      String jsonMessage = new Gson().toJson(msg);
      String jsonBoard = new Gson().toJson(board);
      sendGameBoardToAllPlayers(jsonBoard);
      ctx.result(jsonMessage);
      
      
    });
    
    

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
    
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
     
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
