
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

public class AudioLoopServer extends Thread
{
	private static boolean	DEBUG = true;

	// DEFINED VALUES //////////////////////////////////////////
	private int 	nBufferSize;
	private float 	fFrameRate;
	
	// GLOBAL VARIABLES /////////////////////////////////////////
	private SourceDataLine	m_sourceLine;
	
	/****************** CONSTRUCTER *****************************************************************************/
	/************************************************************************************************************/
	
	public AudioLoopServer(int aNBufferSize, float aFFrameRate) throws LineUnavailableException
	{
		nBufferSize = aNBufferSize;
		fFrameRate = aFFrameRate;
		
		AudioFormat	audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, fFrameRate, 16, 2, 4, fFrameRate, false);		
		
		DataLine.Info	sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat, nBufferSize);
		m_sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
		m_sourceLine.open(audioFormat, nBufferSize);
	}
	
	/****************** RUN / START METHODE *********************************************************************/
	/************************************************************************************************************/

	public void start()
	{
		m_sourceLine.start();
		super.start();
	}
	
	public void run()
	{	
		while(true)
		{
	        DatagramSocket serverSocket;
			try 
			{
				System.out.println("test");
				serverSocket = new DatagramSocket(8243);
			    byte[] dataBuf = new byte[nBufferSize];
			    DatagramPacket packet = new DatagramPacket(dataBuf, dataBuf.length);
			    serverSocket.receive(packet);
			    
			    m_sourceLine.write(packet.getData(), 0, nBufferSize);
			    serverSocket.close();
			} catch (SocketException e) { e.printStackTrace(); break; 
			} catch (IOException e) { e.printStackTrace(); break;
			}
		}
	}


}