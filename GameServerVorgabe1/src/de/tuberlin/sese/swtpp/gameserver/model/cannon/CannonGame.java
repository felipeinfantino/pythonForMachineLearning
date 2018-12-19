package de.tuberlin.sese.swtpp.gameserver.model.cannon;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

import javax.management.RuntimeErrorException;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5424778147226994452L;


	/************************
	 * member
	 ***********************/

	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	private String board;

	/************************
	 * constructors
	 ***********************/

	public CannonGame() {
		super();
		// TODO: add further initializations if necessary
		this.board = "1111111111/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w/1111111111/1111111111/b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/1111111111";
	}

	/*******************************************
	 * Game class functions already implemented
	 ******************************************/

	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);

			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}

		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";

		return "Finished";
	}

	@Override
	public String gameInfo() {
		String gameInfo = "";

		if(started) {
			if(blackGaveUp()) gameInfo = "black gave up";
			else if(whiteGaveUp()) gameInfo = "white gave up";
			else if(didWhiteDraw() && !didBlackDraw()) gameInfo = "white called draw";
			else if(!didWhiteDraw() && didBlackDraw()) gameInfo = "black called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = blackPlayer.isWinner()? "black won" : "white won";
		}

		return gameInfo;
	}	

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}

	@Override
	public boolean callDraw(Player player) {

		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}

		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}	
		return true;
	}

	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) { 
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();

			return true;
		}

		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/

	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}

	/**
	 * Switch next player
	 */
	private void updateNext() {
		if (nextPlayer == whitePlayer) nextPlayer = blackPlayer;
		else nextPlayer = whitePlayer;
		checkIfEndGameNoMoves(nextPlayer);
	}

	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();

			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/

	@Override
	public void setBoard(String state) {
		//hier fehlt eine Funktion isValidString die �berpr�ft ob die Eingabe besteht aus folgende chars (zahlen von 0-9) / w W b B, wenn nicht sollte die eine RuntimeException werfen
		String convertedState = convertString(state);
		this.board = convertedState;

	}

	@Override
	public String getBoard() {
		//return this.board;
		//System.out.println(convertStringZurueck(this.board));
		return convertStringZurueck(this.board);
	}

	public String getBoardIntern(){
		return this.board;
	}

	@Override
	public boolean tryMove(String moveString, Player player) {
		if(getCharacterInBoardFromUrsprung(moveString) == 'W' || getCharacterInBoardFromUrsprung(moveString) == 'B' ){
			return false;
		}
		if(isFirstRound(player)){
			return placeCity(player, moveString);
		}
		if(istZielFrei(moveString)){
			return checkIfMove(moveString, player);
		}else{
			return checkIfAttackOrShot(moveString, player);
		}
	}

	private boolean checkIfAttackOrShot(String moveString, Player player) {
		if(isValidAttack(moveString, player) && istZielGegner(moveString, player)){
			attack(moveString, player);
			//System.out.println("Attack performed");
			updateNext();
			//wird ja nur aufgerufen wenn es nicht die erste Runde ist, dh St�dte m�ssen in Board sein und das Spiel muss gestarted sein
			checkIfEndOfGame(moveString, player);
			return true;
		}
		if(isCannon(moveString, player)){
			if(isValidCannonShot(moveString,player) && isKopfFrei(moveString,player)){
				cannonShot(moveString, player);
				updateNext();
				checkIfEndOfGame(moveString, player);
				return true;
			}
		}
		return false;
	}

	private boolean checkIfMove(String moveString, Player player) {
		if(isValidMove(moveString, player)){
			moveSoldat(moveString, player);
			//System.out.println("Soldat von " + getErsteTeilVonMove(moveString) + " zu " + getLetzteTeilVonMove(moveString) +" bewegt");
			updateNext();
			return true;
		}
		if(isBedroht(moveString, player) && isValidRueckzugMove(moveString,player)){
			moveSoldat(moveString, player);
			updateNext();
			return true;
		}
		if(isValidCannonMove(moveString, player) && isCannon(moveString, player) ){
				moveSoldat(moveString, player);
				updateNext();
				return true;
		}
		
		
		
		return false;
	}

	private boolean placeCity(Player player, String moveString) {
		if(isWhiteNext()){
			return checkIfWhiteCityIsValid(player, moveString); 
		}else{
			return checkIfBlackCityIsValid(player, moveString);
		}
	}
	
	private boolean checkIfBlackCityIsValid(Player player, String moveString) {
		if(!moveString.substring(4).equals("0") || moveString.substring(3, 4).equals("a") || moveString.substring(3, 4).equals("j") || !moveString.substring(0, 2).equals(moveString.substring(3, 5)) || !getBoardIntern().contains("W") ){
			//System.out.println("Invalid place for the city");
			return false;
		}
		setBoard(getBoardIntern().substring(0, (getBuchstabeVonLetzteTeilVonMove(moveString)-'a')+99) + "B"+ getBoardIntern().substring((getBuchstabeVonLetzteTeilVonMove(moveString)-'a')+100));
		//System.out.println("Black City placed ");
		updateNext();
		return true;
	}

	private boolean checkIfWhiteCityIsValid(Player player, String moveString) {
		if(!moveString.substring(4).equals("9") || moveString.substring(3, 4).equals("a") || moveString.substring(3, 4).equals("j") || !moveString.substring(0, 2).equals(moveString.substring(3, 5)) ){
			//System.out.println("Invalid place for the city");
			return false;
		}
		setBoard(getBoardIntern().substring(0, (getBuchstabeVonLetzteTeilVonMove(moveString)-'a')) + "W"+ getBoardIntern().substring((getBuchstabeVonLetzteTeilVonMove(moveString)-'a')+1));
		updateNext();
		return true;
	}
	

	private boolean isKopfFrei(String moveString,Player player) {
		int diffZahlen = getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		int diffChars = (getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString));
		if(checkGeradeIfCannon(moveString, player)){
			return isKopfVonGeradeCannonFrei(moveString,player, diffZahlen);
		}
		if(checkDiagonalLinksIfCannon(moveString, player)){
			return isKopfVonDiagonalLinksCannonFrei(moveString, player , diffZahlen, diffChars);
		}
		if(checkDiagonalRechtsIfCannon(moveString, player)){
			return isKopfVonDiagonalRechtsCannonFrei(moveString, player, diffZahlen ,diffChars);
		}else{
			return isKopfVonWaagerechteCannonFrei(moveString,player, diffZahlen, diffChars);
		}
		
	}

	private boolean isKopfVonWaagerechteCannonFrei(String moveString, Player player, int diffZahlen, int diffChars) {
		
		if(diffChars < 0){
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)+3) + (getZahlVonErstenTeilVonMove(moveString));
			return istZielFrei(kopfKannon);
		}
		else {
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)-3) + (getZahlVonErstenTeilVonMove(moveString));
			return istZielFrei(kopfKannon);
		}
	}

	private boolean isKopfVonDiagonalRechtsCannonFrei(String moveString, Player player, int diffZahlen, int diffChars) {
		
		if(diffZahlen< 0){
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)+3) + (getZahlVonErstenTeilVonMove(moveString)+3);
			return istZielFrei(kopfKannon);
		}
		else {
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)-3)+ (getZahlVonErstenTeilVonMove(moveString)-3);
			return istZielFrei(kopfKannon);	
		}
	}

	private boolean isKopfVonDiagonalLinksCannonFrei(String moveString, Player player, int diffZahlen, int diffChars) {
		if(diffZahlen< 0){
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)-3) + (getZahlVonErstenTeilVonMove(moveString)+3);
			return istZielFrei(kopfKannon);
		}else{
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)(getBuchstabeVonErstenTeilVonMove(moveString)+3)+ (getZahlVonErstenTeilVonMove(moveString)-3);
			return istZielFrei(kopfKannon);	
		}
		
	}

	private boolean isKopfVonGeradeCannonFrei(String moveString, Player player, int diffZahlen) {
		if(diffZahlen< 0 ){ //diffChars == 0 sollte selbsverst�ndlich aus checkgeradeIf cannon
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)getBuchstabeVonErstenTeilVonMove(moveString)+ (getZahlVonErstenTeilVonMove(moveString)+3);
			return istZielFrei(kopfKannon);
		}else{
			String kopfKannon = getErsteTeilVonMove(moveString) + "-" + (char)getBuchstabeVonErstenTeilVonMove(moveString)+ (getZahlVonErstenTeilVonMove(moveString)-3);
			return istZielFrei(kopfKannon);
		}
	}

	private boolean istZielGegner(String moveString, Player player){
		char isGegner = (player.equals(whitePlayer)) ? 'b' : 'w';
		char isStadt = (player.equals(whitePlayer)) ? 'B' : 'W';
		if(getCharacterInBoardFromZiel(moveString) == isGegner || getCharacterInBoardFromZiel(moveString)== isStadt) return true;
		return false;

	}


	private boolean isValidCannonShot(String moveString, Player player) {
		if(istGeradeCannonShot(moveString,player) && checkGeradeIfCannon(moveString, player))return true;
		if(checkDiagonalLinksIfCannon(moveString, player) && istDiagonalLinksShot(moveString,player))return true;
		if(checkDiagonalRechtsIfCannon(moveString, player)&& istDiagonalRechtsShot(moveString,player))return true;
		if(istWaargerechtCannonShot(moveString, player) && checkWaagerechtIfCannon(moveString, player))return true;
		return false;


	}

	private boolean istDiagonalRechtsShot(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		if(diffChars == -4 && diffZahl == -4) return true;
		else if(diffChars == -5 && diffZahl == -5) return true;
		else if(diffChars == 4 && diffZahl == 4) return true;
		else if (diffChars == 5 && diffZahl == 5) return true;
		else return false;
	}

	private boolean istDiagonalLinksShot(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		if(diffChars == 4 && diffZahl == -4) return true;
		else if(diffChars == 5 && diffZahl == -5) return true;
		else if(diffChars == -4 && diffZahl == 4) return true;
		else if (diffChars == -5 && diffZahl == 5) return true;
		else return false;
	}

	private boolean istGeradeCannonShot(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		if(diffChars == 0 && diffZahl == -4) return true;
		else if(diffChars == 0 && diffZahl == 4) return true;
		else if(diffChars == 0 && diffZahl == -5) return true;
		else if (diffChars == 0 && diffZahl == 5) return true;
		else return false;
		}

	private boolean isValidCannonMove(String moveString, Player player) {
		if(istGeradeCannonMove(moveString,player))return true;
		if(istDiagonalLinksMove(moveString,player))return true;
		if( istDiagonalRechtsMove(moveString,player))return true;
		return istWaagerechtCannonMove(moveString, player);
	}

	private boolean istDiagonalRechtsMove(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		return ((diffChars == -3 && diffZahl == -3) || (diffChars == 3 && diffZahl == 3));
	}

	private boolean istDiagonalLinksMove(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		return ((diffChars == 3 && diffZahl == -3) || (diffChars == -3 && diffZahl == 3));
	}

	private boolean istGeradeCannonMove(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		return ((diffChars == 0 && diffZahl == -3) || (diffChars == 0 && diffZahl == 3));
	}

	private boolean isCannon(String moveString, Player player) {
		return (checkGeradeIfCannon(moveString, player)|| checkDiagonalLinksIfCannon(moveString, player) || checkDiagonalRechtsIfCannon(moveString, player) || checkWaagerechtIfCannon(moveString, player));
	}

	private boolean checkDiagonalLinksIfCannon(String moveString, Player player) {
		int anfangZahl = getZahlVonErstenTeilVonMove(moveString);
		char geradeChar = getBuchstabeVonErstenTeilVonMove(moveString);
		char isBlackOrWhite = (player.equals(whitePlayer)) ? 'w' : 'b';
		//check oben
		if(countDLCOben(anfangZahl,geradeChar, isBlackOrWhite) == 3) return true;
		int count = 0;
		for(int i = anfangZahl ; i >= anfangZahl-2; i--){
			if(i < 0 || (geradeChar+(anfangZahl-i)) > 106 )break;
			String toProve = ""+(char)(geradeChar+(anfangZahl-i)) + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
		}
		if(count == 3) return true;
		return false;
	}

	private int countDLCOben(int anfangZahl, char geradeChar, char isBlackOrWhite) {
		int count = 0;
		for(int i = anfangZahl ; i <= anfangZahl+2; i++){
			if(i > 9 || (geradeChar-(i-anfangZahl)) < 97 )break;
			String toProve = ""+(char)(geradeChar-(i-anfangZahl)) + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
		}
		return count;
	}

	private boolean checkDiagonalRechtsIfCannon(String moveString, Player player) {
		int anfangZahl = getZahlVonErstenTeilVonMove(moveString);
		char geradeChar = getBuchstabeVonErstenTeilVonMove(moveString);
		char isBlackOrWhite = (player.equals(whitePlayer)) ? 'w' : 'b';
		int count = countDRCOben(anfangZahl,geradeChar, isBlackOrWhite);
		if(count == 3) return true;
		count = 0;
		for(int i = anfangZahl ; i >= anfangZahl-2; i--){
			if(i < 0)break;
			if( (geradeChar-(anfangZahl-i)) < 97) break;
			String toProve = ""+(char)(geradeChar-(anfangZahl-i)) + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
		}
		if(count == 3) return true;
		return false;

	}

	private int countDRCOben(int anfangZahl, char geradeChar, char isBlackOrWhite) {
		int count = 0;
		for(int i = anfangZahl ; i <= anfangZahl+2; i++){
			if(i > 9 || (geradeChar+(i-anfangZahl)) > 106)break;
			String toProve = ""+(char)(geradeChar+(i-anfangZahl)) + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
		}
		return count;
	}

	private boolean checkGeradeIfCannon(String moveString, Player player) {

		if((getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString)) != 0) return false;
		int anfangZahl = getZahlVonErstenTeilVonMove(moveString);
		char geradeChar = getBuchstabeVonErstenTeilVonMove(moveString);
		char isBlackOrWhite = (player.equals(whitePlayer)) ? 'w' : 'b';
		int count = countGCOben(anfangZahl,geradeChar, isBlackOrWhite);;
		//check oben
		
		if(count == 3) return true;
		count = 0;
		for(int i = anfangZahl ; i >= anfangZahl-2; i--){
			if(i < 0)break;
			String toProve = ""+(char)geradeChar + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
			//System.out.println("Proved : " + toProve + " actual count : " + count + " and result Character in board " + getCharacterInBoardFromUrsprung(toProve));
		}
		if(count == 3) return true;
		return false;
	}
	private int countGCOben(int anfangZahl, char geradeChar, char isBlackOrWhite) {
		int count = 0;
		for(int i = anfangZahl ; i <= anfangZahl+2; i++){
			if(i > 9)break;
			String toProve = ""+(char)geradeChar + i;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
			//System.out.println("Proved : " + toProve + " actual count : " + count + " and result Character in board " + getCharacterInBoardFromUrsprung(toProve));
		}
		return count;
	}

	private boolean checkWaagerechtIfCannon(String moveString, Player player) {
		if(getZahlVonErstenTeilVonMove(moveString) != getZahlVonLetzteTeilVonMove(moveString)) return false;
		char isBlackOrWhite = (player.equals(whitePlayer)) ? 'w' : 'b';
		int anfangZahl = getZahlVonErstenTeilVonMove(moveString);
		char geradeChar = getBuchstabeVonErstenTeilVonMove(moveString);
		int count = countWCOben(anfangZahl, geradeChar, isBlackOrWhite);
		//check rechts
		
		if(count == 3) return true;
		count = 0;
		for(int i = geradeChar ; i >= geradeChar-2; i--){
			if(i < 97)break;
			String toProve = ""+(char)i + anfangZahl;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
			//System.out.println("Proved : " + toProve + " actual count : " + count + " and result Character in board " + getCharacterInBoardFromUrsprung(toProve));
		}
		if(count == 3) return true;
		return false;
	}

	private int countWCOben(int anfangZahl, char geradeChar, char isBlackOrWhite) {
		int count = 0;
		for(int i = geradeChar ; i <= geradeChar+2; i++){
			if(i > 106)break;
			String toProve = ""+(char)i + anfangZahl;
			if(getCharacterInBoardFromUrsprung(toProve) == isBlackOrWhite) count++;
			//System.out.println("Proved : " + toProve + " actual count : " + count + " and result Character in board " + getCharacterInBoardFromUrsprung(toProve));
		}
		return count;
	}

	private boolean istWaargerechtCannonShot(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		return (((diffChars == -4 && diffZahl == 0) || (diffChars == 4 && diffZahl == 0)) || ((diffChars == -5 && diffZahl == 0) || (diffChars == 5 && diffZahl == 0)) );
	}

	private boolean istWaagerechtCannonMove(String moveString, Player player) {
		int diffChars = getBuchstabeVonErstenTeilVonMove(moveString) - getBuchstabeVonLetzteTeilVonMove(moveString);
		int diffZahl =  getZahlVonErstenTeilVonMove(moveString) - getZahlVonLetzteTeilVonMove(moveString);
		return ((diffChars == -3 && diffZahl == 0) || (diffChars == 3 && diffZahl == 0));
	}





	private boolean isValidRueckzugMove(String moveString, Player player) {
		int differenceBetweenLetters = (moveString.charAt(0)-moveString.charAt(3));
		int differenceBetweenNumbers = (Integer.parseInt(moveString.substring(1, 2)) - Integer.parseInt(moveString.substring(4, 5)));
		//Benutzte das erste Teil des Moves damit ich istZielFrei(zwischenPlatz) aufrufen kann
		char charVonZwischenPlatz  = (getErsteTeilVonMove(moveString).charAt(0) == (getLetzteTeilVonMove(moveString).charAt(0))) ? getLetzteTeilVonMove(moveString).charAt(0) : 'z';
		if(charVonZwischenPlatz == 'z'){
			charVonZwischenPlatz = (getErsteTeilVonMove(moveString).charAt(0) > getLetzteTeilVonMove(moveString).charAt(0)) ? (char)(getErsteTeilVonMove(moveString).charAt(0) -1 ) : (char)(getErsteTeilVonMove(moveString).charAt(0) +1);
		}
		String zwischenPlatz = "";
		if(player.equals(whitePlayer)){
			zwischenPlatz = moveString.substring(0, 3) + charVonZwischenPlatz + (getZahlVonErstenTeilVonMove(moveString)+1) ;
			return ((differenceBetweenLetters == 2 || differenceBetweenLetters == -2) || differenceBetweenLetters == 0)  && differenceBetweenNumbers == -2 && istZielFrei(zwischenPlatz);
		}else{
			zwischenPlatz = moveString.substring(0, 3) + charVonZwischenPlatz + (getZahlVonErstenTeilVonMove(moveString)-1) ;
			return ((differenceBetweenLetters == 2 || differenceBetweenLetters == -2) || differenceBetweenLetters == 0)  && differenceBetweenNumbers == 2 && istZielFrei(zwischenPlatz);
			}
	}


	private boolean isBedroht(String moveString, Player player) {
		
		ArrayList<String> nachbarnVonUrsrpungMove = getAdjacentPlaetzeImBezugUrprung(moveString);
		char gegner = (player.equals(whitePlayer)) ? 'b' : 'w'; 
		for(String a : nachbarnVonUrsrpungMove){
			if(getCharacterInBoardFromUrsprung(a) == gegner) {
				return true;
			}
		}
		return false;
	}
	private boolean isBedrohtCity(String moveString, Player player) {
		
		ArrayList<String> nachbarnVonUrsrpungMove = getAdjacentPlaetzeImBezugUrprung(moveString);
		char gegner = (player.equals(whitePlayer)) ? 'B' : 'W'; 
		for(String a : nachbarnVonUrsrpungMove){
			if(getCharacterInBoardFromUrsprung(a) == gegner)return true;
		}
		return false;
	}
	/**
	 * Es werden die Nachbarn von dem Ersten Teil des Moves in einem ArrayList zur�ckgegeben
	 * 
	 * @param moveString
	 * @return ArrayList mit Adjacente Plaetze
	 */
	private ArrayList<String> getAdjacentPlaetzeImBezugUrprung(String moveString) {
		ArrayList<String> adjacentPlaetze = new ArrayList<>();
		int anfangZahl = getZahlVonErstenTeilVonMove(moveString);
		char anfangChar = getBuchstabeVonErstenTeilVonMove(moveString);
		for(int i = anfangZahl-1; i<= anfangZahl+1; i++ ){
			for(int k = anfangChar-1 ; k<= anfangChar+1 ; k++){
				String toAdd =  (""+ (char)k)+ Integer.toString(i);
				adjacentPlaetze.add(toAdd);
			}
		}
		ArrayList<String> adjacentPlaetzeClean = new ArrayList<>();
		for(int i=0; i<adjacentPlaetze.size(); i++) {
			if(adjacentPlaetze.get(i).matches("[a-j][0-9]")) {
				adjacentPlaetzeClean.add(adjacentPlaetze.get(i));
			}
		}
		return adjacentPlaetzeClean;
	}

	private void checkIfEndOfGame(String moveString, Player player) {
		
		if( !getBoardIntern().contains("W")) finish(player);	
		else if(!getBoardIntern().contains("B")) finish(player);	
		else if(!getBoardIntern().contains("b")) finish(player);
		else if(!getBoardIntern().contains("w")) finish(player);	
		
	}
	
	void checkIfEndGameNoMoves(Player player) {
		char playerColor = (player.equals(whitePlayer)) ? 'w' : 'b';
		String[] board = getBoardIntern().split("/");
		//On the last rows, last move of the game, check for each player on this row if he can retreat or attack
		int i = 9;
		//Check if there is no valid moves for the enemy player
		for(String row : board) {
			for(int j = 0; j < 10; j++) {
				char soldier = row.charAt(j);
				String position = ""+ getCharForNumber(j) + i;
				position = position.toLowerCase();
				//Now check all soldiers on this row
				if  (playerColor == soldier && canSoldierDoSomething(position, player)) return;
			}
			i--;
		}
		endTheGame(player);
	}
	
	boolean canSoldierDoSomething(String position, Player player) {
		//Check if he has enemy neighbors, if he can retreat or attack one of them
		if(isBedroht(position+"-"+position, player) || isBedrohtCity(position+"-"+position, player)) {
			//Ok it has enemy neighbors, do I have room to escape or to attack back?
			if(checkIfEnemyIsThere(position, player)) {
				//Can attack back! give the player a chance
				return true;
			} else if(checkIfThereIsRoomToEscape(position, player)) {
				return true;
			}
			
		} else if(checkIfThereIsRoomToMove(position, player)) {
			return true;
		}
		return false;
	}
	
	boolean checkIfThereIsRoomToMove(String position, Player player) {
		char row = position.charAt(1);
		char column = position.charAt(0);
		if(player.equals(whitePlayer)) {
			if(position.charAt(1) != '0') {
				//On the field, check movment
				return checkWhiteMovment(row, column);
			}
		} else {
			if(position.charAt(1) != '9') {
				//On the field, check movment
				return checkBlackMovment(row, column);
			}
		}
		return false;
	}
	
	boolean checkBlackMovment(char row, char column) {
		String vertical = ""+column+""+((char)(row+1));
		String diagonalRight = ""+((char)(column+1))+""+((char)(row+1));
		String diagonalLeft = ""+((char)(column-1))+""+((char)(row+1));
		if(istZielFrei(vertical+"-"+vertical)) return true;
		if(column != 'j' && istZielFrei(diagonalRight+"-"+diagonalRight)) return true;
		//System.out.println((column != 'a') +":"+diagonalLeft+":"+ istZielFrei(diagonalLeft+"-"+diagonalLeft));
		if(column != 'a' && istZielFrei(diagonalLeft+"-"+diagonalLeft)) return true;
		return false;
	}
	
	boolean checkWhiteMovment(char row, char column) {
		String vertical = ""+column+""+((char)(row-1));
		String diagonalRight = ""+((char)(column-1))+""+((char)(row-1));
		String diagonalLeft = ""+((char)(column+1))+""+((char)(row-1));
		if(istZielFrei(vertical+"-"+vertical)) return true;
		if(column != 'a' && istZielFrei(diagonalRight+"-"+diagonalRight)) return true;
		if(column != 'j' && istZielFrei(diagonalLeft+"-"+diagonalLeft)) return true;
		return false;
	}
	
	boolean checkIfEnemyIsThere(String position, Player player) {
		//Check right and left, if you can attack an enemy
		ArrayList<String> adjacentPlaetze = getAdjacentPlaetzeImBezugUrprung(position+"-"+position);
		ArrayList<String> adjacentPlaetzeClean = new ArrayList<>();
		for(int i=0; i<adjacentPlaetze.size(); i++) {
			//Getting only the right and left fields on the soldier
			if(adjacentPlaetze.get(i).matches("[a-j]"+position.charAt(1))) {
				adjacentPlaetzeClean.add(adjacentPlaetze.get(i));
			}
		}
		for(String target : adjacentPlaetzeClean) {
			if(istZielGegner((position+"-"+target), player)) return true;
		}
		return false;
	}
	
	boolean checkIfThereIsRoomToEscape(String position, Player player) {
		//Check diagonal 2 backwards or vertical 2 backwards if they are free to move
		if(player.equals(whitePlayer)) {
			if(checkEscapeWhite(position, player)) return true;
		} else {
			if(checkEscapeBlack(position, player)) return true;
		}
		return false;
	}
	
	boolean checkEscapeWhite(String position, Player player) {
		String verticalRetreat = ""+position.charAt(0) + "2";
		//mirrored to the black diagonal
		String leftDiagonalRetreat = ""+diagonalRightBlack(position, 2)+"2";
		String rightDiagonalRetreat = ""+diagonalLeftBlack(position, 2)+"2";
		if(istZielFrei(position+"-"+verticalRetreat) && isValidRueckzugMove(position+"-"+verticalRetreat, player)) return true;
		if(istZielFrei(position+"-"+leftDiagonalRetreat) && isValidRueckzugMove(position+"-"+leftDiagonalRetreat, player)) return true;
		if(istZielFrei(position+"-"+rightDiagonalRetreat) && isValidRueckzugMove(position+"-"+rightDiagonalRetreat, player)) return true;
		return false;
	}
	
	boolean checkEscapeBlack(String position, Player player) {
		String verticalRetreat = ""+position.charAt(0) + "7";
		String leftDiagonalRetreat = ""+diagonalLeftBlack(position, 2)+"7";
		String rightDiagonalRetreat = ""+diagonalRightBlack(position, 2)+"7";	
		if(istZielFrei(position+"-"+verticalRetreat) && isValidRueckzugMove(position+"-"+verticalRetreat, player)) return true;
		if(istZielFrei(position+"-"+leftDiagonalRetreat) && isValidRueckzugMove(position+"-"+leftDiagonalRetreat, player)) return true;
		if(istZielFrei(position+"-"+rightDiagonalRetreat) && isValidRueckzugMove(position+"-"+rightDiagonalRetreat, player)) return true;
		return false;
	}
	
	void endTheGame(Player player) {
		if(player.equals(whitePlayer)) {
			 finish(blackPlayer);
		} else {
			finish(whitePlayer);
		}
	}
	
	private String getCharForNumber(int i) {
	    return String.valueOf((char)(i + 'A'));
	}
	

	
	char diagonalLeftBlack(String position, int dist) {
		char column = position.charAt(0);
		char dC = (char) (column - dist);
		return dC;
	}
	
	char diagonalRightBlack(String position, int dist) {
		char column = position.charAt(0);
		char dC = (char) (column + dist);
		return dC;
	}
	
	
	

	private void attack(String moveString, Player player) {

		int indexInBoardFromLetzenTeil = getIndexInBoardFromLetzeTeil(moveString);
		setBoard(getBoardIntern().substring(0, indexInBoardFromLetzenTeil)+ "1" + getBoardIntern().substring(indexInBoardFromLetzenTeil+1));
		moveSoldat(moveString, player);//
	}
	private void cannonShot(String moveString, Player player) {
		int indexInBoardFromLetzenTeil = getIndexInBoardFromLetzeTeil(moveString);
		setBoard(getBoardIntern().substring(0, indexInBoardFromLetzenTeil)+ "1" + getBoardIntern().substring(indexInBoardFromLetzenTeil+1));

	}

	private boolean isValidAttack(String moveString, Player player) {
		if(getErsteTeilVonMove(moveString).equals(getLetzteTeilVonMove(moveString))) return false;
		int differenceBetweenLetters = (moveString.charAt(0)-moveString.charAt(3));
		int differenceBetweenNumbers = (Integer.parseInt(moveString.substring(1, 2)) - Integer.parseInt(moveString.substring(4, 5)));
		if(player.equals(whitePlayer)){
			return(differenceBetweenLetters <= 1 && differenceBetweenLetters >=-1 && (differenceBetweenNumbers == 1 || differenceBetweenNumbers == 0) );
		}else{
			return (differenceBetweenLetters <= 1 && differenceBetweenLetters >=-1 && (differenceBetweenNumbers == -1 || differenceBetweenNumbers == 0)  );
		}
	}

	/**
	 * Get the first part of move , zB move c9-b4 it returns "c9"
	 * 
	 * @param moveString
	 * @return
	 */
	private String getErsteTeilVonMove(String moveString){
		return moveString.substring(0, 2);
	}

	/**
	 * Get the last part of move , zB move c9-b4 it returns "b4"
	 * 
	 * @param moveString
	 * @return 
	 */
	private String getLetzteTeilVonMove(String moveString){
		return moveString.substring(3, 5);
	}

	/**
	 * Get the character of the last part of move , zB move c9-b4 it returns "b"
	 * 
	 * @param moveString
	 * @return char
	 */
	private char getBuchstabeVonLetzteTeilVonMove(String moveString){
		return getLetzteTeilVonMove(moveString).substring(0, 1).charAt(0);
	}

	/**
	 * Get the number of the last part of move , zB move c9-b4 it returns "4"
	 * 
	 * @param moveString
	 * @return
	 */
	private int getZahlVonLetzteTeilVonMove(String moveString){
		return Integer.parseInt(getLetzteTeilVonMove(moveString).substring(1, 2));
	}

	/**
	 * Get the number of the first part of move , zB move c9-b4 it returns "9"
	 * 
	 * @param moveString
	 * @return
	 */
	private int getZahlVonErstenTeilVonMove(String moveString){
		return Integer.parseInt(getErsteTeilVonMove(moveString).substring(1, 2));
	}

	/**
	 * Get the character of the first part of move , zB move c9-b4 it returns "c"
	 * 
	 * @param moveString
	 * @return
	 */
	private char getBuchstabeVonErstenTeilVonMove(String moveString){
		return getErsteTeilVonMove(moveString).substring(0, 1).charAt(0);
	}

	private void moveSoldat(String moveString, Player player) {

		int indexInBoardFromErstenTeil = getIndexInBoardFromErstenTeil(moveString);
		int indexInBoardFromLetzenTeil = getIndexInBoardFromLetzeTeil(moveString);
		//Setze erstmal die Ersten Teil von Move zu 1
		setBoard(getBoardIntern().substring(0, indexInBoardFromErstenTeil)+ "1" + getBoardIntern().substring(indexInBoardFromErstenTeil+1));
		//Setze dann die letzen Teil eine w Wenn white und eine b Wenn Black
		String toSet = (player.equals(this.whitePlayer)) ? "w" : "b";
		setBoard(getBoardIntern().substring(0, indexInBoardFromLetzenTeil)+ toSet + getBoardIntern().substring(indexInBoardFromLetzenTeil+1));
	}

	/**
	 * Get index of the char in board from first part of move
	 * 
	 * @param moveString
	 * @return index from the place in board , zB a9-c7 returns 0, because the index of a9 in the board is 0 ->(FEN Notation (a9)111111111/1111111111/1111111111/.....
	 */
	private int getIndexInBoardFromErstenTeil(String moveString) {
		return ((9-getZahlVonErstenTeilVonMove(moveString))*11)+ (getBuchstabeVonErstenTeilVonMove(moveString)-97) ;
	}

	/**
	 * Get index of the char in board from last part of move
	 * 
	 * @param moveString
	 * @return index from the place in board , zB a9-c7 returns 24, because the index of c7 in the board is 24 ->(FEN Notation 1111111111/1111111111/11(c7)1111111/.....
	 * Wenn nicht klar ist siehe getIndexInBoardFromErstenTeil
	 */
	private int getIndexInBoardFromLetzeTeil(String moveString) {
		return ((9-getZahlVonLetzteTeilVonMove(moveString))*11)+ (getBuchstabeVonLetzteTeilVonMove(moveString)-97) ;
	}

	private boolean isValidMove(String moveString, Player player) {
		int differenceBetweenLetters = (moveString.charAt(0)-moveString.charAt(3));
		int differenceBetweenNumbers = (Integer.parseInt(moveString.substring(1, 2)) - Integer.parseInt(moveString.substring(4, 5)));
		if(player.equals(whitePlayer)){
			if(differenceBetweenLetters <= 1 && differenceBetweenLetters >=-1 && differenceBetweenNumbers == 1 )return true;
		}
		if(player.equals(blackPlayer)){
			if(differenceBetweenLetters <= 1 && differenceBetweenLetters >=-1 && differenceBetweenNumbers == -1 )return true;
		}
		return false;
	}

	/**
	 * Guck ob das Ziel (von dem Move) frei ist
	 * 
	 * @param moveString
	 * @return true wenn der Platz von dem letzten Teil des Moves frei ist
	 */
	private boolean istZielFrei(String moveString) {
		if(getCharacterInBoardFromZiel(moveString) == '1' )return true;
		return false;
	}



	/**
	 * So die Formel das Beschreibt die Character von Board ist pX 
	 * Wobei wobei p eine Buchstabe ist von a bis j und X eine Zahl zwischen 0-9  
	 * pX beschreibt den Platz ((X-9)*11)+(p-97) in Board , 
	 * der Wert 9 kommt von 0 bis 9 Zeilen und der Wert 97 von der Ascci Tabelle
	 * @param moveString
	 * @return char also '1' oder 'w' oder 'b'
	 */
	private char getCharacterInBoardFromZiel(String moveString) {
		return getBoardIntern().charAt(((9-getZahlVonLetzteTeilVonMove(moveString))*11)+((getBuchstabeVonLetzteTeilVonMove(moveString))-'a'));
	}
	/**
	 * Das gleiche wie getCharacterInBoardfromZiel nur hlat von der Urprung
	 * @param moveString
	 * @return char also '1' oder 'w' oder 'b'
	 */
	private char getCharacterInBoardFromUrsprung(String moveString) {
		return getBoardIntern().charAt(((9-getZahlVonErstenTeilVonMove(moveString))*11)+((getBuchstabeVonErstenTeilVonMove(moveString))-'a'));
	}

	public boolean isFirstRound(Player player){
		if((this.whitePlayer.equals(player)&& !getBoardIntern().contains("W")) || (this.blackPlayer.equals(player)&& !getBoardIntern().contains("B")) ){
			return true;
		}
		return false;
	}

	public String convertString(String boardState){
		String convertedString = boardState;
		//System.out.println("boardstate bei Aufruf: "+boardState);
		if(convertedString.matches("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/")) {
			convertedString = "1111111111" + convertedString +"1111111111";
		}
		if(convertedString.matches("[0-9]*[W]?[0-9]*/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/")) {
			convertedString = convertedString +"1111111111";
		}
		String einsen = "11";
		//k�nnte sein dass wir diese Schleife sparen k�nnen, weis� nicht genau wie replace intern funktioniert ob es ersetz und weiter beim n�chsten Character macht ? also w�rude /// nicht korrigieren deswegen schleife
		for(int i = 0; i <= 9 ; i++){
			convertedString = convertedString.replace("//", "/1111111111/");
		}
		for(int i = 2; i <= 9 ; i++){
			convertedString = convertedString.replace(String.valueOf(i), einsen);
			einsen += "1";
		}
		//System.out.println("nach Konvertierung: "+convertedString);
		return convertedString;
	}

	public String convertStringZurueck(String boardState){
		String convertedString = boardState;
		String changeAllOnes = "1111111111";
		if(convertedString.contains(changeAllOnes)){
			convertedString = convertedString.replace(changeAllOnes, "");
		}
		String toReplace = "111111111";
		for(int i = 9 ; i > 1; i--){
			if(convertedString.contains(toReplace)){
				convertedString = convertedString.replace(toReplace, i+"");
			}
			toReplace = toReplace.substring(0, toReplace.length()-1);
		}
		//System.out.println("r�ckgabe: "+convertedString);
		return convertedString;
	}


}
