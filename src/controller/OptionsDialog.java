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

import ai.QubicAI;

/**
 * Contains all the buttons and listeners for the preferences popup dialog.
 * @author Blake
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
		p1.add(makeButtonGroup("First Player"));
		p1.add(makeButtonGroup("Second Player"));
		playerPanel.add(p1);		
		
		return playerPanel;
	}
	
	private JPanel makeButtonGroup(String name) {
		JPanel p = new PlayerButtonGroup();
		//p.setPreferredSize(new Dimension(100, 0));
		p.setBorder(BorderFactory.createTitledBorder(name));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));		
		return p;
	}
	
	private class PlayerButtonGroup extends JPanel  implements ActionListener {
		ButtonGroup group;
		
		public PlayerButtonGroup() {
			super();
			group = new ButtonGroup();
			
			JRadioButton hum = new JRadioButton("Human", true);
			JRadioButton comp1 = new JRadioButton("Computer: Simple");
			JRadioButton comp2 = new JRadioButton("Computer: Easy");
			JRadioButton comp3 = new JRadioButton("Computer: Hard");
			JRadioButton comp4 = new JRadioButton("Computer: Expert");
			hum.addActionListener(this);
			comp1.addActionListener(this);
			comp2.addActionListener(this);
			comp3.addActionListener(this);
			comp4.addActionListener(this);
			group.add(hum);
			group.add(comp1);
			group.add(comp2);
			group.add(comp3);
			group.add(comp4);
			add(hum);
			add(comp1);
			add(comp2);
			add(comp3);
			add(comp4);
		}
		
		public void actionPerformed(ActionEvent e) {
			String player = e.getActionCommand();
			if (player.startsWith("Computer"))
				player = player.substring(10);
			if (player.equals("Simple"))
				controller.setCurrentAi(controller.getSimpleAi());
			else if (player.equals("Easy"))
				controller.setCurrentAi(controller.getEasyAi());
			else if (player.equals("Hard"))
				controller.setCurrentAi(controller.getHardAi());
			else if (player.equals("Expert"))
				controller.setCurrentAi(controller.getEasyAi());
			controller.updateSelectedAiRadioButton();
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
