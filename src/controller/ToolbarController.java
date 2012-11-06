package controller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import model.QubicBoard;
import ai.QubicAI;

/**
 * A controller to implement a toolbar to the north of the frame.
 * It contains the new, open, save, undo, and exit buttons in a BoxLayout,
 * along with radio buttons letting the user change the current AI.
 * @author Blake
 */
class ToolbarController extends MenuController {
	private JRadioButton[] buttons;
	
	ToolbarController(QubicBoard b) {
		super(b);
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));		
		toolbar.setFloatable(false);
		toolbar.add(getNewGameAction());
		toolbar.add(getOpenAction());
		toolbar.add(getSaveAction());
		toolbar.add(getUndoAction());
		//toolbar.add(getRedoAction());
		toolbar.add(getOptionsAction());
		toolbar.add(Box.createGlue());
		
		buttons = new JRadioButton[4];
		ButtonGroup group = new ButtonGroup();
		buttons[0] = makeRadioButton(getSimpleAi());
		buttons[1] = makeRadioButton(getEasyAi());
		buttons[2] = makeRadioButton(getHardAi());
		buttons[3] = makeRadioButton(getExpertAi());
		toolbar.add(new JLabel("Difficulty: "));
		for (JRadioButton rb: buttons) {
			group.add(rb);
			toolbar.add(rb);
			updateSelectedAiRadioButton();
		}
		toolbar.add(Box.createGlue());
		toolbar.add(getExitAction());
		getFrame().add(toolbar, BorderLayout.NORTH);
	}
	
	/**
	 * A helper method used in the constructor to reduce redundancy in making the
	 * radio buttons for changing the current AI.
	 * @param name The name of the AI associated with this button.
	 * @param q The reference to the AI to be put into action when the button is selected.
	 * @return The JRadioButton made according to the given parameters.
	 */
	private JRadioButton makeRadioButton(QubicAI q) {
		final QubicAI ai = q;
		JRadioButton b = new JRadioButton(q.toString());
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCurrentAi(ai);
			}
		});
		b.setName(q.toString());
		return b;
	}
	
	/**
	 * This overrides the method from MenuController designed to keep the
	 * radio buttons on the toolbar in sync with the currently selected AI.
	 */
	@Override
	void updateSelectedAiRadioButton() {
		for (JRadioButton rb: buttons) {
			if (rb.getName().equals(getCurrentAi().toString())) {
				rb.setSelected(true);
			}
		}
	}
}
