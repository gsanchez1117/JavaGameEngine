package Tetris.Scenes;

public class TPlayerScore {

	String name;
	long score;
	
	public TPlayerScore(String Name, long Score){
		name = Name;
		score = Score;
	}
	
	public String getName(){ return name; }
	public long getScore(){ return score; }
	
}
