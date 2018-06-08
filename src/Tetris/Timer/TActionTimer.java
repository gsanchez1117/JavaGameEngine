package Tetris.Timer;

public class TActionTimer {
	
	//////////////////////
	//Instance Variables//
	//////////////////////
	
	/** current time of the timer. */
	private float curTime;
	
	/** total time for the timer to count to. */
	private float totalTime;
	
	/** is timer currently running. */
	private boolean isRunning;
	
	///////////////
	//Constructor//
	///////////////
	
	/**
	 * Creates a new instance of TActionTimer with a given time.
	 * @param Time - time in seconds given as a float
	 */
	public TActionTimer(float time){
		curTime = 0.0f;
		totalTime = time;
		isRunning = false;
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	/**
	 * starts the timer updating.
	 */
	public void start(){
		isRunning = true;
	}
	
	/**
	 * stops the timer from updating.
	 */
	public void stop(){
		isRunning = false;
	}
	
	/**
	 * first stops the timer, then resets the current time.
	 */
	public void reset(){
		stop();
		curTime = 0.0f;
	}
	
	/**
	 * Checks if the timer has finished.
	 * @return - true if finished. False if not finished.
	 */
	public boolean isFinished(){
		return curTime >= totalTime;
	}
	
	/**
	 * updates the current time for the timer by stepping the current time by the passed in delta time. 
	 * @param dt - delta time added to current time.
	 */
	public void update(float dt){
		if (!isRunning){ return; }
		curTime += dt;
		if (curTime >= totalTime)
			isRunning = false;
	}
	
	///////////////////
	//Getters/Setters//
	///////////////////

	/**
	 * first resets the timer and then sets the new time for the timer.
	 * @param time - amount of time for the timer to run in seconds given as a float.
	 */
	public void setTime(float time){
		reset();
		totalTime = time;
	}

	/**
	 * gets the total running time of the timer.
	 * @return - time in seconds given as a float.
	 */
	public float getTotalTime(){
		return totalTime;
	}
	
	/**
	 * gets the current running time of the timer.
	 * @return - time in seconds given as a float.
	 */
	public float getCurrTime(){
		return curTime;
	}
	
	/**
	 * Checks if the timer is running and returns a boolean
	 * @return - true if running. false if not.
	 */
	public boolean getIsRunning(){
		return isRunning;
	}

}