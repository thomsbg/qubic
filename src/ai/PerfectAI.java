package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.QubicBoard.Player;
import model.Row.RowState;


public class PerfectAI implements QubicAI {
	private ComputerMove computerGoesFirst;
	private PlayerMove playerGoesFirst;
	private double maxPossibleMoves;
	private final int SEARCH_SPACE_LIMIT = 2;
	private PerfectAIFrame frame;
	
	private boolean factInit;
	private double[] factorials;
	
	private static double count;
	private QubicBoard qboard;
	private Player aiPlayer = Player.COMPUTER;
	private long startTime;
	private double remaining;
	private double completed;
	private double prev_completed;
		
	//private Move root;
	
	public PerfectAI (QubicBoard board) {
		this.qboard = board;
	}
	
	public Square go() {
		startTime = System.currentTimeMillis();
		count = 0;
		QubicBoard tBoard = (QubicBoard)qboard.clone();
		List<Square> openGrid = computeOpenGrid(tBoard.getGrid());
		//PriorityQueue<Possibility> moves = new PriorityQueue<Possibility>(); 
		//return search(tBoard, openGrid, Player.COMPUTER, 1);
		if (true)
			throw new RuntimeException("PerfectAI isn't ready to use yet!");
		return null;
		
	}
		
	public void activateInterface() {
		PerfectAIFrame frame = new PerfectAIFrame(this);
		frame.start();
	}
	
	public void updateInterface(PerfectAIFrame frame) {
		frame.completed = completed;
		frame.maxPossibleMoves = maxPossibleMoves;
		frame.prev_completed = prev_completed;
		frame.remaining = remaining;
		frame.startTime = startTime;
	}
	
	public void computeStrategy () {
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
	}
	
	private StrategyNode search(StrategyNode current, Player curPlayer, QubicBoard board, List<Square> openGrid, int level) {//, double remaining, double completed) {
		/// Diagnostic Code ///
		count++;
		//if (level > 35)
		//	return null;
		if (false) {//count % 500000 == 0) {
			//System.out.println ((char)27 + "[2J");
			System.out.println(//"" + count + " moves processed at " + (.001*(System.currentTimeMillis() - startTime)/count) + " seconds per operation \n" +
					//"and " + (count/(.001*(System.currentTimeMillis() - startTime))) + " moves processed per second\n" +
					//"Completed " + 100.*(completed/maxPossibleMoves) + "% of the calculated operations \n" +
					//"Moves that are out of the way: " + completed + "\n" +
					//"Total estimated moves: " + maxPossibleMoves + "\n" +
					(completed - prev_completed) + " moves computed since last time, this accounts for " + 100*(completed - prev_completed)/maxPossibleMoves + "%");
			//double time = (System.currentTimeMillis() - startTime) / (1. - (remaining)/maxPossibleMoves);
			double time = remaining*(.001*(System.currentTimeMillis() - startTime)/completed);
			//System.out.println(time);
			//Calendar cal = Calendar.getInstance();
			//cal.setTimeInMillis((long)time);
			//System.out.println("Estimated time to completion: " + cal.get(Calendar.YEAR) + " years and " + cal.get(Calendar.DAY_OF_YEAR) + " days." + "\n");
			prev_completed = completed;
		}
		/// End Diagnostic Code ///
		
		if (curPlayer == Player.COMPUTER) //CurPlayer was the player for the last move
			//Changing to this move...
			curPlayer = Player.HUMAN;
		else 
			curPlayer = Player.COMPUTER;
		
		if (board.gameOver()) {
			if (curPlayer == aiPlayer)
				return new EndGame();
			else
				return null;
		}
		
		PriorityQueue<Possibility> possibleMoves = computePossibilities(openGrid, curPlayer);
		
		//Make a new Node
		StrategyNode thisNode = null;
		if (board.catsGame())
			return null;
		double tempRemaining = 0;
		if (curPlayer == Player.COMPUTER) {
			thisNode = new ComputerMove();
			boolean guarnanteedWin = false;
			//for (int i = 0; i < openGrid.size(); i++) {
			while (!possibleMoves.isEmpty()) {
				Square s = possibleMoves.poll().s;
				//Square s = openGrid.get(i);
				openGrid.remove(s);
				board.select(s);
				StrategyNode tempNode = search(thisNode, curPlayer, board, openGrid, level + 1);//, remaining, completed);
				board.undo();
				openGrid.add(s);
				
				remaining -= factorial(level);
				completed += factorial(level);
				tempRemaining += factorial(level);
				
				if (tempNode != null) {
					thisNode.add(s, tempNode);
					guarnanteedWin = true;
					break;
				}
			}
			remaining += tempRemaining;
			remaining -= factorial(level - 1);
			completed -= tempRemaining;
			completed += factorial(level - 1);
			
			//System.out.println(level - 1);
			if (!guarnanteedWin) {
				return null;
			} else {
				return thisNode;
			}
		} else {
			thisNode = new PlayerMove();
			boolean guaranteedWin = true;
			//for (int i = 0; i < openGrid.size(); i++) {//(Square s : openGrid) {
			while (!possibleMoves.isEmpty()) {
				Square s = possibleMoves.poll().s;
				//Square s = openGrid.get(i);
				openGrid.remove(s);
				board.select(s);
				StrategyNode tempNode = search(thisNode, curPlayer, board, openGrid, level + 1);//, remaining, completed);
				board.undo();
				openGrid.add(s);
				remaining = remaining - factorial(level);
				completed += factorial(level);
				tempRemaining += factorial(level);
				if (tempNode == null) {
					guaranteedWin = false;
					break;
				}
				thisNode.add(s, tempNode);
			}
			remaining += tempRemaining;
			remaining -= factorial(level - 1);
			completed -= tempRemaining;
			completed += factorial(level - 1);
			
			if (!guaranteedWin) {
				return null;
			} else {
				return thisNode;
			}
		}
		//System.out.println("ERROR, Logic exception!");
		//return null;
	}
	
	
	
	
	
	
	
