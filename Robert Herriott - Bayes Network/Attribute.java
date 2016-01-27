package bayesnetwork;

import java.util.ArrayList;

public class Attribute {
    String Name;
    int Value;
    ArrayList<Attribute> parents = new ArrayList<>();
    
    public Attribute(String newname, int newvalue)
    {
        Name = newname;
        Value = newvalue;
    }
    
    public static ArrayList<Attribute> getOrdering()
    {
        ArrayList<Attribute> ordering = new ArrayList<>();
        ordering.add(new Attribute("Storms", 0));
        ordering.add(new Attribute("Bus", 0));
        ordering.add(new Attribute("Lightning", 0));
        ordering.add(new Attribute("Campfire", 0));
        ordering.add(new Attribute("Thunder", 0));
        return ordering;
    }
}
