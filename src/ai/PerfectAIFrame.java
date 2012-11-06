package ai;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class PerfectAIFrame {
	private JFrame frame;
	private PerfectAIPanel panel;
	private PerfectAI ai;
	private Timer timer;
	private JProgressBar bar;
	
	public long startTime;
	public double remaining;
	public double completed;
	public double prev_completed;
	public double maxPossibleMoves;
	
	public PerfectAIFrame(PerfectAI parent) {
		ai = parent;

        // set up frame
        frame = new JFrame();
        frame.setTitle("Perfect AI Strategy Computer Readout");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set up animation panel
        panel = new PerfectAIPanel();
        frame.add(panel, "Center");

        // set up timer and slider
        addTimer();
        addProgressBar();
    }
	
    // pre : all components have been initialized
    // post: begins the animation by making the frame visible and starting 
    //       the timer
    public void start() {
        frame.setVisible(true);
        timer.start();
        ai.computeStrategy();
    }
    
    // post: creates a slider that allows the user to change the speed of the
    //       animation (frames per second, 1 to 30) and adds it to the southern
    //       part of the frame.
    private void addProgressBar() {
        bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        /*slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                timer.setDelay(1000 / slider.getValue());
            }
        });*/
        // start at 20 frames per second
        bar.setValue(0);

        // create a panel for the slider and its slow/fast labels
        JPanel p = new JPanel();
        p.add(new JLabel("Start"));
        p.add(bar);
        p.add(new JLabel("Finish"));
        frame.add(p, "South");
    }
    
    // post: creates a timer that repaints the animation
    private void addTimer() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        timer.setCoalesce(true);
        
    }
    
    private void update() {
    	ai.updateInterface(this);
    	panel.update(this);
    	bar.setValue((int)(100*(completed)/maxPossibleMoves));
    }
 }

