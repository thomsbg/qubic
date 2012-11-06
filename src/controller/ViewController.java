package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import model.QubicBoard;
import model.Square;
import view.BoardListener;
import view.MoveHistoryPanel;
import view.View;
import view.View3D;

/**
 * A controller to construct and house the panel with it's mouse listener.
 * Most gameplay happens within this controller, specifically within the internal
 * handleMoves() method.
 * @author Blake
 */
class ViewController extends AIController {
	private JPanel viewPanel;
	private View mainView;
	private View3D view3D;
	private MoveHistoryPanel historyPanel;
	private JToolBar inputToolbar;
	
	/**
	 * Constructs and lays out the views of the game, along with the
	 * progress bar used in the main view. Adds various mouse listeners
	 * to detect clicks and to highlight relevant squares.
	 * @param b
	 */
	public ViewController(QubicBoard b) {
		super(b);
		
		view3D = new View3D(getBoard());
		getBoard().addListener(view3D);
		view3D.setPreferredSize(new Dimension(350, 400));		
		view3D.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Square s = view3D.getSquareAtPoint(e.getPoint());
				humanMove(s);
				view3D.highlightSquare(s);
			}
		});
		view3D.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Square s = view3D.getSquareAtPoint(e.getPoint());
				view3D.highlightSquare(s);
			}
		});
		
		viewPanel = new JPanel(new BorderLayout());
		JPanel p1 = new JPanel(new BorderLayout());
		viewPanel.add(p1);
		historyPanel = new MoveHistoryPanel(getFirstPlayer(), getSecondPlayer());
		getBoard().addListener(historyPanel);
		p1.add(historyPanel);
		
		mainView = new View(getBoard());
		getBoard().addListener(mainView);
		p1.add(mainView, BorderLayout.SOUTH);
		mainView.setPreferredSize(new Dimension(0, 94));
		mainView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Square s = mainView.getSquareAtPoint(e.getPoint());
				humanMove(s);
				mainView.highlightSquare(s);
			}
		});
		mainView.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Square s = mainView.getSquareAtPoint(e.getPoint());
				mainView.highlightSquare(s);
			}
		});
		
		inputToolbar = new JToolBar();
		viewPanel.add(inputToolbar, BorderLayout.SOUTH);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, view3D, viewPanel);
		split.setContinuousLayout(true);
		split.setResizeWeight(1.0);
		getFrame().add(split);
	}

	/**
	 * Returns a reference to the input toolbar so it can be constructed later, in InputController.
	 * @return
	 */
	JToolBar getInputToolbar() {
		return inputToolbar;
	}
	
	/**
	 * This reference to the history panel is used by the game controller to update the text
	 * of the history when a move is made.
	 * @return
	 */
	MoveHistoryPanel getHistoryPanel() {
		return historyPanel;
	}
	
	/**
	 * Returns a reference to the flat view so it can be added back into the board's list of listeners
	 * when a save file is opened.
	 * @return
	 */
	BoardListener getMainView() {
		return mainView;
	}
	
	/**
	 * Returns a reference to the 3D view so it can be added back into he board's list of listeners
	 * when a save file is opened.
	 * @return
	 */
	BoardListener get3DView() {
		return view3D;
	}
	
	/**
	 * This is used by the options dialog to update the historyPanel so that is knows
	 * the most current players of the game.
	 */
	void handleChangedPlayers() {
		historyPanel.updatePlayers(getFirstPlayer(), getSecondPlayer());
	}
	
	/**
	 * @param cArray An array of six colors that changes various colors used in the game.
	 * This method just passes each individual color along to the views, which update their
	 * state variables.
	 */
	void handleChangedColors(Color[] cArray) {
		mainView.setFirstColor(cArray[0]);
		mainView.setSecondColor(cArray[1]);
		mainView.setBoardColor(cArray[2]);
		mainView.setBorderColor(cArray[3]);
		mainView.setHighlightSquareColor(cArray[4]);
		mainView.setHighlightRowColor(cArray[5]);
		mainView.repaint();
		
		view3D.setFirstColor(cArray[0]);
		view3D.setSecondColor(cArray[1]);
		view3D.setBoardColor(cArray[2]);
		view3D.setBorderColor(cArray[3]);
		view3D.setHighlightSquareColor(cArray[4]);
		view3D.setHighlightRowColor(cArray[5]);
		view3D.repaint();
	}
	
	/**
	 * Asks the plain flat view for the colors it is currently using.
	 * Whatever it returns is the same as what the 3D view would return,
	 * because the color fields of each are set at the same time always.
	 */
	Color[] getCurrentColors() {
		Color[] cArray = new Color[6];
		cArray[0] = mainView.getFirstColor();
		cArray[1] = mainView.getSecondColor();
		cArray[2] = mainView.getBoardColor();
		cArray[3] = mainView.getBorderColor();
		cArray[4] = mainView.getHighlightSquareColor();
		cArray[5] = mainView.getHighlightRowColor();
		return cArray;
	}
	
	/**
	 * This is overridden in GameController, it is enacted by the "enter" button.
	 * @param s The square generated by the input in the three text fields.
	 */
	void humanMove(Square s) {
	}
	
	/**
	 * Returns a reference to the panel housing the historyPanel and the flat view, so that
	 * the input panel may add itself later.
	 * @return
	 */
	JPanel getViewPanel() {
		return viewPanel;
	}
}
