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
	
	private Color boardColor;		// color of empty squares
	private Color humanColor;		// color of human sq
	private Color computerColor;	// color of computer squares
	private Color highlightSquareColor;	// color of highlighted square
	private Color highlightRowColor;		// color of highlighted squares in rows
	
	private Rectangle2D highlightedSquare; // square to be highlighted
	private List<Rectangle2D> hList;
	
	private List<Square> squares; // list of every square
	private Map<String, Square> sqMap;	// map of every square
	
	private static final long serialVersionUID = 1;

	
	/**
	 * Creates a view that displays a graphical representation of the
	 * game board.
	 * @param board
	 */
	public View(QubicBoard board) {
		squares = board.getGrid();
		boardColor = Color.WHITE;
		humanColor = Color.BLUE;
		computerColor = Color.RED;
		highlightRowColor = new Color(185, 211, 238);
		highlightSquareColor = new Color(159, 182, 205);
		sqMap = mapMaker(squares);
	}
	
	/**
	 * Draws the graphics on the screen
	 * @param g Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		setPreferredSize(new Dimension(0, boxArea + gap * 2));
		drawBoxes(g2);
		
		g2.setPaint(highlightRowColor);
		if (hList != null) {
			for (Rectangle2D r : hList) {
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
	 * @param board
	 */
	public void update(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		repaint();
	}
	
	/**
	 * Creates a map of squares with the keys in the format of <Row, Col, Plane>
	 * linked to the corresponding square
	 * @param sq
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
	 * @param g2 Graphics2D
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
					Rectangle2D border = new Rectangle2D.Double(
							gap - 1 + ((size + 1) * j) + p * (gap + boxArea), y - 1 + ((size + 1) * i), size + 2, size + 2);
					Rectangle2D box = new Rectangle2D.Double(
							gap + ((size + 1) * j) + p * (gap + boxArea), y + ((size + 1) * i), size, size);
					
					
					g2.setPaint(Color.BLACK);
					g2.fill(border);

					Square s = squares.get(counter);
					Player player = s.getState();

					if (player == Player.HUMAN)
						g2.setPaint(humanColor);
					else if (player == Player.COMPUTER)
						g2.setPaint(computerColor);
					else
						g2.setPaint(boardColor);

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
	 * @param p Point2D
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
			col = (int) (p.getX() - (plane * gap + plane * boxArea)) / (size + 1) + 4;

		Square s = new Square(row, col, plane);
		for (Square s2 : squares) {
			if (s2.equals(s)) {
				return s2;
			}
		}
		return s;
	}
	
	/**
	 * Highlights the given square
	 * @param s1
	 * @param player
	 */
	public void highlightSquare(Square s1) {
		Square s = sqMap.get(s1.toString());
		
		if (s != null) {
			highlightRows(s);
		} else {
			hList = null;
		}
		
		if (s != null && s.getState() == null) {
			int x = (int) s.getY() - 1;
			int y1 = (int) s.getX() - 1;
			int z = (int) s.getZ() - 1;

			highlightedSquare = new Rectangle2D.Double(
					gap + ((size + 1) * x) + z * (gap + boxArea), y + ((size + 1) * y1), size, size);	
		} else
			highlightedSquare = null; 

		repaint();
	}
	
	/**
	 * Highlights all of the squares that are in a row of 4 with the given square
	 * @param s
	 */
	private void highlightRows(Square s) {
		List<Row> rows = s.containingRows();
		hList = new ArrayList<Rectangle2D>();
		
		for (Row r : rows) {
			for (Square s1 : r.getSquares()) {				
				int x = (int) s1.getY() - 1;
				int y1 = (int) s1.getX() - 1;
				int z = (int) s1.getZ() - 1;
				
				Square s2 = sqMap.get(s1.toString());
				if (s2.getState() == null) {
					Rectangle2D hSquare = new Rectangle2D.Double(
							gap + ((size + 1) * x) + z * (gap + boxArea), y + ((size + 1) * y1), size, size);

					hList.add(hSquare);
				}
			}
		}
	}

	/**
	 * Sets the color of the game board
	 * @param c
	 */
	public void setBoardColor(Color c) {
		boardColor = c;
	}
	
	/**
	 * Sets the color of the player's game pieces
	 * @param c
	 */
	public void setHumanColor(Color c) {
		humanColor = c;
	}
	
	/**
	 * Sets the color of the computer's game pieces
	 * @param c
	 */
	public void setComputerColor(Color c) {
		computerColor = c;
	}
	/**
	 * Sets the color of the highlighted square
	 * @param c
	 */
	public void setHighlightSquare(Color c) {
		highlightSquareColor = c;
	}
	
	/**
	 * Sets the color of the highlighted squares in the rows
	 * @param c
	 */
	public void setHighlightRow(Color c) {
		highlightRowColor = c;
	}
}
