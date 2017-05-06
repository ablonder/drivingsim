import java.util.ArrayList;
import java.util.Random;


/*
 * The World class, which runs the program including the roadSquares and the Cars
 * 
 * @author Haven
 */

public class World {

	static int numRuns = 200;
	static int speedLimit = 3; // minimum speed, cars will travel anywhere from
	// 1 to
	// speedLimit
	static float vision = (float) .95; // the proportion of the time the car
	// notices
	// something in its blind spot
	static float carDensity = (float) 0.2; // number of cars per 2 roadSquares

	public ArrayList<roadSquare> exits = new ArrayList<roadSquare>();
	public static ArrayList<ArrayList<roadSquare>> road = new ArrayList<ArrayList<roadSquare>>();
	ArrayList<Car> cars = new ArrayList<Car>();
	public static ArrayList<roadSquare> crashes = null; // list of crashes that
														// have occured in the
														// last tick

	public static drawGrid screen;
	public int frameRate = 200; // rate it changes in milliseconds
	
	int step = 0; // the number of ticks that have passed so far in the
					// simulation for use in calculating speed
	int crashcount; // the number of car crashes that have occurred
	int goals; // the number of cars that have reached their goal
	int numExits = 3; // the number of exit per side
	public static int numLanes;
	public static int numCols;

	Random random = new Random();


	/*
	 * Constructor that creates all roadSquares and randomly sets exits
	 * 
	 * @param int numLanes
	 * @param int numCols
	 * @param int seed
	 */
	public World(int numLanes, int numCols, int seed) {
		exits.clear();
		road.clear();
		cars.clear();
		goals = 0;
		crashcount = 0;

		// seed random
		random.setSeed(seed);
		
		// create the screen
		screen = new drawGrid(numLanes, numCols);
		this.numLanes = numLanes;
		this.numCols = numCols;

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
				int speed = rand.nextInt(speedLimit) + 1;
				Car car = new Car(road.get(lane).get(0), exits.get(exitDraw), speed, 1, true, vision, this);
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
		
		// list of roadSquares that have had crashes this tick
		crashes = new ArrayList<roadSquare>();
		
		// change the signal of every car in the simulation
		for(Car car : cars){
			// boolean to make sure double moves happen
			boolean cont = true;
			roadSquare newsq;
			// make sure the car still exists
			if(newcars.contains(car)){
				// grab the car's location
				roadSquare square = car.location;
				// store the signal in case it's time to move the car
				int sig = square.changeSignal();
				// check to see if it's time to move the car, and if so, move it accordingly and save the result
				if(step%car.speed == 0){
					while(cont && newcars.contains(car)){
						if(sig == 0){
							cont = false;
							newsq = square.neighbors.get(3);
						} else if(sig == 1){
							newsq = square.neighbors.get(1);
						} else {
							newsq = square.neighbors.get(5);
						}
						String result = newsq.occupy(car);
						if(result.equals("GOAL")){
							cont = false;
							newcars.remove(car);
							goals++;
						} else if(result.equals("CRASH")){
							cont = false;
							newcars.remove(car);
							newcars.remove(newsq.car);
							newsq.empty();
							crashes.add(newsq);
							crashcount++;
						}
						square.empty();
						// for the next run through, change sig to 0
						sig = 0;
						// and change square to the current square
						square = newsq;
					}
				}
			}
		}
		// updates the list of cars to include modifications
		cars = newcars;
		
		if (step%2 == 0) { // initalizes new cars every other step
			initializeCars();
		}
		
		//				if(square.cell == null){System.out.println("Fuck");}
		//				square.checkCar();
		//				if(drawGrid.cellMatrix[square.y][square.x]==null){System.out.println("Double Fuck");}
		screen.dispose();
	}
	
	/**
	 * Runs the simulation
	 */
	static void runWorld() {
		World world = new World(5, 100, 0);
		for (int t = 0; t < numRuns; t++) {
			// System.out.printf("t= %d\n", t);
			world.tick();
		}
		System.out.println("Step: " + world.step);
		System.out.println("Crashes: " + world.crashcount);
		System.out.println("Goals: " + world.goals);
		System.out.println("\n----------------\n");
	}

	/**
	 * Prints out the stats like speedLimit before the game
	 */
	public static void statPrinter() {
		System.out.printf("numRuns= %d\n", numRuns);
		System.out.printf("speedLimit= %d\n", speedLimit);
		System.out.printf("vision= %f\n", vision);
		System.out.printf("carDensity= %f\n\n", carDensity);
	}

	/**
	 * Main method from which to run the simulation
	 * 
	 * @param args
	 *            0 - numRuns (optional). 1 - what test to run (or nothing if
	 *            just using preset values). 2,3...+ - what values to use in
	 *            tests
	 * 
	 *            note: vision/density should be in int form to be divided by
	 *            100
	 * @tests speedLimit, vision, density, and risk
	 * @TODO risk method
	 */
	public static void main(String[] args){
		if (args.length > 0) { // if we have params

			/* Checking first param for numRuns */
			if (args[0].matches("[0-9]+")) {
				numRuns = Integer.parseInt(args[0]);
				for (int i = 0; i < args.length; i++) { // shift one back
					System.out.println("i= " + i);
					args[i] = args[i - 1];
				}
			}

			if (args.length == 1) {
				runWorld();
			}
			else {
				statPrinter();
				switch (args[0].toLowerCase()) {
				case "speedlimit":
					for (int i = 1; i < args.length; i++) {
						if (!args[i].matches("[0-9]+")) {
							System.out.printf("Improper argument %s\n", args[i]);
							continue;
						}
						speedLimit = Integer.parseInt(args[i]);
						System.out.println("SpeedLimit test " + i + ": speedLimit = " + speedLimit);
						runWorld();
					}
					break;
				case "vision":
					for (int i = 1; i < args.length; i++) {
						if (!args[i].matches("[0-9]+")) {
							System.out.printf("Improper argument %s\n", args[i]);
							continue;
						}
						vision = (float) Integer.parseInt(args[i]);
						vision /= 100;
						System.out.println("Vision test " + i + ": vision = " + vision);
						runWorld();
					}
					break;
				case "density":
					for (int i = 1; i < args.length; i++) {
						if (!args[i].matches("[0-9]+")) {
							System.out.printf("Improper argument %s\n", args[i]);
							continue;
						}
						carDensity = (float) Integer.parseInt(args[i]);
						carDensity /= 100;
						System.out.println("carDensity test " + i + ": carDensity = " + carDensity);
						runWorld();
					}
					break;
				case "risk":

					break;
				default:
					System.out.println("Improper test given, must be speedLimit/vision/density/risk.");
				}
			}
		}
		else {
			statPrinter();
			runWorld(); // run once normally
		}
		
	}

}
