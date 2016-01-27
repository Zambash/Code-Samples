package knn.fruit.classifier;

public class TestFruit {
    public double redness = 0, yellowness = 0, mass = 0, volume = 0;
    public String type;
    public String guessedType;
    
    //Create a test fruit with the passed values.
    public TestFruit(double newRedness, double newYellowness, double newMass, double newVolume, String newType)
    {
        redness = newRedness;
        yellowness = newYellowness;
        mass = newMass;
        volume = newVolume;
        type = newType;
    }
    
}
