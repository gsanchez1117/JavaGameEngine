package JGGameEngine.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import JGGameEngine.JGGame;
import JGGameEngine.JGGraphics;
import JGGameEngine.JGInputManager;
import JGGameEngine.JGPoint;
import JGGameEngine.JGRect;
import JGGameEngine.JGSize;
import JGGameEngine.JGUtilities;

public class JGNode {
	
	private static int nodes = 0;
	
	private JGPoint position;
	private double rotation; //in degrees
	private JGPoint scale;
	private boolean flipX;
	private boolean flipY;
	private JGPoint anchorPoint;
	private int zOrder;
	private ArrayList<JGNode>children;
	private ArrayList<JGNode>childrenCopy;
	private ArrayList<JGNode>deletionQue;
	private boolean isAlive;
	private AffineTransform transformationMatrix;
	private JGSize contentSize;
	protected boolean _transformDirty;
	private boolean isOpaque;
	private float opacity;
	private boolean oldMousePressed;
	private boolean mouseEntered;
	private boolean oldMouseEntered;
	
	//mouse pressed vars
	private boolean mousePressed;
	private JGPoint mouseStartPosition = new JGPoint();
	private JGPoint mouseMovedPosition = new JGPoint();

	public String name;
	public JGNode parent;
	public Color strokeColor;
	public Color fillColor;
	public boolean isVisible;
	public boolean shouldClipToBounds;
	public boolean shouldDrawBounds;
	public boolean recievesMouseInput;
	public boolean swallowsMouseInput;
	
	// ----------------------------------------------
	// Constructors
	// ----------------------------------------------
	
	
	public JGNode(){
		name = "Node:" + (++nodes);
		parent = null;
		strokeColor = Color.white;
		fillColor = new Color(0.0f, 0.0f, 1.0f, 0.4f);
		position = new JGPoint();
		rotation = 0.0f;
		scale = new JGPoint(1, 1);
		flipX = false;
		flipY = false;
		anchorPoint = new JGPoint(0.5f, 0.5f);
		zOrder = 0;
		children = new ArrayList<JGNode>();
		childrenCopy = new ArrayList<JGNode>();
		deletionQue = new ArrayList<JGNode>();
		isAlive = true;
		transformationMatrix = new AffineTransform(); //identity matrix
		isVisible = true;
		shouldClipToBounds = false;
		shouldDrawBounds = false;
		contentSize = new JGSize();
		_transformDirty = true;
		isOpaque = false;
		opacity = 1.0f;
		recievesMouseInput = false;
		swallowsMouseInput = false;
		mousePressed = false;
	}
	
	// ----------------------------------------------
	// Public Methods
	// ----------------------------------------------
	
	public void Update(float dt){
		
		//update transform if dirty
		if (_transformDirty){
			_transformDirty = false;
			transformationMatrix = JGUtilities.getTransformFromJGNode(this);
		}
		
		childrenCopy.addAll(children);
		//update living children
		for(JGNode aNode : childrenCopy){
			
			if (aNode.isAlive)
				aNode.Update(dt);
			else
				deletionQue.add(aNode);
		}
		childrenCopy.clear();
				
		//remove all dead nodes as children
		for(JGNode aNode : deletionQue){
			removeChild(aNode);
		}
		
		//empty the deletion que
		deletionQue.clear();
		
		//Sort the children by z-order
		Collections.sort(children, new Comparator<JGNode>() {
	        @Override
	        public int compare(JGNode aNode2, JGNode aNode1){
	            return  Integer.compare(aNode2.zOrder, aNode1.zOrder);
	        }
	    });
		
		//calling checkMouseInput last ensures that node
		//touches are processed in order from front to back
		if (recievesMouseInput && !JGInputManager.sharedInstance().getMouseInputBlocked())
			checkMouseInput();
		oldMousePressed = JGInputManager.sharedInstance().getMouseButtonState(1);
	}
	
