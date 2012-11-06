package qubic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import qubic.model.player.EasyComputer;
import qubic.model.player.Player;
import qubic.view.BoardListener;

/**
 * An object representing the state of the Qubic board. It constructs lists to
 * store data about the state of the board. First is the list of Rows and the
 * second is the list of Squares. Also, a list of BoardListeners is recorded so
 * when the state of the board changes, the view may be updated.
 */
public class Board {
	private List<Row> rows;
	private Map<Integer, Square> squares;
	private List<BoardListener> listeners;

	private Player firstPlayer, secondPlayer;
	private boolean firstPlayersTurn, gameOver;
	private int mixedRowCount;
	
	public static final int HUMAN = 1;
	public static final int EASY = 1;

	/**
	 * Constructs a new QubicBoard with the specified arguments.
	 * 
	 * @param rowScanner
	 *            Scanner input to the file containing rows schema
	 * @param squareScanner
	 *            Scanner input to the file containing squares schema
	 */
	public Board(Scanner rowScanner, Scanner squareScanner) {
		rows = new ArrayList<Row>();
		squares = new HashMap<Integer, Square>();
		listeners = new ArrayList<BoardListener>();

		firstPlayer = new Player(true);
		secondPlayer = new EasyComputer(this, false);
		
		firstPlayersTurn = true;
		gameOver = false;
		
		mixedRowCount = 0;

		// Read in the rows schema file, build Row objects, and add them to a
		// list.
		while (rowScanner.hasNextLine()) {
			String line = rowScanner.nextLine().trim();
			String[] values = line.split("[ \t]+");

			Square[] children = new Square[4];
			for (int i = 0; i < 4; i++) {
				int x = Integer.parseInt(values[3 * i + 1]);
				int y = Integer.parseInt(values[3 * i + 2]);
				int z = Integer.parseInt(values[3 * i + 3]);
				if (squares.containsKey(100 * x + 10 * y + z))
					children[i] = squares.get(100 * x + 10 * y + z);
				else {
					Square s = new Square(x, y, z);
					children[i] = s;
					squares.put(100 * x + 10 * y + z, s);
				}
			}

			rows.add(new Row(children));
		}

		// Read in the squares schema file, create Square objects,
		// and put them in a map.
		while (squareScanner.hasNextLine()) {
			String line = squareScanner.nextLine().trim();
			String[] values = line.split("[ \t]+");
			int x = Integer.parseInt(values[0]);
			int y = Integer.parseInt(values[1]);
			int z = Integer.parseInt(values[2]);
			Square s = squares.get(100 * x + 10 * y + z);
			for (int i = 3; i < values.length; i++) {
				s.addParentRow(rows.get(Integer.parseInt(values[i]) - 1));
			}
		}

		firstPlayer.play();
	}

	/**
	 * Returns the square associated with the given coordinates.
	 */
	public Square getSquare(int x, int y, int z) {
		return squares.get(100 * x + 10 * y + z);
	}

	/**
	 * Returns a list of all the squares in the game.
	 */
	public List<Square> getOpenSquares() {
		List<Square> openSquares = new ArrayList<Square>();
		for (Square s : squares.values()) {
			if (s.getOwner() == null)
				openSquares.add(s);
		}
		return openSquares;
	}
	
	/**
	 * If the game has been won, this will return the winning Row. Returns null
	 * when the game is not over, or in the event of a cat's game.
	 */
	public Row getWinningRow() {
		if (gameOver) {
			for (Row r : rows) {
				if (r.getNumSelected() == 4 && !r.isMixed())
					return r;
			}
		}
		return null;
	}

	/**
	 * Returns false if and only if the game is over due to a win or cats game.
	 */
	public boolean isGameOver() {
		return gameOver || (mixedRowCount >= 76);
	}

	/**
	 * End a turn by selecting the given square, checking for game over, then
	 * switching players.
	 */
	public void endTurn(Square s) {
		if (!isGameOver()) {
			Player player = firstPlayersTurn ? firstPlayer : secondPlayer;
			s.setOwner(player);
			
			firstPlayersTurn = !firstPlayersTurn;
			gameOver = false;
			
			for (Row r : s.getParentRows()) {
				r.updateOwner();
				if (r.getNumSelected() == 4) {
					if (r.isMixed())
						mixedRowCount++;
					else {
						gameOver = true;
						break;
					}
				}
			}
			
			updateListeners();
			
			player = firstPlayersTurn ? firstPlayer : secondPlayer;
			player.play();
		}
	}

	/**
	 * Adds a BoardListener to the list of listeners. These listeners are
	 * alerted whenever the board is changed.
	 */
	public void addListener(BoardListener listener) {
		listeners.add(listener);
		listener.update();
	}

	/**
	 * Removes a BoardListener from the listener list.
	 */
	public void removeListener(BoardListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Alerts all listeners to the fact that the state of the board has been
	 * changed and supplies them with the the new board with which to update
	 * themselves.
	 */
	private void updateListeners() {
		for (BoardListener l : listeners) {
			l.update();
		}
	}

	/**
	 * Resets the state of the Board, clearing all Rows and Squares and starting
	 * a new game.
	 */
	public void restartGame() {
		System.out.println("Resetting Board..");
		for (Row r : rows)
			r.clearOwner();
		for (Square s : squares.values())
			s.clearOwner();
		firstPlayersTurn = true;
		gameOver = false;
		mixedRowCount = 0;
		updateListeners();
		firstPlayer.play();
	}

	/**
	 * Returns a String representation of the Board.
	 */
	public String toString() {
		String output = "";
		for (int i = 1; i <= 4; i++) {
			output += "  ";
			for (int j = 1; j <= 4; j++) {
				for (int k = 1; k <= 4; k++) {
					Square s = getSquare(i, k, j);
					if (s.getOwner() == firstPlayer)
						output += "O ";
					else if (s.getOwner() == secondPlayer)
						output += "X ";
					else
						output += "_ ";
				}
				output += "  ";
			}
			output += "\n";
		}
		return output;
	}
	
	public void setPlayer1(int p) {
		if (p == HUMAN)
			firstPlayer = new Player(true);
		else if (p == EASY)
			firstPlayer = new EasyComputer(this, true);
		if (firstPlayersTurn)
			firstPlayer.play();
	}
	
	public void setPlayer2(int p) {
		if (p == HUMAN)
			secondPlayer = new Player(false);
		else if (p == EASY)
			secondPlayer = new EasyComputer(this, false);
		if (!firstPlayersTurn)
			secondPlayer.play();
	}
}
