package JGGameEngine;

/**
 * JGSize
 * @author Gabriel Sanchez
 * 
 * Description: JGSize is an object that is used to store size and width.
 */
public class JGSize {
	
	/** width as double */
	public double width;
	
	/** height as double */
	public double height;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Default Constructor. Creates a JGSize object with width and height set to 0
	 */
	public JGSize(){ width = height = 0; }
	
	/**
	 * Creates a new JGSize object with the passed in width and height
	 * @param w - width 
	 * @param h - height
	 */
	public JGSize(double w, double h){
		width = w;
		height = h;
	}
	
	//////////////////////
	//Override Methods	//
	//////////////////////
	
	/**
	 * Custom override of the toString method.
	 */
	@Override
	public String toString(){
		return "[" + width + ", " + height + "]";
	}
	
	//////////////////////
	//Getters / Setters	//
	//////////////////////
	
	/**
	 * Gets the width value
	 * @return - width as double
	 */
	public double getWidth(){ return width; }
	
	/**
	 * Gets the height value
	 * @return - height as double
	 */
	public double getHeight(){ return height; }
	
	/**
	 * Gets the width value as a float
	 * @return - width cast to a float
	 */
	public float getWidthF(){ return (float) width; }
	
	/**
	 * Gets the height value as a float
	 * @return - height cast to a float
	 */
	public float getHeightF(){ return (float) height; }
	
	/**
	 * Gets the width value as an int
	 * @return - width cast to an int
	 */
	public int getWidthI(){ return (int) width; }
	
	/**
	 * Gets the height value as an int
	 * @return - height cast to an int
	 */
	public int getHeightI(){ return (int) height; }

}
