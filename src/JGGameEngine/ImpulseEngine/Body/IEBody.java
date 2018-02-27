package JGGameEngine.ImpulseEngine.Body;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.JGBitMask;
import JGGameEngine.JGBoundingBox;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.ImpulseEngine.IEManifold;
import JGGameEngine.ImpulseEngine.IEMath;
import JGGameEngine.ImpulseEngine.IEWorld;
import JGGameEngine.ImpulseEngine.Body.IEMaterial.IEMaterialType;
import JGGameEngine.ImpulseEngine.Body.Shape.IEShape;
import JGGameEngine.Node.JGNode;

public class IEBody extends JGNode {

	public JGPoint velocity;
	public JGPoint force;

	public float angularVelocity;
	public float torque;
	public float orient; // radians


	// http://gamedev.tutsplus.com/tutorials/implementation/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table/
	public float staticFriction;
	public float dynamicFriction;
	
	private IEMaterial material;
	private boolean isDynamic;
	public boolean shouldDrawShape;
	public JGBitMask collisionGroup;
	public JGBitMask collisionMask;
	private JGBoundingBox boundingBox;
	public boolean shouldDrawBoundingBox;
	private boolean doesCollisionResponse;
	

	// Shape interface
	public IEShape shape;
	public IEWorld world;
	
	//////////////////
	//Constructors	//
	//////////////////

	/**
	 * Creates a new instance of IEBody with the given shape
	 * @param Shape = shape of the physics body
	 */
	public IEBody(IEShape Shape){
		shape = Shape;
		shape.body = this;
		velocity = new JGPoint();
		angularVelocity = 0;
		torque = 0;
		orient = 0;
		force = new JGPoint();
		staticFriction = 0.5f;
		dynamicFriction = 0.3f;
		material = new IEMaterial(IEMaterialType.Wood);
		setDynamic();
		collisionGroup = new JGBitMask();
		collisionMask = new JGBitMask();
		boundingBox = shape.calculateBoundingBox();
		doesCollisionResponse = true;
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////

	public void ApplyForce(JGPoint f){
		force = force.add(f);
	}

	public void ApplyImpulse(JGPoint impulse, JGPoint contactVector){
		velocity = velocity.add(impulse.multScalar(shape.getMassData().getInvMass()));
		angularVelocity += shape.getMassData().getInvInertia() * IEMath.Cross(contactVector, impulse);
	}
		
	public void collisionOccured(IEManifold manifold){}
		
	//////////////////////
	//Override Methods	//
	//////////////////////

	@Override
	protected void midDraw(float dt, Graphics g){
		if (shape != null && shouldDrawShape)
			shape.draw(g);
		if (shouldDrawBoundingBox){
			JGGraphics.drawNodeRect(this, boundingBox.min, boundingBox.size(), g);
		}	
	}
	
	@Override
	public void setRotation(double radians){
		super.setRotation(radians);
		orient = (float)radians;
		shape.setOrient((float)radians);
	}
		
	@Override
	public void setContentSize(JGSize size){}
	
	//////////////////////
	//Setters / Getters	//
	//////////////////////
	
	public void setTopLeftPosition(JGPoint position){
		setPosition(position.sub(shape.getBoundingBoxTopLeft()));
	}

	public void SetStatic(){
		isDynamic = false;
		shape.setStatic();
		shape.col = new Color(100, 0, 100, 100);
	}
	
	public void setDynamic(){
		isDynamic = true;
		shape.SetDensity(material.getDensity());
		shape.col = new Color(0, 0, 255, 100);
	}
	
	public void setMaterial(IEMaterial Material){
		material = Material;
		if (isDynamic){
			setDynamic();
		}
	}
	
	public IEMaterial getMaterial(){ return material; }
	public boolean getIsDynamic(){ return isDynamic; }
	
	/**
	 * Gets the bounding box for this node ignoring spaces. (i.e. parents and node position not taken into account)
	 * @return - bounding box in local space
	 */
	public JGBoundingBox getBoundingBox(){ return boundingBox; }
	
	/**
	 * Gets the bounding box for this node in world space. (i.e. screen position of bounding box is returned)
	 * @return - bounding box in local space
	 */
	public JGBoundingBox getBoundingBoxWorld(){
		JGPoint worldPo = getPosition();
		if (parent != null)
			worldPo = parent.convertToWorldSpace(worldPo);
		worldPo = worldPo.add(boundingBox.min);
			return new JGBoundingBox(
					worldPo,
					worldPo.add(boundingBox.max.sub(boundingBox.min))
			);
	}
	
	/**
	 * Gets the bounding box for this node in local space. (i.e. node position taken into account)
	 * @return - bounding box in local space
	 */
	public JGBoundingBox getBoundingBoxLocal(){
		JGPoint worldPo = getPosition();
		worldPo = worldPo.add(boundingBox.min);
			return new JGBoundingBox(
					worldPo,
					worldPo.add(boundingBox.max.sub(boundingBox.min))
			);
	}
	public boolean getDoesCollisionResponse(){ return doesCollisionResponse; }
}
