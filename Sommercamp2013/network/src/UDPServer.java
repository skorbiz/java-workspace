
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author erso   Created on 24-10-2010, 11:04:18
 */
public class UDPServer {

    static private int BUF_SIZE = 1024;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] dataBuf = new byte[BUF_SIZE];
        //byte[] sendData = new byte[BUF_SIZE];
        while (true) {
            DatagramPacket packet = new DatagramPacket(dataBuf, dataBuf.length);
            System.out.println("UDPServer waiting...");
            serverSocket.receive(packet);
            System.out.println("UDPServer got at package from "+packet.getAddress()
                    +" port: "+packet.getPort());
            String sentence = new String(packet.getData());
            System.out.println("Server received: " + sentence);
            String captalized = sentence.toUpperCase();

            packet.setData(captalized.getBytes());
            serverSocket.send(packet);

        }

    }
}
