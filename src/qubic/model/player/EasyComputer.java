package qubic.model.player;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import qubic.model.Board;
import qubic.model.Move;
import qubic.model.Row;
import qubic.model.Square;

/**
 * This is the easy AI, it will work with a very basic set of instructions
 * otherwise, it'll randomly choose. 
 * @author John Thomson
 */
public class EasyComputer extends Player {
	private Board board;
		
	/**
	 * Constructs a new AI from the given QubicBoard.
	 * @param board
	 */
	public EasyComputer(Board b, boolean first) {
		super(first);
		board = b;
	}
	
	/**
	 * Computes a priority queue of all possible moves that can be made and
	 * ranks them in order, based on simple rules.
	 */
	@Override
	public void play() {
		List<Square> openSquares = board.getOpenSquares();
		Queue<Move> moves = new PriorityQueue<Move>(); 
		for (Square s : openSquares) {
			Move m = new Move(s);			
			List<Row> parentRows = s.getParentRows();
			
			for (Row r : parentRows) {
				int value = m.getValue();
				if (r.isMixed())
					value -= 1;
				else if (r.getOwner() == this) { // I own the row
					if (r.getNumSelected() == 3)
						value += 1000;
					else if (r.getNumSelected() == 2)
						value += 100;
					else if (r.getNumSelected() == 1)
						value += 10;
				} else if (r.getOwner() != null) { // You own the row
					if (r.getNumSelected() == 3)
						value += 500;
					else if (r.getNumSelected() == 2)
						value += 50;
					else if (r.getNumSelected() == 1)
						value += 5;
				} else // The row is empty
					value += 1;
				m.setValue(value);
			}
			moves.add(m);
		}
		
		board.endTurn(moves.poll().getSquare());
	}
	
	/**
	 * Returns the String representation of the AI.
	 */
	public String toString() {
		return "EasyComputer";
	}
}
