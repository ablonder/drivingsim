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

	/*
	 * Constructor that creates all roadSquares and randomly sets exits
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

}
