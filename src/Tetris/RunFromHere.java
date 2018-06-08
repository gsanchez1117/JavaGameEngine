package Tetris;

import java.awt.EventQueue;

import javax.swing.JFrame;

import JGGameEngine.JGGame;
import JGGameEngine.JGGameEngineMain;
import Tetris.Scenes.TMainMenu;

public class RunFromHere {

	//////////
	//Main	//
	//////////

	public static void main(String[] args) {
			    
		EventQueue.invokeLater(new Runnable() {
			@Override
		    public void run() { 
				
				//Create the Game Window
				JFrame ex = new JGGameEngineMain();
		        ex.setVisible(true); 
		        
		        //set the root node
				JGGame.sharedInstance().replaceRootNode(new TMainMenu());

		    }
		});
		
	}
	
}
