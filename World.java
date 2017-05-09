import java.util.ArrayList;
import java.util.Random;


/*
 * The World class, which runs the program including the roadSquares and the Cars
 * 
 * @author Haven
 */

public class World {

	public static drawGrid screen;
	public int frameRate = 300; // rate it changes in milliseconds

	static int numRuns = 200;
	static int speedLimit = 3; // minimum speed, cars will travel anywhere from
	// 1 to
	// speedLimit
	static float vision = (float) .90; // the proportion of the time the car
	// notices
	// something in its blind spot
	static float carDensity = (float) 0.2; // number of cars per 2 roadSquares
	static int risk = 1; // the lower the riskier (less inclined to adapt to
							// traffic)
	static boolean varrisk = false; // whether cars get riskier as they aproach
									// exit

	public ArrayList<roadSquare> exits = new ArrayList<roadSquare>();
	public static ArrayList<ArrayList<roadSquare>> road = new ArrayList<ArrayList<roadSquare>>();
	public static ArrayList<roadSquare> crashes = null; // list of crashes that
	// have occured in the
	// last tick

	public static int humans = 0;		//proportion of human drivers
	public static int aggressives = 0;	//proportion of aggressive drivers
	public static int agents = 0;		//proportion of agent drivers
	public static int humanCount = 0; // total number of human drivers created
	public static int aggressiveCount = 0; // total number of aggressive drivers
											// created
	public static int agentCount = 0; // total number of agent drivers created
	public static int carCount = 0; // total number of cars created
	ArrayList<Car> cars = new ArrayList<Car>();
	
	int step = 0; // the number of ticks that have passed so far in the
					// simulation for use in calculating speed
	int crashcount; // the number of car crashes that have occurred
	static int humanCrashes = 0;
	static int aggressiveCrashes = 0;
	static int agentCrashes = 0;
	int goals; // the number of cars that have reached their goal
	static int humanGoals = 0;
	static int aggressiveGoals = 0;
	static int agentGoals = 0;
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
	 * Adds cars to each lane in first column depending on carDensityxs
	 */
	private void initializeCars() {
		Random rand = new Random();
		float draw;
		int draw2;
		Car car;
		int exitDraw;
		for (int lane = 0; lane < numLanes; lane++) {
			draw = rand.nextFloat();
			if (road.get(lane).get(0).car != null) {
				continue;
			} // if car already, continue
			if (draw < carDensity) {
				exitDraw = rand.nextInt(exits.size()); // random exit index
				int speed = rand.nextInt(speedLimit) + 1;

				draw2 = rand.nextInt(10) + 1;
				if (draw2 <= humans) {									//human car
					car = intializeHuman(road.get(lane).get(0), exits.get(exitDraw), speed);
					humanCount++;
				} else if (draw2 <= humans + aggressives) {				//aggressive car
					car = initializeAggressive(road.get(lane).get(0), exits.get(exitDraw));
					aggressiveCount++;
				} else if (draw2 <= humans + aggressives + agents) {	//agent car
					car = initializeAgent(road.get(lane).get(0), exits.get(exitDraw));
					agentCount++;
				} else {												//default car
					car = new Car(road.get(lane).get(0), exits.get(exitDraw), "DEFAULT", speed, risk, varrisk, vision,
							this);
				}
				road.get(lane).get(0).car = car;
				cars.add(car);
				carCount++;
			}
		}
	}
	
	/**
	 * Initializes car of profile human
	 * 
	 * @param initsq
	 * @param goalsq
	 * @param speed
	 * @return Car
	 */
	public Car intializeHuman(roadSquare initsq, roadSquare goalsq, int speed) {
		int thisRisk = 2;
		boolean thisVarrisk = true;
		float thisVision = (float) 0.9;
		return new Car(initsq, goalsq, "HUMAN", speed, thisRisk, thisVarrisk, thisVision, this);
	}

	/**
	 * Initializes a car of profile aggressive
	 * 
	 * @param initsq
	 * @param goalsq
	 * @return Car
	 */
	public Car initializeAggressive(roadSquare initsq, roadSquare goalsq) {
		int thisSpeed = 1;
		int thisRisk = 0;
		boolean thisVarrisk = true; // not significant cus already max risky
		float thisVision = (float) 0.8;
		return new Car(initsq, goalsq, "AGGRESSIVE", thisSpeed, thisRisk, thisVarrisk, thisVision, this);
	}

