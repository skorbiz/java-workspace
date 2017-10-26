// This establises a bidirectional voice communication between two PC'S. 
// Refer to the readme.txt file for details
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
import java.lang.String;


		
public class Threadconn extends JFrame{
	public JTextField enter;
	public JTextArea display;
	
	 public Threadconn()
      {	
         super("Main GUI window... ");
         
        
      	 
        Container c = getContentPane();
		enter = new JTextField();
		enter.setEnabled( false );

		c.add( enter, BorderLayout.NORTH );
		display = new JTextArea() ;
		c.add ( new JScrollPane( display ),
			BorderLayout.CENTER );
			
			setSize( 300, 150);
			show();
	display.setText( "Attempting creation \n");	
	
	        PrintThread thread1, thread2;
		
		
		thread1 = new PrintThread("Port_listener");
	display.append( "Port listener created \n");	
		thread2 = new PrintThread("Mic_listener");
	display.append( "Mic listener created \n");				
		System.err.println("\n Starting threads");
		
		
		
		
		
		
		thread1.start();
	display.append( "Port listener is now running... \n");	
		thread2.start();
	display.append( "and Mic listener is also running!\n");	
		System.err.println("Threads started\n");
		
	}


	
	public static void main( String args[])
	{
		
		 Threadconn app = new Threadconn();
          
          
         app.addWindowListener(
               new WindowAdapter() {
                  public void windowClosing(WindowEvent z)
                  {
                     System.exit(0);
                  }
                         }  );                
		
	}
}

class PrintThread extends Thread {
	private int sleepTime;
//private JTextField enter1;
//private JTextArea display1;
	private String message = "";
	private DataOutputStream output;
   	private DataInputStream input;
   	private static int sendCount=0;
   	private static int recvCount=0;
	private double result;
        private double noiseMoving=0;
	private int noiseIndex=0;
	
	
	private static int serverCount=0;
	private ByteArrayOutputStream out  = new ByteArrayOutputStream();
	private static AudioFormat captureFormat;
	private static AudioInputStream audioData;
    	private static DataInputStream dataInputAudio;
    	private static byte[] waveByteData;
    	private static int AudioBytes;
    	public Mixer JavaMixerTarget;
    	public Mixer JavaMixerSource;
    	
		ServerSocket server;
		Socket Connection;
		int counter =1;
		int speechsize = 9000; // 300 * 35
      		int speechStopSize = speechsize -200-300;
      		int speechArrayIndex = 0;
	
	
	
	
	public PrintThread( String name)
	{
		super( name );
	System.err.println( " Name: " + getName());
//		sleepTime= (int) (Math.random() * 5000);
               
               
		if ( name == "Port_listener")
		{
			message = name;
//		       		sleepTime= (int) (Math.random() * 5000);
//		       System.err.println( " Name: " + getName()+" Alloted sleep time"+ sleepTime);

	Mixer.Info[] mixinfo = AudioSystem.getMixerInfo();
    int numberOfMixers = mixinfo.length;
    
    System.out.println("Available Mixers installed:  " + numberOfMixers);

    for (int i=0; i < numberOfMixers; i++)
        System.out.println("Mixer " + i + " Info: " + mixinfo[i].toString());
     
    //------------------------------------------------------------------
    // Get Java Sound Mixers (from displayed installation support above)
    //------------------------------------------------------------------
       Mixer JavaMixerSource = AudioSystem.getMixer(mixinfo[0]); //provides SourceDataLine
  //     Mixer JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
    //		JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
       //-----------------------------------------------------
       // AVAILABLE SOURCE AND TARGET LINES (AUDIO INPUT/OUTPUT)
       //-----------------------------------------------------
//       Line.Info[] targetport = JavaMixerTarget.getTargetLineInfo();
       Line.Info[] sourceport = JavaMixerSource.getSourceLineInfo();

	System.err.println( " Name: " + getName());
		       }//end (if getName == "PortListener")
		       else if (name == "Mic_listener") // this is the Mic listener... 
		       {
		       	
		       	message = name;


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
    	 Mixer	JavaMixerTarget = AudioSystem.getMixer(mixinfo[1]); //provides TargetDataLine
       //-----------------------------------------------------
       // AVAILABLE SOURCE AND TARGET LINES (AUDIO INPUT/OUTPUT)
       //-----------------------------------------------------
       Line.Info[] targetport = JavaMixerTarget.getTargetLineInfo();
   //      Line.Info[] sourceport = JavaMixerSource.getSourceLineInfo();		


	System.err.println( " Name: " + getName());
	        }// end mic Listener
		        
		
		
	}//end  printThread
	
