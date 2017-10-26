package Qlearning;

public class QLv3Main {

	public static void main(String[] args) 
	{
//		new QLv3Evaluator();

//		long timeStart = System.currentTimeMillis();
//		long timeStop = timeStart + 3*60*60*1000;
//		QLv3GeneticAlgorithm ga = new QLv3GeneticAlgorithm();
//		ga.initPopulation();
////		ga.loadPopulationFromDisk();
//		System.out.println("Time left: " + ((timeStop - System.currentTimeMillis())/1000/60) + "min");
//		ga.run(timeStop);
//		ga.reportResult(timeStop);
//		ga.savePopulationToDisk();

//		for(int j = 0; j < 200; j++)
//		{
//		System.out.println("evealuating" + j);
		QLv3LUDOTraining ludo = new QLv3LUDOTraining();
		QLearningv3LUDOPlayer qlPlayer = new QLearningv3LUDOPlayer(ludo.board);

////		qlPlayer.loadPlayerFromDisk(j);
//		qlPlayer.loadChromosomeFromDisk(999);
//		qlPlayer.loadChromosomeFromDisk2(j);

		qlPlayer.loadChromosomeFromDisk(1006);
		qlPlayer.loadChromosomeFromDisk2(1000);
				
		long timeStart = System.currentTimeMillis();
		long timeStop = timeStart + 5*60*1000;
		long timeTemp = timeStart;

		while( timeStop > System.currentTimeMillis() )
		{	
			ludo.play(qlPlayer);
			qlPlayer.nGames++;

			if(qlPlayer.nGames % 100000 == 0)
			{
				System.out.print((System.currentTimeMillis()-timeTemp)/1000 + "         "); 
				timeTemp = System.currentTimeMillis();
				System.out.print("Trained:" + qlPlayer.nGames +  "         ");
				System.out.print("Time left: " + ((timeStop - System.currentTimeMillis())/1000/60) + "min" + "         ");
				System.out.println();
			}
		}
		
		qlPlayer.exploring = false;
		int resultGready = 0;
		for(int i = 0; i < 10000; i++)
			resultGready += ludo.play(qlPlayer);
		
		qlPlayer.printQTable();
//		qlPlayer.savePlayerToDisk(1337);

		System.out.println("Games Used to train: " + qlPlayer.nGames);
		System.out.println("Result for gready: " + resultGready);
//		System.out.println("QLearning : V3");
//		}
	}

}
