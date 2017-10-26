//Setup a client server for a connection oriented system
//As of now the client will send voice data and the server will play the voice data...
// Currently this has been setup to communicate with the server running on the same PC.
// To make it work across two PC's uncomment line 96 and replace the correct IP address for the machine.

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

public class Clientconn extends JFrame {
	private JTextField enter;
	private JTextArea display;
//	ObjectOutputStream output;
//	ObjectInputStream input;
	String message = "";
	DataOutputStream output;
   	DataInputStream input;
   	public static int sendCount=0;
   	public static int recvCount=0;
	public double result;
        public double noiseMoving=0;
	public int noiseIndex=0;
	
	private static AudioFormat captureFormat;
	private static AudioInputStream audioData;
    	private static DataInputStream dataInputAudio;
    	private static byte[] waveByteData;
    	private static int AudioBytes;
    	private Mixer JavaMixerTarget;
    	
	public Clientconn()
	{
		super (" Client ");
		Container c = getContentPane();
		enter = new JTextField();
		enter.setEnabled( false );
		
/*		enter.addActionListener (
		new ActionListener() {
			public void actionPerformed( ActionEvent e)
			{
				sendData ( e.getActionCommand() );
			}
		}
		);
		*/
		
		c.add( enter, BorderLayout.NORTH );
		display = new JTextArea() ;
		c.add ( new JScrollPane( display ),
			BorderLayout.CENTER );
			
			setSize( 300, 150);
			show();
			
// THIS IS THE CODE FOR CAPTURING THE AUDIO PORT
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
   //    Mixer JavaMixerSource = AudioSystem.getMixer(mixinfo[0]); //provides SourceDataLine
    //   Mixer JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
    		JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
       //-----------------------------------------------------
       // AVAILABLE SOURCE AND TARGET LINES (AUDIO INPUT/OUTPUT)
       //-----------------------------------------------------
       Line.Info[] targetport = JavaMixerTarget.getTargetLineInfo();
   //      Line.Info[] sourceport = JavaMixerSource.getSourceLineInfo();		
	
		}
		
