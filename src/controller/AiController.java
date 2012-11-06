package controller;

import model.QubicBoard;
import ai.DefensiveAI;
import ai.EasyAI;
import ai.ExpertAI;
import ai.HardAI;
import ai.QubicAI;

/**
 * A controller storing all the AIs used in the game. It has the getters
 * and setters necessary to allow the other controllers to communicate with it.
 * @author Blake Thomson
 */
class AIController extends SimpleController {
	private QubicAI firstPlayer;
	private QubicAI secondPlayer;
	
	private QubicAI defensiveAI;
	private QubicAI easyAI;
	private QubicAI hardAI;
	private QubicAI expertAI;
	
	AIController(QubicBoard b) {
		super(b);
		defensiveAI = new DefensiveAI(getBoard());
		easyAI = new EasyAI(getBoard());
		hardAI = new HardAI(getBoard());
		expertAI = new ExpertAI(getBoard());		
		firstPlayer = hardAI;
		secondPlayer = null;
	}
	
	/**
	 * @return If the first player is a human, returns null. If it is an AI,
	 * it returns a reference to that AI.
	 */
	QubicAI getFirstPlayer() {
		return firstPlayer;
	}
	
	/**
	 * @param player Sets the first player to be this AI. If passed null,
	 * the first player is a human.
	 */
	void setFirstPlayer(QubicAI player) {
		firstPlayer = player;
	}
	
	/**
	 * @return If the second player is a human, returns null. If it is an AI,
	 * it returns a reference to that AI.
	 */
	QubicAI getSecondPlayer() {
		return secondPlayer;
	}
	
	/**
	 * @param player Sets the second player to be this AI. If passed null,
	 * the second player is a human.
	 */
	void setSecondPlayer(QubicAI player) {
		secondPlayer = player;
	}

	/**
	 * @return A reference to a DefensiveAI.
	 */
	QubicAI getDefensiveAI() {
		return defensiveAI;
	}
	
	/**
	 * @return A reference to an EasyAI.
	 */
	QubicAI getEasyAI() {
		return easyAI;
	}

	/**
	 * @return A reference to a HardAI.
	 */
	QubicAI getHardAI() {
		return hardAI;
	}
	
	/**
	 * @return A reference to an ExpertAI.
	 */
	QubicAI getExpertAI() {
		return expertAI;
	}
}