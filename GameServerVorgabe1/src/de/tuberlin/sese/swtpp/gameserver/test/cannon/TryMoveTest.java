package de.tuberlin.sese.swtpp.gameserver.test.cannon;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	CannonGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "");
		
		game = (CannonGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(expectedBoard,game.getBoard().replaceAll("e", ""));
		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(whiteNext, game.isWhiteNext());
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	String initialBoard = "/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/";
	String initialBoardCityPlaced = "3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7";
	@Test
	public void exampleTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h6-h5",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}
	@Test
	public void cityDontMoveWhite() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("f9-f8",true, false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}	
	@Test
	public void cityDontMoveBlack() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("d0-c0",true, false);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}	
	@Test
	public void placeCityWhite() {
		startGame(initialBoard, true);
		assertMove("b9-b9",true, true);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	
	@Test
	public void placeCityBlack() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , false);
		assertMove("b0-b0",false, true);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1B8",true,false,false);
	}	

	@Test
	public void cityMoveWhiteFirst() {
		startGame(initialBoard, true);
		assertMove("b0-b0",true, false);
		assertGameState(initialBoard, true,false,false);
	}
	
	@Test
	public void cityMoveBlackFirst() {
		startGame(initialBoard, false);
		assertMove("b0-b0",false, false);
		assertGameState(initialBoard, false,false,false);
	}
	@Test
	public void cityNotMoveBlack() {
		startGame(initialBoardCityPlaced, false);
		assertMove("c0-d0",false, false);
		assertGameState(initialBoardCityPlaced,false,false,false);
	}
	@Test
	public void cityNotInCornerWhiteLeftCorner() {
		startGame(initialBoard , true);
		assertMove("a9-a9",true, false);
		assertGameState(initialBoard,true,false,false);
	}	
	@Test
	public void cityNotInCornerWhiteRightCorner() {
		startGame(initialBoard , true);
		assertMove("j9-j9",true, false);
		assertGameState(initialBoard, true,false,false);
	}	
	@Test
	public void cityNotInCornerBlackRightCorner() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", false);
		assertMove("j0-j0",false, false);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	
	@Test
	public void cityNotInCornerBlackLeftCorner() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", false);
		assertMove("a0-a0",false, false);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/", false,false,false);
	}	
	@Test
	public void soldierMove() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("f4-f5",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w2b4//b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}	
	@Test
	public void soldierCantAttackSameColorWhite() {
		startGame(initialBoardCityPlaced ,true);
		assertMove("b8-b7",true,false);
		assertGameState(initialBoardCityPlaced,true,false,false);
		
	}	
	@Test
	public void soldierCantAttackSameColorBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/3w6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,false);
		assertMove("c2-c3",false,false); 
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/3w6//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,false,false,false);
		
	}	
	@Test
	public void soldierAttack() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/2b7//b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("c5-b6",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1b1w1w1w1w///b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}	
	@Test
	public void soldierAttackRight() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1bww1w1w1w///b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("b6-c6",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/2bw1w1w1w///b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void soldierRetreatBlack() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/2bw1w1w1w///b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false);
		assertMove("c6-a4",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/3w1w1w1w//b9/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void soldierRetreatWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/3w1w1w1w//2w7/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("c4-c6",true,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void soldierRetreatUnPossibleWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/3w1w1w1w//3w6/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("d4-d6",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/3w1w1w1w//3w6/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonNotRightAmount() {
		startGame("2W7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("c2-c4",false,false);
		assertGameState("2W7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonRightAmountToMove() {
		startGame("2W7/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-b5",true,true);
		assertGameState("2W7/3w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1w8//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonStraightTopDontShootBlack() {
		startGame( "3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/8w1/8w1/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i1-i5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/8w1/8w1/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonStraightTopDontShootWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1b8/1b8/4b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-b4",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1b8/1b8/4b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonDiagonalLeftTopDontShootWhite() {
		startGame("3W6/1w3w1w1w/1www1w1w1w/1w1w1w1w1w/4b5/5b4/b5b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-f4",true,false);
		assertGameState("3W6/1w3w1w1w/1www1w1w1w/1w1w1w1w1w/4b5/5b4/b5b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonDiagonalRightTopDontShootWhite() {
		startGame("3W6/1w3w1w1w/1w1www1w1w/1w1w1w1w1w/2b7/1b8/4b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("f8-b4",true,false);
		assertGameState("3W6/1w3w1w1w/1w1www1w1w/1w1w1w1w1w/2b7/1b8/4b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonDiagonalRightTopDontShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/4w5/3w6/b1b1b1b1b1/bbb1b1b1b1/b3b1b1b1/2B7",false);
		assertMove("a1-e5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/4w5/3w6/b1b1b1b1b1/bbb1b1b1b1/b3b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalRightTopDontShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w5w1w/bbbww5//4b1b1b1/2b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("a5-e5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w5w1w/bbbww5//4b1b1b1/2b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalLeftTopDontShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w5w1w/4wwbbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i5-e5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w5w1w/4wwbbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalLeftTopBlockedDontMoveBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/ww5w1w/5wbbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i5-f5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/ww5w1w/5wbbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalRightShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/bbb1w5//4b1b1b1/2b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("a5-e5",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/bbb7//4b1b1b1/2b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonHorizontalLeftShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/4w1bbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i5-e5",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/6bbb1//b1b7/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonDiagonalLeftTopDontShootBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/2w7/3w6/b1b1b1b1b1/b1b1bbb1b1/b1b3b1b1/2B7",false);
		assertMove("g1-c5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/2w7/3w6/b1b1b1b1b1/b1b1bbb1b1/b1b3b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonDiagonalLeftMoveBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/2ww6//b1b1b1b1b1/b1b1bbb1b1/b1b3b1b1/2B7",false);
		assertMove("g1-d4",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/5w1w1w/2ww6/3b6/b1b1b1b1b1/b1b1bbb1b1/b1b5b1/2B7",true,false,false);
	}
	@Test
	public void cannonDiagonalRightMoveWhite() {
		startGame("3W6/1w3w1w1w/1w1www1w1w/1w1w1w1w1w//b9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("d6-g9",true,true);
		assertGameState("3W2w3/1w3w1w1w/1w1www1w1w/1w3w1w1w//b9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalRightMoveWhite() {
		startGame("3W6/1w1w1w1w1w/3w1w1w1w/1www1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("b6-e6",true,true);
		assertGameState("3W6/1w1w1w1w1w/3w1w1w1w/2wwww1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalLeftMoveWhite() {
		startGame("3W6/1w1w1w1w1w/3w1w1w1w/1www1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("d6-a6",true,true);
		assertGameState("3W6/1w1w1w1w1w/3w1w1w1w/www2w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonHorizontalRightMoveUnPossibleWhite() {
		startGame("3W6/1w1w1w1w1w/3w1w1w1w/1wwww2w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("b6-e6",true,false);
		assertGameState("3W6/1w1w1w1w1w/3w1w1w1w/1wwww2w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonHorizontalTooSmallWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("c6-e6",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonHorizontalTooSmallDontShootWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1b1w1w///w1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7" ,true);
		assertMove("c6-f6",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1b1w1w///w1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonShot() {
		startGame( "3W6/1w1w1w1w1w/1w1w1w1w1w/4ww1w1w/8w1//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i1-i5",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/4ww1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void whiteWon() {
		startGame( "3W6////////2w7/2B7",true);
		assertMove("c1-c0",true,true);
		assertGameState("3W6/////////2w7",false,true,true);
	}
	@Test
	public void blackWon() {
		startGame( "3W6/3b6////////2B7",false);
		assertMove("d8-d9",false,true);
		assertGameState("3b6/////////2B7",true,true,false);
	}
	@Test
	public void cannonStraightMoveDiagonal() {
		startGame( initialBoardCityPlaced,false);
		assertMove("a1-b4",false,false);
		assertGameState(initialBoardCityPlaced,false,false,false);
	}
	@Test
	public void cannonStraightMoveDiagonalWhite() {
		startGame( initialBoardCityPlaced,true);
		assertMove("b8-a5",true,false);
		assertGameState(initialBoardCityPlaced,true,false,false);
	}
	@Test
	public void threatInvalidRetreat() {
		startGame("5W4/1w1w1w1w1w/1w3w1w1w/2bw1w1w1w///bwb3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("d6-e7",true,false);
		assertGameState("5W4/1w1w1w1w1w/1w3w1w1w/2bw1w1w1w///bwb3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true,false,false);
	}
	@Test
	public void placeCityBlackA() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , false);
		assertMove("a1-a1",false, false);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	

	@Test
	public void placeCityBlackJ() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , false);
		assertMove("j1-j1",false, false);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	
	@Test
	public void placeCityBlackJWithoutW() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , false);
		assertMove("a1-j1",false, false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	

	@Test
	public void placeCityBlackBF() {
		startGame("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , false);
		assertMove("b0-f0",false, false);
		assertGameState("1W8/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",false,false,false);
	}	
	
	
	//WHITE:
	@Test
	public void placeCityWhiteA() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , true);
		assertMove("a8-a8",true, false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true,false,false);
	}	

	@Test
	public void placeCityWhiteJ() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , true);
		assertMove("j8-j8",true, false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true,false,false);
	}	
	@Test
	public void placeCityWhiteJWithoutW() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , true);
		assertMove("a8-j8",true, false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true,false,false);
	}	

	@Test
	public void placeCityWhiteBF() {
		startGame("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/" , true);
		assertMove("b9-f9",true, false);
		assertGameState("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/",true,false,false);
	}	
	
	@Test
	public void cannonDiagonalLeftShootDiagonalRight() {
		startGame("3W6/1w3w1w1w/2ww1w1w1w/1w1w1w1w1w//1b3b4/b5b1bb/b1b1b1b1b1/w1b1b1b1b1/2B7",true);
		assertMove("b8-b4",true,false);
		assertGameState("3W6/1w3w1w1w/2ww1w1w1w/1w1w1w1w1w//1b3b4/b5b1bb/b1b1b1b1b1/w1b1b1b1b1/2B7",true,false,false);
	}
	
	@Test
	public void cannonDiagonalRightShootDiagonalLeft() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1w1w1w///b1b1b1b1b1/b2bb1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("c1-c6",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/2ww1w1w1w///b1b1b1b1b1/b2bb1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	
	@Test
	public void cannonDiagonalRightShootDiagonalRight5() {
		startGame("5W4/3w1w3w/1www1w1www/1w1w1w1w1w///b1b1b1b1b1/b1bbb1b1b1/b1b3b1b1/4B5",false);
		assertMove("c1-h6",false,true);
		assertGameState("5W4/3w1w3w/1www1w1www/1w1w1w3w///b1b1b1b1b1/b1bbb1b1b1/b1b3b1b1/4B5",true,false,false);
	}
	
	@Test
	public void cannonDiagonalRightShootDiagonalRightMinus5() {
		startGame("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//2b7/b3b1b1b1/b1bbb1b1b1/b1b3b1b1/4B5",true);
		assertMove("j8-e3",true,true);
		assertGameState("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//2b7/b5b1b1/b1bbb1b1b1/b1b3b1b1/4B5",false,false,false);
	}
	@Test
	public void cannonDiagonalRightShootDiagonalRight45() {
		startGame("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//2b7/4bbb1b1/b1bbb1b1b1/b1b3b1b1/4B5",true);
		assertMove("j8-f3",true,false);
		assertGameState("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//2b7/4bbb1b1/b1bbb1b1b1/b1b3b1b1/4B5",true,false,false);
	}
	@Test
	public void cannonDiagonalRightShootDiagonalRight54() {
		startGame("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//4b5/4bbb1b1/b1bbb1b1b1/b1b3b1b1/4B5",true);
		assertMove("j8-e4",true,false);
		assertGameState("5W4/3w1w3w/1www1w2ww/1w1w1w1w1w//4b5/4bbb1b1/b1bbb1b1b1/b1b3b1b1/4B5",true,false,false);
	}
	
	@Test
	public void cannonDiagonalRightShootDiagonalRightMinus45() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w2ww1w///b1b1b1b1b1/b1bb2b1b1/b1b1b1b1b1/2B7",false);
		assertMove("c1-g6",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w2ww1w///b1b1b1b1b1/b1bb2b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	
	@Test
	public void cannonDiagonalRightShootDiagonalRightMinus54() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/7w2//b1b1b1b1b1/b1bb2b1b1/b1b1b1b1b1/2B7",false);
		assertMove("c1-h5",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w3w1w/7w2//b1b1b1b1b1/b1bb2b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	
	@Test
	public void moveBlackPlayerDiagonal() {
		startGame(initialBoardCityPlaced,false);
		assertMove("e2-g0",false,false);
		assertGameState(initialBoardCityPlaced,false,false,false);
	}
	
	@Test
	public void Suicide() {
		startGame(initialBoardCityPlaced,true);
		assertMove("b8-b8",true,false);
		assertGameState(initialBoardCityPlaced,true,false,false);
	}
	
	
	@Test
	public void AttackToRightW() {
		startGame("3W6/bw1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b3/2B7",true);
		assertMove("b8-a8",true,true);
		assertGameState("3W6/w2w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b3/2B7",false,false,false);
	}
	
	@Test
	public void whiteWonNoSoldiers() {
		startGame( "3W6////////bw8/2B7",true);
		assertMove("b1-a1",true,true);
		assertGameState("3W6////////w9/2B7",false,true,true);
	}
	@Test
	public void blackWonNoSoldiers() {
		startGame( "3W6////////bw8/2B7",false);
		assertMove("a1-b1",false,true);
		assertGameState("3W6////////1b8/2B7",true,true,false);
	}
	
	@Test
	public void cannonMoveCorner() {
		startGame("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2/3w6/b1b1b1b3/b1b3b1b1/bb2b1b1b1/b2B6",false);
		assertMove("a0-d3",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2/3w6/b1bbb1b3/b1b3b1b1/bb2b1b1b1/3B6",true,false,false);
	}
	@Test
	public void cannonShootCorner() {
		startGame("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2/4w5/b1b1b1b3/b1b3b1b1/bb2b1b1b1/b2B6",false);
		assertMove("a0-e4",false,true);
		assertGameState("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2//b1b1b1b3/b1b3b1b1/bb2b1b1b1/b2B6",true,false,false);
	}
	@Test
	public void cannonShootCornerLessSoldiers() {
		startGame("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2/4w5/b1b1b1b3/b5b1b1/bb2b1b1b1/b2B6",false);
		assertMove("a0-e4",false,false);
		assertGameState("5W4/1w1w1w1w1w/1w3w1w1w/5w2bw/1w5w2/4w5/b1b1b1b3/b5b1b1/bb2b1b1b1/b2B6",false,false,false);
	}
	@Test
	public void horizontalCannonBlockedBlack() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1b///b1b1b1b1b1/bbbw2b1b1/b3b1b1b1/2B7",false);
		assertMove("a2-d2",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1b///b1b1b1b1b1/bbbw2b1b1/b3b1b1b1/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlocked() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//w9/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("a3-d0",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//w9/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlockedWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/bw1w1w1w2//w9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b6-e9",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/bw1w1w1w2//w9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlockedWhiteBackwards() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("j6-g9",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith2StepsBlockedWhiteBackwards() {
		startGame("3W6/1w1w1w3w/1w1w1w1www/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("j6-h8",true,false);
		assertGameState("3W6/1w1w1w3w/1w1w1w1www/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlockedShortWhiteBackwards() {
		startGame("3W6/1w1w1w3w/1w1w1w1www/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("j6-g9",true,false);
		assertGameState("3W6/1w1w1w3w/1w1w1w1www/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith1StepBlockedWhiteBackwards() {
		startGame("3W6/1w1w1w3w/1w1w1w1w1w/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("j6-i7",true,false);
		assertGameState("3W6/1w1w1w3w/1w1w1w1w1w/1w1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith1StepBlockedWhite() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/bw1w1w1w2//w9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b6-c7",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/bw1w1w1w2//w9/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsWhite() {
		startGame("3W6/1w3w1w1w/1w1w1w1w1w/bw1w1w1w2//ww8/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b6-e9",true,false);
		assertGameState("3W6/1w3w1w1w/1w1w1w1w1w/bw1w1w1w2//ww8/2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsWhiteBackwards() {
		startGame("3W6/1w1ww4w/1w1w1w1w1w/ww1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("j6-f8",true,false);
		assertGameState("3W6/1w1ww4w/1w1w1w1w1w/ww1w1w1wbw///2b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlackBackwards() {
		startGame("3W6/1w1ww4w/1w1w1w1w1w/1w1w1w1wbw///bw1b1b1b2/b1b1b1b1b1/b1b3b1b1/2B7",false);
		assertMove("a3-e1",false,false);
		assertGameState("3W6/1w1ww4w/1w1w1w1w1w/1w1w1w1wbw///bw1b1b1b2/b1b1b1b1b1/b1b3b1b1/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlackBackwards2() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b1b1b1/bbb3b1b1/2B7",false);
		assertMove("i3-e1",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b1b1b1/bbb3b1b1/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlackBlockedBackwards() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b2bb1/bbb5bb/2B7",false);
		assertMove("i3-g1",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b2bb1/bbb5bb/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlackBackwardsBlocked() {
		startGame("3W6/1w1ww4w/1w1w1w1w1w/1w1w1w1wbw///bw1b1b1b2/bb2b1b1b1/b1b3b1b1/2B7",false);
		assertMove("a3-e1",false,false);
		assertGameState("3W6/1w1ww4w/1w1w1w1w1w/1w1w1w1wbw///bw1b1b1b2/bb2b1b1b1/b1b3b1b1/2B7",false,false,false);
	}
	@Test
	public void invalidRetreatWith3StepsBlackBlockedBackwards2() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b2bb1/bbb5bb/2B7",false);
		assertMove("i3-g0",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1bw/b1b1b2bb1/bbb5bb/2B7",false,false,false);
	}
	@Test
	public void horizontalCannonWrongMoveRight() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///bbb1b1b3/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("a3-d4",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///bbb1b1b3/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void horizontalCannonWrongMoveLeft() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///2b1b1bbb1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i3-f4",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///2b1b1bbb1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void horizontalCannonWrongShotLeft() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//4w5/2b1b1bbb1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("i3-e4",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//4w5/2b1b1bbb1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void horizontalCannonWrongShotRight() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//4w5/bbb1b1bbb1/b1b1b1b1b1/b1b1b5/2B7",false);
		assertMove("a3-e4",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2//4w5/bbb1b1bbb1/b1b1b1b1b1/b1b1b5/2B7",false,false,false);
	}
	@Test
	public void diagonalCannonShotLeft5() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1bb2/b1b1b1b1b1/2B7",false);
		assertMove("i1-d6",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w///b1b1b1b1b1/b1b1b1bb2/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void diagonalCannonShotLeft4() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2/4w5//b1b1b1b1b1/b1b1b1bb2/b1b1b1b1b1/2B7",false);
		assertMove("i1-e5",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1b1/b1b1b1bb2/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void diagonalCannonShotLeftMinus5() {
		startGame("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("d8-i3",true,true);
		assertGameState("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w///b1b1b1b3/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void diagonalCannonShotLeftMinus4() {
		startGame("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w//7b2/b1b1b1b3/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("d8-h4",true,true);
		assertGameState("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w///b1b1b1b3/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void diagonalCannonShotLeftMinus6() {
		startGame("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w///b1b1b1b3/b1b1b1b1bb/b1b1b1b1b1/2B7",true);
		assertMove("d8-j2",true,false);
		assertGameState("3W6/1w1w3w1w/1w1www1w1w/1w1w1w1w1w///b1b1b1b3/b1b1b1b1bb/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void diagonalLeftCannonShotRight() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/2b1b1bbb1/b1b1b1b1b1/2B7",false);
		assertMove("i1-j6",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/2b1b1bbb1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void diagonalLeftCannonShotWithFirst() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/2b1b1bbb1/b1b1b1b1b1/2B7",false);
		assertMove("g3-d6",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/2b1b1bbb1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraight() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1ww1///b1b1b1bb2/2b1b1bbb1/b1b1b1b1b1/2B7",false);
		assertMove("i1-i6",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1ww1///b1b1b1bb2/2b1b1bbb1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraightW() {
		startGame("3W6/1w1w1w1w1w/2ww1w1w1w/1w1w1w1w1w///b1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-b3",true,false);
		assertGameState("3W6/1w1w1w1w1w/2ww1w1w1w/1w1w1w1w1w///b1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraightLeft() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1b1w1ww1//1b8/b1b1b1bb2/2b1b1bbb1/b1b1w3b1/2B7",false);
		assertMove("i1-e1",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1b1w1ww1//1b8/b1b1b1bb2/2b1b1bbb1/b1b1w3b1/2B7",false,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraightLeftW() {
		startGame("3W6/1w3b1w1w/2ww1w1w1w/1w1w1w1w1w///w1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-f8",true,false);
		assertGameState("3W6/1w3b1w1w/2ww1w1w1w/1w1w1w1w1w///w1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraightLeft5() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1b1w1ww1//1b8/b1b1b1bb2/2b1b1bbb1/b1bw4b1/2B7",false);
		assertMove("i1-d1",false,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1b1w1ww1//1b8/b1b1b1bb2/2b1b1bbb1/b1bw4b1/2B7",false,false,false);
	}
	@Test
	public void diagonalLeftCannonShotStraightLeft5W() {
		startGame("3W6/1w4bw1w/2ww1w1w1w/1w1w1w1w1w///w1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-g8",true,false);
		assertGameState("3W6/1w4bw1w/2ww1w1w1w/1w1w1w1w1w///w1b1b1b1b1/bb2b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void straightCannonShotLeft() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("b8-a3",true,false);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true,false,false);
	}
	@Test
	public void cannonRightMoveW() {
		startGame("3W6/3w1w1w1w/1w1w1www1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
		assertMove("h8-e5",true,true);
		assertGameState("3W6/3w1w3w/1w1w1www1w/1w1w1w1w1w/4w5//b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	}
	@Test
	public void cannonStraightMoveB() {
		startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false);
		assertMove("a1-a4",false,true);
		assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w//b9/b1b1b1b1b1/b1b1b1b1b1/2b1b1b1b1/2B7",true,false,false);
	}
	@Test
	  public void cannonHorizontalShot5() {
	    startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1b1/b1b1w1bbb1/b1b1b1b1b1/2B7",false);
	    assertMove("i2-e2",false,true);
	    assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w2///b1b1b1b1b1/b1b3bbb1/b1b1b1b1b1/2B7",true,false,false);
	  }
	  @Test
	  public void cannonStraightShot5() {
	    startGame("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///bb2b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",true);
	    assertMove("b8-b3",true,true);
	    assertGameState("3W6/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b3b1b1b1/b1b1b1b1b1/b1b1b1b1b1/2B7",false,false,false);
	  }	
	  @Test
	    public void noPossibleMoveW() {
	      startGame( "3W2b3////////9w/2B3w3",true);
	      assertMove("j1-j0",true,true);
	      assertGameState("3W2b3/////////2B3w2w",false,true,true);
	    }
	  @Test
	    public void noPossibleMoveB() {
	      startGame( "3W2b3/9b////////2B3w3",false);
	      assertMove("j8-j9",false,true);
	      assertGameState("3W2b2b/////////2B3w3",true,true,false);
	    }
	  @Test
	    public void nearlyNoPossibleMoveB() {
	      startGame( "3W2b1w1/9b///////9w/2B3w3",false);
	      assertMove("j8-j9",false,true);
	      assertGameState("3W2b1wb////////9w/2B3w3",true,false,false);
	    }
	  @Test
	    public void isBoardCleanNot() {
	      startGame( "3W6/9b/////9b//9w/2B7",false);
	      assertMove("j8-j9",false,true);
	      assertGameState("3W5b//////9b//9w/2B7",true,false,false);
	    }
	  @Test
	    public void isBoardCleanNotWhite() {
		  startGame( "3W2b3/b9////9w///9w/2B3w3",true);
	      assertMove("j1-j0",true,true);
	      assertGameState("3W2b3/b9////9w////2B3w2w",false,false,false);
	    }
	  @Test
	    public void isBoardCleanNotWhite2() {
		  startGame( "3W2b3/b9////9w//9w//2B3w3",true);
	      assertMove("j2-j1",true,true);
	      assertGameState("3W2b3/b9////9w///9w/2B3w3",false,false,false);
	    }
	  @Test
	    public void isBoardCleanNot2() {
	      startGame( "3W6//9b////9b//9w/2B7",false);
	      assertMove("j7-j8",false,true);
	      assertGameState("3W6/9b/////9b//9w/2B7",true,false,false);
	    }
	  
	  @Test
	    public void threatendDontEndGame() {
	      startGame( "b2W6/1w8//////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("b2W6/1w8///////w9/2B7",false,false,false);
	    }
	  
	  @Test
	    public void threatendDontEndGameVertical() {
	      startGame( "b2W6/w9//////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("b2W6/w9///////w9/2B7",false,false,false);
	    }
	  @Test
	    public void threatendEndGameVertical() {
	      startGame( "b2W6/ww8//////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("b2W6/ww8///////w9/2B7",false,true,true);
	    }
	  @Test
	    public void threatendEndGameRightDiagonal() {
	      startGame( "b2W6/ww8/2w7/////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("b2W6/ww8/2w7//////w9/2B7",true,true,true);
	    }
	  @Test
	    public void threatendEndGameLeftDiagonal() {
	      startGame( "3W5b/8ww/7w2/////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("3W5b/8ww/7w2//////w9/2B7",true,true,true);
	    }
	  @Test
	    public void threatendEndGameLeftDiagonal2() {
	      startGame( "3W5b/8w1/9w/////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("3W5b/8w1/9w//////w9/2B7",true,true,true);
	    }
	  @Test
	    public void threatendEndGameLeftDiagonal3() {
	      startGame( "3W5b/9w//////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("3W5b/9w///////w9/2B7",false,false,false);
	    }
	  @Test
	    public void threatendEndGameRightDiagonalW() {
	      startGame( "1W8//b9//////9b/3B5w",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("1W8/b9///////9b/3B5w",true,false,false);
	    }
	  @Test
	    public void threatendEndGameRightDiagonalW2() {
	      startGame( "1W8//b9/////7b2/9b/3B5w",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("1W8/b9//////7b2/9b/3B5w",true,true,false);
	    }
	  @Test
	    public void threatendEndGameLeftDiagonalW2() {
	      startGame( "1W8//b9/////2b7/b9/w5B3",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("1W8/b9//////2b7/b9/w5B3",false,true,false);
	    }
	  @Test
	    public void threatendEndGameLeftDiagonalW3() {
	      startGame( "1W8//b9//////b9/w5B3",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("1W8/b9///////b9/w5B3",true,false,false);
	    }
	  @Test
	    public void threatendDontEndGameW() {
	      startGame( "3W6//b9//////1b8/w2B6",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("3W6/b9///////1b8/w2B6",true,false,false);
	    }
	  @Test
	    public void threatendEndGameSurroundedW() {
	      startGame( "3W6//b9/////b9/1b8/w2B6",false);
	      assertMove("a7-a8",false,true);
	      assertGameState("3W6/b9//////b9/1b8/w2B6",true,true,false);
	    }
	  @Test
	    public void threatendEndGameSurrounded() {
	      startGame( "b2W6/1w8/w9/////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("b2W6/1w8/w9//////w9/2B7",true,true,true);
	    }

	  @Test
	    public void nearCity() {
	      startGame( "2bW6///////w9//2B7",true);
	      assertMove("a2-a1",true,true);
	      assertGameState("2bW6////////w9/2B7",false,false,false);
	    }

	  @Test
	    public void retreatOneStep() {
	      startGame( "3W6//w9/b9/////9w/2B7",true);
	      assertMove("a7-a8",true,false);
	      assertGameState("3W6//w9/b9/////9w/2B7",true,false,false);
	    }
	  
		@Test
		public void boardPartlyClean() {
			startGame("W7w1////////9w/b7B1",true);
			assertMove("j1-j0",true,true);
			assertGameState("W7w1/////////b7Bw", false, false, true);
		}
		
		@Test
		public void blockButNotClean() {
			startGame("bb1W6/b9//////w9//8B1",true);
			assertMove("a2-a1",true,true);
			assertGameState("bb1W6/b9///////w9/8B1", false, true, true);
		}
		
		@Test
		public void notBlockButNotClean() {
			startGame("1bb1W5/1b8//////w9//8B1",true);
			assertMove("a2-a1",true,true);
			assertGameState("1bb1W5/1b8///////w9/8B1", false, false, false);
		}
		
		@Test
		public void blockButNotCleanJ() {
			startGame("6W1bb/9b//////w9//8B1",true);
			assertMove("a2-a1",true,true);
			assertGameState("6W1bb/9b///////w9/8B1", false, true, true);
		}
		
		@Test
		public void notBlockButNotCleanJ() {
			startGame("6W1b1/8b1//////w9//8B1",true);
			assertMove("a2-a1",true,true);
			assertGameState("6W1b1/8b1///////w9/8B1", false, false, false);
		}
		
		@Test
		public void blockButNotCleanW() {
			startGame("bb1W6//b9//////w9/ww1B6",false);
			assertMove("a7-a8",false,true);
			assertGameState("bb1W6/b9///////w9/ww1B6", false, true, false);
		}
		@Test
		public void blockButNotCleanWJ() {
			startGame("bb1W6//b9//////9w/6B1ww",false);
			assertMove("a7-a8",false,true);
			assertGameState("bb1W6/b9///////9w/6B1ww", false, true, false);
		}	
		

}
