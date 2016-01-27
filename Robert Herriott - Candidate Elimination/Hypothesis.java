package candidiateelimination;

import java.util.ArrayList;
import java.util.Arrays;

public class Hypothesis {
    public String Sky, AirTemp, Humidity, Wind, Water, Forecast;
    public String values [][] = new String[6][];
    
    public Hypothesis(String newSky, String newTemp, String newHumid, String newWind, String newWater, String newForecast)
    {
        Sky = newSky;
        AirTemp = newTemp;
        Humidity = newHumid;
        Wind = newWind;
        Water = newWater;
        Forecast = newForecast;
        
        String [] Skies = {"Sunny", "Rainy", "Cloudy"};
        String [] Temps = {"Warm", "Cold"};
        String [] Humids = {"High", "Normal"};
        String [] Winds = {"Weak", "Strong"};
        String [] Waters = {"Cool", "Warm"};
        String [] Forecasts = {"Same", "Change"};
        
        values[0] = Skies;
        values[1] = Temps;
        values[2] = Humids;
        values[3] = Winds;
        values[4] = Waters;
        values[5] = Forecasts;
    }

    public Hypothesis()
    {
        String [] Skies = {"Sunny", "Rainy", "Cloudy"};
        String [] Temps = {"Warm", "Cold"};
        String [] Humids = {"High", "Normal"};
        String [] Winds = {"Weak", "Strong"};
        String [] Waters = {"Cool", "Warm"};
        String [] Forecasts = {"Same", "Change"};
        
        values[0] = Skies;
        values[1] = Temps;
        values[2] = Humids;
        values[3] = Winds;
        values[4] = Waters;
        values[5] = Forecasts;
    }
    
    //Quick helper method to check if an array contains a value.
    public boolean ArrayContains(int [] toTest, int value)
    {
        for(int i = 0; i < toTest.length; i++)
        {
            if(toTest[i] == value)
            {
                return true;
            }
        }
        return false;
    }
    
    //Used to see if a middle-hypothesis is consistent with those in the general boundary.
    public boolean GeneralConsistent(Hypothesis toTest, Hypothesis testAgainst)
    {
        if(!testAgainst.Sky.equals("?"))
        {
            if(toTest.Sky.equals("?"))
            {
                return false;
            }
        }
        if(!testAgainst.AirTemp.equals("?"))
        {
            if(toTest.AirTemp.equals("?"))
            {
                return false;
            }
        }
        if(!testAgainst.Humidity.equals("?"))
        {
            if(toTest.Humidity.equals("?"))
            {
                return false;
            }
        }
        if(!testAgainst.Wind.equals("?"))
        {
            if(toTest.Wind.equals("?"))
            {
                return false;
            }
        }
        if(!testAgainst.Water.equals("?"))
        {
            if(toTest.Water.equals("?"))
            {
                return false;
            }
        }
        if(!testAgainst.Forecast.equals("?"))
        {
            if(toTest.Forecast.equals("?"))
            {
                return false;
            }
        }
        return true;
    }
    
