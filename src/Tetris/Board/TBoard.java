package Tetris.Board;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Sound.JGSimpleAudioEngine;
import Tetris.TDefaults;
import Tetris.Block.TBlock;
import Tetris.Block.TGhostShape;
import Tetris.Block.TShape;
import Tetris.Block.TShapeDelegate;
import Tetris.Timer.TActionTimer;
import Tetris.Timer.TCountDownTimer;
import Tetris.Timer.TCountDownTimerDelegate;

//Level		Drop speed(second)
//00		0.8
//01		0.72
//02		0.63
//03		0.55
//04		0.47
//05		0.38
//06		0.3
//07		0.22
//08		0.13
//09		0.1
//10-12		0.08
//13-15		0.07
//16-18		0.05
//19-28		0.03
//29+		0.02

//level increases every 10 lines

///////////////////////////////////////
//Scoring							 //
//-----------------------------------//
//Single	|	40*(1+MIN(level, 9)) //
//Double	|  100*(1+MIN(level, 9)) //
//Triple	|  300*(1+MIN(level, 9)) //
//Tetris	| 1200*(1+MIN(level, 9)) //
///////////////////////////////////////


public class TBoard extends JGNode implements TShapeDelegate, TCountDownTimerDelegate {
	
	public TBoardDelegate delegate = null;
	
	private final int width = TDefaults.sharedInstance().getWidth();
	private final int height = TDefaults.sharedInstance().getHeight();
	private final int blockSize = TDefaults.sharedInstance().getBlockSize(); 
	
	private TBlock[][] blocks = new TBlock[height][width];
	private TShape nextBlock;
	private TShape currPiece = null;
	private TGhostShape ghostShape = new TGhostShape();
	private int score = 0;
	private int level = 0;
	private int lines = 0;
	private TActionTimer dropTimer = new TActionTimer(0.8f);
	private TActionTimer moveDelayTimer = new TActionTimer(0.05f);
	private TCountDownTimer countDownTimer = new TCountDownTimer(4.0f);
	private boolean isGameRunning = false;
	private boolean isGameOver = false;
	
	///////////////
	//Constructor//
	///////////////
	
	public TBoard(){
		addChild(countDownTimer);
		countDownTimer.setZOrder(2);
		countDownTimer.start();
		countDownTimer.delegate = this;
		countDownTimer.setPosition(new JGPoint(width*blockSize/2, height*blockSize/2));
		addChild(ghostShape);
	}
		
	//////////////////
	//Public Methods//
	//////////////////
	
	public boolean pauseGame(){
		if (!isGameRunning){ return false; }
		isGameRunning = false;
		countDownTimer.setText("Paused");
		countDownTimer.isVisible = true;
		return true;
	}
	
	public boolean resumeGame(){
		if (isGameRunning){ return false; }
		if (countDownTimer.getIsRunning() && !countDownTimer.getIsFinished()){ return false; }
		countDownTimer.reset();
		countDownTimer.start();
		return true;
	}
	
	public void movePieceLeft(){
		if (!isGameRunning){ return; }

		if (moveDelayTimer.isFinished()){
			moveDelayTimer.reset();
			
			JGPoint coord = positionToCoord(currPiece.getPosition());
			JGPoint desiredCoord = coord.sub(new JGPoint(1, 0));
			if (this.isMoveLegal(currPiece, desiredCoord)){
				currPiece.stopStick();
				currPiece.setPosition(coordToPosition(desiredCoord));
				JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TMoveLR.wav");
			}
		}
	}
	
	public void movePieceRight(){
		if (!isGameRunning){ return; }

		if (moveDelayTimer.isFinished()){
			moveDelayTimer.reset();
			
			JGPoint coord = this.positionToCoord(currPiece.getPosition());
			JGPoint desiredCoord = coord.add(new JGPoint(1, 0));
			if (this.isMoveLegal(currPiece, desiredCoord)){
				currPiece.stopStick();
				currPiece.setPosition(coordToPosition(desiredCoord));
				JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TMoveLR.wav");
			}
		}
	}
	
	public void rotatePiece(){
		if (!isGameRunning){ return; }

		if (moveDelayTimer.isFinished()){
			if (currPiece.rotate(this)){
				moveDelayTimer.reset();
				currPiece.stopStick();
			}
		}
	}
	
	public void dropPiece(){
		if (!isGameRunning){ return; }

		dropPiece(true);
	}
	
	public TShape getNext(){
		if (nextBlock == null){
			Random random = new Random();
			int num = random.nextInt(7);
			nextBlock = new TShape(num);
			addChild(nextBlock);
		}
		return nextBlock;
	}
	
