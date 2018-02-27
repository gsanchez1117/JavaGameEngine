package JGGameEngine.Node;

import java.awt.Color;
import java.awt.image.BufferedImage;

import JGGameEngine.JGImageTintMask;
import JGGameEngine.JGSize;
import JGGameEngine.JGUtilities;
import JGGameEngine.ResourceCache.JGBufferedImageCache;

public class JGTextureNode extends JGNode {

	private String imgFile;
	private BufferedImage texture;
	private BufferedImage _texBackup;
	private Color tint;
	private float tintAlpha;
	
	public JGTextureNode(){
		imgFile = null;
		texture = null;
		_texBackup = null;
	}
	
	public void LoadTexture(String fileName){
		
		imgFile = fileName;
		
		JGBufferedImageCache imgCache = JGBufferedImageCache.sharedInstance();
		imgCache.loadResourceFromFile(fileName);
		
		texture = imgCache.getImage(fileName);//new ImageIcon(fileName);
		_texBackup = texture;
		
        //update the content size
        this.setContentSize(new JGSize(texture.getWidth(), 
        							   texture.getHeight()));
	}
	
	public void removeTint(){
		texture = _texBackup; //restore the image
		tint = null; //clear tint color
		tintAlpha = 1.0f; //set alpha to full opacity
	}
	
	//
	//Getters
	//
	
	public String getImgFile(){ return imgFile; }
	public BufferedImage getTexture(){ return texture; }
	public Color getTint(){ return tint; }
	public float getTintAlpha(){ return tintAlpha; }
	
	//
	//Setters
	//
	
	public void setTint(Color color, float alpha){
		_transformDirty = true; //set the transform dirty
		tint = color;
		tintAlpha = alpha;	
		texture = JGImageTintMask.tintImage(_texBackup, tint, tintAlpha);
	}
	
	/**
	 * Changed the hue saturation and ballance of the image
	 * @param h - hue range [0-360]
	 * @param s - saturation [0-100]
	 * @param b - balance [0-100]
	 */
	public void setHSB(float h, float s, float b){
		_transformDirty = true;

		applyHSB(texture, h, s, b);
	}
	
	private void applyHSB(BufferedImage image, float h, float s, float b){
		for (int y=0; y< image.getHeight(); y++)
		    for (int x=0; x< image.getWidth(); x++) {
		        Color pix = new Color(image.getRGB(x, y));
		            float[] hsb = new float[3];
		            hsb = Color.RGBtoHSB(pix.getRed(),pix.getGreen(),pix.getBlue(),hsb);
		            hsb[0] = (float)(hsb[0] +( h/360.0));//hue
		            hsb[1] *=  (s/100);
		            hsb[2] *=  (b/100);
		            for (int i = 1; i < 3; i++)
		            	hsb[i] = JGUtilities.clampf(hsb[i], 0.0f, 1.0f);
		            int rgb = Color.HSBtoRGB(hsb[0], hsb[1],hsb[2]);
		                image.setRGB(x, y, rgb);
		        }
	}

	
}
