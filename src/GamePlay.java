import java.io.*;
import java.util.*;

public class GamePlay{

  private String PlayersDBPath;
  private String QcADBPath;
  private String ScoreboardDBPath;
  private Square[] squares;
  private boolean gameOver;
  private boolean answer;

  private int key;
  private int squareSelected;
  private int lowestScoreOnScoreboard;
  private int round = 0;

  private AbstractPlayer player1;
  private AbstractPlayer player2;
  private AbstractPlayer currentPlayer;
  private AbstractPlayer previousPlayer;

  private ArrayList<ArrayList<String>> dbQA;
  private ArrayList<ArrayList<String>> dbScoreboard;
  private Hashtable<Character, ArrayList<ArrayList<String>>> dbPlayers;
  /*
   * @param PDB		path to the Players database
   * @param QnADB	path to the Questions and Answers database
   * @param SDB		path to the ScoreBoard database
   * @param hs	highest score ever achieved by player
   *
   */

  public GamePlay(String PDB, String QnADB, String SDB){
	  	PlayersDBPath = PDB;
	  	QcADBPath = QnADB;
	  	ScoreboardDBPath = SDB;
	  	squares = new Square[9];
  }

  //should be called before playing each game - sets variable
  public void play(){
	  //read from DB of scoreboard and get the lowest score and set it to lowestScoreOnScoreboard
	  readDB(ScoreboardDBPath);
	  //get int from the 3rd column (2nd index) in the 5th record (4th index)
	  lowestScoreOnScoreboard = Integer.parseInt(dbScoreboard.get(2).get(4));
	  round = 1;
	  initializeSquares();
	  currentPlayer = player1;
	  previousPlayer = player2;
  }

  public boolean login(String loginN, String pswd){
	  //if player name is CPU and player 1 is initialized then instantiate player 2 as CPU
	  if(loginN.equals("CPU") && player1 != null){
		  player2 = new CPU(loginN, pswd, 'X', 99999);
		  return true;
	  }
	  //read in database and if credentials are incorrect return false
	  readDB(PlayersDBPath);
	  int highestScore = 0;
	  ArrayList<ArrayList<String>> bucket = dbPlayers.get(loginN.charAt(0));
	  //locate record containing credentials of a player
	  boolean found = false;
	  if(bucket == null) return false;
	  for(int idx = 0; idx < bucket.size(); idx ++){
		 ArrayList<String> currentRecord = bucket.get(idx);
		 //check if this record is the login name you are looking for
		 if(currentRecord.get(0).equals(loginN)){
			 //check if passwords are matching
			 if(currentRecord.get(1).equals(pswd)) {
				//if credentials are correct then get the highest score of the player
					highestScore = Integer.parseInt(currentRecord.get(2));
					found = true;
					break; //break off the loop to don't waste CPU cycles
			 }  else return false;
		 }
	  }
	  if(!found) return false;

	  //if player 1 not initialized
	  if(player1 == null){
		  player1 = new Player(loginN, pswd, 'O',highestScore);
		  return true;
	  }
	  else{
		  player2 = new Player(loginN, pswd, 'O',highestScore);
	  }
	  //if player was added/logged in to the game then return true
	  return true;
  }

  public void logout(){
	  player1 = null;
	  player2 = null;
  }

  public boolean register(String loginName, String pswd, String pswdRetyped){
	  //if passwords don't match then return false
	  if( !pswd.equals(pswdRetyped)) return false;
	  readDB(PlayersDBPath);
	  //check if record already exists in database return false. First get first character of loginName and get bucket from hash table
	  ArrayList<ArrayList<String>> bucket = dbPlayers.get(loginName.charAt(0));
	  //check if bucket for this character exists
	  if(!(bucket == null)){
		  //locate record containing credentials of a player
		  for(int idx = 0; idx < bucket.size(); idx ++){
			  if(bucket.get(idx).get(0).equals(loginName)) return false;
		  }
	  }
	  //else if bucket does not exists then instantiate it
	  else{
		  bucket = new  ArrayList<ArrayList<String>>();
	  }

	  //Save db of Players
    try{
      // set boolean append = true (2nd constructor of File Writer)
      BufferedWriter out = new BufferedWriter(new FileWriter(PlayersDBPath, true));
      out.append(loginName+"\t"+pswd+"\t"+"0");
      out.newLine();
      out.close();
    }catch(Exception e){
      System.err.println(e);
      return false;
    }
	  //if everything went correctly then return true
	  return true;
  }

