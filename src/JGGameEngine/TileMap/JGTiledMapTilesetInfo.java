package JGGameEngine.TileMap;

import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.JGSize;
import JGGameEngine.TileMap.JGTMXXMLParserUtils.jgTMXTileFlags;

/**
CCTiledMapTilesetInfo contains information about the Tile Map's Tileset.
*/
public class JGTiledMapTilesetInfo{
	
	/// -----------------------------------------------------------------------
	/// @name Tileset Properties
	/// -----------------------------------------------------------------------

	/** Tileset name. */
	public String name;

	/** Lowest GID defined by this tileset. */
	public  int firstGid;
	
	/** The number of tiles in the current tileset */
	public int tileCount;

	/** Content scale of the TMX file. Mostly for backwords compatibility. */
	public float contentScale;

	/// -----------------------------------------------------------------------
	/// @name Tileset Image Properties
	/// -----------------------------------------------------------------------

	/** Tileset spacing. */
	public int spacing;

	/** Tileset margin. */
	public int margin;

	/** Tileset source texture, should be spritesheet. */
	public String sourceImage;

	/** Size of image in pixels. */
	public JGSize imageSize;

	/// -----------------------------------------------------------------------
	/// @name Tile Properties
	/// -----------------------------------------------------------------------

	/** Tileset size. */
	public JGSize tileSize;

	/** Tileset offset in pixels. */
	public JGPoint tileOffset;

	/** Auto set when tileOffset is modified. */
	public JGPoint tileAnchorPoint;
	
	//
	//Default Contructor
	//
	public JGTiledMapTilesetInfo(){}

	/// -----------------------------------------------------------------------
	/// @name Obtaining a Texture Rectangle for a given GID
	/// -----------------------------------------------------------------------

	/**
	 *  Return rectangle for GID value.
	 *
	 *  @param gid GID value to use.
	 *
	 *  @return CGRect.
	 */
	public JGRect rectForGID(int gid){
		JGRect rect = new JGRect();
		rect.size = tileSize;

		gid &= jgTMXTileFlags.kCCFlippedMask.getIntValue();
		gid = gid - firstGid;
		
		int max_x = (int)((imageSize.width - margin*2 + spacing) / (tileSize.width + spacing));
		//	int max_y = (imageSize.height - margin*2 + spacing) / (tileSize.height + spacing);

		rect.origin.x = (gid % max_x) * (tileSize.width + spacing) + margin;
		rect.origin.y = (gid / max_x) * (tileSize.height + spacing) + margin;

		return rect;
	}
	
	/**
	 Custom Setter:
	 Sets the _tileOffset property (expressed in pixels) as specified in the TMX file (should be double for retina).
	 Then it calculates the anchorPoint for the tiles (expressed as %), and stores it in the _tileAnchorPoint readonly property.
	 The CCTMXLayer is then responsible for  setting the anchorPoint of its tile sprites.
	 (implemented in -[CCTMXLayer setupTileSprite:position:withGID:])
	 */
//	private void setTileOffset(JGPoint tos){
//		tileOffset = tos;
//		assert((this.tileSize.width > 0 && this.tileSize.height > 0)) : "Error in [CCTMXTilesetInfo setTileOffset:], tileSize is Zero";
//		float normalizedOffsetX = (float)tileOffset.x / (float)tileSize.width;
//		float normalizedOffsetY = (float)tileOffset.y / (float)tileSize.height;
//		tileAnchorPoint = new JGPoint(normalizedOffsetX, normalizedOffsetY);
//	}
}
