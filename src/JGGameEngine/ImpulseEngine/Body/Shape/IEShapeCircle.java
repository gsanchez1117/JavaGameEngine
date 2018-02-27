package JGGameEngine.ImpulseEngine.Body.Shape;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.JGBoundingBox;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.Body.IEMassData;

public class IEShapeCircle extends IEShape {

	public IEShapeCircle(float r){
		radius = r;
		SetDensity(1.0f);
	}
		
	public void SetDensity(float density){
		float mass = (float)Math.PI * radius * radius * density;
	    float inertia = mass * radius * radius;
	    massData = new IEMassData(mass, inertia);	    
	}
		
	@Override
	public JGBoundingBox calculateBoundingBox(){
		return new JGBoundingBox(
				new JGPoint(-radius,-radius),
				new JGPoint(radius,radius)
		);
	}
	
	@Override
	public JGPoint getBoundingBoxTopLeft(){
		return new JGPoint(-radius,-radius);
	}
		
	@Override
	public void draw(Graphics g){
		
		JGGraphics.drawNodeCirlce(
				body, 
				new JGPoint(-radius,-radius), 
				(int)radius, 
				Color.white, 
				col, 
				g
		);
		
		JGGraphics.drawNodeRoundedLine(
				body, 
				new JGPoint(0, 0), 
				new JGPoint(radius, 0), 
				1, 
				Color.white,
				g
		);
		
	}
	
	@Override
	public void setOrient(float radians){}
	
	public Type GetType(){
		return Type.cirlce;
	}
	
	@Override
	public IEShapeCircle clone(){
		return new IEShapeCircle(radius);
	}
	
}
