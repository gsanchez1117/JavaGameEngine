package Tetris.Timer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextNode;

public class TCountDownTimer extends JGNode {

	public TCountDownTimerDelegate delegate;
	private TActionTimer startDelayTimer = new TActionTimer(3.0f);
	private JGTextNode startText = new JGTextNode("Ready");
	
	public TCountDownTimer(float time){
		try{
			Font f = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("Resources/FONTS/fullPack2025.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(f);
			startText.setFont(f);
			startText.setFontSize(50);
		}catch(Exception e){
		}
		addChild(startText);
		setTime(time);
	}
		
	//////////////////
	//Public Methods//
	//////////////////
	
	public void updateTimer(float dt){
		startDelayTimer.update(dt);
		
		if (startDelayTimer.getIsRunning()){ 
			
			int currTime = (int)(startDelayTimer.getTotalTime()-startDelayTimer.getCurrTime());
			if (currTime > 0){
				startText.setText("" + currTime);
			}else{
				startText.setText("GO");
			}
			
		}
		
		if (startDelayTimer.isFinished()){
			startDelayTimer.reset();
			startDelayTimer.stop();
			isVisible = false;
			
			//notify the delegate
			if (delegate != null){
				delegate.countDownFinished();
			}
		}
	}
	
	public void setTime(float time){
		startDelayTimer.reset();
		startDelayTimer.start();
		startDelayTimer.setTime(time);
		startText.setText("" + time);
	}
	
	public void start(){
		startDelayTimer.start();
		isVisible = true;
	}
	
	public void stop(){
		startDelayTimer.stop();
		isVisible = false;
	}
	
	public void reset(){
		startDelayTimer.reset();
	}
	
	public void setText(String text){
		startText.setText(text);
	}
	
	public boolean getIsRunning(){
		return startDelayTimer.getIsRunning();
	}
	
	public boolean getIsFinished(){
		return startDelayTimer.isFinished();
	}
	
}
