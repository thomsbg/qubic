package controller;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import model.QubicBoard;

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
		toolbar.add(getExitAction());
		getFrame().add(toolbar, BorderLayout.NORTH);
	}
}
