package JGGameEngine.TileMap;

import java.util.HashMap;

import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;

/**
 *  CCTiledMapLayerInfo contains information about a Tiled Map Layer.
 */
public class JGTiledMapLayerInfo{
	
	/// -----------------------------------------------------------------------
	/// @name Tiled Map Layer Properties
	/// -----------------------------------------------------------------------

	/** Layer name. */
	public String name;

	/** Layer size, in tiles. */
	public JGSize layerSize;

	/** Layer offset position. */
	public JGPoint offset;

	/// -----------------------------------------------------------------------
	/// @name Layer Tiles
	/// -----------------------------------------------------------------------

	/** Layer tile memory buffer, ie an array of uint representing tile GIDs. */
	public int[] tiles;

	/** YES if the layer owns the memory of its tiles property. Defaults to YES */
	public boolean ownTiles;

	/** Lowest GID used on this layer. */
	public int minGID;

	/** Highest GID used on this layer. */
	public int maxGID;

	/// -----------------------------------------------------------------------
	/// @name Layer Rendering Properties
	/// -----------------------------------------------------------------------

	/** Layer visibility. */
	public boolean visible = true; //visible by default

	/** Layer opacity. */
	public float opacity = 1.0f; //fully visible by default

	/// -----------------------------------------------------------------------
	/// @name Layer Properties Dictionary
	/// -----------------------------------------------------------------------

	/** Properties dictionary. */
	public HashMap<String, Object> properties;
	
	//
	//Default Constructor
	//
	public JGTiledMapLayerInfo(){
		ownTiles = true;
		minGID = 100000;
		maxGID = 0;
		name = null;
		tiles = null;
		offset = new JGPoint();
		properties = new HashMap<String, Object>(5);
	}
}