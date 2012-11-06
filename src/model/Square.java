package model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.QubicBoard.Player;
import model.Row.RowState;
/**
 * The Square object for the game, recording all moves on the board.
 * @author John Thomson
 *
 */
public class Square implements Serializable {
	private Player state;
	private ArrayList<Row> containingRows;
	private int x;
	private int y;
	private int z;
	private static final long serialVersionUID = 1;
	
	/**
	 * Constructs a new, unselected Square object with the coordinates x, y and z.
	 * @param x
	 * @param y
	 * @param z
	 */
	public Square(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		containingRows = new ArrayList<Row>();
		state = null;
	}
	
	/**
	 * Returns the an unmodifiable version of the set of the 
	 * containing Rows for this Square.
	 * @return containingRows - All rows that contain this square.
	 */
	public List<Row> containingRows() {
		return Collections.unmodifiableList(containingRows);
	}
	
	/**
	 * Returns the x coordinate of the Square.
	 * @return x
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the y coordinate of the Square.
	 * @return y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Returns the z coordinate of the Square.
	 * @return z
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Only to be used in constructing the square, this adds a row to
	 * the Square's inventory of containing rows.
	 * @param r
	 */
	public void addRow(Row r) {
		containingRows.add(r);
	}
	
	/**
	 * Returns the selected state of the square.
	 * 
	 * @return state - Of type player, it can be either Player.SECOND,
	 * Player.FIRST or null (unselected).
	 */
	public Player getState() {
		return state;
	}
	
	/**
	 * Sets the selected state of the square. 
	 * @param player
	 *            
	 */
	public void setState(Player player) {
		state = player;
		stateChanged();
	}
	
	/**
	 * Used by the recursive AI for simulating the human's move.
	 * @return s - The Square that completes the 4-in-a-row.
	 */
	public Square getOtherSquare() {
		for (Row r : containingRows) {
			//System.out.println(this.diagnostic());
			if (r.getNumSelected() == 3 && r.getState() != RowState.MIXED) {
				List<Square> tempList = new ArrayList<Square>(r.getSquares());
				//System.out.println(r);
				for (Square s : r.getSelectedSquares()) {
					if (s.getState() != null)
						tempList.remove(s);
				}
				return tempList.get(0);
			}
		}
		System.out.println("Um.. We have a problem...\n" + this.diagnostic());
		return null;
	}
	
	/**
	 * Returns a String representation of the Square object.
	 */
	public String toString() {
		return "<" + x + ", " + y + ", " + z + ">";
	}
	
	/**
	 * This is useful for finding all relevant information about a square.
	 * It returns all Square state information and the states of the rows
	 * that it is within.
	 * @return A verbose string representation of the Square
	 */
	public String diagnostic() {
		String output = "";
		String stateString = "";
		if (this.getState() == Player.SECOND)
			stateString = "is selected by the player";
		else if (this.getState() == Player.FIRST)
			stateString = "is selected by the computer";
		else
			stateString = "is not selected";
			
		output += "The point " + this.toString() + " " + stateString +
					" and is associated with the rows:\n";
		for (Row r : containingRows) {
			output += r + "\n";
		}
		return output;
	}
	
	/**
	 * Compares this square with another square, if they have the
	 * same coordinates, they are equal.
	 */
	public boolean equals(Object o) {
		Square s = (Square) o;
		if (this.x == s.x && this.y == s.y && this.z == s.z)
			return true;
		else
			return false;
	}
	
	/**
	 * Updates the state of the rows that this square is connected
	 * to, so when a square is selected, its rows will reflect that
	 * change.
	 *
	 */
	private void stateChanged() {
		for (Row r : containingRows) {
			r.selectSquare(this);
		}
	}
	/**
	 * Only the board is to use this!
	 * Resets the square.
	 */
	public void clear() {
		state = null;
	}
	
	/**
	 * Erases the move on this particular square and all rows that it is
	 * associated with. 
	 *
	 */
	public void undo() {
		state = null;
		for (Row r : containingRows)
			r.undo(this);
	}
}