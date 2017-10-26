//setup a connection oriented server and client socket connection
// Opens port 7700 and plays the data recieved through the speakers.
// Kaustubh Kale '02


import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.text.*; 

public class Serverconn extends JFrame {
	private JTextField enter;
	private JTextArea display;
	DataOutputStream output;
   	DataInputStream input;
	private static int serverCount=0;
	 ByteArrayOutputStream out  = new ByteArrayOutputStream();
	private static AudioFormat captureFormat;
    	private static AudioInputStream audioData;
    	private static DataInputStream dataInputAudio;
    	private static byte[] waveByteData;
    	private static int AudioBytes;
	private Mixer JavaMixerSource;
	
	public Serverconn()
	{
		super( " Server ");
		
		Container c = getContentPane();
		
		enter = new JTextField();
		enter.setEnabled( false);
		c.add( enter, BorderLayout.NORTH );
		display = new JTextArea();
		c.add( new JScrollPane( display ),
		BorderLayout.CENTER);
		setSize( 300, 150);
		show();
// THIS INITIALIZES THE AUDIO PORT FOR OUTPUT
		
	//---------------------------------------			
   // Determine installed mixers
   //---------------------------------------			
     Mixer.Info[] mixinfo = AudioSystem.getMixerInfo();
    int numberOfMixers = mixinfo.length;
    
    System.out.println("Available Mixers installed:  " + numberOfMixers);

    for (int i=0; i < numberOfMixers; i++)
        System.out.println("Mixer " + i + " Info: " + mixinfo[i].toString());
     
    //------------------------------------------------------------------
    // Get Java Sound Mixers (from displayed installation support above)
    //------------------------------------------------------------------
       JavaMixerSource = AudioSystem.getMixer(mixinfo[0]); //provides SourceDataLine
   //    Mixer JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
    
       //-----------------------------------------------------
       // AVAILABLE SOURCE AND TARGET LINES (AUDIO INPUT/OUTPUT)
       //-----------------------------------------------------
   //    Line.Info[] targetport = JavaMixerTarget.getTargetLineInfo();
         Line.Info[] sourceport = JavaMixerSource.getSourceLineInfo();	
				
	}
	
	public void runServer()
	{
		ServerSocket server;
		Socket Connection;
		int counter =1;
		int speechsize = 9000; // 300 * 35
      		int speechStopSize = speechsize -200-300;
      		int speechArrayIndex = 0;
      		
		try {
			server = new ServerSocket( 7700, 100);
			
			while ( true ) {
				display.setText( "waiting for connection \n");
				Connection = server.accept();
				
				display.append( "connection " + counter + "recieved from: " + 
				Connection.getInetAddress().getHostName() );
				output = new DataOutputStream( Connection.getOutputStream() );
				output.flush() ;
				input = new DataInputStream( Connection.getInputStream() );
				display.append( "\n Got I/O streams \n");
				
				enter.setEnabled( true );
				
				while ( true) {

          try{
  
           captureFormat = new AudioFormat(8000, 16, 1, true, true);
                      // SOURCE INFO--------------------------------------------------------------------------
           DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, captureFormat); 
           System.out.println("SourceDataLine supported: " + AudioSystem.isLineSupported(sourceInfo));
           SourceDataLine sourceLine = (SourceDataLine) JavaMixerSource.getLine(sourceInfo);  // interface SourceDataLine           
                            	      
      //     targetLine.open(captureFormat);
           sourceLine.open();

      //     System.out.println("Source/Target Open Status: " + sourceLine.isOpen() + " " + targetLine.isOpen());
      
           System.out.println("Source Open Status: " + sourceLine.isOpen());
	    
                int numBytesToRead = 512;
                int numRead =0;
                int total = 0;
                byte[] ByteData = new byte[numBytesToRead];
                sourceLine.start();                
		
	do{
		input.readFully(ByteData,0,ByteData.length); 
                System.out.println(serverCount++ +" "+ByteData.length);
                out.write(ByteData,0,ByteData.length);
		sourceLine.write(ByteData, 0, ByteData.length);		
		
	
		
	speechArrayIndex += ByteData.length/2; 
	 }
	 while(speechArrayIndex <speechStopSize); 
	 
	System.out.println("Finished writting to a file "); 
		// Write the command word to a file...
           byte[] capturedBytes = out.toByteArray();       
           ByteArrayInputStream CapturedByteArray = new ByteArrayInputStream(capturedBytes);
           AudioInputStream capturedAudioStream = new AudioInputStream(CapturedByteArray,captureFormat,speechArrayIndex);
           AudioSystem.write(capturedAudioStream,AudioFileFormat.Type.WAVE, new File("captured.wav")); 
						
				  }
       catch (LineUnavailableException e) { System.out.println("line unavailable due 2 an active program");}
       catch (IllegalArgumentException e) { System.out.println("non-integral number of sample frames written");} 
	catch (IOException e) { 
            //System.out.println("dataInputAudio read problem");}       
               JOptionPane.showMessageDialog(null, e.toString(),
                  "dataInputAudio read problem",
                  JOptionPane.ERROR_MESSAGE);}	
		
	  speechArrayIndex =0 ;
 	   
 	   out.reset();
		System.out.println("Reset the  file "); 	
		}
			
				//close connection
/*				display.append( "\n User terminated connection ");
				enter.setEnabled( false);
				output.close();
				input.close();
				Connection.close();
				++counter;
				*/
				
			}
		}
		catch( EOFException eof) {
			System.out.println( "Client terminated connection" );
		}
		catch ( IOException io) {
			io.printStackTrace();
		}
	}
/*	
	private void sendData ( String s)
	{
		try {
			output.writeObject ( "SERVER>>> " + s);
			output.flush();
			display.append( "\nSERVER>>> " + s);
		}
		catch( IOException cnfex ) {
			display.append(
			"\n Error writing object" );
		}
	}
*/	
	public static void main( String args[])
	{
		Serverconn app= new Serverconn();
		app.runServer();
		
	}
}

// Adios...
			
						
			
	
