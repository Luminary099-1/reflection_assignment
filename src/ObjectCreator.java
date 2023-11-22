// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// ObjectCreator.java
// ======================================================

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import samples.*;


/**
 * Class that provides a user interface to create sample objects for
 * serialization.
 */
public class ObjectCreator {
	// Common reader for the class.
	protected static BufferedReader reader
		= new BufferedReader(new InputStreamReader(System.in));


	/**
	 * Guides the user through a command-line driven object creation process.
	 * One of the class in /src/samples/ (excluding Node.java) will be
	 * instantiated and returned.
	 * 
	 * @return A sample object for serialization.
	 */
	public static Object create() {
		// Prompt the user for a class type to create:
		String classSelection =
			"Available classes: \n"
			+ "\t1: class Simple // A class with primitive instance variables.\n"
			+ "\t2: class Cyclic // A class with reference instance variables and cyclic references.\n"
			+ "\t3: class PrimitiveArray // A class with an array of primitives.\n"
			+ "\t4: class ObjectArray // A class with an array of references.\n"
			+ "\t5: class ArrayListContainer // A class with an instance of a Java collection.\n";

		System.out.print(classSelection);

		int selection = getUserInt(1, 5, false,
			"Please select a class with its listed number (1-5): ");

		// Call a delegate object creator per the user's input:
		switch (selection) {
			case 1:
				return createSimple(false);
			case 2:
				return createCyclic();
			case 3:
				return createArray(true);
			case 4:
				return createArray(false);
			case 5:
				return createCollection();
		}

		// This should be unreachable.
		System.err.println("There was an issue creating an object.");
		return null;
	}


	/**
	 * Guides the user though a command-line driven process to create an
	 * instance of {@code samples.Simple}.
	 * 
	 * @param isField Indicates whether this is being called to create an
	 * instance of {@code samples.Simple} as a field for another object. If
	 * {@code true}, command-line output will receive an additional indent.
	 * @return An instance of {@code samples.Simple} created by the user.
	 */
	protected static Simple createSimple(boolean isField) {
		// Indicate the type being created.
		System.out.format("%sCreating an instance of Simple:\n",
			isField ? "\t" : ""
		);

		// Prompt the user for a value for the 'number' field.
		int number = getUserInt(Integer.MIN_VALUE, Integer.MAX_VALUE, isField,
			String.format("%s\tPlease enter an integer value: ",
			isField ? "\t" : ""
		));

		boolean bool = false;

		while (true) { // Prompt the user for a value for the 'bool' field.
			System.out.printf("%s\tPlease enter a Boolean value: ",
				isField ? "\t" : ""
			);
			String input = "";

			try { // Attempt to read the user input.
				input = reader.readLine().toLowerCase();
			} catch (IOException e) {
				// Assumes any I/O problems cannot be dealt with at runtime.
				System.err.println("Failed to read user input.");
				e.printStackTrace();
				System.exit(1);
			}

			// Validate the input and set the value as appropriate.
			if (input.equals("true")) {
				bool = true;
				break;
			} else if (input.equals("false")) {
				bool = false;
				break;
			}

			// If valid input was not given, inform the user and try again.
			System.out.println("\tInvalid Boolean, please try again.");
			continue;
		}

		return new Simple(number, bool);
	}


	/**
	 * Guides the user though a command-line driven process to create an
	 * instance of {@code samples.Cyclic}.
	 * 
	 * @return An instance of {@code samples.Cyclic} created by the user.
	 */
	protected static Cyclic createCyclic() {
		// A list to store the values that will be stores in cyclic.
		ArrayList<Integer> list = new ArrayList<Integer>();
		// Indicate the type being created.
		System.out.println("Creating an instance of Cyclic:");
		// Prompt the user for the number of Nodes to put in the object.
		int count = getUserInt(1, 10, false,
			"\tPlease enter the number of elements to create (1-10): ");

		// Prompt the user for a value for each node and add it to the list.
		for (int i = 0; i < count; i ++) {
			String prompt
				= String.format("\tPlease enter an integer for index %d: ", i);
			list.add(getUserInt(Integer.MIN_VALUE, Integer.MAX_VALUE, true,
				String.format(prompt)));
		}

		return new Cyclic(list);
	}


