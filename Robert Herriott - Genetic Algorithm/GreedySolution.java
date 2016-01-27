package geneticalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GreedySolution {
    
    ArrayList<Item> items = new ArrayList<>();
    double SolutionValue = 0;
    double SolutionWeight = 0;
    
    //Make a new greedy solution from a list of values and weights.
    public GreedySolution(ArrayList<Integer> values, ArrayList<Integer> weights)
    {
        //Populate the list of items.
        for(int i = 0; i < values.size(); i++)
        {
            items.add(new Item(values.get(i), weights.get(i)));
        }
        
        //Calculate each item's score.
        for(Item current : items)
        {
            current.CalcScore();
        }
        
        //Sort the items by score.
        Collections.sort(items, new ItemSorter());
        //Grab the best items until you are out of weight.
        for(int i = items.size() - 1; i >= 0; i--)
        {
            if(items.get(i).weight + SolutionWeight <= 200)
            {
                SolutionValue += items.get(i).value;
                SolutionWeight += items.get(i).weight;
            }
        }
    }
    
    //A sortable item with just 3 attributes: value, weight, and score. Score is value / weight.
    public class Item
    {
        double value;
        double weight;
        double score;
        
        void CalcScore()
        {
            score = value / weight;
        }
        
        public Item(double newVal, double newWeight)
        {
            value = newVal;
            weight = newWeight;
        }
    }
    
    public class ItemSorter implements Comparator<Item>
    {
        @Override
        public int compare(Item o1, Item o2) {
            if(o1.score < o2.score)
            {
                return -1;
            }
            if(o1.score == o2.score)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }
}
