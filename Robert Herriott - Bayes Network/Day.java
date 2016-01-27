package bayesnetwork;

import java.util.ArrayList;

public class Day {
    ArrayList<Attribute> Attributes = new ArrayList<>();
    int type;
    int guessedType;
    
    public Day(String [] newstuff)
    {
        Attributes.add(new Attribute("Storms", Integer.parseInt(newstuff[0])));
        Attributes.add(new Attribute("Bus", Integer.parseInt(newstuff[1])));
        Attributes.add(new Attribute("Lightning", Integer.parseInt(newstuff[2])));
        Attributes.add(new Attribute("Campfire", Integer.parseInt(newstuff[3])));
        Attributes.add(new Attribute("Thunder", Integer.parseInt(newstuff[4])));
        type = Integer.parseInt(newstuff[5]);
    }
}
