package JGGameEngine.TileMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.Node.JGNode;

/** 

CCTiledMap parses and renders a tile map in the TMX format.

### Features:

- Each tile is treated as a CCSprite if needed, otherwise it's efficiently stored
- A tile is converted to CCSprite when you call [layer tileAt:] - doing so on every tile will greatly increase memory consumption!
- If a tile became a sprite it can be rotated / moved / scaled / tinted / fade out
- Tiles can be added/removed/replaced at runtime
- The zOrder of the tiles can be modified at runtime
- Each tile has an anchorPoint of (0,0)
- The anchorPoint of the TMXTileMap is (0,0)
- The Tiled map's layers create CCTiledMapLayer instances which are added as a child nodes of CCTiledMap
- The Tiled layer's tiles will be aliased by default
- Each tile will have a unique tag
- Each tile will have a unique z value. top-left: z=1, bottom-right: z=max z
- Each object group is stored in an NSMutableArray
- Properties can be assigned to the Map, Layer, Object Group, and Object

### Limitations:

- It only supports one tileset image per layer.
- Embedded images (image layers) are not supported.
- It only supports the XML format. The JSON format is not supported.
- Hexagonal tilemaps are not currently supported.

### Notes:

You can obtain the map's layers at runtime by:

   [map getChildByTag: tag_number];
   [map layerNamed: name_of_the_layer];

### Supported editors

- [Tiled Map Editor](http://www.mapeditor.org/)
- [iTileMaps](https://itunes.apple.com/app/itilemaps/id432784227)
- And others ...

*/

public class JGTiledMap extends JGNode {
	
	/// -----------------------------------------------------------------------
	/// @name Custom Types
	/// -----------------------------------------------------------------------

	public enum JGTiledMapOrientation{
		/** Orthogonal orientation. */
		JGTiledMapOrientationOrtho(0),
		
		/** Isometric orientation. */
		JGTiledMapOrientationIso(1);
		
		public int intValue;
		
		JGTiledMapOrientation(int iVal){
			intValue = iVal;
		}
		
		public int getIntValue(){ return intValue; }
	};
	
	/// -----------------------------------------------------------------------
	/// @name Instance Variables
	/// -----------------------------------------------------------------------

    
	/** Map size measured in tiles.*/
	public JGSize mapSize;

	/** Map Tile size measured in pixels. */
	public JGSize tileSize;
	
	/** Map file name */
	public String fileName;

	/** Map Orientation method.
	 @see CCTiledMapOrientation */
	public JGTiledMapOrientation mapOrientation;

	/** Object Groups contain the objects in a tilemap. */
	public ArrayList<Object> objectGroups;

	/** Tile Properties. */
	public HashMap<String, Object> properties;
	
    // Tile Properties.
	public HashMap<String, Object>	_tileProperties;
	
	/// -----------------------------------------------------------------------
	/// @name Constructors
	/// -----------------------------------------------------------------------

	public JGTiledMap(){
		this.setAnchorPoint(new JGPoint(0, 0));
	}
	/**
	 *  Initializes and returns a Tile Map object using the specified TMX file.
	 *
	 *  @param tmxFile TMX file to use.
	 *
	 *  @return An initialized CCTiledMap Object.
	 */
	public JGTiledMap(String tmxFile){
		this(); //call the default constructor
		assert(tmxFile != null) : "TMXTiledMap: tmx file should not be nil";

		this.setContentSize(new JGSize());

		fileName = tmxFile;
		JGTiledMapInfo mapInfo = new JGTiledMapInfo(tmxFile);

		assert( mapInfo.tilesets.size() != 0) : "TMXTiledMap: Map not found. Please check the filename.";
		this.buildWithMapInfo(mapInfo);
	}

	/**
	 *  Initializes and returns a Tile Map object using the specified TMX XML and path to TMX resources.
	 *
	 *  @param tmxString    TMX XML to use.
	 *  @param resourcePath TMX resource path.
	 *
	 *  @return The CCTiledMap Object.
	 */
	public JGTiledMap(String tmxString, String resourcePath){
		this(); //call the default constructor
		this.setContentSize(new JGSize());

		JGTiledMapInfo mapInfo = new JGTiledMapInfo(tmxString, resourcePath);

		assert( mapInfo.tilesets.size() != 0) : "TMXTiledMap: Map not found. Please check the filename.";
		this.buildWithMapInfo(mapInfo);
	}

	/// -----------------------------------------------------------------------
	/// @name Getting specific Layers, Objects and Properties
	/// -----------------------------------------------------------------------

