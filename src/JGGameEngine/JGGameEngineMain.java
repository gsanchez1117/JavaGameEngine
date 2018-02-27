package JGGameEngine;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class JGGameEngineMain extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public JGGameEngineMain() {

    	add(JGGame.sharedInstance()); //enter the game engine here
    	
        setResizable(false);
        pack();
        
        setTitle("JGGameEngine by Gabriel Sanchez");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
    }

	public static void main(String[] args) {
		    
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame ex = new JGGameEngineMain();
                ex.setVisible(true);                
            }
        });
		
	}

}
