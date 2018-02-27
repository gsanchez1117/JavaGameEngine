package JGGameEngine.ImpulseEngine;

import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.ImpulseEngine.Body.Shape.IEShapeCircle;
import JGGameEngine.ImpulseEngine.Body.Shape.IEShapePolygon;

public class IECollision {

	public static void circleToCircle(IEManifold m, IEBody a, IEBody b){
		IEShapeCircle A = (IEShapeCircle) a.shape;
		IEShapeCircle B = (IEShapeCircle) b.shape;

		// Calculate translational vector, which is normal
		JGPoint normal = b.getPosition().sub(a.getPosition());

		float dist_sqr = (float)normal.lengthSQ();
		float radius = A.radius + B.radius;

		// Not in contact
		if(dist_sqr >= radius * radius){
			m.contact_count = 0;
			return;
		}

		float distance = (float)Math.sqrt( dist_sqr );
		m.contact_count = 1;

		if(distance == 0.0f){
			m.penetration = A.radius;
			m.normal = new JGPoint( 1, 0 );
			m.contacts[0] = a.getPosition();
		}
		else
		{
			m.penetration = radius - distance;
			m.normal = normal.div(new JGPoint(distance, distance)); // Faster than using Normalized since we already performed sqrt
			m.contacts[0] = m.normal.multScalar(A.radius).add(a.getPosition());
		}
	}

	public static void circleToPolygon(IEManifold m, IEBody a, IEBody b){
		IEShapeCircle A = (IEShapeCircle)(a.shape);
		IEShapePolygon B = (IEShapePolygon)(b.shape);

		m.contact_count = 0;

		// Transform circle center to Polygon model space
		//Vec2 center = a->position;
		//center = B->u.Transpose( ) * (center - b->position);
		JGPoint center = a.getPosition();
		center = B.u.Transpose().mult(center.sub(b.getPosition()));


		// Find edge with minimum penetration
		// Exact concept as using support points in Polygon vs Polygon
		float separation = -Float.MAX_VALUE;
		int faceNormal = 0;
		for(int i = 0; i < B.vertexCount; ++i){
			float s = (float)B.normals[i].dot(center.sub(B.vertices[i]));

			if(s > A.radius)
				return;

			if(s > separation){
				separation = s;
				faceNormal = i;
			}
		}

		// Grab face's vertices
		JGPoint v1 = B.vertices[faceNormal];
		int i2 = (faceNormal + 1 < B.vertexCount) ? (faceNormal + 1) : 0;
		JGPoint v2 = B.vertices[i2];

		// Check to see if center is within polygon
		if(separation < IEMath.EPSILON){
			m.contact_count = 1;
			m.normal = B.u.mult(B.normals[faceNormal]).negate();
			m.contacts[0] = m.normal.multScalar(A.radius).add(a.getPosition());
			m.penetration = A.radius;
			return;
		}

		// Determine which voronoi region of the edge center of circle lies within
		float dot1 = (float)center.sub(v1).dot(v2.sub(v1));
		float dot2 = (float)center.sub(v2).dot(v1.sub(v2));
		m.penetration = A.radius - separation;

		// Closest to v1
		if(dot1 <= 0.0f){
			if(center.distSQ(v1) > (A.radius * A.radius))
				return;

			m.contact_count = 1;
			JGPoint n = v1.sub(center);
			n = B.u.mult(n);
			n = n.normalize();
			m.normal = n;
			v1 = B.u.mult(v1).add(b.getPosition());

			m.contacts[0] = v1;

			// Closest to v2
		}else if(dot2 <= 0.0f){
			if(center.distSQ(v2) > (A.radius * A.radius))
				return;

			m.contact_count = 1;
			JGPoint n = v2.sub(center);
			v2 = B.u.mult(v2).add(b.getPosition());
			m.contacts[0] = v2;
			n = B.u.mult(n);
			n = n.normalize();
			m.normal = n;


			// Closest to face
		}else{
			JGPoint n = B.normals[faceNormal];
			if(center.sub(v1).dot(n) > A.radius)
				return;

			n = B.u.mult(n);
			m.normal = n.negate();
			m.contacts[0] = m.normal.multScalar(A.radius).add(a.getPosition());
			m.contact_count = 1;
		}
	}

