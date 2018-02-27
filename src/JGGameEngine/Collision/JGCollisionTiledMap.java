package JGGameEngine.Collision;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import JGGameEngine.JGPoint;
import JGGameEngine.ImpulseEngine.IEWorld;
import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.ImpulseEngine.Body.Shape.IEShapePolygon;
import JGGameEngine.Node.JGNode;
import JGGameEngine.TileMap.JGTiledMap;
import JGGameEngine.TileMap.JGTiledMapObjectGroup;

public class JGCollisionTiledMap extends JGTiledMap {
	
	private static final String COLLISIONLAYERNAME = "collision";
	
	private IEWorld collisionWorld;
	private JGTiledMapObjectGroup collisionGroup;
	
	public JGCollisionTiledMap(String mapName){
		super(mapName);										//call super constructor
		
		collisionGroup = objectGroupNamed(COLLISIONLAYERNAME);	//get the collision layer
		
		collisionWorld = new IEWorld();
		addChild(collisionWorld);
		
		loadCollisionObjects();
	}
	
	//////////////////////
	//Override Methods	//
	//////////////////////
	
	@Override
	public void Update(float dt){
		super.Update(dt);
				
		//step the collision world
		collisionWorld.step(dt);
				
	}
	
	@Override
	public void postChildDraw(float dt, Graphics g){
//		ArrayList<IEBody> bodies = collisionWorld.getBodies();
//		for (IEBody body : bodies){
//			JGBoundingBox bb = body.getBoundingBoxWorld();
//			JGGraphics.drawNodeRect(this.parent, bb.min, bb.size(), g);
//			//JGBoundingBox bb = body.getBoundingBox();
//			//JGGraphics.drawNodeRect(body, bb.min, bb.size(), g);
//		}
	}
	
	@Override
	public void addChild(JGNode aNode){
		super.addChild(aNode);  				//call super add child
		
		//if aNode is a collision node
		if (aNode instanceof IEBody){
			IEBody cn = (IEBody)aNode;
			if (!collisionWorld.getBodies().contains(cn))
				collisionWorld.addBody(cn);
		}
			
	}
	
	@Override
	public void removeChild(JGNode aNode){
		super.removeChild(aNode);  				//call super remove child
		
		//if aNode is a collision node
		if (aNode instanceof IEBody){
			IEBody cn = (IEBody)aNode;
			collisionWorld.removeBody(cn);
		}
	}
	
	//////////////////////
	//Private Methods	//
	//////////////////////
	
	@SuppressWarnings("unchecked")
	private void loadCollisionObjects(){

		for (Object obj : collisionGroup.objects){
			if (obj instanceof HashMap){
				HashMap<String, Object> dict = (HashMap<String, Object>)obj;
				
				int px = (int)dict.get("x");
				int py = (int)dict.get("y");
				
				//
				//Polygon
				//
				if (dict.containsKey("polygonPoints")){
					
					//get points for the polygon
					ArrayList<JGPoint> points = new ArrayList<JGPoint>();
					String pString = (String)dict.get("polygonPoints");
					Scanner scn = new Scanner(pString);
					while (scn.hasNext()){
						String pointString = scn.next();
						Scanner scn2 = new Scanner(pointString);
						scn2.useDelimiter(",");
						
						JGPoint pos = new JGPoint(
								(int)Float.parseFloat(scn2.next()),
								(int)Float.parseFloat(scn2.next())
						);
						points.add(pos);
						scn2.close();
					}
					scn.close();
					
					JGPoint[] pArr = new JGPoint[points.size()];
					for (int i = 0; i < points.size(); i++)
						pArr[i] = points.get(i);
					
					//polygons must have at least 3 points
					if (points.size() > 2){
						IEShapePolygon shape = new IEShapePolygon(pArr);
						IEBody body = new IEBody(shape);
						//body.SetStatic();
						body.setTopLeftPosition(new JGPoint(px, py));
						body.collisionGroup.setFlag(0);
						//body.name = "bob";
						//body.shouldDrawShape = true;
						//body.shouldDrawBoundingBox = true;
						addChild(body);
					}
				}
			}
		}
	}

}
