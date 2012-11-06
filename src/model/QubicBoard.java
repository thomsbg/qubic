package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import model.Row.RowState;
import view.BoardListener;
import ai.EasyAI;
import ai.ExpertAI;
import ai.HardAI;
import ai.QubicAI;
import ai.SimpleAI;

/**
 * This is the model for the Qubic game.  It constructs two lists for data storage.
 * One is the lists is the list of Rows and the other is the list of Squares.
 * Also, a list of BoardListeners is included so when listeners are added,
 * they can be updated by the QubicBoard. 
 * @author John Thomson
 *
 */
public class QubicBoard implements Cloneable, Serializable {
	private ArrayList<Row> rows;
	private ArrayList<Square> squares;
	private ArrayList<BoardListener> listeners;
	private ArrayList<QubicAI> ais;
	private QubicAI currentFirstAI;
	private QubicAI currentSecondAI;
	private Stack<Square> undoStack;
	private boolean gameOver;
	private boolean catsGame;
	private Player currentPlayer;
	private Player firstPlayer;
	private Player secondPlayer;
	private Square lastMove;
	
	private static final long serialVersionUID = 1;
	
	/**
	 * An enumerator for who's turn it is, HUMAN, or COMPUTER. 
	 * @author John Thomson
	 */
	public enum Player {
		HUMAN, COMPUTER;
		//public Object clone() { return super.clone();}
	};
	
	/**
	 * Constructs a new QubicBoard with the specified arguments.
	 * @param initialPlayer
	 * @param allRows Scanner input to the dat file containing the rows information
	 * @param allSquares Scanner input to the dat file containing the rows information
	 */
	public QubicBoard(Player first, Player second, Scanner allRows, Scanner allSquares) {
		firstPlayer = first;
		secondPlayer = second;
		currentPlayer = firstPlayer;
		
		rows = new ArrayList<Row>();
		squares = new ArrayList<Square>();
		listeners = new ArrayList<BoardListener>();
		ais = new ArrayList<QubicAI>();
		addAI(new SimpleAI(this));
		addAI(new EasyAI(this));
		addAI(new HardAI(this));
		addAI(new ExpertAI(this));
		setFirstAI("Hard");
		setSecondAI("Hard");
		undoStack = new Stack<Square>();
		gameOver = false;
		catsGame = false;
		// This is just reading the rows file and converting it into
		// a set of integers that can be used to build the set of rows.
		//TODO: Remove the need for clipping, it is unnecessary and could
		//      be decrimental to our grade.
		while (allRows.hasNextLine()) {
			String line = allRows.nextLine();
			String[] values = line.split("[ \t]+");
			ArrayList<String> clippedValues = new ArrayList<String>();
			for (int i = 0; i < values.length; i++) {
				if (!values[i].equals("")) {
					clippedValues.add(values[i]);
				}
			}
			ArrayList<Square> rowList = new ArrayList<Square>();
			for (int i = 0; i < 4; i++) {
				int x = Integer.parseInt(clippedValues.get(3 * i + 1));
				int y = Integer.parseInt(clippedValues.get(3 * i + 2));
				int z = Integer.parseInt(clippedValues.get(3 * i + 3));
				Square s = new Square(x, y, z);
				rowList.add(s);
			}
			rows.add(new Row(rowList.get(0), rowList.get(1), rowList.get(2),
					rowList.get(3)));
		}

		// This parses up the squares.dat file and associates each square with
		// every row in the setup.
		while (allSquares.hasNextLine()) {
			String square = allSquares.nextLine();
			String[] values = square.split(" ");
			ArrayList<String> clippedValues = new ArrayList<String>();
			for (int i = 0; i < values.length; i++) {
				if (!values[i].equals("")) {
					clippedValues.add(values[i]);
				}
			}

			int x = Integer.parseInt(clippedValues.get(0));
			int y = Integer.parseInt(clippedValues.get(1));
			int z = Integer.parseInt(clippedValues.get(2));
			Square s = new Square(x, y, z);
			for (int i = 3; i < clippedValues.size(); i++) {
				int rowIndex = Integer.parseInt(clippedValues.get(i)) - 1;
				s.addRow(rows.get(rowIndex));
			}
			squares.add(s);
			//System.out.println(s.diagnostic());
		}
	}
	/**
	 * Returns an unmodifiable version of the squares list.
	 * @return the list of squares, unmodifiable
	 */
	public List<Square> getGrid() {
		return Collections.unmodifiableList(squares);
	}
	
	private void addAI(QubicAI ai) {
		if (!ais.contains(ai))
			ais.add(ai);
	}
	
	public QubicAI getFirstAI() {
		return currentFirstAI;
	}
	
	public void setFirstAI(String name) {
		for (QubicAI ai: ais) {
			if (name.equals(ai.toString()))
				currentFirstAI = ai;
		}
	}
	
