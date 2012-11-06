package view;
import model.QubicBoard;

/**
 * Interface for all Listeners to the QubicBoard.
 * 
 */
public interface BoardListener {
	/**
	 * Updates each view of the board
	 * @param board
	 */
	public void update(QubicBoard board);
}
