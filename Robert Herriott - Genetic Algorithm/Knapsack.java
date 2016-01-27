package geneticalgorithm;

import java.util.ArrayList;

public class Knapsack{
    
    public ArrayList<Integer> items = new ArrayList<>();
    public double fitness;
    public double weight;
    public double cumFitness;
    
    //Create a new random knapsack.
    public Knapsack()
    {
        double value;
        for(int i = 0; i < 50; i++)
        {
            value = Math.random();
            if(value < 0.5)
            {
                items.add(0);
            }
            else
            {
                items.add(1);
            }
        }
    }
    
    //Create a new knapsack from uniform crossover of 2 parents.
    public Knapsack(Knapsack parent1, Knapsack parent2)
    {
        double value;
        for(int i = 0; i < 50; i++)
        {
            value = Math.random();
            if(value < 0.5)
            {
                items.add(parent1.items.get(i));
            }
            else
            {
                items.add(parent2.items.get(i));
            }
        }
    }
    
    //Calculate the weight and fitness of this knapsack.
    public void WeightAndFitness(ArrayList<Integer> weights, ArrayList<Integer> values)
    {
        fitness = 0;
        weight = 0;
        for(int i = 0; i < 50; i++)
        {
            if(items.get(i) == 1)
            {
                fitness += values.get(i);
                weight += weights.get(i);
            }
        }
        if(weight > 200)
        {
            fitness = fitness / weight;
        }
    }
    
    //Mutate using a geometric distribution.
    public void Mutate(int mutationRate)
    {
        int n = GeoDist(mutationRate / (double)50);
        while(n < 50)
        {
            items.set(n, Math.abs(items.get(n) - 1));
            n += GeoDist(mutationRate / (double)50);
        }
    }
    
    //Get a number from a geometric distribution.
    private int GeoDist(double prob)
    {
        return (int)(Math.ceil(Math.log(Math.random()) / Math.log(1.0 - prob)));
    }   
}
