vmArgs=--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED

buildCP="./lib/json-20220924.jar;./lib/jdom-2.0.6.1.jar;./lib/junit-4.13.2.jar"
runCP="./lib/hamcrest-core-1.3.jar;./lib/json-20220924.jar;./lib/jdom-2.0.6.1.jar;./lib/junit-4.13.2.jar;./build/"

compile:
	mkdir -p ./build
	javac -d build -cp $(buildCP) ./src/*.java ./src/Samples/*.java ./test/*.java

receiver:
	java -cp $(runCP) $(vmArgs) Receiver

receiver-x:
	java -cp $(runCP) $(vmArgs) Receiver xml

sender:
	java -cp $(runCP) $(vmArgs) Sender
	
sender-x:
	java -cp $(runCP) $(vmArgs) Sender xml

doTest:
	java -cp $(runCP) org.junit.runner.JUnitCore TestVisualizer TestReflectionHelper TestSerialize

clean:
	rm -r ./build/