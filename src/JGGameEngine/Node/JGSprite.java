package JGGameEngine.Node;


import java.awt.Graphics;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;


public class JGSprite extends JGTextureNode {
	
	protected String fileName;
	protected JGPoint sourcePosition = new JGPoint();
	protected JGSize sourceSize = new JGSize();
	protected boolean isPartialImage;
	
	//////////////////////////
	//Constructor Methods	//
	//////////////////////////
	
	public JGSprite(String FileName){
		super(); //use default constructor
		fileName = FileName;
		this.LoadTexture(fileName); //load the texture
		isPartialImage = false;
	}
	
	public JGSprite(String FileName, JGPoint sPosition, JGSize sSize){
		super(); //use default constructor
		fileName = FileName;
		this.LoadTexture(fileName); //load the texture
		sourcePosition = sPosition;
		sourceSize = sSize;
		isPartialImage = true;
		setContentSize(sSize);
	}
	
	public JGSprite(JGSprite sprite){
		super();
		fileName = sprite.getFileName();
		this.LoadTexture(fileName);
		setSourcePosition(sprite.getSourcePosition());
		setSourceSize(sprite.getSourceSize());
		setContentSize(sprite.getContentSize());
	}
	
	//////////////////////
	//Override Methods	//
	//////////////////////
	
	@Override
	protected void midDraw(float dt, Graphics g){

		if (getGameWindow() != null){
			if (isPartialImage){
				JGGraphics.drawSprite(this, sourcePosition, sourceSize, g);
			}else{
				JGGraphics.drawSprite(this, g);
			}
		}
		
	}
	
	//////////////////////
	//Private Methods	//
	//////////////////////
	
	private void updateIsPartialImage(){
		if ((sourceSize.width > 0 && sourceSize.height > 0)){
			isPartialImage =  true;
			return;
		}
		isPartialImage = false;
	}
	
	//////////////////////
	//Setters / Getters	//
	//////////////////////
	
	public void setSourcePosition(JGPoint sPo){
		sourcePosition = sPo;
	}
	
	public void setSourceSize(JGSize sSize){
		sourceSize = sSize;
		updateIsPartialImage();
	}
	
	public String getFileName(){ return fileName; }
	public JGPoint getSourcePosition(){ return sourcePosition; }
	public JGSize getSourceSize(){ return sourceSize; }
	
	
}
