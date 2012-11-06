package ai;

import java.util.Random;

import model.Square;

/**
 * This is an object for storing and ranking possible moves.
 * @author John Thomson
 *
 */
public class Possibility implements Comparable{
	public Square s;
	public int value;
	public int seed;
	
	public Possibility(Square s) {
		this.s = s;
		value = 0;
		Random r = new Random();
		seed = r.nextInt(100);
	}
	
	/**
	 * Overrides the Object.compareTo function.  If two
	 * Possibilities are the same, it compares the seeds
	 * to add randomness to the game.
	 */
	public int compareTo(Object o) {
		Possibility other = (Possibility)o;
		int diff = -(value - other.value);
		if (diff == 0)
			diff = this.seed - other.seed;
		return diff;
	}
	
	
	public String toString() {
		return "" + s.toString() + " Priority " + value;
	}
}
