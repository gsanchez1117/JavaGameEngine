package JGGameEngine.ParticleSystem;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGUtilities;
import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGTextureNode;

public class JGParticleSystem extends JGNode {
	
	ArrayList<JGParticle> particlePool;
	boolean active;
	boolean manualRunning;
	boolean manualRun;
	HashMap<String, Object> configDict;
	boolean autoKill;
	
	JGPoint posVar;
	float life;
	float lifeVar;
	int totalParticles;
	int emissionRate;
	float duration;
	
	Color startColor;
	Color startColorVar;
	Color endColor;
	Color endColorVar;
	float radius;
	float radiusVar;
	float startScale;
	float startScaleVar;
	float endScale;
	float endScaleVar;
	
	String textureFile;
	JGTextureNode texture;
	boolean textureEnabled;
	boolean textureAdditive;
	
	float speed;
	float speedVar;
	float angle;
	float angleVar;
	JGPoint gravity;
	float radialAccel;
	float radialAccelVar;
	float tangentialAccel;
	float tangentialAccelVar;
	
    int particleCount;
    int particleIndex;
    float elapsed;
    float emitCounter;

	public JGParticleSystem(String configFile){

        particlePool = new ArrayList<JGParticle>();
        active = true;
        manualRunning = false;
        manualRun = true;
        configDict = null;
        autoKill = false;

        posVar = new JGPoint(2, 2);
        life = 1.0f;
        lifeVar = 0;
        totalParticles = 500;
        emissionRate = 50;
        duration = 2;
        
        startColor = new Color(255, 255, 0, 255);
        startColorVar = new Color(0, 0, 0, 0);
        endColor = new Color(255, 0, 0, 0);
        endColorVar = new Color(0, 0, 0, 0);
        radius = 0;
        radiusVar = 0;
        startScale = 1;
        startScaleVar = 0;
        endScale = 5;
        endScaleVar = 0;
        
        texture = null;
        textureEnabled = false;
        textureAdditive = false;
        
        speed = 50;
        speedVar = 50;
        angle = 0;
        angleVar = 360;
        gravity = new JGPoint(0, 0);
        radialAccel = 0;
        radialAccelVar = 0;
        tangentialAccel = 0;
        tangentialAccelVar = 0;
        
        
        if (configFile != null){
            loadSystem(configFile);
        }
	}
	
	@Override
	public void Update(float dt){
		super.Update(dt);

		elapsed += dt;
        active = (elapsed < duration);

        if (!active && !manualRunning){
            if (autoKill)
               this.killNode();

            for (JGParticle p : particlePool){
            	if (p.life > 0)
            		updateParticle(p, dt, particleIndex);
            }
            return;
        }

        if (emissionRate != 0 && manualRun){

            //# emit new particles based on how much time has passed and the emission rate
            float rate = 1.0f / emissionRate;
            emitCounter += dt;

            while (!isFull() && emitCounter > rate){
                addParticle();
                emitCounter -= rate;
            }
        }

        particleIndex = 0;

        while (particleIndex < particleCount){
            JGParticle p = particlePool.get(particleIndex);
            updateParticle(p, dt, particleIndex);
        }
	}
	
	@Override 
	public void midDraw(float dt, Graphics g){
        for (JGParticle p : particlePool){
            if (p.life > 0){
            	JGGraphics.drawNodeCirlce(this, p.position, (int)(p.drawRadius * p.scale), p.color, p.color, g);
            }
        }
	}
	public void restart(){
        //empty the particle pool
        particlePool.clear(); 
        
        for(int i = 0; i < totalParticles; i++){
            JGParticle p = new JGParticle();
            particlePool.add(p);
        }
    
        particleCount = 0;
        particleIndex = 0;
        elapsed = 0;
        emitCounter = 0;
	}
	
	public void reset(){
		reconfigure();
	}
	
	@SuppressWarnings("unchecked")
	public void loadSystem(String fileName){
		File file = new File(fileName);
		try{
			if (!file.exists()){
				System.out.println("Eorro: JGParticleSystem file does not exist!");
				return;
			}
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			configDict = (HashMap<String, Object>)ois.readObject();
			ois.close();
			fis.close();
		}catch(Exception e){
			System.out.println("Error loading JGParticleSystem!");
			e.printStackTrace();
		}
		
		if (configDict != null){
			reset();
		}else{
			restart();
		}
	}
	
