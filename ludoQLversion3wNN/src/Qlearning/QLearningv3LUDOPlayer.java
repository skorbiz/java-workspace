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

public class QLearningv3LUDOPlayer implements LUDOPlayer {
	
	public boolean exploring = true;
	public int nGames = 0;
	
	private LUDOBoard board;
		
	private static int nActions = 4;
	private static int nInputs = 15;
	private static int nRewards = 19;
	private static long nStates = (int) Math.pow(2, nInputs*4);

	private static boolean useNegativeRewards = true;
	
	Hashtable<Long, double[]> QTable = new Hashtable<Long, double[]>(1500000 , (float) 0.6);
	Hashtable<Long, double[]> alpha = new Hashtable<Long, double[]>(1500000 , (float) 0.6);
	
	double chromosome[] = new double[nRewards];

	private static boolean useAlternativeChromosome = true;
	private static int normalChromosomeToLoad = 1004;
	private static int nChromosome = 5;
	double chromosome2[] = new double[nChromosome];
	
	double stateBinary[] = new double[nInputs*4];
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: CONSTRUCTOR :::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public QLearningv3LUDOPlayer(LUDOBoard aLUDOBoard)
	{
		board = aLUDOBoard;
		
		for(int i = 0; i < chromosome.length; i++)
			chromosome[i] = Math.random()*2.0-1.0;
		
		for(int i = 0; i < chromosome2.length; i++)
			chromosome2[i] = 1.0;
		
		if(useAlternativeChromosome)
		{
			for(int i = 0; i < chromosome2.length; i++)
				chromosome2[i] = Math.random()*5.0;
			
			loadChromosomeFromDisk(normalChromosomeToLoad);
		}
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: UPDATE QTABLE :::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private long lastState = -1;
	private int lastAction = -1;
	private int[][] lastBoard = new int[4][4];
	double lastStateBinary[] = new double[nInputs*4];

	public void clearTables()
	{
		QTable.clear();
		alpha.clear();
	}
	
	private void updateQTable(long state, int action)
	{	
		if(lastState != -1)
			if(lastAction != -1)
			{
				double reward = calcReward(action);
				alpha.get(lastState)[lastAction]++;
				double QTableMax = getQTableMax(state);
//				QTable.get(lastState)[lastAction] += (0.5 / alpha.get(lastState)[lastAction])*(reward + Math.pow(10.0,-3.0/alpha.get(lastState)[lastAction])*QTableMax-QTable.get(lastState)[lastAction]);
				QTable.get(lastState)[lastAction] += (chromosome2[0]*10 / (alpha.get(lastState)[lastAction]+chromosome2[1]*20))*(reward + Math.pow(chromosome2[2]*10.0,-3.0*chromosome2[3]*2/(chromosome2[4]*20+alpha.get(lastState)[lastAction]))*QTableMax-QTable.get(lastState)[lastAction]);

			}
		
		lastState = state;
		lastAction = action;
	}
	
	public void updateQTableWithCompletion(double reward)
	{
		if(exploring && reward == 3)
		{
			alpha.get(lastState)[lastAction]++;
			double QTableMax = getQTableMax(lastState);
//			QTable.get(lastState)[lastAction] += (0.5 / alpha.get(lastState)[lastAction])*(reward*chromosome[18] + Math.pow(10.0,-3.0/alpha.get(lastState)[lastAction])*QTableMax-QTable.get(lastState)[lastAction]);
			QTable.get(lastState)[lastAction] += (chromosome2[0]*10 / (alpha.get(lastState)[lastAction]+chromosome2[1]*20))*(reward + Math.pow(chromosome2[2]*10.0,-3.0*chromosome2[3]*2/(chromosome2[4]*20+alpha.get(lastState)[lastAction]))*QTableMax-QTable.get(lastState)[lastAction]);

		}
	}
	
	private double getQTableMax(long state)
	{
		double qTableMax = Double.NEGATIVE_INFINITY;
		for(int action = 0; action < nActions; action++)
			if(qTableMax < QTable.get(state)[action])
				qTableMax = QTable.get(state)[action];
		return qTableMax;
	}
	
	private double calcReward(int action)
	{
		int[][] current_board = board.getBoardState();

		double reward = 0;
		
		reward += chromosome[0] * rewardDistanceToGoal(current_board)/20;
		reward += chromosome[1] * rewardBricksCompleted(current_board);

		//reward += rewardIsDone();
		
		int offset = lastAction*nInputs;
    	
    	if(lastStateBinary[0 + offset] == 1) //moveOut
    		reward += chromosome[3];
    	if(lastStateBinary[1 + offset] == 1) //hitOpponentHome
    		reward += chromosome[4];
    	if(lastStateBinary[2 + offset] == 1) //hitMySelfHome
    		reward += chromosome[5];
    	if(lastStateBinary[3 + offset] == 1) //isStar
    		reward += chromosome[6];
    	if(lastStateBinary[4 + offset] == 1) //enemyBehind(old)
    		reward += chromosome[7];
    	if(lastStateBinary[5 + offset] == 1) //enemyBehind(new)
    		reward += chromosome[8];
    	if(lastStateBinary[6 + offset] == 1) //isAlmostHome(old)
    		reward += chromosome[9];
    	if(lastStateBinary[7 + offset] == 1) //isAlmostHome(new)
    		reward += chromosome[10];
    	if(lastStateBinary[8 + offset] == 1) //isOnSafeGlobe(old)
    		reward += chromosome[11];
    	if(lastStateBinary[9 + offset] == 1) //isOnSafeGlobe(new)
    		reward += chromosome[12];
    	if(lastStateBinary[10 + offset] == 1) //isMoreBricks(old)
    		reward += chromosome[13];
    	if(lastStateBinary[11 + offset] == 1) //isMoreBricks(new)
    		reward += chromosome[14];
    	if(lastStateBinary[12 + offset] == 1) //isOnSecondPart
    		reward += chromosome[15];
    	if(lastStateBinary[13 + offset] == 1) //isOnThridPart
    		reward += chromosome[16];
    	if(lastStateBinary[14 + offset] == 1) //isOnFourthPart
    		reward += chromosome[17];

		if(useNegativeRewards == false)
			if(reward < 0)
				reward = 0;
		
    	lastStateBinary = stateBinary;
		lastBoard = current_board;
		return reward;
	}
	
	public double[] getCromosome()
	{
		if(useAlternativeChromosome)
			return chromosome2;

		return chromosome;
	}
	
	public void setChromosome(double[] aChromosome)
	{
		if(useAlternativeChromosome)
			chromosome2 = aChromosome;
		else
			chromosome = aChromosome;
	}
	

			
	boolean lastHitOpponentHome = false;
	private double rewardHitOpponentHome(int action)
	{
		boolean temp = lastHitOpponentHome;
		
		int[][] current_board = board.getBoardState();
		int[][] new_board = board.getNewBoardState(action, board.getMyColor(), board.getDice());
		
		if(hitOpponentHome(current_board, new_board))
			lastHitOpponentHome = true;
		else
			lastHitOpponentHome = false;
		
		if(temp == true)
			return 1.0;
		return 0.0;
	}
	
	private double rewardIsDone()
	{
		if(board.isDone(board.getMyColor()))
			return 1.0;
		return 0.0;
	}
		
	private double rewardBricksCompleted(int[][] current_board)
	{
		double reward = 0;
		int[] score = new int[4];
		
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)			
			{
				if(board.atHome(current_board[i][j], i))
					score[i]++;
			}
			
		reward = score[board.getMyColor()];
		for(int i = 0; i < 4; i++)
			if(board.getMyColor() != i)
				reward -= 1.0/3.0*score[i];
		return reward;
		
	}
	
