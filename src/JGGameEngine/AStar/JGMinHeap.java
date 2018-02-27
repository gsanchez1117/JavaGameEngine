package JGGameEngine.AStar;

public class JGMinHeap {
	
	JGSearchNode listHead;
	
	public JGMinHeap(){
		listHead = null;
	}
	
	public boolean hasNext(){
		return listHead != null;
	}
	
	public void add(JGSearchNode item){
		if (listHead == null){
			listHead = item;
		}else if (listHead.next == null && item.cost <= listHead.cost){
			item.nextListElem = listHead;
			listHead = item;
		}else{
			JGSearchNode ptr = listHead;
			while (ptr.nextListElem != null && ptr.nextListElem.cost < item.cost)
				ptr = ptr.nextListElem;
			item.nextListElem = ptr.nextListElem;
			ptr.nextListElem = item;
		}
	}
	
	public JGSearchNode extractFirst(){
		JGSearchNode result = listHead;
		listHead = listHead.nextListElem;
		return result;
	}

}
