package decisiontreeclassifier;

import java.util.ArrayList;

public class Tumor {
    ArrayList<Attribute> attributes = new ArrayList<>();
    String Type;
    String classifiedType;
    
    //Use an array of passed data to make a new tumor and populate its attributes list.
    public Tumor(String[] newAttributes)
    {    
        attributes.add(new Attribute("Thickness", Integer.parseInt(newAttributes[0])));
        attributes.add(new Attribute("Size", Integer.parseInt(newAttributes[1])));
        attributes.add(new Attribute("Shape", Integer.parseInt(newAttributes[2])));
        attributes.add(new Attribute("Adhesion", Integer.parseInt(newAttributes[3])));
        attributes.add(new Attribute("Epithelial", Integer.parseInt(newAttributes[4])));
        attributes.add(new Attribute("Nuclei", Integer.parseInt(newAttributes[5])));
        attributes.add(new Attribute("Chromatin", Integer.parseInt(newAttributes[6])));
        attributes.add(new Attribute("Nucleoli", Integer.parseInt(newAttributes[7])));
        attributes.add(new Attribute("Mitoses", Integer.parseInt(newAttributes[8])));
        
        if(Integer.parseInt(newAttributes[9]) == 0)
        {
            Type = "Benign";
        }
        else
        {
            Type = "Malignant";
        }
    }
}
