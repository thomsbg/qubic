package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import model.QubicBoard;
import model.Row;
import model.Square;
import model.QubicBoard.Player;

/**
 * This class creates a planar view of the board.
 * (i.e. four 4x4 planes side by side)
 * It displays all of the squares occupied by each player,
 * and also shows highlighted squares.
 * @author Raman
 *
 */
public class View extends JPanel implements BoardListener { 
	private int boxArea;	// size of each plane
	private int gap;		// distance between planes
	private int size;		// size of each square
	private int y;			// y coordinate of the top of each plane
		
	private Color firstColor;		// color of player 1 squares
	private Color secondColor;		// color of player 2 squares
	private Color boardColor;		// color of empty squares
	private Color borderColor;		// color of border lines
	private Color highlightSquareColor;		// color of highlighted square
	private Color highlightRowColor;		// color of highlighted squares in rows
	private Color pickedHighlightSquareColor; // user picked square highlight color
	
	private Rectangle2D highlightedSquare; // square to be highlighted
	private List<Rectangle2D> highlightList;
	
	private List<Square> squares; // list of every square
	private Map<String, Square> sqMap;	// map of every square
	
	private static final long serialVersionUID = 1;

	private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(171, 130,
			255);
	
	private static final Color DEFAULT_ROW_HIGHLIGHT_COLOR = new Color(255, 255,
			255, 185);
	
	/**
	 * Creates a view that displays a graphical representation of the
	 * game board.
	 * @param QubicBoard
	 */
	public View(QubicBoard board) {
		squares = board.getGrid();
		firstColor = Color.BLUE;
		secondColor = Color.RED;
		boardColor = Color.YELLOW;
		borderColor = Color.BLACK;
		highlightRowColor = DEFAULT_ROW_HIGHLIGHT_COLOR;
		pickedHighlightSquareColor = DEFAULT_HIGHLIGHT_COLOR;
		sqMap = mapMaker(squares);
	}

	/**
	 * Draws the graphics on the screen
	 * @param Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		setPreferredSize(new Dimension(260, boxArea + gap * 2));
		drawBoxes(g2);

		g2.setPaint(highlightRowColor);
		if (highlightList != null) {
			for (Rectangle2D r : highlightList) {
				g2.fill(r);
			}
		}

		if (highlightedSquare != null) {
			g2.setPaint(highlightSquareColor);
			g2.fill(highlightedSquare);
		}
	}

	/**
	 * Updates the view of the board
	 * @param QubicBoard
	 */
	public void update(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		repaint();
	}

	/**
	 * Creates a map of squares with the keys in the format of <Row, Col, Plane>
	 * linked to the corresponding square
	 * @param List<Squares>
	 * @return Map<String, Square>
	 */
	private Map<String, Square> mapMaker(List<Square> sq) {
		Map m = new HashMap<String, Square>();
		for (Square s : sq)
			m.put(s.toString(), s);

		return m;
	}

