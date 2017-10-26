
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

    static private int BUF_SIZE = 1024;

    public static void main(String[] args) throws Exception 
    {
	    DatagramSocket clientSocket = new DatagramSocket();
	    InetAddress ipAdr = InetAddress.getByName("localhost");

    	while(true)
    	{
		    Scanner scanner = new Scanner(System.in);
		
		    String sentence = scanner.nextLine();
		    byte[] dataBuf = sentence.getBytes();
		
		    DatagramPacket packet = new DatagramPacket(dataBuf, dataBuf.length, ipAdr, 9876);
		    clientSocket.send(packet);
				
		    clientSocket.receive(packet);
		    String modifiedSentence = new String(packet.getData());
		    System.out.println("FROM SERVER: "+modifiedSentence);
		
    	}

	    //clientSocket.close();

    }
}
