package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.QubicBoard;
import model.Square;

public class MoveHistoryPanel extends JPanel implements BoardListener {
	JTextArea area;
	JScrollPane pane;
	QubicBoard board;
	private static final long serialVersionUID = 1;
	
	public MoveHistoryPanel() {
		this.setLayout(new BorderLayout());
		area = new JTextArea();
		pane = new JScrollPane(area);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setMinimumSize(new Dimension(260, 0));
		area.setEditable(false);
		add(pane);
	}
	
	public void update(QubicBoard b) {
		board = b;
	}
	
	public void humanMoves(Square s) {
		area.append("Human moved to square:\t" + s + "\n");
		area.setCaretPosition(area.getDocument().getLength());
		repaint();
	}
	
	public void computerMoves(Square s) {
		area.append("Computer moved to square:\t" + s + "\n");
		area.setCaretPosition(area.getDocument().getLength());
		repaint();
	}
	
	public void undoMove() {
		String[] s = area.getText().split("\n");
		String result = "";
		for (int i = 0; i < s.length - 1; i++) {
			result += s[i] + "\n";
		}
		area.setText(result);
		repaint();
	}
	
	public void clearHistory() {
		System.out.println("Clearing history panel");
		area.setText("");
		repaint();
	}
}
