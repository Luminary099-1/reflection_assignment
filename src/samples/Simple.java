// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// ObjectCreator.java
// ======================================================

package samples;


/**
 * A simple class with primitive fields.
 */
public class Simple {
	int number;
	boolean bool;


	// Default constructor for reflection.
	public Simple() {}

	/**
	 * Instantiate the Simple class.
	 * 
	 * @param number A value for the {@code number} field.
	 * @param condition A value for the {@code bool} field.
	 */
	public Simple(int number, boolean bool) {
		this.number = number;
		this.bool = bool;
	}
}