	public void Draw(float dt, Graphics g){
		
		//return if not visible
		if (!isVisible){ return; } //for root node
		
		//start bounds
		JGGraphics.startBounds(this, shouldClipToBounds, shouldDrawBounds, g);
		
		midDraw(dt, g); //do mid drawing
		
		//Draw children
		for(JGNode aNode : children){
			if (aNode.isAlive && aNode.isVisible)
				aNode.Draw(dt, g);
		}
		
		postChildDraw(dt, g); //do drawing after children
		
		if (shouldClipToBounds){
			JGGraphics.endBounds(this, g); //clear any clipping
		}
	}
	
	/**
	 * does drawing before children are drawn
	 * @param dt
	 * @param g
	 */
	protected void midDraw(float dt, Graphics g){
		
	}
	
	/**
	 * does drawing after children are drawn
	 * @param dt
	 * @param g
	 */
	protected void postChildDraw(float dt, Graphics g){
		
	}
	
	public void addChild(JGNode aNode){
		
		if (aNode.parent != null){ 
			System.out.println("node already has a parent. Can't be added to two nodes"); 
			return;
		}
		
		//if the node is not a PENode return
		if (!(aNode instanceof JGNode)) {
			System.out.print ("node is not of type JGNode and cannot be added");	
			return;
		}
		
		//if the node is already a child
		if (children.contains(aNode)){
			System.out.print ("node is already a child and cant be added again!");	
			return;
		}
		
		//add the node as a child if it passes all of the checks
		children.add(aNode);
		
		//set the nodes parent to self
		aNode.parent = this;		
	}
	
	public void removeChild(JGNode aNode){
		//if the node is not a child
        if (!children.contains(aNode)){
            System.out.print ("can't remove node because it is not a child of this node");
            return;
        }
        
        //remove the node as a child if it passes all of the checks
        children.remove(aNode);
        
        //set the nodes parent to none
        aNode.parent = null;

	}
	
	public void killNode(){ isAlive = false; }
	
	public JGPoint convertToWorldSpace(JGPoint p){
		Point ptSrc = new Point();
		Point ptDst = new Point();
		ptSrc.x = (int)p.x;
		ptSrc.y = (int)p.y;
		nodeToWorldTransform().transform(ptSrc, ptDst);
		return new JGPoint(ptDst.x,ptDst.y);
	}
	
	public JGPoint convertToNodeSpace(JGPoint p){
		Point ptSrc = new Point();
		Point ptDst = new Point();
		ptSrc.x = (int)p.x;
		ptSrc.y = (int)p.y;
		worldToNodeTransform().transform(ptSrc, ptDst);
		return new JGPoint(ptDst.x,ptDst.y);
	}
	
	public AffineTransform nodeToWorldTransform(){		
		AffineTransform t = nodeToParentTransform();

		for (JGNode p = parent; p != null; p = p.parent){
			AffineTransform temp = p.nodeToParentTransform();
			temp.concatenate(t);
			t = temp;
		}

		return t;
	}
	
	public AffineTransform worldToNodeTransform(){
		AffineTransform ret = nodeToWorldTransform();
		try{
			ret.invert();
			return ret;
		}catch(Exception e){
			return null;
		}
	}
	
	public AffineTransform nodeToParentTransform(){
			return JGUtilities.getTransformFromJGNode(this, false);
	}

	// ----------------------------------------------
	// Protected Methods
	// ----------------------------------------------
	
	protected void mousePressBegan(JGPoint p){
		System.out.println("started");
	}
	
	protected void mousePressMoved(JGPoint p){
		System.out.println("dragged");
	}
	
	protected void mousePressEnded(JGPoint p){
		System.out.println("ended");
	}
	
	protected void onMouseEnter(){
		System.out.println("entered");
	}
	
	protected void onMouseExit(){
		System.out.println("exited");
	}
	
	// ----------------------------------------------
	// Private Methods
	// ----------------------------------------------
		
	private boolean hitTestWithWorldPos(JGPoint p){
		JGPoint pWS;		
		if (parent != null)	
			pWS = this.parent.convertToWorldSpace(this.getPosition());
		else
			pWS = getPosition();
		
		//adjust for anchor point
		pWS = pWS.sub(new JGPoint(getContentSize().width * getAnchorPoint().x, getContentSize().height * getAnchorPoint().y));		
		
		JGRect bounds = new JGRect(pWS, getContentSize());
		return bounds.contains(p);
	}
	
