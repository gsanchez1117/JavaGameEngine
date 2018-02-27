package JGGameEngine.ResourceCache;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;


public class JGBufferedImageCache extends JGResourceCache {
	
	private static JGBufferedImageCache instance = null; //singleton instance	
	
	public static JGBufferedImageCache sharedInstance(){
		if (instance == null){
			instance = new JGBufferedImageCache();
		}
		return instance;
	}
		
	public BufferedImage getImage(String fileName){
		return (BufferedImage)super.getResource(fileName);
	}
	
	@Override
	public void loadResourceFromFile(String fileName) {
		
		//check if image is already in cache
		if (resources.containsKey(fileName)){ return; }
		
		BufferedImage imgICO = null;
		try{
			File file = new File(fileName); 
			imgICO = ImageIO.read(file);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("error2");
		}
		if (imgICO != null){
			resources.put(fileName, imgICO);
		}
	}
		
	public void removeImageFromCache(String fileName){
		super.removeResourceFromCache(fileName);
	}
	
}
