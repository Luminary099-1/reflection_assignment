// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Cyclic.java
// ======================================================

package samples;

import java.util.List;


/**
 * A class whose fields reference other objects with a cyclic relationship. The
 * stored structure is a cyclic singly linked list.
 */
public class Cyclic {
	// The "first" node in the cyclic list.
	Node head;

	// Default constructor for reflection.
	public Cyclic() {}

	/**
	 * Constructs a list with the elements of the passed {@code List} type.
	 * 
	 * @param list The list structure whose elements will be added to this list
	 * in order.
	 */
	public Cyclic(List<Integer> list) {
		// Used to store the previous node in sequence to set its next value.
		Node prev = null;

		// Add each value of the passed list:
		for (int i = 0; i < list.size(); i ++) {
			// Create a node for the list.
			Node cur = new Node(list.get(i));
			// Store the first element as the head.
			if (i == 0) head = cur;
			else {
				// Point the last element to the head of the list.
				if (i + 1 == list.size())
					cur.next = head;
				// Point the previous element to the latest new element.
				prev.next = cur; 
			}
			// Shuffle the new element to the previous,
			prev = cur;
		}
	}
	
}
