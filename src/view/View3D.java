package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

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
 * A board listener that implements a "fake" 3d view of the game.
 * @author Blake Thomson
 */
public class View3D extends JPanel implements BoardListener { 
	private List<Square> squares;
	
	private Color boardColor;		// color of empty squares
	private Color borderColor;		// color of border lines
	private Color firstColor;		// color of player 1 squares
	private Color secondColor;		// color of player 2 squares
	private Color highlightSquareColor;			// color of highlighted square
	private Color highlightRowColor;			// color of highlighted squares in rows
	private Color pickedHighlightSquareColor; 	// user picked square highlight color
	
	private Polygon highlightedSquare; 		// square to be highlighted
	private List<Polygon> highlightList;	// list of polygons to be highlighted
	
	private QubicPlane3D[] planes; 		// array of planes
	private Map<String, Square> sqMap; 	// map of every square
	private int width;
	private int height;
	private int gap;
	
	private static final long serialVersionUID = 1;

	private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(171, 130,
			255);

	private static final Color DEFAULT_ROW_HIGHLIGHT_COLOR = new Color(255, 255,
			255, 185);
	
	/**
	 * Creates a pseudo 3D view that displays a 
	 * graphical representation of the game board.
	 * @param QubicBoard
	 */
	public View3D(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		width = 200;
		height = 150;
		gap = 10;
		
		firstColor = Color.BLUE;
		secondColor = Color.RED;
		boardColor = Color.YELLOW;
		borderColor = Color.BLACK;
		highlightRowColor = DEFAULT_ROW_HIGHLIGHT_COLOR;
		pickedHighlightSquareColor = DEFAULT_HIGHLIGHT_COLOR;
		
		planes = new QubicPlane3D[4];
		for (int i = planes.length - 1; i >= 0; i--) {
			planes[i] = new QubicPlane3D(new Point2D.Double(gap, i
					* (gap + height)), width, height);
		}
	}
	
	/**
	 * Overrides the default method to draw all the squares in their appropriate colors.
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
				g2.setPaint(boardColor);
				Square s = new Square(4 - (j % 4), j / 4 + 1, i + 1);
				if (sqMap.get(s.toString()).getState() == QubicBoard.Player.FIRST)
					g2.setPaint(firstColor);
				else if (sqMap.get(s.toString()).getState() == QubicBoard.Player.SECOND)
					g2.setPaint(secondColor);
				g2.fill(poly);
				g2.setPaint(borderColor);
				g2.draw(poly);
			}
		}
		
		if (highlightList != null) {			
			for (Polygon p : highlightList) {
				g2.setPaint(highlightRowColor);
				g2.fill(p);
				g2.setPaint(borderColor);
				g2.draw(p);
			}
		}
		
		if (highlightedSquare != null) {	
			g2.setPaint(highlightSquareColor);
			g2.fill(highlightedSquare);
			g2.setPaint(borderColor);
			g2.draw(highlightedSquare);
		}
	}
	
	/**
	 * Highlights the given square
	 * @param Square
	 */
	public void highlightSquare(Square s1) {
		highlightedSquare = null;
		Square s = sqMap.get(s1.toString());

		if (s != null) {
			highlightRows(s);
			highlightedSquare = getPolygonAtSquare(s);
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
	 * Highlights all of the squares that are in a row of 4 with the given
	 * square
	 * 
	 * @param Square
	 */
	private void highlightRows(Square s) {
		List<Row> rows = s.containingRows();
		highlightList = new ArrayList<Polygon>();
		
		for (Row r : rows) {			
			for (Square s1 : r.getSquares()) {				
				Square s2 = sqMap.get(s1.toString());
				if (s2.getState() == null) {
					Polygon hSquare = getPolygonAtSquare(s2);
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
	/**
	 * Used internally to determine which polygons to highlight, given a Square parameter.
	 * @param s
	 * @return
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
	/**
	 * Updates the view by receiving the newest version of the board.
	 */
	public void update(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		repaint();
	}

	/**
	 * Returns the square at a given point
	 * If the square doesnt exist at that point
	 * a square with all dimensions set to -1 is returned
	 * @param Point2D
	 * @return Square
	 */
	/**
	 * When asked by the controller which square to update, this method
	 * returns that square given a point relative to this the panel.
	 * @param p
	 * @return
	 */
	public Square getSquareAtPoint(Point2D p) {
		for (int i = 0; i < planes.length; i++) {
			if (planes[i].contains(p)) {
				return planes[i].getSquareAtPoint(p, i + 1);
			}
		}
		return new Square(-1, -1, -1);
	}

	/**
	 * Creates a map of squares with the keys in the format of <Row, Col, Plane>
	 * linked to the corresponding square
	 * @param List<Square>
	 * @return Map<String, Square>
	 */
	/**
	 * Makes a map associating each square received in the list from the
	 * board with its toString, thereby letting me draw the squares in any order.
	 * @param sq
	 * @return
	 */
	private Map<String, Square> mapMaker(List<Square> sq) {
		Map m = new HashMap<String, Square>();
		for (Square s : sq) {
			m.put(s.toString(), s);
		}
		return m;
	}

	/**
	 * Sets the color of the game board
	 * @param Color
	 */
	public void setBoardColor(Color c) {
		boardColor = c;
	}

	/**
	 * Sets the color of the border lines
	 * @param Color
	 */
	public void setBorderColor(Color c) {
		borderColor = c;
	}

	/**
	 * Sets the color of the player's game pieces
	 * @param Color
	 */
	public void setFirstColor(Color c) {
		firstColor = c;
	}

	/**
	 * Sets the color of the computer's game pieces
	 * @param Color
	 */
	public void setSecondColor(Color c) {
		secondColor = c;
	}

	/**
	 * Sets the color of the highlighted square
	 * @param Color
	 */
	public void setHighlightSquareColor(Color c) {
		pickedHighlightSquareColor = c;
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
