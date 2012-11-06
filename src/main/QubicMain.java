package main;
import java.util.Scanner;

import model.QubicBoard;
import controller.GameController;

public class QubicMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner("");
		Scanner input2 = new Scanner("");
		try {
			input = new Scanner(QubicMain.class.getResourceAsStream("/resources/rows.dat"));
			input2 = new Scanner(QubicMain.class.getResourceAsStream("/resources/squares2.dat"));
		} catch (Exception e) {
			System.out.println("File not found");
			throw new RuntimeException(e);
		}
		QubicBoard q = new QubicBoard(QubicBoard.Player.COMPUTER, input, input2);
		
		GameController c = new GameController(q);
		c.start();
		/*try {
			Thread.sleep(10000);
		} catch (Exception e) {}
		
		q.currentPlayer = QubicBoard.Player.COMPUTER;
		PerfectAI ai = new PerfectAI(q);
		ai.computeStrategy();*/

				
		// The ai plays itself for 10 moves.
		//for (int i = 0; i < 10; i++) {
		//	q.select(ai.go());
		//}
		//System.out.println(q);
	
	}
}
