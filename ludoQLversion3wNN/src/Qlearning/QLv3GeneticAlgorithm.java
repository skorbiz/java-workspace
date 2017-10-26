package Qlearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QLv3GeneticAlgorithm 
{
	QLearningv3LUDOPlayer population[] = new QLearningv3LUDOPlayer[200];
	
	double fitness[] = new double[population.length];
	double bestFitness;

	double mutationPercentage = 0.03;
	
	QLv3LUDOTraining ludo = new QLv3LUDOTraining();
	
	int nTraningGames = 2000;
	int nEvaluationGames = 100;
	
	int generationNumber = 0;

	public void run( long timeLimit )
	{
		int c1;
		int c2;
				
		for(int i = 0; i < population.length; i++)
			if(fitness[i] > bestFitness)
				bestFitness = fitness[i];
		
		while( timeLimit > System.currentTimeMillis() )
		{
			QLearningv3LUDOPlayer kidPlayer = new QLearningv3LUDOPlayer(ludo.board);
			double kidChromosome[] = new double[ kidPlayer.getCromosome().length ];
			double fitnessOfKid;
			
			c1 = tournament(2);
			c2 = tournament(2);
			
			crossover(c1, c2, kidChromosome);
			mutate(kidChromosome);
			kidPlayer.setChromosome(kidChromosome);
			
			fitnessOfKid = evaluate(kidPlayer);
			if(fitnessOfKid > bestFitness)
				bestFitness = fitnessOfKid;
			insert(kidPlayer, fitnessOfKid);
			if( generationNumber % 10 == 0)
				System.out.println("# " + generationNumber + " " + bestFitness);
			if( generationNumber % 100 == 0)
				reportResult(timeLimit);
			
			generationNumber++;
		}
		
	}
	
	public void reportResult(long timeStop)
	{
		int best;
		double bestFit;
		
		bestFit = 0;
		best = 0;
		
		for( int i = 0; i < population.length; i++)
			if(fitness[i] > bestFit)
			{
				bestFit = fitness[i];
				best = i;
			}
		System.out.println("# Best Value = " + bestFit + " at " + best);		
		savePopulationToDisk();
		System.out.println("Time left: " + ((timeStop - System.currentTimeMillis())/1000/60) + "min");


	}	
	
	public double evaluate( QLearningv3LUDOPlayer player ) 
	{
		double fitness = 0;
		
		for(int i = 0; i < nTraningGames; i++)
			ludo.play(player);

		player.exploring = false;
		
		for(int i = 0; i < nEvaluationGames; i++)
			fitness += ludo.play(player);

		player.nGames = nEvaluationGames + nTraningGames;
	    player.clearTables();
		return fitness;
	}
	
	public void initPopulation()
	{
		for(int i = 0; i < population.length; i++)
			population[i] = new QLearningv3LUDOPlayer(ludo.board);
			
		System.out.print("Finished init population member: ");
		for(int i = 0; i < population.length; i++)
		{
			fitness[i] = evaluate( population[i] );
			System.out.print(i + "  ,  ");
		}
		System.out.println();
		
		savePopulationToDisk();
	}
	
	public int tournament(int tournamentSize)
	{
		double bestFit = 0;
		int best = 0;
		
		for(int i = 0; i < tournamentSize; i++)
		{
			int randomN = (int) (Math.random()*population.length);
			if(fitness[ randomN ] > bestFit)
			{
				bestFit = fitness[ randomN ];
				best = randomN;
			}
		}
		
		return best;
	}
	
	public void crossover(int c1, int c2, double kid[])
	{	
		int chromosomeLength = population[c1].getCromosome().length;
		int cut = (int) ( 1 + Math.random()*(chromosomeLength-1) );
		
		for(int i = 0; i < cut; i++)
			kid[i] = population[c1].getCromosome()[i];
		
		for(int i = cut; i < chromosomeLength; i++)
			kid[i] = population[c2].getCromosome()[i];
	}
	
	public void mutate( double kid[])
	{
		for(int i = 0; i < kid.length; i++)
			if( Math.random() < mutationPercentage )
				kid[i] = Math.random()*2.0-1.0;				
	}
	
	public void insert(QLearningv3LUDOPlayer kid, double fitnessOfKid)
	{
		int worst;
		double worstFit;
		
		worstFit = fitness[0];
		worst = 0;
		
		for(int i = 0; i < population.length; i++)
			if(worstFit > fitness[i])
			{
				worst = i;
				worstFit = fitness[i];
			}
		
		if( fitnessOfKid > worstFit )
		{
			population[worst] = kid;
			fitness[worst] = fitnessOfKid;
		}
	}
		
	
		
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: READ AND WRITE FUNCTIONS ::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	public void loadPopulationFromDisk()
	{
		for(int i = 0; i < population.length; i++)
		{
			population[i] = new QLearningv3LUDOPlayer(ludo.board);
			population[i].loadPlayerFromDisk(i);
		}
		loadFitnessFromDisk();
	}
	
	public void savePopulationToDisk()
	{
		for(int i = 0; i < population.length; i++)
			population[i].savePlayerToDisk(i);
		saveFitnessToDisk();
	}
	
	public void saveFitnessToDisk()
	{
	    try 
	    {		
	    	File outputFile = new File("data/fitness.txt");
			
		    BufferedWriter writer = null;
	        writer = new BufferedWriter(new FileWriter(outputFile));

	       	writer.write("generationNumber:");			    
	    	writer.write(Integer.toString(generationNumber));
            writer.newLine();
            
	       	writer.write("bestFitness:");			    
	    	writer.write(Double.toString(bestFitness));
            writer.newLine();
	        
	        for(int i = 0; i < fitness.length; i++)
	        {
	        	 writer.write(Double.toString(fitness[i]));
	             writer.newLine();
			}
			
			writer.close();
	    } 
	    catch(IOException ex) { ex.printStackTrace(); }
	}
	
	public void loadFitnessFromDisk()
	{
		try 
		{
	    	File inputFile = new File("data/fitness.txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));;
			String str = null;
			
			if( (str = reader.readLine()) != null )
			{
			    String[] strGenerationNumber = str.split(":");
			    generationNumber = Integer.parseInt(strGenerationNumber[1]);
			}
			
			if( (str = reader.readLine()) != null )
			{
			    String[] strBestFitness = str.split(":");
			    bestFitness = Double.parseDouble(strBestFitness[1]);
			}
			
			int i = 0;
			while ( (str = reader.readLine()) != null )
			{
				fitness[i] = Double.parseDouble(str);
			    i++;
			}
			
		} 
		catch (FileNotFoundException ex) { ex.printStackTrace(); }
		catch (NumberFormatException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
}