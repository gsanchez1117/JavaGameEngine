package JGGameEngine;


import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.BitSet;



public class JGInputManager implements KeyListener, MouseListener, MouseMotionListener {

	private static JGInputManager instance = null; //singleton instance	
	private final BitSet bitset = new BitSet();
	private JGPoint mousePosition = new JGPoint();
	private boolean[] mouseButtonState = new boolean[MouseInfo.getNumberOfButtons()];
	private boolean mouseInputBlocked = false;
	
	protected JGInputManager(){}
		
	// -------------------------
	// Public Methods
	// -------------------------
	
	public static JGInputManager sharedInstance(){
		if (instance == null){
			instance = new JGInputManager();
		}
		return instance;
	}
	
	public boolean getKeyPressed(int keyEventID){
		return bitset.get(keyEventID);
	}
	
	public JGPoint getMousePosition(){return mousePosition; }
	public boolean getMouseButtonState(int button){ return mouseButtonState[button]; } //NOTE: 0 is no button 
	public boolean getMouseInputBlocked(){ return mouseInputBlocked; }
	public void setMouseInputBlocked(boolean blocked){ mouseInputBlocked = blocked; }
	
	// -------------------------
	// Key Listener Events
	// -------------------------
	
	@Override
	public synchronized void keyPressed(KeyEvent e) {
	    bitset.set(e.getKeyCode());
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		bitset.clear(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {}


	// -------------------------
	// Mouse Listener Events
	// -------------------------

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtonState[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtonState[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	// ------------------------------
	// Mouse Motion Listener Events
	// ------------------------------

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition = new JGPoint(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = new JGPoint(e.getPoint());
	}

}
