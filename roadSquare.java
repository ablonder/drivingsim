import java.util.ArrayList;

/**
 * This class represents each square on the grid on which the agents will act
 * 
 * Methods:
 * 		constructor
 * 		occupy (moves car into square, tells if crash or goal)
 * 		changeSignal (changes the signal)
 * 		empty  (moves car out of square)
 * 
 * @author Haven
 */


public class roadSquare {
	
	public final int x;				//x-coordinate of square, should never be changed
	public final int y;				//y-coordinate of square, should never be changed
	public Car car;					//car present in roadSquare, null if no car
	public final boolean exit;		//whether it's an exit, should never be changed
	public ArrayList<roadSquare> neighbors;	//arraylist of neighbors
	public int signal;				//-1 for left, 1 for right, 0 for none
	
	/**
	 * Constructor that sets class variables
	 */
	public roadSquare(int x, int y, Car car, boolean exit, ArrayList<roadSquare> neighbors) {
		this.x = x;
		this.y = y;
		this.car = car;
		this.exit = exit;
		this.signal = 0;
		this.neighbors = neighbors;
	}
	
	/**
	 * Sets the signal for the car for where it wants to go
	 * -1 for left, 0 for straight, 1 for right
	 * 
	 * @return int -1, 0, 1
	 */
	public int changeSignal() {
		signal = car.getAction(this, neighbors);
		return signal;
	}
	
	/**
	 * Moves car into roadSquare
	 * 
	 * @return "CRASH" if crash
	 * @return "GOAL" if exit
	 * @return "X" otherwise
	 */
	public String occupy(Car newCar) {
		if (car != null) {
			empty();			//removes both cars
			return "CRASH";
		}
		this.car = newCar;
		if (exit==true) {
			// We should probably remove cars when they reach their exit so that they don't crash into each other
			// You may also want to check to see if this is the correct exit for that car
			return "GOAL";
		}
		return "X";
	}
	
	/**
	 * Moves car out of roadSquare
	 */
	public void empty() {
		car = null;
	}
	
}
