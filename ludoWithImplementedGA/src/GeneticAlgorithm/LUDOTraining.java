package GeneticAlgorithm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import LUDOSimulator.LUDOBoard;
import LUDOSimulator.RandomLUDOPlayer;
import LUDOSimulator.SemiSmartLUDOPlayer;


public class LUDOTraining
{
	private static final long serialVersionUID = 1L;
	
	static LUDOBoard board;
	
	public LUDOTraining() 
	{  
		board = new LUDOBoard();
	}
	
	public LUDOBoard getBoard()
	{
		return board;
	}
		
	public double play(GeneticAlgorithmLUDOPlayer GAplayer) 
	{
		double result = 0;
		
		board.setPlayer(GAplayer ,LUDOBoard.YELLOW);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.RED);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.BLUE);
		board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.GREEN);
		//board.setPlayer(new RandomLUDOPlayer(board),LUDOBoard.GREEN);
		//board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.GREEN);

		try 
		{
			for(int i = 0; i < 20; i++) 
			{
				board.play();
				board.kill();
				
				result += board.getPoints()[0];

				board.reset();
				board.setPlayer(GAplayer ,LUDOBoard.YELLOW);
				board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.RED);
				board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.BLUE);
				board.setPlayer(new SemiSmartLUDOPlayer(board),LUDOBoard.GREEN);
			}
		} catch (InterruptedException e) { e.printStackTrace(); }

		return result;
	}
		
}