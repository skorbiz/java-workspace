package GeneticAlgorithm;
import java.util.ArrayList;

public class NeuralNetwork 
{	
	int nLevels;
	int outputNeuronID;
	
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		
	public void setBiasValue(double value)
	{
	    neurons.get( getNeuronArrayPos( 0 ) ).value = value;
	}
	
	public double evaluateNetwork(double input[])
	{
		setInputs(input);
		forwardPropergate();
		return neurons.get( getNeuronArrayPos(outputNeuronID) ).value;
	}
	
	double[] getCromosome()
	{
		double chromosome[] = new double[edges.size()];
		for(int i = 0; i < edges.size(); i++)
			chromosome[i] = edges.get(i).weight;
		return chromosome;
	}
	
	void setChromosome(double aChromosome[])
	{
		if(aChromosome.length != edges.size())
			System.out.println("Err in length of chromosome");
		
		for(int i = 0; i < edges.size(); i++)
			edges.get(i).weight = aChromosome[i];
	}
	
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: INTERNAL FUNCTIONS ::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private void forwardPropergate()
	{
		for(int level = 1; level <= nLevels; level++)
		{
			for(int i = 0; i < edges.size(); i++)
				if(edges.get(i).w.level == level)
					edges.get(i).w.sumOfInputsTimesWaights += edges.get(i).weight * edges.get(i).v.value;

			
			for(int i = 0; i < neurons.size(); i++)
				if(neurons.get(i).level == level)
				{
					neurons.get(i).value = sigmoid(neurons.get(i).sumOfInputsTimesWaights);
					neurons.get(i).sumOfInputsTimesWaights = 0;
				}
		}
	}
	
	private void setInputs(double input[])
	{
		int inputPointer = 0;
		for(int i = 0; i < neurons.size(); i++)
			if(neurons.get(i).level == -1)
				neurons.get(i).value = input[inputPointer++];
	}

    
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: CONSTRUCTION FUNCTIONS ::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public void addNuron( int theTopologicalValue, int level )
    {
    	for(int i = 0; i < neurons.size(); i++)
    		if( neurons.get(i).topologicalValue == theTopologicalValue )
    			return;
 
    	neurons.add( new Neuron(theTopologicalValue, level) );    	
    }
    
    public void addEdge(int sourceID, int destID)
    {
    	Neuron v = neurons.get( getNeuronArrayPos( sourceID ) );
    	Neuron w = neurons.get( getNeuronArrayPos( destID ) );
    	
   		edges.add( new Edge(v, w) );
    }
    
    private int getNeuronArrayPos(int ID)
    {
    	for(int i = 0; i < neurons.size(); i++)
    		if( neurons.get(i).topologicalValue == ID )
    			return i;
    	return -1;
    }
    
    private int getNeuronArrayPos(Neuron v)
    {
    	for(int i = 0; i < neurons.size(); i++)
    		if( neurons.get(i) == v )
    			return i;
    	return -1;
    }
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: SUPPORT FUNCTIONS :::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private static double sigmoid(double x) 
	{
		return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: INNER CLASSES :::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private static class Edge
    {
    	Neuron v;
    	Neuron w;
    	double weight = Math.random();
    	
    	Edge(Neuron source, Neuron dest)
    	{
    		v = source;
    		w = dest;
    	}
    }
	
	private static class Neuron
	{
		int level;
		int topologicalValue;
		double value;
		double sumOfInputsTimesWaights = 0;

		Neuron(int atopologicalValue, int aLevel)
		{
			level = aLevel;
			topologicalValue = atopologicalValue; 
		}
	}
	
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::: CONSTRUCTOR :::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	NeuralNetwork()
	{
		int configuration = 2;
        switch (configuration) 
        {
            case 1:  configuration1();
                     break;
            case 2:  configuration2();
                     break;
        }
    }
	
	private void configuration1()
	{	
		//ADD NEURONSS			Struture( 3, 3, 2, 1)
		addNuron( 0,  0);	//BIAS
		setBiasValue(-1);	
		addNuron(-1, -1);	//INPUTS
		addNuron(-2, -1);
		addNuron(-3, -1);
		addNuron(-4, -1);
		addNuron(-5, -1);
		addNuron( 1, 1);	//Layer 1 (input)
		addNuron( 2, 1);
		addNuron( 3, 1);
		addNuron( 4, 2);	//Layer 2 (midel)
		addNuron( 5, 2);
		addNuron( 6, 3);	//Layer 3 (output)
				
		//ADD EDGES
		addEdge( 0, 1);		//Layer 1
		addEdge(-1, 1);
		addEdge(-2, 1);
		addEdge(-3, 1);
		addEdge(-4, 1);
		addEdge(-5, 1);


		addEdge( 0, 2);
		addEdge(-1, 2);
		addEdge(-2, 2);
		addEdge(-3, 2);
		addEdge(-4, 2);
		addEdge(-5, 2);

		addEdge( 0, 3);
		addEdge(-1, 3);
		addEdge(-2, 3);
		addEdge(-3, 3);
		addEdge(-4, 3);
		addEdge(-5, 3);

		addEdge( 0, 4);		//Layer 2
		addEdge( 1, 4);		
		addEdge( 2, 4);
		addEdge( 3, 4);

		addEdge( 0, 5);
		addEdge( 1, 5);		
		addEdge( 2, 5);
		addEdge( 3, 5);

		addEdge( 0, 6);
		addEdge( 4, 6);		//Layer 3
		addEdge( 5, 6);
		
		nLevels = 3;
		outputNeuronID = 6;
	}
	
	private void configuration2()
	{	
		//ADD NEURONSS			Struture( 3, 3, 2, 1)
		addNuron( 0,  0);	//BIAS
		setBiasValue(-1);	
		addNuron(-1, -1);	//INPUTS
		addNuron(-2, -1);
		addNuron(-3, -1);
		addNuron(-4, -1);
		addNuron(-5, -1);
		addNuron( 1, 1);	//Layer 1 (input)
		addNuron( 2, 1);
		addNuron( 3, 2);	//Layer 3 (output)
				
		//ADD EDGES
		addEdge( 0, 1);		//Layer 1
		addEdge(-1, 1);
		addEdge(-2, 1);
		addEdge(-3, 1);
		addEdge(-4, 1);
		addEdge(-5, 1);

		addEdge( 0, 2);
		addEdge(-1, 2);
		addEdge(-2, 2);
		addEdge(-3, 2);
		addEdge(-4, 2);
		addEdge(-5, 2);

		addEdge( 0, 3);		//Layer 2
		addEdge( 1, 3);		
		addEdge( 2, 3);

		
		nLevels = 2;
		outputNeuronID = 3;
	}
}



