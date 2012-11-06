package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import model.QubicBoard;

/**
 * Contains all the buttons and listeners for the preferences popup dialog.
 * @author Blake
 */
public class OptionsDialog {
	private JDialog dialog;
	private JFrame frame;
	private JTabbedPane tp;
	private ActionController controller;
	private QubicBoard board;
	private static final long serialVersionUID = 1;
	
	public OptionsDialog(JFrame f, ActionController c) {
		frame = f;
		controller = c;
		board = controller.getBoard();
		dialog = new JDialog(frame, "Options", true);
		tp = new JTabbedPane();
		//dialog.setContentPane(tp);	
		dialog.setResizable(false);
		
		tp.add("Players", makePlayerOptions());
		tp.add("Colors", makeColorOptions());
		dialog.add(tp);
		
		JPanel p2 = new JPanel();
		
		final JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		p2.add(cancelButton);
		dialog.add(p2, BorderLayout.SOUTH);
		
		//dialog.setSize(400, 200);
		dialog.pack();
	}
	
	private JPanel makePlayerOptions() {
		JPanel playerPanel = new JPanel(new BorderLayout());
		//playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		
		JPanel p1 = new JPanel(new GridLayout(1, 0));
		p1.add(makeButtonGroup("First Player", true));
		p1.add(makeButtonGroup("Second Player", false));
		playerPanel.add(p1);		
		
		return playerPanel;
	}
	
	private JPanel makeButtonGroup(String name, boolean firstPlayer) {
		JPanel p = new PlayerButtonGroup(firstPlayer);
		//p.setPreferredSize(new Dimension(100, 0));
		p.setBorder(BorderFactory.createTitledBorder(name));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));		
		return p;
	}
	
	private class PlayerButtonGroup extends JPanel  implements ActionListener {
		private ButtonGroup group;
		private boolean first;
		private static final long serialVersionUID = 1;
		
		public PlayerButtonGroup(boolean firstPlayer) {
			super();
			first = firstPlayer;
			group = new ButtonGroup();
			
			JRadioButton hBut = createRadioButton("Human", true);
			JRadioButton c1But = createRadioButton("Computer: Simple", false);
			JRadioButton c2But = createRadioButton("Computer: Easy", false);
			JRadioButton c3But = createRadioButton("Computer: Hard", false);
			JRadioButton c4But = createRadioButton("Computer: Expert", false);
			
			if (first) {
				if (board.getFirstPlayer() == QubicBoard.Player.HUMAN)
					hBut.setSelected(true);
				else {
					String aiName = board.getFirstAI().toString();
					if (aiName.equals("Simple"))
						c1But.setSelected(true);
					else if (aiName.equals("Easy"))
						c2But.setSelected(true);
					else if (aiName.equals("Hard"))
						c3But.setSelected(true);
					else if (aiName.equals("Expert"))
						c4But.setSelected(true);
				}
			}
		}
		
		private JRadioButton createRadioButton(String name, boolean selected) {
			JRadioButton button = new JRadioButton(name, selected);
			button.addActionListener(this);
			group.add(button);
			add(button);
			return button;
		}
		
		public void actionPerformed(ActionEvent e) {
			String player = e.getActionCommand();
			if (player.startsWith("Computer")) {
				player = player.substring(10);
				if (first) {
					board.setFirstPlayer(QubicBoard.Player.COMPUTER);
					board.setFirstAI(player);
				} else {
					board.setSecondPlayer(QubicBoard.Player.COMPUTER);
					board.setSecondAI(player);
				}
			} else {
				if (first)
					board.setFirstPlayer(QubicBoard.Player.HUMAN);
				else
					board.setSecondPlayer(QubicBoard.Player.HUMAN);
			}				
		}
	}
	
	private JPanel makeColorOptions() {
		JPanel colorPanel = new JPanel(new BorderLayout());
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Player"));
		p.add(new JLabel("Choose the player you wish to change the color for:"), BorderLayout.PAGE_START);
		JRadioButton b1 = new JRadioButton("Human", true);
		JRadioButton b2 = new JRadioButton("Computer");
		ButtonGroup group = new ButtonGroup();
		group.add(b1);
		group.add(b2);
		JPanel p2 = new JPanel();
		p2.add(b1);
		p2.add(b2);
		p.add(p2, BorderLayout.WEST);
		colorPanel.add(p, BorderLayout.NORTH);
		JColorChooser choo = new JColorChooser(Color.RED);
		colorPanel.add(choo);
		return colorPanel;
	}
	
	public void start() {
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
}