	private Square search(QubicBoard tempBoard, List<Square> openGrid, Player curPlayer, int level) {
		int limitSize = 1;
		PriorityQueue<Possibility> moves = computePossibilities(openGrid, curPlayer);
		Square move = null;
		if (curPlayer == Player.COMPUTER)
			curPlayer = Player.HUMAN;
		else 
			curPlayer = Player.COMPUTER;
		
		if (tempBoard.gameOver()) {
			//System.out.println("Endgame found \n" + tempBoard.getCurrentPlayer());
			//System.out.println();
			//System.out.println(tempBoard);
			return new Square(5,5,5);
		} else if (tempBoard.catsGame()) {
			//System.out.println("CatsGame found");
			//System.out.println(tempBoard);
			return null;
		} else {
			boolean guaranteedWin = false;
			//while (!moves.isEmpty()) {
			int size = moves.size();
			if (size > limitSize) //Limit the search to just the first 4 options in the
				size = limitSize; //priorityQueue - makes it go faster, shouldn't need this
			for (int i = 0; i < size; i++) {
				move = moves.poll().s;
				openGrid.remove(move);
				tempBoard.select(move);
				count++;
				//System.out.println(tempBoard);
				Square output = search(tempBoard, openGrid, curPlayer, level + 1);
				tempBoard.undo();
				openGrid.add(move);
				if (count % 100000 == 0) {
					System.out.println("" + count + " moves processed at " + (.001*(System.currentTimeMillis() - startTime)/count) + " seconds per operation \n" +
							"and " + (count/(.001*(System.currentTimeMillis() - startTime))) + " moves processed per second");
				}
				if (output != null) {
					if (curPlayer != aiPlayer) {
						guaranteedWin = false;
						//System.out.println("Human wins");
					break;
					} else {
						//System.out.println("Computer wins");
						guaranteedWin = true;
						break;
					}
				}
			}
			if (guaranteedWin == true) {
				if (level < 30)
					System.out.println("Guaranteed Win found after " + level + " moves, " + count + 
							" operations done, ");
				return move;
			}
		}
		return null;
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
	
	private interface StrategyNode {
		public void add(Square s, StrategyNode n);
	}
	
	private class PlayerMove implements StrategyNode{
		public Map<Square, StrategyNode> possibleMoves;
		
		public PlayerMove () {
			possibleMoves = new HashMap<Square, StrategyNode>();
		}
		
		public void add (Square s, StrategyNode n) {
			possibleMoves.put(s,(StrategyNode) n);
		}
	}
	
	private class ComputerMove implements StrategyNode {
		public StrategyNode nextMove;
		public Square move;
		
		public ComputerMove (){
			
		}
		
		public void add (Square s, StrategyNode n) {
			nextMove = (PlayerMove) n;
			move = s;
		}
	}
	
	private class EndGame implements StrategyNode {
		
		public EndGame () {	
		}
		
		public void add (Square s, StrategyNode n) {
			//Do nothing, this is an endcap node.
		}
	}
	
	public String toString() {
		return "Perfect";
	}
}