	/**
	 *  Return the Tiled Map Layer specified by the layer name.
	 *
	 *  @param layerName Name of layer to lookup.
	 *
	 *  @return The CCTiledMapLayer object.
	 *  @see CCTiledMapLayer
	 */
	public JGTiledMapLayer layerNamed(String layerName){
	    for (JGNode aNode : this.getChildren()) {
			if(aNode instanceof JGTiledMapLayer){
				JGTiledMapLayer layer = (JGTiledMapLayer)aNode;
				if(layer.layerName.equals(layerName))
					return layer;
			}
		}

		// layer not found
		return null;
	}

	/** return the TMXObjectGroup for the specific group */

	/**
	 *  Return the Tiled Map Object Group specified by the object group name.
	 *
	 *  @param groupName Object group name to lookup.
	 *
	 *  @return The CCTiledMapObjectGroup object.
	 *  @see CCTiledMapObjectGroup
	 */
	public JGTiledMapObjectGroup objectGroupNamed(String groupName){
		for( Object obj : objectGroups ) {
			JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)obj;
			if( objectGroup.groupName.equals(groupName) )
				return objectGroup;
		}

		// objectGroup not found
		return null;
	}

	/**
	 *  Return the value held by the specified property name.
	 *
	 *  @param propertyName Property name to lookup.
	 *
	 *  @return The property value object.
	 */
	public Object propertyNamed(String propertyName){
		return properties.get(propertyName);
	}

	/** return properties dictionary for tile GID */

	/**
	 *  Return the properties dictionary for the specified tile GID.
	 *
	 *  @param GID GID to lookup.
	 *
	 *  @return The properties NSDictionary.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> propertiesForGID(int GID){
		return (HashMap<String, Object>)_tileProperties.get(Integer.toString(GID)); 
	}
	
	private void buildWithMapInfo(JGTiledMapInfo mapInfo){
		
		mapSize = mapInfo.mapSize;
		tileSize = mapInfo.tileSize;
		mapOrientation = mapInfo.orientation;
		objectGroups = mapInfo.objectGroups;
		properties = mapInfo.properties;
		_tileProperties = mapInfo.tileProperties;

		int idx=0;

		for( Object obj : mapInfo.layers ) {
			JGTiledMapLayerInfo layerInfo = (JGTiledMapLayerInfo)obj;
			if( layerInfo.visible ) {
				JGNode child = (JGNode)this.parseLayer(layerInfo, mapInfo);
	            String idxStr = "" + idx;
	            this.addChild(child);
	            child.setZOrder(idx);
	            child.name = idxStr;

				// update content size with the max size
				JGSize childSize = child.getContentSize();
				JGSize currentSize = this.getContentSize();

				currentSize.width = Math.max( currentSize.width, childSize.width );
				currentSize.height = Math.max( currentSize.height, childSize.height );
				this.setContentSize(currentSize);

				idx++;
			}
		}
	}
	
	// private
	private Object parseLayer(JGTiledMapLayerInfo layerInfo, JGTiledMapInfo mapInfo){
		JGTiledMapTilesetInfo tileset = this.tilesetForLayer(layerInfo, mapInfo);
		JGTiledMapLayer layer = new JGTiledMapLayer(tileset, layerInfo, mapInfo);

		// tell the layerinfo to release the ownership of the tiles map.
		layerInfo.ownTiles = false;

		layer.setupTiles();

		return layer;
	}
	
	private JGTiledMapTilesetInfo tilesetForLayer(JGTiledMapLayerInfo layerInfo, JGTiledMapInfo mapInfo){
		JGSize size = layerInfo.layerSize;
			
		ArrayList<Object> iter = new ArrayList<Object>(mapInfo.tilesets);
		Collections.reverse(iter);
		for( Object obj : iter) {
			JGTiledMapTilesetInfo tileset = (JGTiledMapTilesetInfo)obj;
			for( int y = 0; y < size.height; y++ ) {
				for( int x = 0; x < size.width; x++ ) {

					int pos = (int)(x + size.width * y);
					int gid = layerInfo.tiles[ pos ];


					// XXX: gid == 0 --> empty tile
					if( gid != 0 ) {
					
						// Optimization: quick return
						// if the layer is invalid (more than 1 tileset per layer) an assert will be thrown later
						if( gid >= tileset.firstGid ){
							return tileset;
						}
					}
				}
			}
		}

		// If all the tiles are 0, return empty tileset
		System.out.println("cocos2d: Warning: TMX Layer '"+layerInfo.name+"' has no tiles");
		return null;
	}

}
