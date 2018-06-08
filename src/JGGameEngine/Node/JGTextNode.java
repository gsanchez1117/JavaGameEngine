package JGGameEngine.Node;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;

import JGGameEngine.JGGame;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGSize;

public class JGTextNode extends JGNode {
	
	public static enum JGTextHorizontalAlignment{
		LEFT,
		CENTER,
		RIGHT
	}
	
	public static enum JGTextVerticalAlignment{
		TOP,
		MIDDLE,
		BOTTOM
	}
	
	private Font font;
	private Color fontColor;
	private JGTextHorizontalAlignment horizontalAlignment;
	private JGTextVerticalAlignment verticalAlignment;
	private String text;
	private String[] lines;
	
	public JGTextNode(){
		text = "";
		font = new Font("Arial", Font.PLAIN, 14);
		fontColor = Color.white;
		horizontalAlignment = JGTextHorizontalAlignment.LEFT;
		verticalAlignment = JGTextVerticalAlignment.BOTTOM;
	}
	
	public JGTextNode(Font Font, String Text){
		this(); //call default constructor
		setFont(Font);
		setText(Text);
	}
	
	public JGTextNode(String Text){
		this(); //call default constructor
		setText(Text);
	}
		
	@Override
	public void midDraw(float dt, Graphics g){
		drawText(g);
	}
	
	
	//
	//Private Methods
	//
	
	private void recalculateContentSize(){
		Graphics2D g = (Graphics2D)JGGame.sharedInstance().getGraphics();

		lines = text.split("\n");
		int maxWidth = 0;
		for (String line : lines){
			int curWidth = g.getFontMetrics(font).stringWidth(line);
			if (curWidth > maxWidth)
				maxWidth = curWidth;
		}
		setContentSize(new JGSize(maxWidth,g.getFontMetrics(font).getHeight() * lines.length));
	}
	
	private void drawText(Graphics g){
		
		float opacity = getIsOpaque() ? 1.0f : getOpacity();
		JGGraphics.JGGraphicsPackage gp = JGGraphics.startGraphics(g, opacity);

		gp.g2.setColor(getFontColor());
        gp.g2.setFont(getFont());
		gp.g2.transform(getTransformationMatrix());
		
		FontMetrics fm = gp.g2.getFontMetrics(getFont());
		
		int i = 1;
		for (String line : getLines()){
			
			int x = 0;
			int y = 0;
			
			switch (horizontalAlignment){
				case LEFT:
					x = 0;
					break;
				case CENTER:
					x = (int)getContentSize().width/2 - fm.stringWidth(line)/2;
					break;
				case RIGHT:
					x = (int)getContentSize().width - fm.stringWidth(line);
					break;
			}
			
			switch (verticalAlignment){
			case TOP:
				int offset = (int)(fm.getAscent() - fm.getDescent()) - fm.getHeight();
				y = (int)fm.getHeight() * i + offset;	
				break;
			case MIDDLE:
				offset = (int)(fm.getAscent() - fm.getDescent()) - fm.getHeight();
				y = (int)fm.getHeight() * i + offset/2;
				break;
			case BOTTOM:
				y = (int)fm.getHeight() * i;
				break;
			}
			
			gp.g2.drawString(line, 
							 x, 
							 y);
			i++;
		}	    
		JGGraphics.endGraphics(gp);
	}
	
	//
	//Getters
	//
	
	public Font getFont(){ return font; }
	
	public Color getFontColor(){ return fontColor; }
	
	public JGTextHorizontalAlignment getHorizonatalAlignment(){ return horizontalAlignment; }
	
	public JGTextVerticalAlignment getVerticalAlignment(){ return verticalAlignment; }
	
	public String getText(){ return text; }
	
	public String[] getLines(){ return lines; }
	
	//
	//Setters
	//
	
	public void setFont(Font f){ 
		font = f; 
		recalculateContentSize();
	}
	
	public void setFontSize(int size){
		setFont(font.deriveFont((float)size));
	}
	
	public void setFontStyle(int style){
		setFont(font.deriveFont(style));
	}
	
	public void setFontName(String name){
		setFont(new Font(name, font.getStyle(), font.getSize()));
	}
	
	public void setFontFromFile(String fileName){
		try{
			File file = new File(fileName);
			Font f = null;
			if (file.exists()){
				f = Font.createFont(Font.TRUETYPE_FONT, file);
			}else{
				f = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream(fileName));
			}
			if (f != null){
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				ge.registerFont(f);
				setFont(f);
			}
		}catch(Exception e){
			System.out.println("JGTextNode: Error Loading Font");
		}
	}
	
	public void setText(String Text){
		text = Text; 
		recalculateContentSize();
	}
	
	public void setFontColor(Color col){
		fontColor = col;
	}
	
	public void setHorizontalAlignment(JGTextHorizontalAlignment ha){ horizontalAlignment = ha; }
	public void setVerticalAlignment(JGTextVerticalAlignment va){ verticalAlignment = va; }

}
