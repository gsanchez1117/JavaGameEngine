package Tetris.Scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


import JGGameEngine.JGGame;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;
import JGGameEngine.Sound.JGSimpleAudioEngine;
import Tetris.TStarField;
import Tetris.Button.TButton;
import Tetris.Button.TButtonDelegate;

public class TMainMenu extends JGNode implements TButtonDelegate {
		
	TStarField starField;
	JGTextNode titleText = new JGTextNode("Tetris");
	JGTextNode versionText = new JGTextNode("Version 1.0.0" + " © " + "2017 Gabriel Sanchez");
	TButton playButton = new TButton(new JGSize(100,50), "Play");
	TButton creditsButton = new TButton(new JGSize(100,50), "Credits");

	public TMainMenu(){
		fillColor = new Color(25, 35, 50, 255);

		starField = new TStarField(JGGame.sharedInstance().getWidth(), JGGame.sharedInstance().getHeight());
		starField.setSpeed(0.1f);
		starField.setStars(200);
		starField.setZOrder(-1);
		addChild(starField);
		
		titleText.setFontFromFile("src/Resources/FONTS/fullPack2025.ttf");
		titleText.setFontSize(50);
		titleText.setPosition(new JGPoint(JGGame.sharedInstance().getWidth()/2 , 150));
		addChild(titleText);
		
		versionText.setFontSize(12);
		versionText.setPosition(new JGPoint(JGGame.sharedInstance().getWidth()/2 , JGGame.sharedInstance().getHeight() - versionText.getContentSize().height));
		addChild(versionText);
		
		playButton.setFont(new Font("Arial", Font.PLAIN, 25));
		playButton.setPosition(new JGPoint(JGGame.sharedInstance().getWidth()/2, 300));
		playButton.delegate = this;
		addChild(playButton);
				
		creditsButton.setFont(new Font("Arial", Font.PLAIN, 25));
		creditsButton.setPosition(new JGPoint(JGGame.sharedInstance().getWidth()/2, 375));
		creditsButton.delegate = this;
		addChild(creditsButton);
		
		JGSimpleAudioEngine.SharedInstance().playMusic("src/Resources/SFX/Good-Morning-Doctor-Weird.wav", true);
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

		//play button pressed
		if (button == playButton){
			JGSimpleAudioEngine.SharedInstance().stopMusic("src/Resources/SFX/Good-Morning-Doctor-Weird.wav");
			JGGame.sharedInstance().replaceRootNode(new TGamePlay());
		}
				
		//credits button pressed
		if (button == creditsButton){
			JGGame.sharedInstance().replaceRootNode(new TCredits());
		}
		
	}

}
