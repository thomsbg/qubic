package qubic.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import qubic.model.Board;
import qubic.model.Square;
import qubic.view.BoardView3D;

@SuppressWarnings("serial")
public class QubicApplet extends JApplet {
	public void init() {
		Scanner rows_input = new Scanner(getClass().getResourceAsStream(
				"/resources/rows.dat"));

		Scanner squares_input = new Scanner(getClass().getResourceAsStream(
				"/resources/squares.dat"));

		Board board = new Board(rows_input, squares_input);
		constructButtonPanel(board);
		constructView3D(board);
	}

	private void constructButtonPanel(final Board board) {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setBackground(Color.WHITE);

		JButton newGame = new JButton("New Game", createImageIcon("/images/new.gif"));
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.restartGame();
			}
		});
		
		String[] players = { "Human", "Easy Computer" };
		JComboBox player1 = new JComboBox(players);
		player1.setSelectedIndex(0);
		player1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.setPlayer1(Board.HUMAN);
			}
		});
		JComboBox player2 = new JComboBox(players);
		player2.setSelectedIndex(1);
		player2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.setPlayer2(Board.EASY);
			}
		});

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(newGame);
		buttonPanel.add(new JLabel("Player 1"));
		buttonPanel.add(player1);
		buttonPanel.add(new JLabel("Player 2"));
		buttonPanel.add(player2);
	}

	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void constructView3D(final Board board) {
		final BoardView3D view3D = new BoardView3D(board);
		board.addListener(view3D);

		view3D.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Square s = view3D.getSquareAtPoint(e.getPoint());
				if (s != null && s.getOwner() == null) {
					board.endTurn(s);
					view3D.highlightSquare(null);
				}
			}
		});

		view3D.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Square s = view3D.getSquareAtPoint(e.getPoint());
				view3D.highlightSquare(s);
			}
		});

		getContentPane().add(view3D);
	}
}
