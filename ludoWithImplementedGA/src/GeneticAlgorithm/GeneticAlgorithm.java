package GeneticAlgorithm;
public class GeneticAlgorithm 
{
	NeuralNetwork population[] = new NeuralNetwork[100];
	
	double fitness[] = new double[population.length];
	double bestFitness;

	double mutationPercentage = 0.03;
	

	public double evaluate( NeuralNetwork neuralNet ) 
	{
		LUDOTraining ludo = new LUDOTraining();
		GeneticAlgorithmLUDOPlayer player = new GeneticAlgorithmLUDOPlayer( ludo.getBoard(), neuralNet);
		double fitness = ludo.play(player);
		
	    return fitness;
	}
	
	
	public void initPopulation()
	{
		for(int i = 0; i < population.length; i++)
			population[i] = new NeuralNetwork();
			
		for(int i = 0; i < population.length; i++)
			fitness[i] = evaluate( population[i] );
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
				kid[i] = Math.random();				
	}
	
	public void insert(NeuralNetwork kid, double fitnessOfKid)
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
	
	
	public void run( int gensLimit )
	{
		int c1;
		int c2;
		
		
		NeuralNetwork kidNeuralNetwork = new NeuralNetwork();
		double kidChromosome[] = new double[ kidNeuralNetwork.getCromosome().length ];
		double fitnessOfKid;
		
		for(int i = 0; i < gensLimit; i++)
		{
			c1 = tournament(2);
			c2 = tournament(2);
			
			crossover(c1, c2, kidChromosome);
			mutate(kidChromosome);
			kidNeuralNetwork.setChromosome(kidChromosome);
			
			fitnessOfKid = evaluate(kidNeuralNetwork);
			if(fitnessOfKid > bestFitness)
				bestFitness = fitnessOfKid;
			insert(kidNeuralNetwork, fitnessOfKid);
			if( i % 10 == 0)
				System.out.println("# " + i + " " + bestFitness);
			if( i % 100 == 0)
				reportResult();

		}
		
	}
	
	public void reportResult()
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
		
		double chromosome[] = population[best].getCromosome();
		for( int i = 0; i < chromosome.length; i++)
			System.out.println(i + " " + chromosome[i]);
		System.out.println("# Best Value = " + bestFit + " at " + best);
	}
	  	
	// SIGNIFICANT VALUES
}