	private double rewardDistanceToGoal(int[][] current_board)
	{
		double reward = 0;
		int[] score = new int[4];
		
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
			{
				score[i] += distanceToGoal(lastBoard[i][j], i, j);
				score[i] -= distanceToGoal(current_board[i][j], i, j);
			}
		
		reward = score[board.getMyColor()];
		for(int i = 0; i < 4; i++)
			if(board.getMyColor() != i)
				reward -= 1.0/3.0*score[i];
		return reward;
		
	}
	
	private int distanceToGoal(int index, int color, int brick)
	{
		int MAXDISTANCE = 62;//56;

		/// START AREA
		if( index >= 100*color +100)
		if( index <  100*color +104)
			return MAXDISTANCE;

		/// GOAL AREA
		if( index >= 100*color + 104)
		if( index <  100*color + 110)
			return   100*color + 109-index;

		/// NORMAL
		index -= 13*color;
			
		// OVERFLOW
		if(index < 0)
			index = 51 + index;
			
		return MAXDISTANCE - index;
	}
	
	public void printQTable()
	{	
		System.out.println("Number of states: " + nStates);
		System.out.println("Number of visited states: " + QTable.size());
		System.out.println("Unitilisation ratio: " + ( ( (double) QTable.size() ) / ( (double) nStates ) ) );
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: PLAY FUNCTION :::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public void play() 
	{
		board.print("QLearning player playing");
		board.rollDice();

		long state = getState();
		checkStateInQTable(state);
		int action = -1;
		if( exploring )
			action = getRandomMove();
		else
			action = getGreadyMove(state);
		
		if(action != -1)
		{
			if( exploring )
				updateQTable(state, action);
			board.moveBrick(action);
		}
	}
	
	private void checkStateInQTable(long state)
	{
		if( QTable.containsKey(state) )
			return;
		
		alpha.put(state, new double[nActions]);			
		QTable.put(state, new double[nActions]);			
	}
	
	public void resetForNewGame() 
	{
		lastState = -1;
		lastAction = -1;
	}
	
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: CHOOSE MOVE FUNCTIONS :::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	private int getRandomMove()
	{
		ArrayList<Integer> moveable = new ArrayList<Integer>();
		
		for(int i = 0; i < 4; i++)
			if( board.moveable(i) )
				moveable.add(i);
		
		if(moveable.isEmpty())
			return -1;
		
		return moveable.get( (int) (Math.random()*moveable.size()) );
	}

	private int getGreadyMove(long state)
	{
		ArrayList<Integer> moveable = new ArrayList<Integer>();
		
		for(int i = 0; i < 4; i++)
			if( board.moveable(i) )
				moveable.add(i);
		
		if(moveable.isEmpty())
			return -1;
			
		int brick = moveable.get(0);
		double maxQ = QTable.get(state)[moveable.get(0)];
		
		for(int i = 1; i < moveable.size(); i++)
				if( QTable.get(state)[moveable.get(i)] >= maxQ )
				{
					brick = moveable.get(i);
					maxQ = QTable.get(state)[moveable.get(i)];
				}	
		
		if (maxQ == 0)
			return getRandomMove();
		return brick;
	}

	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: STATE FUNCTIONS ::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private int[] stars = { 5, 11, 18, 24, 31, 37, 44, 50 };

	private long getState() 
	{

		for(int i = 0; i < stateBinary.length; i++)
			stateBinary[i] = 0;
		
		for(int i = 0; i < 4; i++)
		{
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(), board.getDice());
			
			int offset = i*nInputs;
		  
        	if (moveOut(current_board, new_board))
        		stateBinary[0 + offset] = 1;
				
        	if (hitOpponentHome(current_board, new_board))
        		stateBinary[1 + offset] = 1;
        	
        	if (hitMySelfHome(current_board, new_board))
        		stateBinary[2 + offset] = 1;
        	
        	if (board.isStar(new_board[board.getMyColor()][i])) 
        		stateBinary[3 + offset] = 1;
    
        	if (enemyBehind(current_board, current_board[board.getMyColor()][i])) 
        		stateBinary[4 + offset] = 1;
        	
        	if (enemyBehind(new_board, new_board[board.getMyColor()][i])) 
        		stateBinary[5 + offset] = 1;
        	
        	if (isAlmostHome(current_board, i)) 
        		stateBinary[6 + offset] = 1;
        	
        	if (isAlmostHome(new_board, i)) 
        		stateBinary[7 + offset] = 1;
        	
        	if (isOnSafeGlobe(current_board, i)) 
        		stateBinary[8 + offset] = 1;
        	
        	if (isOnSafeGlobe(new_board, i)) 
        		stateBinary[9 + offset] = 1;
        	
        	if (isMoreBricks(current_board, i)) 
        		stateBinary[10 + offset] = 1;
        	
        	if (isMoreBricks(new_board, i)) 
        		stateBinary[11 + offset] = 1;
        	        	
        	if (isOnSecondPart(current_board, i)) 
        		stateBinary[12 + offset] = 1;
        	
        	if (isOnThirdPart(current_board, i)) 
        		stateBinary[13 + offset] = 1;
        	
        	if (isOnFourthPart(current_board, i)) 
        		stateBinary[14 + offset] = 1;
		}

		long state = 0;
		for(int i = 0; i < nInputs*4; i++ )
			if(stateBinary[i] == 1)
				state += Math.pow(2, i);
		
		return state;
	}
	
