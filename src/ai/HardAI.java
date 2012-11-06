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
	private Player aiPlayer = Player.COMPUTER;
	private int winlevel;
	
	public HardAI (QubicBoard board) {
		this.board = board;
	}
	
	public Square go() {
		QubicBoard tBoard = (QubicBoard)board.clone();
		List<Square> openGrid = computeOpenGrid(tBoard.getGrid());
		Square tempMove = search(tBoard, openGrid, 0);
		if (tempMove != null) {
			if (winlevel > 1)
				System.out.println("\nI will beat you in a maximum of " + winlevel + " moves!\n");
			else
				System.out.println("\n Sorry to tell you this, but you've lost.");
			return tempMove;
		} else {
			System.out.println("Hard recursion didn't work, using scoring method..");
			PriorityQueue<Possibility> moves = computePossibilities(openGrid, aiPlayer);
			System.out.println(moves);
			return moves.poll().s;
		}
	}
	
	private Square search(QubicBoard board, List<Square> openGrid, int level) {

		PriorityQueue<Possibility> moves = computePossibilities(openGrid, aiPlayer);
		
		if (moves.peek().value >= 50000) 
			return moves.poll().s;
		else if (board.gameOver()) {
			System.out.println("Error: endgame! \n " + board);
			return null;
		}
		else if (board.catsGame())
			return null;
		else if (level >= 63)
			return null;
		else {
			List<Row> rows = board.getRows();
			for (Row r : rows) {
				Player rowState = null;
				if (r.getState() == RowState.COMPUTER)
					rowState = Player.COMPUTER;
				else if (r.getState() == RowState.HUMAN)
					rowState = Player.HUMAN;
				
				if (r.getNumSelected() == 2 && rowState == aiPlayer) {
					//if (level == 0) {
						System.out.println(r);
					//}
					
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
						//Square opponentMove = tempMove.s.getOtherSquare();
						// Check if the human has a trivial win...
						
						//if (!openGrid.remove(sComp))
							//throw new RuntimeException("ERROR!!!"); //TODO: Remove this.
						List<Square> openGrid2 = computeOpenGrid(board.getGrid());
						PriorityQueue<Possibility> cMoves = computePossibilities(openGrid2, Player.HUMAN);
						if (cMoves.peek().value >= 50000)
							System.out.println("Human wins... \n" + board);
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
			/*
			while (!moves.isEmpty()) {
				Possibility tempMove = moves.poll();
				if (tempMove.value >= 20) {
					openGrid.remove(tempMove.s);
					board.select(tempMove.s);
					Square opponentMove = tempMove.s.getOtherSquare();
					//if (opponentMove == null)
						//throw new RuntimeException("AI picked the wrong row!");
					Square nextMove = null;
					if (opponentMove != null) { //This shouldn't be necessary...
						openGrid.remove(opponentMove);
						board.select(opponentMove);
						winlevel = level + 1;
						nextMove = search(board, openGrid, level +1);
						openGrid.add(opponentMove);
						board.undo();
					}
					openGrid.add(tempMove.s);
					board.undo();
					if (nextMove != null)
						return tempMove.s;
				}
			}
			*/
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