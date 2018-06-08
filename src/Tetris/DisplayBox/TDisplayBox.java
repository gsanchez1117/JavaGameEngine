package Tetris.DisplayBox;

import JGGameEngine.JGPoint;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;

public class TDisplayBox extends JGNode {

	private JGTextNode titleText = new JGTextNode();
	private JGTextNode contentText = new JGTextNode();

	
	public TDisplayBox(String title){

		titleText.setFontFromFile("src/Resources/FONTS/fullPack2025.ttf");
		titleText.setFontSize(30);
		titleText.setText(title);
		addChild(titleText);

		contentText.setFontFromFile("src/Resources/FONTS/fullPack2025.ttf");
		contentText.setFontSize(20);
		contentText.setPosition(new JGPoint(0, 50));
		addChild(contentText);
	}
	
	//////////////////
	//Public Methods//
	//////////////////
		
	public void setTitle(String title){
		titleText.setText(title);
	}
	
	public void setContentText(String text){
		contentText.setText(text);
	}
	
}
