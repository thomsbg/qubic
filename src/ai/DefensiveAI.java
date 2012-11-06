package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.QubicBoard.Player;
import model.Row.RowState;

/**
 * This is the Defensive AI, it will block virtually every move
 * that you make, requiring 
 * @author John Thomson
 *
 */
public class DefensiveAI implements QubicAI {
	private QubicBoard board;
	private RowState aiPlayer;
	private RowState otherPlayer;
		
	/**
	 * Constructs a new AI from the given QubicBoard.
	 * @param board
	 */
	public DefensiveAI(QubicBoard board) {
		this.board = board;
	}
	
	/**
	 * Sets the state of the AI such that when it chooses its moves
	 * it will choose them on behalf of the supplied Player.
	 * @param aiPlayer
	 */
	private void setAI(Player aiPlayer) {
		if (aiPlayer == Player.FIRST) {
			this.aiPlayer = RowState.FIRST;
			this.otherPlayer = RowState.SECOND;
		} else {
			this.aiPlayer = RowState.SECOND;
			this.otherPlayer = RowState.FIRST;
		}
	}
	
	/**
	 * Makes the AI tell you what it's move will be,
	 * the controller needs to actually make the move.
	 * The parameter aiPlayer is the Player that the ai
	 * will be acting for.
	 * @param aiPlayer
	 * @return move
	 */
	public Square go(Player aiPlayer) {
		setAI(aiPlayer);
		List<Square> openGrid = computeOpenGrid(board.getGrid());
		PriorityQueue<Possibility> moves = computePossibilities(openGrid);
		return moves.poll().s;
	}
	
	/**
	 * Computes a priority queue of all possible moves that can be made and
	 * ranks them in order of value to the AI.
	 * @param openGrid
	 * @return possibilities
	 */
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
					if (r.getState() == aiPlayer) {
						p.value += 50000;
					} else if (r.getState() == otherPlayer) {
						p.value += 4000;
					}
				} else if (r.getNumSelected() == 2) {
					if (r.getState() == otherPlayer) {
						p.value += 10;
					} else if (r.getState() == aiPlayer)
						p.value += 2;
				} else if (r.getNumSelected() == 1) {
					if (r.getState() == aiPlayer) {
						p.value += 1;
					} else if (r.getState() == otherPlayer)
						p.value += 2;
				} else if (r.getNumSelected() == 0) {
					p.value += 1;
				}
			}
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
	
	/**
	 * Returns the String representation of the AI.
	 */
	public String toString() {
		return "Easy";
	}
}
