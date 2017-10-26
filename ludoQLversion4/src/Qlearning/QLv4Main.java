package Qlearning;

public class QLv4Main {

	public static void main(String[] args) 
	{
		
		QLv4LUDOTraining ludo = new QLv4LUDOTraining();
		QLearningv4LUDOPlayer qlPlayer = new QLearningv4LUDOPlayer(ludo.board);
		
//		qlPlayer.loadPlayerFromDisk(2);
				
		long timeStart = System.currentTimeMillis();
		long timeStop = timeStart + 10*60*1000;
		long timeTemp = timeStart;

		while( timeStop > System.currentTimeMillis() )
		{	
			ludo.play(qlPlayer);
			qlPlayer.nGames++;

			if(qlPlayer.nGames % 10000 == 0)
			{
				System.out.print((System.currentTimeMillis()-timeTemp)/1000 + "         "); 
				timeTemp = System.currentTimeMillis();
				System.out.print("Trained:" + qlPlayer.nGames +  "         ");
				System.out.print("Time left: " + ((timeStop - System.currentTimeMillis())/1000/60) + "min" + "         ");
				System.out.println();
			}
		}
		
		System.out.println("Evaluating");
		qlPlayer.exploring = false;
		int resultGready = 0;
		for(int i = 0; i < 10000; i++)
			resultGready += ludo.play(qlPlayer);
		
		System.out.println("Printing");
		qlPlayer.printQTable();
		qlPlayer.savePlayerToDisk(2);
		
		System.out.println("Games Used to train: " + qlPlayer.nGames);
		System.out.println("Result for gready: " + resultGready);
		System.out.println("QLearning : V4");

		
	}

}
