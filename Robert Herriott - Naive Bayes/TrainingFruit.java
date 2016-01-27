

public class TrainingFruit{
    public double redness = 0, yellowness = 0, mass = 0, volume = 0, sampleDistance = 0;
    public String type;
    public int numericType;
    
    //Create a new fruit with the inputted values.
    public TrainingFruit(double newRedness, double newYellowness, double newMass, double newVolume, String newType)
    {
        redness = newRedness;
        yellowness = newYellowness;
        mass = newMass;
        volume = newVolume;
        type = newType;
        
        switch(type)
        {
            case "2":
                type = "peach";
                numericType = 0;
                break;
            case "3":
                type = "orange";
                numericType = 1;
                break;
            case "1":
                type = "apple";
                numericType = 2;
                break;
            case "4":
                type = "lemon";
                numericType = 3;
                break;
        }
    }
    
    //Find which bin this fruit belongs in.
    public int findBin(double attribute, double min, double max, int numBins)
    {
        if(attribute <= min)
        {
            return 0;
        }
        else if(attribute > max)
        {
            return numBins - 1;
        }
        else
        {
            double binSize = (max - min) / numBins;
            for(int i = 0; i < numBins; i++)
            {
                if(attribute > min + (binSize * i) && attribute <= min + (binSize * (i + 1)))
                {
                    return i;
                }
            }
        }
        return 0;
    }
}
