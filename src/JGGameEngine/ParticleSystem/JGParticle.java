package JGGameEngine.ParticleSystem;

import java.awt.Color;

import JGGameEngine.JGPoint;
import JGGameEngine.Node.JGTextureNode;

public class JGParticle {
	
	JGPoint position;
	float drawRadius;
	
	JGTextureNode texture;
	boolean textureEnabled;
	boolean textureAdditive;
	
	float radialAccel;
	float tangentialAccel;
	
	float life;
	
	float scale;
	float deltaScale;
	float radius;
	
	Color color;
	int[] deltaColor;
	
	JGPoint forces;
	JGPoint radial;
	JGPoint tangential;
	JGPoint velocity;
	
	public JGParticle(){
        position = new JGPoint(0, 0);
        drawRadius = 2;
        
        texture = null;
        textureEnabled = false;
        textureAdditive = false;
        
        radialAccel = 0;
        tangentialAccel = 0;
        
        life = 0;
        
        scale = 1.0f;
        deltaScale = 0;
        radius = 0;
        
        color = new Color(0,0,0,0);
        deltaColor = new int[4];
        
        forces = new JGPoint(0, 0);
        radial = new JGPoint(0, 0);
        tangential = new JGPoint(0, 0);
        setVelocity(0, 0);
	}
	
	public void setVelocity(float angle, float speed){
		velocity = new JGPoint(speed * Math.cos(Math.toRadians(angle)),
							  -speed * Math.sin(Math.toRadians(angle)));
	}

}