	public void saveSystem(String fileName){
		HashMap<String, Object> outDict = new HashMap<String, Object>();
		
		outDict.put("PosVar", posVar);
		outDict.put("Life", life);
		outDict.put("LifeVar", lifeVar);
		outDict.put("TotalParticles", totalParticles);
		outDict.put("EmissionRate", emissionRate);
		outDict.put("Duration", duration);
         
		outDict.put("StartColor", startColor);
		outDict.put("StartColorVar", startColorVar);
		outDict.put("EndColor", endColor);
		outDict.put("EndColorVar", endColorVar);
		outDict.put("Radius", radius);
		outDict.put("RadiusVar", radiusVar);
		outDict.put("StartScale", startScale);
		outDict.put("StartScaleVar", startScaleVar);
		outDict.put("EndScale", endScale);
		outDict.put("EndScaleVar", endScaleVar);
         
		outDict.put("TextureFile", textureFile);
		outDict.put("TextureEnabled", textureEnabled);
		outDict.put("TextureAdditive", textureAdditive);
         
		outDict.put("Speed", speed);
		outDict.put("SpeedVar", speedVar);
		outDict.put("Angle", angle);
		outDict.put("AngleVar", angleVar);
		outDict.put("Gravity", gravity);
		outDict.put("RadialAccel", radialAccel);
		outDict.put("RadialAccelVar", radialAccelVar);
		outDict.put("TangentialAccel", tangentialAccel);
		outDict.put("TangentialAccelVar", tangentialAccelVar);

		File file = new File(fileName);
		try{
			if (!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(outDict);
			oos.close();
			fos.close();
		}catch(Exception e){
			System.out.println("Error saving JGParticleSystem");
			e.printStackTrace();
		}
	}
	
	private void reconfigure(){
		if (configDict != null){
			posVar = (JGPoint)configDict.get("PosVar");
			life = (float)configDict.get("Life");
			lifeVar = (float)configDict.get("LifeVar");
			totalParticles = (int)configDict.get("TotalParticles");
			emissionRate = (int)configDict.get("EmissionRate");
			duration = (float)configDict.get("Duration");
	         
			startColor = (Color)configDict.get("StartColor");
			startColorVar = (Color)configDict.get("StartColorVar");
			endColor = (Color)configDict.get("EndColor");
			endColorVar = (Color)configDict.get("EndColorVar");
			radius = (float)configDict.get("Radius");
			radiusVar = (float)configDict.get("RadiusVar");
			startScale = (float)configDict.get("StartScale");
			startScaleVar = (float)configDict.get("StartScaleVar");
			endScale = (float)configDict.get("EndScale");
			endScaleVar = (float)configDict.get("EndScaleVar");
	         
			textureFile = (String)configDict.get("TextureFile");
			textureEnabled = (boolean)configDict.get("TextureEnabled");
			textureAdditive = (boolean)configDict.get("TextureAdditive");
	         
			speed = (float)configDict.get("Speed");
			speedVar = (float)configDict.get("SpeedVar");
			angle = (float)configDict.get("Angle");
			angleVar = (float)configDict.get("AngleVar");
			gravity = (JGPoint)configDict.get("Gravity");
			radialAccel = (float)configDict.get("RadialAccel");
			radialAccelVar = (float)configDict.get("RadialAccelVar");
			tangentialAccel = (float)configDict.get("TangentialAccel");
			tangentialAccelVar = (float)configDict.get("TangentialAccelVar");
		}
		restart();
	}
	
	private boolean isFull(){
		return particleCount == totalParticles;
	}
	
	private boolean addParticle(){
        if (isFull())
            return false;

        JGParticle p = particlePool.get(particleCount);
        initParticle(p);
        particleCount += 1;
        
        return true;
	}
	
	private void initParticle(JGParticle particle){
        particle.texture = texture;
        particle.textureEnabled = textureEnabled;
        particle.textureAdditive = textureAdditive;
        
        JGPoint pVar = new JGPoint(posVar.x * JGUtilities.random11(),
                        	 	   posVar.y * JGUtilities.random11());
        

        particle.position = pVar;

        float a = angle + angleVar * JGUtilities.random11();
        float s = speed + speedVar * JGUtilities.random11();

        //# it's easier to set speed and angle at this level
        //# but once the particle is active and being updated, it's easier
        //# to use a vector to indicate speed and angle. So particle.setVelocity
        //# converts the angle and speed values to a velocity vector
        particle.setVelocity(a, s);

        particle.radialAccel = radialAccel + radialAccelVar * JGUtilities.random11();
        particle.tangentialAccel = tangentialAccel + tangentialAccelVar * JGUtilities.random11();

        float l = life + lifeVar * JGUtilities.random11();

        particle.life = (int)Math.max(0, l);
        
        particle.scale = startScale;
        particle.deltaScale =  (endScale - startScale);
        
        //#in order to avoid division by zero
        particle.deltaScale /= particle.life;

        if (radiusVar != 0){
        	particle.radius = radius + radiusVar * JGUtilities.random11();
        }else{
        	particle.radius = radius;
        }

        //# color
        //# note that colors are stored as arrays => [r,g,b,a],
        //# this makes it easier to tweak the color every frame in _updateParticle
        //# The renderer will take this array and turn it into a css rgba string
        if (startColor != null){
        	Color sCol = new Color((int)JGUtilities.clampf(startColor.getRed() + startColorVar.getRed() * JGUtilities.random11(), 0, 255),
        			(int)JGUtilities.clampf(startColor.getGreen() + startColorVar.getGreen() * JGUtilities.random11(), 0, 255),
        			(int)JGUtilities.clampf(startColor.getBlue() + startColorVar.getBlue() * JGUtilities.random11(), 0, 255),
        			(int)JGUtilities.clampf(startColor.getAlpha() + startColorVar.getAlpha() * JGUtilities.random11(), 0, 255));
            
            //# if there is no endColor, then the particle will end up staying at startColor the whole time
            Color eCol = sCol;
            if (endColor != null){
            	eCol = new Color((int)JGUtilities.clampf(endColor.getRed() + endColorVar.getRed() * JGUtilities.random11(), 0, 255),
            			(int)JGUtilities.clampf(endColor.getGreen() + endColorVar.getGreen() * JGUtilities.random11(), 0, 255),
            			(int)JGUtilities.clampf(endColor.getBlue() + endColorVar.getBlue() * JGUtilities.random11(), 0, 255),
            			(int)JGUtilities.clampf(endColor.getAlpha() + endColorVar.getAlpha() * JGUtilities.random11(), 0, 255));

            }
            particle.color = sCol;
            
            //#in order to avoid division by zero
            if (particle.life > 0){
                particle.deltaColor[0] = (int)JGUtilities.clampf(eCol.getRed() - sCol.getRed() / particle.life, -255, 255);
                particle.deltaColor[1] = (int)JGUtilities.clampf(eCol.getGreen() - sCol.getGreen() / particle.life, -255, 255);
                particle.deltaColor[2] = (int)JGUtilities.clampf(eCol.getBlue() - sCol.getBlue() / particle.life, -255, 255);
                particle.deltaColor[3] = (int)JGUtilities.clampf(eCol.getAlpha() - sCol.getAlpha() / particle.life, -255, 255);
            }
        }
	}
	
	private void updateParticle(JGParticle p, float dt, int i){
        if (p.life > 0){

            //# these vectors are stored on the particle so we can reuse them, avoids
            //# generating lots of unnecessary objects each frame
            p.forces.x = 0;
            p.forces.y = 0;

            p.radial.x = 0;
            p.radial.y = 0;

            //# don't apply radial forces until moved away from the emitter
            if ((p.position.x != 0 || p.position.y != 0) && (p.radialAccel != 0 || p.tangentialAccel != 0)){
                p.radial.x = p.position.x;
                p.radial.y = p.position.y;

                p.radial.normalize();
            }

            p.tangential.x = p.radial.x;
            p.tangential.y = p.radial.y;

            p.radial.x *= p.radialAccel;
            p.radial.y *= p.radialAccel;

            float newy = (float)p.tangential.x;
            p.tangential.x = - p.tangential.y;
            p.tangential.y = newy;

            p.tangential.x *= p.tangentialAccel;
            p.tangential.y *= p.tangentialAccel;

            p.forces.x = p.radial.x + p.tangential.x + gravity.x;
            p.forces.y = p.radial.y + p.tangential.y + gravity.y;

            p.forces.x *= dt;
            p.forces.y *= dt;

            p.velocity.x += p.forces.x;
            p.velocity.y += p.forces.y;

            p.position = p.position.add(new JGPoint(p.velocity.x * dt, p.velocity.y * dt));

            p.life -= dt;
            
            p.scale += p.deltaScale * dt;

            if (p.color != null){
            	p.color = new Color((int)JGUtilities.clampf(p.color.getRed() + p.deltaColor[0] * dt, 0, 255),
            			(int)JGUtilities.clampf(p.color.getGreen() + p.deltaColor[1] * dt, 0, 255),
            			(int)JGUtilities.clampf(p.color.getBlue() + p.deltaColor[2] * dt, 0, 255),
            			(int)JGUtilities.clampf(p.color.getAlpha() + p.deltaColor[3] * dt, 0, 255));
            }

            particleIndex += 1;
        }else{
            //# the particle has died, time to return it to the particle pool
            //# take the particle at the current index
            JGParticle temp = particlePool.get(i);

            //# and move it to the end of the active particles, keeping all alive particles pushed
            //# up to the front of the pool
            particlePool.remove(i);
            particlePool.add(temp);

            //# decrease the count to indicate that one less particle in the pool is active.
            particleCount -= 1;
        }
	}
}
