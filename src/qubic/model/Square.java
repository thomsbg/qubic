package qubic.model;

import java.util.ArrayList;
import java.util.List;

import qubic.model.player.Player;

/**
 * An object representing a Square on the Qubic board. The coordinates, owner
 * and parent rows of each square are recorded.
 */
public class Square {
	private Player owner;
	private List<Row> parentRows;
	private int x, y, z;

	/**
	 * Constructs a new, unselected Square object with the coordinates x, y and
	 * z.
	 */
	public Square(int x, int y, int z) {
		owner = null;

		this.x = x;
		this.y = y;
		this.z = z;

		parentRows = new ArrayList<Row>(7);
	}

	/**
	 * Returns the x coordinate of the Square.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the Square.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the z coordinate of the Square.
	 */
	public int getZ() {
		return z;
	}
	
	public void addParentRow(Row r) {
		parentRows.add(r);
	}

	/**
	 * Returns a list of parent Rows identifiers for this Square.
	 */
	public List<Row> getParentRows() {
		return parentRows;
	}

	/**
	 * @return owner - The current owner of this square.
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * @param player
	 *            - Sets the owner to be the given player.
	 */
	public void setOwner(Player player) {
		owner = player;
	}

	/**
	 * Resets the square to be unoccupied.
	 */
	public void clearOwner() {
		owner = null;
	}

	/**
	 * Returns a String representation of the Square object.
	 */
	public String toString() {
		return "<" + x + ", " + y + ", " + z + ">";
	}

	/**
	 * Compares this square with another square, if they have the same
	 * coordinates, they are equal.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Square))
			return false;

		Square s = (Square) o;
		if (this.x == s.x && this.y == s.y && this.z == s.z)
			return true;
		else
			return false;
	}

	/**
	 * The hash code for a square may simply be a three digit number, because x,
	 * y, and z are guaranteed to be less than 10.
	 */
	public int hashCode() {
		return 100 * x + 10 * y + z;
	}
}