	public void run()
	{
		if (message == "Port_listener")
			{
// This configuration is for the client machine...
// Port listener listens on the socket 6600		
// Mic listener writes to the socket 7700      	
// Following is the configuration for this Server machine
// Port listener listens on the socket 7700		
// Mic listener writes to the socket 6600  	
		try {
			server = new ServerSocket( 7700, 100);
			
//			while ( true ) {
			//	display.setText( "waiting for connection \n");
				Connection = server.accept();
				
				System.out.println( "connection " + counter + "recieved from: " + 
				Connection.getInetAddress().getHostName() );
				output = new DataOutputStream( Connection.getOutputStream() );
				output.flush() ;
				input = new DataInputStream( Connection.getInputStream() );
			//	display.append( "\n Got I/O streams \n");
				
			//	enter.setEnabled( true );
				
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
			catch( EOFException eof) {
			System.out.println( "Client terminated connection" );
		}
		catch ( IOException io) {
			io.printStackTrace();
		}
	}			
	else if (message == "Mic_listener") // This is for the mic listener...
		{
// This configuration is for the client machine...
// Port listener listens on the socket 6600		
// Mic listener writes to the socket 7700      	
// Following is the configuration for this Server machine
// Port listener listens on the socket 7700		
// Mic listener writes to the socket 6600  
	Socket client;
	      while (true)  // This while true loop is to prevent the program from exiting
	      {
	      	
//	      	try {
//		System.err.println( getName() + " going to sleep");
//			Thread.sleep( sleepTime );
//		}
//		catch( InterruptedException Exception ) {
//			System.err.println( Exception.toString() );
//		}
//		System.err.println( getName() + "done sleeping");
	      	
	      	
		try {
			System.err.println( getName() + "Trying to connect to server...");
//			display.setText( "Attempting connection \n");
			//client = new Socket(InetAddress.getLocalHost(),7700);
			client = new Socket(InetAddress.getByName( "128.227.80.54" ), 6600);
			
			System.out.println( "Conencted to: " + client.getInetAddress().getHostName() );
			
			output = new DataOutputStream(
			client.getOutputStream() );
			output.flush();
//			input = new DataInputStream(
//			client.getInputStream() );
//			display.append( "\n Got I/O streams \n");

			
//			enter.setEnabled( true );
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
  	 if (SpeechSum > 20*result)
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
//			display.append( "\n Error writing object ");
		}
		total += numRead;
		}
	}while (speechArrayIndex <speechStopSize);    // 300 *26 -200
           	
 		System.out.println("senddata "+ sendCount++ + " "+ ByteData.length);
//               	display.append( "Sent bytes to Server: "  + total + "\n");          	
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
/*		
			display.append( "Closing connection. \n");
			input.close();
			output.close();
			client.close();
*/			
			
		}
		catch ( EOFException eof ) {
			System.out.println( "Server terminate connection");
		}
		catch ( IOException  e) {
			e.printStackTrace();
		}			
			
			
  	} // This while true loop is to prevent the program from exiting 


			
		}
//	System.err.println( getName() + "done sleeping");
	//	while(true)
	//	{

	//		}
//		try {
//		System.err.println( getName() + " going to sleep");
//			Thread.sleep( sleepTime );
//		}
//		catch( InterruptedException Exception ) {
//			System.err.println( Exception.toString() );
//		}
//		System.err.println( getName() + "done sleeping");
		
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
	
}


// Adios...