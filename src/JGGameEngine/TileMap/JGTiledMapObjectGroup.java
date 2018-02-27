package JGGameEngine.TileMap;

import java.util.ArrayList;
import java.util.HashMap;

import JGGameEngine.JGPoint;

public class JGTiledMapObjectGroup {
	    
	@SuppressWarnings("unused")
	private HashMap<String, Object>	_properties;
	
	/// -----------------------------------------------------------------------
	/// @name Tiled Map Object Group Properties
	/// -----------------------------------------------------------------------

	/** Name of the object group. */
	public String groupName;

	/** Offset position of child objects, */
	public JGPoint positionOffset;
	
	/** Array of the objects. */
	public ArrayList<Object> objects;

	/// -----------------------------------------------------------------------
	/// @name Accessing Layer Objects
	/// -----------------------------------------------------------------------

	/**
	 *  Return the dictionary for the first entry of specified object namee.
	 *
	 *  @param objectName Object name to use.
	 *
	 *  @return Object dictionary.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> objectNamed(String objectName){
		for( Object object : objects ) {
			HashMap<String, Object> dict = (HashMap<String, Object>)object;
			if( dict.get("name").equals(objectName) )
				return dict;
		}
		
		// object not found
		return null;
	}

	/// -----------------------------------------------------------------------
	/// @name Accessing Layer Object Properties
	/// -----------------------------------------------------------------------

	/** List of properties stored in the dictionary. */
	public HashMap<String, Object> properties;

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
	
	public JGTiledMapObjectGroup(){
		groupName = null;
		positionOffset = new JGPoint();
		objects = new ArrayList<Object>();
		properties = new HashMap<String, Object>(5);
	}

}
