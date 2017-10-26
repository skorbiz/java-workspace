package Qlearning;

import LUDOSimulator.LUDOBoard;
import LUDOSimulator.RandomLUDOPlayer;
import LUDOSimulator.SemiSmartLUDOPlayer;


public class QLv3LUDOTraining
{
	private static final long serialVersionUID = 1L;

	public static LUDOBoard board = new LUDOBoard();
	
	public QLv3LUDOTraining() 
	{  
		board = new LUDOBoard();
	}
	
	
	public int play(QLearningv3LUDOPlayer qlPlayer) 
	{
		int[] result = new int[4];

		qlPlayer.resetForNewGame();
		board.setPlayer(qlPlayer,LUDOBoard.YELLOW);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.RED);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.BLUE);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.GREEN);	//SemiSmartLUDOPlayer  //RandomLUDOPlayer
		
		try
		{
			board.play();
			board.kill();
			
			result[0]+=board.getPoints()[0];
			result[1]+=board.getPoints()[1];
			result[2]+=board.getPoints()[2];
			result[3]+=board.getPoints()[3];
					
			qlPlayer.updateQTableWithCompletion(result[0]);
			board.reset();
		
		} catch (InterruptedException e) { e.printStackTrace(); }	
		
		return result[0];
	}

}