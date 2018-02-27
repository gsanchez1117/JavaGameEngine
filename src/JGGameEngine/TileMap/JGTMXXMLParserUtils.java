package JGGameEngine.TileMap;

public class JGTMXXMLParserUtils {
	
	public enum TMXLayerAttrib{
		TMXLayerAttribNone((byte)(1 << 0)),
		TMXLayerAttribBase64((byte)(1 << 1)),
		TMXLayerAttribGzip((byte)(1 << 2)),
		TMXLayerAttribZlib((byte)(1 << 3));
		
		private byte byteValue;
		
		TMXLayerAttrib(byte bVal){
			byteValue = bVal;
		}
		
		public byte getByteValue(){ return byteValue; }
	}
	
	public enum TMXProperty {
		TMXPropertyNone(0),
		TMXPropertyMap(1),
		TMXPropertyLayer(2),
		TMXPropertyObjectGroup(3),
		TMXPropertyObject(4),
		TMXPropertyTile(5);
		
		public int intValue;
		
		TMXProperty(int iVal){
			intValue = iVal;
		}
		
		public int getIntValue(){ return intValue; }
	};
	
	public enum jgTMXTileFlags{
	    /** Tile is flipped horizontally. */
		kCCTMXTileHorizontalFlag(0x80000000),
	    /** Tile is flipped vertically. */
		kCCTMXTileVerticalFlag(0x40000000),
	    /** Tile is flipped diagonally. */
		kCCTMXTileDiagonalFlag(0x20000000),

	    /** All flags are set. */
		kCCFlipedAll(0x80000000|0x40000000|0x20000000),
	    /** None of the flags are set (mask). */
		kCCFlippedMask(~(0x80000000|0x40000000|0x20000000));
		
		private int intValue;
		
		jgTMXTileFlags(int iVal){
			intValue = iVal;
		}
		
		public int getIntValue(){ return intValue; }
	}
	
}
