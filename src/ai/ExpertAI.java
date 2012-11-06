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
 * This is the final result of John Thomson's research into recursive
 * backtracking.  While the failed PerfectAI was unable to complete the
 * initial calculations required to solve Qubic, this searches
 * a much smaller population of moves, not looking entirely into the 
 * future, only as many moves as your computer can handle.  Because it 
 * is capable of searching every move on the board, it can devise more
 * powerful strategies than the limited HardAI.  The more powerful your
 * computer, the harder this can be.  Unfortunately, due to the recursive
 * and inherently factorial nature of this algorithm, it becomes much slower
 * as you increase the search space.  But if there's a win, it'll find it. 
 * @author John Thomson
 *
 */
public class ExpertAI implements QubicAI {
	/**
	 * The number of moves to be computed each level, sorted by priority.
	 */
	private final int SEARCH_WIDTH_LIMIT = 15;
	/**
	 * The number of moves to serch into the future, this exponentially
	 * increases the computation time.
	 */
	private final int SEARCH_DEPTH_LIMIT = 5;
	
	private QubicBoard board;
	//private Player aiPlayer = Player.COMPUTER;
	private QubicAI backupAI;
	
	private RowState aiPlayer;
	private RowState otherPlayer;


	/**
	 * Constructs a new AI from the given QubicBoard.
	 * @param board
	 */
	public ExpertAI(QubicBoard board) {
		this.board = board;
		backupAI = new HardAI(board);
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
		try {
			Thread.sleep(100);
		} catch (Exception e) {}
		List<Square> openGrid = computeOpenGrid(board.getGrid());
		
		Square move = search(this.aiPlayer, board, openGrid, 1);
		if (move != null)
			return move;
		else {
			//System.out.println("Perfect recursive backtracking couldn't find a solution, reverting to dumbed down version.");
			return backupAI.go(aiPlayer);
		}
	}
	
	/**
	 * Recursively searches all likely candidates for a winning move, starting from
	 * the most probable and down to a designated cut off as defined by SEARCH_WIDTH_LIMIT.
	 * It uses the helper fuctions computerMove and playerMove to simulate the different
	 * possibilities.
	 * @param player
	 * @param board
	 * @param openGrid
	 * @param level
	 * @return winningMove otherwise null
	 */
	private Square search(RowState player, QubicBoard board, List<Square> openGrid, int level) {

		if (player == this.aiPlayer) //Player.COMPUTER)
			return computerMove(board, openGrid, level);
		else
			return playerMove(board, openGrid, level);
	}
	
	/**
	 * This function simulates a move by the computer.
	 * @param board
	 * @param openGrid
	 * @param level
	 * @return Square winningMove otherwise null.
	 */
	private Square computerMove(QubicBoard board, List<Square> openGrid, int level) {

		if (!testBoardState(level))
			return null;
		RowState curPlayer = aiPlayer;
		List<Square> openGrid2 = computeOpenGrid(board.getGrid());
		PriorityQueue<Possibility> possibleMoves = computePossibilities(openGrid2, curPlayer);
		Square thisMove = null;
		
		boolean guarnanteedWin = false;
		while (!possibleMoves.isEmpty()) {
			Possibility p = possibleMoves.poll();
			if (p.value >= 50000) {
				return p.s;
				}
			Square s = p.s;
			thisMove = s;
			
			//openGrid.remove(s);
			board.select(s);
			
			Square tempMove = playerMove(board, openGrid2, level + 1);
			
			board.undo();
			//openGrid.add(s);
			
			if (tempMove != null) {
				guarnanteedWin = true;
				break;
			}
		}
		if (guarnanteedWin) {
			return thisMove;
		} else {
			return null;
		}
	}
	
	
	/**
	 * This function simulates a move by the player.
	 * @param board
	 * @param openGrid
	 * @param level
	 * @return Square move otherwise null
	 */
	private Square playerMove(QubicBoard board, List<Square> openGrid, int level) {
		if (!testBoardState(level))
			return null;
		RowState curPlayer = otherPlayer;
		List<Square> openGrid2 = computeOpenGrid(board.getGrid());
		PriorityQueue<Possibility> possibleMoves = computePossibilities(openGrid2, curPlayer);
		
		Square thisMove = null;
		boolean guaranteedWin = true;
		while (!possibleMoves.isEmpty()) {
			Possibility p = possibleMoves.poll();
			if (p.value >= 50000) {
				return null;
			}
			Square s = p.s;
			thisMove = s;
			
			//openGrid.remove(s);
			board.select(s);
			
			Square tempMove = computerMove(board, openGrid2, level + 1);
			
			board.undo();
			//openGrid.add(s);
			
			if (tempMove == null) {
				guaranteedWin = false;
				break;
			}
		}
		
		if (guaranteedWin) 
			return thisMove;
		else 
			return null;
	}
	
	/**
	 * Tests to see if any of the halting conditions for recursion have occured.  These are:
	 * an endgame scenario being recursed into and reaching the SEARCH_DEPTH_LIMIT.
	 * @param level
	 * @return canContinue
	 */
	private boolean testBoardState(int level) {
		if (level >= SEARCH_DEPTH_LIMIT) {
			return false; //Reached the max depth of the search.
		}
		if (board.gameOver()) {
			//System.out.println("Error: endgame happened"); //Recursive stuff
			//System.out.println(board);
			return false;
		}
		return true;
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
		PriorityQueue<Possibility> result = new PriorityQueue<Possibility>();
		for (int i = 0; i <= SEARCH_WIDTH_LIMIT; i++) {
			if (!moves.isEmpty())
				result.add(moves.poll());
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
		return "Expert";
	}	
}
