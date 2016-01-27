package neuralnet;

import java.util.ArrayList;

public class Layer {
    ArrayList<Node> Nodes = new ArrayList<>();
    
    //Create a layer with the desired number of nodes and desired
    //number of weights on each node.
    public Layer(int numnodes, int numweights)
    {
        for(int i = 0; i < numnodes; i++)
        {
            Nodes.add(new Node(numweights));
        }
    }
}