	public void runClient()
	{
		Socket client;
		try {
			display.setText( "Attempting connection \n");
// This line can be used to see if the server and client are working...			
			client = new Socket(InetAddress.getLocalHost(),7700);
// In real life distributed implementaion this can be used to communicate between two PC with their correct IP addresses.			
			//client = new Socket(InetAddress.getByName( "128.227.80.54" ), 7700);
			
			display.append( "Conencted to: " + client.getInetAddress().getHostName() );
			
			output = new DataOutputStream(
			client.getOutputStream() );
			output.flush();
			input = new DataInputStream(
			client.getInputStream() );
			display.append( "\n Got I/O streams \n");

			
			enter.setEnabled( true );
			while ( true) {

          try{
  
           captureFormat = new AudioFormat(8000, 16, 1, true, true);
// TARGET INFO------------------------------------------------------------------------
           TargetDataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, captureFormat); 
           System.out.println("TargetDataLine supported: " + JavaMixerTarget.isLineSupported(targetInfo));
           TargetDataLine targetLine = (TargetDataLine) JavaMixerTarget.getLine(targetInfo);    
           
           	AudioFormat[] targetInfoFormats = targetInfo.getFormats();
           	for (int i=0; i <  targetInfoFormats.length;i++)
                    System.out.println("----> formats: " + targetInfoFormats[i].toString());   
                                                                          

           // SOURCE INFO--------------------------------------------------------------------------
     /*      DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, captureFormat); 
           System.out.println("SourceDataLine supported: " + AudioSystem.isLineSupported(sourceInfo));
           SourceDataLine sourceLine = (SourceDataLine) JavaMixerSource.getLine(sourceInfo);  // interface SourceDataLine           
     */                       	      
           targetLine.open(captureFormat);
      //     sourceLine.open();

      //     System.out.println("Source/Target Open Status: " + sourceLine.isOpen() + " " + targetLine.isOpen());
             System.out.println("Target Open Status: " + targetLine.isOpen());
              
	    
                int numBytesToRead = 512;
                int numRead =0;
                int total = 0;
                int indexa=0;
                byte[] ByteData = new byte[numBytesToRead];
                double	[] NoiseArray = new double[100];
                double NoiseFloat =0;
		double SpeechSum =0;
                
       		int speechsize = 9000; // 300 * 35
      		int speechStopSize = speechsize -200;
      		double SampleRate = 8000;
      		int speechFlag = 0; 
      		int speechArrayIndex = 0;
      		
      		
                targetLine.start();               
                
      // This is to calculate the ambient noise in the room and set a threshold.
                while (indexa<100)
			{
		numRead =  targetLine.read(ByteData, 0, numBytesToRead);
		NoiseFloat = generate(ByteData);
		NoiseArray[indexa]= NoiseFloat;
		indexa++;
		}
	result = squaremy(NoiseArray);
            
              while (true)
              {
              	
              
	 do {
			
  		numRead =  targetLine.read(ByteData, 0, numBytesToRead);
  		SpeechSum= generate(ByteData);
  		
  // Logic to catch the correct frame of speech...
  	 if (SpeechSum > 40*result)
  	 if( speechFlag == 0)
		{
			speechFlag = 1;
			speechArrayIndex= speechArrayIndex + ByteData.length/2;
  		}
  		
  		if ( speechFlag ==1 )
  			{
  			if (speechArrayIndex < speechStopSize) // 0.6 * SampleRate; here it is 300 *26 -200
        			{
  					speechArrayIndex= speechArrayIndex + ByteData.length/2;
  				}
  			else
  			{
  				if (noiseIndex<= 300)
        			{
        				noiseMoving = noiseMoving + SpeechSum/301;   	
       					noiseIndex ++;
       				}
       	     			else if( noiseIndex > 300)
       	     			{
       	     				System.out.println("The previous value of noise was"+result);
       	     				result = noiseMoving;
       	     				System.out.println("The updated noise value is"+noiseMoving);
       	     				noiseIndex=0;
       	     				noiseMoving =0;
       	     	       	     	}
        	
        		}
        	}
        	
  		if (speechFlag ==1)
 		          {  		
  			try {
  		output.write(ByteData,0,ByteData.length);
         	output.flush();
         			}
		catch ( IOException cnfex) {
			display.append( "\n Error writing object ");
		}
		total += numRead;
		}
	}while (speechArrayIndex <speechStopSize);    // 300 *26 -200
           	
 		System.out.println("senddata "+ sendCount++ + " "+ ByteData.length);
               	display.append( "Sent bytes to Server: "  + total + "\n");          	
         speechArrayIndex =0 ;
 	 speechFlag = 0;

 	
 	// Some code to act as a Pause...   
 	   int delay =0;
 	   int delay1 =0;
 	   while (delay < 10000)
 	   {delay ++;
 	      //System.out.println("Seconds are : " + t.getSeconds()); 
 	       while(delay1<1000)
 	         {
 	         	delay1++;
 	         	}
 		}
     // Crap code ends here
        System.out.println("Fresh Capture.. ");
         	
        }
                          	
          // 	targetLine.stop();                                         
          // 	targetLine.close();
        }
       catch (LineUnavailableException e) { System.out.println("line unavailable due 2 an active program");}
       catch (IllegalArgumentException e) { System.out.println("non-integral number of sample frames written");} 
//       catch (IOException e) { System.out.println("dataInputAudio read problem");} 
			}
			
		}
		catch ( EOFException eof ) {
			System.out.println( "Server terminate connection");
		}
		catch ( IOException  e) {
			e.printStackTrace();
		}
	}

public double squaremy( double[] noiseFrame)
   {
   	double sumOfNoise =0;
   	double sumOf =0;
	
	  	
   	for (int indexb=0;indexb< noiseFrame.length; indexb++)
   	{
   		sumOfNoise += noiseFrame[indexb];
   	}
   	
   	sumOf = sumOfNoise/(noiseFrame.length);
   	
   	System.out.println("Sumation of the noise template done");
   	System.out.println("The noise value is" + sumOf);
   	//System.out.println("The noise value is" + noiseFrame[10]);
   	return sumOf;
    }  	
		
	public double generate(byte[] AudioFrame){
	
	int len=0;
	double noiseVar =0;
	double noiseTotal =0;
	double noiseSum =0;
	double   temp = 0; 
	double speechFram =0;
        //double[] speechFram = new double[AudioFrame.length];
	
	  ByteArrayInputStream InByteStream    	= new ByteArrayInputStream(AudioFrame); 
	  DataInputStream dataInputStream 	= new DataInputStream(InByteStream);
	  
	  try {     
	for ( len=0;len<AudioFrame.length/2; len++)
		{
		temp = (double) dataInputStream.readShort();   
               	speechFram = temp* 3.0517578125e-5;   //            /Math.pow(2,15);  
		noiseVar = speechFram * speechFram;
		noiseTotal += noiseVar;
	}
	   }
        catch (IOException e) { System.out.println("readShort problem");e.printStackTrace();}  
	
	noiseSum = noiseTotal/300;
	return noiseSum;
	}		
	
	
	public static void main ( String args[])
	{
		Clientconn app = new Clientconn();
		app.runClient();
	}
}

			
// Adios...			

			
		
		
				
		