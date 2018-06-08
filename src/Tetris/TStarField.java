package Tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import JGGameEngine.JGGraphics;
import JGGameEngine.JGPoint;
import JGGameEngine.JGSize;
import JGGameEngine.JGUtilities;
import JGGameEngine.Node.JGNode;

public class TStarField extends JGNode {

	private int width;
	private int height;
	private int stars = 100;
	private int zMax = 5;
	private float speed = 0.01f;
	private float [][] starfield = new float[1000][8];
	private Random random = new Random();

	
	public TStarField(int Width, int Height){
		shouldClipToBounds = true; //clip all children to bounds
		setStarFieldSize(Width, Height);
		initialize();
		setAnchorPoint(new JGPoint(0,0));
		fillColor = Color.white;
		strokeColor = Color.white;
		this.setOpacity(0.1f);
	}
	
	//////////////////
	//Public Methods//
	//////////////////
	
	public void setStarFieldSize(int Width, int Height){
		width = Width;
		height = Height;
		setContentSize(new JGSize(width, height));
	}
	
	public void setSpeed(float Speed){
		speed = Speed;
	}
	
	public void setStars(int num){
		stars = (int)JGUtilities.clampf(num, 0, 999);
	}
	
	///////////////////
	//Private Methods//
	///////////////////
	
	private void initialize(){
		for (int i = 0; i < stars; i++){
			createStar(i);
			starfield[i][2] = random.nextInt(zMax);
		}
	}
	
	private void createStar(int i){
		starfield[i][0] = random.nextInt(2*width) - width;		//x
		starfield[i][1] = random.nextInt(2*height) - height;	//y
		starfield[i][2] = zMax;									//z
		starfield[i][3] = random.nextInt(5);					//size
		
		//Color[] colors = TDefaults.sharedInstance().getShapeColors();
		Color color = Color.white;//colors[random.nextInt(colors.length)];
		starfield[i][4] = color.getRed();						//r
		starfield[i][5]	= color.getGreen();						//g
		starfield[i][6]	= color.getBlue();						//b
		starfield[i][7]	= color.getAlpha();						//a
	}
	
	////////////////////
	//Override Methods//
	////////////////////
	
	@Override
	public void midDraw(float dt, Graphics g){
		for (int i = 0; i < stars; i++){
			starfield[i][2] -= speed; 
			if (starfield[i][2] < speed ){ createStar(i); }
			int x = (int)(width / 2 + starfield[i][0] / starfield[i][2]);
			int y = (int)(height / 2 + starfield[i][1] / starfield[i][2]);
			if (x < 0 || y < 0 || x >= width || y >= height){
				createStar(i);
			}else{
				JGGraphics.drawNodeCirlce(
						this, 
						new JGPoint(x, y), 
						(int)starfield[i][3], 
						new Color(starfield[i][4] /255.0f, starfield[i][5] /255.0f, starfield[i][6] /255.0f, starfield[i][2]/zMax), 
						new Color(starfield[i][4] /255.0f, starfield[i][5] /255.0f, starfield[i][6] /255.0f, starfield[i][2] /zMax),
						g
				);
			}
		}
	}
	
	
}
