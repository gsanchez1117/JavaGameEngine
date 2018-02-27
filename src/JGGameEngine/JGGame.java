package JGGameEngine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import JGGameEngine.Node.JGNode;

/**
 * JGGame
 * @author Gabriel Sanchez
 * Description: The JGGame class is the root of all games built upon the JGGameEngine.
 * JGGame subclasses JPanel and controls the update and drawing of the entire game.
 */
public class JGGame extends JPanel implements ActionListener {

	/** Here just to silence the warning */
	private static final long serialVersionUID = 1L; 
	
	/** Singleton instance for the JGGame */
	private static JGGame instance = null;
	
	//////////////////////////
	//Instance Variables	//
	//////////////////////////
	
	/** Holds the width of the game panel */
	private int B_WIDTH = 480;
	
	/** Holds the height of the game panel */
	private int B_HEIGHT = 272;
	
	/** Holds the tick rate for the game in frames per second. */
	private int TICKS_PER_SECOND = 60;
	
	/** Determine if the FPS of the game should be displayed on the screen. */
	private boolean SHOULD_SHOW_FPS = true;
	
	/** A timer used to repeatedly call the update loop. */
    private Timer timer;
    
    /** Holds the current system time. */
    private long systemTime = 0;
    
    /** Holds the old system time. (1 tick behind) */
    private long oldSystemTime = 0;
    
    /** Holds the delta time between ticks. (Time difference between system time and old system time.) */
    private float deltaTime = 0.0f;
    
    /** Holds the debug font used for debug drawing. */
    private Font debugFont = new Font("Arial", Font.PLAIN, 14);
    
    /** Holds the root node of the game. */
    private JGNode rootNode = null;
    
    /** Determines if the game is paused or not. */
    private boolean gamePaused;
        
    //////////////////
    //Class Methods	//
    //////////////////
    
    /**
     * Gets the shared singleton instance of the JGGame
     * @return - a singleton instance of the JGGame.
     */
	public static JGGame sharedInstance(){
		if (JGGame.instance == null){
			JGGame.instance = new JGGame();
		}
		return JGGame.instance;
	}
	
	//////////////////
	//Constructors	//
	//////////////////

	/**
	 * Default Constructor. Creates a new instance of JGGame. 
	 * Note: set to private and is only called once by the SharedInstance method.
	 */
	private JGGame(){
		
		//perform compatibility check
		performCompatibilityCheck();
		
		//read in configuration file
		configureGameEngine();
		
        //configure the window
        configureWindow();
		                        
        //run game engine
        runGameEngine();

	}
	
	//////////////////////
	//Protected Methods	//
	//////////////////////
	
	/**
	 * This is the main update method for the entire game. Through this method the root node of the game is updated
	 * and it in turn updates all child nodes connected to it. 
	 * Note: this method should not be invoked manually.
	 * @param dt - the delta time between frame updates given in milliseconds.
	 */
	protected void Update(float dt){
		
		        
		//only update the root node if:
		//	1: not null
		//	2: alive
		//	3: game is not paused
		if (rootNode!= null && rootNode.getIsAlive() && !gamePaused)
			rootNode.Update(dt);
		
		//release the mouse input block
		JGInputManager.sharedInstance().setMouseInputBlocked(false);
	}
	
	
	/**
	 * This is the main draw method for the entire game. Through this method the root node of the game is drawn
	 * and it in turn draws all child nodes connected to it.
	 * Note: this method should not be invoked manually.
	 * @param dt - the delta time between frame draws given in milliseconds.
	 * @param g - the graphics context being drawn to.
	 */
	protected void Draw(float dt, Graphics g){
		
		//draw the root node
		if (rootNode != null && rootNode.getIsAlive())
			rootNode.Draw(dt, g);


		if (SHOULD_SHOW_FPS && dt > 0){
	        String msg = "FPS:" + (int)(1/dt);
	        

	        g.setColor(Color.white);
	        g.setFont(debugFont);
	        g.drawString(msg, 0, B_HEIGHT);
		}
	}

	//////////////////////
	//Override Methods	//
	//////////////////////
	
