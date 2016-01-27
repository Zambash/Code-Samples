package candidiateelimination;
//Pretty obvious what this class is.
public class Day {
    public String Sky, AirTemp, Humidity, Wind, Water, Forecast, type, guessedType;
    
    public Day(String [] data)
    {
        Sky = data[0];
        AirTemp = data[1];
        Humidity = data[2];
        Wind = data[3];
        Water = data[4];
        Forecast = data[5];
        type = data[6];
    }
}
