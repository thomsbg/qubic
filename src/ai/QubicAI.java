package ai;

import model.Square;

/**
 * A basic interface for AI's in Qubic.
 * @author John Thomson
 *
 */
public interface QubicAI {
	/**
	 * Makes the AI tell you what it's move will be,
	 * the controller needs to actually make the move.
	 * @return move
	 */
	public Square go(); 
	
}
