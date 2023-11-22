// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Node.java
// ======================================================

package samples;


/**
 * Simple node class for linked lists.
 */
public class Node {
	int value;		// The value stored in this node.
	Node next;	// The next node in the list.


	// Default constructor for reflection.
	public Node() {}


	/**
	 * Instantiate the node with the given value.
	 * 
	 * @param value The value to store in this node.
	 */
	public Node(int value) {
		this.value = value;
	}
}