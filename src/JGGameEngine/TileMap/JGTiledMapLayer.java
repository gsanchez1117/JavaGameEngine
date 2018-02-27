package JGGameEngine.TileMap;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.JGSize;
import JGGameEngine.JGUtilities;
import JGGameEngine.Node.JGTextureNode;
import JGGameEngine.TileMap.JGTMXXMLParserUtils.jgTMXTileFlags;
import JGGameEngine.TileMap.JGTiledMap.JGTiledMapOrientation;

public class JGTiledMapLayer extends JGTextureNode {
	
	// Number of rows and columns of tiles in the layer.
	private int _mapRows, _mapColumns;
	
	// Size of a tile in points.
	private int _tileWidth, _tileHeight;
	
		
	/** Name of the tile layer. */
	public String layerName;

	/** Size of the Map's tiles, could be different from the tile size but typically is the same. */
	public JGSize mapTileSize;

	/** Layer orientation. Is always the same as the map's orientation.
	 @see CCTiledMapOrientation */
	public JGTiledMapOrientation layerOrientation;

	
	/** Tile pointer. */
	public int[] tiles;

	/** Tileset information for the layer.
	 @see CCTiledMapTilesetInfo */
	public JGTiledMapTilesetInfo tileset;

	
	/** Properties from the layer. They can be added using tiled. */
	public HashMap<String, Object> properties;


	/// -----------------------------------------------------------------------
	/// @name Creating a Tiled Map Layer
	/// -----------------------------------------------------------------------

	/**
	 *  Initializes and returns a CCTiledMapLayer using the specified tileset info, layerinfo and mapinfo values.
	 *
	 *  @param tilesetInfo Tileset Info to use.
	 *  @param layerInfo   Layer Info to use.
	 *  @param mapInfo     Map Info to use.
	 *
	 *  @return An initialized CCTiledMapLayer Object.
	 *  @see CCTiledMapTilesetInfo
	 *  @see CCTiledMapLayerInfo
	 *  @see CCTiledMapInfo
	 */
	public JGTiledMapLayer(JGTiledMapTilesetInfo tilesetInfo, JGTiledMapLayerInfo layerInfo, JGTiledMapInfo mapInfo){
		JGSize size = layerInfo.layerSize;

		if( tilesetInfo != null ){
			LoadTexture(tilesetInfo.sourceImage);
		}
		
		// layerInfo
		layerName = layerInfo.name;
		_mapColumns = (int)size.width;
		_mapRows = (int)size.height;
		tiles = layerInfo.tiles;
		this.setOpacity(layerInfo.opacity); 
		properties = new HashMap<String, Object>(layerInfo.properties);
		
		// tilesetInfo
		tileset = tilesetInfo;

		// mapInfo
		_tileWidth = (int)mapInfo.tileSize.width;
		_tileHeight = (int)mapInfo.tileSize.height;
		layerOrientation = mapInfo.orientation;
		
		// offset (after layer orientation is set);
		JGPoint offset = calculateLayerOffset(layerInfo.offset);
		this.setPosition(offset);

		this.setContentSize(new JGSize(_mapColumns * _tileWidth , _mapRows * _tileHeight ));
		this.setAnchorPoint(new JGPoint(0, 0));

	}
	
	// ---------------------------------------
	// Override Methods
	// ---------------------------------------
	
	@Override
	protected void midDraw(float dt, Graphics g){

		if (getGameWindow() != null){
			
			if (parent instanceof JGTiledMap){
				
				JGTiledMap tileMap = (JGTiledMap)parent;
				BufferedImage bi = getTexture();
				JGSize tileSize = tileMap.tileSize;
				JGRect drawRect = getDrawRect();

				for (int y = (int)drawRect.origin.y; y <= (int)drawRect.origin.y + (int)drawRect.size.height; y++){
					for (int x = (int)drawRect.origin.x; x <= (int)drawRect.origin.x + (int)drawRect.size.width; x++){
						
						int gid = tileGIDAt(new JGPoint(x,y));
						gid -= tileset.firstGid-1;
						gid -=1;
						
						
						//FIX: this fix solved a problem where tile GID's were not rounded correctly on certain systems
						gid = (int)JGUtilities.clampf(gid, -1, this.tileset.tileCount);
						
						//only draw visible tiles
						if (gid >= 0){
						
							int gy = (int)gid/(int)(bi.getWidth()/tileSize.width);
							int gx = gid - gy * (int)(bi.getWidth()/tileSize.width);
							
							gx *= tileSize.width;
							gy *= tileSize.height;
							JGGraphics.drawTliedLayer(this, 
									(int)(x*tileSize.width), //dx1
									(int)(y*tileSize.height), //dy1
									
									(int)(x*tileSize.width) + (int)tileSize.width, //dx2
									(int)(y*tileSize.height) + (int)tileSize.height, //dy2
									
									gx, 
									gy, 
									gx + (int)tileSize.width,   //sx2
									gy + (int)tileSize.height,  //sy2
									g);
						
						}
					}
				}	
			}	
		}
	}


