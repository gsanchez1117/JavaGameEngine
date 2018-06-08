package Tetris;

import java.awt.Color;

import JGGameEngine.JGPoint;

public class TDefaults {
	
	static private TDefaults instance = null;
	
	private final int WIDTH = 10;
	private final int HEIGHT = 22;
	private final int BLOCK_SIZE = 32;
	
	private TDefaults(){}
	
	public static TDefaults sharedInstance(){
		if (instance == null){
			instance = new TDefaults();
		}
		return instance;
	}
	
	public int getWidth(){ return WIDTH; }
	public int getHeight(){ return HEIGHT; }
	public int getBlockSize(){ return BLOCK_SIZE; }
	public int[][][][] getRotations(){ return rots; }
	public Color[] getShapeColors(){ return shapeColors; }
	public JGPoint[][] getShapeKicks(){ return shapeKicks; }
	
	private final int[][][][] rots = {
			
			//O
			{
				//0
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 1, 0},
					{0, 0, 0, 0},
				},
			},
			//S
			{
				//0
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{1, 1, 0, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 1, 0},
					{0, 0, 1, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{1, 1, 0, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{1, 0, 0, 0},
					{1, 1, 0, 0},
					{0, 1, 0, 0},
				},
			},
			//Z
			{
				//0
				{
					{0, 0, 0, 0},
					{1, 1, 0, 0},
					{0, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 0, 1, 0},
					{0, 1, 1, 0},
					{0, 1, 0, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{1, 1, 0, 0},
					{0, 1, 1, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{1, 1, 0, 0},
					{1, 0, 0, 0},
				},
			},
			//J
			{
				//0
				{
					{0, 0, 0, 0},
					{1, 0, 0, 0},
					{1, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{1, 1, 1, 0},
					{0, 0, 1, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
					{1, 1, 0, 0},
				},
			},
			//L
			{
				//0
				{
					{0, 0, 0, 0},
					{0, 0, 1, 0},
					{1, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 1, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{1, 1, 1, 0},
					{1, 0, 0, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{1, 1, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
				},
			},
			//I
			{
				//0
				{
					{0, 0, 0, 0},
					{1, 1, 1, 1},
					{0, 0, 0, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 1, 0},
					{0, 0, 1, 0},
					{0, 0, 1, 0},
					{0, 0, 1, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{1, 1, 1, 1},
					{0, 0, 0, 0},
				},
				//3
				{
					{0, 1, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 0, 0},
				},
			},
			//T
			{
				//0
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{1, 1, 1, 0},
					{0, 0, 0, 0},
				},
				//1
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{0, 1, 1, 0},
					{0, 1, 0, 0},
				},
				//2
				{
					{0, 0, 0, 0},
					{0, 0, 0, 0},
					{1, 1, 1, 0},
					{0, 1, 0, 0},
				},
				//3
				{
					{0, 0, 0, 0},
					{0, 1, 0, 0},
					{1, 1, 0, 0},
					{0, 1, 0, 0},
				},
			},
			
	};
	
	private JGPoint[][] shapeKicks = {
		//o
			{},
		//s
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
			},
		//z
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
			},
		//j
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
			},
		//l
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
			},
		//i
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),	
				new JGPoint(-2, 0),
				new JGPoint(2, 0),
				
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
				new JGPoint(-2, -1),
				new JGPoint(2, -1),
				
				new JGPoint(0, -2),
				new JGPoint(-1, -2),
				new JGPoint(1, -2),
				new JGPoint(-2, -2),
				new JGPoint(2, -2),
			},
		//t
			{
				new JGPoint(0, 0),
				new JGPoint(-1, 0),
				new JGPoint(1, 0),
				new JGPoint(0, -1),
				new JGPoint(-1, -1),
				new JGPoint(1, -1),
			},
	};
	
	private final Color[] shapeColors = {
			new Color(233,108,2,255),
			new Color(115,229,4,255),
			new Color(234,2,2,255),
			new Color(3,216,163,255),
			new Color(228,2,166,255),
			new Color(4,146,226,255),
			new Color(130,3,224,255)
	};

}
