package JGGameEngine.ImpulseEngine.Body.Shape;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.JGBoundingBox;
import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.IEMat2;
import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.ImpulseEngine.Body.IEMassData;

public abstract class IEShape {
	
	public static final int MaxPolyVertexCount = 64;
	
	public enum Type{cirlce,poly}
	public Color col = new Color(0, 0, 255, 100);
	
	public IEBody body;
	
	public float radius;
	
	protected IEMassData massData;
	
	public IEMat2 u = new IEMat2();
		
	public IEShape(){}
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	public void setStatic(){
		massData = new IEMassData(0.0f, 0.0f);
	}
	
	//////////////////////
	//Abstract Methods	//
	//////////////////////
	
	public abstract void SetDensity(float density);
		
	public abstract JGBoundingBox calculateBoundingBox();
	
	public abstract JGPoint getBoundingBoxTopLeft();
			
	public abstract Type GetType();
	
	public abstract void setOrient(float radians);

	public abstract void draw(Graphics g);
	
	//////////////////////
	//Getters / Setters	//
	//////////////////////
	
	public IEMassData getMassData(){ return massData; }
	
}
