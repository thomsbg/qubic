package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.QubicBoard;
import ai.QubicAI;

/**
 * Shows the user a list of the history of moves made: whether the player
 * was first or second, a human or an ai, and the square that they moved to.
 * @author Blake Thomson
 */
public class MoveHistoryPanel extends JPanel implements BoardListener {
	JTextArea area;
	JScrollPane pane;
	QubicBoard board;
	String firstPlayer;
	String secondPlayer;
	private static final long serialVersionUID = 1;
	
	public MoveHistoryPanel(QubicAI first, QubicAI second) {
		updatePlayers(first, second);
		
		this.setLayout(new BorderLayout());
		area = new JTextArea();
		pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setMinimumSize(new Dimension(350, 0));
		area.setEditable(false);
		add(pane);
	}
	
	public void update(QubicBoard b) {
		board = b;
	}
	
	/**
	 * When notified by the controller that the players have changed,
	 * this updates internal string fields to reflect the change.
	 * @param first
	 * @param second
	 */
	public void updatePlayers(QubicAI first, QubicAI second) {
		if (first == null)
			firstPlayer = "(Human)";
		else
			firstPlayer = "(" + first.toString() + " AI)";
		
		if (second == null)
			secondPlayer = "(Human)";
		else
			secondPlayer = "(" + second.toString() + " AI)";	
	}
	
	/**
	 * Called by various controllers to update the text area with the most
	 * recent data of who went where.
	 */
	public void writeLine() {
		String player;
		if (board.getCurrentPlayer() == QubicBoard.Player.SECOND)
			player = "Player 1 " + firstPlayer;
		else
			player = "Player 2 " + secondPlayer;
		area.append(player + " moved to square:\t" + board.getLastMove() + "\n");
		area.setCaretPosition(area.getDocument().getLength());
		repaint();
	}
	
	/**
	 * When using the undo function of the game, this method is called to remove
	 * the last line of the text area, so it matches with the displayed board pieces. 
	 */
	public void removeLine() {
		String[] s = area.getText().split("\n");
		String result = "";
		for (int i = 0; i < s.length - 1; i++) {
			result += s[i] + "\n";
		}
		area.setText(result);
		repaint();
	}
	
	/**
	 * Completely clears the text area, so that it can be used in a new game.
	 */
	public void clearHistory() {
		area.setText("");
		repaint();
	}
	
	/**
	 * Gets all of the text stored in the text area as a String.
	 * @return
	 */
	public String getText() {
		return area.getText();
	}
	
	/**
	 * Explicitly sets the text in the panel to be the given string. The string
	 * must contain newlines if it is to appear correctly in the text area, as it
	 * will not wrap automatically.
	 * @param s
	 */
	public void setText(String s) {
		area.setText(s);
	}
}