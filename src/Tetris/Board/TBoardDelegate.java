package Tetris.Board;

import Tetris.Block.TShape;

public interface TBoardDelegate {
	
	public void gameOver();
	public void nextShapeChanged(TShape shape);
	public void scoreChanged(int score);
	public void linesChanged(int lines);
	public void levelChanged(int level);

}
