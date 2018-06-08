package Tetris.Scenes;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.JGGame;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;
import JGGameEngine.Node.JGTextNode.JGTextHorizontalAlignment;
import Tetris.TStarField;
import Tetris.Button.TButton;
import Tetris.Button.TButtonDelegate;

public class TCredits extends JGNode implements TButtonDelegate {

	TStarField starField;
	TButton backButton = new TButton(new JGSize(50,50), "");
	JGTextNode creditsText = new JGTextNode("Tetris\nby\nGabriel Sanchez\n\n\nMusic\n\n“Good-Morning-Doctor-Weird”\nby\nEric Matyas\nwww.soundimage.org\n\n\"Tetris Theme\"\nby\nAnaklosmos\nwww.newgrounds.com\n\nFonts\n\n\"Font Awesome\"\nby\nfontawesome.io");
	
	public TCredits(){
		fillColor = new Color(25, 35, 50, 255);
		
		starField = new TStarField(JGGame.sharedInstance().getWidth(), JGGame.sharedInstance().getHeight());
		starField.setSpeed(0.1f);
		starField.setStars(200);
		starField.setZOrder(-1);
		addChild(starField);
		
		
		backButton.setPosition(new JGPoint(backButton.getContentSize().width*.6, JGGame.sharedInstance().getHeight() - backButton.getContentSize().height*.6));
		backButton.delegate = this;
		addChild(backButton);
		
		creditsText.setHorizontalAlignment(JGTextHorizontalAlignment.CENTER);
		creditsText.setFontSize(20);
		creditsText.setPosition(new JGPoint(JGGame.sharedInstance().getWidth()/2 , 300));
		addChild(creditsText);
	}
	
	@Override
	public void midDraw(float dt, Graphics g){
		
		JGGraphics.fillNodeRect(this, new JGPoint(), this.getContentSize(), g);
		JGGraphics.fillNodeGradientRect(this, new JGPoint(), this.getContentSize(), fillColor, Color.BLACK, new JGPoint(0,300), new JGPoint(0,this.getContentSize().height), g);
		
	}
	
	////////////////////////////
	//TButton Delegate Methods//
	////////////////////////////

	@Override
	public void pressed(TButton button) {		
	}

	@Override
	public void released(TButton button) {
		JGGame.sharedInstance().replaceRootNode(new TMainMenu());
	}
	
}
