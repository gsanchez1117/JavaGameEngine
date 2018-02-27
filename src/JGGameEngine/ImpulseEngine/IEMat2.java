package JGGameEngine.ImpulseEngine;

import JGGameEngine.JGPoint;

public class IEMat2 {

	float m00, m01;
	float m10, m11;
	
	float[][] m = new float[2][2];
	float[] v = new float[4];
	
	public IEMat2(){}
	
	public IEMat2(float radians){
		float c = (float)Math.cos(radians);
	    float s = (float)Math.sin(radians);

	    m00 = c; m01 = -s;
	    m10 = s; m11 =  c;
	}
	
	public IEMat2(float a, float b, float c, float d){
		m00 = a;
		m01 = b;
		m10 = c;
		m11 = d;
	}
	
	public void set(float radians){
		float c = (float)Math.cos( radians );
		float s = (float)Math.sin( radians );

	    m00 = c; m01 = -s;
	    m10 = s; m11 =  c;
	}
	
	public IEMat2 Transpose(){
		return new IEMat2( m00, m10, m01, m11 );
	}
	
	public JGPoint mult(JGPoint rhs){
		return new JGPoint(m00 * rhs.x + m01 * rhs.y, m10 * rhs.x + m11 * rhs.y);
	}
}
