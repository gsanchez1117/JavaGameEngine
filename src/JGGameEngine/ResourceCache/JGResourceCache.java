package JGGameEngine.ResourceCache;

import java.util.HashMap;


public abstract class JGResourceCache {

	protected HashMap<String, Object> resources = null;
	
	protected JGResourceCache(){
		resources = new HashMap<String, Object>();
	}
		
	public Object getResource(String fileName){
		
		//return null if not in cache
		if (!resources.containsKey(fileName)){ return null; }
		
		Object retVal = resources.get(fileName);
		return retVal;
	}
	
	public abstract void loadResourceFromFile(String fileName);
			
	
	public void removeResourceFromCache(String fileName){
		if(resources.containsKey(fileName)){
			resources.remove(fileName);
		}
	}

	
}