	/// -----------------------------------------------------------------------
	/// @name Modifying Tiles by Global Identifier (GID)
	/// -----------------------------------------------------------------------

	/**
	 *  Returns the tile GID at the specified tile coordinates.
	 *
	 *  @param tileCoordinate Tile Coordinate to use.
	 *
	 *  @return Tile GID value.
	 */
	public int tileGIDAt(JGPoint pos){
		return tileGIDAt(pos, null);
	}

	/*
	 *  Returns the tile GID using the specified tile coordinates and flag options.
	 *
	 *  @param pos   Tile Coordinate to use.
	 *  @param flags Flags options to use.
	 *
	 *  @return Tile GID value.
	 *  @see ccTMXTileFlags
	 */
	public int tileGIDAt(JGPoint pos, jgTMXTileFlags flags){
		assert( pos.x < _mapColumns && pos.y < _mapRows && pos.x >=0 && pos.y >=0) : "TMXLayer: invalid position";
		assert( tiles != null) : "TMXLayer: the tiles map has been released";

		int idx = (int)pos.x + (int)pos.y*_mapColumns;
		int tile = tiles[idx];
		return tile;
	}

	/**
	 *  Sets the tile GID using the specified tile coordinates and GID value.
	 *
	 *  @param gid            GID value to use.
	 *  @param tileCoordinate Tile Coordinate to use.
	 */
	public void setTileGID(int gid, JGPoint pos){
		setTileGID(gid, pos, null);
	}

	/**
	 *  Sets the tile GID using the specified GID value, tile coordinates and flag option values.
	 *
	 *  @param gid   GID value to use.
	 *  @param pos   Tile Coordinate to use.
	 *  @param flags Flag options to use.
	 *  @see ccTMXTileFlags
	 */
	public void setTileGID(int gid, JGPoint pos, jgTMXTileFlags flags){
		assert( pos.x < _mapColumns && pos.y < _mapRows && pos.x >=0 && pos.y >=0 ) : "TMXLayer: invalid position";
		assert( tiles != null ) : "TMXLayer: the tiles map has been released";
		assert( gid == 0 || gid >= tileset.firstGid) : "TMXLayer: invalid gid";

		int currentGID = tileGIDAt(pos);
		
		if(currentGID != gid){
			int idx = (int)pos.x + (int)pos.y*_mapColumns;
			tiles[idx] = gid;
		}
	}

	/**
	 *  Remove tile at specified tile coordinates.
	 *
	 *  @param tileCoordinate Tile Coordinate to use.
	 */
	public void removeTileAt(JGPoint pos){
		assert( pos.x < _mapColumns && pos.y < _mapRows && pos.x >=0 && pos.y >=0) : "TMXLayer: invalid position";
		assert( tiles != null ) : "TMXLayer: the tiles map has been released";

		int gid = tileGIDAt(pos);

		if( gid > 0 ) {
			int idx = (int)pos.x + (int)pos.y * _mapColumns;
			tiles[idx] = 0;
		}
	}


	/// -----------------------------------------------------------------------
	/// @name Converting Coordinates
	/// -----------------------------------------------------------------------

	/**
	 *  Return the position in points of the tile specified by the tile coordinates.
	 *
	 *  @param tileCoordinate Tile Coordinate to use.
	 *
	 *  @return Return position of tile.
	 */
	public JGPoint positionAt(JGPoint tileCoordinate){
		
		switch(layerOrientation){
			case JGTiledMapOrientationOrtho:{
				return new JGPoint(Math.floor(tileCoordinate.x) * _tileWidth, 
								   Math.floor(tileCoordinate.y) * _tileHeight);
			}case JGTiledMapOrientationIso:{
				return null; //XXX: need to add implementation for isometric maps
			}
		}
		return null;
	}

