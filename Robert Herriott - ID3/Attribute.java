package decisiontreeclassifier;

//A simple class that I am using to turn attributes into an object, just holds name
//and value.
public class Attribute {
    public String name;
    public int value;
    
    public Attribute(String newName, int newValue)
    {
        name = newName;
        value = newValue;
    }
}
