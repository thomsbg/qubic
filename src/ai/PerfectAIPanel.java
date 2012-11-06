package ai;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class PerfectAIPanel extends JPanel{
	
	private long startTime;
	private double remaining;
	private double completed;
	private double prev_completed;
	private double maxPossibleMoves;
	
	public PerfectAIPanel() {
		setBackground(Color.WHITE);
	}
	
    // post: paints the next frame of the animation
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // set up font size based on current animation count and find the
    	// bounding rectangle of the text
    	int fontSize = 10;
    	Font f = new Font("Serif", Font.BOLD, fontSize);
    	g.setFont(f);
    	String text = //"" + count + " moves processed at " + (.001*(System.currentTimeMillis() - startTime)/count) + " seconds per operation \n" +
				//"and " + (count/(.001*(System.currentTimeMillis() - startTime))) + " moves processed per second\n" +
				//"Completed " + 100.*(completed/maxPossibleMoves) + "% of the calculated operations \n" +
				//"Moves that are out of the way: " + completed + "\n" +
				//"Total estimated moves: " + maxPossibleMoves + "\n" +
				(completed) + " moves computed so far, this accounts for " + (100*(completed)/maxPossibleMoves) + "%";
    	
    	
    	FontRenderContext context = g2.getFontRenderContext();
    	Rectangle2D bounds = f.getStringBounds(text, context);

    	// center the text within the panel and surround with 2 points of
    	// extra space
    	int x = 2;//(int) ((getWidth() - bounds.getWidth()) / 2.0);
    	int y = 10 + fontSize;//(getHeight() + fontSize) / 2;

    	
    	// use a background fill and draw the text over it
    	g2.setPaint(Color.BLACK);
    	g2.drawString(text, x, y);
    }
    
    public void update(PerfectAIFrame frame) {
		completed = frame.completed ;
		maxPossibleMoves = frame.maxPossibleMoves;
		prev_completed = frame.prev_completed;
		remaining = frame.remaining;
		startTime = frame.startTime;
		repaint();
    }
}
