package JGGameEngine;

public class JGBitMask {
		
	private int mask;
	
	//////////////////
	//Constructors	//
	//////////////////
	
	/**
	 * Default Constructor. Creates a new instance of JGBitmask with all flags cleared.
	 */
	public JGBitMask(){
		mask = 0;
	}
	
	//////////////////////
	//Public Methods	//
	//////////////////////
	
	/**
	 * Checks if a flag is set. Flags are numbered 0 - 30 inclusive. The flag parameter is clamped to those values. 
	 * @param flag - the flag to check... (0-30) inclusive
	 * @return - true if the flag is set. false if not.
	 */
	public boolean checkFlag(int flag){
		flag = (int)JGUtilities.clamp(flag, 0, 30);
		flag = (int)Math.pow(2, flag);
		return (mask & flag) != 0;
	}
	
	public boolean testMask(JGBitMask bm){
		return ((mask & bm.getMask()) != 0);
	}
	
	public boolean allClear(){
		return mask == 0;
	}
	
	public void setFlag(int flag){
		flag = (int)JGUtilities.clamp(flag, 0, 30);
		flag = (int)Math.pow(2, flag);
		mask = mask | flag;
	}
	
	public void clearFlag(int flag){
		flag = (int)JGUtilities.clamp(flag, 0, 30);
		flag = (int)Math.pow(2, flag);
		mask = mask & -flag;
	}
	
	public void clearAll(){ mask = 0; }
	
	public void toggleFlag(int flag){
		flag = (int)JGUtilities.clamp(flag, 0, 30);
		flag = (int)Math.pow(2, flag);
		mask = mask ^ flag;
	}
	
	public int getMask(){ return mask; }
	
	@Override
	public String toString(){
		return Integer.toBinaryString(mask);
	}

}
