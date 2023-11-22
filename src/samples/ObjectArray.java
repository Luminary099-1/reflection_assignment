// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// ObjectArray.java
// ======================================================

package samples;

import java.util.List;


/**
 * A class containing an array of references.
 */
public class ObjectArray {
	Simple[] array; // An array of Simple references.


	// Default constructor for reflection.
	public ObjectArray() {}


	/**
	 * Instantiate the class, populating the array with the elements passed in
	 * the list.
	 * 
	 * @param list The list of initializer elements.
	 */
	public ObjectArray(List<Simple> list) {
		array = new Simple[list.size()];

		for (int i = 0; i < list.size(); i ++)
			array[i] = list.get(i);
	}
}
