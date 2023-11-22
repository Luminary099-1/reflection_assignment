// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Sender.java
// ======================================================

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Command-line program that enables the user to create simple demo objects to
 * undergo serialization and deserialization. To be used in conjunction with
 * Receiver.java (which must be run before this program).
 */
public class Sender {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Serialize serial = null; // Stores the object (de)serializer.
		// Check if the user specified that XML encoding should be used.
		if (args.length > 0 && args[0].toLowerCase().compareTo("xml") == 0)
			serial = new XMLSerialize();
		else
			serial = new JSONSerialize();

		System.out.printf("Sender Mode: %s\n",
			serial.getClass() == JSONSerialize.class ? "JSON" : "XML"
		);

		// Create a reader for user input.
		BufferedReader reader
			= new BufferedReader(new InputStreamReader(System.in));
		String input = ""; // The user's command-line input.
		Object payloadObj = null; // The user created object to be sent.
		Socket sok = null; // The socket to connect to the Receiver.
		
		// Create the socket and connect to the receiver.
		try { 
			sok = new Socket(InetAddress.getByName(null), 25678);
		} catch (UnknownHostException e1) {
			System.err.println("Unable to open socket, exiting.");
			System.exit(1);
		}

		// Get the socket's output stream.
		DataOutputStream outStream = new DataOutputStream(sok.getOutputStream());

		// Continuously have the user create objects and send them to Receiver.
		while (true) {
			// Get the user to create an object.
			payloadObj = ObjectCreator.create();
			String payload;
			// Serialize the object.
			try {
				payload = serial.serializeObject(payloadObj);
			} catch (Exception e) {
				System.out.println("Failed to serialize object!");
				continue;
			}

			// Preview and send the object.
			System.out.println("\nJSON Serialized Object Preview:");
			System.out.println(payload);
			outStream.writeInt(payload.length());
			sok.getOutputStream().write(payload.getBytes());
			sok.getOutputStream().flush();
			System.out.println("Object sent!");

			boolean finished = false;

			// Ask the user if they wish to make another object.
			while (true) {
				System.out.print(
					"\nWould you like to send another object? (y/n): ");
				input = reader.readLine().toLowerCase();
				
				if (input.compareTo("n") == 0) {
					finished = true;
				} else if ((input.compareTo("y") != 0))
					continue;

				break;
			};

			// End the program the user is done.
			if (finished) break;
		}

		// Close the receiver and the socket.
		outStream.writeInt(0);
		sok.close();
	}
}
