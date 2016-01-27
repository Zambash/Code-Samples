package neuralnet;

import java.util.ArrayList;
import java.util.Random;

public class Node {
    ArrayList<Double> Weights = new ArrayList<>();
    Double BiasWeight;
    Double Output;
    Double Delta;
    
    //Create a node with the desired number of weights and
    //the bias weight set to random doubles between -.5 and .5;
    public Node(int numweights)
    {
        Random myRand = new Random();
        for(int i = 0; i < numweights; i++)
        {
            Weights.add(myRand.nextDouble() - .5);
            //Weights.add(.05);
        }
        BiasWeight = myRand.nextDouble() - .5;
       // BiasWeight = .05;
    }
}
