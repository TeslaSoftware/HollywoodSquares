import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

//see lines 313 and 98: edit the exit button's functionality (get it to write whatever's
//necessary to the game and then logout the player, then exit)

@SuppressWarnings("serial")
public class GamePlayGUI extends JFrame implements ActionListener{
	
	static private ArrayList<JButton> menuButtons = new ArrayList<JButton>();
	static private ArrayList<JButton> gameButtons = new ArrayList<JButton>();
	static private ArrayList<JButton> loginButtons = new ArrayList<JButton>();
	static private ArrayList<JButton> registerButtons = new ArrayList<JButton>();
	static private ArrayList<JLabel> currentGameInfo = new ArrayList<JLabel>();
	static private ArrayList<JPanel> menuPanel = new ArrayList<JPanel>();
	static private ArrayList<TextField> txtFields = new ArrayList<TextField>();	
	static private boolean[] squaresTaken = new boolean[9];
	static private GamePlay game;
	
	
////////////////////////////////	
    static private String btnValue;
	static int turn = 1;
////////////////////////////////	
	
	public GamePlayGUI(String player, String square, String score){}
	

    private static JMenuBar displayMainMenu() {

        //Menu bar.
        JMenuBar menuBar = new JMenuBar();

        
        //Initialize menu
		for (int i = 0; i < 4 ; i++){
			JButton btnInitialize= new JButton("");
			menuButtons.add(btnInitialize);
		}
		
		menuButtons.get(0).setText("Login");
		menuButtons.get(1).setText("Register");
		menuButtons.get(2).setText("Scoreboard");
		menuButtons.get(3).setText("Exit");
		
        //Login menu
		menuBar.add(menuButtons.get(0));
		menuButtons.get(0).addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e)
				{
					displayLoginMenu();
				}	
		  });
		
		menuButtons.get(0).setOpaque(false);
		menuButtons.get(0).setContentAreaFilled(false);
		menuButtons.get(0).setBorderPainted(false);
   
		//Register menu
        menuBar.add(menuButtons.get(1));
        
        menuButtons.get(1).addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e)
				{
					displayRegisterMenu();
				}	
		  });
        
        menuButtons.get(1).setOpaque(false);
        menuButtons.get(1).setContentAreaFilled(false);
        menuButtons.get(1).setBorderPainted(false);
        
        //Score board menu
        menuBar.add(menuButtons.get(2));

        menuButtons.get(2).addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e)
				{
					displayScoreboard();
				}	
		  });
        
        menuButtons.get(2).setOpaque(false);
        menuButtons.get(2).setContentAreaFilled(false);
        menuButtons.get(2).setBorderPainted(false);
        
        //Exit menu
        menuBar.add(menuButtons.get(3));
        menuButtons.get(3).addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e)
			{//perform logout operations: save info to database (hash table) and clear the player data from the GUI
				//JOptionPane.showMessageDialog(null, "Logout successfully!");
				if(JOptionPane.showConfirmDialog(null, "Logout successfully! Are you sure you want to exit?") == 0){
					game.logout();
					System.exit(0);
				}
			}	
	  });
        
        menuButtons.get(3).setOpaque(false);
        menuButtons.get(3).setContentAreaFilled(false);
        menuButtons.get(3).setBorderPainted(false);
        
        return menuBar;
    }
    
    
    private static void displayGame() {
    	
        JFrame frame = new JFrame("Hollywood Squares Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	    	//Info area    	
	    	JPanel pInfo = new JPanel(new BorderLayout());

	        //Content area	    	
	        JPanel pContent = new JPanel(new BorderLayout());

	        //Initialize menu	
	        frame.setJMenuBar(displayMainMenu());  
	        
	        pInfo.setLayout(new GridLayout(1,3));	   
	        pContent.setLayout(new GridLayout(3, 3));
	        
	        frame.add(pInfo, BorderLayout.NORTH);
	        frame.add(pContent, BorderLayout.CENTER);	        
	        
			for (int i = 0; i < 3 ; i++){
				JLabel lblInitialize= new JLabel("");
				currentGameInfo.add(lblInitialize);
			}
	        
	        ////////////////////////////////
	        currentGameInfo.get(0).setText("   " + "Welcome, " + game.getCurrentPlayer().getName() + " and " + 
	        		game.getPreviousPlayer().getName() + "!");
	        currentGameInfo.get(1).setText("Round 1");
	        currentGameInfo.get(2).setText("Score 100");
	        //Put value in the welcome string like player info
	        ////////////////////////////////
	        
	        pInfo.add(currentGameInfo.get(0)); 	 
	        pInfo.add(currentGameInfo.get(1)); 
	        pInfo.add(currentGameInfo.get(2)); 
	        
			for (int i = 0; i < 9 ; i++){
				JButton btnInitialize= new JButton("");
				gameButtons.add(btnInitialize);
			}
	            
	            //Get value from 9 buttons
				for (int i = 0; i < 9 ; i++){
					gameButtons.get(i).addActionListener(new ActionListener(){
						
						/////////////////////////////////////////////////////////
						//program ability to ensure that game rules are followed! i.e. one player after another, check if
						//there's a winner after each turn, will this move cause other player to win, etc.
						/////////////////////////////////////////////////////////
							public void actionPerformed(ActionEvent e)
							{
								JButton btnClicked = (JButton) e.getSource();		
								
						           if(turn == 1){
										btnClicked.setText("O");
										///////////////////////////////////////////
										//NOTE: use btnClicked.setEnabled(false) to "unclick" it if the player getting this square causes a 
										//"other-player-can-win" scenario
										//
										//also, turn's value and char's value should be updated by/received from GamePlay class ideally,
										//but can be avoided if hard/impossible to implement
										///////////////////////////////////////////
										btnValue = btnClicked.getText();										
										turn = 2;
						           }else{
										btnClicked.setText("X");
										btnValue = btnClicked.getText();
										turn = 1;
						           }
						           
									btnClicked.setEnabled(false);
							}	
					  });
					
					////////////////////////////////
					gameButtons.get(i).setText(btnValue);
					//Get button value into the array
					//
					////////////////////////////////


					pContent.add(gameButtons.get(i));
				
				}
				

				
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    
    private static void perfomCPUturn() {
    	
        JFrame frame = new JFrame("Hollywood Squares Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
	    	//Info area    	
	    	JPanel pInfo = new JPanel(new BorderLayout());

	        //Content area	    	
	        JPanel pContent = new JPanel(new BorderLayout());

	        //Initialize menu	
	        frame.setJMenuBar(displayMainMenu());  
	        
	        pInfo.setLayout(new GridLayout(1,3));	   
	        pContent.setLayout(new GridLayout(3, 3));
	        
	        frame.add(pInfo, BorderLayout.NORTH);
	        frame.add(pContent, BorderLayout.CENTER);	        
	        
			for (int i = 0; i < 3 ; i++){
				JLabel lblInitialize= new JLabel("");
				currentGameInfo.add(lblInitialize);
			}
	        
	        ////////////////////////////////
	        currentGameInfo.get(0).setText("   " + "Welcome, " + game.getCurrentPlayer().getName() + "Player!");
	        currentGameInfo.get(1).setText("Round 1");
	        currentGameInfo.get(2).setText("Score 100");
	        //Put value in the welcome string like player info
	        ////////////////////////////////
	        
	        pInfo.add(currentGameInfo.get(0)); 	 
	        pInfo.add(currentGameInfo.get(1)); 
	        pInfo.add(currentGameInfo.get(2)); 
	        
			for (int i = 0; i < 9 ; i++){
				JButton btnInitialize= new JButton("");
				gameButtons.add(btnInitialize);
			}
	            
	            //Get value from 9 buttons
				for (int i = 0; i < 9 ; i++){
					gameButtons.get(i).addActionListener(new ActionListener(){
						
							public void actionPerformed(ActionEvent e)
							{
								JButton btnClicked = (JButton) e.getSource();		
								
						           if(turn == 1){
										btnClicked.setText("O");											
										btnValue = btnClicked.getText();										
										turn = 2;
						           }else{
										btnClicked.setText("X");
										btnValue = btnClicked.getText();
										turn = 1;
						           }
						           
									btnClicked.setEnabled(false);
							}	
					  });
					
					////////////////////////////////
					gameButtons.get(i).setText(btnValue);
					//Get button value into the array
					////////////////////////////////

					
					pContent.add(gameButtons.get(i));
				
				}
				

				
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    private static boolean displayLoginMenu(){
    	
    	boolean statue = true;
    	
        final JFrame frame = new JFrame("Login Form");

	        JPanel pLabel = new JPanel(new GridLayout(4, 2));
	        JPanel pTextfield = new JPanel(new GridLayout(1, 2));
	        frame.add(pLabel, BorderLayout.WEST);
	        frame.add(pTextfield, BorderLayout.CENTER);       
	        
			for (int i = 0; i < 2 ; i++){
				JButton btnInitialize= new JButton("");
				loginButtons.add(btnInitialize);
			}
			
			loginButtons.get(0).setText("Login");
			loginButtons.get(1).setText("Cancel");			
	 
	        JLabel lblUser = new JLabel("   User ID: ");   
	        JLabel lblPsw = new JLabel("   Password: ");   
	        
			for (int i = 0; i < 2 ; i++){
				TextField txtInitialize = new TextField("", 9);
				txtFields.add(i, txtInitialize);
			}
	        
	        lblUser.setLabelFor(txtFields.get(0));
	        lblPsw.setLabelFor(txtFields.get(1));
	        
	        frame.add(loginButtons.get(0), BorderLayout.SOUTH);
	        
	        loginButtons.get(0).addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent e)
					{
						////////////////////
						//somewhere in here we need to only activate the ability to make the game work after lines 320 or 329 execute!!
						while(!game.login(txtFields.get(0).getText(), txtFields.get(1).getText())){}
						if(JOptionPane.showConfirmDialog(null, "Do you want to play against the computer?"
								+ " Click no if Player 2 is a human player.") == 0){
							game.login("CPU", txtFields.get(1).getText());
							JOptionPane.showMessageDialog(null, "Logged in successfully!");
						}
						else{
							String un = JOptionPane.showInputDialog(null, "Player 2's username: ");
							String pwd = JOptionPane.showInputDialog(null, "Player 2's password: ");
							while(!game.login(un, pwd)){
								un = JOptionPane.showInputDialog(null, "Player 2's username: ");
								pwd = JOptionPane.showInputDialog(null, "Player 2's password: ");
							}
							JOptionPane.showMessageDialog(null, "Logged in successfully!");
						}
					}	
			  });
		        
	        frame.add(loginButtons.get(1), BorderLayout.SOUTH);
	        
	        loginButtons.get(1).addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent e)
					{
						frame.dispose();
					}	
			  });
	        
		    JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    pLabel.add(lblUser);
		    pLabel.add(lblPsw);
		    pForm.add(txtFields.get(0));          
		    pForm.add(txtFields.get(1));
		    pForm.add(loginButtons.get(0));
		    pForm.add(loginButtons.get(1));
		    pTextfield.add(pForm);           
     
        frame.setSize(200, 150);
        frame.setVisible(true);
        frame.setResizable(false);
        
		return statue;
    	
    }
    
    private static void displayRegisterMenu() {
    	
    	String strUser = null;
    	String strPsw = null;
    	
        final JFrame frame = new JFrame("Register Form");

	        JPanel pLabel = new JPanel(new GridLayout(4, 2));
	        JPanel pTextfield = new JPanel(new GridLayout(1, 2));
	        frame.add(pLabel, BorderLayout.WEST);
	        frame.add(pTextfield, BorderLayout.CENTER);    	        	        
			
			for (int i = 0; i < 2 ; i++){
				JButton btnInitialize= new JButton("");
				registerButtons.add(btnInitialize);
			}
			
			registerButtons.get(0).setText("Register");
			registerButtons.get(1).setText("Cancel");	
	 
	        JLabel lblUser = new JLabel("   User ID: ");   
	        JLabel lblPsw = new JLabel("   Password: ");   
	
			for (int i = 0; i < 2 ; i++){
				TextField txtInitialize = new TextField("", 9);
				txtFields.add(i, txtInitialize);
			}
	        
	        lblUser.setLabelFor(txtFields.get(0));
	        lblPsw.setLabelFor(txtFields.get(1));
	        
	        frame.add(registerButtons.get(0), BorderLayout.SOUTH);
	        
	        registerButtons.get(0).addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent e)
					{
						////////////////////////////////
						JOptionPane.showMessageDialog(null, "Register successfully!");
						//Save values to another method, then popup the message dialog
						////////////////////////////////
					}	
			  });

	        frame.add(registerButtons.get(1), BorderLayout.SOUTH);
	        
	        registerButtons.get(1).addActionListener(new ActionListener(){
					
					public void actionPerformed(ActionEvent e)
					{
						frame.dispose();
					}	
			  });	        
	        
		    JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    pLabel.add(lblUser);
		    pLabel.add(lblPsw);
		    pForm.add(txtFields.get(0));          
		    pForm.add(txtFields.get(1));
		    pForm.add(registerButtons.get(0));
		    pForm.add(registerButtons.get(1));
		    pTextfield.add(pForm);           
     
        frame.setSize(200, 150);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    private static void displayScoreboard(){
    	
        JFrame frame = new JFrame("Score Board");

        	////////////////////////////////
	        JPanel lblPlayer = new JPanel(new GridLayout(3, 1));
	        JPanel lblScore = new JPanel(new GridLayout(3, 1));
	        //Get row number to instead the number 3
	        ////////////////////////////////
	        
	        frame.add(lblPlayer, BorderLayout.WEST);
	        frame.add(lblScore, BorderLayout.CENTER);        
	 
	        ////////////////////////////////
	        JLabel lblUser = new JLabel("   " + "Player" + ":  ");   
	        JLabel lblPsw = new JLabel("Score");   
	        //Use loop to load data
	        //Put player name in the Player and score value in the Score
	        ////////////////////////////////
	        
	        lblPlayer.add(lblUser);
	        lblScore.add(lblPsw);
     
        frame.setSize(200, 300);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private static void congratulateWinner(){
		JOptionPane.showMessageDialog(null, "Congratulations!");
	}

	//Do not remove this actionPerformed method that it's the default action code of GUI
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
////////////////////////////////
	public static void main(String[] args) {	
			displayGame();   
	}
////////////////////////////////
	
}