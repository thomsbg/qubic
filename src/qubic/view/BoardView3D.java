package qubic.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import qubic.model.Board;
import qubic.model.Row;
import qubic.model.Square;

/**
 * A board listener that implements a "fake" 3d view of the game.
 * 
 * @author Blake Thomson
 */
public class BoardView3D extends JPanel implements BoardListener {
	private static final long serialVersionUID = 1L;
	private static final Color SQUARE_HIGHLIGHT_COLOR = new Color(0, 0, 0, 150);
	private static final Color ROW_HIGHLIGHT_COLOR = new Color(0, 0, 0, 50);
	
	private Board board;

	private Color backgroundColor;
	private Color boardColor; // color of empty squares
	private Color borderColor; // color of border lines
	private Color firstColor; // color of player 1 squares
	private Color secondColor; // color of player 2 squares

	private Polygon highlightedSquare; // square to be highlighted
	private List<Polygon> highlightList; // list of polygons to be highlighted
	private QubicPlane3D[] planes; // array of planes

	private int width;
	private int height;
	private int gap;

	/**
	 * Creates a pseudo 3D view that displays a graphical representation of the
	 * game board.
	 */
	public BoardView3D(Board b) {
		board = b;
		width = 200;
		height = 150;
		gap = 10;

		backgroundColor = Color.WHITE;
		firstColor = Color.BLUE;
		secondColor = Color.RED;
		boardColor = Color.YELLOW;
		borderColor = Color.BLACK;

		highlightList = new ArrayList<Polygon>(12);

		planes = new QubicPlane3D[4];
		for (int i = planes.length - 1; i >= 0; i--) {
			planes[i] = new QubicPlane3D(new Point2D.Double(gap, i
					* (gap + height)), width, height);
		}
		
		setBackground(backgroundColor);
	}

	/**
	 * Overrides the default method to draw all the squares in their appropriate
	 * colors.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		for (int i = planes.length - 1; i >= 0; i--) {
			int gap = getHeight() / 30;
			int height = getHeight() - 5 * gap;
			int width = getWidth() - 2 * gap;
			planes[i].setHeight(height / 4);
			planes[i].setWidth(width);
			planes[i].setP1(new Point2D.Double(gap, (i + 1)
					* (gap + height / 4)));
			planes[i].computeBoxes();
			for (int j = 0; j < planes[i].getBoxes().length; j++) {
				Polygon poly = planes[i].getBoxes()[j];
				Square s = board.getSquare(4 - (j % 4), j / 4 + 1, i + 1);

				if (s.getOwner() != null) {
					if (s.getOwner().amIFirst())
						g2.setPaint(firstColor);
					else
						g2.setPaint(secondColor);
				} else {
					g2.setPaint(boardColor);
				}

				g2.fill(poly);

				g2.setPaint(borderColor);
				g2.draw(poly);
			}
		}

		if (!board.isGameOver()) {
			if (highlightList != null) {
				Color c = ROW_HIGHLIGHT_COLOR;
				for (Polygon p : highlightList) {
					g2.setPaint(c);
					g2.fill(p);
					g2.setPaint(borderColor);
					g2.draw(p);
				}
			}

			if (highlightedSquare != null) {
				g2.setPaint(SQUARE_HIGHLIGHT_COLOR);
				g2.fill(highlightedSquare);
				g2.setPaint(borderColor);
				g2.draw(highlightedSquare);
			}
		} else {
			Row winningRow = board.getWinningRow();
			if (winningRow != null) {
				for (Square s : winningRow.getSquares()) {
					Polygon p = getPolygonAtSquare(s);

					Stroke temp = g2.getStroke();
					g2.setStroke(new BasicStroke(5));
					g2.setPaint(Color.GREEN);
					g2.draw(p);
					g2.setStroke(temp);
				}
			}
		}
	}

	/**
	 * Highlights the given square
	 * 
	 * @param Square
	 */
	public void highlightSquare(Square s) {
		highlightedSquare = null;

		if (s != null) {
			highlightRows(s);
			if (s.getOwner() == null)
				highlightedSquare = getPolygonAtSquare(s);
		} else {
			highlightList.clear();
		}
		repaint();
	}

	/**
	 * Highlights all the parent rows of the given square.
	 * 
	 * @param Square
	 */
	private void highlightRows(Square s) {
		highlightList.clear();

		for (Row row : s.getParentRows()) {
			for (Square square : row.getSquares()) {
				if (square.getOwner() == null && square != s) {
					Polygon hSquare = getPolygonAtSquare(square);
					highlightList.add(hSquare);
				}
			}
		}
	}

	/**
	 * Returns a Polygon that represents the given square
	 * 
	 * @param Square
	 * @return Polygon
	 */
	private Polygon getPolygonAtSquare(Square s) {
		for (int i = 0; i < planes.length; i++) {
			if (s.getZ() - 1 == i)
				return planes[i].getPolygonAtSquare(s);
		}
		return null; // Should never reach this point
	}

	/**
	 * Updates the view of the board
	 * 
	 * @param QubicBoard
	 */
	public void update() {
		repaint();
	}

	/**
	 * Returns the square at a given point If the square doesnt exist at that
	 * point, return null.
	 * 
	 * @param Point2D
	 * @return Square
	 */
	public Square getSquareAtPoint(Point2D p) {
		for (int i = 0; i < planes.length; i++) {
			if (planes[i].contains(p)) {
				return planes[i].getSquareAtPoint(p, i + 1, board);
			}
		}
		return null;
	}

	/**
	 * Sets the color of the game board
	 * 
	 * @param Color
	 */
	public void setBoardColor(Color c) {
		boardColor = c;
	}

	/**
	 * Sets the color of the border lines
	 * 
	 * @param Color
	 */
	public void setBorderColor(Color c) {
		borderColor = c;
	}

	/**
	 * Sets the color of the player's game pieces
	 * 
	 * @param Color
	 */
	public void setFirstColor(Color c) {
		firstColor = c;
	}

	/**
	 * Sets the color of the computer's game pieces
	 * 
	 * @param Color
	 */
	public void setSecondColor(Color c) {
		secondColor = c;
	}

	/**
	 * Clears all of the highlighted squares
	 */
	public void clearHighlight() {
		highlightedSquare = null;
		highlightList = null;
		repaint();
	}
}
