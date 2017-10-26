Kaustubh Kale   '02

Real time VoIP:

1. FOR UNIDIRECTIONAL FLOW OF VOICE BETWEEN TWO PC'S

A program to establish a server client socket communication using a dedicated connection oriented 
link between the two.

Run these two in separate windows:

javac -classpath Classes;. Clientconn.java
javac -classpath Classes;. Serverconn.java

Then do the following: "Start the Clientconn after Serverconn has started"

Java -cp Classes;. Serverconn
java -cp Classes;. Clientconn


The client takes input from the microphone and sends it via the internet to the Server which 
plays it back through the speaker...
 
2. THIS IS FOR BIDIRECTIONAL VOICE TRAFFIC BETWEEN TWO PC'S

In this we create two threads each for the server[PC] and the client[laptop] machine on the respective machines. 
The client machine initiates a socket connection with the server on virtual port no. 7700 for sending data
and it's server listens on port 6600 for incoming data from PC.
Here the teminology is such that, a server thread plays voice on the speaker and the client thread reads
from the mic on respective machines.
Also the client thread must be configured to talk to the correct IP like shown below:
client = new Socket(InetAddress.getByName( "128.227.80.54" ), 7700);

// Run these commands on the two machines and it will automatically establish a direct connection between
the two
javac -classpath Classes;. Threadconn.java
java -cp Classes;. Threadtconn