    //Used to see if a middle-hypothesis is consistent with those in the specific boundary.
    public boolean SpecificConsistent(Hypothesis toTest, Hypothesis testAgainst)
    {
        if(testAgainst.Sky.equals("?"))
        {
            if(!toTest.Sky.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.Sky.equals(testAgainst.Sky))
            {
                if(!toTest.Sky.equals("?"))
                {
                    return false;
                }
            }
        }
        if(testAgainst.AirTemp.equals("?"))
        {
            if(!toTest.AirTemp.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.AirTemp.equals(testAgainst.AirTemp))
            {
                if(!toTest.AirTemp.equals("?"))
                {
                    return false;
                }
            }
        }
        if(testAgainst.Humidity.equals("?"))
        {
            if(!toTest.Humidity.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.Humidity.equals(testAgainst.Humidity))
            {
                if(!toTest.Humidity.equals("?"))
                {
                    return false;
                }
            }
        }
        if(testAgainst.Wind.equals("?"))
        {
            if(!toTest.Wind.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.Wind.equals(testAgainst.Wind))
            {
                if(!toTest.Wind.equals("?"))
                {
                    return false;
                }
            }
        }
        if(testAgainst.Water.equals("?"))
        {
            if(!toTest.Water.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.Water.equals(testAgainst.Water))
            {
                if(!toTest.Water.equals("?"))
                {
                    return false;
                }
            }
        }
        if(testAgainst.Forecast.equals("?"))
        {
            if(!toTest.Forecast.equals("?"))
            {
                return false;
            }
        }
        else
        {
            if(!toTest.Forecast.equals(testAgainst.Forecast))
            {
                if(!toTest.Forecast.equals("?"))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    //Use a bunch of nested loops to calculate all possible hypothesis with number
    //of question marks between minQ and maxQ, then add those which are consistent
    //with the boundaries to the middle version space.
    public ArrayList<Hypothesis> CalculateMiddle(ArrayList<Hypothesis> specificBound, ArrayList<Hypothesis> generalBound, int minQ, int maxQ)
    {
        ArrayList<Hypothesis> theMiddle = new ArrayList<>();
        
        for(int z = minQ + 1; z < maxQ; z++)
        {
            ArrayList<int[]> qPositions = GetQPositions(6,z);
        
            for(int [] currentList: qPositions)
            {
                for(int i = 0; i < 3; i++)
                {            
                    for(int j = 0; j < 2; j++)
                    {
                        for(int k = 0; k < 2; k++)
                        {
                            for(int l = 0; l < 2; l++)
                            {
                                for(int m = 0; m < 2; m++)
                                {
                                    for(int n = 0; n < 2; n++)
                                    {
                                        Hypothesis current = new Hypothesis(values[0][i], values[1][j], values[2][k], values[3][l], values[4][m], values[5][n]);
                                        if(ArrayContains(currentList, 1))
                                        {
                                            current.Sky = "?";
                                        }
                                        if(ArrayContains(currentList, 2))
                                        {
                                            current.AirTemp = "?";
                                        }
                                        if(ArrayContains(currentList, 3))
                                        {
                                            current.Humidity = "?";
                                        }
                                        if(ArrayContains(currentList, 4))
                                        {
                                            current.Wind = "?";
                                        }
                                        if(ArrayContains(currentList, 5))
                                        {
                                            current.Water = "?";
                                        }
                                        if(ArrayContains(currentList, 6))
                                        {
                                            current.Forecast = "?";
                                        }
                                        boolean toadd = true;
                                        for(Hypothesis temp: theMiddle)
                                        {
                                            if(temp.Sky.equals(current.Sky) && temp.AirTemp.equals(current.AirTemp) && temp.Humidity.equals(current.Humidity) && temp.Wind.equals(current.Wind) && temp.Water.equals(current.Water) && temp.Forecast.equals(current.Forecast))
                                            {
                                                toadd = false;
                                            }
                                        }
                                        for(Hypothesis temp: specificBound)
                                        {
                                            if(!SpecificConsistent(current, temp))
                                            {
                                                toadd = false;
                                            }
                                        }
                                        for(Hypothesis temp: generalBound)
                                        {
                                            if(!GeneralConsistent(current, temp))
                                            {
                                                toadd = false;
                                            }
                                        }
                                        if(toadd)
                                        {
                                            theMiddle.add(current);
                                        }    
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return theMiddle;
    }
    
    //Calculate all minimal specializations, AKA those with number of question
    //marks equal to numq - 1, of this hypothesis given a day.
    public ArrayList<Hypothesis> getSpecializations(Day d, int numq)
    {
        ArrayList<Hypothesis> Specializations = new ArrayList<>();
        int numQ = numq - 1;
        ArrayList<int[]> qPositions = GetQPositions(6,numQ);
        
        for(int [] currentList: qPositions)
        {
            for(int i = 0; i < 3; i++)
            {            
                for(int j = 0; j < 2; j++)
                {
                    for(int k = 0; k < 2; k++)
                    {
                        for(int l = 0; l < 2; l++)
                        {
                            for(int m = 0; m < 2; m++)
                            {
                                for(int n = 0; n < 2; n++)
                                {
                                    Hypothesis current = new Hypothesis(values[0][i], values[1][j], values[2][k], values[3][l], values[4][m], values[5][n]);
                                    if(ArrayContains(currentList, 1))
                                    {
                                        current.Sky = "?";
                                    }
                                    if(ArrayContains(currentList, 2))
                                    {
                                        current.AirTemp = "?";
                                    }
                                    if(ArrayContains(currentList, 3))
                                    {
                                        current.Humidity = "?";
                                    }
                                    if(ArrayContains(currentList, 4))
                                    {
                                        current.Wind = "?";
                                    }
                                    if(ArrayContains(currentList, 5))
                                    {
                                        current.Water = "?";
                                    }
                                    if(ArrayContains(currentList, 6))
                                    {
                                        current.Forecast = "?";
                                    }
                                    if(current.NegativelyConsistent(d))
                                    {
                                        boolean toadd = true;
                                        for(Hypothesis temp: Specializations)
                                        {
                                            if(temp.Sky.equals(current.Sky) && temp.AirTemp.equals(current.AirTemp) && temp.Humidity.equals(current.Humidity) && temp.Wind.equals(current.Wind) && temp.Water.equals(current.Water) && temp.Forecast.equals(current.Forecast))
                                            {
                                                toadd = false;
                                            }
                                        }
                                        if(toadd)
                                        {
                                            Specializations.add(current);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return Specializations;
    }
    
    //Get the minimal generalization of this hypothesis given a day.
    public Hypothesis getGeneralization(Day d)
    {
        Hypothesis newHypo = new Hypothesis();
        if(!d.Sky.equals(Sky))
        {
            newHypo.Sky = "?";
        }
        else
        {
            newHypo.Sky = Sky;
        }
        if(!d.AirTemp.equals(AirTemp))
        {
            newHypo.AirTemp = "?";
        }
        else
        {
            newHypo.AirTemp = AirTemp;
        }
        if(!d.Humidity.equals(Humidity))
        {
            newHypo.Humidity = "?";
        }
        else
        {
            newHypo.Humidity = Humidity;
        }
        if(!d.Wind.equals(Wind))
        {
            newHypo.Wind = "?";
        }
        else
        {
            newHypo.Wind = Wind;
        }
        if(!d.Water.equals(Water))
        {
            newHypo.Water = "?";
        }
        else
        {
            newHypo.Water = Water;
        }
        if(!d.Forecast.equals(Forecast))
        {
            newHypo.Forecast = "?";
        }
        else
        {
            newHypo.Forecast = Forecast;
        }
        return newHypo;
    }
    
    //Get the number of question marks in this hypothesis.
    public int NumQuestions()
    {
        int numberq = 0;
        if(Sky.equals("?"))
        {
            numberq++;
        }
        if(AirTemp.equals("?"))
        {
            numberq++;
        }
        if(Humidity.equals("?"))
        {
            numberq++;
        }
        if(Wind.equals("?"))
        {
            numberq++;
        }
        if(Water.equals("?"))
        {
            numberq++;
        }
        if(Forecast.equals("?"))
        {
            numberq++;
        }
        return numberq;
    }
    
    //Check to see if this hypothesis contains any nulls
    public boolean ContainsNulls()
    {
        if(Sky.equals("!"))
        {
            return true;
        }
        if(AirTemp.equals("!"))
        {
            return true;
        }
        if(Humidity.equals("!"))
        {
            return true;
        }
        if(Wind.equals("!"))
        {
            return true;
        }
        if(Water.equals("!"))
        {
            return true;
        }
        return Forecast.equals("!");
    }
    
    //Check if a day is consistent with this hypothesis in the positive sense.
    public boolean PositivelyConsistent(Day toTest)
    {
        if(!toTest.Sky.equals(Sky) & !Sky.equals("?"))
        {
            return false;
        }
        if(!toTest.AirTemp.equals(AirTemp) & !AirTemp.equals("?"))
        {
            return false;
        }
        if(!toTest.Humidity.equals(Humidity) & !Humidity.equals("?"))
        {
            return false;
        }
        if(!toTest.Wind.equals(Wind) & !Wind.equals("?"))
        {
            return false;
        }
        if(!toTest.Water.equals(Water) & !Water.equals("?"))
        {
            return false;
        }
        return !(!toTest.Forecast.equals(Forecast) & !Forecast.equals("?"));
    }
    
    //Check if a day is consistent with this hypothesis in the negative sense.
    public boolean NegativelyConsistent(Day toTest)
    {
        if(!toTest.Sky.equals(Sky) & !Sky.equals("?"))
        {
            return true;
        }
        if(!toTest.AirTemp.equals(AirTemp) & !AirTemp.equals("?"))
        {
            return true;
        }
        if(!toTest.Humidity.equals(Humidity) & !Humidity.equals("?"))
        {
            return true;
        }
        if(!toTest.Wind.equals(Wind) & !Wind.equals("?"))
        {
            return true;
        }
        if(!toTest.Water.equals(Water) & !Water.equals("?"))
        {
            return true;
        }
        return (!toTest.Forecast.equals(Forecast) & !Forecast.equals("?"));
    }
    
    //Used along with getNext to calculate all the possible positions of a given
    //number if questions marks within a hypothesis.
    public static final ArrayList<int[]> GetQPositions(final int n, final int r) {       
        ArrayList<int[]> positions = new ArrayList<>();
        int[] res = new int[r];
        for (int i = 0; i < res.length; i++) {
            res[i] = i + 1;
        }
        boolean done = false;
        while (!done) {
            positions.add(Arrays.copyOf(res, r));
            done = getNext(res, n, r);          
        }
        return positions;
    }

    //Helper function for GetQPositions.
    public static final boolean getNext(final int[] num, final int n, final int r) {
        int target = r - 1;
        num[target]++;
        if (num[target] > ((n - (r - target)) + 1)) {
            // Carry the One
            while (num[target] > ((n - (r - target)))) {
                target--;
                if (target < 0) {
                    break;
                }
            }
            if (target < 0) {
                return true;
            }
            num[target]++;
            for (int i = target + 1; i < num.length; i++) {
                num[i] = num[i - 1] + 1;
            }
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "<" + Sky + "," + AirTemp + "," + Humidity + "," + Wind + "," + Water + "," + Forecast + ">";
    }
}
