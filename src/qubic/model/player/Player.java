package qubic.model.player;



/**
 * A human player of Qubic. AI players will extend this class.
 */
public class Player {
	boolean first;

	public Player(boolean f) {
		first = f;
	}

	/**
	 * Tells the player to begin their turn. The player must call
	 * Board.endTurn(Square s) in order for the game to continue
	 */
	public void play() {
		return;
	}

	/**
	 * Returns true if this player is currently playing as player 1.
	 */
	public boolean amIFirst() {
		return first;
	}
}
