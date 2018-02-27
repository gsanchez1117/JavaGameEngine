package JGGameEngine;

import java.awt.geom.AffineTransform;
import JGGameEngine.Node.JGNode;


public class JGUtilities {
	
	public static int unsignedToBytes(byte b) {
	    return b & 0xFF;
	}
	
	public static float clampf(float val, float min, float max){
		return Math.min(Math.max(val, min), max);
	}
	
	public static double clamp(double val, double min, double max){
		return Math.min(Math.max(val, min), max);
	}
	
	public static double lerp(double v0, double v1, double t) {
		  return (1 - t) * v0 + t * v1;
	}
	
	public static float rand(int minOrMax, int maxOrUndefined, boolean dontFloor){

	    int rangee = maxOrUndefined - minOrMax;
	   
	    double result = minOrMax + (Math.random() * rangee);

	    if (minOrMax > 0 && maxOrUndefined > 0 && !dontFloor){
	        return (float)Math.floor(result);
	    }else{
	        return (float)result;
	    }
	}
	    
	public static float random11(){
	    return rand(-1, 1, true);
	}
	
	/*getTransformFromJGNode
	 * aNode - the node to pass into the method
	 * return - an AffineTransform based on the nodes rotation, position, scale, content size, and anchor point
	 */
	public static AffineTransform getTransformFromJGNode(JGNode aNode){
		return getTransformFromJGNode(aNode, true);
	}
	
	public static AffineTransform getTransformFromJGNode(JGNode aNode, boolean includeParent){
		AffineTransform at = new AffineTransform();
		
		//if the node has a parent then apply the parents transform first
		if (includeParent && aNode.parent != null){
			at = getTransformFromJGNode(aNode.parent);
		}
        
		//get properties from node
		JGPoint pos = aNode.getPosition();
		JGSize size = aNode.getContentSize();
		JGPoint cs = new JGPoint(size.width, size.height);
		JGPoint ap = aNode.getAnchorPoint();
        JGPoint scale = aNode.getScale();
        
        //flip the scale based on flip flags on node
        scale = scale.mult(new JGPoint(aNode.getFlipX() ? -1 : 1, 
        		aNode.getFlipY() ? -1 : 1));
		

		//Rotation
        at.rotate(aNode.getRotation(), pos.x, 
        			   		  		   pos.y);
        
       
        //Translation
        cs = cs.mult(scale); //multiply the content size by the scale
        pos = pos.sub(cs.mult(ap)); //apply anchor point and content size to translation
        at.translate(pos.x, 
        			 pos.y);
       
        //Scale
        at.scale(scale.x, 
        		 scale.y);
        
		
		return at; //return new transformation 
	}
		
}