  private void initializeSquares(){
	  
	  String [] celebritiesArr = { "Jimmy Fallon", "Lady Gaga", "Donald Trump", "Ariana Grande", "Oprah Winfrey", "Lil Wayne", "Rihanna",
			  "George Lucas", "Barack Obama", "Avril Lavigne", "Alicia Keys", "Beyonce Knowles",  "Brad Pitt", "Angelina Jolie", "Bruce Willis",
			  "Celine Dion", "Cameron Diaz", "Carmen Electra", "Christina Aguilera", "David Beckham", "David Guetta", "Denzel Washington", "Gwen Stefani",
			  "Hugh Jackman","Jennifer Aniston", "Jennifer Lawrence", "Jennifer Lopez", "Jessica Alba", "Johnny Depp", "Justin Bieber", "Justin Timberlake",
			  "Princess Kate", "Katy Perry", "Keanu Reeves", "Kelly Clarkson", "Leonardo DiCaprio", "Lindsay Lohan", "Megan Fox", "Miley Cyrus",
			  "Naomi Campbell", "Natalie Portman", "Nicole Kidman", "Orlando Bloom", "Pamela Anderson", "Paris Hilton", "Penelope Cruz", "P!nk",
			  "Salma Hayek","Sandra Bullock","Selena Gomez", "Shakira", "Taylor Swift", "Tiger Woods", "Tyra Banks", "Tom Cruise", "Usher", "Will Smith",
			  "PSY"};
	  ArrayList<String> celebrities = new ArrayList<String>();
	  for(int i =0 ; i <celebritiesArr.length; i++ ) celebrities.add(celebritiesArr[i]);

	  String question ="", answerToQuestion = "";
	  boolean ansIsCorrect = true;
	  Random random = new Random();

	  // Copy QA database into an ArrayList
	  readDB(QcADBPath);

	  for(int i = 0; i <9; i++) {
		  //get random celebrity from the list and remove this celebrity from list to avoid duplicates
		  String cName = new String ( celebrities.remove(new Random().nextInt(celebrities.size())));
	      // Randomly select a question
	      int index = random.nextInt(dbQA.size()-1);
	      // Randomly select a right or wrong answer
	      ansIsCorrect = random.nextBoolean();

	      // Copy question from ArrayList to a String and remove question to avoid duplicates
	      question = dbQA.get(0).get(index);
	
	      // Copy Celebrity's answer to a String and remove both answers to avoid duplicates
	      if(ansIsCorrect) {
	        answerToQuestion = dbQA.get(1).get(index);
	      } else{
	        answerToQuestion = dbQA.get(2).get(index);  
	      }
	      dbQA.get(0).remove(index);
	      dbQA.get(1).remove(index);
	      dbQA.get(2).remove(index);
	      squares[i] = null;
	      squares[i] = new Square(cName, question, answerToQuestion, ansIsCorrect);
	  }



  }

//read from the scoreboard database and return value of the entries in the array of strings
public String[] getScoreboard(){
	readDB(ScoreboardDBPath);
	String[] scores =  new String[5];
	for(int i = 0; i <5; i++)
		scores[i] = dbScoreboard.get(0).get(i)+". " + dbScoreboard.get(1).get(i) + "\t" + dbScoreboard.get(2).get(i);
	return scores;
}

  public void setKey(int i){ key = i; }

  //expected value allowed 0-8
  public void setSquareSelected(int i){ squareSelected = i; }

  public void setAnswer(boolean ans){ answer = ans; }

  public int getRound(){ return round; }

  //@return returns string containing question from selected square
  public String getQuestion(){ return squares[squareSelected].getQuestion(); }

  //@return returns string containing question from selected square
  public String getCelebAnswer(){ return squares[squareSelected].getCelebAnswer();}

