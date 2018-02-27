package JGGameEngine;

import java.awt.Point;
import java.io.Serializable;

/**
 * 
 * @author Gabriel Sanchez
 * Description: JGPoint is an object that holds x and why values as doubles. A point is used to represent the position 
 * of a JGNode on the screen. JGPoint also has many math helper methods.
 */
public class JGPoint implements Serializable {
	
	//silence the serialize warning
	private static final long serialVersionUID = 1L;
	
	/** The x value of the point */
	public double x;
	
	/** The y value of the point */
	public double y;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Default Constructor. Creates a new JGPoint with x and y set to 0
	 */
	public JGPoint(){ x = y = 0; }
	
	/**
	 * Creates a new JGPoint with the passed in x and y values.
	 * @param x - x value
	 * @param y - y value
	 */
	public JGPoint(double x, double y){ 
		this.x = x; 
		this.y = y;
	}
	
	/**
	 * Creates a new JGPoint from the passed in JGPoint copying its x and y values over.
	 * @param p
	 */
	public JGPoint(Point p){ 
		this.x = p.x; 
		this.y = p.y;
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	/**
	 * Adds two JGPoints together and returns a new JGPoint containing the sum.
	 * @param other - the other JGPoint to add to this one.
	 * @return - a new JGPoint containing the sum.
	 */
    public JGPoint add(JGPoint other){
        return new JGPoint(this.x + other.x, this.y + other.y);
    }
    
    /**
     * Subtracts two JGPoints from each other returning a new JGPoint containing the difference.
     * @param other - the other JGPoint to sub from this one.
     * @return - a new JGPoint containing the difference.
     */
    public JGPoint sub(JGPoint other){
    	return new JGPoint(this.x - other.x, this.y - other.y);
    }
   
    /**
     * Multiplies two JGPoints by each other returning a new JGPoint containing the product.
     * @param other - the other JGPoint to multiply this one by.
     * @return - a new JGPoint containing the product.
     */
    public JGPoint mult(JGPoint other){
    	return new JGPoint(this.x * other.x, this.y * other.y);
    }
    
    /**
     * Multiplies each component of this JGPoint by a scalar number returning a new JGPoint whose components have been multiplied by the scalar.
     * @param scl - the number to multiply the components of this JGPoint by.
     * @return - a new JGPoint whose components have been multiplies by the scalar.
     */
    public JGPoint multScalar(double scl){
    	return new JGPoint(this.x * scl, this.y * scl);
    }

    
    /**
     * Divides two JGPoints by each other returning a new JGPoint whose components are the quotient of this JGPoint and the other.
     * @param other - the other JGPoint to divide this JGPoint by.
     * @return - a new JGPoint whose components are the quotient of this JGPoint and the other.
     */
    public JGPoint div(JGPoint other){
    	return new JGPoint(this.x / other.x, this.y / other.y);
    }
    
    /**
     * Gets the length squared of this JGPoint from the origin. 
     * @return - length squared from the origin as a double.
     */
    public double lengthSQ(){
    	return (this.x * this.x + this.y * this.y);
    }
    
    /**
     * Gets the length of this JGPoint from the origin.
     * @return - length from the origin as a double
     */
    public double length(){
    	return Math.sqrt(this.lengthSQ());
    }

    /**
     * Gets the distance squared from this JGPoint to another JGPoint.
     * @param other - the other JGPoin to calculate distance squared from.
     * @return - distance squared as a double.
     */
    public double distSQ(JGPoint other){
    	return Math.pow((other.x - this.x), 2) + Math.pow((other.y - this.y), 2);
    }

    /**
     * Gets the distance from this JGPoint to another JGPoint.
     * @param other - the other JGPoin to calculate distance from.
     * @return - distance as a double.
     */
    public double dist(JGPoint other){
    	return Math.sqrt(this.distSQ(other));
    }    
    
    /**
     * Normalizes the components of a JGPoint between 0 and 1.
     * @return - a new JGPoint with normalized components.
     */
    public JGPoint normalize(){
    	double length = this.length();
    	if (length > 0)
    		return new JGPoint(this.x / length, this.y / length);
    	return new JGPoint();
    }

    /**
     * Gets the dot product of this JGPoint and another.
     * @param other - the other JGPoint used to calculate the dot product.
     * @return - dot product as double.
     */
    public double dot(JGPoint other){
    	return (this.x * other.x) + (this.y * other.y);
    }
    
    public double cross(JGPoint other){
        return this.x * other.y - this.y * other.x;
    }
    
    public JGPoint cross(float a){
        return new JGPoint( -a * y, a * x );
    }

    /**
     * Negates the components of this JGPoint returning a new JGPoint with negated components.
     * @return - a new JGPoint with negated components.
     */
    public JGPoint negate(){
    	return new JGPoint(-this.x,-this.y);
    }
    
    /**
     * Checks if this JGPoint is equal to another .
     * @param other - the other JGPoint to check with.
     * @return - true if the points are considered equal. false if not.
     */
    public boolean equals(JGPoint other){
    	return this.x == other.x && this.y == other.y;
    }
    
    /**
     * Checks if this JGPoint is equal to another casting both JGPoint's components to integets.
     * @param other - the other JGPoint to check with.
     * @return - true if the points are considered equal. false if not.
     */
    public boolean equalsINT(JGPoint other){
    	return ((int)this.x == (int)other.x && (int)this.y == (int)other.y);
    }
    
    /**
     * Checks if this JGPoint is within the bounds of a triangle defined by three other JGPoints.
     * @param p0 - point 1
     * @param p1 - point 2
     * @param p2 - point 3
     * @return - true if the point is within the triangle. false if not.
     */
    public boolean intTriangle(JGPoint p0, JGPoint p1, JGPoint p2){
        
        // calculate barycentric coordinates to check
    	JGPoint v0 = p2.sub(p0);
    	JGPoint v1 = p1.sub(p0);
    	JGPoint v2 = sub(p0);
        
        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);
        
        double denom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * denom;
        double v = (dot00 * dot12 - dot01 * dot02) * denom;
        
        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
    
    //////////////////////
    //Override Methods	//
    //////////////////////
    
    /**
     * Custom override of the toString method.
     */
    @Override
    public String toString(){
    	return "{" + x + ", " + y + "}";
    }

}
