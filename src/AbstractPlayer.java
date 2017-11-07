public abstract class AbstractPlayer{

  protected String loginName;	
  protected int curGameScore = 0;	
  protected int curRoundScore = 0;	
  protected char XorO;  //ADDED VARIABLE. NOT ON THE UML CLASS DiAGRAM 1
    
  
  public AbstractPlayer(String str_1, String str_2, char ch, int integer){}

  //These methods must be implemented by any class that extends abstractPlayer
  public abstract String getName();

  public abstract char getMark();

  public abstract int getCurGameScore();

  public abstract int getCurRoundScore();

  public abstract void incCurGameScore(int integer);

  public abstract void incCurRoundScore(int integer);
}