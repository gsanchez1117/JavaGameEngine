package JGGameEngine.AStar;

import JGGameEngine.JGPoint;
import JGGameEngine.TileMap.JGTiledMap;
import JGGameEngine.TileMap.JGTiledMapLayer;

public class JGAstar {
	
	static class JGSurr{
		JGPoint point;
		int cost;
		
		public JGSurr(int x, int y){
			point = new JGPoint(x, y);
			cost = x*x + y*y;
		}
	}
	
	private static final JGSurr surrounding[] = {
			new JGSurr(-1, 0),
			new JGSurr(0, 1),
			new JGSurr(1, 0),
			new JGSurr(0, -1),
			new JGSurr(-1, -1),
			new JGSurr(-1, 1),
			new JGSurr(1, 1),
			new JGSurr(1, -1)
	};
	
	public static JGSearchNode findPath(JGTiledMapLayer layer, JGPoint start, JGPoint end, boolean searchesDiag){
		return findPathReversed(layer, end, start, searchesDiag);
	}
	
	public static JGSearchNode findPathReversed(JGTiledMapLayer layer, JGPoint start, JGPoint end, boolean searchesDiag){
		
		int direcs = 4;
		if (searchesDiag)
			direcs = 8;
		
		JGSearchNode startNode = new JGSearchNode(start, 0, 0, null);
		JGMinHeap openList = new JGMinHeap();
		openList.add(startNode);
		
		JGTiledMap map = (JGTiledMap)layer.parent;
		boolean[][] closedList = new boolean[(int)map.mapSize.height][(int)map.mapSize.width];
		for (int y = 0; y < map.mapSize.height; y++){
			for (int x = 0; x < map.mapSize.width; x++){
				closedList[y][x] = false;
			}
		}
		closedList[(int)start.y][(int)start.x] = true;	//add the start coordinate to the closed list
		
		while (openList.hasNext()){
			JGSearchNode current = openList.extractFirst();
			
			//Found our way to the end of the path!!!!!!
			if (current.position.equalsINT(end)){
				return new JGSearchNode(end, current.pathCost + 1, current.cost + 1, current);
			}
			
			for (int i = 0; i < direcs; i++){
				JGSurr surr = surrounding[i];
				JGPoint tmp = current.position.add(surr.point);
				
				int GID = layer.tileGIDAt(tmp);
				if (GID <= 0 && closedList[(int)tmp.y][(int)tmp.x] == false){
					closedList[(int)tmp.y][(int)tmp.x] = true;
					int pathCost = current.pathCost + surr.cost;
					int cost = (int)(pathCost + tmp.distSQ(end));
					JGSearchNode node = new JGSearchNode(tmp, cost, pathCost, current);
					openList.add(node);
				}
			}
		}
		return null; //sadly.. no path was found... guess i'm not L33T :[
	}
	
	public static boolean lineOfSight(JGTiledMap map, JGPoint start, JGPoint end){
		
		if (start.equalsINT(end)){ return true; }
		
	    int x0 = (int)start.x;  
	    int y0 = (int)start.y;  
	    int x1 = (int)end.x;  
	    int y1 = (int)end.y;  
	  
	    int dx = (int)Math.abs(x1-x0);  
	    int dy = (int)Math.abs(y1-y0);  
	  
	    int sx;  
	    if(x0 < x1)  
	        sx = 1;  
	    else   
	        sx = -1;  
	  
	    int sy;  
	    if(y0 < y1)  
	        sy = 1;  
	    else   
	        sy = -1;  
	  
	    int err = dx-dy;  
	  
	    while(true){  
	   
	    	JGTiledMapLayer layer = map.layerNamed("pathMesh");
	        int GID = layer.tileGIDAt(new JGPoint(x0, y0)) - layer.tileset.firstGid;

	        if(GID == 1)  
	            return false;  
	  
	        if(x0 == x1 && y0 == y1)  
	            return true;  
	  
	        int e2 = 2*err;  
	        if(e2 > -dy){  
	            err = err - dy;  
	            x0 = x0 + sx;  
	        }  
	  
	        if(e2 <  dx){  
	            err = err + dx;  
	            y0 = y0 + sy;  
	        }  
	    }  
	} 

}
