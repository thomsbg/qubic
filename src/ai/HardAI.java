package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.QubicBoard.Player;
import model.Row.RowState;

public class HardAI implements QubicAI {

	//private static double count;
	private QubicBoard board;
	private Player aiPlayer = Player.COMPUTER;
	private int winlevel;
	//private Move root;
	
	public HardAI (QubicBoard board) {
		this.board = board;
	}
	
	public Square go() {
		//count = 0;
		QubicBoard tBoard = (QubicBoard)board.clone();
		List<Square> openGrid = computeOpenGrid(tBoard.getGrid());
		//PriorityQueue<Possibility> moves = new PriorityQueue<Possibility>(); 
		Square tempMove = search(tBoard, openGrid, 1);
		if (tempMove != null) {
			System.out.println("\nI will beat you in a maximum of " + winlevel + " moves!\n");
			return tempMove;
		} else {
			System.out.println("Hard recursion didn't work, using scoring method..");
			PriorityQueue<Possibility> moves = computePossibilities(openGrid, aiPlayer);
			System.out.println(moves);
			return moves.poll().s;
		}
	}
	
	private Square search(QubicBoard tempBoard, List<Square> openGrid, int level) {
		PriorityQueue<Possibility> moves = computePossibilities(openGrid, aiPlayer);
		
		if (moves.peek().value >= 4000) {
			return moves.poll().s;
		} else {
			while (!moves.isEmpty()) {
				Possibility tempMove = moves.poll();
				if (tempMove.value >= 20) {
					openGrid.remove(tempMove.s);
					tempBoard.select(tempMove.s);
					Square opponentMove = tempMove.s.getOtherSquare();
					//if (opponentMove == null)
						//throw new RuntimeException("AI picked the wrong row!");
					Square nextMove = null;
					if (opponentMove != null) { //This shouldn't be necessary...
						openGrid.remove(opponentMove);
						tempBoard.select(opponentMove);
						winlevel = level + 1;
						nextMove = search(tempBoard, openGrid, level +1);
						openGrid.add(opponentMove);
						tempBoard.undo();
					}
					openGrid.add(tempMove.s);
					tempBoard.undo();
					if (nextMove != null)
						return tempMove.s;
				}
			}
			return null;
		}
	}
	
	private PriorityQueue<Possibility> computePossibilities(List<Square> openGrid, Player player) {
		RowState thisPlayer;
		RowState otherPlayer;
		if (player == Player.COMPUTER){
			thisPlayer = RowState.COMPUTER;
			otherPlayer = RowState.HUMAN;
		} else {
			thisPlayer = RowState.HUMAN;
			otherPlayer = RowState.COMPUTER;
		}
		PriorityQueue<Possibility> moves = new PriorityQueue<Possibility>(); 
		for (int i = 0; i < openGrid.size(); i++) {
			Possibility p = new Possibility(openGrid.get(i));
			
			List<Row> tempRows = openGrid.get(i).containingRows();
			for (int j = 0; j < tempRows.size(); j++) {
				Row r = tempRows.get(j);
				if (r.getState() == RowState.MIXED)
					p.value -= 1;
				else if (r.getNumSelected() == 3) {
					if (r.getState() == thisPlayer) {
						p.value += 50000;
					} else if (r.getState() == otherPlayer) {
						p.value += 4000;
					}
				} else if (r.getNumSelected() == 2) {
					if (r.getState() == thisPlayer) {
						p.value += 20;
					}
				} else if (r.getNumSelected() == 1) {
					if (r.getState() == thisPlayer) {
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
		return "Hard";
	}
}