	private long getStateDEPRICATED() 
	{

		long state = 0;
		for(int i = 0; i < 4; i++)
		{
			int[][] current_board = board.getBoardState();
			int[][] new_board = board.getNewBoardState(i, board.getMyColor(), board.getDice());
			
			int offset = i*nInputs;
		  
        	if (moveOut(current_board, new_board))
				state += Math.pow(2, 0 + offset);
				
        	if (hitOpponentHome(current_board, new_board))
				state += Math.pow(2, 1 + offset);
        	
        	if (hitMySelfHome(current_board, new_board))
				state += Math.pow(2, 2 + offset);
        	
        	if (board.isStar(new_board[board.getMyColor()][i])) 
				state += Math.pow(2, 3 + offset);
    
        	if (enemyBehind(current_board, current_board[board.getMyColor()][i])) 
				state += Math.pow(2, 4 + offset);
        	
        	if (enemyBehind(new_board, new_board[board.getMyColor()][i])) 
				state += Math.pow(2, 5 + offset);
        	
        	if (isAlmostHome(current_board, i)) 
				state += Math.pow(2, 6 + offset);
        	
        	if (isAlmostHome(new_board, i)) 
				state += Math.pow(2, 7 + offset);
        	
        	if (isOnSafeGlobe(current_board, i)) 
				state += Math.pow(2, 8 + offset);
        	
        	if (isOnSafeGlobe(new_board, i)) 
				state += Math.pow(2, 9 + offset);
        	
        	if (isMoreBricks(current_board, i)) 
				state += Math.pow(2, 10 + offset);
        	
        	if (isMoreBricks(new_board, i)) 
				state += Math.pow(2, 11 + offset);
        	        	
        	if (isOnSecondPart(current_board, i)) 
				state += Math.pow(2, 12 + offset);
        	
        	if (isOnThirdPart(current_board, i)) 
				state += Math.pow(2, 13 + offset);
        	
        	if (isOnFourthPart(current_board, i)) 
				state += Math.pow(2, 14 + offset); 
			
		}
		
    	/*
    	 * if (isOnSafeStar(current_board, i)) { inputs[10] = 1; } 
    	 * if (isOnSafeStar(new_board, i)) { inputs[11] = 1; }
    	 * if (canMoveHome(new_board, i)) 
    	 * if (isOnFirstPart(current_board, i)) 
    	 */
		
		return state;
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
	public void savePlayerToDisk(int index)
	{
		saveQTableToDisk(index);
		saveAlphaToDisk(index);
		if( ! useAlternativeChromosome)
			saveChromosomeToDisk(index);
		else
			saveChromosomeToDisk2(index);
	}
	
	public void loadPlayerFromDisk(int index)
	{
		loadQTableFromDisk(index);
		loadAlphaFromDisk(index);
		if( ! useAlternativeChromosome)
			loadChromosomeFromDisk(index);
		else
			loadChromosomeFromDisk2(index);
	}
	
	public void saveQTableToDisk(int index)
	{
	    try 
	    {		
	    	File outputFile = new File("data/QTable"+index+".txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

	       	writer.write("nGames:");			    
	    	writer.write(Integer.toString(nGames));
            writer.newLine();

		    Enumeration<Long> states = QTable.keys();
			while( states.hasMoreElements() )
			{
				long state = states.nextElement();
				
	        	 writer.write(Long.toString(state)+",   ");			    
	        	 writer.write(Double.toString(QTable.get(state)[0])+",   ");
	        	 writer.write(Double.toString(QTable.get(state)[1])+",   ");
	        	 writer.write(Double.toString(QTable.get(state)[2])+",   ");
	        	 writer.write(Double.toString(QTable.get(state)[3])+",   ");
	             writer.newLine();
			}
			
			writer.close();
	    } 
	    catch(IOException ex) { ex.printStackTrace(); }
	}
	
	public void loadQTableFromDisk(int index)
	{
		try 
		{
	    	File inputFile = new File("data/QTable"+index+".txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));;
			String line = null;
		    
			if( (line = reader.readLine()) != null )
			{
			    String[] str = line.split(":");
			    nGames = Integer.parseInt(str[1]);
			}
			
			while ( (line = reader.readLine()) != null )
			{
			    String[] str = line.split(",");
			    double[] values = new double[4];
			    values[0] = Double.parseDouble(str[1]);
			    values[1] = Double.parseDouble(str[2]);
			    values[2] = Double.parseDouble(str[3]);
			    values[3] = Double.parseDouble(str[4]);
			    QTable.put( Long.parseLong(str[0]), values );
			}
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void saveAlphaToDisk(int index)
	{
	    try 
	    {		
	    	File outputFile = new File("data/alpha"+index+".txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

		    Enumeration<Long> states = alpha.keys();
			while( states.hasMoreElements() )
			{
				long state = states.nextElement();
				
	        	 writer.write(Long.toString(state)+",   ");			    
	        	 writer.write(Double.toString(alpha.get(state)[0])+",   ");
	        	 writer.write(Double.toString(alpha.get(state)[1])+",   ");
	        	 writer.write(Double.toString(alpha.get(state)[2])+",   ");
	        	 writer.write(Double.toString(alpha.get(state)[3])+",   ");
	             writer.newLine();
			}
			
			writer.close();
	    } 
	    catch(IOException ex) { ex.printStackTrace(); }
	}
	
	public void loadAlphaFromDisk(int index)
	{
		try 
		{
	    	File inputFile = new File("data/alpha"+index+".txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));;
			String line = null;
		    
			while ( (line = reader.readLine()) != null )
			{
			    String[] str = line.split(",");
			    double[] values = new double[4];
			    values[0] = Double.parseDouble(str[1]);
			    values[1] = Double.parseDouble(str[2]);
			    values[2] = Double.parseDouble(str[3]);
			    values[3] = Double.parseDouble(str[4]);
			    alpha.put( Long.parseLong(str[0]), values );
			}
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void saveChromosomeToDisk(int index)
	{
	    try 
	    {		
	    	File outputFile = new File("data/chromosome"+index+".txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

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
		    			
			int i = 0;
			while ( (str = reader.readLine()) != null )
			{
				chromosome[i] = Double.parseDouble(str);
			    i++;
			}
			
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void saveChromosomeToDisk2(int index)
	{
	    try 
	    {		
	    	File outputFile = new File("data/chromosomeAlt"+index+".txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

	        for(int i = 0; i < chromosome2.length; i++)
	        {
	        	 writer.write(Double.toString(chromosome2[i]));
	             writer.newLine();
			}
			
			writer.close();
	    } 
	    catch(IOException ex) { ex.printStackTrace(); }
	}
	
	public void loadChromosomeFromDisk2(int index)
	{
		try 
		{
	    	File inputFile = new File("data/chromosomeAlt"+index+".txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));;
			String str = null;
		    			
			int i = 0;
			while ( (str = reader.readLine()) != null )
			{
				chromosome2[i] = Double.parseDouble(str);
			    i++;
			}
			
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
}
