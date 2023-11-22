# Java Reflection and Serialization Assignment (Fall 2022)
Matthew Michaud (matthew.michaud@alumni.ucalgary.ca)

## Overview
This repository contains my submission for an undergraduate Java programming assignment. The objective of the assignment was to create a serialization/deserialization system using Java's reflection. The system consists of a sender that serializes objects and a receiver connected over a network socket that instantiates an object from the received serializations. The purpose of the socket was to separate the serializer from the receiver to prove the process was taking place.

The system supports JSON and XML serialization and can be run on instances of most classes (in theory). The sender class includes a command-line interface to construct objects with user-defined members, enabling the grader to test the system with arbitrary inputs.

I have removed my student ID and the information that identified the course from each source file. I have not modified anything otherwise.

## Building and Running
The following was written for the original submission:

To build the project, invoke `make compile` or just `make`. All build products will be stored in a generated `./build` dirrectory. There are some targets to run the project as well:
- To run the JUnit test suite, invoke `make doTest`.
- The Sender and Receiver programs can be run in JSON mode using `make sender` and `make receiver`, respectively.
- The Sender and Receiver programs can be run in XML mode using `make sender-x` and `make receiver-x`, respectively.

Additionally, the build directory can be removed using `make clean`. All make targets are to be run from the root directory.

The Receiver program must be run first as it will wait for a connection from the Sender. Then, once the user starts the Sender program, they will be prompted with five options for a sample class that will be instantiate, serialize, and sent to the Receiver. The user will be prompted to select a class by its number, then given subsequent prompts for values to create an instance of the class. Depending on the complexity of the class, the user might be prompted to create multiple instances of certain classes, or provide a list of values. Once the user is done creating the object, its serialization will be printed before being sent to the Receiver. The Receiver will print a visualization of the instance once it's deserialized. The Sender will then prompt the user if they wish to create another class, and this process will repeat as long as the user continues to respond yes.