	public JGPoint coordToPosition(JGPoint coord){
		return coord.mult(new JGPoint(blockSize, blockSize));
	}
	
	public JGPoint positionToCoord(JGPoint pos){
		return new JGPoint((int)(pos.x / blockSize), (int)(pos.y / blockSize));
	}
	
	public boolean isRotationLegal(JGPoint[] blockPos, JGPoint shapeCoord){
		for (int i = 0; i < blockPos.length; i++){
			JGPoint blockPo = shapeCoord.add(positionToCoord(blockPos[i]));
			
			//check left right bounds
			if (blockPo.x < 0 || blockPo.x >= blocks[0].length)
				return false;
			
			//check top bottom boudns
			if (blockPo.y < 0 || blockPo.y >= blocks.length)
				return false;
			
			if (blocks[(int)(blockPo.y)][(int)(blockPo.x)] != null)
				return false;
		}
		return true;
	}
	
	public boolean isMoveLegal(TShape piece, JGPoint nextCoord){
		TBlock[] pBlocks = piece.getBlocks();
		for (int i = 0; i < pBlocks.length; i++){
			TBlock block = pBlocks[i];
			JGPoint coord = positionToCoord(block.getPosition());
			JGPoint blockCoord = nextCoord.add(coord);
			
			//collision with sides of the board
			if (blockCoord.x < 0 || blockCoord.x >= blocks[0].length)
				return false;
			
			//collision with bottom of the board
			if (blockCoord.y >= blocks.length)
				return false;
			
			//collision with another block
			if (blocks[(int)blockCoord.y][(int)blockCoord.x] != null)
				return false;
			
		}
		return true;
	}
	
	////////////////////
	//Override Methods//
	////////////////////
	
	@Override
	public void Update(float dt){
		super.Update(dt);
				
		//
		//game is running
		//
		if (isGameRunning){
			
			//input delay
			if (!moveDelayTimer.getIsRunning()){
				moveDelayTimer.start();
			}
			moveDelayTimer.update(dt);		
			
			//drop controls
			if (!dropTimer.getIsRunning())
				dropTimer.start();
			
			dropTimer.update(dt);
			
			if (dropTimer.isFinished()){
				dropTimer.reset();
				dropPiece(false);
			}
			
			//update GhostShape
			ghostShape.updatePosition(this, blocks, currPiece);
			
		}else{
			if (dropTimer.getIsRunning())
				dropTimer.stop();
			
			
			if (!isGameOver){
				//start the count down
				countDownTimer.updateTimer(dt);
			}else{
				fillBoard();
			}
			
		}
	}
	
