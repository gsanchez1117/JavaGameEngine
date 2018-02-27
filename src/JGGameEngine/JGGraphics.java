package JGGameEngine;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import JGGameEngine.Node.JGNode;
import JGGameEngine.Node.JGSprite;
import JGGameEngine.TileMap.JGTiledMapLayer;


public class JGGraphics {
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	/*drawSprite
	 * aSprite - the JGSprite to pass into the method
	 * g - the graphics context to draw in
	 */
	public static void drawSprite(JGSprite aSprite, Graphics g){
		
		float opacity = aSprite.getIsOpaque() ? 1.0f : aSprite.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aSprite.getTransformationMatrix());
	    gp.g2.drawImage(aSprite.getTexture(), 
	    		(int)(aSprite.getContentSize().width/2 - aSprite.getTexture().getWidth(aSprite.getGameWindow())/2), 
	    		(int)(aSprite.getContentSize().height/2 - aSprite.getTexture().getHeight(aSprite.getGameWindow())/2), 
	    		aSprite.getGameWindow());
	    
	    endGraphics(gp);
	}
		
	public static void drawSprite(JGSprite aSprite, JGPoint sPos, JGSize frameSize, Graphics g){
		
		float opacity = aSprite.getIsOpaque() ? 1.0f : aSprite.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		JGPoint d = new JGPoint((aSprite.getContentSize().width/2 - frameSize.width/2),
								(aSprite.getContentSize().height/2 - frameSize.height/2));
		
		gp.g2.transform(aSprite.getTransformationMatrix());
	    gp.g2.drawImage(aSprite.getTexture(), 
	    		
	    		(int)d.x, 
	    		(int)d.y, 
	    		
	    		(int)(d.x + frameSize.width), 
	    		(int)(d.y + frameSize.height),
	    		
	    		(int)sPos.x, 
	    		(int)sPos.y,
	    		
	    		(int)(sPos.x + frameSize.width), 
	    		(int)(sPos.y + frameSize.height),
	    		
	    		aSprite.getGameWindow());
	    
	    endGraphics(gp);
	}
	
	/*drawSprite
	 * aSprite - the JGSprite to pass into the method
	 * g - the graphics context to draw in
	 */
	public static void drawSpriteMode7(JGSprite aSprite, Graphics g){
		
		float opacity = aSprite.getIsOpaque() ? 1.0f : aSprite.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aSprite.getTransformationMatrix());
		
		
		ColorModel cm = aSprite.getTexture().getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = aSprite.getTexture().copyData(null);
		BufferedImage img = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		
		int x = 0;
		int y = 0;
		int xres = img.getWidth();
		int yres = img.getHeight();
		
		for (y = 0 ; y < yres ; y++)
			  for (x = 0 ; x < xres ; x++)
			  {
			     float horizon = 100; //adjust if needed
			     float fov = 200; 

			     float px = x;
			     float py = fov; 
			     float pz = y + horizon;      

			     //projection 
			     float sx = px / pz;
			     float sy = py / pz; 

			     float scaling = 200; //adjust if needed, depends of texture size
			     //color = get2DTexture(sx * scaling, sy * scaling); 
			     int color = 0;
			     if (sx * scaling < 0 || sx * scaling >= xres || sy * scaling < 0 || sy * scaling >= yres){
			    	 color = 0;
			     }else{
			    	 color = img.getRGB((int)(sx * scaling), (int)(sy * scaling));
			     }
			     //put (color) at (x, y) on screen
			     img.setRGB(x, y, color);
			     
			  }

	    gp.g2.drawImage(img, 
	    		(int)(aSprite.getContentSize().width/2 - aSprite.getTexture().getWidth(aSprite.getGameWindow())/2), 
	    		(int)(aSprite.getContentSize().height/2 - aSprite.getTexture().getHeight(aSprite.getGameWindow())/2), 
	    		aSprite.getGameWindow());
	    
	    endGraphics(gp);
	}
	
	public static void drawNodeCirlce(JGNode aNode, int radius, Graphics g){
		drawNodeEllipse(aNode, aNode.getPosition(), radius, radius, g);
	}
		
	public static void drawNodeCirlce(JGNode aNode, JGPoint position, int radius, Color strokeCol, Color fillCol, Graphics g){
		drawNodeEllipse(aNode, position, radius, radius, strokeCol, fillCol, g);
	}
	
	public static void drawNodeEllipse(JGNode aNode, JGPoint position, int radiusX, int radiusY, Graphics g){
		drawNodeEllipse(aNode, position, radiusX, radiusY, aNode.strokeColor, aNode.fillColor, g);
	}
	
	public static void drawNodeEllipse(JGNode aNode,JGPoint pos, int radiusX, int radiusY, Color strokeCol, Color fillCol, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		//DRAW OUTLINE
		gp.g2.setColor(strokeCol);
		gp.g2.drawOval((int)pos.x, 
				   (int)pos.y, 
				   radiusX * 2, 
				   radiusY * 2);

		//DRAW FILL
		gp.g2.setColor(fillCol);
		gp.g2.fillOval((int)pos.x, 
					   (int)pos.y, 
					   radiusX * 2, 
					   radiusY * 2);
	    
	    endGraphics(gp);
	}
	
	public static void drawNodeRoundedLine(JGNode aNode, JGPoint start, JGPoint end, float width, Color col, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		//Set Stroke
		Stroke bak = gp.g2.getStroke();
		BasicStroke s = new BasicStroke(width,
		                        BasicStroke.CAP_ROUND,
		                        BasicStroke.JOIN_ROUND,
		                        10.0f, null, 0.0f);
		
		//Set Fill Color
		gp.g2.setColor(col);
		gp.g2.setStroke(s);
		gp.g2.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);
	    		
		gp.g2.setStroke(bak);
	    endGraphics(gp);
	}
	
	public static void drawNodeRoundedRect(JGNode aNode, JGRect rect, int radius, Color stroke, Color fill, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		gp.g2.setColor(stroke);
		gp.g2.drawRoundRect((int)rect.origin.x, (int)rect.origin.y, (int)rect.size.width, (int)rect.size.height, radius, radius);
		
		gp.g2.setColor(fill);
		gp.g2.fillRoundRect((int)rect.origin.x, (int)rect.origin.y, (int)rect.size.width, (int)rect.size.height, radius, radius);
		
	    endGraphics(gp);
	}
	
	public static void drawNodePolygon(JGNode aNode, int[] xPoints, int[] yPoints, float width, Color stroke, Color fill, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		Stroke bak = gp.g2.getStroke();
		BasicStroke s = new BasicStroke(width,
		                        BasicStroke.CAP_ROUND,
		                        BasicStroke.JOIN_ROUND,
		                        10.0f, null, 0.0f);
		gp.g2.setStroke(s);
		
		gp.g2.setColor(stroke);
		gp.g2.drawPolygon(xPoints, yPoints, xPoints.length);
		gp.g2.setColor(fill);
		gp.g2.fillPolygon(xPoints, yPoints, xPoints.length);
		
		gp.g2.setStroke(bak);
	    endGraphics(gp);
	}
	
	public static void drawNodeRect(JGNode aNode, JGPoint position, JGSize size, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		
		//Set Fill Color
		gp.g2.setColor(aNode.strokeColor);
		gp.g2.drawRect((int)position.x, (int)position.y, (int)size.width-1, (int)size.height-1);
	    
	    endGraphics(gp);
	}
	
	public static void fillNodeRect(JGNode aNode, JGPoint position, JGSize size, Graphics g){
		
		float opacity = aNode.getIsOpaque() ? 1.0f : aNode.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
		
		gp.g2.transform(aNode.getTransformationMatrix());
		
		
		//Set Fill Color
		gp.g2.setColor(aNode.fillColor);
		gp.g2.fillRect((int)position.x, (int)position.y, (int)size.width-1, (int)size.height-1);
	    
	    endGraphics(gp);
	}
	
	public static void drawImagePart(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, JGGame io, Graphics g){
		
		JGGraphicsPackage gp = startGraphics(g, 1.0f);
		
	    gp.g2.drawImage(img, 
	    		dx1, 
	    		dy1, 
	    		
	    		dx2, 
	    		dy2, 
	    		
	    		sx1, 
	    		sy1,
	    		
	    		sx2, 
	    		sy2, 
	    		io);
	   	    
	    endGraphics(gp);
	}
	
	
	public static void drawTliedLayer(JGTiledMapLayer layer, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Graphics g){
		
		float opacity = layer.getIsOpaque() ? 1.0f : layer.getOpacity();
		JGGraphicsPackage gp = startGraphics(g, opacity);
	    	    
	    gp.g2.transform(layer.getTransformationMatrix());
	    gp.g2.drawImage(layer.getTexture(), 
	    		dx1, 
	    		dy1, 
	    		
	    		dx2, 
	    		dy2, 
	    		
	    		sx1, 
	    		sy1,
	    		
	    		sx2, 
	    		sy2, 
	    		layer.getGameWindow());
	   	    
	    endGraphics(gp);
	}
	
	/**
	 * Sets up the graphics context to allow for clipping on a given JGNode.
	 * @param aNode - the JGNode to start clipping for.
	 * @param clips - a boolean denoting if clipping should take place
	 * @param draws - a boolean denoting if drawing should take place
	 * @param g - the Graphics object
	 */
	public static void startBounds(JGNode aNode, boolean clips, boolean draws, Graphics g){		
		
		//return if not drawing and not clipping
		if (!clips && !draws){ return; }
		
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    AffineTransform matrix = gg.getTransform(); // Backup

		JGSize size = aNode.getContentSize();
		JGPoint cs = new JGPoint(size.width, size.height);
	    gg.transform(aNode.getTransformationMatrix());
	    
	    if (clips){
	    	Rectangle2D clipRect = new Rectangle2D.Float(0, 0, (int)cs.x, (int)cs.y);
			gg.clip(clipRect);			
	    }
		
	    if (draws){
		    gg.setColor(aNode.strokeColor);
		    gg.drawRect(
		    		(int)(-cs.x * aNode.getAnchorPoint().x), 
		    		(int)(-cs.y * aNode.getAnchorPoint().y), 
		    		(int)cs.x-1, 
		    		(int)cs.y-1);
	    }
	    
	    gg.setTransform(matrix); //Restore
	}
	
	/**
	 * Restores the Graphics context to how it was before startBounds was called
	 * @param aNode - the JGNode to restore the Graphics context for
	 * @param g - the Graphics context to restore
	 */
	public static void endBounds(JGNode aNode, Graphics g){
		Graphics2D gg = (Graphics2D) g;
		
		if (aNode.parent != null && aNode.parent.shouldClipToBounds){
	    	Rectangle2D clipRect = new Rectangle2D.Float(0, 0, (int)aNode.getContentSize().width, (int)aNode.getContentSize().getHeightI());
			gg.setClip(clipRect);
		}else{
			gg.setClip(null);
		}
	}
	
	/**
	 * Sets up the graphics context to allow for transparency and returns a JGGraphicsPackage
	 * @param g - the Graphics object
	 * @param opacity - the opacity of the current draw call
	 * @return - a JGGraphicsPackage
	 */
	public static JGGraphicsPackage  startGraphics(Graphics g, float opacity){
		Graphics2D gg = (Graphics2D) g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Composite oldComp = gg.getComposite();
	    AffineTransform matrix = gg.getTransform(); // Backup
	    
	    //start opacity
        int rule = AlphaComposite.SRC_OVER;
        Composite comp = AlphaComposite.getInstance(rule , opacity);
        gg.setComposite(comp);
		
		return new JGGraphicsPackage(gg, oldComp, matrix);
	}
	
	/**
	 * Restores the graphics context back to how it was before startGraphcis was called.
	 * @param gp - the JGGraphicsPackage to restore
	 */
	public static void endGraphics(JGGraphicsPackage gp){
		 gp.g2.setComposite(gp.comp); //restore composite
		 gp.g2.setTransform(gp.matrix); //Restore
	}
	
	//////////////////////////////////////
	//Internal Graphics Package Class	//
	//////////////////////////////////////
	
	/**
	 * JGGraphicsPackage
	 * @author Gabriel Sanchez
	 * Description: A small internal class used to pass graphics information around in a clean package.
	 */
	public static class JGGraphicsPackage{
		
		/** Holds the graphics2d object */
		public Graphics2D g2;
		
		/** Holds the composite object */
		public Composite comp;
		
		/** Holds the transformation matrix */
		public AffineTransform matrix;
		
		//////////////////
		//Constructors	//
		//////////////////
		
		/**
		 * Creates a new JGGraphicsPackage object with passed in Graphics2D, Composite, and AffineTransform objects.
		 * @param g - the Graphics2D object
		 * @param c - the Composite object
		 * @param a - the AfineTransform object
		 */
		public JGGraphicsPackage(Graphics2D g, Composite c, AffineTransform a){
			g2 = g;
			comp = c;
			matrix = a;
		}
	}

}
