import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class HollywoodSquaresCLI {
	private GamePlay game;
	private Scanner input;
	public static boolean timeout;
	
	//Constructor
	public HollywoodSquaresCLI(){
		String pathDBPlayers = System.getProperty("user.dir") + "/db/user_accounts.tsv"; //location of Players database
		String pathDBQnA = System.getProperty("user.dir") + "/db/hs_qa.tsv"; //location of Questions and Answers database
		String pathDBScores = System.getProperty("user.dir") + "/db/global_scoreboard.tsv"; //location of Scoreboard database
		game = new GamePlay(pathDBPlayers, pathDBQnA, pathDBScores);
		input = new Scanner(System.in);
		
		
	}

	
	
	public void mainMenu(){
		int key;
		do{
			input = new Scanner(System.in);
			displayMainMenu();
			key = Character.getNumericValue(input.next().charAt(0));
			switch(key){
				
				//get user input and login
				case 1:{
					login();//login players and proceed with game
					game.logout(); //after game is over log out
					break;
				}
					
				//get user input and register	
				case 2:{
					register();
					break;
				}
				
				//show scoraboard to user
				case 3:
					displayScoreboard();
					break;
				
				//just exit
				case 4: { System.exit(0); break ;}	
				
			}
			
			
		}while(key !=4);	
	
	}
	
	private void login(){
		boolean tryAgain = false;
		int mode;
		String decide;
		do{
			displayLoginMenu(1);
			String loginName = input.next();
			System.out.println("Please your password:");
			String pswd = input.next();
			//if player's credentials are correct then ask if he wants to play vs CPU or vs another Player
			if(game.login(loginName, pswd)){
				System.out.println("Player 1 login successful!");
				boolean p2selected = false;
				do{
					System.out.println("Please select the game mode:");
					System.out.println("1 - vs CPU");
					System.out.println("2 - vs Player 2");
					mode = Character.getNumericValue(input.next().charAt(0));
					if(mode == 1){
						game.login("CPU", "computer");
						p2selected = true;
						play();
					}
					else if(mode == 2){
						do{
							displayLoginMenu(2);
							loginName = input.next();
							System.out.println("Please your password:");
							pswd = input.next();	
							//if player 2 login is successful you can start the game
							if(game.login(loginName, pswd)){
								p2selected = true;
								play();
							}
							else{
								System.out.println("You have entered an invalid username or password");
								System.out.println("Would you like to try again?");
								System.out.println("Y - yes");
								System.out.println("N - no");
								decide = input.next();			
								if(decide.toLowerCase().equals("yes") || decide.toLowerCase().equals("y") || decide.toLowerCase().equals("yeah") || decide.toLowerCase().equals("yup")) tryAgain = true; 
								else tryAgain = false;
							}
						}while(tryAgain);
						
					}
					else System.out.println("Incorrect input!! Please select 1 or 2");
				}while(!p2selected);
				
			}
			else{
				System.out.println("You have entered an invalid username or password");
				System.out.println("Would you like to try again?");
				System.out.println("Y - yes");
				System.out.println("N - no");
				decide = input.next();			
				if(decide.toLowerCase().equals("yes") || decide.toLowerCase().equals("y") || decide.toLowerCase().equals("yeah") || decide.toLowerCase().equals("yup")) tryAgain = true; 
				else tryAgain = false;
			}
		}while(tryAgain);
		input = null;
	}
	

	//this method supervises game play until the of game
	private void play(){
		String inputStr;
		boolean correctSelection;
		boolean playerAgree = true;
		int selectedSquare= 99;
		game.play();
		while(!game.isGameOver()){
			
				if(game.getCurrentPlayer().getName().equals("CPU")) {
					doCPUTurn();
				}
				else{
					correctSelection = false;
					//ask user to select square
					while(!correctSelection){
						displayTurn();
						try{ 
							try { 
								String str = input.next();
								selectedSquare= Integer.parseInt(str); }
							catch(NullPointerException e){} ;
							}
						catch(NumberFormatException e){ System.out.println("Incorrect input. Please try again by entering a numeric value" ); }	
						
						if(game.isValidMove(selectedSquare)){ correctSelection = true; }
						else System.out.println("Incorrect selection. Please select square from 0 to 8, which is not marked yet." );
					}
					game.setSquareSelected(selectedSquare);
					//Display question from the square
					System.out.println("Question: "+ game.getQuestion());
					//delay answer to make it look natural
					try { TimeUnit.SECONDS.sleep(1);} 
					catch (InterruptedException e) {}
					//Let celebrity to answer it
					System.out.println(game.getCelebrityName() + ": " + game.getCelebAnswer());
					//ask user to agree or disagree
					correctSelection = false;
					timeout = false;
					long startTime = System.currentTimeMillis();
					//timer with lambda function
					Timer tim =new Timer(); 
					tim.schedule( new TimerTask() {
					            @Override
					            public void run() {
					               System.out.println("\nTIME OUT!");
					               HollywoodSquaresCLI.timeout=true;
					            }
					        }, 
					        15000 //timeout limit in miliseconds
					);
				    
					while(!correctSelection && !timeout){
						System.out.println("Do you agree with "+ game.getCelebrityName() + "?");
						System.out.println("Y - yes");
						System.out.println("N - no");
						inputStr = input.next();			
						if(inputStr.toLowerCase().equals("yes") || inputStr.toLowerCase().equals("y") || inputStr.toLowerCase().equals("yeah") || inputStr.toLowerCase().equals("yup")) {
							correctSelection = true; 
							playerAgree = true;
						}
						else if(inputStr.toLowerCase().equals("no") || inputStr.toLowerCase().equals("n") || inputStr.toLowerCase().equals("nope") || inputStr.toLowerCase().equals("nah")){
							correctSelection = true; 
							playerAgree = false;
						}
					}
					tim.cancel();
					//determine if player is correct
					game.setAnswer(playerAgree);
					if(game.verifyPlayerAnswer() && !timeout){
						//aquire square
						game.getSquares()[selectedSquare].acquireSquare(game.getCurrentPlayer().getMark());
						System.out.println("Correct!");
						//give points
						long finishTime = System.currentTimeMillis();
						int points = (int) (160- ((finishTime - startTime)/1000)*10); //calculate points to give
						game.getCurrentPlayer().incCurRoundScore(points);
					}
					else if(timeout){
						System.out.println("TOO SLOW!!! You run out of time!");
					}
					else{
						System.out.println("Incorrect...");
						//else opponent acquires square if it will not cause him/her to win
						if(!game.willCauseWin(selectedSquare)) game.getSquares()[selectedSquare].acquireSquare(game.getPreviousPlayer().getMark());
					}
				}			
			//if there is round winner then congratulate them and inform players about new round 	
			if(game.isRoundWinner()){
				System.out.println("\nCONGRATULATIONS!!! " + game.getCurrentPlayer().getName() + " is a winner of this round!");
				System.out.println("============================================");
				if(game.getRound() > 5) {
					System.out.println("GAME OVER");
					System.out.print("THE WINNER IS:  "); 
					if(game.getCurrentPlayer().getCurGameScore() > game.getPreviousPlayer().getCurGameScore())System.out.println(game.getCurrentPlayer().getName());
					else System.out.println(game.getPreviousPlayer().getName());
					System.out.println("============================================");
				}
				else System.out.println("ROUND #" + (game.getRound() +1) + " of 5");
				System.out.println("============================================");
				
			}
			//prepare game for next turn
			game.nextTurn();
			
		}
		
		
		
		
	}
	
	private void doCPUTurn(){
		displayTurn();
		Random ran = new Random();
		int number = 99;
		boolean CPUanswer = false;
		//select square
		do{
			number = ran.nextInt(9);
		}while(!game.isValidMove(number));
		System.out.println("CPU selected square #"  + number);
		game.setSquareSelected(number);
		//get question
		System.out.println("Question: "+ game.getQuestion());
		//delay answer to make it look natural
		try { TimeUnit.SECONDS.sleep(1);} 
		catch (InterruptedException e) {}
		//Let celebrity to answer it
		System.out.println(game.getCelebrityName() + ": " + game.getCelebAnswer());
		//agree or disagree with celebrity
		CPUanswer = ran.nextBoolean();
		if(CPUanswer) System.out.println("CPU agrees!!!");
		else System.out.println("CPU does not agree...");
		game.setAnswer(CPUanswer);
		if(game.verifyPlayerAnswer()){
			//aquire square
			game.getSquares()[number].acquireSquare(game.getCurrentPlayer().getMark());
			System.out.println("Correct!");
			//give points
			game.getCurrentPlayer().incCurRoundScore(ran.nextInt(10)*10);
		}
		else{
			System.out.println("Incorrect...");
			//else opponent acquires square if it will not cause him/her to win
			if(!game.willCauseWin(number)) game.getSquares()[number].acquireSquare(game.getPreviousPlayer().getMark());
		}
		
	}
	
	private void register(){
		String decision;
		boolean tryAgain = true;
		while(tryAgain){
			String name, pswd1, pswd2;
			System.out.println();
			System.out.println("================================================================");
			System.out.println("REGISTER MENU");
			System.out.println("================================================================");
			System.out.println("Please enter user name: [Only alphanumeric values allowed. Name has to be at least 3 characters and no longe than 10");
			name = input.next();
			//validate user name
			if(!validateString(name)) System.out.println("Invalid user name!");
			else{
				System.out.println("Please enter password: [Only alphanumeric values allowed. Name has to be at least 3 characters and no longe than 10");
				pswd1 = input.next();
				System.out.println("Please retype your passwor:");
				pswd2 = input.next();
				//check is passwords match
				if(!pswd1.equals(pswd2)) System.out.println("Passwords do not match!");
				//validate password
				else if(!validateString(pswd1)){
					System.out.println("Invalid passwod! Password has to be at least 3 characters and no longe than 10 and contain only alphanumeric characters.");
				}
				else{
					if(game.register(name, pswd1, pswd2)) {
						System.out.println("Registration has been successful !!!");
						return;
					}
					else System.out.println("Username you selected already exists");
				}
			}
			do{
				System.out.println("Would you like to try again?");
				decision = input.next();
				if(decision.toLowerCase().equals("yes") || decision.toLowerCase().equals("y") || decision.toLowerCase().equals("yeah") || decision.toLowerCase().equals("yup")) {
					decision="y";
					tryAgain=true;
				}
				else if(decision.toLowerCase().equals("no") || decision.toLowerCase().equals("n") || decision.toLowerCase().equals("nope") || decision.toLowerCase().equals("nah")){
					decision="n";
					tryAgain=false;
				}
					
			}while(!decision.equals("y" ) && !decision.equals("n"));			
		}

		
		
	}
	
	private boolean validateString(String name){
		if(name.length()<3 || name.length()>10) return false; //check if string has correct length
		if(!name.matches("[A-Za-z0-9]+")) return false;  //check if string is not alphanumeric
		return true;
	}

	
	private void displayMainMenu(){
		System.out.println();
		System.out.println("================================================================");
		System.out.println("MAIN MENU");
		System.out.println("================================================================");
		System.out.println("Please enter the key associated with option you wish to select:");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.println("3. Show Scoreboard");
		System.out.println("4. Exit");
	}
	
	private void displayLoginMenu(int i){
		System.out.println();
		System.out.println("================================================================");
		System.out.println("LOGIN MENU");
		System.out.println("================================================================");
		System.out.println("Player "+ i + " please your user name:");
		
	}
	
	private void displayScoreboard(){
		System.out.println();
		String[] scores = game.getScoreboard();
		System.out.println("================================================================");
		System.out.println("SCOREBOARD");
		System.out.println("================================================================");
		for(int i = 0; i < scores.length; i++) System.out.println(scores[i]);
		System.out.println("Press any key to continue...");
		try {System.in.read(); } 
		catch (IOException e) {}
		System.out.println();
	}
	
	private void displayTurn(){
		String p1 = game.getPlayer1().getName(); //get name of player 1
		//fix padding of name of player 1
		if(p1.length() <12 ) {
			for(int i = 1; i + p1.length() <= 12; i++) p1 += " ";
		}
		String p2 = game.getPlayer2().getName();//get name of player 2
		System.out.println("============================================");
		System.out.println("PLAYER 1    \t\t\tPLAYER 2    ");
		System.out.println(p1 + "\t\t\t" + p2);
		System.out.println("TOTAL SCORE\t\t\tTOTAL SCORE");
		System.out.println(game.getPlayer1().getCurGameScore() + "\t\t\t\t" + game.getPlayer2().getCurGameScore());
		System.out.println("ROUND SCORE\t\t\tROUND SCORE");
		System.out.println(game.getPlayer1().getCurRoundScore() + "\t\t\t\t" + game.getPlayer2().getCurRoundScore());
		System.out.println("============================================");
		System.out.println("INSTRUCTIONS\t\t    CURRECNT STATE OF GRID");
		System.out.println("------------\t\t\t------------");
		System.out.println("| 0 | 1 | 2 |\t\t\t| " + game.getSquares()[0].belongsTo() + " | " + game.getSquares()[1].belongsTo() + " | " + game.getSquares()[2].belongsTo() + " |");
		System.out.println("------------\t\t\t------------");
		System.out.println("| 3 | 4 | 5 |\t\t\t| " + game.getSquares()[3].belongsTo() + " | " + game.getSquares()[4].belongsTo() + " | " + game.getSquares()[5].belongsTo() + " |");
		System.out.println("------------\t\t\t------------");
		System.out.println("| 6 | 7 | 8 |\t\t\t| " + game.getSquares()[6].belongsTo() + " | " + game.getSquares()[7].belongsTo() + " | " + game.getSquares()[8].belongsTo() + " |");
		System.out.println("------------\t\t\t------------");
		System.out.println(game.getCurrentPlayer().getName() + ", please select square number according to instructions on the left:");
		
	}
}