	@Override
	public void midDraw(float dt, Graphics g){
		
		int offset = (int)((1.0f - 0.875f) * blockSize / 2);
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				JGGraphics.drawNodeRoundedRect(
						this, 
						new JGRect(offset + (int)(x * blockSize), offset + (int)(y * blockSize), (int)(blockSize * 0.875), (int)(blockSize * 0.875)), 
						(int)(blockSize * 0.3125), 
						new Color(200,200,200,25), 
						new Color(200,200,200,25), 
						g
				);
			}
		}
	}
	
	///////////////////
	//Setters/Getters//
	///////////////////
	
	public boolean getGameRunning(){
		return isGameRunning;
	}
		
	///////////////////
	//Private Methods//
	///////////////////
		
	/**
	 * Checks if the game is over. Game over happens whenever a new piece is spawned in overlapping an existing piece.
	 * @return - true if the game is over. false if not.
	 */
	private boolean checkGameOver(){
		
		//check stuck piece above line 3
		for (int i = 0; i < blocks[0].length; i++){
			if (blocks[0][i] != null || blocks[1][i] != null){
				return true;
			}
		}
		
		//Check Overlapping piece
		JGPoint piecePos = currPiece.getPosition();
		for (int i = 0; i < currPiece.getBlocks().length; i++){
			JGPoint blockCoord = positionToCoord(piecePos.add(currPiece.getBlocks()[i].getPosition()));
			if (blocks[(int)blockCoord.y][(int)blockCoord.x] != null){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkLineFull(int y){
		for (int i = 0; i < blocks[y].length; i++)
			if (blocks[y][i] == null)
				return false;
		return true;
	}
	
	private boolean checkLineEmpty(int y){
		for (int i = 0; i < blocks[y].length; i++)
			if (blocks[y][i] != null)
				return false;
		return true;
	}
	
	private void removeLine(int y){
		for (int i = 0; i < blocks[y].length; i++){
			TBlock block = blocks[y][i];
			removeChild(block);
			blocks[y][i] = null;
		}
	}
	
	private void dropLines(){
		for (int y = 0; y < blocks.length; y++){
			if (checkLineEmpty(y)){
				for (int yy = y; yy > 0; yy--){
					for (int x = 0; x < blocks[yy].length; x++){
						blocks[yy][x] = blocks[yy-1][x];
						if (blocks[yy][x] != null){
							JGPoint coord = positionToCoord(blocks[yy][x].getPosition());
							blocks[yy][x].setPosition(coordToPosition(coord.add(new JGPoint(0,1))));
						}
					}
				}
			}
		}
	}
	
	private void incScore(int lines){
		if (lines < 1 || lines > 4){ return; } //invalid numbers of lines
		int adder = 0;
		switch(lines){
			case 1:
				adder = 40;
				break;
			case 2:
				adder = 100;
				break;
			case 3:
				adder = 300;
				break;
			case 4:
				adder = 1200;
				break;
		}
		score += adder * (1+Math.min(level, 9));
		
		//notify delegate of the score change
		if (delegate != null){
			delegate.scoreChanged(score);
		}
	}
	
	private void incLines(){
		lines++;
		if (lines % 10 == 0){ //increase level every 10 lines
			incLevel();
		}
		
		//notify delegate of the lines change
		if (delegate != null){
			delegate.linesChanged(lines);
		}
	}
	
	private void incLevel(){
		level++;
		dropTimer.setTime(0.8f - level*0.08f);
		
		//notify delegate of the level change
		if (delegate != null){
			delegate.levelChanged(level);
		}
		JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/advanceLevel.wav");
	}
	
	private void dropPiece(boolean player){
		JGPoint coord = positionToCoord(currPiece.getPosition());
		JGPoint desiredCoord = coord.add(new JGPoint(0,1));
		if (isMoveLegal(currPiece, desiredCoord)){
			currPiece.setPosition(coordToPosition(desiredCoord));
			if (player)
				JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TDrop.wav");
		}else{
			currPiece.startStick();
		}
	}
	
	private void spawnNext(){
		TShape n = getNext();
		
		//remove old current
		if (currPiece != null){
			removeChild(currPiece);
			currPiece.delegate = null;
			currPiece = null;
		}
		//set new current
		currPiece = n;
		currPiece.delegate = this;
		currPiece.setPosition(coordToPosition(new JGPoint((int)(width/2), 1)));
		
		nextBlock = null;
		
		//notify delegate of the score change
		if (delegate != null){
			delegate.nextShapeChanged(getNext());
		}
		
		//check if the game is over
		if (checkGameOver()){
			isGameRunning = false;
			isGameOver = true;
			countDownTimer.isVisible = true;
			countDownTimer.setText("Game\nOver");
			ghostShape.isVisible = false;
			
			JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TLose.wav");
			
			//notify the delegate
			if (delegate != null){
				delegate.gameOver();
			}
		}
	}
	
	private void fillBoard(){
		for (int y = 0; y < blocks.length; y++){
			for (int x = 0; x < blocks[y].length; x++){
				if (blocks[y][x] != null){
					removeChild(blocks[y][x]);
					blocks[y][x] = null;
					return;
				}
			}
		}
	}
					
	////////////////////
	//Delegate Methods//
	////////////////////

	@Override
	public void shapeStuck(TShape shape) {
		
		spawnNext();
		int[] cleared = new int[4];
		for (int i = 0; i < shape.getBlocks().length; i++){
			TBlock block = shape.getBlocks()[i];
			JGPoint coord = positionToCoord(shape.getPosition());
			JGPoint blockCoord = coord.add(positionToCoord(block.getPosition()));
			blocks[(int)(blockCoord.y)][(int)(blockCoord.x)] = block;
			block.setPosition(this.coordToPosition(blockCoord));
			block.parent.removeChild(block);
			addChild(block);
			
			cleared[i] = checkLineFull((int)blockCoord.y) ? (int)blockCoord.y : -1;
		}
		
		int linesCleared = 0;
		for (int i = 0; i < cleared.length; i++){
			if (cleared[i] != -1){
				linesCleared++;
				removeLine(cleared[i]);
				incLines();
			}
		}
		
		if (linesCleared > 0 && linesCleared <= 1){
			JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/TClearLine.wav");
		}else if (linesCleared > 1 && linesCleared <= 2){
			JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/doubleKill.wav");
		}else if (linesCleared > 2 && linesCleared <= 3){
			JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/trippleKill.wav");
		}else if (linesCleared > 3 && linesCleared <= 4){
			JGSimpleAudioEngine.SharedInstance().playSoundEffect("src/Resources/SFX/dominating.wav");
		}
		
		incScore(linesCleared);
		dropLines();
	}

	@Override
	public void countDownFinished() {
		if (currPiece == null){
			spawnNext();
		}
		isGameRunning = true;
	}	
}
