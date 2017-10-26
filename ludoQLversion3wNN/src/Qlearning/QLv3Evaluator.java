package Qlearning;

public class QLv3Evaluator 
{
	
	QLv3LUDOTraining ludo = new QLv3LUDOTraining();

	public QLv3Evaluator()
	{	
		comparing();
	}

	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: EVALUATION ::::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public void comparing()
	{
		System.out.println("EVALUATER COMPARING");
			
		QLearningv3LUDOPlayer qlPlayer1 = new QLearningv3LUDOPlayer(ludo.board);
		qlPlayer1.loadChromosomeFromDisk(1004);
		qlPlayer1.loadChromosomeFromDisk2(-36);
		trainAndEval2(qlPlayer1);
		printResults(1);
		
		results = new int[11][60];
		
		QLearningv3LUDOPlayer qlPlayer2 = new QLearningv3LUDOPlayer(ludo.board);
		qlPlayer2.loadChromosomeFromDisk(1006);
		qlPlayer2.loadChromosomeFromDisk2(-36);
		trainAndEval2(qlPlayer2);
		printResults(1);
		
		results = new int[11][60];
		
		QLearningv3LUDOPlayer qlPlayer3 = new QLearningv3LUDOPlayer(ludo.board);
		qlPlayer3.loadChromosomeFromDisk(1005);
		qlPlayer3.loadChromosomeFromDisk2(-36);
		trainAndEval2(qlPlayer3);
		printResults(1);
		
		results = new int[11][60];
		
		QLearningv3LUDOPlayer qlPlayer4 = new QLearningv3LUDOPlayer(ludo.board);
		qlPlayer3.loadChromosomeFromDisk(1006);
		qlPlayer3.loadChromosomeFromDisk2(-36);
		trainAndEval2(qlPlayer4);
		printResults(1);
		
		results = new int[11][60];
		
		QLearningv3LUDOPlayer qlPlayer5 = new QLearningv3LUDOPlayer(ludo.board);
		qlPlayer3.loadChromosomeFromDisk(1005);
		qlPlayer3.loadChromosomeFromDisk2(-36);
		trainAndEval2(qlPlayer5);
		printResults(1);
	
	}	
	
	private void trainAndEval2(QLearningv3LUDOPlayer player )
	{
		printGames = 0;

		for(int m = 1; m < 1000000; m*=10)
			for(int i = 0; i < 10; i++)
			{
	
				while( player.nGames < i*m )
				{					
					ludo.play(player);
					player.nGames++;
		
				}
				
				System.out.print("  train " + m + "," + i);
				evaluate2(player);
			}
		System.out.println();
	}
	
	int results[][] = new int[11][60];
	int printGames = 0;
	
	private void evaluate2(QLearningv3LUDOPlayer player)
	{
		player.exploring = false;
		int resultSemiSmart = 0;
		for(int i = 0; i < 10000; i++)
			resultSemiSmart += ludo.play(player);
				
		player.exploring = true;
		
	
		results[0][printGames] += resultSemiSmart;

	
		printGames++;
	
	}
	
	private void printResults(double nAvarge)
	{
		int traningGames[] = {0,1,2,3,4,5,6,7,8,9,9,10,
				20,30,40,50,60,70,80,90,90,100,
				200,300,400,500,600,700,800,900,900,1000,
				2000,3000,4000,5000,6000,7000,8000,9000,9000,10000,
				20000,30000,40000,50000,60000,70000,80000,90000,90000,100000,
				200000,300000,400000,500000,600000,700000,800000,900000};
	
		
		for(int i = 0; i < 1; i++)
		{
			for(int j = 0; j < 60; j++)
			{
				System.out.print("{");
				System.out.print(traningGames[j]);
				System.out.print(",");
				System.out.print(( (double) results[i][j])/nAvarge);
				System.out.print("}");
				System.out.print(",");
			}
			System.out.println();
		}
	}	
	
	

}
