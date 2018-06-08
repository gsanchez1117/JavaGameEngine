package Tetris.Block;

import JGGameEngine.JGPoint;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Sound.JGSimpleAudioEngine;
import Tetris.TDefaults;
import Tetris.Board.TBoard;
import Tetris.Timer.TActionTimer;

public class TShape extends JGNode {
			
	public TShapeDelegate delegate;
	private TBlock[] blocks = new TBlock[4]; // all shapes have 4 blocks
	private int rot = 0;
	private int type;
	private boolean isSticking = false;
	private TActionTimer stickTimer = new TActionTimer(0.5f);
	
	///////////////
	//Constructor//	
	///////////////
		
	public TShape(int Type){
		type = Type % 7;		
		fillColor = TDefaults.sharedInstance().getShapeColors()[type];
		buildBlocks();
	}
	
	////////////////////
	//Override Methods//
	////////////////////
	
	@Override
	public void Update(float dt){
		super.Update(dt);
		
		//
		//Stick Timer
		//
		if (isSticking){
			stickTimer.update(dt);
			float delta = (stickTimer.getCurrTime() / stickTimer.getTotalTime()) * 180;
			setOpacity((float)Math.abs(Math.cos(Math.toRadians(delta))));
			if (stickTimer.isFinished()){
				stopStick();
				if (delegate != null)
					delegate.shapeStuck(this);
			}
		}else{
			setOpacity(1.0f);
			stickTimer.reset();
			stickTimer.start();
		}
		
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	public void startStick(){
		isSticking = true;
	}
	
	public void stopStick(){
		isSticking = false;
	}
	
	public boolean rotate(TBoard board){
		int desiredRot = (rot + 1) % 4;
		JGPoint[] blockPos = getRotationPositions(desiredRot);
		
		JGPoint[][] shapeKicks = TDefaults.sharedInstance().getShapeKicks();
		for(int i = 0; i < shapeKicks[type].length; i++){
			JGPoint shapeCoord = board.positionToCoord(getPosition()).add(shapeKicks[type][i]);
			if (board.isRotationLegal(blockPos, shapeCoord)){
				updateBlockPositions(blockPos);
				setPosition(board.coordToPosition(shapeCoord));
				JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TRotate.wav");
				rot = desiredRot;
				return true;
			}
		}
		return false;
	}
		
	
	///////////////////
	//Private Methods//
	///////////////////
	
	private void buildBlocks(){
		for (int i = 0; i < blocks.length; i++){
			TBlock block = new TBlock(fillColor);
			addChild(block);
			blocks[i] = block;
		}
		
		JGPoint[] blockPos = getRotationPositions(rot);
		updateBlockPositions(blockPos);	
	}
	
	private JGPoint[] getRotationPositions(int Rot){
		Rot = Rot % 7;
		int blockNum = 0;
		int size = TDefaults.sharedInstance().getBlockSize();
		int[][][][] rots = TDefaults.sharedInstance().getRotations();
		JGPoint [] blockPos = new JGPoint[4];
		for (int y = 0; y < rots[type][Rot].length; y++){
			for (int x = 0; x < rots[type][Rot][y].length; x++){
				if (rots[type][Rot][y][x] == 1){
					blockPos[blockNum] = new JGPoint((x - 1) * size, (y - 2) * size);
					blockNum++;
				}
			}
		}
		return blockPos;
	}
	
	private void updateBlockPositions(JGPoint[] positions){
		for (int i = 0; i < blocks.length; i++){
			blocks[i].setPosition(positions[i]);
		}
	}
		
	///////////////////
	//Getters/Setters//
	///////////////////
	
	public TBlock[] getBlocks(){ return blocks; }
	public int getType(){ return type; }

}
