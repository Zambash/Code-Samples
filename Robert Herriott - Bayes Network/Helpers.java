package bayesnetwork;

import java.util.ArrayList;

public class Helpers {
    
    //The simple logfact function.
    public static double logfact(int n)
    {
        double sum = 0;
        for(int i = 1; i <= n; i++)
        {
            sum += Math.log(i);
        }
        return sum;
    }
    
    //Gets the number of days with a certain class.
    public static int getCount(ArrayList<Day> days, int Class)
    {
        int count = 0;
        for(Day current : days)
        {
            if(current.type == Class)
            {
                count++;
            }
        }
        return count;
    }
    
    //Gets the predecessors of a node within a list of nodes.
    public static ArrayList<Attribute> pred(Attribute current, ArrayList<Attribute> all)
    {
        ArrayList<Attribute> preds = new ArrayList<>();
        
        for(int i = all.indexOf(current) + 1; i < all.size(); i++)
        {
            preds.add(all.get(i));
        }
        
        return preds;
    }
    
    //Adds 2 sets of attributes.
    public static ArrayList<Attribute> addAttributes(ArrayList<Attribute> setOne, Attribute toAdd)
    {
        ArrayList<Attribute> sum = new ArrayList<>();
        sum.addAll(setOne);
        sum.add(toAdd);
        return sum;
    }
    
    //Subtracts 2 sets of attributes.
    public static ArrayList<Attribute> subAttributes(ArrayList<Attribute> subFrom, ArrayList<Attribute> sub)
    {
        ArrayList<Attribute> difference = new ArrayList<>();
        difference.addAll(subFrom);
        for(Attribute current : sub)
        {
            difference.remove(current);
        }
        return difference;
    }
    
    //Gets the set of days which match a parental instantiation.
    public static ArrayList<Day> getMatches(ArrayList<Attribute> toMatchAgainst, ArrayList<Day> toMatch, String values)
    {
        ArrayList<Day> matches = new ArrayList<>();
        
        for(Day current : toMatch)
        {
            boolean doesMatch = true;
            for(int i = 0; i < toMatchAgainst.size(); i++)
            {
                Attribute currentAtt = toMatchAgainst.get(i);
                if(!(getAttributeValue(current, currentAtt.Name) == Character.getNumericValue(values.charAt(i))))
                {
                    doesMatch = false;
                }
            }
            if(doesMatch)
            {
                matches.add(current);
            }
        }
        
        return matches;
    }
    
    //Gets the value of an attribute from a day.
    public static int getAttributeValue(Day theDay, String attName)
    {
        int value = 0;
        for(Attribute current: theDay.Attributes)
        {
            if(current.Name.equals(attName))
            {
                value = current.Value;
            }
        }
        return value;
    }
}
