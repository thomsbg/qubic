package qubic.model;


/**
 * This is an object for storing and ranking possible moves.
 * @author John Thomson
 *
 */
public class Move implements Comparable<Move> {
	private Square s;
	private int value;
	private double rseed;
	
	public Move(Square s) {
		this.s = s;
		value = 0;
		rseed = Math.random();
	}
	
	/**
	 * Overrides the Object.compareTo function.  If two
	 * Possibilities are the same, it compares the seeds
	 * to add randomness to the game.
	 */
	public int compareTo(Move other) {
		int diff = -(value - other.value);
		if (diff == 0)
			diff = new Double(this.rseed).compareTo(other.rseed);
		return diff;
	}
	
	public Square getSquare() {
		return s;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int v) {
		value = v;
	}
	
	public String toString() {
		return s.toString() + " Priority " + value;
	}
}
