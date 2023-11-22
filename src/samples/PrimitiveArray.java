// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// PrimitiveArray.java
// ======================================================

package samples;

import java.util.List;


/**
 * A class containing an array of primitives.
 */
public class PrimitiveArray {
	int array[]; // An array of integer primitives.

	
	// Default constructor for reflection.
	public PrimitiveArray() {}


	/**
	 * Instantiate the class, populating the array with the elements passed in
	 * the list.
	 * 
	 * @param list The list of initializer elements.
	 */
	public PrimitiveArray(List<Integer> list) {
		array = new int[list.size()];

		for (int i = 0; i < list.size(); i ++)
			array[i] = list.get(i);
	}
}
