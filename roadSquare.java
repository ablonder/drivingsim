import java.awt.Color;
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
	public boolean exit; // whether it's an exit
	public ArrayList<roadSquare> neighbors;	//arraylist of neighbors
	public int signal;				//-1 for left, 1 for right, 0 for none
	public drawGrid.CellPane cell;
	
	/**
	 * Constructor that sets class variables
	 */
	public roadSquare(int x, int y, Car car, boolean exit, drawGrid.CellPane cell) {
		this.x = x;
		this.y = y;
		this.car = car;
		this.exit = exit;
		this.signal = 0;
		this.cell = cell;
	}

	/**
	 * Sets the array of neighbors from the World class
	 * 
	 * @param neighbors
	 */
	public void setNeighbors(ArrayList<roadSquare> neighbors) {
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
		if (exit==true && newCar.goal == this) {
			empty();
			return "GOAL";
		}
		this.car = newCar;
		return "X";
	}
	
	/**
	 * Moves car out of roadSquare
	 */
	public void empty() {
		car = null;
	}
	
	public void checkCar() {
		if(car != null){
			System.out.println(y);
			System.out.println(x);
    		drawGrid.cellMatrix[y][x].setBackground(Color.BLUE);
    		
    	}
    	
    	else if(car == null){
    		System.out.println(y);
			System.out.println(x);
    		drawGrid.cellMatrix[y][x].setBackground(Color.GRAY);
    	}

		}
	}
	