	public static void polygonToCircle(IEManifold m, IEBody a, IEBody b){
		circleToPolygon( m, b, a );
		m.normal = m.normal.negate();
	}

	public static JGPoint FindAxisLeastPenetration(int faceIndex, IEShapePolygon A, IEShapePolygon B){
		float bestDistance = -Float.MAX_VALUE;
		int bestIndex = 0;

		for(int i = 0; i < A.vertexCount; ++i){
			// Retrieve a face normal from A
			JGPoint n = A.normals[i];
			JGPoint nw = A.u.mult(n);

			// Transform face normal into B's model space
			IEMat2 buT = B.u.Transpose();
			n = buT.mult(nw);

			// Retrieve support point from B along -n
			JGPoint s = B.GetSupport(n.negate());

			// Retrieve vertex on face from A, transform into
			// B's model space
			JGPoint v = A.vertices[i];
			v = A.u.mult(v).add(A.body.getPosition());
			v = v.sub(B.body.getPosition());
			v = buT.mult(v);

			// Compute penetration distance (in B's model space)
			float d = (float)n.dot(s.sub(v));

			// Store greatest distance
			if(d > bestDistance){
				bestDistance = d;
				bestIndex = i;
			}
		}

		return new JGPoint(bestIndex, bestDistance);
	}
	
	public static void FindIncidentFace( JGPoint[] v, IEShapePolygon RefPoly, IEShapePolygon IncPoly, int referenceIndex){
	  JGPoint referenceNormal = RefPoly.normals[referenceIndex];

	  // Calculate normal in incident's frame of reference
	  referenceNormal = RefPoly.u.mult(referenceNormal); // To world space
	  referenceNormal = IncPoly.u.Transpose().mult(referenceNormal); // To incident's model space

	  // Find most anti-normal face on incident polygon
	  int incidentFace = 0;
	  float minDot = Float.MAX_VALUE;
	  for(int i = 0; i < IncPoly.vertexCount; ++i)
	  {
	    float dot = (float)referenceNormal.dot(IncPoly.normals[i]);
	    if(dot < minDot)
	    {
	      minDot = dot;
	      incidentFace = i;
	    }
	  }

	  // Assign face vertices for incidentFace
	  v[0] = IncPoly.u.mult(IncPoly.vertices[incidentFace]).add(IncPoly.body.getPosition());
	  incidentFace = incidentFace + 1 >= (int)IncPoly.vertexCount ? 0 : incidentFace + 1;
	  v[1] = IncPoly.u.mult(IncPoly.vertices[incidentFace]).add(IncPoly.body.getPosition());
	}
	
	public static int Clip(JGPoint n, float c, JGPoint[] face){
	  int sp = 0;
	  JGPoint[] out = {
	    face[0],
	    face[1]
	  };

	  // Retrieve distances from each endpoint to the line
	  // d = ax + by - c
	  float d1 = (float)n.dot(face[0]) - c;
	  float d2 = (float)n.dot(face[1]) - c;

	  // If negative (behind plane) clip
	  if(d1 <= 0.0f) out[sp++] = face[0];
	  if(d2 <= 0.0f) out[sp++] = face[1];
	  
	  // If the points are on different sides of the plane
	  if(d1 * d2 < 0.0f) // less than to ignore -0.0f
	  {
	    // Push interesection point
		  float alpha = d1 / (d1 - d2);
	    out[sp] = face[0].add(face[1].sub(face[0]).multScalar(alpha));
	    ++sp;
	  }

	  // Assign our new converted values
	  face[0] = out[0];
	  face[1] = out[1];

	  assert( sp != 3 );

	  return sp;
	}
	
