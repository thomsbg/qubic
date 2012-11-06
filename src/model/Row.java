package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.QubicBoard.Player;
/**
 * The data storage unit for the Row.  It contains a List of Squares
 * for use in determining the current state of the Row based on the 
 * Squares' states.
 * @author John Thomson
 *
 */
public class Row implements Serializable {
	// Need to add state information
	private ArrayList<Square> squares;
	private ArrayList<Square> selectedSquares;
	
	/**
	 * An enumerator for the Row and its possible states: OPEN, FIRST, SECOND
	 * and MIXED.  
	 * @author John Thomson
	 * @see Row#getNumSelected()
	 */
	public enum RowState {OPEN, FIRST, SECOND, MIXED};
	private RowState state;
	private int numSelected;
	
	private static final long serialVersionUID = 1;
	
	/**
	 * Constructs a new empty Row object with the points s1, s2, s3, s4.
	 * @param s1
	 * @param s2
	 * @param s3
	 * @param s4
	 */
	public Row(Square s1, Square s2, Square s3, Square s4) {
		squares = new ArrayList<Square>();
		selectedSquares = new ArrayList<Square>();
		state = RowState.OPEN;
		squares.add(s1);
		squares.add(s2);
		squares.add(s3);
		squares.add(s4);
	}
	
	/**
	 * Returns a String representation of the Row.
	 */
	public String toString() {
		String output = "";
		//output += "This is a ";
		if (state == RowState.OPEN)
			output += "open";
		else if (state == RowState.FIRST)
			output += "computer" + numSelected;
		else if (state == RowState.SECOND)
			output += "human" + numSelected;
		else
			output += "mixed";
		output += " row clicked " + numSelected + " times with the squares: \n";
		for(Square s : squares){
			output += s + " ";
		}
		return output;
	}
	
	/**
	 * Selects a given square on the Row.  Coordintates are ignored, only
	 * the information on who selected the Square is processed.
	 * @param s The selected Square
	 */
	public void selectSquare(Square s) {
		selectedSquares.add(s);
		numSelected++;
		stateChanged();
	}
	
	/**
	 * This is called every time the internal state of the Row is changed.
	 * It updates the Row state information, determining if it is a first,
	 * second or mixed.
	 *
	 */
	private void stateChanged() {		
		RowState tempState = RowState.OPEN;
		for (Square s : selectedSquares) {
			if (s.getState() == Player.FIRST) {
				if (tempState == RowState.OPEN) {
					tempState = RowState.FIRST;
				} else if (tempState == RowState.SECOND) {
					tempState = RowState.MIXED;
				}
			} else if (s.getState() == Player.SECOND) {
				if (tempState == RowState.OPEN) {
					tempState = RowState.SECOND;
				} else if (tempState == RowState.FIRST) {
					tempState = RowState.MIXED;
				}
			}
		}
		state = tempState;
	}
	
	/**
	 * Returns the state of the Row.  
	 * @return state - OPEN, SECOND, FIRST, MIXED
	 */
	public RowState getState() {
		return state;
	}
	
	/**
	 * Returns the number of selected Squares in this Row.
	 * @return numSelected
	 */
	public int getNumSelected() {
		return numSelected;
	}
	
	/**
	 * This will be useful for doing the "Highlight all possible Rows"
	 * functionality in the gui. Note: This does not return the list of
	 * SELECTED Squares, it returns a list of ALL Squares
	 * @return squares the list of all Squares in the Row, selected or not.
	 */
	public List<Square> getSquares() {
		return Collections.unmodifiableList(squares);
	}
	
	/**
	 * Returns an unmodifiable list of the selected Squares.
	 * @return selectedSquares
	 */
	public List<Square> getSelectedSquares() {
		return Collections.unmodifiableList(selectedSquares);
	}
	
	/**
	 * Returns an unmodifiable list of the unselected Squares.
	 * @return unselectedSquares
	 */
	public List<Square> getUnselectedSquares() {
		List<Square> result = new ArrayList<Square>(squares); 
		for (Square s : selectedSquares) {
			if (!result.remove(s)) {
				System.out.println("Inconsistent Square: " + s);
				System.out.println(squares);
				System.out.println(selectedSquares);
			}
		}
		return result;
	}
	
	/**
	 * This is only to be used by the board in clearing the board for
	 * a new game.
	 *
	 */
	public void clear() {
		state = RowState.OPEN;
		selectedSquares = new ArrayList<Square>();
		numSelected = 0;
	}
	
	/**
	 * Removes the specified square from the row, used for the
	 * board's undo function.
	 * @param s
	 */
	public void undo(Square s) {
		numSelected--;
		selectedSquares.remove(s); //TODO: Confirm functionality
		stateChanged();
	}
}