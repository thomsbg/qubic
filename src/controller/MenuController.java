package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import model.QubicBoard;

/**
 * A controller to construct and implement some specific actions for the window's menu bar.
 * @author Blake Thomson
 */
class MenuController extends ActionController {
	private JMenuBar menuBar;
	
	/**
	 * Create each menu with its submenus and menu items.
	 * @param b
	 */
	MenuController(QubicBoard b) {
		super(b);
		
		menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);		
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		fileMenu.add(getNewGameAction());
		fileMenu.add(getOpenAction());
		fileMenu.add(getSaveAction());
		fileMenu.add(getExitAction());
		
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		editMenu.add(getUndoAction());
		editMenu.add(getRedoAction());
		editMenu.add(getOptionsAction());
		
		JMenu helpMenu = new JMenu("Help");		
		menuBar.add(helpMenu);
		helpMenu.add(makeAboutDialog());
	}
	
	/**
	 * A helper method used by the constructor that creates a menu item
	 * with a listener attatched that pops up a little about dialog.
	 * @return A JMenuItem, that when clicked displays an about dialog.
	 */
	private JMenuItem makeAboutDialog() {
		JMenuItem item = new JMenuItem("About");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(getFrame(),
				"This version of Qubic was created by:\n" +
				"Raman Ahluwalia, Blake Thomson and John Thomson as their final\n" +
				"project in the course CSE 190 at the University of Washington, Spring 2007",
				"About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		return item;
	}
}
