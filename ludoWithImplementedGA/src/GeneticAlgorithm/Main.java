package GeneticAlgorithm;

public class Main 
{
	public static void main(String[] args) 
	{
		
		System.out.println("Start program");
						
		int generations = 10000000;
		GeneticAlgorithm ga = new GeneticAlgorithm();
		ga.initPopulation();
		ga.run(generations);
		ga.reportResult();

		System.out.println("End program");
	}
}
