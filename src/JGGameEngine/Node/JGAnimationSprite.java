package JGGameEngine.Node;

import java.awt.Graphics;
import java.util.HashMap;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;

public class JGAnimationSprite extends JGSprite {
	
	/** the size of each frame */
	private JGSize frameSize;					
	
	/** the currently selected frame */
	private int currentFrame;					
	
	/** number of frames in the animation */
	private int frames;	
	
	/** the frame to start the animation on */
	private int startFrame;
	
	/** the frame to end the animation on */
	private int endFrame;
		
	/** the current frame position */
	private JGPoint currentFramePosition;		
	
	/** is the sprite animating */
	private boolean isAnimating;				
	
	/** total time of animation in seconds */
	private float animationDuration;			
	
	/** the current time of animation */
	private float currentAnimationTime;	
	
	/** A hash map to store predefined animations in the following format
	 *  Key:	  Value:
	 *  String	  JGPoint
	 *  (animName)(startFrame, endFrame)
	 */
	private HashMap<String, JGPoint> animations;
	
	/** name of the current animation */
	private String currentAnimation;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Constructor method for the JGAnimationSprite class.
	 * @param fileName - the file name to the image to be used for animation
	 * @param FrameSize - the size of each frame in the animation sheet. ex: 32x32
	 */
	public JGAnimationSprite(String fileName, JGSize FrameSize){
		super(fileName); 								//call the super constructor it loads the texture
		
		frameSize = FrameSize; 							//set the frame size
		frames = getFrameCount();						//get the number of frames available
		setCurrentFrame(0); 							//set the current frame to 0 by default
		
		isAnimating = false;							//not animating by default
		animationDuration = 1.0f;						//the duration of an animation is set to 1 second by default
		currentAnimationTime = 0.0f;					//set to 0 by default
		
		setContentSize(frameSize);						//update the content size
		
		animations = new HashMap<String, JGPoint>(); 	//initialize the animations hash map
		currentAnimation = null;						//current animation is null by default
	}
		
	//////////////////
	//Public Methods//
	//////////////////
		
	/**
	 * Increments the current frame of the animation.
	 * If current frame goes beyond the animation bounds it rolls over to the beginning of the animation.
	 */
	public void incFrame(){
		setCurrentFrame(currentFrame+1);
	}
	
	/**
	 * Decrements the current frame of the animation.
	 * If current frame goes below the animation bounds it rolls over to the end of the animation.
	 */
	public void decFrame(){
		setCurrentFrame(currentFrame-1);
	}
	
	/**
	 * Adds an animation with the given name, start, and end frame.
	 * If the animation already exists then the existing animation will be overwritten.
	 * If name is null then the method returns and does nothing.
	 * @param name - name of the animation. also used for running the animation
	 * @param startFrame - the starting frame of the animation
	 * @param endFrame - the ending frame of the animation
	 */
	public void addAnimation(String name, int startFrame, int endFrame){
		if (name == null){ return; }
		animations.put(name, new JGPoint(startFrame, endFrame));
	}
	
	/**
	 * Removes an animation from the animations hash map. 
	 * If the animation is not in the map then this method does nothing.
	 * If name is null this method does nothing.
	 * @param name - the name of the animation to remove.
	 */
	public void removeAnimation(String name){
		if (name == null){ return; }
		animations.remove(name);
	}
		
	/**
	 * Runs the animation with the given name. 
	 * If an animation with the same name is already running the method does nothing.
	 * If the animation does not exist then this method does nothing.
	 * If name is null then the animation will run from frame 0 to frames-1.
	 * @param name - the name of the animation to run.
	 */
	public void animate(String name){
		
		//don't animate again if already animating with the same animation
		if (name == null || currentAnimation == null){
			if (name == currentAnimation && isAnimating)
				return;
		}else if (name.equals(currentAnimation) && isAnimating){
			return;
		}
		
		JGPoint startStop = new JGPoint(0, frames-1);
		
		if (name != null){
			if (!animations.containsKey(name)){ return; }
			startStop = animations.get(name);
		}
		setStartFrame((int)startStop.x);
		setEndFrame((int)startStop.y);
		isAnimating = true;
	}
	
	/**
	 * Runs the animation from frame 0 to frames-1.
	 * Equivalent to calling animate(null);
	 */
	public void animate(){
		animate(null);
	}
	
