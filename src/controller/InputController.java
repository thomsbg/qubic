package controller;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import model.QubicBoard;
import model.Square;

/**
 * A controller to house and implement the listeners for the panel at the bottom
 * which lets the user enter their next move in a series of text boxes.
 * @author Blake Thomson
 */
class InputController extends ToolbarController {
	
	/**
	 * Constructs the JPanel used to store the input toolbar and the main view
	 * (the main view is added later in the ViewController).
	 * @param b
	 */
	InputController(QubicBoard b) {
		super(b);
		getInputToolbar().setLayout(new FlowLayout());
		getInputToolbar().setFloatable(false);
		
		final JFormattedTextField row = makeEntryBox("Row");
		final JFormattedTextField column = makeEntryBox("Column");
		final JFormattedTextField plane = makeEntryBox("Plane");
		
		JButton enter = new JButton("Enter");
		getInputToolbar().addSeparator(new Dimension(10, 10));
		getInputToolbar().add(enter);
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = Integer.parseInt(row.getText());
				int y = Integer.parseInt(column.getText());
				int z = Integer.parseInt(plane.getText());
				row.setText("");
				column.setText("");
				plane.setText("");
				Square s = new Square(x, y, z);
				humanMove(s);
			}
		});		
	}
	
	/**
	 * Helper method used by the constructor to reduce redundancy in creating
	 * the three text fields for the row, column, and plane numbers.
	 * @param name The name of the field to create.
	 * @return A reference to the created JTextField.
	 */
	JFormattedTextField makeEntryBox(String name) {
		NumberFormat format = NumberFormat.getIntegerInstance();
		JFormattedTextField field = new JFormattedTextField(format);
		field.setColumns(2);
		getInputToolbar().add(new JLabel(name));
		getInputToolbar().add(field);
		return field;
	}	
}
