package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;

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
class ViewController extends InputController {	
	private View mainView;
	private View3D view3D;
	private MoveHistoryPanel historyPanel;
	private JProgressBar progressBar;
	
	/**
	 * Constructs and lays out the views of the game, along with the
	 * progress bar used in the main view. Adds various mouse listeners
	 * to detect clicks and to highlight relevant squares.
	 * @param b
	 */
	public ViewController(QubicBoard b) {
		super(b);

		progressBar = new JProgressBar(0, 100);
		progressBar.setOrientation(JProgressBar.VERTICAL);
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(getViewPanel());
		p.add(progressBar, BorderLayout.WEST);
		
		view3D = new View3D(getBoard());
		getBoard().addListener(view3D);
		getViewPanel().add(view3D);
		view3D.setPreferredSize(new Dimension(350, 400));		
		view3D.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				handleMoves(view3D.getSquareAtPoint(e.getPoint()));
			}
		});
		/*view3D.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Square s = view3D.getSquareAtPoint(e.getPoint());
				view3D.highlightSquare(s);
				// what does this do? i commented it out because it was messing up the highlighting
				/*for (Row r : s.containingRows()) {
					for (Square s3 : r.getSquares()) {
						view3D.highlightSquare(s3, s.getState());
					}
				}
			}
		});*/
		
		JPanel p2 = new JPanel(new BorderLayout());
		historyPanel = new MoveHistoryPanel();
		getBoard().addListener(historyPanel);
		p2.add(historyPanel);
		
		mainView = new View(getBoard());
		getBoard().addListener(mainView);
		p2.add(mainView, BorderLayout.SOUTH);
		mainView.setPreferredSize(new Dimension(260, 75));
		mainView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Square s = mainView.getSquareAtPoint(e.getPoint());
				handleMoves(s);
				mainView.highlightSquare(s);
			}
		});
		mainView.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Square s = mainView.getSquareAtPoint(e.getPoint());
				mainView.highlightSquare(s);
			}
		});
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p, p2);
		split.setContinuousLayout(true);
		split.setResizeWeight(1.0);
		getFrame().add(split);
	}

	MoveHistoryPanel getHistoryPanel() {
		return historyPanel;
	}

	BoardListener getMainView() {
		return mainView;
	}
	
	BoardListener get3DView() {
		return view3D;
	}

	JProgressBar getProgressBar() {
		return progressBar;
	}
}
