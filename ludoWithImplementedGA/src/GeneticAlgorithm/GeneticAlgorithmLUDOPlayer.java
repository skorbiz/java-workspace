package GeneticAlgorithm;
import java.util.Random;

import LUDOSimulator.LUDOBoard;
import LUDOSimulator.LUDOPlayer;
/**
 * Example of automatic LUDO player
 * @author David Johan Christensen
 * 
 * @version 0.9
 *
 */
public class GeneticAlgorithmLUDOPlayer implements LUDOPlayer {
	
	LUDOBoard board;
	NeuralNetwork neuralNet;

	public GeneticAlgorithmLUDOPlayer (LUDOBoard aBoard, NeuralNetwork aNeuralNet)
	{
		board = aBoard;
		neuralNet = aNeuralNet;
	}
	
	public void play() 
	{
		board.print("GA player playing");
		board.rollDice();
		
		double maxFitness =-1;
		int bestIndex = -1;
		
		for( int i = 0; i < 4; i++)
		{
			double fitness = analyzeBrickSituation(i); 
			if(fitness > maxFitness && fitness > 0) 
			{
				bestIndex = i;
				maxFitness = fitness;
			}
		}
		
		if( bestIndex != -1) 
			board.moveBrick(bestIndex);
	}
	
	public double analyzeBrickSituation( int i ) 
	{
		if( board.moveable(i) ) 
		{
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(), board.getDice());
			
			double neuralNetInputs[] = new double[5];
			
			if(hitOpponentHome(current_board,new_board)) 
				neuralNetInputs[0] = 1;

			if(board.isStar(new_board[board.getMyColor()][i])) 
				neuralNetInputs[1] = 1;
			
			if(moveOut(current_board,new_board)) 
				neuralNetInputs[2] = 1;
			
			if(board.atHome(new_board[board.getMyColor()][i],board.getMyColor())) 
				neuralNetInputs[3] = 1;
			
			if(hitMySelfHome(current_board,new_board))
				neuralNetInputs[4] = 1;
			
			return neuralNet.evaluateNetwork(neuralNetInputs);
		}
		else 
		{
			return 0;
		}
	}


	private boolean moveOut(int[][] current_board, int[][] new_board) 
	{
		for(int i=0;i<4;i++)
			if(board.inStartArea(current_board[board.getMyColor()][i],board.getMyColor())&&!board.inStartArea(new_board[board.getMyColor()][i],board.getMyColor()))
				return true;
		
		return false;
	}

	private boolean hitOpponentHome(int[][] current_board, int[][] new_board) 
	{
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++)
				if(board.getMyColor()!=i)
					if(board.atField(current_board[i][j])&&!board.atField(new_board[i][j]))
						return true;
		
		return false;
	}
	
	private boolean hitMySelfHome(int[][] current_board, int[][] new_board)
	{
		for(int i=0;i<4;i++)
			if(!board.inStartArea(current_board[board.getMyColor()][i],board.getMyColor())&&board.inStartArea(new_board[board.getMyColor()][i],board.getMyColor()))
				return true;

		return false;
	}
}
