package Qlearning;

import LUDOSimulator.LUDOBoard;
import LUDOSimulator.RandomLUDOPlayer;
import LUDOSimulator.SemiSmartLUDOPlayer;


public class QLv4LUDOTraining
{
	private static final long serialVersionUID = 1L;

	public static LUDOBoard board = new LUDOBoard();
	
	private GATrainingLUDOPlayer GAPlayer = new GATrainingLUDOPlayer(board);
	
	public QLv4LUDOTraining() 
	{  
		GAPlayer.loadChromosomeFromDisk(10);
	}

	
	public int play(QLearningv4LUDOPlayer qlPlayer) 
	{
		int[] result = new int[4];

		qlPlayer.resetForNewGame();
		board.setPlayer(qlPlayer,LUDOBoard.YELLOW);
		
//		board.setPlayer(GAPlayer,LUDOBoard.RED);
//		board.setPlayer(GAPlayer,LUDOBoard.BLUE);
//		board.setPlayer(GAPlayer,LUDOBoard.GREEN);

		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.RED);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.BLUE);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.GREEN);
		
//		board.setPlayer(new RandomLUDOPlayer(board),LUDOBoard.RED);
//		board.setPlayer(new RandomLUDOPlayer(board),LUDOBoard.BLUE);
//		board.setPlayer(new RandomLUDOPlayer(board),LUDOBoard.GREEN);
		
		try
		{
			board.play();
			board.kill();
			
			result[0]+=board.getPoints()[0];
			result[1]+=board.getPoints()[1];
			result[2]+=board.getPoints()[2];
			result[3]+=board.getPoints()[3];
			
//			qlPlayer.updateQTableWithCompletion(result[0]);
			
			board.reset();
		
		} catch (InterruptedException e) { e.printStackTrace(); }	
		
		return result[0];
	}

}