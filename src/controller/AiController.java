package controller;

import model.QubicBoard;
import ai.EasyAI;
import ai.ExpertAI;
import ai.HardAI;
import ai.QubicAI;
import ai.SimpleAI;

/**
 * A controller that houses the different AI's that can be used, and has
 * getter and setter methods to select which one to use.
 * @author Blake
 */
class AiController extends SimpleController {
	private QubicAI simpleAi;
	private QubicAI easyAi;
	private QubicAI hardAi;
	private QubicAI expertAi;
	
	private QubicAI currentAi;
	
	/**
	 * Constructs each AI giving each the board as a parameter, so it knows where
	 * and who it is playing. Assigns a pointer called currentAi to hardAi as a default.
	 * The GameController only asks for the currentAi when playing the game. The currentAi
	 * pointer is changed by calling the setCurrentAi method with a reference to which one
	 * of the AIs to use.
	 * @param b
	 */
	AiController(QubicBoard b) {
		super(b);
		simpleAi = new SimpleAI(getBoard());
		easyAi = new EasyAI(getBoard());
		hardAi = new HardAI(getBoard());
		expertAi = new ExpertAI(getBoard());
		
		currentAi = hardAi;
	}

	QubicAI getEasyAi() {
		return easyAi;
	}

	QubicAI getHardAi() {
		return hardAi;
	}

	QubicAI getExpertAi() {
		return expertAi;
	}

	QubicAI getSimpleAi() {
		return simpleAi;
	}
	
	QubicAI getCurrentAi() {
		return currentAi;
	}
	
	void setCurrentAi(QubicAI ai) {
		currentAi = ai;
	}
	
	/**
	 * A helper method called by handleChangeAI to keep what the user selected
	 * in the menu, and what shows up in the toolbar's radio buttons in sync.
	 * It is overridden later in ToolbarController.
	 */
	void updateSelectedAiRadioButton() {
	}
}
