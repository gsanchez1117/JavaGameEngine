package Tetris.Block;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.Node.JGNode;
import Tetris.TDefaults;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGRect;

public class TBlock extends JGNode {
	
	public boolean isOutline = false;
	
	public TBlock(Color col){
		fillColor = col;
		strokeColor = col;
	}
	
	@Override
	public void midDraw(float dt, Graphics g){
		
		int blockSize = (int)(TDefaults.sharedInstance().getBlockSize() * 0.875);
		int offset = (int)((TDefaults.sharedInstance().getBlockSize() - blockSize)/2);
		
		if (isOutline){
			JGGraphics.drawNodeRoundedRect(
					this, 
					new JGRect(offset, offset, blockSize, blockSize), 
					10, 
					fillColor, 
					new Color(0,0,0,0), 
					g
			);
		}else{
			JGGraphics.drawNodeRoundedRect(
					this, 
					new JGRect(offset, offset, blockSize, blockSize), 
					10, 
					strokeColor, 
					fillColor, 
					g
			);
		}
	}
				
}