	/**
	 * Stops the currently running animation. 
	 */
	public void stopAnimation(){
		isAnimating = false;
		currentAnimationTime = 0.0f;
	}
	
	/**
	 * Resets the current animation to its start frame.
	 * Equivalent to setCurrentFrame(getStartFrame());
	 */
	public void resetAnimation(){
		setCurrentFrame(getStartFrame());
	}
	
	//////////////////////////
	//Getters and Setters	//
	//////////////////////////
	
	public int getFrames(){ return frames; }
	public int getCurrentFrame(){ return currentFrame; }
	public int getStartFrame(){ return startFrame; }
	public int getEndFrame(){ return endFrame; }
	public boolean getIsAnimating(){ return isAnimating; }
	public int getAnimationFrames(){ return endFrame - startFrame; }
	
	/**
	 * Gets the current animation of the sprite.
	 * @return - null if default animation, else returns current animation name.
	 */
	public String getCurrentAnimation(){ return currentAnimation; }
	
	/**
	 * Sets the current frame of the animation
	 * @param frame - frame to update the animation to
	 */
	public void setCurrentFrame(int frame){
		currentFrame = frame % frames; //circular indexing
		currentFramePosition = getFramePosition(currentFrame);			//update the current frame position
	}
		
	/**
	 * Sets the duration of the animation. 
	 * @param duration - how long to run the animation in seconds.
	 */
	public void setAnimationDuraction(float duration){ animationDuration = duration; }
	
	/**
	 * Sets the current animation for the sprite.
	 * If the animation name is not in the animations hash map then this method does nothing.
	 * If the animation name is null then the currentAnimation will be set to null.(NOTE: null is default animation)
	 * @param name - the name of the animation to be set.
	 */
	public void setCurrentAnimation(String name){
		if (name == null){ 
			currentAnimation = null;
			return;
		}
		if (!animations.containsKey(name)){ return; }
		currentAnimation = name;
		setStartFrame((int)animations.get(name).x);
		setEndFrame((int)animations.get(name).y);
	}
	
	///////////////////////////
	//Override Methods		//
	//////////////////////////
	
	@Override
	public void Update(float dt){
		super.Update(dt);														//must call super update

		if (isAnimating){
			currentAnimationTime += dt;											//add delta time to the current animation time
			
			float act = (animationDuration / (float)getAnimationFrames());		//animation change time
			int caf = startFrame + (int)(currentAnimationTime / act);			//current animation frame

			if (currentFrame != caf)
				setCurrentFrame(caf);
			
			if (currentAnimationTime > animationDuration){
				isAnimating = false;
				currentAnimationTime = 0.0f;
			}
		}
	}
		
	@Override 
	public void midDraw(float dt, Graphics g){
		
		if (getGameWindow() != null){
			JGGraphics.drawSprite(this, currentFramePosition, frameSize, g);
		}
		
	}
	
	///////////////////
	//Private Methods//
	///////////////////
	
	/**
	 * This method gets the position for drawing at a given frame position. (used by the JGAnimationSprite class)
	 * @param frame - the frame to get position for
	 * @return - the position on the source image of the given frame
	 */
	private JGPoint getFramePosition(int frame){
		int framesX = (int)(this.getTexture().getWidth() / frameSize.width);
		int y = (int)Math.floor(currentFrame/framesX);
		int x = currentFrame % framesX;

		return new JGPoint(x * frameSize.width,
				y * frameSize.height);
	}
	
	/**
	 * This method gets the amount of frames available in the animation sprite
	 * @return - the number of frames available in the animation sprite
	 */
	private int getFrameCount(){
		return (int)((this.getTexture().getWidth() / frameSize.width) * (this.getTexture().getHeight() / frameSize.height));
	}
	
	/**
	 * Used internally to set the start frame of the animation. 
	 * If the start frame goes out of bounds, the value is rolled over.
	 * @param frame - the frame to set as start frame
	 */
	private void setStartFrame(int frame){
		startFrame = frame % frames; //circular indexing
		if (endFrame < startFrame)
			endFrame = startFrame;
	}
	
	/**
	 * Used internally to set the end frame of the animation. 
	 * If the end frame goes out of bounds, the value is rolled over.
	 * @param frame - the frame to set as end frame
	 */
	private void setEndFrame(int frame){
		endFrame = frame % frames; //circular indexing
		if (startFrame > endFrame)
			startFrame = endFrame;
	}
	
}