	/**
	 * Initializes a car of profile agent
	 * 
	 * @param initsq
	 * @param goalsq
	 * @return Car
	 */
	public Car initializeAgent(roadSquare initsq, roadSquare goalsq) {
		int thisSpeed = 2;
		int thisRisk = 3;
		boolean thisVarrisk = false;
		float thisVision = (float) 1;
		return new Car(initsq, goalsq, "AGENT", thisSpeed, thisRisk, thisVarrisk, thisVision, this);
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
							switch (car.type) {
							case "HUMAN":
								humanGoals++;
								break;
							case "AGGRESSIVE":
								aggressiveGoals++;
								break;
							case "AGENT":
								agentGoals++;
								break;
							}
						} else if(result.equals("CRASH")){

							/* Counts for car that DOESN'T cause the crash */
							switch (newsq.car.type) {
							case "HUMAN":
								humanCrashes++;
								break;
							case "AGGRESSIVE":
								aggressiveCrashes++;
								break;
							case "AGENT":
								agentCrashes++;
								break;
							}
							/* Counts for car that CAUSES the crashes */
							switch (car.type) {
							case "HUMAN":
								humanCrashes++;
								break;
							case "AGGRESSIVE":
								aggressiveCrashes++;
								break;
							case "AGENT":
								agentCrashes++;
								break;
							}
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
		System.out.println("Steps: " + world.step);
		System.out.println("Total Cars: " + world.carCount);
		System.out.println("Total Crashes: " + world.crashcount);
		System.out.println("Total Goals: " + world.goals);
		
		/* If we are comparing profiles */
		if (humans > 0 || aggressives > 0 || agents > 0) {
			System.out.printf("\t\t\tHumans\tAggres\tAgents\n");
			System.out.printf("Cars:\t\t\t%d\t%d\t%d\n", humanCount, aggressiveCount, agentCount);
			System.out.printf("Percent crashed:\t%.2f\t%.2f\t%.2f\n", (float) humanCrashes / (float) humanCount,
					(float) aggressiveCrashes / (float) aggressiveCount, (float) agentCrashes / (float) agentCount);
			System.out.printf("Percent exitted:\t%.2f\t%.2f\t%.2f\n", (float) humanGoals / (float) humanCount,
					(float) aggressiveGoals / (float) aggressiveCount, (float) agentGoals / (float) agentCount);
		}

		System.out.println("\n----------------\n");
	}

	/**
	 * Prints out the stats like speedLimit before the game
	 */
	public static void statPrinter() {
		System.out.println("Base stats: ");
		System.out.printf("numRuns= %d\n", numRuns);
		System.out.printf("speedLimit= %d\n", speedLimit);
		System.out.printf("vision= %.2f\n", vision);
		System.out.printf("carDensity= %.2f\n", carDensity);
		System.out.printf("risk= %d\n", risk);
		System.out.println("varrisk= " + varrisk + "\n");
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
			statPrinter();
			int length = args.length;

			/* Checking first param for numRuns */
			if (args[0].matches("[0-9]+")) {
				numRuns = Integer.parseInt(args[0]);
				for (int i = 0; i < args.length - 1; i++) { // shift one back
					args[i] = args[i + 1];
				}
				if (args.length > 1) {
					length--;
				}
			}
			
			/* Checking to see if we want to compare preset profiles */
			if (args[0].equalsIgnoreCase("profile")) {
				if (length == 4) {
					if (args[1].matches("[0-9]|10")) {
						humans = Integer.parseInt(args[1]);
					} else {
						System.out.println("Improper human percentage entered: enter int 0-10");
					}

					if (args[2].matches("[0-9]|10")) {
						aggressives = Integer.parseInt(args[2]);
					} else {
						System.out.println("Improper aggressive percentage entered: enter int 0-10");
					}

					if (args[3].matches("[0-9]|10")) {
						agents = Integer.parseInt(args[3]);
					} else {
						System.out.println("Improper agent percentage entered: enter int 0-10");
					}

					if (humans + aggressives + agents <= 10) {
						// print statements
						runWorld();
					} else {
						System.out.println("Humans + aggressives + agents must be less than or equal to 10");
					}
				} else {
					System.out.println("Enter int (humans), int (aggressives), int (agents)");
				}
			}

			/* Random one arg entered in */
			else if (length == 1 && !args[0].toLowerCase().equals("varrisk")) {
				runWorld();
			}

			/* Analysis of one variable */
			else {
				System.out.println("Singe variable avalysis\n");
				switch (args[0].toLowerCase()) {
				case "speedlimit":
					for (int i = 1; i < length; i++) {
						if (!args[i].matches("[0-9]+")) {
							System.out.printf("Improper argument %s\n", args[i]);
							continue;
						}
						System.out.printf("args[%d] = %s\n", i, args[i]);
						speedLimit = Integer.parseInt(args[i]);
						System.out.println("SpeedLimit test " + i + ": speedLimit = " + speedLimit);
						runWorld();
					}
					break;
				case "vision":
					for (int i = 1; i < length; i++) {
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
					for (int i = 1; i < length; i++) {
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
					for (int i = 1; i < length; i++) {
						if (!args[i].matches("[0-9]+")) {
							System.out.printf("Improper argument %s\n", args[i]);
							continue;
						}
						risk = Integer.parseInt(args[i]);
						System.out.println("Risk test " + i + ": risk = " + risk);
						runWorld();
					}
					break;
				case "varrisk":
					varrisk = true;
					System.out.println("Varrisk = true");
					runWorld();
					varrisk = false;
					System.out.println("Varrisk = false");
					runWorld();
					break;
				default:
					System.out.println("Improper test given, must be speedLimit/vision/density/risk/varrisk.");
				}
			}
		}
		else {
			statPrinter();
			runWorld(); // run once normally
		}
		
	}

}