	/**
	 *  Return the position in tile coordinates of the tile specified by position in points.
	 *
	 *  @param position Position in points.
	 *
	 *  @return Coordinate of the tile at that position.
	 */
	public JGPoint tileCoordinateAt(JGPoint position){
		
		switch(layerOrientation){
			case JGTiledMapOrientationOrtho:{
				int x = (int)(position.x / _tileWidth);
				int y = (int)(position.y / _tileHeight);
				
				x = (int)JGUtilities.clampf(x, 0, _mapColumns-1);
				y = (int)JGUtilities.clampf(y, 0, _mapRows-1);
				
				return new JGPoint(x, 
								   y);
				
			}case JGTiledMapOrientationIso:{
				return null; //XXX: need to add implementation for isometric maps
			}
		}
		return null;
	}

	/// -----------------------------------------------------------------------
	/// @name Accessing Tiled Layer Properties
	/// -----------------------------------------------------------------------

	/**
	 *  Return the value for the specified property name value.
	 *
	 *  @param propertyName Propery name to lookup.
	 *
	 *  @return Property name value.
	 */
	public Object propertyNamed(String propertyName){
		return properties.get(propertyName);
	}


	// purposefully undocumented: users should not use this method
	/*
	 *  @warning addchild:z:tag: is not supported on CCTMXLayer.  Instead use setTileGID:at: and tileAt: methods.
	 *
	 *  @param node Node to use.
	 *  @param z    Z value to use.
	 *  @param tag  Tag to use.
	 *  @see CCNode
	 */
//	@Override
//	public void addChild(JGNode node, int z, int tag){
//		assert(false) : "addChild: is not supported on CCTMXLayer. Instead use setTileGID:at:/tileAt:";
//	}
	
	
	// -----------------------------------------------------------------------
	// Private/Protected Methods
	// -----------------------------------------------------------------------
	
	private JGRect getDrawRect(){
		
		if (getGameWindow() == null){
			return new JGRect();
		}
		
		Rectangle rect = getGameWindow().getBounds();
		JGPoint topLeftNS = convertToNodeSpace(new JGPoint(rect.getMinX(), rect.getMinY()));
		JGPoint bottomRightNS = convertToNodeSpace(new JGPoint(rect.getMaxX(), rect.getMaxY()));
		
		
		JGRect ret = new JGRect(
				this.tileCoordinateAt(topLeftNS),
				this.tileCoordinateAt(bottomRightNS)
				//new JGPoint(),
				//new JGPoint(_mapColumns-1, _mapRows-1)
		);
		//System.out.println("DrawRect:" + ret);
		return ret;
	}
	
	private JGPoint calculateLayerOffset(JGPoint pos){
		switch( layerOrientation ) {
			case JGTiledMapOrientationOrtho:
				return new JGPoint( pos.x * _tileWidth, pos.y *_tileHeight); 
			case JGTiledMapOrientationIso:
				return new JGPoint(
					(_tileWidth /2) * (pos.x - pos.y),
					(_tileHeight /2 ) * (-pos.x - pos.y)
				);
			default: return new JGPoint();
		}
	}
	
	protected void setupTiles(){
		// Optimization: quick hack that sets the image size on the tileset
		tileset.imageSize = new JGSize(this.getTexture().getWidth(), this.getTexture().getHeight());
	}
	
	// -----------------------------------------------------------------------
	// Getters
	// -----------------------------------------------------------------------
	
	public JGSize getLayerSize(){ return new JGSize(_mapColumns, _mapRows); }
	public JGSize getMapTileSize(){ return new JGSize(_tileWidth, _tileHeight); }
	
	
	// -----------------------------------------------------------------------
	// Setters
	// -----------------------------------------------------------------------
	
	public void setLayerSize(JGSize layerSize){
		_mapColumns = (int)layerSize.width;
		_mapRows = (int)layerSize.height;
	}
	
	public void setMapTileSize(JGSize mapTileSize){
		_tileWidth = (int)mapTileSize.width;
		_tileHeight = (int)mapTileSize.height;
	}
		
}
