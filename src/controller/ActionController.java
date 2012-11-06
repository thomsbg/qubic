package controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.QubicBoard;

/**
 * A controller storing all the action objects and methods used by those
 * objects. Stub methods are overridden in subclasses, depending on where
 * they are implemented (i.e. menu, toolbar, etc.)
 * @author Blake
 */
class ActionController extends AiController {
	private NewGameAction newGameAction;
	private OpenAction openAction;
	private SaveAction saveAction;	
	private ExitAction exitAction;
	private UndoAction undoAction;
	private OptionsAction optionsAction;
	private AIAction aiAction;
	
	private JFileChooser chooser;
	
	/**
	 * Constructs six action objects using the inner classes defined in ActionController.
	 * It makes an action for each: starting a new game, opening a game, saving a game,
	 * exiting the game, undoing a pair of moves, and changing the AI difficulty.
	 * @param b A reference to a QubicBoard object in order to play the game!
	 */
	ActionController(QubicBoard b) {
		super(b);
		chooser = new JFileChooser();
		
		newGameAction = new NewGameAction("New", new ImageIcon(getClass().getResource("/resources/new.gif")));
		openAction = new OpenAction("Open", new ImageIcon(getClass().getResource("/resources/open.gif")));
		saveAction = new SaveAction("Save", new ImageIcon(getClass().getResource("/resources/save.gif")));
		exitAction = new ExitAction("Exit", new ImageIcon(getClass().getResource("/resources/stop.gif")));
		undoAction = new UndoAction("Undo", new ImageIcon(getClass().getResource("/resources/undo.gif")));
		aiAction = new AIAction("Computer Difficulty", new ImageIcon(getClass().getResource("/resources/dialog.gif")));
		optionsAction = new OptionsAction("Options", new ImageIcon(getClass().getResource("/resources/options.gif")));
	}

	/**
	 * An action to show a dialog and clear the board to make a new game.
	 */
	class NewGameAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public NewGameAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Starts a new game");
		}
		
		public void actionPerformed(ActionEvent e) {
			int result;
			if (!getBoard().gameOver()) {
				result = JOptionPane.showOptionDialog(getFrame(),
					"Do you really want to start a new game?\n" +
					"You will lose any current progress...",
					"New", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, null, null);
			} else
				result = JOptionPane.YES_OPTION;
			if (result == JOptionPane.YES_OPTION)
				handleNewGame();
		}
	}
	
	/**
	 * Overridden later in GameController.
	 */
	void handleNewGame() {
	}
	
	/**
	 * An action to show a file dialog to select a data file to load from.
	 */
	class OpenAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public OpenAction(String name, Icon icon) {
			super(name, icon);
			setEnabled(true);
			putValue(SHORT_DESCRIPTION, "Opens a saved game");
		}
		
		public void actionPerformed(ActionEvent e) {
			//chooser.setCurrentDirectory(new File("."));
			chooser.setSelectedFile(new File("board.qbd"));
			int result = chooser.showOpenDialog(getFrame());
			if (result == JFileChooser.APPROVE_OPTION) {
				try {
					File file = chooser.getSelectedFile();
					FileInputStream fis = new FileInputStream(file.getPath());
					ObjectInputStream input = new ObjectInputStream(fis);
					QubicBoard newBoard = (QubicBoard) input.readObject();
					handleNewBoard(newBoard);
					System.out.println("I opened: " + file.getPath());
				} catch (Exception error) {
					throw new RuntimeException(error.toString());
				}
			} else {
				System.out.println("Not opening a file after all");
			}
		}
	}
	
	/**
	 * Overridden later in GameController.
	 * @param newBoard A saved QubicBoard object that will replace the current one
	 * when opening a new game.
	 */
	void handleNewBoard(QubicBoard newBoard) {
	}
	
	/**
	 * An action to open a file dialog to select the file to save to.
	 */
	class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public SaveAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Saves the current game");
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e) {
			handleSave();
		}
	}
	
	/**
	 * Overwrites the previous defenition of the method to only fire when
	 * the SaveAction object is enabled. If this condition is true, it just
	 * calls super.handleConfirmSave();
	 */
	@Override
	void handleConfirmSave() {
		if (saveAction.isEnabled())
			super.handleConfirmSave();
		else
			System.exit(0);
	}
	
	/**
	 * The code that brings up the file chooser dialog box and allows the
	 * user to save the QubicBoard state to a file.
	 */
	@Override
	void handleSave() {
		//chooser.setCurrentDirectory(new File("."));
		chooser.setSelectedFile(new File("board.qbd"));
		int result = chooser.showSaveDialog(getFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				FileOutputStream fos = new FileOutputStream(file.getPath());
				ObjectOutputStream output = new ObjectOutputStream(fos);
				output.writeObject(getBoard());
				System.out.println("I saved: " + file.getPath());
			} catch (Exception error) {
				throw new RuntimeException(error.toString());
			}
			saveAction.setEnabled(!file.exists());
			if (!openAction.isEnabled())
				openAction.setEnabled(file.exists());
		} else {
			System.out.println("Not saving a file after all");
		}
	}
	
	/**
	 * A very simple action to exit the program.
	 */
	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public ExitAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Exits the game");
		}
		
		public void actionPerformed(ActionEvent e) {
			handleConfirmSave();
		}
	}
	
	/**
	 * An action to tell the model to undo the last pair of moves.
	 * It is only active when it is the human's turn to play, so it always
	 * undoes the last computer move, and the human's move before that.
	 */
	class UndoAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public UndoAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Undo the last move");
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (getBoard().canUndo()) {				
				System.out.println("undoing...");
				getBoard().undo();
				handleUndo();
				if (getBoard().canUndo()) {
					getBoard().undo();
					handleUndo();
				} else
					handleNewGame();
				setEnabled(getBoard().canUndo());
				saveAction.setEnabled(getBoard().canUndo());
				System.out.println(getBoard());
				System.out.println("-----------------------------------------");
			}
		}
	}
	
	/**
	 * Overridden later in GameController. It is needed to update the
	 * history panel after the board has changed.
	 */
	void handleUndo() {
	}
	
	/**
	 * An action to change the computer's difficulty. Calls an external method,
	 * handleChangeAI(), which is overridden later.
	 */
	class AIAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public AIAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Change the computer's level of difficulty");
		}
		
		public void actionPerformed(ActionEvent e) {
			handleChangeAI();
		}
	}
	
	/**
	 * Actually does the changing of the AI, is overridden later by introducing a dialog.
	 * May be overridden differently, depending on what interface is used to change the AI.
	 */
	void handleChangeAI() {
	}

	class OptionsAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		private OptionsDialog dialog;
		public OptionsAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Change game preferences");
			dialog = new OptionsDialog(getFrame(), ActionController.this);
		}
		
		public void actionPerformed(ActionEvent e) {
			dialog.start();
		}
	}
	
	AIAction getAiAction() {
		return aiAction;
	}

	ExitAction getExitAction() {
		return exitAction;
	}

	NewGameAction getNewGameAction() {
		return newGameAction;
	}

	OpenAction getOpenAction() {
		return openAction;
	}

	SaveAction getSaveAction() {
		return saveAction;
	}

	UndoAction getUndoAction() {
		return undoAction;
	}
	
	OptionsAction getOptionsAction() {
		return optionsAction;
	}
}