	/**
	 * Guides the user though a command-line driven process to create an
	 * instance of {@code samples.PrimitiveArray} or
	 * {@code samples.ObjectArray}.
	 * 
	 * @param isPrimitive If {@code true}, the object created will be an
	 * instance of {@code samples.PrimitiveArray}. Otherwise, the created object
	 * will be an instance of {@code samples.ObjectArray}.
	 * @return An instance of {@code samples.Cyclic} created by the user.
	 */
	protected static Object createArray(boolean isPrimitive) {
		// Indicate the type being created.
		System.out.printf("Creating an instance of %s:\n",
			isPrimitive ? "PrimitiveArray" : "ObjectArray"
		);

		// Ask the user for the desired array size to be stored in the object.
		int count = getUserInt(1, 10, false,
			"\tPlease enter the number of elements to create (1-10): ");

		if (isPrimitive) { // To construct PrimitiveArray:
			// A list to store the integers for the array.
			ArrayList<Integer> list = new ArrayList<Integer>();
			// Prompt the user for each value to store in the list, then add it.
			for (int i = 0; i < count; i ++) {
				String prompt = String.format(
					"\tPlease enter an integer for index %d: ", i);
				list.add(getUserInt(Integer.MIN_VALUE, Integer.MAX_VALUE, false,
					String.format(prompt)));
			}

			return new PrimitiveArray(list);

		} else { // To construct ObjectArray:
			// A list to store the Simple instances for the array.
			ArrayList<Simple> list = new ArrayList<Simple>();
			// Have the user construct each instance of Simple, then store it.
			for (int i = 0; i < count; i ++) {
				System.out.printf(
					"\tPlease create an instance of Simple for index %d:\n", i);
				list.add(createSimple(true));
			}

			return new ObjectArray(list);
		}
	}

	/**
	 * Guides the user though a command-line driven process to create an
	 * instance of {@code samples.ArrayListContainer}.
	 * 
	 * @return An instance of {@code samples.ArrayListContainer} created by the
	 * user.
	 */
	protected static ArrayListContainer createCollection() {
		// A list to store the Simple instances for the array.
		ArrayList<Simple> list = new ArrayList<Simple>();
		// Indicate the type being created.
		System.out.println("Creating an instance of ArrayListContainer:");
		// Ask the user for the desired array size to be stored in the object.
		int count = getUserInt(1, 10, true,
			"\tPlease enter the number of elements to create (1-10): ");

		// Have the user construct each instance of Simple, then store it.
		for (int i = 0; i < count; i ++) {
			System.out.printf(
				"\tPlease create an instance of Simple for index %d:\n", i);
			list.add(createSimple(true));
		}

		return new ArrayListContainer(list);
	}


	/**
	 * Prompts the user for an integer input, reprompting them until the provide
	 * a valid response.
	 * 
	 * @param lowerBound The lower bound (inclusive) of allowable values.
	 * @param upperBound The upper bound (inclusive) of allowable values.
	 * @param isField Indicates whether this is being called to create an
	 * instance as a field for another object. If {@code true}, command-line
	 * output will receive an additional indent.
	 * @param prompt A prompt to be sent to the user via the terminal that
	 * describes the expected input.
	 * @return An integer provided by the user that within the specified bounds.
	 */
	protected static int getUserInt(
	int lowerBound, int upperBound, boolean isField, String prompt) {
		int input = 0;

		while (true) { // Prompt the user until valid input is given.
			System.out.print(prompt); // Deliver the prompt.
			
			try { // Attempt to read the user input.
				input = Integer.valueOf(reader.readLine());
			} catch (IOException e) {
				// Assumes any I/O problems cannot be dealt with at runtime.
				System.err.println("Failed to read user input.");
				e.printStackTrace();
				System.exit(1);
			} catch (NumberFormatException e) {
				// Informs the user they did not provide integer input.
				System.out.printf("%sInvalid integer, please try again.\n",
					isField ? "\t" : ""
				);
				continue;
			}

			// Perform a bounds check and inform the user if they did not pass.
			if (input < lowerBound || input > upperBound) {
				System.out.printf("%sInput out of bounds, please try again.\n",
					isField ? "\t" : ""
				);
				continue;
			}

			return input;
		}

	}
}
