// ======================================================
// Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)
// Receiver.java
// ======================================================

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Command-line program that prints deserialized objects that were created by
 * the user. To be used in conjunction with Sender.java (a program to create and
 * send the serialized objects).
 */
public class Receiver {
	public static void main(String args[]) throws IOException {
		Serialize serial = null; // Stores the object (de)serializer.
		// Check if the user specified that XML encoding should be used.
		if (args.length > 0 && args[0].toLowerCase().compareTo("xml") == 0)
			serial = new XMLSerialize();
		else
			serial = new JSONSerialize();

		System.out.printf("Receiver Mode: %s\n",
			serial.getClass() == JSONSerialize.class ? "JSON" : "XML"
		);
		
		Socket sok = null; // Socket for the connection to Sender.
		System.out.print("Waiting for connection... ");

		// Create a server socket and wait for a connection from the Sender.
		try {
			ServerSocket srv = new ServerSocket(25678);
			sok = srv.accept();
			srv.close();
		} catch (UnknownHostException e1) {
			System.err.println("Unable to open socket, exiting.");
			System.exit(1);
		}

		System.out.println("Connected!");
		// Get the socket's input stream.
		DataInputStream inStream = new DataInputStream(sok.getInputStream());

		// Continuously deserialize and inspect objects.
		while (true) {
			// Wait for the message length to be sent in.
			while(sok.getInputStream().available() < 4);
			
			// Output the object.
			Object payloadObj = null;
			int payloadSize = inStream.readInt();
			if (payloadSize == 0) // Stop if the sender has closed.
				break;
			System.out.println("New Object Received:");
			String payload 
				= new String(sok.getInputStream().readNBytes(payloadSize));
				
			try {
				payloadObj = serial.deserializeObject(payload);
			} catch (Exception e) {
				System.out.println("Failed to deserialize object!");
				continue;
			}

			try {
				System.out.println(Visualizer.visualize(payloadObj));
			} catch (Exception e) {
				System.err.println("Failed to visualize object!");
			}
		}

		System.out.println("Sender closed. Exiting.");
		sok.close();
	}
}