package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ai.QubicAI;

/**
 * Contains all the buttons and listeners for the preferences popup dialog.
 * @author Blake Thomson
 */
public class OptionsDialog {
	private JDialog dialog;
	private JFrame frame;
	private JTabbedPane tp;
	private ActionController controller;
	private static final long serialVersionUID = 1;
	
	public OptionsDialog(JFrame f, ActionController c) {
		frame = f;
		controller = c;
		dialog = new JDialog(frame, "Options", true);
		tp = new JTabbedPane();
		dialog.setResizable(false);
		
		JPanel playerOptions = makePlayerOptions();
		playerOptions.setPreferredSize(new Dimension(500, 450));
		tp.add("Players", playerOptions);
		tp.add("Colors", new colorOptionsPanel());
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
		
		dialog.pack();
	}
	
	/**
	 * A helper method returning the panel to be displayed on the "Player" tab of the
	 * dialog popup. This constructs all the buttons and text that is used in this tab.
	 * @return
	 */
	private JPanel makePlayerOptions() {
		JPanel playerPanel = new JPanel(new BorderLayout());
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		JPanel p1 = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new GridLayout(1, 0));
		p2.add(makeButtonGroup("First Player", true));
		p2.add(makeButtonGroup("Second Player", false));
		p1.add(p2);
		JPanel p3 = new JPanel();
		p3.setBorder(BorderFactory.createTitledBorder("Delay"));
		p3.add(new JLabel("Time delay before computer moves (ms): "));
		SpinnerModel model = new SpinnerNumberModel(1000, 0, 10000, 500);
		final JSpinner spinner = new JSpinner(model);
		spinner.setValue(1000);
		p3.add(spinner);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				controller.setDelay((Integer) spinner.getValue());
			}
		});
		p1.add(p3, BorderLayout.SOUTH);
		playerPanel.add(p1);
		
		JTextArea desc = new JTextArea("For each first and second player, you can choose " +
				"whether you want a human to play, or one of the various levels of artificial " +
				"intelligence:\n\nDefensive AI will only take action when there is a 3-in-a-row: " +
				"it will win if it has a 3-in-a-row, and will block the opponent's winning move if " +
				"it can.\n\nEasy AI gives each possible move a priority, based on the rows that the " +
				"possible move is in. It then chooses the move with the highest priority score. " +
				"\n\nHard AI uses limited recursive backtracking to find a sequence of winning moves, " +
				"but only looks a few moves ahead. It falls back on the priority method when a " +
				"guaranteed win is not in sight.\n\nExpert AI allows for a deeper search during " +
				"recursive backtracking, allowing it to follow more intricate strategies to force a win.");
		
		desc.setBorder(BorderFactory.createTitledBorder("Player Information"));
		desc.setEditable(false);
		desc.setBackground(null);
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		playerPanel.add(desc, BorderLayout.SOUTH);
		
		//playerPanel.add(playerPanel);		
		return playerPanel;
	}
	
	/**
	 * A little factory method used by makePlayerOptions that creates a group of five
	 * radio buttons representing the different possible players. It is called twice by
	 * makePlayerOptions, to create a group of buttons for each player.
	 * @param name
	 * @param firstPlayer
	 * @return
	 */
	private JPanel makeButtonGroup(String name, boolean firstPlayer) {
		JPanel p = new PlayerButtonGroup(firstPlayer);
		//p.setPreferredSize(new Dimension(100, 0));
		p.setBorder(BorderFactory.createTitledBorder(name));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));		
		return p;
	}
	
	/**
	 * The makeButtonGroup method initializes one of these objects, which 
	 * contains all the radio buttons, borders, and listeners to implement
	 * the radio button controls. Communicates directly and immediately with
	 * the controller when changes are made.
	 * @author Blake
	 */
	private class PlayerButtonGroup extends JPanel  implements ActionListener {
		private ButtonGroup group;
		private boolean first;
		
		private static final long serialVersionUID = 1;
		
		public PlayerButtonGroup(boolean firstPlayer) {
			super();
			first = firstPlayer;
			group = new ButtonGroup();
			
			JRadioButton hBut = createRadioButton("Human", true);
			JRadioButton c1But = createRadioButton("Computer: Defensive", false);
			JRadioButton c2But = createRadioButton("Computer: Easy", false);
			JRadioButton c3But = createRadioButton("Computer: Hard", false);
			JRadioButton c4But = createRadioButton("Computer: Expert", false);
			
			if (first) {
				if (controller.getFirstPlayer() == null)
					hBut.setSelected(true);
				else {
					String aiName = controller.getFirstPlayer().toString();
					if (aiName.equals("Defensive"))
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
			if (player.startsWith("Computer"))
				player = player.substring(10);
			QubicAI ai = null;
			if (player.equals("Defensive"))
				ai = controller.getDefensiveAI();
			else if (player.equals("Easy"))
				ai = controller.getEasyAI();
			else if (player.equals("Hard"))
				ai = controller.getHardAI();
			else if (player.equals("Expert"))
				ai = controller.getExpertAI();
			if (first) {
				controller.setFirstPlayer(ai);
			} else {
				controller.setSecondPlayer(ai);
			}
			controller.handleChangedPlayers();
		}
	}
	
	/**
	 * This panel is constructed by the dialog to be used in the second tab of the
	 * dialog popup. It contains a color chooser, and a set of radio buttons allow
	 * the user to select which color they would like to change. Communicates directly
	 * and immediately with the controller as changes are made.
	 * @author Blake
	 */
	private class colorOptionsPanel extends JPanel implements ActionListener, ChangeListener {
		private JRadioButton[] b;
		private JColorChooser choo;
		
		private static final long serialVersionUID = 1;
		
		public colorOptionsPanel() {
			super();
			JPanel p = new JPanel();
//			setPreferredSize(new Dimension(500, 400));
			p.setPreferredSize(new Dimension(0, 90));
			p.setBorder(BorderFactory.createTitledBorder("Color to Change: "));
			ButtonGroup group = new ButtonGroup();
			b = new JRadioButton[6];
			b[0] = makeRadioButton("First Player", group, p);			
			b[1] = makeRadioButton("Second Player", group, p);
			b[2] = makeRadioButton("Empty Squares", group, p);
			b[3] = makeRadioButton("Square Border", group, p);
			b[4] = makeRadioButton("Square Highlighting", group, p);
			b[5] = makeRadioButton("Row Highlighting", group, p);
			b[0].setSelected(true);
			
			choo = new JColorChooser(controller.getCurrentColors()[0]);
			choo.getSelectionModel().addChangeListener(this);
			
			setLayout(new BorderLayout());
			add(p, BorderLayout.NORTH);
			add(choo);
		}
		
		private JRadioButton makeRadioButton(String name, ButtonGroup group, JPanel p) {
			JRadioButton b = new JRadioButton(name);
			group.add(b);
			b.addActionListener(this);
			p.add(b);
			return b;
		}
		
		public void actionPerformed(ActionEvent e) {
			Color[] cArray = controller.getCurrentColors();
			if (e.getActionCommand().startsWith("First")) {
				b[0].setSelected(true);
				choo.setColor(cArray[0]);
			} else if (e.getActionCommand().startsWith("Second")) {
				b[1].setSelected(true);
				choo.setColor(cArray[1]);
			} else if (e.getActionCommand().startsWith("Empty")) {
				b[2].setSelected(true);
				choo.setColor(cArray[2]);
			} else if (e.getActionCommand().startsWith("Square Border")) {
				b[3].setSelected(true);
				choo.setColor(cArray[3]);
			} else if (e.getActionCommand().startsWith("Square")) {
				b[4].setSelected(true);
				choo.setColor(cArray[4]);
			} else if (e.getActionCommand().startsWith("Row")) {
				b[5].setSelected(true);
				choo.setColor(cArray[5]);
			}
		}
		
		public void stateChanged(ChangeEvent e) {
			Color c = choo.getColor();
			Color[] cArray = controller.getCurrentColors();
			for (int i = 0; i < b.length; i++) {
				if (b[i].isSelected())
					cArray[i] = c;
			}
			controller.handleChangedColors(cArray);
		}
	}
	
	/**
	 * Causes the dialog to become visible.
	 */
	public void start() {
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
}
