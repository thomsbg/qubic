package controller;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import model.QubicBoard;

/**
 * Holds the frame and board object needed to run Qubic. It is the base
 * level controller in an inheritance chain. This just initializes the frame
 * and provides some getters/setters for the various fields required by other
 * controllers in the inheritance chain. It also adds a window listener to the
 * frame, to implement the ask to save on close feature.
 * @author Blake Thomson
 */
class SimpleController {
	private JFrame frame;	
	private QubicBoard board;
	
	private static final int DEFAULT_WIDTH = 700;
	private static final int DEFAULT_HEIGHT = 500;
	
	SimpleController(QubicBoard b) {		
		this.board = b;
		frame = new JFrame("Welcome to Qubic");
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				handleConfirmSave();
			}
		});
	}
	
	/**
	 * This method is called when the window close button is clicked, or when
	 * the exit action is fired. It calls the handleSave method, which is overridden
	 * later in the ActionController class.
	 */
	void handleConfirmSave() {
		Toolkit.getDefaultToolkit().beep(); // Why doesn't this work?
		int result = JOptionPane.showConfirmDialog(frame,
				"Do you want to save the current game before you leave?",
				"Save Game?", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			if (handleSave())
				System.exit(0);
		} else if (result == JOptionPane.NO_OPTION)
			System.exit(0);
	}
	
	/**
	 * Overridden in the ActionController to do the actual work of saving the
	 * QubicBoard state to a file.
	 */
	boolean handleSave() {
		return false;
	}

	/**
	 * @return A reference to the QubicBoard currently being used to play the game.
	 */
	QubicBoard getBoard() {
		return board;
	}

	/**
	 * @param board A new QubicBoard to replace the existing one. It is used when opening
	 * a game save state.
	 */
	void setBoard(QubicBoard board) {
		this.board = board;
	}

	/**
	 * @return A reference to the outtermost JFrame, which contains the entire program.
	 */
	JFrame getFrame() {
		return frame;
	}
}
