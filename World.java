import java.util.ArrayList;

/*
 * The World class, which runs the program including the roadSquares and the Cars
 * 
 * @author Haven
 */

public class World {

	int speedLimit = 70; // we can change as needed

	public ArrayList<ArrayList<roadSquare>> road = new ArrayList<ArrayList<roadSquare>>();
	public ArrayList<roadSquare> exits = new ArrayList<roadSquare>();

	ArrayList<Car> cars = new ArrayList<Car>();
	double carDensity = 0.3; // number of cars per 100 roadSquares
	
	int step = 0; // the number of ticks that have passed so far in the simulation for use in calculating speed


	/*
	 * Constructor that creates all roadSquares and randomly sets exits
	 * 
	 * @param int numLanes
	 * @param int numCols
	 */
	public World(int numLanes, int numCols) {
		
		/* Adds the roadSquares to road */
		roadSquare current;
		for (int y=0; y<numLanes; y++) {
			road.add(new ArrayList<roadSquare>());
			for (int x=0; x<numCols; x++) {
				/* func definition: roadSquare(int x, int y, Car car, boolean exit) */
				/* Sets as not a car, and not an exit */
				current = new roadSquare(x, y, null, false);
				road.get(y).add(current);
			}
		}
		
		/* Sets each roadSquare's neighbors to list of 8 squares, and null if out of bounds 
		 * Sets null for neighbors over the edge
		 * Note that route is continuous, so neighbor of the far right column is the far left column.
		 */
		for (int y=0; y<numLanes; y++) {
			for (int x=0; x<numCols; x++) {
				ArrayList<roadSquare> neighbors = new ArrayList<roadSquare>();
				current = road.get(y).get(x);
				
				if (y != numLanes-1) {		//if not on top lane
					neighbors.add(road.get(y+1).get(x-1));		//top left
					neighbors.add(road.get(y+1).get(x));		//top
					neighbors.add(road.get(y+1).get(x+1));		//top right
				}
				else { neighbors.add(null); neighbors.add(null); neighbors.add(null); }
				
				if (x != numCols-1) {		//if not in far right
					neighbors.add(road.get(y).get(x+1));		//right
				}
				else { 						//else add far left
					neighbors.add(road.get(y).get(0)); }		
				
				if (y != 0) {			//if not on bottom lane
					neighbors.add(road.get(y-1).get(x+1));		//bottom right
					neighbors.add(road.get(y-1).get(x));		//bottom
					neighbors.add(road.get(y-1).get(x-1));		//bottom left
				}
				else { neighbors.add(null); neighbors.add(null); neighbors.add(null); }
				
				if (x != 0) {			//if not in far left
					neighbors.add(road.get(y).get(x-1));		//left
				}
				else { 					//else add far right
					neighbors.add(road.get(y).get(numCols)); }
				
				current.setNeighbors(neighbors);
			}
		}
	}
	
	/*
	 * A single tick in the simulation
	 * 
	 * We'll have to integrate the GUI in here
	 */
	public void tick(){
		// increment step
		step++;
		// change the signal of every car in the simulation
		for(int l = 0; l < road.size(); l++){
			// grab this lane
			ArrayList<roadSquare> lane = road.get(l);
			for(int sq = 0; sq < lane.size(); sq++){
				// grab this square
				roadSquare square = lane.get(sq);
				// if there is a car in that square, change its signal
				if(square.car != null){
					// store the signal in case it's time to move the car
					int sig = square.changeSignal();
					// check to see if it's time to move the car, and if so, move it accordingly
					if(step%square.car.speed == 0){
						if(sig == 0){
							lane.get(sq+1).occupy(square.car);
						} else {
							road.get(l-sig).get(sq).occupy(square.car);
						}
						square.empty();
					}
				}
			}
		}
	}

}