	/**
	 * Draws each of the 4 planes of the view, and the pieces occupying the squares
	 * @param Graphics2D
	 */
	private void drawBoxes(Graphics2D g2) {
		gap = getWidth() / 40;
		boxArea = (getWidth() - gap * 5) / 4;
		size = boxArea / 4;
		y = getHeight() / 2 - boxArea / 2;
		int counter = 0;

		for (int p = 0; p < 4; p++) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					Rectangle2D border = new Rectangle2D.Double(gap - 1
							+ ((size + 1) * j) + p * (gap + boxArea), y - 1
							+ ((size + 1) * i), size + 2, size + 2);
					Rectangle2D box = new Rectangle2D.Double(gap
							+ ((size + 1) * j) + p * (gap + boxArea), y
							+ ((size + 1) * i), size, size);

					g2.setPaint(borderColor);
					g2.fill(border);

					Square s = squares.get(counter);
					Player player = s.getState();

					g2.setPaint(boardColor);
					if (player == Player.FIRST)
						g2.setPaint(firstColor);
					else if (player == Player.SECOND)
						g2.setPaint(secondColor);

					g2.fill(box);
					counter++;
				}
			}
		}
	}

	/**
	 * Returns what square the given point is on the board.
	 * If the given point is not on the board, returns -1 for that dimension.
	 * e.g. if the user clicks between the planes, the col dimension is set to -1.
	 * @param Point2D
	 * @return Square
	 */
	public Square getSquareAtPoint(Point2D p) {
		highlightedSquare = null;
		int row = -1;
		int col = -1;
		int plane = (int) (p.getX() / (gap + boxArea) + 1);

		if (p.getY() >= y - 1)
			row = (int) (p.getY() - y) / (size + 1) + 1;

		if (p.getX() > gap * plane + boxArea * (plane - 1))
			col = (int) (p.getX() - (plane * gap + plane * boxArea))
					/ (size + 1) + 4;

		Square s = new Square(row, col, plane);
		for (Square s2 : squares) {
			if (s2.equals(s)) {
				return s2;
			}
		}
		return s;
	}

	/**
	 * Highlights the given square and any connected
	 * possible 4 in a rows to that square
	 * @param Square
	 */
	public void highlightSquare(Square s1) {
		Square s = sqMap.get(s1.toString());

		if (s != null) {
			int x = (int) s.getY() - 1;
			int y1 = (int) s.getX() - 1;
			int z = (int) s.getZ() - 1;

			highlightRows(s);
			highlightedSquare = new Rectangle2D.Double(gap + ((size + 1) * x)
					+ z * (gap + boxArea), y + ((size + 1) * y1), size, size);

			if (s.getState() == null) {
				highlightSquareColor = pickedHighlightSquareColor;
			} else if (s.getState() == Player.FIRST) {
				highlightSquareColor = firstColor.brighter();
				if (highlightSquareColor.equals(firstColor))
					highlightSquareColor = firstColor.darker();
			} else if (s.getState() == Player.SECOND) {
				highlightSquareColor = secondColor.brighter();
				if (highlightSquareColor.equals(secondColor))
					highlightSquareColor = secondColor.darker();
			} else
				highlightedSquare = null;
		} else
			highlightList = null;

		repaint();
	}

	/**
	 * Highlights all of the squares that are in a row of 4 with the given square
	 * @param Square
	 */
	private void highlightRows(Square s) {
		List<Row> rows = s.containingRows();
		highlightList = new ArrayList<Rectangle2D>();

		for (Row r : rows) {

			for (Square s1 : r.getSquares()) {

				int x = (int) s1.getY() - 1;
				int y1 = (int) s1.getX() - 1;
				int z = (int) s1.getZ() - 1;

				Square s2 = sqMap.get(s1.toString());
				if (s2.getState() == null) {
					Rectangle2D hSquare = new Rectangle2D.Double(gap
							+ ((size + 1) * x) + z * (gap + boxArea), y
							+ ((size + 1) * y1), size, size);

					highlightList.add(hSquare);
				}
			}
		}
	}

	/**
	 * Returns the color of the first players squares
	 * @return Color
	 */
	public Color getFirstColor() {
		return firstColor;
	}

	/**
	 * Sets the color of the player's game pieces
	 * @param Color
	 */
	public void setFirstColor(Color c) {
		firstColor = c;
	}

	/**
	 * Returns the color of the second players squares
	 * @return Color
	 */
	public Color getSecondColor() {
		return secondColor;
	}

	/**
	 * Sets the color of the computer's game pieces
	 * @param Color
	 */
	public void setSecondColor(Color c) {
		secondColor = c;
	}

	/**
	 * Returns the empty squares on the board
	 * @return Color
	 */
	public Color getBoardColor() {
		return boardColor;
	}

	/**
	 * Sets the color of the game board
	 * @param Color
	 */
	public void setBoardColor(Color c) {
		boardColor = c;
	}

	/**
	 * Returns the color of the outline of all squars on the board
	 * @return Color
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the outline color of all squares
	 * @param Color
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Returns the current color of the highlightedSquare
	 * @return Color
	 */
	public Color getHighlightSquareColor() {
		return highlightSquareColor;
	}

	/**
	 * Sets the color of the highlighted square
	 * @param Color
	 */
	public void setHighlightSquareColor(Color c) {
		pickedHighlightSquareColor = c;
	}

	/**
	 * Returns the current color fo the highlighted 4 in a rows
	 * @return Color
	 */
	public Color getHighlightRowColor() {
		return highlightRowColor;
	}

	/**
	 * Sets the color of the highlighted squares in the rows
	 * @param Color
	 */
	public void setHighlightRowColor(Color c) {
		highlightRowColor = c;
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
