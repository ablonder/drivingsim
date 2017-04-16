import java.util.ArrayList;
import java.util.Random;

/*
 * Cars in the simulator.
 * 
 * @author ablonder
 */
public class Car {
	
	// current location
	// If this is only being used in getAction, this may not need to be a class variable
	roadSquare currentloc;
	// location of the exit the car is trying to reach
	static roadSquare goal;
	// the car's speed - currently unchanging
	static int speed;
	// proportion of the time the car can tell if something is in its blind spot (between 0 and 1)
	static float visibility;
	// a random number generator
	static Random random;
	
	
	/*
	 * Constructor for a new car to initialize class variables
	 * 
	 * @param roadSquare start - starting location
	 * @param roadSquare goalsq - the exit you're trying to get to
	 * @param int initspeed - the number of ticks it takes for you to move one square
	 * @param float vision - the proportion of the time the car notices something in its blind spot
	 * @param Random rand - the world's random number generator (so we can use the same seed)
	 * 
	 */
	public Car(roadSquare start, roadSquare goalsq, int initspeed, float vision, Random rand){
		currentloc = start;
		goal = goalsq;
		speed = initspeed;
		visibility = vision;
		random = rand;
	}
	
	
	/*
	 * Takes in the current state of the world and uses it to determine which action to take
	 * For now I'm having cars assume that all their neighbors will just move forward
	 * TODO - account for signaling
	 * 
	 * @param roadSquare newloc - the car's current location
	 * @param ArrayList<roadSquare> neighbors - list of 8 neighboring roadSquares
	 * 
	 * @return int action - chosen action where 1 is left, -1 is right, and 0 is forward.
	 */
	public int getAction(roadSquare newloc, ArrayList<roadSquare> neighbors){
		// check to see if the car can see it's back right and back left neighbors
		float ldraw = random.nextFloat();
		float rdraw = random.nextFloat();
		
		// for now set currentloc to newloc
		currentloc = newloc;
		
		// TODO - move to accommodate traffic, otherwise move toward the goal exit
		// if front left or right neighbors are signaling right or left respectively
		
		// TODO - if either draw is less than visibility, check to see if there is a neighbor there
		
		// for now, always return 0
		return 0;
		
	}
	
}
