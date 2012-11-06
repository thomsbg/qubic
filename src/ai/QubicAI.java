package ai;

import model.Square;
import model.QubicBoard.Player;

/**
 * A basic interface for AI's in Qubic.
 * @see EasyAI, DefensiveAI, ExpertAI, HardAI
 * @author John Thomson
 *
 */
public interface QubicAI {
	/**
	 * Makes the AI tell you what it's move will be,
	 * the controller needs to actually make the move.
	 * The parameter aiPlayer is the Player that the ai
	 * will be acting for.
	 * @param aiPlayer
	 * @return move
	 */
	public Square go(Player aiPlayer); 
	
}
