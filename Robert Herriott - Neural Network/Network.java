package neuralnet;

import java.util.ArrayList;

public class Network {
    ArrayList<Layer> Layers = new ArrayList<>();
    double LearningRate;
    
    //Create a new network.
    public Network(int [] layersizes, double newRate)
    {
        //Make the first layer with nodes having number of weights equal to
        //the number of attributes in the data, in the case of the Iris data,
        //4 attributes.
        Layers.add(new Layer(layersizes[0], 4));
        
        //Go through the rest of the desired layer sizes and create each layer
        //having nodes with number of weights equal to the number of nodes in
        //the previous layer.
        for(int i = 1; i < layersizes.length; i++)
        {
            Layers.add(new Layer(layersizes[i], Layers.get(i - 1).Nodes.size()));
        }
        
        LearningRate = newRate;
    }
    
    //Feed in an Iris and train the network.
    public void FeedIris(Iris toFeed)
    {
        FeedForward(toFeed);
        CalculateDeltas(toFeed);
        UpdateWeights(toFeed);
    }
    
    //Get the guessed class of the last Iris fed through the network.
    public int GetClass()
    {
        Layer OutputLayer = Layers.get(Layers.size() - 1);
        
        //If the first node had the highest output then the class is 1.
        if(OutputLayer.Nodes.get(0).Output >= OutputLayer.Nodes.get(1).Output && OutputLayer.Nodes.get(0).Output >= OutputLayer.Nodes.get(2).Output)
        {
            return 1;
        }
        //If the second node had the highest output then the class is 2.
        else if(OutputLayer.Nodes.get(1).Output >= OutputLayer.Nodes.get(0).Output && OutputLayer.Nodes.get(1).Output >= OutputLayer.Nodes.get(2).Output)
        {
            return 2;
        }
        //If the third node had the highest output then the class is 3.
        else
        {
            return 3;
        }
    }
    
    public void FeedForward(Iris fedIn)
    {
        for(int i = 0; i < Layers.size(); i++)
        {
            Layer currentLayer = Layers.get(i);
            //If you are on the first layer...
            if(i == 0)
            {
                //Go through each node
                for(Node currentNode : currentLayer.Nodes)
                {
                    double output = 0;
                    //Go through each weight and multiply by the iris data
                    //then add to the output.
                    for(int j = 0; j < currentNode.Weights.size(); j++)
                    {
                        double currentWeight = currentNode.Weights.get(j);
                        
                        output += currentWeight * fedIn.Attributes.get(j);
                    }
                    //Add the bias weight.
                    output += currentNode.BiasWeight;
                    
                    //Apply the sigmoid function
                    output = 1 / (1 + Math.pow(Math.E, -output));
                    
                    //Assign the calculated output to the current node.
                    currentNode.Output = output;
                }
            }
            //If you aren't on the first layer...
            else
            {
                //Go through each node
                for(Node currentNode: currentLayer.Nodes)
                {
                    double output = 0;
                    //Go through each weight and multiply by the outputs of
                    //the previous layer then add to the output.
                    for(int j = 0; j < currentNode.Weights.size(); j++)
                    {
                        double currentWeight = currentNode.Weights.get(j);
                        
                        output += currentWeight * Layers.get(i - 1).Nodes.get(j).Output;
                    }
                    //Add the ias weight.
                    output += currentNode.BiasWeight;
                    
                    //Apply the sigmoid function
                    output = 1 / (1 + Math.pow(Math.E, -output));
                    
                    //Assign the calculated output to the current node.
                    currentNode.Output = output;
                }
            }
        }
    }
    
    public void CalculateDeltas(Iris fedIn)
    {
        //Loop backwards through the layers.
        for(int i = Layers.size() - 1; i >= 0; i--)
        {
            Layer currentLayer = Layers.get(i);
            //If you are on the output layer...
            if(i == Layers.size() - 1)
            {
                //For each node in the layer...
                for(int j = 0; j < currentLayer.Nodes.size(); j++) 
                {
                    Node currentNode = currentLayer.Nodes.get(j);
                    //Figure out what the target value is based on what node you
                    //are at.
                    int target;
                    if(fedIn.Type == j + 1)
                    {
                        target = 1;
                    }
                    else
                    {
                        target = 0;
                    }
                    //Delta <- ok(1 - ok)(tk - ok)
                    currentNode.Delta = currentNode.Output * (1 - currentNode.Output) * (target - currentNode.Output);
                }
            }
            //If you aren't on the output layer...
            else
            {
                //For each node in the layer...
                for(int j = 0; j < currentLayer.Nodes.size(); j++)
                {
                    Node currentNode = currentLayer.Nodes.get(j);
                    
                    //Start delta off at oh(1 - oh)
                    double delta = currentNode.Output * (1 - currentNode.Output);
                    double toMultiply = 0;
                    
                    //Loop through nodes in the next layer and multiply the weight
                    //to which this output is connected by the delta of that node
                    //and add that to the delta of this node.
                    for(Node nextLayerNode : Layers.get(i + 1).Nodes)
                    {
                        toMultiply += nextLayerNode.Weights.get(j) * nextLayerNode.Delta;
                    }
                    delta *= toMultiply;
                    
                    //Assign the calculated delta to the current node
                    currentNode.Delta = delta;
                }
            }
        }
    }
    
    public void UpdateWeights(Iris fedIn)
    {
        //Loop through each layer...
        for(int i = 0; i < Layers.size(); i++)
        {
            Layer currentLayer = Layers.get(i);
            
            //If you are on the first layer...
            if(i == 0)
            {
                //Loop through each node in the layer...
                for(Node currentNode : currentLayer.Nodes)
                {
                    //Loop through the weights in node and update each one with n * delta * x(from the iris)
                    for(int j = 0; j < currentNode.Weights.size(); j++)
                    {
                        currentNode.Weights.set(j, currentNode.Weights.get(j) + (LearningRate * currentNode.Delta * fedIn.Attributes.get(j)));
                    }
                    currentNode.BiasWeight += LearningRate * currentNode.Delta;
                }
            }
            //If you aren't on the first layer...
            else
            {
                //Loop through each node in the layer...
                for(Node currentNode : currentLayer.Nodes)
                {
                    //Loop through the weights in node and update each one with n * delta * x(from the previous layer)
                    for(int j = 0; j < currentNode.Weights.size(); j++)
                    {
                        currentNode.Weights.set(j, currentNode.Weights.get(j) + (LearningRate * currentNode.Delta * Layers.get(i - 1).Nodes.get(j).Output));
                    }
                    currentNode.BiasWeight += LearningRate * currentNode.Delta;
                }
            }
        }
    }
}
