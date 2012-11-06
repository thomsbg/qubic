package main;

import java.util.Scanner;

import javax.swing.JApplet;

import model.QubicBoard;
import controller.GameController2;

public class QubicApplet extends JApplet {

	public void init() {
		Scanner input = new Scanner("");
		Scanner input2 = new Scanner("");
		try {
			input = new Scanner(QubicApplet.class.getResourceAsStream("/resources/rows.dat"));
			input2 = new Scanner(QubicApplet.class.getResourceAsStream("/resources/squares2.dat"));
		} catch (Exception e) {
			System.out.println("File not found");
			throw new RuntimeException(e);
		}
		QubicBoard q = new QubicBoard(input, input2);
		
		GameController2 c = new GameController2(q);
		c.start();
		/*try {
			Thread.sleep(10000);
		} catch (Exception e) {}
		
		q.currentPlayer = QubicBoard.Player.FIRST;
		PerfectAI ai = new PerfectAI(q);
		ai.computeStrategy();*/

				
		// The ai plays itself for 10 moves.
		//for (int i = 0; i < 10; i++) {
		//	q.select(ai.go());
		//}
		//System.out.println(q);
	
	}
}