	private void checkMouseInput(){
		JGInputManager im = JGInputManager.sharedInstance();
		if (this.hitTestWithWorldPos(im.getMousePosition())){
			
			mouseEntered = true;
			
			//test for mouse entered
			if (!oldMouseEntered){
				onMouseEnter();
			}
			
			//
			//mouse pressed / released
			//
			if (im.getMouseButtonState(1) && !oldMousePressed && !mousePressed){
				
				mousePressed = true;
				mouseStartPosition = im.getMousePosition();
				mouseMovedPosition = mouseStartPosition;
				mousePressBegan(mouseStartPosition);
				
				//block mouse input if this node swallows inputs
				if (swallowsMouseInput)
					im.setMouseInputBlocked(true);
			}
			
			//
			//mouse press moved
			//
			if (mousePressed && !mouseMovedPosition.equalsINT(im.getMousePosition())){
				mouseMovedPosition = im.getMousePosition();
				mousePressMoved(mouseMovedPosition);
			}
			
		}else{
			mouseEntered = false;
			
			//test for mouse exited
			if (oldMouseEntered){
				onMouseExit();
			}
		}
		//update old mouse entered last
		oldMouseEntered = mouseEntered;
		
		//check for mouse released even if the mouse doesn't lie within the nodes bounds
		if (mousePressed && !im.getMouseButtonState(1)){
			this.mousePressEnded(im.getMousePosition());
			mousePressed = false;
		}
	}
	
	// ----------------------------------------------
	// Getters
	// ----------------------------------------------
	
	public JGGame getGameWindow(){ return JGGame.sharedInstance(); }
	public JGPoint getPosition(){ return position; }
	public double getRotation(){ return rotation; }
	public JGPoint getScale(){ return scale; }
	public boolean getFlipX(){ return flipX; }
	public boolean getFlipY(){ return flipY; }
	public JGPoint getAnchorPoint(){ return anchorPoint; }
	public int getZOrder(){ return zOrder; }
	public ArrayList<JGNode> getChildren(){ return children; }
	public boolean getIsAlive(){return isAlive; }
	public AffineTransform getTransformationMatrix(){ return transformationMatrix; }
	public JGSize getContentSize(){ return contentSize; }

	public boolean getIsOpaque(){ return isOpaque; }
	public float getOpacity(){ return opacity; }
	
	// ----------------------------------------------
	// Setters
	// ----------------------------------------------
		
	public void setPosition(JGPoint p){
		setTransformDirty(); //mark transform dirty
		position = p;
	}
	
	public void setRotation(double r){
		setTransformDirty(); //mark transform dirty
		rotation = r;
	}
	
	public void setScale(JGPoint s){
		setTransformDirty(); //mark transform dirty
		scale = s;
	}
	
	public void setFlipX(boolean fx){
		setTransformDirty(); //mark transform dirty
		flipX = fx;
	}
	
	public void setFlipY(boolean fy){
		setTransformDirty(); //mark transform dirty
		flipY = fy;
	}
	
	public void setAnchorPoint(JGPoint ap){
		setTransformDirty(); //mark transform dirty
		anchorPoint = new JGPoint(JGUtilities.clamp(ap.x, 0.0f,1.0f),
				JGUtilities.clamp(ap.y, 0.0f,1.0f));
	}
	
	public void setZOrder(int z){
		zOrder = z;
	}
	
	public void setContentSize(JGSize cs){
		_transformDirty = true; //mark transform dirty
		contentSize = cs;
	}
	
	public void setTransformDirty(){
		_transformDirty = true; //mark transform dirty
		
		//relay dirty transform to all children
		for(JGNode aNode : children){
			aNode.setTransformDirty();
		}
	}
	
	public void setIsOpaque(boolean op){
		_transformDirty = true; //mark transform dirty
		
		isOpaque = op;
		//update all children to opaque
		for (JGNode aNode : children)
			aNode.setIsOpaque(isOpaque);
	}
	
	public void setOpacity(float o){
		if (!isOpaque)
			_transformDirty = true; //mark transform dirty
		opacity = JGUtilities.clampf(o, 0.0f, 1.0f);
		//update all children to opaque
		for (JGNode aNode : children)
			aNode.setOpacity(opacity);
	}
	
}
