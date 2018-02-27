package JGGameEngine.ImpulseEngine;

import java.awt.geom.AffineTransform;

import JGGameEngine.JGPoint;

public class IEMath {
	
	public static final float EPSILON = 0.0001f;
	public static final float gravityScale = 5.0f;
	
	// Comparison with tolerance of EPSILON
	public static boolean Equal(float a, float b){
		// <= instead of < for NaN comparison safety
		return Math.abs( a - b ) <= EPSILON;
	}
	
	public static float Sqr(float a){
		return a * a;
	}

	public static int Round(float a){
		return (int)(a + 0.5f);
	}

	public static float Random(float l, float h){
		float a = (float)Math.random();
		a = (h - l) * a + l;
		return a;
	}
	
	public static JGPoint Cross(float a, JGPoint v){
		return new JGPoint( -a * v.y, a * v.x );
	}
	
	public static JGPoint Cross(JGPoint v, float a){
		return new JGPoint( a * v.y, -a * v.x );
	}
	
	public static float Cross(JGPoint a, JGPoint b){
		return (float)(a.x * b.y - a.y * b.x);
	}

	public static boolean BiasGreaterThan(float a, float b){
		float k_biasRelative = 0.95f;
		float k_biasAbsolute = 0.01f;
		return a >= b * k_biasRelative + a * k_biasAbsolute;
	}
	
	public static JGPoint matMult(AffineTransform at, JGPoint p){
		double[] ms = new double[4];
		at.getMatrix(ms);
		return new JGPoint( ms[0] * p.x + ms[1] * p.y, ms[2] * p.x + ms[3] * p.y );
	}
}
