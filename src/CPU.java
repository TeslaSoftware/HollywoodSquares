public class CPU extends AbstractPlayer{
/*
 * @param name	name of the CPU
 * @param ch	character of the player used on a grid (either X or O)
 * 
 */

public CPU(String name, String pswd, char ch, int hs) {	//parameters as follows: login, password, XorO, highestScore
		super(name, pswd, ch, hs);
		
		loginName=name;
		XorO=ch;
	}

public String getName() { return loginName; }

public char getMark() { return XorO; }

public int getCurGameScore() { return curGameScore;}

public int getCurRoundScore() {	return curRoundScore; }

public void incCurGameScore(int i) { curGameScore += i;} 

public void incCurRoundScore(int i) { curRoundScore += i;} 

}//end of class CPU