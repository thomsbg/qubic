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

	private QubicBoard board;
	private int winlevel;
	private RowState aiPlayer;
	private RowState otherPlayer;
	
	public HardAI (QubicBoard board) {
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
		Square tempMove = search(board, openGrid, 0);
		if (tempMove != null) {
			if (winlevel >= 1)
				System.out.println("" + aiPlayer + " will beat you in a maximum of " + winlevel + " moves!");
			else
				System.out.println("\n Sorry to tell you this, but you've lost.");
			return tempMove;
		} else {
			//System.out.println("Hard recursion didn't work, using scoring method..");
			PriorityQueue<Possibility> moves = computePossibilities(openGrid, this.aiPlayer);
			return moves.poll().s;
		}
	}
	
	/**
	 * Recursively searches through the future possible moves.  If the
	 * conditions are right, it will return a move guaranteeing a win.
	 * @param board
	 * @param openGrid
	 * @param level
	 * @return
	 */
	private Square search(QubicBoard board, List<Square> openGrid, int level) {

		PriorityQueue<Possibility> moves = computePossibilities(openGrid, aiPlayer);
		
		if (moves.peek().value >= 50000) 
			return moves.poll().s;
		else if (board.gameOver()) {
			//System.out.println("Error: endgame! \n " + board);
			return null;
		}
		else if (board.catsGame())
			return null;
		else if (level >= 63)
			return null;
		else {
			List<Row> rows = board.getRows();
			for (Row r : rows) {

				
				if (r.getNumSelected() == 2 && r.getState() == aiPlayer) {
					
					List<Square> squares = r.getUnselectedSquares();
					for (int i = 0; i <= 1; i++) {
						Square sComp = null;
						Square sHum = null;

						if (i == 0) {
							sComp = squares.get(0);
							sHum = squares.get(1);
						} else {
							sComp = squares.get(1);
							sHum = squares.get(0);
						}
						board.select(sComp);
						// Check if the human has a trivial win...
						List<Square> openGrid2 = computeOpenGrid(board.getGrid());
						PriorityQueue<Possibility> cMoves = computePossibilities(openGrid2, otherPlayer);
						Square nextMove = null;
						if (!(cMoves.peek().value >= 50000)) {
							board.select(sHum);
							//openGrid.remove(sHum);
							winlevel = level + 1;
							nextMove = search(board, openGrid, level +1);
							board.undo();
							//openGrid.add(sHum);
						}
						board.undo();
						//openGrid.add(sComp);
						if (nextMove != null)
							return sComp;
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * Computes a priority queue of all possible moves that can be made and
	 * ranks them in order of value to the AI.
	 * @param openGrid
	 * @return possibilities
	 */
	private PriorityQueue<Possibility> computePossibilities(List<Square> openGrid, RowState player) {
		RowState thisPlayer = player;
		RowState otherPlayer;
		
		if (player == RowState.FIRST)
			otherPlayer = RowState.SECOND;
		else
			otherPlayer = RowState.FIRST;
			
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
					} else if (r.getState() == otherPlayer)
						p.value += 3;
				} else if (r.getNumSelected() == 1) {
					if (r.getState() == thisPlayer) {
						p.value += 2;
					}
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
		return "Hard";
	}
}