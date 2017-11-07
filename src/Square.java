class Square{

  private String celebrity;
  private boolean taken;
  private char owner;
  private String question;
  private String answer;
  private boolean answerIsCorrect;
  
  /*
   * @param celebrity	celebrity name
   * @param question	question that celebrity will be asked
   * @param answer		answer that celebrity will give
   * @param taken		boolean indicating if square is taken  
   */ 
  
  public Square(String celeb, String q, String ans, boolean b ){
	  celebrity = celeb;
	  question = q;
	  answer = ans;
	  answerIsCorrect = b;
	  taken = false;
	  owner = ' ';
  }

  public String getCelebrity(){ return celebrity; }

  public boolean isTaken(){ return taken; }

  public char belongsTo(){ return owner; }
  
  public String getQuestion(){ return question;}
  
  public String getCelebAnswer(){ return answer; }
  
  public boolean celebIsCorrect() { return answerIsCorrect; }

  //mark square with character sign of the player
  public void acquireSquare(char sign){ 
	  taken = true;
	  owner = sign; 
  }


}