package JGGameEngine;

/**
 * JGRect
 * @author Gabriel Sanchez
 * Description: JGRect is a class that is used to represent a rectangle in 2d space. It is described by an origin (JGPoint) and a size (JGSize).
 * The origin of a JGRect is the upper left hand corner and its size extends our from the origin right and down.
 */
public class JGRect {
	
	/** Origin of the rectangle represented as a JGPoint */
	public JGPoint origin;
	
	/** Size of the rectangle represented as a JGSize object */
	public JGSize size;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Default constructor. Creates a new JGRect with size and origin set to zero.
	 */
	public JGRect(){
		origin = new JGPoint();
		size = new JGSize();
	}
	
	/**
	 * Creates a new JGRect with passed in Origin(x, y) and Size(w, h)
	 * @param x - x position of the origin
	 * @param y - y position of the origin
	 * @param w - width of the size
	 * @param h - height of the size
	 */
	public JGRect(float x, float y, float w, float h){
		origin = new JGPoint(x, y);
		size = new JGSize(w, h);
	}
	
	/**
	 * Creates a new JGRect from passed in origin and size.
	 * @param o - origin JGPoint
	 * @param s - size JGPoint
	 */
	public JGRect(JGPoint o, JGSize s){
		origin = new JGPoint(o.x, o.y);
		size = new JGSize(s.width, s.height);
	}
	
	/**
	 * Creates a new JGRect from a minimum and maximum point. 
	 * Origin becomes the minimum point and size becomes the distance between the minimum and maximum points.
	 * @param min - minimum point 
	 * @param max - maximum point
	 */
	public JGRect(JGPoint min, JGPoint max){
		origin = new JGPoint(min.x, min.y);
		size = new JGSize(max.x - min.x, max.y - min.y);
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	/**
	 * Checks if this JGRect contains a given JGPoint.
	 * @param p - point to test for.
	 * @return - true if the point is within the JGRect. false if not.
	 */
    public boolean contains(JGPoint p){
        return (origin.x < p.x && origin.y < p.y &&
        		origin.x + size.width > p.x  &&
        		origin.y + size.height > p.y);
    }
    
	//////////////////////
	//Override Methods	//
	//////////////////////
	
    /**
     * Custom override of the toString method.
     */
	@Override 
	public String toString(){
		return "{ Origin:" + origin +  ", Size:" + size + "}"; 
	}
}
