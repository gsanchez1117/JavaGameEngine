package JGGameEngine.TileMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.JGUtilities;
import JGGameEngine.Support.ZipUtils;
import JGGameEngine.Support.base64;
import JGGameEngine.TileMap.JGTMXXMLParserUtils.TMXLayerAttrib;
import JGGameEngine.TileMap.JGTMXXMLParserUtils.TMXProperty;
import JGGameEngine.TileMap.JGTiledMap.JGTiledMapOrientation;;;

/**
 *  CCTiledMapInfo contains information about the Tiled Map.
 */
public class JGTiledMapInfo extends DefaultHandler{
	
	private StringBuilder		_currentString;
	private boolean				_storingCharacters;
	private int					_layerAttribs;
	private int					_parentElement;
	private int					_parentGID;
	private int					_currentFirstGID;
	
	/// -----------------------------------------------------------------------
	/// @name Tiled Map Properties
	/// -----------------------------------------------------------------------

	/** Map orientation.
	 @see CCTiledMapOrientation */
	public JGTiledMapOrientation orientation;

	/** Map size. */
	public JGSize mapSize = new JGSize();

	/** Map tile size. */
	public JGSize tileSize = new JGSize();

	/** Content scale of the TMX file. Mostly for backwards compatibility. */
	public float contentScale = 1.0f;

	/// -----------------------------------------------------------------------
	/// @name Accessing Tiled Map Contents
	/// -----------------------------------------------------------------------

	/** Map layers array. */
	public ArrayList<Object> layers;

	/** Map tileset array. */
	public ArrayList<Object> tilesets;

	/** Object groups. */
	public ArrayList<Object> objectGroups;

	/** Properties dictionary. */
	public HashMap<String, Object> properties;

	// Tile properties dictionary. */
	public HashMap<String, Object> tileProperties;

	/// -----------------------------------------------------------------------
	/// @name Accessing Tiled Map Resource Files
	/// -----------------------------------------------------------------------

	/** Tile Map file path. */
	public String filename;

	/** Tile Map resource file path. */
	public String resources;
	
	
	/// -----------------------------------------------------------------------
	/// @name Creating a Tiled Map Info
	/// -----------------------------------------------------------------------
	
	/**
	 *  Initializes and returns a CCTiledMapInfo object using the TMX format file specified.
	 *
	 *  @param tmxFile CCTiledMapInfo.
	 *
	 *  @return An initialized CCTiledMapInfo Object.
	 */
	public JGTiledMapInfo(String tmxFile){
		internalInit(tmxFile, null);
		parseXMLFile(tmxFile);
	}
	
	/**
	 *   Initializes and returns a  CCTiledMapInfo object using the TMX XML and resource file path.
	 *
	 *  @param tmxString    TMX XML.
	 *  @param resourcePath Resource file path.
	 *
	 *  @return An initialized CCTiledMapInfo Object.
	 */
	public JGTiledMapInfo(String tmxString, String resourcePath){
		internalInit(null, resourcePath);
		parseXMLString(tmxString);
	}

	private void internalInit(String tmxFileName, String resourcePath){
		
		this.tilesets = new ArrayList<Object>(4);//[NSMutableArray arrayWithCapacity:4];
		this.layers = new ArrayList<Object>(4); //[NSMutableArray arrayWithCapacity:4];
		this.filename = tmxFileName;
		this.resources = resourcePath;
		this.objectGroups = new ArrayList<Object>(4); //[NSMutableArray arrayWithCapacity:4];
		this.properties = new HashMap<String, Object>(5); //[NSMutableDictionary dictionaryWithCapacity:5];
		this.tileProperties = new HashMap<String, Object>(5); //[NSMutableDictionary dictionaryWithCapacity:5];

		// tmp vars
		_currentString = new StringBuilder(1024);//[[NSMutableString alloc] initWithCapacity:1024];
		_storingCharacters = false;
		_layerAttribs = TMXLayerAttrib.TMXLayerAttribNone.getByteValue();
		_parentElement = TMXProperty.TMXPropertyNone.getIntValue();
		_currentFirstGID = 0;
	}
	
