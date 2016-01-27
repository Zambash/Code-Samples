
package decisiontreeclassifier;

import java.util.ArrayList;

//Nodes for building the tree, has a list of children, a data set at that node,
//the attribute in question for that node, and whether it's benign or malignant
//dominant at this node.
public class ID3Node {
    public ArrayList<ID3Node> children = new ArrayList<>();
    public ArrayList<Tumor> data = new ArrayList<>();
    public Attribute attribute;
    public String type;
    
    public ID3Node(ArrayList<Tumor> newData)
    {
        data = newData;
    }
    
    public ID3Node(String newType)
    {
        type = newType;
    }
}