  public String getCelebrityName(){ return squares[squareSelected].getCelebrity();}
  
  public boolean verifyPlayerAnswer(){ return answer == squares[squareSelected].celebIsCorrect();}

  public void nextTurn(){
	  //check if there is a round winner
	  if(isRoundWinner()){
		  //increment winning player current game score by score achieved in this round +100 bonus for winning
		  currentPlayer.incCurGameScore(currentPlayer.getCurRoundScore() + 100);
		  //reset players scores to zero for this new round
		  currentPlayer.incCurRoundScore(-currentPlayer.getCurRoundScore());
		  previousPlayer.incCurRoundScore(-previousPlayer.getCurRoundScore());
		  round++;
		  initializeSquares();
		  //check all 5 rounds were played and if so then game is over
		  if(round > 5){
			  gameOver = true;
			  //check if either one of human players qualifies for score board or reached its own personal highest score
			  if(player1.getCurGameScore() > ((Player) player1).getHighScore()) saveHighScore(player1);
			  if(player1.getCurGameScore() > lowestScoreOnScoreboard ) saveToScoreboard(player1);
			  if(!player2.getClass().getName().equals(new String ("CPU"))) {
				  if(player2.getCurGameScore() > ((Player) player2).getHighScore()) saveHighScore(player2);
				  if(player2.getCurGameScore() > lowestScoreOnScoreboard ) saveToScoreboard(player2);
			  }
		  }
	  }
	  else swapPlayers();

  }

  public AbstractPlayer getCurrentPlayer(){ return currentPlayer; }

  public AbstractPlayer getPreviousPlayer(){ return previousPlayer; }
  
  public AbstractPlayer getPlayer1(){ return player1; }

  public AbstractPlayer getPlayer2(){ return player2; }


  public boolean isGameOver(){ return gameOver; }

  //this class checks if there is a winner of this round
  public boolean isRoundWinner(){
	  char p1 = player1.getMark();
	  char p2 = player2.getMark();
	  //check if either of the players conquered 5 squares
	  int sumP1 = 0;
	  int sumP2 = 0;
	  for(int i=0; i<9; i++){
		  if(p1 == squares[i].belongsTo()) sumP1++;
		  if(p2 == squares[i].belongsTo()) sumP2++;
	  }
	  if(sumP1 >=5 || sumP2 >= 5) return true;
	  //check if either player wins diagonal-wise
	  if( ((squares[0].belongsTo() == squares[4].belongsTo()) && (squares[4].belongsTo() == squares[8].belongsTo()) && squares[4].belongsTo()!=' ')
			  || ((squares[2].belongsTo() == squares[4].belongsTo()) && (squares[4].belongsTo() == squares[6].belongsTo()) && squares[4].belongsTo()!=' ') ) return true;
	  //check if either player wins row-wise
	  for(int i =0; i<9; i+=3){
		  if( (squares[i].belongsTo() == squares[i+1].belongsTo()) && (squares[i+1].belongsTo() == squares[i+2].belongsTo())  && squares[i+1].belongsTo()!=' ' ) return true;
	  }
	  //check if either player wins column-wise
	  for(int i =0; i<3; i++){
		  if( (squares[i].belongsTo() == squares[i+3].belongsTo()) && (squares[i+3].belongsTo() == squares[i+6].belongsTo())  && squares[i].belongsTo()!=' ') return true;
	  }
	  //if non of the above conditions are met then game is still on
	  return false;
  }

