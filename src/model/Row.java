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
	 * An enumerator for the Row and its possible states: OPEN, COMPUTER, HUMAN
	 * and MIXED.  
	 * @author John Thomson
	 * @see Row#getNumSelected()
	 */
	public enum RowState {OPEN, COMPUTER, HUMAN, MIXED};
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
		else if (state == RowState.COMPUTER)
			output += "computer" + numSelected;
		else if (state == RowState.HUMAN)
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
	 * It updates the Row state information, determining if it is a computer,
	 * human or mixed.
	 *
	 */
	private void stateChanged() {		
		RowState tempState = RowState.OPEN;
		for (Square s : selectedSquares) {
			if (s.getState() == Player.COMPUTER) {
				if (tempState == RowState.OPEN) {
					tempState = RowState.COMPUTER;
				} else if (tempState == RowState.HUMAN) {
					tempState = RowState.MIXED;
				}
			} else if (s.getState() == Player.HUMAN) {
				if (tempState == RowState.OPEN) {
					tempState = RowState.HUMAN;
				} else if (tempState == RowState.COMPUTER) {
					tempState = RowState.MIXED;
				}
			}
		}
		state = tempState;
	}
	
	/**
	 * Returns the state of the Row.  
	 * @return state - OPEN, HUMAN, COMPUTER, MIXED
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
	
	public List<Square> getSelectedSquares() {
		return Collections.unmodifiableList(selectedSquares);
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