	public static void PolygonToPolygon(IEManifold m, IEBody a, IEBody b){
	  IEShapePolygon A = (IEShapePolygon)(a.shape);
	  IEShapePolygon B = (IEShapePolygon)(b.shape);
	  m.contact_count = 0;

	  // Check for a separating axis with A's face planes
	  int faceA = 0;
	  JGPoint packet = FindAxisLeastPenetration( faceA, A, B );
	  faceA = (int)packet.x;
	  float penetrationA = 	(float)packet.y;				
	  if(penetrationA >= 0.0f)
	    return;

	  // Check for a separating axis with B's face planes
	  int faceB = 0;
	  packet = FindAxisLeastPenetration( faceB, B, A );
	  faceB = (int)packet.x;
	  float penetrationB = (float)packet.y;				
	  if(penetrationB >= 0.0f)
	    return;

	  int referenceIndex;
	  boolean flip; // Always point from a to b

	  IEShapePolygon RefPoly; // Reference
	  IEShapePolygon IncPoly; // Incident

	  // Determine which shape contains reference face
	  if(IEMath.BiasGreaterThan( penetrationA, penetrationB )){
	    RefPoly = A;
	    IncPoly = B;
	    referenceIndex = faceA;
	    flip = false;
	  }

	  else
	  {
	    RefPoly = B;
	    IncPoly = A;
	    referenceIndex = faceB;
	    flip = true;
	  }

	  // World space incident face
	  JGPoint[] incidentFace = new JGPoint[2];
	  FindIncidentFace( incidentFace, RefPoly, IncPoly, referenceIndex );

	  //        y
	  //        ^  ->n       ^
	  //      +---c ------posPlane--
	  //  x < | i |\
	  //      +---+ c-----negPlane--
	  //             \       v
	  //              r
	  //
	  //  r : reference face
	  //  i : incident poly
	  //  c : clipped point
	  //  n : incident normal

	  // Setup reference face vertices
	  JGPoint v1 = RefPoly.vertices[referenceIndex];
	  referenceIndex = referenceIndex + 1 == RefPoly.vertexCount ? 0 : referenceIndex + 1;
	  JGPoint v2 = RefPoly.vertices[referenceIndex];

	  // Transform vertices to world space
	  v1 = RefPoly.u.mult(v1).add(RefPoly.body.getPosition());
	  v2 = RefPoly.u.mult(v2).add(RefPoly.body.getPosition());

	  // Calculate reference face side normal in world space
	  JGPoint sidePlaneNormal = v2.sub(v1);
	  sidePlaneNormal = sidePlaneNormal.normalize();

	  // Orthogonalize
	  JGPoint refFaceNormal = new JGPoint( sidePlaneNormal.y, -sidePlaneNormal.x );

	  // ax + by = c
	  // c is distance from origin
	  float refC = (float)refFaceNormal.dot(v1);
	  float negSide = -(float)sidePlaneNormal.dot(v1);
	  float posSide =  (float)sidePlaneNormal.dot(v2);

	  // Clip incident face to reference face side planes
	  if(Clip( sidePlaneNormal.negate(), negSide, incidentFace ) < 2)
	    return; // Due to floating point error, possible to not have required points

	  if(Clip(  sidePlaneNormal, posSide, incidentFace ) < 2)
	    return; // Due to floating point error, possible to not have required points

	  // Flip
	  m.normal = flip ? refFaceNormal.negate() : refFaceNormal;

	  // Keep points behind reference face
	  int cp = 0; // clipped points behind reference face
	  float separation = (float)refFaceNormal.dot(incidentFace[0]) - refC;
	  if(separation <= 0.0f){
	    m.contacts[cp] = incidentFace[0];
	    m.penetration = -separation;
	    ++cp;
	  }
	  else
	    m.penetration = 0;

	  separation = (float)refFaceNormal.dot(incidentFace[1]) - refC;
	  if(separation <= 0.0f){
	    m.contacts[cp] = incidentFace[1];

	    m.penetration += -separation;
	    ++cp;

	    // Average penetration
	    m.penetration /= (float)cp;
	  }

	  m.contact_count = cp;
	}
	
}
