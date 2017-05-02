import java.util.ArrayList;
import java.util.Random;


/*
 * The World class, which runs the program including the roadSquares and the Cars
 * 
 * @author Haven
 */

public class World {

	int speedLimit = 2; // we can change as needed

	public static ArrayList<ArrayList<roadSquare>> road = new ArrayList<ArrayList<roadSquare>>();
	public static drawGrid screen;
	public int frameRate = 250; // rate it changes in milliseconds

	public ArrayList<roadSquare> exits = new ArrayList<roadSquare>();

	ArrayList<Car> cars = new ArrayList<Car>();
	double carDensity = 0.3; // number of cars per 2 roadSquares
	
	int step = 0; // the number of ticks that have passed so far in the
					// simulation for use in calculating speed
	int crashes = 0; // the number of car crashes that have occurred
	int goals = 0; // the number of cars that have reached their goal
	int numExits = 3; // the number of exit per side
	public int numLanes;
	public int numCols;
	float vision = (float) .95; // the proportion of the time the car notices
								// something in its blind spot

	/*
	 * Constructor that creates all roadSquares and randomly sets exits
	 * 
	 * @param int numLanes
	 * @param int numCols
	 */
	public World(int numLanes, int numCols) {
		
		this.numLanes = numLanes;
		this.numCols = numCols;

		/* Adds the roadSquares to road */
		roadSquare current;
		for (int y=0; y<numLanes; y++) {
			road.add(new ArrayList<roadSquare>());
			for (int x=0; x<numCols; x++) {
				/* func definition: roadSquare(int x, int y, Car car, boolean exit) */
				/* Sets as not a car, and not an exit */
				current = new roadSquare(x, y, null, false, drawGrid.cellMatrix[y][x]);
				road.get(y).add(current);
			}
		}
		
		/*
		 * Sets goal squares based on class variable numExits Currently sets an
		 * even number spaced equally on each side
		 */
		int t = numCols / numExits;
		while (t < numCols) {
			road.get(0).get(t).exit = true;
			road.get(numLanes - 1).get(t).exit = true;
			exits.add(road.get(0).get(t));
			exits.add(road.get(numLanes - 1).get(t));
			t += numCols / numExits;
		}
		
		initializeCars();
		
		/* Sets each roadSquare's neighbors to list of 8 squares, and null if out of bounds 
		 * Sets null for neighbors over the edge
		 * Note that route is continuous, so neighbor of the far right column is the far left column.
		 */
		for (int y=0; y<numLanes; y++) {
			for (int x=0; x<numCols; x++) {
				ArrayList<roadSquare> neighbors = new ArrayList<roadSquare>();
				current = road.get(y).get(x);
				
				/* Top neighbors */
				if (y != numLanes - 1) {
					if (x != 0) {
						neighbors.add(road.get(y + 1).get(x - 1)); // top left
					} else {
						neighbors.add(road.get(y + 1).get(numCols - 1));
					}

					neighbors.add(road.get(y+1).get(x));		//top

					if (x != numCols - 1) {
						neighbors.add(road.get(y + 1).get(x + 1)); // top right
					} else {
						neighbors.add(road.get(y + 1).get(0));
					}
				}
				else { neighbors.add(null); neighbors.add(null); neighbors.add(null); }
				
				/* Right neighbor */
				if (x != numCols-1) {		//if not in far right
					neighbors.add(road.get(y).get(x+1));		//right
				}
				else { 						//else add far left
					neighbors.add(road.get(y).get(0));
				}
				
				/* Bottom Neighbors */
				if (y != 0) {
					if (x != numCols - 1) {
						neighbors.add(road.get(y - 1).get(x + 1)); // bottom
																	// right
					} else {
						neighbors.add(road.get(y - 1).get(0));
					}

					neighbors.add(road.get(y-1).get(x));		//bottom

					if (x != 0) {
						neighbors.add(road.get(y - 1).get(x - 1)); // bottom
																	// left
					} else {
						neighbors.add(road.get(y - 1).get(numCols - 1));
					}
				}
				else {
					neighbors.add(null);
					neighbors.add(null);
					neighbors.add(null);
				}
				
				/* Left neighbor */
				if (x != 0) {			//if not in far left
					neighbors.add(road.get(y).get(x-1));		//left
				}
				else { 					//else add far right
					neighbors.add(road.get(y).get(numCols - 1));
				}
				
				current.setNeighbors(neighbors);
			}
		}
	}
	
	/*
	 * Adds cars to each lane in first column depending on carDensity
	 */
	private void initializeCars() {
		Random rand = new Random();
		float draw;
		int exitDraw;
		for (int lane = 0; lane < numLanes; lane++) {
			draw = rand.nextFloat();
			if (road.get(lane).get(0).car != null) {
				continue;
			} // if car already, continue
			if (draw < carDensity) {
				exitDraw = rand.nextInt(exits.size()); // random exit index
				Car car = new Car(road.get(lane).get(0), exits.get(exitDraw), speedLimit, vision, rand);
				road.get(lane).get(0).car = car;
				cars.add(car);
			}
		}
	}
	
	/*
	 * A single tick in the simulation
	 * 
	 * We'll have to integrate the GUI in here
	 */
	public void tick(){
		try{
			//print something here
			Thread.sleep(frameRate); // sleep for 3 seconds
			//print something else here
			}
			catch(InterruptedException e){    System.out.println("got interrupted!");
			}
		// increment step
		step++;
		
		// creates a new list of cars to hold any changes made
		ArrayList<Car> newcars = new ArrayList<Car>(cars);			
		
		// change the signal of every car in the simulation
		for(Car car : cars){
			// grab the car's location
			roadSquare square = car.location;
			// store the signal in case it's time to move the car
			int sig = square.changeSignal();
			// boolean to make sure double moves happen
			boolean cont = true;
			roadSquare newsq;
			// check to see if it's time to move the car, and if so, move it accordingly and save the result
			if(step%car.speed == 0 && newcars.contains(car)){
				while(cont){
					if(sig == 0){
						cont = false;
						newsq = square.neighbors.get(3);
					} else if(sig == 1){
						newsq = square.neighbors.get(5);
					} else {
						newsq = square.neighbors.get(1);
					}
					String result = newsq.occupy(car);
					if(result.equals("GOAL")){
						newcars.remove(car);
						goals++;
					} else if(result.equals("CRASH")){
						newcars.remove(car);
						newcars.remove(newsq.car);
						newsq.empty();
						crashes++;
					}
					square.empty();
					// for the next run through, change sig to 0
					sig = 0;
					// and change square to the current square
					square = newsq;
				}
			}
		}
		// updates the list of cars to include modifications
		cars = newcars;
		
		if (step % 2 == 0) { // initalizes new cars every other step
			initializeCars();
		}
		
		//				if(square.cell == null){System.out.println("Fuck");}
		//				square.checkCar();
		//				if(drawGrid.cellMatrix[square.y][square.x]==null){System.out.println("Double Fuck");}
		screen.dispose();
	}
	
	/**
	 * Main method from which to run the simulation
	 * 
	 * @param args - list containing the number of lanes, number of columns, and number of ticks to run
	 * 
	 * TODO - modify to use args instead of preset values
	 */
	public static void main(String[] args){
		drawGrid screen = new drawGrid(5, 100);
		
		World world = new World(5, 100);
		for(int t = 0; t < 1000; t++){
			System.out.printf("t= %d\n", t);
			world.tick();
		}
		System.out.println("Step: " + world.step);
		System.out.println("Crashes: " + world.crashes);
		System.out.println("Goals: " + world.goals);
	}

}
