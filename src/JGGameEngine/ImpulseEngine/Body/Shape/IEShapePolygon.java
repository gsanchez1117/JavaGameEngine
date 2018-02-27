package JGGameEngine.ImpulseEngine.Body.Shape;

import java.awt.Color;
import java.awt.Graphics;

import JGGameEngine.JGBoundingBox;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.IEMath;
import JGGameEngine.ImpulseEngine.Body.IEMassData;

public class IEShapePolygon extends IEShape {

	public int vertexCount = 0;
	public JGPoint[] vertices = new JGPoint[MaxPolyVertexCount];
	public JGPoint[] normals = new JGPoint[MaxPolyVertexCount];
	private JGPoint centroid;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	public IEShapePolygon(float hw, float hh){
		constructAsBox(hw, hh);
		SetDensity(1.0f);
		u.set(0);
	}
	
	public IEShapePolygon(JGPoint[] verts){
		constructWithVerts(verts);
		SetDensity(1.0f);
		u.set(0);
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////

	public void SetDensity( float density ){
		// Calculate centroid and moment of inertia
		JGPoint c = new JGPoint(); // centroid
		float area = 0.0f;
		float I = 0.0f;
		float k_inv3 = 1.0f / 3.0f;

		for(int i1 = 0; i1 < vertexCount; ++i1){
			// Triangle vertices, third vertex implied as (0, 0)
			JGPoint p1 = vertices[i1];
			int i2 = (i1 + 1 < vertexCount) ? (i1 + 1) : 0;
			JGPoint p2 = vertices[i2];

			float D = (float)p1.cross(p2);
			float triangleArea = 0.5f * D;

			area += triangleArea;

			// Use area to weight the centroid average, not just vertex position
			c = c.add(p1.add(p2).multScalar(triangleArea * k_inv3));

			float intx2 = (float)(p1.x * p1.x + p2.x * p1.x + p2.x * p2.x);
			float inty2 = (float)(p1.y * p1.y + p2.y * p1.y + p2.y * p2.y);
			I += (0.25f * k_inv3 * D) * (intx2 + inty2);
		}

		c = c.multScalar(1.0f / area);
		if (centroid == null){
			centroid = c;

			// Translate vertices to centroid (make the centroid (0, 0)
			// for the polygon in model space)
			// Not really necessary, but I like doing this anyway
			for(int i = 0; i < vertexCount; ++i)
				vertices[i] = vertices[i].sub(c);
		}

		float mass = density * area;
		float inertia = I * density;		
		massData = new IEMassData(mass, inertia);
	}
	
	//////////////////////
	//Override Methods	//
	//////////////////////
	
	@Override
	public JGBoundingBox calculateBoundingBox(){
		float minX = (float)vertices[0].x, minY = (float)vertices[0].y;
		float maxX = (float)vertices[0].x, maxY = (float)vertices[0].y;
		for (int i = 0; i < vertexCount; i++){
			minX = (float)Math.min(minX, vertices[i].x);			
			minY = (float)Math.min(minY, vertices[i].y);
			
			maxX = (float)Math.max(maxX, vertices[i].x);			
			maxY = (float)Math.max(maxY, vertices[i].y);
		}
		return new JGBoundingBox(
				new JGPoint(minX,minY),
				new JGPoint(maxX,maxY)
		);
	}
	
	@Override
	public JGPoint getBoundingBoxTopLeft(){
		return centroid.negate();
	}
		
	@Override
	public void setOrient(float radians){
		u.set(radians);
	}

	@Override
	public void draw(Graphics g){
		
		int[] xPoints = new int[vertexCount];
		int[] yPoints = new int[vertexCount];
		for (int i = 0; i < vertexCount; i++){
			xPoints[i] = (int)vertices[i].x + (int)body.getContentSize().width/2;
			yPoints[i] = (int)vertices[i].y + (int)body.getContentSize().height/2;
		}
		JGGraphics.drawNodePolygon(body, xPoints, yPoints, 1, Color.white, col, g);

	}

	// The extreme point along a direction within a polygon
	public JGPoint GetSupport(JGPoint dir){
		float bestProjection = -Float.MAX_VALUE;
		JGPoint bestVertex = new JGPoint();

		for(int i = 0; i < vertexCount; ++i){
			JGPoint v = vertices[i];
			float projection = (float)v.dot(dir);

			if(projection > bestProjection){
				bestVertex = v;
				bestProjection = projection;
			}
		}
		return bestVertex;
	}

	public Type GetType(){
		return Type.poly;
	}

	@Override
	public IEShapePolygon clone(){
		IEShapePolygon poly = new IEShapePolygon(vertices);
		poly.u = u;
		for(int i = 0; i < vertexCount; ++i){
			poly.vertices[i] = vertices[i];
			poly.normals[i] = normals[i];
		}
		poly.vertexCount = vertexCount;
		return poly;	
	}
	
	//////////////////////
	//Private Methods	//
	//////////////////////
	
	// Half width and half height
	private void constructAsBox(float hw, float hh){
		vertexCount = 4;
		vertices[0] = new JGPoint( -hw, -hh );
		vertices[1] = new JGPoint(  hw, -hh );
		vertices[2] = new JGPoint(  hw,  hh );
		vertices[3] = new JGPoint( -hw,  hh );
		normals[0] = new JGPoint(  0.0f,  -1.0f );
		normals[1] = new JGPoint(  1.0f,   0.0f );
		normals[2] = new JGPoint(  0.0f,   1.0f );
		normals[3] = new JGPoint( -1.0f,   0.0f );
	}
	
	private void constructWithVerts(JGPoint[] Vertices){
		// No hulls with less than 3 vertices (ensure actual polygon)
		assert( Vertices.length > 2 && Vertices.length <= MaxPolyVertexCount ) : "IEPolygon Error : must have at least 3 verts";
		int count = Math.min(Vertices.length, MaxPolyVertexCount);

		// Find the right most point on the hull
		int rightMost = 0;
		float highestXCoord = (float)Vertices[0].x;
		for(int i = 1; i < count; i++){
			float x = (float)Vertices[i].x;
			if(x > highestXCoord){
				highestXCoord = x;
				rightMost = i;

				// If matching x then take farthest negative y
			}else if(x == highestXCoord){
				if(Vertices[i].y < Vertices[rightMost].y){
					rightMost = i;
				}
			}
		}

		int[] hull = new int[MaxPolyVertexCount];
		int outCount = 0;
		int indexHull = rightMost;

		for (;;){
			hull[outCount] = indexHull;

			// Search for next index that wraps around the hull
			// by computing cross products to find the most counter-clockwise
			// vertex in the set, given the previos hull index
			int nextHullIndex = 0;
			for(int i = 1; i < (int)count; ++i){
				// Skip if same coordinate as we need three unique
				// points in the set to perform a cross product
				if(nextHullIndex == indexHull){
					nextHullIndex = i;
					continue;
				}

				// Cross every set of three unique vertices
				// Record each counter clockwise third vertex and add
				// to the output hull
				// See : http://www.oocities.org/pcgpe/math2d.html
				JGPoint e1 = Vertices[nextHullIndex].sub(Vertices[hull[outCount]]);
				JGPoint e2 = Vertices[i].sub(Vertices[hull[outCount]]);
				float c = (float)e1.cross(e2);
				if(c < 0.0f){
					nextHullIndex = i;
				}

				// Cross product is zero then e vectors are on same line
				// therefor want to record vertex farthest along that line
				if(c == 0.0f && e2.lengthSQ() > e1.lengthSQ()){
					nextHullIndex = i;
				}
			}

			++outCount;
			indexHull = nextHullIndex;

			// Conclude algorithm upon wrap-around
			if(nextHullIndex == rightMost){
				vertexCount = outCount;
				break;
			}
		}

		// Copy vertices into shape's vertices
		for(int i = 0; i < vertexCount; ++i){
			vertices[i] = Vertices[hull[i]];
		}

		// Compute face normals
		for(int i1 = 0; i1 < vertexCount; ++i1)
		{
			int i2 = i1 + 1 < vertexCount ? i1 + 1 : 0;
			JGPoint face = vertices[i2].sub(vertices[i1]);

			// Ensure no zero-length edges, because that's bad
			assert( face.lengthSQ() > IEMath.EPSILON * IEMath.EPSILON ) : "IEPolygon Error : edges cannot be zero length";

			// Calculate normal with 2D cross product between vector and scalar
			normals[i1] = new JGPoint( face.y, -face.x );
			normals[i1] = normals[i1].normalize();
		}
	}
	
}
