package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import model.QubicBoard;
import model.Square;

/**
 * A controller to house and implement the listeners for the panel at the bottom
 * which lets the user enter their next move in a series of text boxes.
 * TODO: Work with the formatting/valid values/ etc.
 * @author Blake
 */
class InputController extends ToolbarController {

	private JPanel viewPanel;
	private JToolBar toolbar;
	
	/**
	 * Constructs the JPanel used to store the input toolbar and the main view
	 * (the main view is added later in the ViewController).
	 * @param b
	 */
	InputController(QubicBoard b) {
		super(b);
		viewPanel = new JPanel();
		viewPanel.setLayout(new BorderLayout());
		toolbar = new JToolBar();
		viewPanel.add(toolbar, BorderLayout.SOUTH);
		toolbar.setLayout(new FlowLayout());
		toolbar.setFloatable(false);
		
		final JTextField row = makeEntryBox("Row");
		final JTextField column = makeEntryBox("Column");
		final JTextField plane = makeEntryBox("Plane");
		
		JButton enter = new JButton("Enter");
		toolbar.addSeparator(new Dimension(10, 10));
		toolbar.add(enter);
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = Integer.parseInt(row.getText());
				int y = Integer.parseInt(column.getText());
				int z = Integer.parseInt(plane.getText());
				row.setText("");
				column.setText("");
				plane.setText("");
				Square s = new Square(x, y, z);
				handleMoves(s);
			}
		});		
	}
	
	/**
	 * Helper method used by the constructor to reduce redundancy in creating
	 * the three text fields for the row, column, and plane numbers.
	 * @param name The name of the field to create.
	 * @return A reference to the created JTextField.
	 */
	JTextField makeEntryBox(String name) {
		JTextField field = new JTextField(2);
		toolbar.add(new JLabel(name));
		toolbar.add(field);
		return field;
	}
	
	/**
	 * This is overridden in GameController, it is enacted by the "enter" button.
	 * @param s The square generated by the input in the three text fields.
	 */
	void handleMoves(Square s) {
	}

	JPanel getViewPanel() {
		return viewPanel;
	}
}