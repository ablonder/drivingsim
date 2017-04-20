import java.util.ArrayList;

/*
 * The World class, which runs the program including the roadSquares and the Cars
 * 
 * @author Haven
 */

public class World {

	int speedLimit = 70; // we can change as needed

	ArrayList<ArrayList<roadSquare>> road = new ArrayList<ArrayList<roadSquare>>();
	ArrayList<roadSquare> exits = new ArrayList<roadSquare>();

	ArrayList<Car> cars = new ArrayList<Car>();
	double carDensity = 0.3; // number of cars per 100 roadSquares
	
	int step = 0; // the number of ticks that have passed so far in the simulation for use in calculating speed


	/*
	 * Constructor that creates all roadSquares and randomly sets exits
	 * 
	 * You may want to include the number of rows and number of columns as parameters to make it more flexible
	 */
	public World() {
		// /* Creates the roadSquares, 5 lanes of 200 squares with null car */
		// roadSquare current;
		// for (int y=0; y<5; y++) {
		// for (int x=0; x<200; x++) {
		// current = new RoadSquare(x, y, null, )
		// }
		// }
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