	private void parseXMLData(FileInputStream is){
		
		//get a factory
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {

			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(is, this);
			
			saxParser.getXMLReader().setEntityResolver(new EntityResolver(){
			    public InputSource resolveEntity(String publicId,String systemId){
			        return new InputSource(new ByteArrayInputStream(new byte[0]));
			    }
			});
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	private void parseXMLString(String xmlString){
		//this.parseXMLData(new InputSource(new StringReader(xmlString)));	
	}

	private void parseXMLFile(String xmlFilename){
		try{
			File file = new File(xmlFilename);
			FileInputStream fis = new FileInputStream(file);
			this.parseXMLData(fis);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("error");
		}
	}
	
	///
	@SuppressWarnings("unchecked")
	///Event Handlers
	///
	@Override
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException{

		if( qName.equals("map") ){
			
			String version = attributes.getValue("version");
			if( ! version.equals("1.0") )
				System.out.println("cocos2d: TMXFormat: Unsupported TMX version: " + version);
			String orientationStr = attributes.getValue("orientation");
			if( orientationStr.equals("orthogonal"))
				orientation = JGTiledMapOrientation.JGTiledMapOrientationOrtho;
			else if ( orientationStr.equals("isometric"))
				orientation = JGTiledMapOrientation.JGTiledMapOrientationIso;
			else
				System.out.println("cocos2d: TMXFomat: Unsupported orientation: " + orientation);

			mapSize.width = getAttributeIntValue(attributes, "width");
			mapSize.height = getAttributeIntValue(attributes, "height");
			tileSize.width = getAttributeIntValue(attributes, "tilewidth")/contentScale;
			tileSize.height = getAttributeIntValue(attributes, "tileheight")/contentScale;
			
			// The parent element is now "map"
			_parentElement = TMXProperty.TMXPropertyMap.getIntValue();
		} else if(qName.equals("tileset")) {

			// If this is an external tileset then start parsing that
			String externalTilesetFilename = attributes.getValue("source");
			if (externalTilesetFilename != null) {
				
//				// Tileset file will be relative to the map file. So we need to convert it to an absolute path
//				String dir = stringByRemovingLastPathComponent(filename);;	// Directory of map file
//				if (dir == null)
//					dir = resources;
//				externalTilesetFilename = dir + "/" + externalTilesetFilename;	// Append path to tileset file
//
//				_currentFirstGID = getAttributeIntValue(attributes, "firstgid");
//			
//	            // since the xml parser is not reentrant, we need to invoke each child xml parser in its own queue
//	            dispatch_queue_t reentrantAvoidanceQueue = dispatch_queue_create("xmlParserSafeQueue", DISPATCH_QUEUE_SERIAL);
//	            dispatch_async(reentrantAvoidanceQueue, ^{
//	                [self parseXMLFile:externalTilesetFilename];
//	            });
//	            dispatch_sync(reentrantAvoidanceQueue, ^{ });
	            
			} else {
				JGTiledMapTilesetInfo tileset = new JGTiledMapTilesetInfo();
				tileset.name = attributes.getValue("name"); 
				if(_currentFirstGID == 0) {
					tileset.firstGid = getAttributeIntValue(attributes, "firstgid"); 
				} else {
					tileset.firstGid = _currentFirstGID;
					_currentFirstGID = 0;
				}
				tileset.tileCount = getAttributeIntValue(attributes, "tilecount");
				tileset.spacing = (int)(getAttributeIntValue(attributes, "spacing")/contentScale);
				tileset.margin = (int)(getAttributeIntValue(attributes, "margin")/contentScale);
				JGSize s = new JGSize();
				s.width = getAttributeIntValue(attributes, "tilewidth")/contentScale;
				s.height = getAttributeIntValue(attributes, "tileheight")/contentScale;
				tileset.tileSize = s;
				tileset.tileOffset = new JGPoint(); //default offset (0,0)
				tileset.contentScale = contentScale;

				tilesets.add(tileset);
			}

		}
		else if(qName.equals("tileoffset")) {
			//should only be found within a tileset. Getting the parent.
			JGTiledMapTilesetInfo tileset = (JGTiledMapTilesetInfo)tilesets.get(tilesets.size()-1);
			JGPoint offset = new JGPoint(Float.parseFloat(attributes.getValue("x")),
										 Float.parseFloat(attributes.getValue("y")));
			tileset.tileOffset = offset.mult(new JGPoint(1.0/contentScale, 1.0/contentScale));
		}
		else if(qName.equals("tile")) {
			JGTiledMapTilesetInfo info = (JGTiledMapTilesetInfo)tilesets.get(tilesets.size()-1);
			HashMap<String, Object> dict = new HashMap<String, Object>(3);
			_parentGID =  info.firstGid + getAttributeIntValue(attributes, "id");
			tileProperties.put(Integer.toString(_parentGID), dict); 

			_parentElement = TMXProperty.TMXPropertyTile.getIntValue();

		} else if(qName.equals("layer")) {
			JGTiledMapLayerInfo layer = new JGTiledMapLayerInfo();
			layer.name = attributes.getValue("name"); 

			JGSize s = new JGSize();
			s.width = getAttributeIntValue(attributes, "width");
			s.height = getAttributeIntValue(attributes, "height");
			layer.layerSize = s;

			String visible = attributes.getValue("visible");
			if (visible != null)
				layer.visible = ! visible.equals("0");

			if( attributes.getValue("opacity") != null ){
				layer.opacity = Float.parseFloat(attributes.getValue("opacity"));
			} else {
				layer.opacity = 1.0f;
			}

			int x = getAttributeIntValue(attributes, "x");
			int y = getAttributeIntValue(attributes, "y");
			layer.offset = new JGPoint(x, y);

			layers.add(layer);

			// The parent element is now "layer"
			_parentElement = TMXProperty.TMXPropertyLayer.getIntValue();

		} else if(qName.equals("objectgroup")) {

			JGTiledMapObjectGroup objectGroup = new JGTiledMapObjectGroup();
			objectGroup.groupName = attributes.getValue("name"); 
			JGPoint positionOffset = new JGPoint();
			positionOffset.x = getAttributeIntValue(attributes, "x") * tileSize.width;
			positionOffset.y = getAttributeIntValue(attributes, "y") * tileSize.height;
			objectGroup.positionOffset = positionOffset;

			objectGroups.add(objectGroup);

			// The parent element is now "objectgroup"
			_parentElement = TMXProperty.TMXPropertyObjectGroup.getIntValue();

		} else if(qName.equals("image")) {

			JGTiledMapTilesetInfo tileset = (JGTiledMapTilesetInfo)tilesets.get(tilesets.size()-1);

			// build full path
			String imagename = attributes.getValue("source");
			
			String path = stringByRemovingLastPathComponent(filename);
			if (path == null)
				path = resources;
			
			tileset.sourceImage = path + imagename;
		} else if(qName.equals("data")) {
			String encoding = attributes.getValue("encoding");
			String compression = attributes.getValue("compression");

			if( encoding.equals("base64")  ) {
				_layerAttribs |= TMXLayerAttrib.TMXLayerAttribBase64.getByteValue();
				_storingCharacters = true;

				if( compression.equals("gzip")  )
					_layerAttribs |= TMXLayerAttrib.TMXLayerAttribGzip.getByteValue();

				else if( compression.equals("zlib") )
					_layerAttribs |= TMXLayerAttrib.TMXLayerAttribZlib.getByteValue();

				assert( compression == null || compression.equals("gzip") || compression.equals("zlib")) : "TMX: unsupported compression method";
			}

			assert( _layerAttribs != TMXLayerAttrib.TMXLayerAttribNone.getByteValue()) : "TMX tile map: Only base64 and/or gzip/zlib maps are supported";

		} else if(qName.equals("object")) {

			JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);

			// The value for "type" was blank or not a valid class name
			// Create an instance of TMXObjectInfo to store the object and its properties
			HashMap<String, Object> dict = new HashMap<String, Object>(); //NOTE: potential problem(10) initing

			// Parse everything automatically
			ArrayList<String> array = new ArrayList<String>();
			array.add("name");
			array.add("type");
			array.add("width");
			array.add("height");
			array.add("gid");
			for( String key : array ) {
				String obj = attributes.getValue(key);
				if( obj != null )
					dict.put(key, obj);
			}

			// But X and Y since they need special treatment
			// X
			String value = attributes.getValue("x"); 
			if( value != null ) {
				int x = (int)(Float.parseFloat(value) + objectGroup.positionOffset.x);				
				dict.put("x", x);
			}
			
			// Y
			value = attributes.getValue("y"); 
			if( value != null )  {
			int y = (int)(Float.parseFloat(value) + objectGroup.positionOffset.y);
				dict.put("y", y);
			}
			
			// Add the object to the objectGroup
			objectGroup.objects.add(dict);

			// The parent element is now "object"
			_parentElement = TMXProperty.TMXPropertyObject.getIntValue();

		} else if(qName.equals("property")) {

			if ( _parentElement == TMXProperty.TMXPropertyNone.getIntValue() ) {

				System.out.println("TMX tile map: Parent element is unsupported. Cannot add property named '"+ attributes.getValue("name") + "' with value '" + attributes.getValue("value") + "'");

			} else if ( _parentElement == TMXProperty.TMXPropertyMap.getIntValue() ) {

				// The parent element is the map
				properties.put(attributes.getValue("name"), attributes.getValue("value"));

			} else if ( _parentElement == TMXProperty.TMXPropertyLayer.getIntValue() ) {

				// The parent element is the last layer
				JGTiledMapLayerInfo layer = (JGTiledMapLayerInfo)layers.get(layers.size()-1);
				// Add the property to the layer
				layer.properties.put(attributes.getValue("name"), attributes.getValue("value"));

			} else if ( _parentElement == TMXProperty.TMXPropertyObjectGroup.getIntValue() ) {

				// The parent element is the last object group
				JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);
				objectGroup.properties.put(attributes.getValue("name"), attributes.getValue("value"));

			} else if ( _parentElement == TMXProperty.TMXPropertyObject.getIntValue() ) {

				// The parent element is the last object
				JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);
				HashMap<String, Object> dict = (HashMap<String, Object>)objectGroup.objects.get(objectGroup.objects.size()-1);

				String propertyName = attributes.getValue("name");
				String propertyValue = attributes.getValue("value");

				dict.put(propertyName, propertyValue);

			} else if ( _parentElement == TMXProperty.TMXPropertyTile.getIntValue() ) {

				HashMap<String, Object> dict = (HashMap<String, Object>)tileProperties.get(Integer.toString(_parentGID));
				String propertyName = attributes.getValue("name");
				String propertyValue = attributes.getValue("value");

				dict.put(propertyName, propertyValue);
			}

	    } else if (qName.equals("ellipse")) {
			
			// find parent object's dict and add ellipse-boolean (true) to it
	    	JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);
			HashMap<String, Object> dict = (HashMap<String, Object>)objectGroup.objects.get(objectGroup.objects.size()-1);
			dict.put("ellipse", true);
	    
		} else if (qName.equals("polygon")) {
			
			// find parent object's dict and add polygon-points to it
			JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);
			HashMap<String, Object> dict = (HashMap<String, Object>)objectGroup.objects.get(objectGroup.objects.size()-1);
			dict.put("polygonPoints", attributes.getValue("points"));
			
		} else if (qName.equals("polyline")) {
			
			// find parent object's dict and add polyline-points to it
			JGTiledMapObjectGroup objectGroup = (JGTiledMapObjectGroup)objectGroups.get(objectGroups.size()-1);
			HashMap<String, Object> dict = (HashMap<String, Object>)objectGroup.objects.get(objectGroup.objects.size()-1);
			dict.put("polylinePoints", attributes.getValue("points"));
		}
	}
	
	private int getAttributeIntValue(Attributes at, String key){
		int retVal = 0;
		try{
			retVal = Integer.parseInt(at.getValue(key));
		}catch(Exception e){
//XXX:			System.out.println("ParseIntError: " + e.getMessage() + "For key: " + key);
		}
		return retVal;
	}
	
	private String stringByRemovingLastPathComponent(String s){
		int idx = s.lastIndexOf('/');
		if (idx == -1){ return ""; }
		return s.substring(0, idx+1);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	        throws SAXException {			
		
		if(qName.equalsIgnoreCase("data") && (_layerAttribs&TMXLayerAttrib.TMXLayerAttribBase64.getByteValue()) > 0) {
			_storingCharacters = false;

			JGTiledMapLayerInfo layer = (JGTiledMapLayerInfo)layers.get(layers.size()-1);//[_layers lastObject];
			
			
			byte[] buffer = base64.base64Decode(_currentString.toString());
			
			if( buffer == null ) {
				System.out.println("cocos2d: TiledMap: decode data error");
				return;
			}

			if( (_layerAttribs & (TMXLayerAttrib.TMXLayerAttribGzip.getByteValue() | TMXLayerAttrib.TMXLayerAttribZlib.getByteValue())) > 0 ) {

				JGSize s = layer.layerSize;
				int sizeHint = (int)(s.width * s.height);
				byte[] deflated = ZipUtils.decompressDiv4(buffer);
				
				assert( deflated.length == sizeHint) : "CCTMXXMLParser: Hint failed!";

				buffer = null; //free the buffer

				if( deflated == null ) {
					System.out.println("cocos2d: TiledMap: inflate data error");
					return;
				}
	   
	            int[] ui = new int[deflated.length];
	           
	            for (int i = 0; i < deflated.length; i++){
	            	ui[i] = JGUtilities.unsignedToBytes(deflated[i]);
	            }
				layer.tiles = ui;
			} else{
				int[] ui = new int[buffer.length];
	            for (int i = 0; i < buffer.length; i++)
	            	ui[i] = JGUtilities.unsignedToBytes(buffer[i]);
				layer.tiles = ui;
			}

			_currentString.setLength(0); //clear the buffer

		} else if (qName.equalsIgnoreCase("map")) {
			// The map element has ended
			_parentElement = TMXProperty.TMXPropertyNone.getIntValue();

		}	else if (qName.equalsIgnoreCase("layer")) {
			// The layer element has ended
			_parentElement = TMXProperty.TMXPropertyNone.getIntValue();

		} else if (qName.equalsIgnoreCase("objectgroup")) {
			// The objectgroup element has ended
			_parentElement = TMXProperty.TMXPropertyNone.getIntValue();

		} else if (qName.equalsIgnoreCase("object")) {
			// The object element has ended
			_parentElement = TMXProperty.TMXPropertyNone.getIntValue();
		}

	}
	
	@Override
	public void characters(char ch[], int start, int length)
	        throws SAXException  {
		
		if (_storingCharacters){
			for (int i = 0; i < ch.length; i++)
				_currentString.append(ch[i]);
		}
	}
	
}
