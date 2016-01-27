package geneticalgorithm;

import java.util.Comparator;

public class FitnessSorter implements Comparator<Knapsack>{

    @Override
    public int compare(Knapsack o1, Knapsack o2) {
        if(o1.fitness < o2.fitness)
        {
            return -1;
        }
        else if(o1.fitness == o2.fitness)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
}
