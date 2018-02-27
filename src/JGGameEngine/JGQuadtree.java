package JGGameEngine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import JGGameEngine.ImpulseEngine.Body.IEBody;
import JGGameEngine.Node.JGNode;

public class JGQuadtree extends JGNode {
	
	private static final int maxNodesPerLeaf = 50;
	
	ArrayList<IEBody> nodes;
	public JGBoundingBox root;
	JGQuadtree[] children;
	boolean shouldDrawQuadTree = false;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	public JGQuadtree(){
		strokeColor = Color.red;
	}
	
	private JGQuadtree(JGBoundingBox newRoot, ArrayList<IEBody> Nodes){
		initialize(newRoot, Nodes);
		strokeColor = Color.red;
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	public void rebuild(ArrayList<IEBody> Nodes){
		initialize(null, Nodes);
	}
	
	public ArrayList<IEBody> getNodes(IEBody Node){
		if (children == null){ return nodes; }
		
		//if (Node.getBoundingBoxWorld().contains(root)){
		if (Node.getBoundingBoxLocal().contains(root)){
			return nodes;
		}
		
		ArrayList<IEBody> v = new ArrayList<IEBody>();
		for (int i = 0; i < 4; i++){
			//if (children[i].root.testBB(Node.getBoundingBoxWorld())){
			if (children[i].root.testBB(Node.getBoundingBoxLocal())){
				v.addAll(children[i].getNodes(Node));
			}
		}
		return v;
	}
	
	@Override
	public void midDraw(float dt, Graphics g){
		if (shouldDrawQuadTree){
			JGGraphics.drawNodeRect(
					this, 
					new JGPoint((int)root.min.x, (int)root.min.y), 
					root.size(), 
					g
			);
		}
	}
		
	//////////////////////
	//Private Methods	//
	//////////////////////
	
	private void initialize(JGBoundingBox newRoot, ArrayList<IEBody> Nodes){
		nodes = Nodes;
		children = null;
		
		if (newRoot == null)
			root = new JGBoundingBox(nodes);
		else
			root = newRoot;
		
		//should we go deeper?
		if (nodes.size() > maxNodesPerLeaf){
			children = new JGQuadtree[4];
			for (int i = 0; i < 4; i++){
				JGBoundingBox bb = root.cut(i);
				children[i] = new JGQuadtree(bb, Prune(nodes, bb));
				//addChild(children[i]);
			}
		}
	}
	
	private ArrayList<IEBody> Prune(ArrayList<IEBody> ns, JGBoundingBox bb){
		ArrayList<IEBody> inside = new ArrayList<IEBody>();
		for (IEBody n : ns){
			//JGBoundingBox check = n.getBoundingBoxWorld();
			JGBoundingBox check = n.getBoundingBoxLocal();
			if (bb.testBB(check)){
				inside.add(n);
			}
		}
		return inside;
	}
	
}
