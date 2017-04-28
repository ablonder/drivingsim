import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
 * Cars in the simulator.
 * 
 * @author ablonder
 */
public class Car {
	
	// location of the exit the car is trying to reach
	final roadSquare goal;
	// the car's speed - currently unchanging
	final int speed;
	// proportion of the time the car can tell if something is in its blind spot (between 0 and 1)
	final float visibility;
	// a random number generator
	final Random random;
	// the car's current location
	roadSquare location;
	
	/*
	 * Constructor for a new car to initialize class variables
	 * 
	 * @param roadSquare initsq - the roadSqare the car starts at
	 * @param roadSquare goalsq - the exit you're trying to get to
	 * @param int initspeed - the number of ticks it takes for you to move one square
	 * @param float vision - the proportion of the time the car notices something in its blind spot
	 * @param Random rand - the world's random number generator (so we can use the same seed)
	 * 
	 */
	public Car(roadSquare initsq, roadSquare goalsq, int initspeed, float vision, Random rand){
		goal = goalsq;
		speed = initspeed;
		visibility = vision;
		random = rand;
		location = initsq;
	}
	
	
	/*
	 * Takes in the current state of the world and uses it to determine which action to take
	 * 
	 * @param ArrayList<roadSquare> neighbors - list of 8 neighboring roadSquares (clockwise starting with up-left)
	 * 
	 * @return int action - chosen action where 1 is left, -1 is right, and 0 is forward.
	 */
	public int getAction(ArrayList<roadSquare> neighbors){
		// check to see if the car can see it's back right and back left neighbors
		float ldraw = random.nextFloat();
		float rdraw = random.nextFloat();
		
		// list of possible directions to be removed as directions are noted as occupied
		ArrayList<Integer> posdirs = new ArrayList<Integer>(Arrays.asList(-1, 0, 1));
		
		// check to see if there actually is a neighbor immediately to the left
		if(neighbors.get(1) == null || neighbors.get(1).car != null){
			posdirs.remove(Integer.valueOf(1));
		}
		// to the right
		if(neighbors.get(5) == null || neighbors.get(5).car != null){
			posdirs.remove(Integer.valueOf(-1));
		}
		// or ahead
		if(neighbors.get(3) == null || neighbors.get(3).car != null){
			posdirs.remove(Integer.valueOf(0));
		}
		
		// if there is only one item left in the list of possible actions at this point, do that
		if(posdirs.size() == 1){
			return posdirs.get(0);
		}
		ArrayList<Integer> posdirs2;
		// if there are no actions left, refresh the list
		if(posdirs.size() == 0){
			posdirs = new ArrayList<Integer>(Arrays.asList(-1, 0, 1));
		}
		// now use that or the remaining actions from the previous sweep
		posdirs2 = new ArrayList<Integer>(posdirs);
		
		// either way, now use the car's neighbors' signals to determine which way to move
		// check behind to the left to see if a car is there and is not signaling
		if(neighbors.get(0) == null ||
				(ldraw <= visibility && neighbors.get(0).car != null && neighbors.get(0).signal == 0)){
			posdirs2.remove(Integer.valueOf(1));
		}
		// check behind to the right to see if a car is there and is not signaling
		if(neighbors.get(6) == null ||
				(rdraw <= visibility && neighbors.get(6).car != null && neighbors.get(6).signal == 0)){
			posdirs2.remove(Integer.valueOf(-1));
		}
		// check ahead to the right to see if a car is there and signaling left and ahead to the left to see if a car is there and signaling right
		if((neighbors.get(2) != null && neighbors.get(2).car != null && neighbors.get(2).signal == -1) ||
				(neighbors.get(4) != null && neighbors.get(4).car != null && neighbors.get(4).signal == 1)){
			posdirs2.remove(Integer.valueOf(0));
		}
		
		// if there is only one item left in the list of possible actions at this point, do that
		if(posdirs2.size() == 1){
			return posdirs2.get(0);
		}
		// if there are no actions left, revert to the previous list
		if(posdirs2.size() == 0){
			posdirs2 = posdirs;
		}
		
		// lastly, remove all directions that don't exist one more time
		if(neighbors.get(1) == null){
			posdirs2.remove(Integer.valueOf(1));
		}
		if(neighbors.get(5) == null){
			posdirs2.remove(Integer.valueOf(-1));
		}
		
		// if there is only one action left, return that
		if(posdirs2.size() == 1){
			return posdirs2.get(0);
		}
		
		// otherwise, use the remaining actions to approach the desired exit
		// if the exit is farther away horizontally (down the road) than it is vertically (in lanes) prioritize going forward
		if(goal.x - location.x > goal.y - location.y || location.x > goal.x){
			// but only take an action if it is an option
			if(posdirs2.contains(0)){
				return 0;
			} else if(goal.y == 4){
				// if the goal is on the left return 1
				return 1;
			}
			// otherwise return -1
			return -1;
		} else {
			// otherwise prioritize changing lanes
			if(goal.y == 4 && posdirs2.contains(1)){
				return 1;
			} else if(goal.y == 1 && posdirs2.contains(-1)){
				return -1;
			}
			// and if that fails, just go forward (unless that would make the car pass its exit)
			if(goal.y == location.y){
				if(posdirs2.contains(1)){
					return 1;
				}
				return -1;
			}
			return 0;
		}
		
	}
	
}
