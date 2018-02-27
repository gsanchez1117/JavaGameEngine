package JGGameEngine.AStar;

import JGGameEngine.JGPoint;

public class JGSearchNode {

	JGPoint position;
	int cost;
	int pathCost;
	JGSearchNode next;
	JGSearchNode nextListElem;
	
	public JGSearchNode(JGPoint Position, int Cost, int PathCost, JGSearchNode Next){
		
		position = Position;
		cost = Cost;
		pathCost = PathCost;
		next = Next;
		
	}
	
	public JGPoint getPosition(){
		return position;
	}
	
	public JGSearchNode getNext(){
		return next;
	}
	
}
