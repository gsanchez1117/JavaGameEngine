package JGGameEngine.ImpulseEngine;

import java.util.ArrayList;

import JGGameEngine.JGPoint;
import JGGameEngine.JGQuadtree;
import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.Node.JGNode;

public class IEWorld extends JGNode {

	private ArrayList<IEBody> bodies;
	private ArrayList<IEManifold> contacts;
	private static JGPoint gravity = new JGPoint();
	public JGQuadtree qTree;

	public IEWorld(){
		bodies = new ArrayList<IEBody>();
		contacts = new ArrayList<IEManifold>();
		qTree = new JGQuadtree();
		addChild(qTree);
	}

	public void step(float dt){
		
		// Generate new collision info
		contacts.clear();
		
		ArrayList<IEBody> nodes = new ArrayList<IEBody>();
		nodes.addAll(bodies);
		
		//rebuild the quadTree
		qTree.rebuild(nodes);
		
		for(int i = 0; i < nodes.size( ); i++){
			IEBody A = nodes.get(i);

			ArrayList<IEBody> neighbors = qTree.getNodes(A);
			//ArrayList<IEBody> neighbors = new ArrayList<IEBody>();
			//neighbors.addAll(nodes);
			for(int j = 0; j < neighbors.size(); j++){
				IEBody B = (IEBody)neighbors.get(j);
				
				//don't do collision with self
				if (A == B)
					continue;
				
				//don't do collision between two static bodies
				if(A.shape.getMassData().getInvMass() == 0 && B.shape.getMassData().getInvMass() == 0)
					continue;
				
				//apply collision masks
				if(!A.collisionMask.testMask(B.collisionGroup))
					continue;
				
				IEManifold m = new IEManifold( A, B );
				m.Solve();
				if(m.contact_count != 0){
					if (m.a.getDoesCollisionResponse() && m.b.getDoesCollisionResponse()){
						contacts.add(m);
					}
					m.a.collisionOccured(m);
					m.b.collisionOccured(m);
				}
			}
		}

		// Integrate forces
		for(int i = 0; i < bodies.size( ); ++i)
			integrateForces( bodies.get(i), dt );

		// Initialize collision
		for(int i = 0; i < contacts.size( ); ++i)
			contacts.get(i).Initialize(dt);

		// Solve collisions
		for(int i = 0; i < contacts.size(); ++i){
			contacts.get(i).ApplyImpulse();
		}

		// Integrate velocities
		for(int i = 0; i < bodies.size( ); ++i)
			integrateVelocity( bodies.get(i), dt );

		// Correct positions
		for(int i = 0; i < contacts.size( ); ++i)
			contacts.get(i).PositionalCorrection( );

		// Clear all forces
		for(int i = 0; i < bodies.size( ); ++i)
		{
			IEBody b = bodies.get(i);
			b.force = new JGPoint();
			b.torque = 0;
		}
	}

	public void addBody(IEBody body){
		if (body != null && !bodies.contains(body)){
			bodies.add(body);
			body.world = this;
		}
	}

	public void removeBody(IEBody body){
		if (body != null && bodies.contains(body)){
			bodies.remove(body);
			body.world = null;
		}
	}

	private void integrateForces(IEBody b, float dt){
		if(b.shape.getMassData().getInvMass() == 0.0f)
			return;

		b.velocity = b.velocity.add(b.force.multScalar(b.shape.getMassData().getInvMass()).add(gravity).multScalar(dt / 2.0f));
		b.angularVelocity += b.torque * b.shape.getMassData().getInvInertia() * (dt / 2.0f);		
	}

	private void integrateVelocity(IEBody b, float dt){
		if(b.shape.getMassData().getInvMass() == 0.0f)
			return;

		b.setPosition(b.getPosition().add(b.velocity.multScalar(dt)));
		b.orient += b.angularVelocity * dt;
		b.setRotation(b.orient);
		integrateForces( b, dt );		
	}
	
	//////////////////////
	//Setters/Getters	//
	//////////////////////
	
	public static void setGravity(JGPoint g){
		gravity = g.multScalar(IEMath.gravityScale); 
	}
	
	public ArrayList<IEBody> getBodies(){ return bodies; }
	public static JGPoint getGravity(){ return gravity; }

}
