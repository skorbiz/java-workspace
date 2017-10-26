package Qlearning;

import java.util.ArrayList;

public class GATraningNeuralNetwork 
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
	
	public double[] getCromosome()
	{
		double chromosome[] = new double[edges.size()];
		for(int i = 0; i < edges.size(); i++)
			chromosome[i] = edges.get(i).weight;
		return chromosome;
	}
	
	public void setChromosome(double aChromosome[])
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
    	double weight = Math.random()*2.0-1.0;
    	
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
	GATraningNeuralNetwork()
	{
		configuration1();
    }
	
	private void configuration1()
	{	
		//ADD NEURONSS
		addNuron( 0,  0);	//BIAS
		setBiasValue(-1);	
		addNuron(-1, -1);	//INPUTS
		addNuron(-2, -1);
		addNuron(-3, -1);
		addNuron(-4, -1);
		addNuron(-5, -1);
		addNuron(-6, -1);
		addNuron(-7, -1);
		addNuron(-8, -1);
		addNuron(-9, -1);
		addNuron(-10, -1);
		addNuron(-11, -1);
		addNuron(-12, -1);
		addNuron(-13, -1);
		addNuron(-14, -1);
		addNuron(-15, -1);
		addNuron( 1, 1);	//Layer 1 (input)
		addNuron( 2, 1);
		addNuron( 3, 1);
		addNuron( 4, 1);
		addNuron( 5, 2);	//Layer 2 (midel)
		addNuron( 6, 2);
		addNuron( 7, 2);
		addNuron( 8, 3);	//Layer 3 (output)
				
		//ADD EDGES
		addEdge( 0, 1);		//Layer 1
		addEdge(-1, 1);
		addEdge(-2, 1);
		addEdge(-3, 1);
		addEdge(-4, 1);
		addEdge(-5, 1);
		addEdge(-6, 1);
		addEdge(-7, 1);
		addEdge(-8, 1);
		addEdge(-9, 1);
		addEdge(-10, 1);
		addEdge(-11, 1);
		addEdge(-12, 1);
		addEdge(-13, 1);
		addEdge(-14, 1);
		addEdge(-15, 1);

		addEdge( 0, 2);		//Layer 1
		addEdge(-1, 2);
		addEdge(-2, 2);
		addEdge(-3, 2);
		addEdge(-4, 2);
		addEdge(-5, 2);
		addEdge(-6, 2);
		addEdge(-7, 2);
		addEdge(-8, 2);
		addEdge(-9, 2);
		addEdge(-10, 2);
		addEdge(-11, 2);
		addEdge(-12, 2);
		addEdge(-13, 2);
		addEdge(-14, 2);
		addEdge(-15, 2);
	
		addEdge( 0, 3);		//Layer 1
		addEdge(-1, 3);
		addEdge(-2, 3);
		addEdge(-3, 3);
		addEdge(-4, 3);
		addEdge(-5, 3);
		addEdge(-6, 3);
		addEdge(-7, 3);
		addEdge(-8, 3);
		addEdge(-9, 3);
		addEdge(-10, 3);
		addEdge(-11, 3);
		addEdge(-12, 3);
		addEdge(-13, 3);
		addEdge(-14, 3);
		addEdge(-15, 3);
		
		addEdge( 0, 4);		//Layer 1
		addEdge(-1, 4);
		addEdge(-2, 4);
		addEdge(-3, 4);
		addEdge(-4, 4);
		addEdge(-5, 4);
		addEdge(-6, 4);
		addEdge(-7, 4);
		addEdge(-8, 4);
		addEdge(-9, 4);
		addEdge(-10, 4);
		addEdge(-11, 4);
		addEdge(-12, 4);
		addEdge(-13, 4);
		addEdge(-14, 4);
		addEdge(-15, 4);

		addEdge( 0, 5);		//Layer 2
		addEdge( 1, 5);		
		addEdge( 2, 5);
		addEdge( 3, 5);
		addEdge( 4, 5);
		
		addEdge( 0, 6);		//Layer 2
		addEdge( 1, 6);		
		addEdge( 2, 6);
		addEdge( 3, 6);
		addEdge( 4, 6);
	
		addEdge( 0, 7);		//Layer 2
		addEdge( 1, 7);		
		addEdge( 2, 7);
		addEdge( 3, 7);
		addEdge( 4, 7);

		addEdge( 0, 8);
		addEdge( 5, 8);		//Layer 3
		addEdge( 6, 8);
		addEdge( 7, 8);
		
		nLevels = 3;
		outputNeuronID = 8;
	}
	

}