	public QubicAI getSecondAI() {
		return currentSecondAI;
	}
	
	public void setSecondAI(String name) {
		for (QubicAI ai: ais) {
			if (name.equals(ai.toString()))
				currentSecondAI = ai;
		}
	}
	
	public Player getFirstPlayer() {
		return firstPlayer;
	}
	
	public void setFirstPlayer(Player p) {
		firstPlayer = p;
	}
	
	public Player getSecondPlayer() {
		return secondPlayer;
	}
	
	public void setSecondPlayer(Player p) {
		secondPlayer = p;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Selects the determined square and sets it to the determined player
	 * as the owner.  Returns false if the game is over.
	 * @return true if the square was sucessfully selected, false if
	 * the square had been previously selected or the game is over.
	 */
	public boolean select(Square s) {
		if (gameOver) // If the game is over, you can't change the board.
			return false;
		int index = squares.indexOf(s);
		if (index == -1) {
			System.out.println("The square you gave me doesn't exist on the board!!!");
			//throw new IllegalArgumentException();
			return false;
		} else if (squares.get(index).getState() == null) {
			squares.get(index).setState(currentPlayer);
			lastMove = squares.get(index);
		}
		else
			return false;
		changePlayer();
		stateChanged();
		undoStack.push(s);
		return true;
	}
	
	/**
	 * Adds a BoardListener to the list of listeners to the board.
	 * These listeners are alerted whenever the board is changed with
	 * BoardListener.update(this).
	 * @param listener The listener you want to add
	 */
	public void addListener(BoardListener listener) {
		listeners.add(listener);
		stateChanged();
	}
	
	/**
	 * Removes a BoardListener from the listener list.  Use this whenever
	 * you disable or remove an old view or controller that no longer needs
	 * to be updated.
	 * @param listener The listener you want to remove
	 */
	public void removeListener(BoardListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Alerts all listeners to the fact that the state of the board has
	 * been changed and supplies them with the address to update themselves.
	 */
	private void stateChanged() {
		//TODO: Impliment Cat's Game determination.
		int count = 0;
		for (Row r : rows) {
			if (r.getNumSelected() == 4 && r.getState() != Row.RowState.MIXED) {
				//System.out.println("GameOver!");
				//System.out.println(r.getState());
				gameOver = true;
				break;
			}
			if (r.getState() == RowState.MIXED)
				count++;
		}
		if (count == 76)
			catsGame = true;
		for (BoardListener l : listeners) {
			l.update(this);
		}
	}
	
	/**
	 * Internal function that sets it so the next player now gets a turn.
	 */
	private void changePlayer() {
		if (currentPlayer == firstPlayer)
			currentPlayer = secondPlayer;
		else
			currentPlayer = firstPlayer;
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
					Square sqr = new Square(i, k, j);
					Square s = squares.get(squares.indexOf(sqr));
					if (s.getState() == firstPlayer)
						output += "O ";
					else if (s.getState() == secondPlayer)
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
	
	/**
	 * Resets the state of the Board, clearing all Rows and Squares
	 * and starting a new game.
	 *
	 */
	public void resetBoard() {
		for (Row r: rows)
			r.clear();
		for (Square s : squares)
			s.clear();
		gameOver = false;
		undoStack = new Stack<Square>();
		currentPlayer = firstPlayer;
		stateChanged();
	}
	/**
	 * Undoes the last move, setting it to the exact state before that
	 * move was made.
	 * @return move - The move that was undone.
	 */
	public Square undo() {
		if (undoStack.empty())
			return null;
		Square undo = undoStack.pop();
		
		int index = squares.indexOf(undo);
		if (index == -1)
			throw new RuntimeException("Undo Stack is corrupted, Square not found!");
		gameOver = false;
		catsGame = false;
		squares.get(index).undo();
		changePlayer();
		stateChanged();
		return undo;
	}
	
	/**
	 * Returns if an undo is possible.
	 * @return undoable
	 */
	public boolean canUndo() {
		return !undoStack.empty();
	}
	
	public Object clone() {
		QubicBoard clone;
		try {
			clone = (QubicBoard) super.clone();
			clone.listeners = (ArrayList<BoardListener>) listeners.clone();
			clone.rows = (ArrayList<Row>) rows.clone();
            clone.squares = (ArrayList<Square>) squares.clone();
            //for (int i = 0; i < squares.size(); i++)
            //    clone.squares.set(i, (Square)clone.squares.get(i).clone());
			//clone.undoStack = (Stack<Square>) undoStack.clone();
			//clone.currentPlayer = (Player) currentPlayer.
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return clone;
	}
	
	public Square getLastMove() {
		return lastMove;
	}
	
	public boolean catsGame() {
		return catsGame;
	}
	
	public boolean gameOver() {
		return gameOver;
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		listeners = new ArrayList<BoardListener>();
	}
}



