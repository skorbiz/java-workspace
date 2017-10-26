package Qlearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import LUDOSimulator.LUDOBoard;
import LUDOSimulator.LUDOPlayer;

public class GATrainingLUDOPlayer implements LUDOPlayer {
	
	private LUDOBoard board;
		
	private static int nInputs = 15;
	
	GATraningNeuralNetwork NN = new GATraningNeuralNetwork();
		
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: CONSTRUCTOR :::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public GATrainingLUDOPlayer(LUDOBoard aLUDOBoard)
	{
		board = aLUDOBoard;
		initChromosome();
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: UPDATE CHROMOSOME :::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		
	public double[] getCromosome()
	{
		return NN.getCromosome();
	}
	
	public void setChromosome(double[] aChromosome)
	{
		NN.setChromosome(aChromosome);
	}
	
	private void initChromosome()
	{
		double newChromosome[] = new double[NN.getCromosome().length];
		
		for(int i = 0; i < newChromosome.length; i++)
			newChromosome[i] = Math.random()*4.0-2.0;
		
		setChromosome(newChromosome);
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: PLAY FUNCTION :::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public void play() 
	{
		board.print("GA player playing");
		board.rollDice();

		int bestAction = -1;
		double bestNNValue = -1;
		
		for(int i = 0; i < 4; i++)
			if(board.moveable(i))
			{
				double state[] = getState(i);
				double temp = NN.evaluateNetwork(state);
				if(temp > bestNNValue)
				{
					bestAction = i;
					bestNNValue = temp;
				}
			}
						
		if(bestAction != -1)
		{
			board.moveBrick(bestAction);
		}
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: STATE FUNCTIONS ::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private int[] stars = { 5, 11, 18, 24, 31, 37, 44, 50 };

	private double[] getState(int brick) 
	{
		double stateBinary[] = new double[nInputs];
	
		int[][] current_board = board.getBoardState();
		int[][] new_board = board.getNewBoardState(brick, board.getMyColor(), board.getDice());
			  
    	if (moveOut(current_board, new_board))
    		stateBinary[0] = 1;
			
    	if (hitOpponentHome(current_board, new_board))
    		stateBinary[1] = 1;
    	
    	if (hitMySelfHome(current_board, new_board))
    		stateBinary[2] = 1;
    	
    	if (board.isStar(new_board[board.getMyColor()][brick])) 
    		stateBinary[3] = 1;

    	if (enemyBehind(current_board, current_board[board.getMyColor()][brick])) 
    		stateBinary[4] = 1;
    	
    	if (enemyBehind(new_board, new_board[board.getMyColor()][brick])) 
    		stateBinary[5] = 1;
    	
    	if (isAlmostHome(current_board, brick)) 
    		stateBinary[6] = 1;
    	
    	if (isAlmostHome(new_board, brick)) 
    		stateBinary[7] = 1;
    	
    	if (isOnSafeGlobe(current_board, brick)) 
    		stateBinary[8] = 1;
    	
    	if (isOnSafeGlobe(new_board, brick)) 
    		stateBinary[9] = 1;
    	
    	if (isMoreBricks(current_board, brick)) 
    		stateBinary[10] = 1;
    	
    	if (isMoreBricks(new_board, brick)) 
    		stateBinary[11] = 1;
    	        	
    	if (isOnSecondPart(current_board, brick)) 
    		stateBinary[12] = 1;
    	
    	if (isOnThirdPart(current_board, brick)) 
    		stateBinary[13] = 1;
    	
    	if (isOnFourthPart(current_board, brick)) 
    		stateBinary[14] = 1;
		
		return stateBinary;
	}
		
	private boolean moveOut(int[][] current_board, int[][] new_board) 
	{
		for(int i=0;i<4;i++) {
			if(board.inStartArea(current_board[board.getMyColor()][i],board.getMyColor())&&!board.inStartArea(new_board[board.getMyColor()][i],board.getMyColor())) {
				return true;
			}
		}
		return false;
	}

	private boolean hitOpponentHome(int[][] current_board, int[][] new_board) {
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				if(board.getMyColor()!=i) {
					if(board.atField(current_board[i][j])&&!board.atField(new_board[i][j])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean hitMySelfHome(int[][] current_board, int[][] new_board) 
	{
		for(int i=0;i<4;i++) {
			if(!board.inStartArea(current_board[board.getMyColor()][i],board.getMyColor())&&board.inStartArea(new_board[board.getMyColor()][i],board.getMyColor())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isOnFirstPart(int[][] current_board, int index) {

		int boardIndex = current_board[board.getMyColor()][index];
		switch (board.getMyColor()) {
		case 0:
			return ((boardIndex <= 11 && boardIndex >= 0) || boardIndex == 51);
		case 1:
			return (boardIndex <= 24 && boardIndex >= 12);
		case 2:
			return (boardIndex <= 37 && boardIndex >= 25);
		case 3:
			return (boardIndex <= 50 && boardIndex >= 38);
		}
		return false;
	}

	private boolean isOnSecondPart(int[][] current_board, int index) {

		int boardIndex = current_board[board.getMyColor()][index];
		switch (board.getMyColor()) {
		case 0:
			return (boardIndex <= 24 && boardIndex >= 12);
		case 1:
			return (boardIndex <= 37 && boardIndex >= 25);
		case 2:
			return (boardIndex <= 50 && boardIndex >= 38);
		case 3:
			return ((boardIndex <= 11 && boardIndex >= 0) || boardIndex == 51);
		}
		return false;
	}

	private boolean isOnThirdPart(int[][] current_board, int index) {

		int boardIndex = current_board[board.getMyColor()][index];
		switch (board.getMyColor()) {
		case 0:
			return (boardIndex <= 37 && boardIndex >= 25);
		case 1:
			return (boardIndex <= 50 && boardIndex >= 38);
		case 2:
			return ((boardIndex <= 11 && boardIndex >= 0) || boardIndex == 51);
		case 3:
			return (boardIndex <= 24 && boardIndex >= 12);
		}
		return false;
	}

	private boolean isOnFourthPart(int[][] current_board, int index) {

		int boardIndex = current_board[board.getMyColor()][index];
		switch (board.getMyColor()) {
		case 0:
			return (boardIndex <= 50 && boardIndex >= 38);
		case 1:
			return ((boardIndex <= 11 && boardIndex >= 0) || boardIndex == 51);
		case 2:
			return (boardIndex <= 24 && boardIndex >= 12);
		case 3:
			return (boardIndex <= 37 && boardIndex >= 25);
		}
		return false;
	}

	private boolean canMoveHome(int[][] new_board, int index) {
		int boardIndex = new_board[board.getMyColor()][index];
		board.atHome(boardIndex, board.getMyColor());
		return false;
	}

	private boolean isAlmostHome(int[][] current_board, int index) {
		int boardIndex = current_board[board.getMyColor()][index];
		return board.almostHome(boardIndex, board.getMyColor());
	}

	private boolean isOnSafeGlobe(int[][] current_board, int index) {
		int boardIndex = current_board[board.getMyColor()][index];
		if (board.isGlobe(boardIndex)) {
			if (boardIndex % 13 == 0 && (boardIndex / 13) != board.getMyColor()) {
				for (int j = 0; j < 4; j++) {
					if (current_board[boardIndex / 13][j] >= 100 + 100 * boardIndex / 13
							&& current_board[boardIndex / 13][j] <= 103 + 100 * boardIndex / 13) {
						return false;
					} else if (j >= 3)
						return true;
				}
			} else
				return true;
		} else
			return false;

		return false;
	}

	private boolean isOnSafeStar(int[][] current_board, int index) {
		int boardIndex = current_board[board.getMyColor()][index];
		int starIndex = 0;
		if (board.isStar(boardIndex)) {
			for (int i = 0; i < stars.length; i++) {
				if (stars[i] == boardIndex) {
					if (i < 1) {
						starIndex = stars[stars.length - 1];
					} else {
						starIndex = stars[i - 1];
					}
				}
			}
			return enemyBehind(current_board, starIndex);
		}
		return false;
	}

	private boolean isMoreBricks(int[][] current_board, int index) {

		int boardIndex = current_board[board.getMyColor()][index];
		for (int i = 0; i < 4; i++) {
			if (i != index) {
				if (current_board[board.getMyColor()][i] == boardIndex)
					return true;
			}
		}
		return false;
	}

	private boolean isSafe(int[][] current_board, int index) {

		boolean almostHome = false, onSafeGlobe = false, onSafeStar = false, farAway = false, moreBricks = false;

		int boardIndex = current_board[board.getMyColor()][index];

		almostHome = board.almostHome(boardIndex, board.getMyColor());

		for (int i = 0; i < 4; i++) {
			if (i != index) {
				if (current_board[board.getMyColor()][i] == boardIndex)
					moreBricks = true;
			}
		}

		if (board.isGlobe(boardIndex)) {
			if (boardIndex % 13 == 0 && (boardIndex / 13) != board.getMyColor()) {
				for (int j = 0; j < 4; j++) {
					if (current_board[boardIndex / 13][j] >= 100 + 100 * boardIndex / 13
							|| current_board[boardIndex / 13][j] <= 103 + 100 * boardIndex / 13) {
						onSafeGlobe = false;
						break;
					} else
						onSafeGlobe = true;
				}
			} else
				onSafeGlobe = true;
		} else
			onSafeGlobe = false;

		int starIndex = 0;
		if (board.isStar(boardIndex)) {
			for (int i = 0; i < stars.length; i++) {
				if (stars[i] == boardIndex) {
					if (i < 1) {
						starIndex = stars[stars.length - 1];
					} else {
						starIndex = stars[i - 1];
					}
				}
			}
			onSafeStar = enemyBehind(current_board, starIndex);
		}

		farAway = !enemyBehind(current_board, boardIndex);

		if (almostHome || onSafeGlobe || onSafeStar || farAway || moreBricks)
			return true;

		return false;

	}

	private boolean enemyBehind(int[][] current_board, int index) {

		int boardIndex = -1;
		do {
			boardIndex++;
		} while (current_board[board.getMyColor()][boardIndex] != index
				&& boardIndex < 3);

		if (isOnSafeGlobe(current_board, boardIndex))
			return false;

		for (int j = 0; j < 4; j++) {
			if (j != board.getMyColor()) {
				for (int i = 0; i < 4; i++) {
					if (current_board[j][i] > 51)
						continue;
					if (current_board[j][i] > 45 && current_board[j][i] < 52
							&& index >= 0) {
						if (index - (current_board[j][i] - 52) <= 6) {
							return true;
						}
					} else if (index - current_board[j][i] > 0) {
						if (index - current_board[j][i] <= 6) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: READ AND WRITE FUNCTIONS ::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	public void saveChromosomeToDisk(int index)
	{
	    try 
	    {		
	    	File outputFile = new File("data/chromosome"+index+".txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

	        double chromosome[] = NN.getCromosome();
	        for(int i = 0; i < chromosome.length; i++)
	        {
	        	 writer.write(Double.toString(chromosome[i]));
	             writer.newLine();
			}
			
			writer.close();
	    } 
	    catch(IOException ex) { ex.printStackTrace(); }
	}
	
	public void loadChromosomeFromDisk(int index)
	{
		try 
		{
	    	File inputFile = new File("data/chromosome"+index+".txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));;
			String str = null;
		    
			double chromosome[] = new double[NN.getCromosome().length];
			
			int i = 0;
			while ( (str = reader.readLine()) != null )
			{
				chromosome[i] = Double.parseDouble(str);
			    i++;
			}
			
			NN.setChromosome(chromosome);
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
}
