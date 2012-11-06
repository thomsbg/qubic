package view;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import model.Square;


/**
 * Used by the View3D class to define the trapezoid shapes drawn onscreen.
 * Contains low level details about coordinates and contains methods used 
 * to help the View3D class with the getSquareAtPoint method.
 * @author blake
 *
 */
public class QubicPlane3D {
	private Polygon[] boxes;
	private Polygon overallPlane;
	private int width;
	private int height;
	private Point2D p1;
	
	public QubicPlane3D(Point2D p1, int w, int h) {
		width = w;
		height = h;
		this.p1 = p1;
		boxes = new Polygon[16];
		
		computeBoxes();
	}

	public void computeBoxes() {
		double x1 = p1.getX();
		double x2 = p1.getX() + width * 0.25;
		double x3 = p1.getX() + width * 0.50;
		double x4 = p1.getX() + width * 0.75;
		double x5 = p1.getX() + width;
		double x6 = x2 + (x4 - x2) * 0.25;
		double x7 = x2 + (x4 - x2) * 0.75;
		
		double y1 = p1.getY();
		double y2 = p1.getY() - height * 0.25;
		double y3 = p1.getY() - height * 0.50;
		double y4 = p1.getY() - height * 0.75;
		double y5 = p1.getY() - height;
		
		Point2D p2 = new Point2D.Double(x1 + ((x2 - x1) * 0.25), y2);
		Point2D p3 = new Point2D.Double(x1 + ((x2 - x1) * 0.50), y3);
		Point2D p4 = new Point2D.Double(x1 + ((x2 - x1) * 0.75), y4);
		Point2D p5 = new Point2D.Double(x2, y5);
		
		Point2D p6 = new Point2D.Double(x2, y1);
		Point2D p7 = new Point2D.Double(x2 + ((x6 - x2) * 0.25), y2);
		Point2D p8 = new Point2D.Double(x2 + ((x6 - x2) * 0.50), y3);
		Point2D p9 = new Point2D.Double(x2 + ((x6 - x2) * 0.75), y4);
		Point2D p10 = new Point2D.Double(x6, y5);
		
		Point2D p11 = new Point2D.Double(x3, y1);
		Point2D p12 = new Point2D.Double(x3, y2);
		Point2D p13 = new Point2D.Double(x3, y3);
		Point2D p14 = new Point2D.Double(x3, y4);
		Point2D p15 = new Point2D.Double(x3, y5);
		
		Point2D p16 = new Point2D.Double(x4, y1);
		Point2D p17 = new Point2D.Double(x4 - (x4 - x7) * 0.25, y2);
		Point2D p18 = new Point2D.Double(x4 - (x4 - x7) * 0.50, y3);
		Point2D p19 = new Point2D.Double(x4 - (x4 - x7) * 0.75, y4);
		Point2D p20 = new Point2D.Double(x7, y5);
		
		Point2D p21 = new Point2D.Double(x5, y1);
		Point2D p22 = new Point2D.Double(x5 - (x5 - x4) * 0.25, y2);
		Point2D p23 = new Point2D.Double(x5 - (x5 - x4) * 0.50, y3);
		Point2D p24 = new Point2D.Double(x5 - (x5 - x4) * 0.75, y4);
		Point2D p25 = new Point2D.Double(x4, y5);
		
		boxes[0] = new Polygon();
		boxes[0].addPoint((int) p1.getX(), (int) p1.getY());
		boxes[0].addPoint((int) p2.getX(), (int) p2.getY());
		boxes[0].addPoint((int) p7.getX(), (int) p7.getY());
		boxes[0].addPoint((int) p6.getX(), (int) p6.getY());
		
		boxes[1] = new Polygon();
		boxes[1].addPoint((int) p2.getX(), (int) p2.getY());
		boxes[1].addPoint((int) p3.getX(), (int) p3.getY());
		boxes[1].addPoint((int) p8.getX(), (int) p8.getY());
		boxes[1].addPoint((int) p7.getX(), (int) p7.getY());

		boxes[2] = new Polygon();
		boxes[2].addPoint((int) p3.getX(), (int) p3.getY());
		boxes[2].addPoint((int) p4.getX(), (int) p4.getY());
		boxes[2].addPoint((int) p9.getX(), (int) p9.getY());
		boxes[2].addPoint((int) p8.getX(), (int) p8.getY());

		boxes[3] = new Polygon();
		boxes[3].addPoint((int) p4.getX(), (int) p4.getY());
		boxes[3].addPoint((int) p5.getX(), (int) p5.getY());
		boxes[3].addPoint((int) p10.getX(), (int) p10.getY());
		boxes[3].addPoint((int) p9.getX(), (int) p9.getY());

		boxes[4] = new Polygon();
		boxes[4].addPoint((int) p6.getX(), (int) p6.getY());
		boxes[4].addPoint((int) p7.getX(), (int) p7.getY());
		boxes[4].addPoint((int) p12.getX(), (int) p12.getY());
		boxes[4].addPoint((int) p11.getX(), (int) p11.getY());

		boxes[5] = new Polygon();
		boxes[5].addPoint((int) p7.getX(), (int) p7.getY());
		boxes[5].addPoint((int) p8.getX(), (int) p8.getY());
		boxes[5].addPoint((int) p13.getX(), (int) p13.getY());
		boxes[5].addPoint((int) p12.getX(), (int) p12.getY());

		boxes[6] = new Polygon();
		boxes[6].addPoint((int) p8.getX(), (int) p8.getY());
		boxes[6].addPoint((int) p9.getX(), (int) p9.getY());
		boxes[6].addPoint((int) p14.getX(), (int) p14.getY());
		boxes[6].addPoint((int) p13.getX(), (int) p13.getY());

		boxes[7] = new Polygon();
		boxes[7].addPoint((int) p9.getX(), (int) p9.getY());
		boxes[7].addPoint((int) p10.getX(), (int) p10.getY());
		boxes[7].addPoint((int) p15.getX(), (int) p15.getY());
		boxes[7].addPoint((int) p14.getX(), (int) p14.getY());

		boxes[8] = new Polygon();
		boxes[8].addPoint((int) p11.getX(), (int) p11.getY());
		boxes[8].addPoint((int) p12.getX(), (int) p12.getY());
		boxes[8].addPoint((int) p17.getX(), (int) p17.getY());
		boxes[8].addPoint((int) p16.getX(), (int) p16.getY());

		boxes[9] = new Polygon();
		boxes[9].addPoint((int) p12.getX(), (int) p12.getY());
		boxes[9].addPoint((int) p13.getX(), (int) p13.getY());
		boxes[9].addPoint((int) p18.getX(), (int) p18.getY());
		boxes[9].addPoint((int) p17.getX(), (int) p17.getY());

		boxes[10] = new Polygon();
		boxes[10].addPoint((int) p13.getX(), (int) p13.getY());
		boxes[10].addPoint((int) p14.getX(), (int) p14.getY());
		boxes[10].addPoint((int) p19.getX(), (int) p19.getY());
		boxes[10].addPoint((int) p18.getX(), (int) p18.getY());

		boxes[11] = new Polygon();
		boxes[11].addPoint((int) p14.getX(), (int) p14.getY());
		boxes[11].addPoint((int) p15.getX(), (int) p15.getY());
		boxes[11].addPoint((int) p20.getX(), (int) p20.getY());
		boxes[11].addPoint((int) p19.getX(), (int) p19.getY());

		boxes[12] = new Polygon();
		boxes[12].addPoint((int) p16.getX(), (int) p16.getY());
		boxes[12].addPoint((int) p17.getX(), (int) p17.getY());
		boxes[12].addPoint((int) p22.getX(), (int) p22.getY());
		boxes[12].addPoint((int) p21.getX(), (int) p21.getY());

		boxes[13] = new Polygon();
		boxes[13].addPoint((int) p17.getX(), (int) p17.getY());
		boxes[13].addPoint((int) p18.getX(), (int) p18.getY());
		boxes[13].addPoint((int) p23.getX(), (int) p23.getY());
		boxes[13].addPoint((int) p22.getX(), (int) p22.getY());

		boxes[14] = new Polygon();
		boxes[14].addPoint((int) p18.getX(), (int) p18.getY());
		boxes[14].addPoint((int) p19.getX(), (int) p19.getY());
		boxes[14].addPoint((int) p24.getX(), (int) p24.getY());
		boxes[14].addPoint((int) p23.getX(), (int) p23.getY());

		boxes[15] = new Polygon();
		boxes[15].addPoint((int) p19.getX(), (int) p19.getY());
		boxes[15].addPoint((int) p20.getX(), (int) p20.getY());
		boxes[15].addPoint((int) p25.getX(), (int) p25.getY());
		boxes[15].addPoint((int) p24.getX(), (int) p24.getY());
		
		overallPlane = new Polygon();
		overallPlane.addPoint((int) p1.getX(), (int) p1.getY());
		overallPlane.addPoint((int) p5.getX(), (int) p5.getY());
		overallPlane.addPoint((int) p25.getX(), (int) p25.getY());
		overallPlane.addPoint((int) p21.getX(), (int) p21.getY());
	}
	
	public Polygon[] getBoxes() {
		return boxes;
	}
	
	public boolean contains(Point2D p) {
		return overallPlane.contains(p);
	}
	
	public Square getSquareAtPoint(Point2D p, int plane) {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].contains(p)) {
				int x = 4 - (i % 4);
				int y = i / 4 + 1;
				return new Square(x, y, plane);
			}
		}
		System.out.println("The point is within the overall plane, but I" +
				"\ncould not find the specific box it was in");
		return new Square(-1, -1, -1);
	}
	
	public void setWidth(int n) {
		width = n;
	}
	
	public void setHeight(int n) {
		height = n;
	}
	
	public void setP1(Point2D p1) {
		this.p1 = p1;
	}
}
