package geneticalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {
    
    public ArrayList<Knapsack> sacks = new ArrayList<>();
    public ArrayList<Integer> weights = new ArrayList<>();
    public ArrayList<Integer> values = new ArrayList<>();
    int mutationRate;
    int tourneySize;
    
    public Population(int newRate, int newTourneySize, ArrayList<Integer> newWeights, ArrayList<Integer> newPrices)
    {
        mutationRate = newRate;
        tourneySize = newTourneySize;
        for(int i = 0; i < 100; i++)
        {
            sacks.add(new Knapsack());
        }
        
        for(int i = 0; i < 50; i++)
        {
            weights.add(newWeights.get(i));
            values.add(newPrices.get(i));
        }
        
        for(Knapsack current : sacks)
        {
            current.WeightAndFitness(weights, values);
        }
    }
    
    //Return the most fit member.
    public Knapsack highestFitness()
    {
        Collections.sort(sacks, new FitnessSorter());
        return sacks.get(sacks.size() - 1);
    }
    
    //Return the average fitness of the population.
    public double averageFitness()
    {
        double average = 0;
        for(Knapsack current : sacks)
        {
            average += current.fitness;
        }
        return average / sacks.size();
    }
    
    public void Generation()
    {
        //Spawn 100 children.
        ArrayList<Knapsack> children = new ArrayList<>();
        for(int i = 0; i < 100; i++)
        {
            children.add(makeChild());
        }
        
        //Add the children to the population to compete.
        sacks.addAll(children);
        
        //Create an arraylist to hold the next generation.
        ArrayList<Knapsack> nextGen = new ArrayList<>();
        
        //Sort the current generation by fitness.
        Collections.sort(sacks, new FitnessSorter());
        
        //Save the 10 most fit individuals.
        for(int i = 0; i < 10; i++)
        {
            nextGen.add(sacks.get(sacks.size() - 1));
            sacks.remove(sacks.size() - 1);
        }
        
        //Now we need to get 90 more individuals using proportional fitness.
        for(int i = 0; i < 90; i++)
        {
            //Get the total fitness.
            double totalFitness = 0;
            for(Knapsack current : sacks)
            {
                totalFitness += current.fitness;
            }
            
            //Get the cumulative fitness for each knapsack.
            sacks.get(0).cumFitness = sacks.get(0).fitness / totalFitness;
            for(int j = 1; j < sacks.size(); j++)
            {
                sacks.get(j).cumFitness = (sacks.get(j).fitness / totalFitness) + sacks.get(j - 1).cumFitness;
            }
            
            //Generate random between 0 and 1 and find the right sack to move to the next generation.
            double toStop = Math.random();
            int indexToRemove = 0;
            
            for(int j = 0; j < sacks.size(); j++)
            {
                if(sacks.get(j).cumFitness >= toStop)
                {
                    nextGen.add(sacks.get(j));
                    indexToRemove = j;
                    break;
                }
            }
            sacks.remove(indexToRemove);
        }
        
        //Update the current generation to the next generation.
        sacks.clear();
        sacks.addAll(nextGen);
    }
    
    public Knapsack makeChild()
    {
        //Get two parents.
        ArrayList<Knapsack> potentialParents = new ArrayList<>();
        Random myRand = new Random();
        Knapsack parent1;
        Knapsack parent2;
        Knapsack child;
        
        for(int i = 0; i < tourneySize; i++)
        {
            potentialParents.add(sacks.get(myRand.nextInt(100)));
        }
        
        Collections.sort(potentialParents, new FitnessSorter());
        parent1 = potentialParents.get(potentialParents.size() - 1);
        
        potentialParents.clear();
        
        for(int i = 0; i < tourneySize; i++)
        {
            potentialParents.add(sacks.get(myRand.nextInt(100)));
        }
        
        Collections.sort(potentialParents, new FitnessSorter());
        parent2 = potentialParents.get(potentialParents.size() - 1);
        
        //Generate a new child from the chosen parents.
        child = new Knapsack(parent1, parent2);
        
        //Mutate the child.
        child.Mutate(mutationRate);
        
        //Calculate the child's weight and fitness.
        child.WeightAndFitness(weights, values);
        
        return child;
    }
    
}
