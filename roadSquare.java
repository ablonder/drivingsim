/**
 * This class represents each square on the grid on which the agents will act
 * 
 * Methods:
 * 		constructor
 * 		occupy (moves car into square, tells if crash or goal)
 * 		empty  (moves car out of square)
 * 
 * @author Haven
 */


public class roadSquare {
	
	public final int x;				//x-coordinate of square, should never be changed
	public final int y;				//y-coordinate of square, should never be changed
	public boolean occupied;		//whether it's occupied by a car
	public final boolean exit;		//whether it's an exit, should never be changed
	
	/**
	 * Constructor that sets class variables
	 */
	pulic roadSquare(int x, int y, boolean occupied, boolean exit) {
		this.x = x;
		this.y = y;
		this.occupied = occupied;
		this.exit = exit;
	}
	
	/**
	 * Moves car into roadSquare
	 * 
	 * @return "CRASH" if crash
	 * @return "GOAL" if exit
	 * @return "X" otherwise
	 */
	public String occupy() {
		if (occupied) {
			empty();			//removes both cars
			return "CRASH";
		}
		occupied = true;
		if (exit==true) {
			return "GOAL";
		}
		return "X";
	}
	
	/**
	 * Moves car out of roadSquare
	 */
	public void empy() {
		occupied = false;
	}
	
}
