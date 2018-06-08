package Tetris.Button;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.JGSize;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;
import JGGameEngine.Sound.JGSimpleAudioEngine;
import Tetris.TDefaults;

public class TButton extends JGNode {
	
	public TButtonDelegate delegate;
	private static Random random = new Random();
	private Color normalColor;
	private Color pressedColor; 
	private JGTextNode titleText;

	public TButton(JGSize size, String Title){
		setContentSize(size);
		
		normalColor = getRandomColor();
		pressedColor = getRandomColor();
		while (normalColor == pressedColor){
			pressedColor = getRandomColor();
		}
		fillColor = normalColor;
		strokeColor = normalColor;
		
		titleText = new JGTextNode(Title);
		titleText.setFontFromFile("src/Resources/FONTS/fontawesome.ttf");
		titleText.setFontSize(25);
		titleText.setPosition(new JGPoint(getContentSize().width/2, getContentSize().height/2 - titleText.getContentSize().height/4));
		addChild(titleText);
		
		recievesMouseInput = true;
		swallowsMouseInput = true;
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	public void setTitle(String Title){
		titleText.setText(Title);
	}
	
	public void setFont(Font font){
		titleText.setFont(font);;
	}
	
	public void setFontFromFile(String file){
		titleText.setFontFromFile(file);
	}
	
	public void setFontSize(int size){
		titleText.setFontSize(size);
	}
		
	////////////////////
	//Override Methods//
	////////////////////
	
	@Override
	public void midDraw(float dt, Graphics g){
		
		int width = (int)getContentSize().width;
		int height = (int)getContentSize().height;
		
		JGGraphics.drawNodeRoundedRect(
				this, 
				new JGRect(0, 0, width, height), 
				10, 
				strokeColor, 
				fillColor, 
				g
		);
				
	}
	
	@Override
	public void mousePressBegan(JGPoint p){
		fillColor = pressedColor;
		strokeColor = pressedColor;
		JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TButton_Pressed.wav");
		
		//notify the delegate
		if (delegate != null){
			delegate.pressed(this);
		}
	}
	
	@Override
	public void mousePressEnded(JGPoint p){
		fillColor = normalColor;
		strokeColor = normalColor;
		JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TButton_Released.wav");
		
		//notify the delegate
		if (delegate != null){
			delegate.released(this);
		}
	}
	
	////////////////////
	//Get random color//
	////////////////////
	
	private Color getRandomColor(){
		return TDefaults.sharedInstance().getShapeColors()[random.nextInt(TDefaults.sharedInstance().getShapeColors().length-1)];
	}
	
}
