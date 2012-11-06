package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import model.QubicBoard;
import model.Square;

/**
 * A board listener that implements a "fake" 3d view of the game.
 * @author Blake
 */
public class View3D extends JPanel implements BoardListener { 
	private List<Square> squares;
	private Color boardColor;
	private Color borderColor;
	private Color computerColor;
	private Color humanColor;
	private QubicPlane3D[] planes;
	private int width;
	private int height;
	private int gap;
	private Map<String, Square> sqMap;
	private static final long serialVersionUID = 1;
	
	public View3D(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		width = 200;
		height = 150;
		gap = 10;
		boardColor = Color.YELLOW;
		borderColor = Color.BLACK;
		humanColor = Color.BLUE;
		computerColor = Color.RED;
		planes = new QubicPlane3D[4];
		for (int i = planes.length - 1; i >= 0; i--) {
			planes[i] = new QubicPlane3D(new Point2D.Double(gap, i * (gap + height)), width, height);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		for (int i = planes.length - 1; i >= 0; i--) {
			int gap = getHeight() / 30;
			int height = getHeight() - 5 * gap;
			int width = getWidth() - 2 * gap;
			planes[i].setHeight(height / 4);
			planes[i].setWidth(width);
			planes[i].setP1(new Point2D.Double(gap,(i + 1) * (gap + height / 4)));
			planes[i].computeBoxes();
			for (int j = 0; j < planes[i].getBoxes().length; j++) {
				Polygon poly = planes[i].getBoxes()[j];
				g2.setPaint(boardColor);
				Square s = new Square(4 - (j % 4), j / 4 + 1, i + 1);
				if (sqMap.get(s.toString()).getState() == QubicBoard.Player.COMPUTER)
					g2.setPaint(computerColor);
				else if (sqMap.get(s.toString()).getState() == QubicBoard.Player.HUMAN)
					g2.setPaint(humanColor);
				g2.fill(poly);
				g2.setPaint(borderColor);
				g2.draw(poly);
			}
		}
	}
	
	public void update(QubicBoard board) {
		squares = board.getGrid();
		sqMap = mapMaker(squares);
		repaint();
	}
	
	public Square getSquareAtPoint(Point2D p) {
		for (int i = 0; i < planes.length; i++) {
			if (planes[i].contains(p)) {
				return planes[i].getSquareAtPoint(p, i + 1);
			}
		}
		return new Square(-1, -1, -1);
	}
	
	public void setBoardColor(Color c) {
		boardColor = c;
	}
	
	private Map<String, Square> mapMaker(List<Square> sq) {
		Map m = new HashMap<String, Square>(); 
		for (Square s : sq) {
			m.put(s.toString(), s);
		}
		return m;
	}
}
