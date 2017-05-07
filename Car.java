import java.util.ArrayList;
import java.util.Arrays;

/*
 * Cars in the simulator.
 * 
 * @author ablonder
 */
public class Car {
	
	// the world the car is in, just so it can access some essential class variables
	final World world;
	// location of the exit the car is trying to reach
	final roadSquare goal;
	// the car's base speed - unchanging
	final int basespeed;
	// the car's current speed
	int speed;
	// the amount the car is willing to deviate from its base speed
	int risktaking;
	// does risktaking increase as the car approaches its exit
	final boolean var_risk;
	// proportion of the time the car can tell if something is in its blind spot (between 0 and 1)
	final float visibility;
	// the car's current location
	roadSquare location;
	String type; // either "DEFAULT", "HUMAN", "AGGRESSIVE", or "AGENET"
	
	/*
	 * Constructor for a new car to initialize class variables
	 * 
	 * @param roadSquare initsq - the roadSqare the car starts at
	 * 
	 * @param roadSquare goalsq - the exit the car is trying to get to
	 * 
	 * @param String type - what profile of car we're dealing with
	 * 
	 * @param int initspeed - the number of ticks it takes for the car to move
	 * one square
	 * 
	 * @param int risk - amoung a car is willing to deviate from their basespeed
	 * (if not overwritten)
	 * 
	 * @param boolen varrisk - does the car's tendency to take risks vary as it
	 * approaches its exit
	 * 
	 * @param float vision - the proportion of the time the car notices
	 * something in its blind spot
	 * 
	 * @param World w - the world that the car is in
	 */
	public Car(roadSquare initsq, roadSquare goalsq, String type, int initspeed, int risk, boolean varrisk,
			float vision, World w) {
		this.type = type;
		world = w;
		goal = goalsq;
		basespeed = initspeed;
		speed = basespeed;
		risktaking = risk;
		var_risk = varrisk;
		visibility = vision;
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
		float ldraw = world.random.nextFloat();
		float rdraw = world.random.nextFloat();
		
		// check the speed of the cars around it and change its own speed accordingly
		speedcheck(neighbors);
		
		// list of possible directions to be removed as directions are noted as occupied
		ArrayList<Integer> posdirs = new ArrayList<Integer>(Arrays.asList(-1, 0, 1));
		
		// check to see if there actually are any neighbors to the left
		if(neighbors.get(2) == null || neighbors.get(2).car != null || neighbors.get(1) == null ||
				neighbors.get(1).car != null){
			posdirs.remove(Integer.valueOf(1));
		}
		// to the right
		if(neighbors.get(4) == null || neighbors.get(4).car != null || neighbors.get(5) == null ||
				neighbors.get(5).car != null){
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
		// check just to the left to see if a car is there and is not signaling
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
		// if they are in the same lane as their goal, prioritize going forward
		if(location.y == goal.y){
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
			} else if(goal.y == 0 && posdirs2.contains(-1)){
				return -1;
			}
			// and if that fails, just go forward
			return 0;
		}
		
	}
	
	/*
	 * Checks the speed of this cars' neighbors and adjusts the car's speed in accordance with its risktakingness
	 * Decrements risktaking if necessary
	 * @param neighbors - a list of the neighboring squares clockwise from the car's back left
	 */
	public void speedcheck(ArrayList<roadSquare> neighbors){
		// if the car's risktaking tendency varies decrease it as the car approaches its exit
		if(var_risk && ((goal.y - location.y)/3 < risktaking) && goal.y > location.y){
			risktaking = (goal.y-location.y)/3;
		}
		
		// sum of the car's neighbors' speeds
		int totalspeed = 0;
		// number of neighboring cars
		int carcount = 0;
		// loop through all the car's neighbors and add all the cars' speeds
		for(roadSquare sq : neighbors){
			if(sq != null && sq.car != null){
				carcount++;
				totalspeed += sq.car.speed;
			}
		}
		// if the car actually has neighbors change its speed accordingly
		if(carcount > 0){
			// calculate the difference between average speed of the car's neighbors and the car's base speed
			int diff = Math.abs(basespeed - totalspeed/carcount);
			// if that's less than the car's risktakingness, just change to that
			if(diff < risktaking){
				speed = totalspeed/carcount;
			} else {
				// otherwise adjust speed by its risktakingness in the right direction
				if(totalspeed/carcount > basespeed){
					speed = basespeed + risktaking;
				}else{
					speed = basespeed - risktaking;
				}
			}
		} else {
			// otherwise just revert to basespeed
			speed = basespeed;
		}
	}
	
}
