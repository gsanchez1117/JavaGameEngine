package Tetris.DisplayBox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;
import Tetris.TDefaults;
import Tetris.Block.TShape;

public class TNextBox extends JGNode {

	private JGTextNode text = new JGTextNode("Next");
	TShape shape = null;
	
	public TNextBox(){
		try{
			Font f = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("Resources/FONTS/fullPack2025.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(f);
			text.setFont(f);
			text.setFontSize(30);
		}catch(Exception e){
			System.out.println("problesms");
		}
		addChild(text);
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	public void updateShape(TShape Shape){
		if (shape != null){
			removeChild(shape);
			shape = null;
		}
		shape = new TShape(Shape.getType());
		if (Shape.getType() == 0){
			shape.setPosition(new JGPoint(-TDefaults.sharedInstance().getBlockSize(),TDefaults.sharedInstance().getBlockSize()*2));
		}else if (Shape.getType() == 5){
			shape.setPosition(new JGPoint(-TDefaults.sharedInstance().getBlockSize(),TDefaults.sharedInstance().getBlockSize()*2.5f));
		}else{
			shape.setPosition(new JGPoint(-TDefaults.sharedInstance().getBlockSize()/2,TDefaults.sharedInstance().getBlockSize()*2));
		}
		addChild(shape);
	}
	
	////////////////////
	//Override Methods//
	////////////////////
	
	@Override
	public void midDraw(float dt, Graphics g){
		int width = 140;
		Color col = new Color(1,1,1,0.1f);
		JGGraphics.drawNodeRoundedRect(this, new JGRect(-width/2,25,width,80), 10, col, col, g);
	}
	
}
