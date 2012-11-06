package qubic.model;

import java.util.ArrayList;
import java.util.List;

import qubic.model.player.Player;

/**
 * An object representing a row of squares on the Qubic board. It contains an
 * array of Squares for use in determining the current state of the Row based on
 * the Squares' states.
 */
public class Row {
	private Square[] squares;
	private Player owner;

	/**
	 * Constructs a new Row object with the squares contained in the array.
	 */
	public Row(Square[] children) {
		squares = children;
		owner = null;
	}

	/**
	 * Returns the owner of the Row, null if it is open or mixed.
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Used by the board in clearing the board for a new game.
	 */
	public void clearOwner() {
		owner = null;
	}

	/**
	 * Updates the Row state based on the state of the member squares.
	 */
	public void updateOwner() {
		owner = squares[0].getOwner();
		for (int i = 1; i < 4; i++) {
			Player o = squares[i].getOwner();
			if (o != null) {
				if (owner == null) {
					owner = o;
				} else if (owner != o) {
					owner = null;
					break;
				}
			}
		}
	}

	/**
	 * Returns the number of selected Squares in this Row.
	 */
	public int getNumSelected() {
		int tally = 0;
		for (int i = 0; i < 4; i++) {
			if (squares[i].getOwner() != null)
				tally++;
		}
		return tally;
	}

	/**
	 * Returns true if the row is occupied by both players.
	 */
	public boolean isMixed() {
		return getNumSelected() > 0 && owner == null;
	}

	/**
	 * Returns a list of all Squares in the Row, selected or not.
	 */
	public Square[] getSquares() {
		return squares;
	}

	/**
	 * Returns a list of the selected Squares in the Row.
	 */
	public List<Square> getSelectedSquares() {
		List<Square> selectedSquares = new ArrayList<Square>(4);
		for (int i = 0; i < 4; i++) {
			if (squares[i].getOwner() != null)
				selectedSquares.add(squares[i]);
		}
		return selectedSquares;
	}

	/**
	 * Returns a list of the unselected Squares in the Row.
	 */
	public List<Square> getUnselectedSquares() {
		List<Square> unselectedSquares = new ArrayList<Square>(4);
		for (int i = 0; i < 4; i++) {
			if (squares[i].getOwner() == null)
				unselectedSquares.add(squares[i]);
		}
		return unselectedSquares;
	}

	/**
	 * A String representation of the Row's state and contents.
	 */
	public String toString() {
		String output = "";
		if (isMixed())
			output += "mixed";
		else if (owner == null)
			output += "open";
		else if (owner.amIFirst())
			output += "player1 owned";
		else
			output += "player2 owned";
		output += " row clicked " + getNumSelected()
				+ " times with the squares: \n";
		for (Square s : squares) {
			output += s.toString() + " ";
		}
		return output;
	}
}