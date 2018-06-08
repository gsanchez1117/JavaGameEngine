package Tetris.Scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Sound.JGSimpleAudioEngine;
import Tetris.TDefaults;
import Tetris.TStarField;
import Tetris.Block.TShape;
import Tetris.Board.TBoard;
import Tetris.Board.TBoardDelegate;
import Tetris.Button.TButton;
import Tetris.Button.TButtonDelegate;
import Tetris.DisplayBox.TDisplayBox;
import Tetris.DisplayBox.TNextBox;
import Tetris.Timer.TActionTimer;
import JGGameEngine.JGGame;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGInputManager;

public class TGamePlay extends JGNode implements TBoardDelegate, TButtonDelegate {
	
	TBoard board;
	TNextBox nextBox = new TNextBox();
	TDisplayBox scoreBox;
	TDisplayBox levelBox;
	TDisplayBox linesBox;
	private TActionTimer moveDelayTimer = new TActionTimer(0.005f);
	boolean oldLeft, oldRight, oldUp;
	TStarField starField;
	TButton pauseButton;
	TButton restartButton;
	TButton mainMenuButton;

	
	public TGamePlay(){
		
		fillColor = new Color(25, 35, 50, 255);
		
		board = new TBoard();
		board.delegate = this;
		board.setPosition(new JGPoint(0,-TDefaults.sharedInstance().getBlockSize()*2));
		this.addChild(board);
		
		starField = new TStarField(TDefaults.sharedInstance().getWidth()*TDefaults.sharedInstance().getBlockSize(), 
				TDefaults.sharedInstance().getHeight()*TDefaults.sharedInstance().getBlockSize());
		starField.setZOrder(-1);
		addChild(starField);
		
		nextBox.setPosition(new JGPoint(400,50));
		addChild(nextBox);
		
		scoreBox = new TDisplayBox("Score");
		scoreBox.setContentText("0");
		scoreBox.setPosition(new JGPoint(400, 185));
		addChild(scoreBox);
		
		levelBox = new TDisplayBox("Level");
		levelBox.setContentText("0");
		levelBox.setPosition(new JGPoint(400, 285));
		addChild(levelBox);
		
		linesBox = new TDisplayBox("Lines");
		linesBox.setContentText("0");
		linesBox.setPosition(new JGPoint(400, 385));
		addChild(linesBox);
		
		pauseButton = new TButton(new JGSize(75, 75), "");
		pauseButton.setFontSize(40);
		pauseButton.setPosition(new JGPoint(400, 550));
		pauseButton.delegate = this;
		addChild(pauseButton);
		
		restartButton = new TButton(new JGSize(150, 50), "Restart");
		restartButton.setFont(new Font("Arial", Font.PLAIN, 25));
		restartButton.setFontSize(35);
		restartButton.setPosition(new JGPoint(starField.getContentSize().width/2, 400));
		restartButton.delegate = this;
		restartButton.isVisible = false;
		addChild(restartButton);
		
		mainMenuButton = new TButton(new JGSize(225, 50), "Main Menu");
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 25));
		mainMenuButton.setFontSize(35);
		mainMenuButton.setPosition(new JGPoint(starField.getContentSize().width/2, 475));
		mainMenuButton.delegate = this;
		mainMenuButton.isVisible = false;
		addChild(mainMenuButton);
		
		JGSimpleAudioEngine.SharedInstance().playMusic("src/Resources/SFX/TTheme.wav", true);
						
	}
	
	@Override
	public void Update(float dt){
		super.Update(dt);
		
		//start the move delay timer if it hasn't already
		moveDelayTimer.update(dt);
		
		JGInputManager im = JGInputManager.sharedInstance();
		if((im.getKeyPressed(KeyEvent.VK_LEFT) && !oldLeft)){
			moveDelayTimer.setTime(0.1f);
			moveDelayTimer.start();
			board.movePieceLeft();
		}else if(im.getKeyPressed(KeyEvent.VK_LEFT) &&moveDelayTimer.isFinished()){
			board.movePieceLeft();
		}
		oldLeft = im.getKeyPressed(KeyEvent.VK_LEFT);
		
		if((im.getKeyPressed(KeyEvent.VK_RIGHT) && !oldRight)){
			moveDelayTimer.setTime(0.1f);;
			moveDelayTimer.start();
			board.movePieceRight();
		}else if(im.getKeyPressed(KeyEvent.VK_RIGHT) &&moveDelayTimer.isFinished()){
			board.movePieceRight();
		}
		oldRight = im.getKeyPressed(KeyEvent.VK_RIGHT);
				
		if((im.getKeyPressed(KeyEvent.VK_UP) && !oldUp)){
			board.rotatePiece();
		}
		oldUp = im.getKeyPressed(KeyEvent.VK_UP);
		
		if(im.getKeyPressed(KeyEvent.VK_DOWN)){
			board.dropPiece();
		}
	}
	
	@Override
	public void midDraw(float dt, Graphics g){
		
		JGGraphics.fillNodeRect(this, new JGPoint(), this.getContentSize(), g);
		JGGraphics.fillNodeGradientRect(this, new JGPoint(), this.getContentSize(), fillColor, Color.BLACK, new JGPoint(0,300), new JGPoint(0,this.getContentSize().height), g);
		
	}
	
	///////////////////////////
	//TBoard Delegate Methods//
	///////////////////////////

	@Override
	public void nextShapeChanged(TShape shape) {
		nextBox.updateShape(shape);
	}

	@Override
	public void scoreChanged(int score) {
		scoreBox.setContentText("" + score);
	}

	@Override
	public void linesChanged(int lines) {
		linesBox.setContentText("" + lines);
	}

	@Override
	public void levelChanged(int level) {
		levelBox.setContentText("" + level);
		starField.setSpeed((level+1)*2*0.01f);
	}
	
	@Override
	public void gameOver(){
		restartButton.isVisible = true;
		mainMenuButton.isVisible = true;
	}
	
	////////////////////////////
	//TButton Delegate Methods//
	////////////////////////////

	@Override
	public void pressed(TButton button) {	
		
		//pause button
		if (button == pauseButton){
			if (!board.getGameRunning()){
				if (board.resumeGame()){
					button.setTitle("");
					JGSimpleAudioEngine.SharedInstance().resumeMusic("src/Resources/SFX/TTheme.wav");
					restartButton.isVisible = false;
					mainMenuButton.isVisible = false;
				}
			}else{
				if (board.pauseGame()){
					button.setTitle("");
					JGSimpleAudioEngine.SharedInstance().pauseMusic("src/Resources/SFX/TTheme.wav");
					restartButton.isVisible = true;
					mainMenuButton.isVisible = true;
				}
			}
		}else if (button == restartButton){
			JGSimpleAudioEngine.SharedInstance().stopMusic("src/Resources/SFX/TTheme.wav");
			JGGame.sharedInstance().replaceRootNode(new TGamePlay());
		}else if (button == mainMenuButton){
			JGSimpleAudioEngine.SharedInstance().stopMusic("src/Resources/SFX/TTheme.wav");
			JGGame.sharedInstance().replaceRootNode(new TMainMenu());
		}

	}

	@Override
	public void released(TButton button) {
		
	}
	
}
