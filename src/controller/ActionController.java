package controller;

import java.awt.Color;
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
 * @author Blake Thomson
 */
class ActionController extends ViewController {
	private NewGameAction newGameAction;
	private OpenAction openAction;
	private SaveAction saveAction;	
	private UndoAction undoAction;
	private RedoAction redoAction;
	private OptionsAction optionsAction;
	private ExitAction exitAction;
	
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
		undoAction = new UndoAction("Undo", new ImageIcon(getClass().getResource("/resources/undo.gif")));
		redoAction = new RedoAction("Redo", new ImageIcon(getClass().getResource("/resources/redo.gif")));
		optionsAction = new OptionsAction("Options", new ImageIcon(getClass().getResource("/resources/options.gif")));
		exitAction = new ExitAction("Exit", new ImageIcon(getClass().getResource("/resources/stop.gif")));
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
			chooser.setCurrentDirectory(new File("."));
			chooser.setSelectedFile(new File("board.qbd"));
			int result = chooser.showOpenDialog(getFrame());
			if (result == JFileChooser.APPROVE_OPTION) {
				try {
					File file = chooser.getSelectedFile();
					FileInputStream fis = new FileInputStream(file.getPath());
					ObjectInputStream input = new ObjectInputStream(fis);
					
					QubicBoard newBoard = (QubicBoard) input.readObject();
					setBoard(newBoard);
					getBoard().addListener(getMainView());
					getBoard().addListener(get3DView());
					getBoard().addListener(getHistoryPanel());
					
					Color[] colors = (Color[]) input.readObject();
					handleChangedColors(colors);
					
					String s = (String) input.readObject();
					getHistoryPanel().setText(s);
				} catch (Exception error) {
					throw new RuntimeException(error.toString());
				}
			}
		}
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
	boolean handleSave() {
		chooser.setCurrentDirectory(new File("."));
		chooser.setSelectedFile(new File("board.qbd"));
		int result = chooser.showSaveDialog(getFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				FileOutputStream fos = new FileOutputStream(file.getPath());
				ObjectOutputStream output = new ObjectOutputStream(fos);
				output.writeObject(getBoard());
				output.writeObject(getCurrentColors());
				output.writeObject(getHistoryPanel().getText());
			} catch (Exception error) {
				throw new RuntimeException(error.toString());
			}
			saveAction.setEnabled(!file.exists());
			if (!openAction.isEnabled())
				openAction.setEnabled(file.exists());
			return true;
		} else
			return false;
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
				getBoard().undo();
				getHistoryPanel().removeLine();
				redoAction.setEnabled(true);
				if (getBoard().canUndo()) {
					getBoard().undo();
					getHistoryPanel().removeLine();
					redoAction.setEnabled(true);
				} else {
					redoAction.setEnabled(false);
					handleNewGame();
				}
				setEnabled(getBoard().canUndo());
				saveAction.setEnabled(getBoard().canUndo());
			}
		}
	}
	
	/**
	 * Overridden later in GameController. It is needed to update the
	 * history panel after the board has changed.
	 */
	void handleUndo() {
	}
	
	class RedoAction extends AbstractAction {
		private static final long serialVersionUID = 1;
		
		public RedoAction(String name, Icon icon) {
			super(name, icon);
			putValue(SHORT_DESCRIPTION, "Redo the last move");
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (getBoard().canRedo()) {
				getBoard().redo();
				getHistoryPanel().writeLine();
				if (getBoard().canRedo()) {
					getBoard().redo();
					getHistoryPanel().writeLine();
				}
				setEnabled(getBoard().canRedo());
				saveAction.setEnabled(getBoard().canUndo());
			}
		}
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
	
	/**
	 * This is overridden later (in GameController). It allows the options panel
	 * to communicate a length of time to wait before the computer moves.
	 * @param d
	 */
	void setDelay(int d) {
	}
	
	/**
	 * This is overridden later (in GameController). It allows the options panel
	 * to ask for the current delay so it can update the number displayed in
	 * the spinner box within the dialog.
	 * @return
	 */
	int getDelay() {
		return 0;
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
	
	RedoAction getRedoAction() {
		return redoAction;
	}
	
	OptionsAction getOptionsAction() {
		return optionsAction;
	}
	
	ExitAction getExitAction() {
		return exitAction;
	}
}
