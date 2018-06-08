package Tetris.Block;


import JGGameEngine.JGPoint;
import Tetris.Board.TBoard;

public class TGhostShape extends TShape {
	
	///////////////
	//Constructor//
	///////////////
	
	public TGhostShape(){
		super(0);
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	public void updatePosition(TBoard board, TBlock[][] grid, TShape shape){
		for (int i = 0; i < getBlocks().length; i++){
			getBlocks()[i].isOutline = true;
			getBlocks()[i].fillColor = shape.fillColor;
			getBlocks()[i].setPosition(shape.getBlocks()[i].getPosition());
		}
		
		JGPoint desiredPos = board.positionToCoord(shape.getPosition());
		while(board.isMoveLegal(this, desiredPos)){
			setPosition(board.coordToPosition(desiredPos));
			desiredPos = desiredPos.add(new JGPoint(0,1));
		}
		
	}
		
}
