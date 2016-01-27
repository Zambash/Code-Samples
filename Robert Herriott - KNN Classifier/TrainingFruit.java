package knn.fruit.classifier;

public class TrainingFruit implements Comparable<TrainingFruit>{
    public double redness = 0, yellowness = 0, mass = 0, volume = 0, sampleDistance = 0;
    public String type;
    
    //Create a new fruit with the inputted values.
    public TrainingFruit(double newRedness, double newYellowness, double newMass, double newVolume, String newType)
    {
        redness = newRedness;
        yellowness = newYellowness;
        mass = newMass;
        volume = newVolume;
        type = newType;
    }
    
    //Calculate the euclidean distance between this fruit and a passed test fruit.
    public void calcDistance(TestFruit toTest)
    {
        sampleDistance = 0;
        sampleDistance += Math.pow(redness - toTest.redness, 2);
        sampleDistance += Math.pow(yellowness - toTest.yellowness, 2);
        sampleDistance += Math.pow(mass - toTest.mass, 2);
        sampleDistance += Math.pow(volume - toTest.volume, 2);
        sampleDistance = Math.pow(sampleDistance, .5);
    }

    //Compare the distance from the sample of this fruit with another, used for
    //sorting the list of fruits.
    @Override
    public int compareTo(TrainingFruit t) {
        if(sampleDistance < t.sampleDistance)
        {
            return -1;
        }
        else if(sampleDistance == t.sampleDistance)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}
