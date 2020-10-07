package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.Gson;

import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Message;
import models.Player;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameTest {
	 /**
	    * Runs only once before the testing starts.
	    */
	    @BeforeAll
		public static void init() {
			// Start Server
	    	PlayGame.main(null);
	    	System.out.println("******************************Before All**************************");
	    }
		
	    /**
	    * This method starts a new game before every test run. It will run every time before a test.
	    */
	    @BeforeEach
	    public void startNewGame() {
	    	// Test if server is running. You need to have an endpoint /
	        // If you do not wish to have this end point, it is okay to not have anything in this method.
	    	Unirest.get("http://localhost:8080/").asEmpty();

	    	System.out.println("-----------------Before Each----------\n");
	    }
		
	    /**
	    * This is a test case to evaluate the newgame endpoint.
	    */
	    @Test
	    @Order(1)
	    public void newGameTest() {
	    	System.out.println("~tests to evaluate /newgame~");
	    	// Create HTTP request and get response
	        HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
	        int restStatus = response.getStatus();
	        
	        // Check assert statement (New Game has started)
	        assertEquals(200,restStatus);
	        System.out.println("....restStatus is: " + restStatus);
	        System.out.println("All Good: new game started");
	    }
	    
	    /**
	    * This is a test case to evaluate the startgame endpoint.
	    */
	    @Test
	    @Order(2)
	    public void startGameTest() {
	    	
	    	// Create a POST request to startgame endpoint and get the body
	        // Remember to use asString() only once for an endpoint call. Every time you call asString(), a new request will be sent to the endpoint. Call it once and then use the data in the object.
	        HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
	        String responseBody = response.getBody();
	        
	        // --------------------------- JSONObject Parsing ----------------------------------
	        
	        System.out.println("Start Game Response: " + responseBody);
	        
	        // Parse the response to JSON object
	        JSONObject jsonObject = new JSONObject(responseBody);

	        // Check if game started after player 1 joins: Game should not start at this point
	        assertEquals(false, jsonObject.get("gameStarted"));
	        
	        // ---------------------------- GSON Parsing -------------------------
	        
	        // GSON use to parse data to object
	        Gson gson = new Gson();
	        GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
	        Player player1 = gameBoard.getP1();
	        
	        // Check if player type is correct 
	        assertEquals('X', player1.getType());
	        
	        System.out.println("All Good: Start Game");
	    }
	    
	    /**
	     * This is a test case to evaluate the joingame endpoint.
	     */
	    @Test
	    @Order(3)
	    public void joinGameTest() {
	        System.out.println("~tests to evaluate /joingame~");
	      
	        //start a new game and then join the game 
  	        Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty();
  	        Unirest.get("http://localhost:8080/joingame").asEmpty();
  	        HttpResponse<String> boardResponse = Unirest.get("http://localhost:8080/boardstate").asString();
  	            
  	        
  	        //get the response body and extract message. 
  	        String responseBody = boardResponse.getBody();
  	        System.out.println("...responseBody: " + responseBody);
  	        JSONObject jsonBody = new JSONObject(responseBody);
  	        Gson gson = new Gson();
  	        GameBoard board = gson.fromJson(jsonBody.toString(), GameBoard.class);
  	        
  	        
  	        int pID1 = board.getP1().getId();
  	        int pID2 = board.getP2().getId();
  	        
  	        
  	        assertEquals(1, pID1);
  	        assertEquals(2, pID2);
  	        System.out.println("All Good: P1 & P2 joined game");
	    }
	    
	    
	    /*
	     * This test that a player cannot make a move until both players have joined the game. --1
	     */
	    @Test
	    @Order(4)
	    public void preemptiveMove() {
	      
	        System.out.println("~tests to evaluate /preemptiveMove~");
	        //starts the game with player 1 as X
	        Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty();
  	         
	        //user attempts to make an incorrect move 
	        HttpResponse<String> moveResponseF = Unirest.post("http://localhost:8080/move/1").body("X=0&Y=0").asString();
	        
	        
  	        
	        //checks status after move made
	        String responseBody = moveResponseF.getBody();
  	        JSONObject jsonBody = new JSONObject(responseBody);
  	        System.out.println("...responseBody: " + responseBody);
  	        Gson gson = new Gson();
  	        Message msg = gson.fromJson(jsonBody.toString(), Message.class);
  	        System.out.println("msg: " + msg);
  	        int msgCode = msg.getCode();
  	        assertEquals(340, msgCode);
  	        System.out.print("All Good: p1 cannot move before p2");
	    }  
	    
	    /*
	     * This test that after game has started Player 1 always makes the first move. ---2
	     */
	    @Test
	    @Order(5)
	    public void player1GoesFirst() {
	        
	        System.out.println("~~~tests to evaluate /player1GoesFirst~~~");
  	    	//starts the game with player 1 as X
	    	Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty(); 
	    	Unirest.get("http://localhost:8080/joingame").asEmpty();
	    	
	    	//user attempts to make an incorrect move 
	        HttpResponse<String> moveResponseF = Unirest.post("http://localhost:8080/move/2").body("X=0&Y=0").asString();
	        
	        //checks status after move made
	        String responseBody = moveResponseF.getBody();
	        JSONObject jsonBody = new JSONObject(responseBody);
	        System.out.println("...responseBody: " + responseBody);
	        Gson gson = new Gson();
	        Message msg = gson.fromJson(jsonBody.toString(), Message.class);
	        int msgCode = msg.getCode();
	        assertEquals(9000, msgCode);
	        System.out.print("All Good: Player2 does not move b4 Player1");
	    }
	    
	    
	    /*
	     * This test that a player cannot make two moves in their turn 
	     */
	    @Test
	    @Order(6)
	    public void noDoubleMoves() {
	      
	        System.out.println("~~test for no double moves~~");
	    	//starts the game with player 1 as X
	    	Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty();
	    	Unirest.get("http://localhost:8080/joingame").asEmpty();
	    	Unirest.post("http://localhost:8080/move/1").body("X=1&Y=1").asEmpty();
	    	
	    	//user attempts to make an incorrect move 
	        HttpResponse<String> moveResponseF = Unirest.post("http://localhost:8080/move/1").body("X=0&Y=0").asString();
	        
            
	        //checks status after move made
	        String responseBody = moveResponseF.getBody();
	        JSONObject jsonBody = new JSONObject(responseBody);
	        System.out.println("...responseBody: " + responseBody);
	        Gson gson = new Gson();
	        Message msg = gson.fromJson(jsonBody.toString(), Message.class);
	        int msgCode = msg.getCode();
	        
	        assertEquals(9000, msgCode);
	        System.out.print("All Good: Player 1 tries to move again and fails");
	    }
	    
	    /*
	     * This test that a player can win the game
	     */
	    @Test
	    @Order(7)
	    public void playersCanWin() {
	      
	        System.out.println("~~test for a player can win~~");
	        
	    	//starts the game with player 1 as X
	    	Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty();
	    	Unirest.get("http://localhost:8080/joingame").asEmpty();
	    	
	    	//make moves s.t P1 wins diagonally
	    	Unirest.post("http://localhost:8080/move/1").body("X=0&Y=0").asEmpty();
	    	Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asEmpty();
	    	Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asEmpty();
	        Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asEmpty();
	        
	    	
	    	//user makes winning move
	        HttpResponse<String> gameEndResponse = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
	        
	        //checks status after move made
	        String responseBody = gameEndResponse.getBody();
	        JSONObject jsonBody = new JSONObject(responseBody);
	        System.out.println("...responseBody: " + responseBody);
	        Gson gson = new Gson();
	        Message msg = gson.fromJson(jsonBody.toString(), Message.class);
	        int msgCode = msg.getCode();
	        
	        
	        assertEquals(101, msgCode);
	        System.out.print("All Good: Player 1 can win");
	    }
	    
	    
	    /*
         * This test that there can be a draw game 
         */
        @Test
        @Order(8)
        public void playersCanDraw() {
            System.out.println("~~test for a player can draw~~");
          
            //starts the game with player 1 as X
            Unirest.post("http://localhost:8080/startgame").body("type=X").asEmpty();
            Unirest.get("http://localhost:8080/joingame").asEmpty();
            
            //make moves that lead to a tie
            Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asEmpty();
            Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asEmpty();
            
            Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asEmpty();
            Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asEmpty();
            
            Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asEmpty();
            
            
                       

            HttpResponse<String> gameEndResponse = Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
                
            
            //checks status after move made
            String responseBody = gameEndResponse.getBody();
            JSONObject jsonBody = new JSONObject(responseBody);
            System.out.println("...responseBody: " + responseBody);
            Gson gson = new Gson();
            Message msg = gson.fromJson(jsonBody.toString(), Message.class);
            int msgCode = msg.getCode();
            
            assertEquals(389, msgCode);
            System.out.print("All Good: Cats Game");
        }
	    
        /*
         * This test that after game has ended if a player moves 
         */
        @Test
        @Order(9)
        public void endGameMove() {
            
            System.out.println("~~~tests to when a user makes a move after game over~~~");
            //starts the game with player 1 as X
            Unirest.post("http://localhost:8080/startgame").body("type=O").asEmpty(); 
            Unirest.get("http://localhost:8080/joingame").asEmpty();
            
            //make moves that lead to a tie
            Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asEmpty();
            Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asEmpty();
            
            Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asEmpty();
            Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asEmpty();
            
            Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asEmpty();
            Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asEmpty();
            Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asEmpty();
            
            HttpResponse<String> gameEndResponse = Unirest.post("http://localhost:8080/move/2").body("x=2&y=1").asString();
            
            //checks status after move made
            String responseBody = gameEndResponse.getBody();
            JSONObject jsonBody = new JSONObject(responseBody);
            System.out.println("...responseBody: " + responseBody);
            Gson gson = new Gson();
            Message msg = gson.fromJson(jsonBody.toString(), Message.class);
            int msgCode = msg.getCode();
            assertEquals(345, msgCode);
            System.out.print("All Good: ferris was beulered");
        }
	    
        /*
         * This test that after game has ended if a player moves 
         */
        @Test
        @Order(10)
        public void tryInvalidMove() {
            
            System.out.println("~~~tests when a user makes a move on a spot already taken~~~");
            
            //starts the game with player 1 as X
            Unirest.post("http://localhost:8080/startgame").body("type=O").asEmpty(); 
            Unirest.get("http://localhost:8080/joingame").asEmpty();
            
            //make moves that lead to a tie
            Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asEmpty();
            HttpResponse<String> gameEndResponse = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
            
            //checks status after move made
            String responseBody = gameEndResponse.getBody();
            JSONObject jsonBody = new JSONObject(responseBody);
            System.out.println("...responseBody: " + responseBody);
            Gson gson = new Gson();
            Message msg = gson.fromJson(jsonBody.toString(), Message.class);
            int msgCode = msg.getCode();
            assertEquals(270, msgCode);
            System.out.print("All Good: that's an invalid move");
        }
	    
	    /**
	    * This will run every time after a test has finished.
	    */
	    @AfterEach
	    public void finishGame() {
	    	System.out.println("\n--------------After Each-----------------");
	    }
	    
	    /**
	     * This method runs only once after all the test cases have been executed.
	     */
	    @AfterAll
	    public static void close() {
		// Stop Server
	    	PlayGame.stop();
	    	System.out.println("**********************After All*******************************");
	    }
	    
	   
	    
}
