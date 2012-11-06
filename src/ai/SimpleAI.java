package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.QubicBoard;
import model.Square;
/**
 * A really stupid AI designed for testing the QubicBoard and other
 * components of the game.
 * @author John Thomson
 *
 */
public class SimpleAI implements QubicAI{
	private QubicBoard board;
	public SimpleAI(QubicBoard board) {
		this.board = board;
	}
	
	/**
	 * Randomly selects an open square on the board.
	 */
	public Square go() {
		List<Square> grid = board.getGrid();
		List<Square> openGrid = new ArrayList<Square>();
		for (Square s: grid) {
			if (s.getState() == null) {
				openGrid.add(s);
			}
		}
		Random r = new Random();
		int index = r.nextInt(openGrid.size());
		return openGrid.get(index);
	}
	
	public String toString() {
		return "Simple";
	}
}
