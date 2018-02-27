package JGGameEngine;

import java.util.ArrayList;

import JGGameEngine.ImpulseEngine.Body.IEBody;

public class JGBoundingBox {
	
	public JGPoint min;
	public JGPoint max;
	
	public JGBoundingBox(JGPoint Min, JGPoint Max){
		min = Min;
		max = Max;
	}
		
	/**
	 * Creates a new instance of JGBoundingBox with the given bodies. 
	 * <p>
	 * Note: the bounding boxe is given in local space for the bodies.
	 * @param bodies - the bodies used to create the bounding box
	 */
	public JGBoundingBox(ArrayList<IEBody> bodies){
		JGBoundingBox bb = null;
		for (IEBody body : bodies){
			if (bb == null){
				bb = body.getBoundingBoxLocal();
			}else{
				bb = join(body.getBoundingBoxLocal());
			}
			min = bb.min;
			max = bb.max;
		}
	}
	
	public JGBoundingBox copy(JGBoundingBox bb){
		return new JGBoundingBox(min, max);
	}
	
	public JGBoundingBox join(JGBoundingBox bb2){
		return new JGBoundingBox(
				new JGPoint(Math.min(min.x, bb2.min.x), Math.min(min.y, bb2.min.y)),
				new JGPoint(Math.max(max.x, bb2.max.x), Math.max(max.y, bb2.max.y))
		);
	}
	
	public JGBoundingBox addPoint(JGBoundingBox bb, JGPoint point){
		JGBoundingBox bbR = copy(bb);
		if (point.x < bbR.min.x) bbR.min.x = point.x;
		if (point.y < bbR.min.y) bbR.min.y = point.y;
		if (point.x > bbR.max.x) bbR.max.x = point.x;
		if (point.y > bbR.max.y) bbR.max.y = point.y;
		return bbR;
	}
	
	public boolean testPoint(JGBoundingBox bb, JGPoint point){
		if (point.x < bb.min.x) return false;
		if (point.y < bb.min.y) return false;
		if (point.x > bb.max.x) return false;
		if (point.y > bb.max.y) return false;
		return true;
	}
	
	public boolean testBB(JGBoundingBox bb2){
		if (min.x > bb2.max.x) return false;
		if (min.y > bb2.max.y) return false;
		if (max.x < bb2.min.x) return false;
		if (max.y < bb2.min.y) return false;
		return true;
	}
	
	public JGSize size(){
		return new JGSize(max.x-min.x, max.y-min.y);
	}

	public JGPoint center(JGBoundingBox bb){
		return new JGPoint((bb.max.x+bb.min.x)/2,
				(bb.max.y+bb.min.y)/2);
	}
	
	public boolean contains(JGBoundingBox bb){
		return (min.x < bb.min.x &&
				min.y < bb.min.y &&
				max.x > bb.max.x &&
				max.y > bb.max.y);
	}
	
	public JGBoundingBox cut(int axis){
		JGPoint center = center(this);
		float x = axis % 2;
		float y = (float)Math.floor(axis/2) % 2;
		return new JGBoundingBox(
				new JGPoint(x != 0 ? center.x : min.x, y != 0 ? center.y : min.y),
				new JGPoint(x != 0 ? max.x : center.x, y != 0 ? max.y : center.y)
		);
	}
	
}
