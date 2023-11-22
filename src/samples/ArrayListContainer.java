// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// ArrayListContainer.java
// ======================================================

package samples;

import java.util.ArrayList;


/**
 * A class containing a Java Collections Framework instance populated with
 * object references.
 */
public class ArrayListContainer {
	// A Java Collections Framework class containing references.
	ArrayList<Simple> list;


	// Default constructor for reflection.
	public ArrayListContainer() {}


	/**
	 * Instantiate the class, setting the internal list to refer to the list
	 * passed.
	 * 
	 * @param list The initializer list of {@code Simple} elements.
	 */
	public ArrayListContainer(ArrayList<Simple> list) {
		this.list = list;
	}
}
