package neuralnet;

import java.util.ArrayList;

public class Iris {
    ArrayList<Double> Attributes = new ArrayList<>();
    int Type;
    int GuessedType;
    
    //Create a new Iris from passed data.
    public Iris(String [] newatts)
    {
        for(int i = 0; i < newatts.length - 1; i++)
        {
            Attributes.add(Double.parseDouble(newatts[i]));
        }
        Type = Integer.parseInt(newatts[newatts.length - 1]);
    }
    
    public Iris(double specialInput, double spectwo)
    {
        Attributes.add(specialInput);
        Attributes.add(spectwo);
    }
}
