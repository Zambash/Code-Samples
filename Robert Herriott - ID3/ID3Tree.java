package decisiontreeclassifier;

import java.util.ArrayList;

public class ID3Tree {

    public ID3Node head;
    public ArrayList<Attribute> allAttributes = new ArrayList<>();
    
    //Initialize the list of all attributes.
    public ID3Tree()
    {
        allAttributes.add(new Attribute("Thickness", 0));
        allAttributes.add(new Attribute("Size", 0));
        allAttributes.add(new Attribute("Shape", 0));
        allAttributes.add(new Attribute("Adhesion", 0));
        allAttributes.add(new Attribute("Epithelial", 0));
        allAttributes.add(new Attribute("Nuclei", 0));
        allAttributes.add(new Attribute("Chromatin", 0));
        allAttributes.add(new Attribute("Nucleoli", 0));
        allAttributes.add(new Attribute("Mitoses", 0));
    }
       
    //The main ID3 algorithm.
    public ID3Node ID3(ArrayList<Tumor> data, ArrayList<Attribute> attributes)
    {
        //Create a root node for the tree.
        ID3Node root = new ID3Node(data);
        //If S are all same class, return root with that label.
        if(Homogenous(data))
        {
            root.type = MostCommon(data);
            return root;
        }
        //If attributes is empty, return node with label of most common class.
        else if(attributes.isEmpty())
        {
            root.type = MostCommon(data);
            return root;
        }
        else
        {
            //Find attribute with greatest gain and set that as attribute of root.
            root.attribute = attributes.get(GreatestGain(data, attributes));
            
            //For each value of the chosen attribute.
            for(int i = 0; i <= 10; i++)
            {
                //Determine Sv for that value.
                ArrayList<Tumor> currentSubset = Subset(data, root.attribute.name, i);
                //If Sv is empty.
                if(currentSubset.isEmpty())
                {
                    //Add leaf with label of most common class.
                    root.children.add(new ID3Node(MostCommon(data)));
                }
                else
                {
                    //Add a subtree to this branch.   
                    ArrayList<Attribute> newAttributes = attributes;
                    newAttributes.remove(root.attribute);
                    root.children.add(ID3(currentSubset, newAttributes));
                }
            }
            return root;
        }
    }
    
    //Find the index of an attribute of a tumor based on name.
    public int AttributeIndex(Tumor theTumor, String attributeName)
    {
        for(int i = 0; i < theTumor.attributes.size(); i++)
        {
            if(theTumor.attributes.get(i).name.equals(attributeName))
            {
                return i;
            }
        }
        return 0;
    }
    
    //Traverse the tree to classify a Tumor.
    public void Classify(Tumor toClassify)
    {
        ID3Node currentNode = head;
        
        while(true)
        {
            if(currentNode.children.isEmpty())
            {
                toClassify.classifiedType = currentNode.type;
                return;
            }
            else
            {
                currentNode = currentNode.children.get(toClassify.attributes.get(AttributeIndex(toClassify, currentNode.attribute.name)).value);
            }
        }
    }
    
    //Find if a group of tumors is all one class.
    public boolean Homogenous(ArrayList<Tumor> data)
    {
        int numBenign = 0;
        int numMalignant = 0;
        
        for(Tumor current: data)
        {
            if(current.Type.equals("Benign"))
            {
                numBenign++;
            }
            else
            {
                numMalignant++;
            }
        }
        
        return numBenign == 0 || numMalignant == 0;
    }
    
    //Find the most common class in a group of tumors.
    public String MostCommon(ArrayList<Tumor> data)
    {
        int numBenign = 0;
        int numMalignant = 0;
        
        for(Tumor current: data)
        {
            if(current.Type.equals("Benign"))
            {
                numBenign++;
            }
            else
            {
                numMalignant++;
            }
        }
        
        if(numBenign > numMalignant)
        {
            return "Benign";
        }
        else
        {
            return "Malignant";
        }
    }
    
    //Caluculate the entropy in a set of tumors.
    public double Entropy(ArrayList<Tumor> data)
    {
        //Check if empty set.
        if(data.isEmpty())
        {
            return 0;
        }
        
        //Proportions of the set that are benign and malignant.
        double propBenign = 0;
        double propMalignant = 0;
        
        //Get counts of each type from set.
        for (Tumor currentTumor : data)
        {
            if(currentTumor.Type.equals("Benign"))
            {
                propBenign++;
            }
            else
            {
                propMalignant++;
            }
        }
        
        //Divide by size of set.
        propBenign = propBenign / data.size();
        propMalignant = propMalignant / data.size();
        
        //Get the log values, accounting for zeros.
        double benignLog;
        if(propBenign == 0)
        {
            benignLog = 0;
        }
        else
        {
            benignLog = Math.log(propBenign)/Math.log(2);
        }
        
        double malignantLog;
        if(propMalignant == 0)
        {
            malignantLog = 0;
        }
        else
        {
            malignantLog = Math.log(propMalignant)/Math.log(2);
        }
        
        //Get entropy value using formula for entropy.
        return -propBenign*benignLog -propMalignant*malignantLog;
    }
    
    //Find the information gained by splitting the data by a specified attribute.
    public double InformationGain(ArrayList<Tumor> data, String attributeName)
    {
        //Make a list of lists to hold the split data.
        ArrayList<ArrayList<Tumor>> splitByValue = new ArrayList<>();
        
        //Use the Subset function to populate the list of lists.
        for(int i = 1; i <= 10; i++)
        {
            splitByValue.add(Subset(data, attributeName, i));
        }
        
        //Variable for holding the information gain.
        double infoGain = 0;
        
        //Add up the entropy of each subset weighted by its size.
        for (ArrayList<Tumor> Sv : splitByValue)
        {
            infoGain += ((double)Sv.size() / (double)data.size()) * Entropy(Sv);
        }
        
        //Subtract from the entropy of the super set.
        infoGain = Entropy(data) - infoGain;
        
        //Return the amount of information gain.
        return infoGain;
    }
    
    //Return the index of the attribute with the greatest information gain from
    //a set of attributes.
    public int GreatestGain(ArrayList<Tumor> data, ArrayList<Attribute> attributes)
    {
        double biggestGain = Double.NEGATIVE_INFINITY;
        int attributeIndex = 0;
        
        for(int i = 0; i < attributes.size(); i++)
        {
            Attribute currentAtt = attributes.get(i);
            double currentGain = InformationGain(data, currentAtt.name);
            if(currentGain > biggestGain)
            {
                biggestGain = currentGain;
                attributeIndex = i;
            }
        }
        return attributeIndex;
    }
    
    //Get a subset of tumors from a larger set based on an attribute name and value.
    public ArrayList<Tumor> Subset(ArrayList<Tumor> data, String attributeName, int attributeValue)
    {
        ArrayList<Tumor> theSubset = new ArrayList<>();
        int attributeIndex = 0;
        for(int i = 0; i < data.get(0).attributes.size(); i++)
        {
            if(data.get(0).attributes.get(i).name.equals(attributeName))
            {
                attributeIndex = i;
            }
        }
        
        for(Tumor currentTumor : data)
        {
            if(currentTumor.attributes.get(attributeIndex).value == attributeValue)
            {
                theSubset.add(currentTumor);
            }
        }
        
        return theSubset;
    }
}
