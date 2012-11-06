package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.Row.RowState;

/**
 * This is the easy AI, it will work with a very basic set of instructions
 * othewise, it'll randomly choose. (Revise?)
 * @author John Thomson
 *
 */
public class EasyAI implements QubicAI {
	private QubicBoard board;
		
	public EasyAI (QubicBoard board) {
		this.board = board;
	}
	
	public Square go() {
		QubicBoard tBoard = (QubicBoard)board.clone();
		List<Square> openGrid = computeOpenGrid(tBoard.getGrid());
		PriorityQueue<Possibility> moves = computePossibilities(openGrid);
		System.out.println("There are " + moves.size() + " possible moves");
		System.out.println(moves);
		return moves.poll().s;
	}
	
	private PriorityQueue<Possibility> computePossibilities(List<Square> openGrid) {
		PriorityQueue<Possibility> moves = new PriorityQueue<Possibility>(); 
		for (int i = 0; i < openGrid.size(); i++) {
			Possibility p = new Possibility(openGrid.get(i));
			
			List<Row> tempRows = openGrid.get(i).containingRows();
			for (int j = 0; j < tempRows.size(); j++) {
				Row r = tempRows.get(j);
				if (r.getState() == RowState.MIXED)
					p.value -= 1;
				else if (r.getNumSelected() == 3) {
					if (r.getState() == RowState.COMPUTER) {
						p.value += 50000;
					} else if (r.getState() == RowState.HUMAN) {
						p.value += 4000;
					}
				} else if (r.getNumSelected() == 2) {
					if (r.getState() == RowState.COMPUTER) {
						p.value += 10;
					}
				} else if (r.getNumSelected() == 1) {
					if (r.getState() == RowState.COMPUTER) {
						p.value += 2;
					}
				} else if (r.getNumSelected() == 0) {
					p.value += 1;
				}
			}
			//System.out.println(p.s);
			moves.add(p);
		}
		return moves;
	}
	
	/**
	 * This computes every open square on the board.
	 * @param grid The grid of Squares
	 * @return grid List<Square>
	 */
	private List<Square> computeOpenGrid(List<Square> grid) {
		List<Square> openGrid = new ArrayList<Square>();
		for (Square s: grid) {
			if (s.getState() == null) {
				openGrid.add(s);
			}
		}
		return openGrid;
	}
	
	public String toString() {
		return "Easy";
	}
}