  public boolean willCauseWin(int i){
	  char sign = previousPlayer.getMark();
	  //there are only 3 patters of placement of mark that could cause adversary player to win
	  //1) Middle (4 ways to win) - only one option on the grid, locations of indexes in array: 4
	  //2) Corner (3 ways to win) - 4 options on the grid, locations of indexes in array: 0, 2, 6, 8
	  //3) Side (2 ways to win) - 4 options on the grid, locations of indexes in array: 1, 3, 5 ,7
	  // also player can win by acquiring 5 squares

	  int sumOpp =0; //value to sum opponent marks in squares
	  for(int j = 0; j <9; j++) { if(squares[j].belongsTo() == sign) sumOpp++; }
	  if(sumOpp >= 4) return true; //if player has 4 squares acquired and this would be player's 5th square then player will win

	  switch (i){
		  case 0: { if(squares[1].belongsTo() == sign && squares[2].belongsTo() == sign ) return true;
		  			if(squares[4].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
		  			if(squares[3].belongsTo() == sign && squares[6].belongsTo() == sign ) return true;
		  			break; }
		  case 1: { if(squares[0].belongsTo() == sign && squares[2].belongsTo() == sign ) return true;
		  			if(squares[4].belongsTo() == sign && squares[7].belongsTo() == sign ) return true;
		  			break; }
		  case 2: { if(squares[0].belongsTo() == sign && squares[1].belongsTo() == sign ) return true;
					if(squares[4].belongsTo() == sign && squares[6].belongsTo() == sign ) return true;
					if(squares[5].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
					break; }
		  case 3: { if(squares[0].belongsTo() == sign && squares[6].belongsTo() == sign ) return true;
					if(squares[4].belongsTo() == sign && squares[5].belongsTo() == sign ) return true;
					break; }
		  case 4: { if(squares[0].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
					if(squares[1].belongsTo() == sign && squares[7].belongsTo() == sign ) return true;
					if(squares[2].belongsTo() == sign && squares[6].belongsTo() == sign ) return true;
					if(squares[3].belongsTo() == sign && squares[5].belongsTo() == sign ) return true;
					break; }
		  case 5: { if(squares[2].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
					if(squares[3].belongsTo() == sign && squares[4].belongsTo() == sign ) return true;
					break; }
		  case 6: { if(squares[0].belongsTo() == sign && squares[3].belongsTo() == sign ) return true;
					if(squares[4].belongsTo() == sign && squares[2].belongsTo() == sign ) return true;
					if(squares[7].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
					break; }
		  case 7: { if(squares[1].belongsTo() == sign && squares[4].belongsTo() == sign ) return true;
					if(squares[6].belongsTo() == sign && squares[8].belongsTo() == sign ) return true;
					break; }
		  case 8: { if(squares[0].belongsTo() == sign && squares[4].belongsTo() == sign ) return true;
					if(squares[2].belongsTo() == sign && squares[5].belongsTo() == sign ) return true;
					if(squares[6].belongsTo() == sign && squares[7].belongsTo() == sign ) return true;
					break; }
	  }
	  //if all the conditions above failed then return false
	  return false;
  }

  public boolean isValidMove(int i) {
	  if( i >= 0 && i < 9) return !squares[i].isTaken();
	  else return false;
  }

  private void saveHighScore(AbstractPlayer player){
    // Copy TSV into a Hashtable
    readDB(PlayersDBPath);

    // get player's key and find their Set
    char ch = player.loginName.charAt(0);
    ArrayList<ArrayList<String>> bucket = dbPlayers.get(ch);

    // Find player in Set and update their score.  Place Set back in Hashtable
    for(int i = 0; i < bucket.size(); i++){
    	if(bucket.get(i).get(0).equals(player.getName())) {
    		String highScore = Integer.toString(player.getCurGameScore());
    		bucket.get(i).set(2, highScore );
    	}
    		
    }

    // Update database by overwriting TSV with new Hashtable 
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter(PlayersDBPath));
      //creating enumeration and iterating over it to get each bucket
      Enumeration<ArrayList<ArrayList<String>>> buck = dbPlayers.elements();
      while(buck.hasMoreElements()){
    	  //iterating over each bucket to get each data record
    	  ArrayList<ArrayList<String>> elem = buck.nextElement();
    	  //concatenating tokenized data into tsv file
    	  for(int i = 0; i < elem.size(); i++){
    		  out.write(elem.get(i).get(0)+"\t" + elem.get(i).get(1)+"\t" + elem.get(i).get(2)+"\t");
    		  out.newLine();
    	  } 	  
      }
      out.close();
    }
    catch(Exception e){ System.err.println(e); }

  }

  private void saveToScoreboard(AbstractPlayer player){
    // reads current database for Scoreboard and puts into an ArrayList
    readDB(ScoreboardDBPath);

    // Loop through ArrayList and add new score in correct spot
    for (int i = 0; i < dbScoreboard.get(0).size(); i++){
    	if(Integer.parseInt(dbScoreboard.get(2).get(i)) < player.getCurGameScore() ){
    		dbScoreboard.get(0).add(i, Integer.toString(i+1));
    		dbScoreboard.get(1).add(i, player.getName());
    		dbScoreboard.get(2).add(i, Integer.toString(player.getCurGameScore()));
    		break;
    	}    	
    }
    dbScoreboard.get(0).remove(5); //remove 6th element from scoreboard
    dbScoreboard.get(1).remove(5);
    dbScoreboard.get(2).remove(5);
    
    	
    // Update database by overwriting TSV with data from updated ArrayList
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter(ScoreboardDBPath));
      for(int i = 0; i < dbScoreboard.get(0).size(); i++)
      {
    	  //concatenate the string and separate values using tabulator
        out.write(dbScoreboard.get(0).get(i)+"\t"
                  +dbScoreboard.get(1).get(i)+"\t"
                  +dbScoreboard.get(2).get(i));
        out.newLine();
      }
      out.close();
    }
    catch(Exception e){  System.err.println(e);}


  }
  
  private void swapPlayers(){
	  if(currentPlayer == player1) {
		  currentPlayer = player2;
		  previousPlayer = player1;
	  }
	  else{
		  currentPlayer = player1;
		  previousPlayer = player2;
	  }
  }

  //read from given database to main memory reference
  private void readDB(String DBPath){
	  Scanner input = null;
	  try { input = new Scanner(new FileReader(DBPath)).useDelimiter("\\t"); }
	  catch (FileNotFoundException e) { System.err.println(e); }
	  //create arrayLists to store values for each column
	  ArrayList<String> column1 = new ArrayList<String>();
	  ArrayList<String> column2 = new ArrayList<String>();
	  ArrayList<String> column3 = new ArrayList<String>();
	  //read from tsv file and store values in order
	  while(input.hasNext()){
		  String[] values = input.nextLine().split("\\t");
		  column1.add(values[0]);
		  column2.add(values[1]);
		  column3.add(values[2]);
	  }
	  //check which db is requested to be read and proceed accordingly
	  if(DBPath.equals(QcADBPath)){
		  dbQA = null;
		  dbQA = new ArrayList<ArrayList<String>>();
		  // load columns into dbQA
		  dbQA.add(column1);
		  dbQA.add(column2);
		  dbQA.add(column3);
	  }
	  else if(DBPath.equals(ScoreboardDBPath)){
		  dbScoreboard = null;
		  dbScoreboard = new ArrayList<ArrayList<String>>();
		  // load columns into dbScoreboard
		  dbScoreboard.add(column1);
		  dbScoreboard.add(column2);
		  dbScoreboard.add(column3);

	  }
	  else if(DBPath.equals(PlayersDBPath)){
		  dbPlayers = null;
		  dbPlayers = new Hashtable<Character, ArrayList<ArrayList<String>>>();
		  //get name, password and highest score for each player
		  while(!column1.isEmpty()){
			  String name = column1.remove(0);
			  String pswd = column2.remove(0);
			  String hs = column3.remove(0);
			  //map first character of name to store it in hash table dbPlayers
			  char ch = name.charAt(0);
			  //create data record that will store players name, password and score
			  ArrayList<String> dataRecord = new ArrayList<String>();
			  dataRecord.add(name);
			  dataRecord.add(pswd);
			  dataRecord.add(hs);
			  //hash the datarecord into hashtable of players - dbPlayers
			  ArrayList<ArrayList<String>> bucket;
			  //check if bucket exists, if it doesnt then create new bucket
			  if( dbPlayers.get(ch) == null) { bucket = new ArrayList<ArrayList<String>>(); }
			  //else the bucket exists so get its reference
			  else { bucket = dbPlayers.get(ch);}
			  bucket.add(dataRecord); //add data to bucket
			  dbPlayers.put(ch, bucket); //add bucket to hash table
		  }
	  }
	  input = null; //clean up
  }
  
  public Square[] getSquares(){
	  return squares;
  }
}