	/**
	 * Custom Override of the actionPerformed method.
	 * This method is fired continually based on the java swing timer. The TICKS_PER_SECOND variable controls how often this method is fired.
	 * The body of this method is used to determine the approximate time between method calls and to subsequently call the update and repaint methods.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		oldSystemTime = systemTime;
		systemTime = System.currentTimeMillis();
				
		//the difference in time between updates/ticks in milliseconds
		float timeDif = systemTime - oldSystemTime;
				
		//scale down to delta time represented by a value between 0 and 1
		deltaTime = timeDif / 1000.0f;
		
		//don't update the game if the old system time is 0 (i.e. first iteration)
		if (oldSystemTime != 0){

			//UPDATE GAME ENGINE
			Update(deltaTime);
	
			//DRAW GAME ENGINE
			repaint();
		}
		
	}
	
	/**
	 * Custom override of the paintComponent method. This method is called by the actionPerformed method. 
	 * Every tick of the game engine the actionPerofrmed method asks the window to repaint itself in turn calling the Draw method 
	 * for the game engine.
	 */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //call the draw method using the current graphics context
        Draw(deltaTime, g);
    }
    
    //////////////////////
    //Public Methods	//
    //////////////////////
        
    /**
     * Replaces the root node of the game with the passed in node.
     * @param aNode - the node to replace the root node with.
     */
    public void replaceRootNode(JGNode aNode){
    	if (aNode == null){ return; }				//do not replace with a null node.
    	
    	rootNode = aNode;
		rootNode.setAnchorPoint(new JGPoint(0, 0));
		rootNode.setContentSize(new JGSize(getWidth(), getHeight()));
	}
    
    //////////////////////////
    //Getters and Setters	//
    //////////////////////////
    
    /**
     * Gets whether the game is paused or not.
     * @return - true if the game is paused. false if not.
     */
    public boolean getGamePaused(){ return gamePaused; }
    
    /**
     * Sets the games paused state.
     * @param p - a boolean that determines if the game is paused or not. 
     */
    public void setGamePaused(boolean p){
    	gamePaused = p;
    }
	
	///////////////////
	//Private Methods//
	///////////////////
	
	private void configureGameEngine(){
		
		Scanner scan;
		
		try{
			InputStream cfg = getClass().getResourceAsStream("JGConfig.txt");
			if (cfg == null){
				System.out.println("Couldn't load config file");
			}
			
			scan = new Scanner(cfg);
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				
				//skip comments
				if (line.length() > 0 && line.charAt(0) == '#'){continue;}
				
				//check for valid commands
				Scanner parser = new Scanner(line);
				if (parser.hasNext()){
					String cmd = parser.next();
					if (parser.hasNext()){
						String value = parser.next();
						switch(cmd){
						case "B_WIDTH":
							B_WIDTH = Integer.parseInt(value);
							break;
						case "B_HEIGHT":
							B_HEIGHT = Integer.parseInt(value);
							break;
						case "FPS":
							TICKS_PER_SECOND = Integer.parseInt(value);
							break;
						case "SHOW_FPS":
							SHOULD_SHOW_FPS = Boolean.parseBoolean(value);
							break;
						}
					}
				}	
				parser.close();
			}
			scan.close();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	private void configureWindow(){
		
		//add input listeners
		addKeyListener(JGInputManager.sharedInstance());
        addMouseListener(JGInputManager.sharedInstance());
        addMouseMotionListener(JGInputManager.sharedInstance());
		
        //set the background color of the panel
		setBackground(Color.black);
		
		//allow the panel to receive focus.
        setFocusable(true);
        
        //set the size of the panel.
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        //the game is not paused initially
        gamePaused = false;
	}
		
	private void runGameEngine(){
        timer = new Timer(1000 / TICKS_PER_SECOND, this);
        timer.start();
	}
	
	private void performCompatibilityCheck(){
		
		//get the OS name and version
		String osName = System.getProperty("os.name").toLowerCase();
		String osVersion = System.getProperty("os.version");
		
		//exit if we can't perform check
		if (osName == null || osVersion == null){ displayErrorMessageToUser("Could Not Perform compatibility check. exiting!"); }
		
		//We only need to worry about the MAC operating system
		if (osName.startsWith("mac")){
			try{
				
				//check if Accent Menu is enabled
				Process np = Runtime.getRuntime().exec("defaults read -g ApplePressAndHoldEnabled");
				np.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(np.getInputStream()));
				String enabled = reader.readLine();
				
				//return and run the App if the OS doesn't support accent menu
				if (enabled == null){ return; }
				
				if (enabled.equals("1")){ //if accent menu is enabled
					//ask the user if they want to disable it
					if (JOptionPane.showConfirmDialog(null, 
							"This application requires the \"Character Accent Menu\" be disabled to function properly.\nDo you want us to disable it for you?", 
							"MAC WARNING", 
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
						
						//if they select yes then disable it and return
						Process p = Runtime.getRuntime().exec("defaults write -g ApplePressAndHoldEnabled -bool false");
						p.waitFor();
						return;
						
					}else{ 
						//if they select no then exit the app
						System.exit(0);
					}
				}
			}catch(Exception e){
				displayErrorMessageToUser("Could Not Perform compatibility check. exiting!");
			}		
		}
	}
	
	/**
	 * Displays a pop up error message to the user 
	 * @param message - the message to display to the user.
	 */
	private void displayErrorMessageToUser(String message){
		JOptionPane.showMessageDialog(null, message, "Warning!", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
					
}
