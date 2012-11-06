package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.QubicBoard.Player;
import model.Row.RowState;


public class ExpertAI implements QubicAI {
	//private ComputerMove computerGoesFirst;
	//private PlayerMove playerGoesFirst;
	private double maxPossibleMoves;
	private final int SEARCH_SPACE_LIMIT = 10;
	private final int SEARCH_DEPTH_LIMIT = 9;
	
	private boolean factInit;
	private double[] factorials;
	
	private static double count;
	private QubicBoard qboard;
	private Player aiPlayer = Player.COMPUTER;
	private QubicAI backupAI;
	private long startTime;
	private double remaining;
	private double completed;
	private double prev_completed;
		
	//private Move root;
	
	public ExpertAI (QubicBoard board) {
		this.qboard = board;
		backupAI = new HardAI(board);
	}
	
	public Square go() {
		startTime = System.currentTimeMillis();
		count = 0;
		QubicBoard tBoard = (QubicBoard)qboard.clone();
		List<Square> openGrid = computeOpenGrid(tBoard.getGrid());
		
		Square move = search(Player.HUMAN, tBoard, openGrid, 1);
		if (move != null)
			return move;
		else {
			System.out.println("Perfect recursive backtracking couldn't find a solution, reverting to dumbed down version.");
			return backupAI.go();
		}
	}
	
	/*public void computeStrategy () {
		maxPossibleMoves = factorial(2);
		//The game board MUST be clean, or else, the computation will not work!
		qboard.select(new Square(1,1,1));
		List<Square> openGrid = computeOpenGrid(qboard.getGrid());
		startTime = System.currentTimeMillis();
		count = 0;
		remaining = maxPossibleMoves;
		completed = 0;
		computerGoesFirst = (ComputerMove)search(computerGoesFirst, Player.COMPUTER, qboard, openGrid, 2);
		System.out.println("Calculations finished...");
		if (computerGoesFirst != null)
			System.out.println("Perfect Strategy FOUND!!!");
		else
			System.out.println("No unbeatable strategy found, there must be a bug in your logic");
	}*/
	
	public Square search(Player curPlayer, QubicBoard board, List<Square> openGrid, int level) {
		//System.out.println(curPlayer + " " + level);
		if (curPlayer == Player.COMPUTER) //CurPlayer was the player for the last move
			//Changing to this move...
			curPlayer = Player.HUMAN;
		else 
			curPlayer = Player.COMPUTER;
		
		if (level >= SEARCH_DEPTH_LIMIT) {
			//System.out.println("Maxed out at " + curPlayer);
			return null; //Reached the max depth of the search.
		}
			
		if (board.gameOver()) {
			System.out.println("Error: endgame happened");
			System.out.println(board);
			return null;
		}
		
		PriorityQueue<Possibility> possibleMoves = computePossibilities(openGrid, curPlayer);
		
		//Make a new Node
		Square thisMove = null;

		//Simulate a computer move.
		if (curPlayer == Player.COMPUTER) {
			boolean guarnanteedWin = false;
			while (!possibleMoves.isEmpty()) {
				Possibility p = possibleMoves.poll();
				if (p.value >= 50000) {
					//System.out.println("I have devised that in " + level + " moves, I will beat you.");
					return p.s;
				}
				Square s = p.s;
				thisMove = s;
				
				openGrid.remove(s);
				board.select(s);
				
				Square tempMove = search(curPlayer, board, openGrid, level + 1);//, remaining, completed);
				
				board.undo();
				openGrid.add(s);
				
				if (tempMove == null) {
					guarnanteedWin = true;
					break;
				}
			}
			if (guarnanteedWin) {
				//System.out.println("Other diag");
				//System.out.println("Endgame scenario detected @ move " + level);
				return thisMove;
			} else {
				return null;
			}
		} else { ///Simulate a player move.
			
			boolean guaranteedWin = true;
			while (!possibleMoves.isEmpty()) {
				Possibility p = possibleMoves.poll();
				if (p.value >= 50000) {
					//System.out.println("Virtual Player win");
					return null;
				} //else if (p.value >= 4000) {
					//return p.s;
				//}
				Square s = p.s;
				thisMove = s;
				
				openGrid.remove(s);
				board.select(s);
				
				Square tempMove = search(curPlayer, board, openGrid, level + 1);
				
				board.undo();
				openGrid.add(s);
				
				if (tempMove == null) {
					guaranteedWin = false;
					break;
				}
			}
			
			if (guaranteedWin) {
				return thisMove;
			} else {
				//System.out.println("Diag");
				return null;
			}
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
		
		int size = openGrid.size();
		for (int i = 0; i < size; i++) {
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
						p.value += 10;
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
		
		PriorityQueue<Possibility> sortedQueue = new PriorityQueue<Possibility>();
		
		for (int i = 0; i < SEARCH_SPACE_LIMIT && !moves.isEmpty(); i++) {
			sortedQueue.add(moves.poll());
		}
		return sortedQueue;
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
	
	public double factorial (int n) {
		int j = 63;
		if (!factInit) {
			factInit = true;
			factorials = new double[65];
			
			for (int k = 1; k <= 64; k++) {
				double result = 1.;
				for (int i = k; i <= j; i++) {
					int x = i;
					if (x > SEARCH_SPACE_LIMIT)
						x = SEARCH_SPACE_LIMIT;
					result = x*result;
				}
				factorials[k - 1] = result;
			}
			factorials[64] = 1.;
		}
		return factorials[n];
	}	
	public String toString() {
		return "Expert";
	}
}
