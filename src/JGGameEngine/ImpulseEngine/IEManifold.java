package JGGameEngine.ImpulseEngine;

import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.ImpulseEngine.Body.Shape.IEShape;

public class IEManifold {

	public IEBody a, b;

	public float penetration = 0;     // Depth of penetration from collision
	public JGPoint normal = new JGPoint();          // From A to B
	public JGPoint[] contacts = new JGPoint[2];     // Points of contact during collision
	public int contact_count = 0; // Number of contacts that occured during collision
	public float e = 0;               // Mixed restitution
	public float df = 0;              // Mixed dynamic friction
	public float sf = 0;              // Mixed static friction

	public IEManifold(IEBody A, IEBody B){
		a = A;
		b = B;
	}

	public void Solve(){                // Generate contact information
		
		//
		//Circle vs Cirlce
		//
		if (a.shape.GetType() == IEShape.Type.cirlce && b.shape.GetType() == IEShape.Type.cirlce){
			IECollision.circleToCircle(this, a, b);
			
		//
		//Poly vs Circle
		//
		}else if (a.shape.GetType() == IEShape.Type.poly && b.shape.GetType() == IEShape.Type.cirlce){
			IECollision.polygonToCircle(this, a, b);
			
		//
		//Circle vs Poly
		//
		}else if (a.shape.GetType() == IEShape.Type.cirlce && b.shape.GetType() == IEShape.Type.poly){
			IECollision.circleToPolygon(this, a, b);
			
		//
		//Poly vs Poly
		//
		}else if (a.shape.GetType() == IEShape.Type.poly && b.shape.GetType() == IEShape.Type.poly){
			IECollision.PolygonToPolygon(this, a, b);
		}
	}
	
	public void Initialize(float dt){           // Precalculations for impulse solving
		  // Calculate average restitution
		  e = Math.min( a.getMaterial().getRestitution(), b.getMaterial().getRestitution() );

		  // Calculate static and dynamic friction
		  sf = (float)Math.sqrt( a.staticFriction * b.staticFriction );
		  df = (float)Math.sqrt( a.dynamicFriction * b.dynamicFriction );

		  for(int i = 0; i < contact_count; ++i){
			  // Calculate radii from COM to contact
			  JGPoint ra = contacts[i].sub(a.getPosition());
			  JGPoint rb = contacts[i].sub(b.getPosition());

			  JGPoint rv = b.velocity.add(IEMath.Cross(b.angularVelocity, rb)).sub(a.velocity).sub(IEMath.Cross(a.angularVelocity, ra));

		    // Determine if we should perform a resting collision or not
		    // The idea is if the only thing moving this object is gravity,
		    // then the collision should be performed without any restitution
		    if(rv.lengthSQ() < IEWorld.getGravity().multScalar(dt).lengthSQ() + IEMath.EPSILON){
		    	e = 0.0f;
		    }
		  }
	}

	public void ApplyImpulse(){          // Solve impulse and apply
		  // Early out and positional correct if both objects have infinite mass
		  if(IEMath.Equal( a.shape.getMassData().getInvMass() + b.shape.getMassData().getInvMass(), 0 )){
			  InfiniteMassCorrection();
			  return;
		  }

		  for(int i = 0; i < contact_count; i++){
		    // Calculate radii from COM to contact
		    JGPoint ra = contacts[i].sub(a.getPosition());
		    JGPoint rb = contacts[i].sub(b.getPosition());

		    // Relative velocity
		    JGPoint rv = b.velocity.add(IEMath.Cross(b.angularVelocity, rb)).sub(a.velocity).sub(IEMath.Cross(a.angularVelocity, ra));

			
		    // Relative velocity along the normal
		    float contactVel = (float)rv.dot(normal);

		    // Do not resolve if velocities are separating
		    if(contactVel > 0)
		    	return;

		    float raCrossN = (float)ra.cross(normal);
		    float rbCrossN = (float)rb.cross(normal);
		    float invMassSum = a.shape.getMassData().getInvMass() + b.shape.getMassData().getInvMass() + (raCrossN*raCrossN) * a.shape.getMassData().getInvInertia() + (rbCrossN*rbCrossN) * b.shape.getMassData().getInvInertia();

		    // Calculate impulse scalar
		    float j = -(1.0f + e) * contactVel;
		    j /= invMassSum;
		    j /= (float)contact_count;

		    // Apply impulse
		    JGPoint impulse = normal.multScalar(j);
		    a.ApplyImpulse( impulse.negate(), ra );
		    b.ApplyImpulse(  impulse, rb );

		    // Friction impulse
		    rv = b.velocity.add(IEMath.Cross(b.angularVelocity, rb)).sub(a.velocity).sub(IEMath.Cross(a.angularVelocity, ra));

		    
		    JGPoint t = rv.sub(normal.multScalar(rv.dot(normal)));
		    t = t.normalize();

		    // j tangent magnitude
		    float jt = -(float)rv.dot(t);
		    jt /= invMassSum;
		    jt /= (float)contact_count;

		    // Don't apply tiny friction impulses
		    if(IEMath.Equal( jt, 0.0f ))
		      return;

		    // Coulumb's law
		    JGPoint tangentImpulse = new JGPoint();
		    if(Math.abs( jt ) < j * sf)
		      tangentImpulse = t.multScalar(jt);
		    else
		      tangentImpulse = t.multScalar(-j).multScalar(df);
		    
		    // Apply friction impulse
		    a.ApplyImpulse( tangentImpulse.negate(), ra );
		    b.ApplyImpulse(  tangentImpulse, rb );
		  }
	}

	public void PositionalCorrection(){  // Naive correction of positional penetration
		  float k_slop = 0.05f; // Penetration allowance
		  float percent = 0.4f; // Penetration percentage to correct
		  JGPoint correction = normal.multScalar(percent).multScalar((Math.max( penetration - k_slop, 0.0f ) / (a.shape.getMassData().getInvMass() + b.shape.getMassData().getInvMass())));
		  a.setPosition(a.getPosition().sub(correction.multScalar(a.shape.getMassData().getInvMass())));
		  b.setPosition(b.getPosition().add(correction.multScalar(b.shape.getMassData().getInvMass())));
	}

	public void InfiniteMassCorrection(){
		  a.velocity = new JGPoint();
		  b.velocity = new JGPoint();
	}

}
