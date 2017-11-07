public class Player extends AbstractPlayer{
	private String password;
	private int highestScore;

/*
 * @param name	login name of the player
 * @param pswd	password of the player
 * @param ch	character of the player used on a grid (either X or O)
 * @param hs	highest score ever achieved by player
 * 
 */

public Player(String name, String pswd, char ch, int hs) {	//parameters as follows: login, password, XorO, highestScore
		super(name, pswd, ch, hs);
		
		loginName=name;
		password=pswd;
		highestScore= hs;
		XorO=ch;
	}

public String getName() {//Assuming the login name is the Name.
	return loginName;
}

public char getMark() { return XorO; }

public int getCurGameScore() { return curGameScore;}

public int getCurRoundScore() {	return curRoundScore; }

public int getHighScore(){ 	return highestScore;}

public void incCurGameScore(int i) {  curGameScore += i;} 

public void incCurRoundScore(int i) { curRoundScore += i;} 

}//end of class Player