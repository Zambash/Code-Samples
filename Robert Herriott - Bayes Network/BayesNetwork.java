package bayesnetwork;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BayesNetwork {

    static ArrayList<Day> trainingDays = new ArrayList<>();
    static ArrayList<Day> testDays = new ArrayList<>();
    
    static String[] results = new String[10];
    
    static int Positives = 0;
    static int Negatives = 0;
    static int FalsePositives = 0;
    static int FalseNegatives = 0;
    
    public static void main(String[] args) throws FileNotFoundException {
        //Make a file chooser for grabbing data.
        JFileChooser myChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Data Files", "csv");
        myChooser.setFileFilter(filter);

        //Pick a data file.
        JOptionPane.showMessageDialog(null, "Welcome to the Bayes Network classifier!\nClick OK and I will have you choose your data file.");
        myChooser.showOpenDialog(null);
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        

        for(int q = 0; q < 10; q++)
        {
            testDays.clear();
            trainingDays.clear();
            //Set up scanner and loop through training file, adding each line as a new Day to the trainingDays list.
            Scanner myScanner = new Scanner(trainingFile);
            myScanner.nextLine();
            while(myScanner.hasNextLine())
            {
                String [] currentLine = myScanner.nextLine().split(",");
                trainingDays.add(new Day(currentLine));
            }

            int counter = trainingDays.size() / 10;

            //Pull out a random 10% of the data to use for test data, and leave the other 90% for training data.
            for(int i = 0; i < counter; i++)
            {
                int indexToRemove = (int)(Math.random() * (trainingDays.size() - 1));
                testDays.add(trainingDays.get(indexToRemove));
                trainingDays.remove(indexToRemove);
            }

            //Create a set of attributes in a specific order.
            ArrayList<Attribute> theAttributes = Attribute.getOrdering();

            //Perform the K2 algorithm to find parents for the attributes.
            K2(theAttributes, trainingDays);

            //Sanity numbers for making the data-cube.
            int Attributes = 5;
            int Values = 2;
            int Classes = 2;

            int Storms = 0;
            int Bus = 1;
            int Lightning = 2;
            int Campfire = 3;
            int Thunder = 4;

            int [][][] dataCube = new int[Attributes][Values][Classes];

            //Build up the dataCube
            for(Day current : trainingDays)
            {
                dataCube[Storms][Helpers.getAttributeValue(current, "Storms")][current.type]++;
                dataCube[Bus][Helpers.getAttributeValue(current, "Bus")][current.type]++;
                dataCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][current.type]++;
                dataCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][current.type]++;
                dataCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][current.type]++;
            }

            //Loop through the testDays and classify them.
            for(Day current : testDays)
            {
                //Note, these are not saying yes or no to whether or not there was a storm,
                //they are values for holding the number of trainingDays which have a fire
                //or don't have a fire which have the same value as the test day in this dimension.
                double NoStorm;
                double YesStorm;
                double BinTotal;
                //If the attribute has no parents.
                if(theAttributes.get(Storms).parents.isEmpty())
                {
                    //Get the number in each bin
                    NoStorm = dataCube[Storms][Helpers.getAttributeValue(current, "Storms")][0];
                    YesStorm = dataCube[Storms][Helpers.getAttributeValue(current, "Storms")][1];

                    //Add them to get the total
                    BinTotal = NoStorm + YesStorm;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoStorm = NoStorm / BinTotal;
                    YesStorm = YesStorm / BinTotal;

                    //Account for zero's
                    if(NoStorm == 0)
                    {
                        NoStorm = 1 / (1000 + BinTotal);
                    }
                    if(YesStorm == 0)
                    {
                        YesStorm = 1 / (1000 + BinTotal);
                    }
                }
                //If the attribute does have parents.
                else
                {
                    //Get the subset of training days which match the child in current dimension and its parents.
                    ArrayList<Day> subset = new ArrayList<>();
                    ArrayList<Attribute> childAndParents = Helpers.addAttributes(theAttributes.get(Storms).parents, theAttributes.get(Storms));
                    for(Day currTrain : trainingDays)
                    {
                        boolean matches = true;
                        for(Attribute currAtt : childAndParents)
                        {
                            if(Helpers.getAttributeValue(current, currAtt.Name) != Helpers.getAttributeValue(currTrain, currAtt.Name))
                            {
                                matches = false;
                            }
                        }
                        if(matches)
                        {
                            subset.add(currTrain);
                        }
                    }
                    //Build a mini-cube from the shrunken universe.
                    int [][][] miniCube = new int[Attributes][Values][Classes];

                    //Build up the miniCube
                    for(Day miniDay : subset)
                    {
                        miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][miniDay.type]++;
                        miniCube[Bus][Helpers.getAttributeValue(current, "Bus")][miniDay.type]++;
                        miniCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][miniDay.type]++;
                        miniCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][miniDay.type]++;
                        miniCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][miniDay.type]++;
                    }

                    //Get the number in each bin
                    NoStorm = miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][0];
                    YesStorm = miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][1];

                    //Add them to get the total
                    BinTotal = NoStorm + YesStorm;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoStorm = NoStorm / BinTotal;
                    YesStorm = YesStorm / BinTotal;

                    //Account for zero's
                    if(NoStorm == 0)
                    {
                        NoStorm = 1 / (1000 + BinTotal);
                    }
                    if(YesStorm == 0)
                    {
                        YesStorm = 1 / (1000 + BinTotal);
                    }
                }

                //Okay, now time for a ton of shameless copy-pasta to repeat that for the other 4 dimensions.

                double NoBus;
                double YesBus;
                //If the attribute has no parents.
                if(theAttributes.get(Bus).parents.isEmpty())
                {
                    //Get the number in each bin
                    NoBus = dataCube[Storms][Helpers.getAttributeValue(current, "Bus")][0];
                    YesBus = dataCube[Storms][Helpers.getAttributeValue(current, "Bus")][1];

                    //Add them to get the total
                    BinTotal = NoBus + YesBus;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoBus = NoBus / BinTotal;
                    YesBus = YesBus / BinTotal;

                    //Account for zero's
                    if(NoBus == 0)
                    {
                        NoBus = 1 / (1000 + BinTotal);
                    }
                    if(YesBus == 0)
                    {
                        YesBus = 1 / (1000 + BinTotal);
                    }
                }
                //If the attribute does have parents.
                else
                {
                    //Get the subset of training days which match the child in current dimension and its parents.
                    ArrayList<Day> subset = new ArrayList<>();
                    ArrayList<Attribute> childAndParents = Helpers.addAttributes(theAttributes.get(Bus).parents, theAttributes.get(Bus));
                    for(Day currTrain : trainingDays)
                    {
                        boolean matches = true;
                        for(Attribute currAtt : childAndParents)
                        {
                            if(Helpers.getAttributeValue(current, currAtt.Name) != Helpers.getAttributeValue(currTrain, currAtt.Name))
                            {
                                matches = false;
                            }
                        }
                        if(matches)
                        {
                            subset.add(currTrain);
                        }
                    }
                    //Build a mini-cube from the shrunken universe.
                    int [][][] miniCube = new int[Attributes][Values][Classes];

                    //Build up the miniCube
                    for(Day miniDay : subset)
                    {
                        miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][miniDay.type]++;
                        miniCube[Bus][Helpers.getAttributeValue(current, "Bus")][miniDay.type]++;
                        miniCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][miniDay.type]++;
                        miniCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][miniDay.type]++;
                        miniCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][miniDay.type]++;
                    }

                    //Get the number in each bin
                    NoBus = miniCube[Storms][Helpers.getAttributeValue(current, "Bus")][0];
                    YesBus = miniCube[Storms][Helpers.getAttributeValue(current, "Bus")][1];

                    //Add them to get the total
                    BinTotal = NoBus + YesBus;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoBus = NoBus / BinTotal;
                    YesBus = YesBus / BinTotal;

                    //Account for zero's
                    if(NoBus == 0)
                    {
                        NoBus = 1 / (1000 + BinTotal);
                    }
                    if(YesBus == 0)
                    {
                        YesBus = 1 / (1000 + BinTotal);
                    }
                }

                double NoLightning;
                double YesLightning;
                //If the attribute has no parents.
                if(theAttributes.get(Lightning).parents.isEmpty())
                {
                    //Get the number in each bin
                    NoLightning = dataCube[Storms][Helpers.getAttributeValue(current, "Lightning")][0];
                    YesLightning = dataCube[Storms][Helpers.getAttributeValue(current, "Lightning")][1];

                    //Add them to get the total
                    BinTotal = NoLightning + YesLightning;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoLightning = NoLightning / BinTotal;
                    YesLightning = YesLightning / BinTotal;

                    //Account for zero's
                    if(NoLightning == 0)
                    {
                        NoLightning = 1 / (1000 + BinTotal);
                    }
                    if(YesLightning == 0)
                    {
                        YesLightning = 1 / (1000 + BinTotal);
                    }
                }
                //If the attribute does have parents.
                else
                {
                    //Get the subset of training days which match the child in current dimension and its parents.
                    ArrayList<Day> subset = new ArrayList<>();
                    ArrayList<Attribute> childAndParents = Helpers.addAttributes(theAttributes.get(Lightning).parents, theAttributes.get(Lightning));
                    for(Day currTrain : trainingDays)
                    {
                        boolean matches = true;
                        for(Attribute currAtt : childAndParents)
                        {
                            if(Helpers.getAttributeValue(current, currAtt.Name) != Helpers.getAttributeValue(currTrain, currAtt.Name))
                            {
                                matches = false;
                            }
                        }
                        if(matches)
                        {
                            subset.add(currTrain);
                        }
                    }
                    //Build a mini-cube from the shrunken universe.
                    int [][][] miniCube = new int[Attributes][Values][Classes];

                    //Build up the miniCube
                    for(Day miniDay : subset)
                    {
                        miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][miniDay.type]++;
                        miniCube[Bus][Helpers.getAttributeValue(current, "Bus")][miniDay.type]++;
                        miniCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][miniDay.type]++;
                        miniCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][miniDay.type]++;
                        miniCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][miniDay.type]++;
                    }

                    //Get the number in each bin
                    NoLightning = miniCube[Storms][Helpers.getAttributeValue(current, "Lightning")][0];
                    YesLightning = miniCube[Storms][Helpers.getAttributeValue(current, "Lightning")][1];

                    //Add them to get the total
                    BinTotal = NoLightning + YesLightning;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoLightning = NoLightning / BinTotal;
                    YesLightning = YesLightning / BinTotal;

                    //Account for zero's
                    if(NoLightning == 0)
                    {
                        NoLightning = 1 / (1000 + BinTotal);
                    }
                    if(YesLightning == 0)
                    {
                        YesLightning = 1 / (1000 + BinTotal);
                    }
                }

                double NoCampfire;
                double YesCampfire;
                //If the attribute has no parents.
                if(theAttributes.get(Campfire).parents.isEmpty())
                {
                    //Get the number in each bin
                    NoCampfire = dataCube[Storms][Helpers.getAttributeValue(current, "Campfire")][0];
                    YesCampfire = dataCube[Storms][Helpers.getAttributeValue(current, "Campfire")][1];

                    //Add them to get the total
                    BinTotal = NoCampfire + YesCampfire;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoCampfire = NoCampfire / BinTotal;
                    YesCampfire = YesCampfire / BinTotal;

                    //Account for zero's
                    if(NoCampfire == 0)
                    {
                        NoCampfire = 1 / (1000 + BinTotal);
                    }
                    if(YesCampfire == 0)
                    {
                        YesCampfire = 1 / (1000 + BinTotal);
                    }
                }
                //If the attribute does have parents.
                else
                {
                    //Get the subset of training days which match the child in current dimension and its parents.
                    ArrayList<Day> subset = new ArrayList<>();
                    ArrayList<Attribute> childAndParents = Helpers.addAttributes(theAttributes.get(Campfire).parents, theAttributes.get(Campfire));
                    for(Day currTrain : trainingDays)
                    {
                        boolean matches = true;
                        for(Attribute currAtt : childAndParents)
                        {
                            if(Helpers.getAttributeValue(current, currAtt.Name) != Helpers.getAttributeValue(currTrain, currAtt.Name))
                            {
                                matches = false;
                            }
                        }
                        if(matches)
                        {
                            subset.add(currTrain);
                        }
                    }
                    //Build a mini-cube from the shrunken universe.
                    int [][][] miniCube = new int[Attributes][Values][Classes];

                    //Build up the miniCube
                    for(Day miniDay : subset)
                    {
                        miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][miniDay.type]++;
                        miniCube[Bus][Helpers.getAttributeValue(current, "Bus")][miniDay.type]++;
                        miniCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][miniDay.type]++;
                        miniCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][miniDay.type]++;
                        miniCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][miniDay.type]++;
                    }

                    //Get the number in each bin
                    NoCampfire = miniCube[Storms][Helpers.getAttributeValue(current, "Campfire")][0];
                    YesCampfire = miniCube[Storms][Helpers.getAttributeValue(current, "Campfire")][1];

                    //Add them to get the total
                    BinTotal = NoCampfire + YesCampfire;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoCampfire = NoCampfire / BinTotal;
                    YesCampfire = YesCampfire / BinTotal;

                    //Account for zero's
                    if(NoCampfire == 0)
                    {
                        NoCampfire = 1 / (1000 + BinTotal);
                    }
                    if(YesCampfire == 0)
                    {
                        YesCampfire = 1 / (1000 + BinTotal);
                    }
                }

                double NoThunder;
                double YesThunder;
                //If the attribute has no parents.
                if(theAttributes.get(Campfire).parents.isEmpty())
                {
                    //Get the number in each bin
                    NoThunder = dataCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][0];
                    YesThunder = dataCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][1];

                    //Add them to get the total
                    BinTotal = NoThunder + YesThunder;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoThunder = NoThunder / BinTotal;
                    YesThunder = YesThunder / BinTotal;

                    //Account for zero's
                    if(NoThunder == 0)
                    {
                        NoThunder = 1 / (1000 + BinTotal);
                    }
                    if(YesThunder == 0)
                    {
                        YesThunder = 1 / (1000 + BinTotal);
                    }
                }
                //If the attribute does have parents.
                else
                {
                    //Get the subset of training days which match the child in current dimension and its parents.
                    ArrayList<Day> subset = new ArrayList<>();
                    ArrayList<Attribute> childAndParents = Helpers.addAttributes(theAttributes.get(Thunder).parents, theAttributes.get(Thunder));
                    for(Day currTrain : trainingDays)
                    {
                        boolean matches = true;
                        for(Attribute currAtt : childAndParents)
                        {
                            if(Helpers.getAttributeValue(current, currAtt.Name) != Helpers.getAttributeValue(currTrain, currAtt.Name))
                            {
                                matches = false;
                            }
                        }
                        if(matches)
                        {
                            subset.add(currTrain);
                        }
                    }
                    //Build a mini-cube from the shrunken universe.
                    int [][][] miniCube = new int[Attributes][Values][Classes];

                    //Build up the miniCube
                    for(Day miniDay : subset)
                    {
                        miniCube[Storms][Helpers.getAttributeValue(current, "Storms")][miniDay.type]++;
                        miniCube[Bus][Helpers.getAttributeValue(current, "Bus")][miniDay.type]++;
                        miniCube[Lightning][Helpers.getAttributeValue(current, "Lightning")][miniDay.type]++;
                        miniCube[Campfire][Helpers.getAttributeValue(current, "Campfire")][miniDay.type]++;
                        miniCube[Thunder][Helpers.getAttributeValue(current, "Thunder")][miniDay.type]++;
                    }

                    //Get the number in each bin
                    NoThunder = miniCube[Storms][Helpers.getAttributeValue(current, "Thunder")][0];
                    YesThunder = miniCube[Storms][Helpers.getAttributeValue(current, "Thunder")][1];

                    //Add them to get the total
                    BinTotal = NoThunder + YesThunder;
                    if(BinTotal == 0)
                    {
                        BinTotal = 1;
                    }

                    //Divide by the total number in the bin
                    NoThunder = NoThunder / BinTotal;
                    YesThunder = YesThunder / BinTotal;

                    //Account for zero's
                    if(NoThunder == 0)
                    {
                        NoThunder = 1 / (1000 + BinTotal);
                    }
                    if(YesThunder == 0)
                    {
                        YesThunder = 1 / (1000 + BinTotal);
                    }
                }

                //Multiply Across to get total vote for no and yes.
                double NoTotal = NoStorm + NoBus + NoLightning + NoCampfire + NoThunder;
                double YesTotal = YesStorm + YesBus + YesLightning + YesCampfire + YesThunder;

                //Assign the higher vote.
                if(NoTotal > YesTotal)
                {
                    current.guessedType = 0;
                }
                else
                {
                    current.guessedType = 1;
                }
            }
            
            int numerator = 0;
            int denominator = testDays.size();

            for (Day currentTest : testDays) 
            {
                if(currentTest.type == 1 && currentTest.guessedType == 1)
                {
                    Positives++;
                }
                if(currentTest.type == 1 && currentTest.guessedType == 0)
                {
                    FalseNegatives++;
                }
                if(currentTest.type == 0 && currentTest.guessedType == 1)
                {
                    FalsePositives++;
                }
                if(currentTest.type == 0 && currentTest.guessedType == 0)
                {
                    Negatives++;
                }
                if(currentTest.guessedType == currentTest.type)
                {
                    numerator += 1;
                }
            }
            results[q] = numerator + "/" + denominator;        
        }
        
        //Create a display to show guessed types vs labels and accuracy.
        JTextArea resultsArea = new JTextArea();
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        JLabel resultsLabel = new JLabel("Results:");
        
        int numerator = 0;
        int denominator = testDays.size();
        
        //Loop through test fruit, see how many have their guessed type matching their actual type.
        for(int i = 0; i < results.length; i++)
        {
            Day currentTest = testDays.get(i);
            resultsArea.setText(resultsArea.getText() + "Fold " + (i+1) + ": " + results[i] + " classified correctly\n");
        }
        
        resultsArea.setText(resultsArea.getText() + "\nPositives: " + Positives + "\nNegatives: " + Negatives +
                "\nFalse Positives: " + FalsePositives + "\nFalse Negatives: " + FalseNegatives);
        
        //Configure and display all the GUI elements.
        resultsLabel.setPreferredSize(new Dimension(250, 15));
        resultsScroll.setPreferredSize(new Dimension(250,270));
        JFrame resultsFrame = new JFrame("Bayes Network!");
        resultsFrame.setLayout(new FlowLayout());
        resultsFrame.setSize(270, 340);
        resultsFrame.setDefaultCloseOperation(3);
        resultsFrame.add(resultsLabel);
        resultsFrame.add(resultsScroll);
        resultsFrame.setVisible(true);
        
    }
    
    //K2, it pretty much looks exactly the same as the one in the slides.
    public static void K2(ArrayList<Attribute> attributes, ArrayList<Day> data)
    {
        for(Attribute current : attributes)
        {
            ArrayList<Attribute> parents = new ArrayList<>();
            double Pold = log_g(current, parents);
            boolean OKToProceed = true;
            while(OKToProceed)
            {
                ArrayList<Attribute> toConsider = Helpers.subAttributes(Helpers.pred(current, attributes), parents);
                Attribute Z = null;
                double currentScore = Double.NEGATIVE_INFINITY;
                for(Attribute innerCurrent : toConsider)
                {
                    if(log_g(current, Helpers.addAttributes(parents, innerCurrent)) > currentScore)
                    {
                        currentScore = log_g(current, Helpers.addAttributes(parents, innerCurrent));
                        Z = innerCurrent;
                    }
                }
                
                if(currentScore > Pold)
                {
                    Pold = currentScore;
                    parents = Helpers.addAttributes(parents, Z);
                }
                else
                {
                    OKToProceed = false;
                }
            }
            current.parents = parents;
        }
    }
    
    public static double log_g(Attribute child, ArrayList<Attribute> parents)
    {
        double score = 0;
        //If set of parents is empty
        if(parents.isEmpty())
        {
            //Nij is the size of the entire training set.
            int Nij = trainingDays.size();
            //Sv is the entire training set.
            ArrayList<Day> Sv = new ArrayList<>();
            Sv.addAll(trainingDays);
            
            score += Helpers.logfact(1) + Helpers.logfact(Nij + 1);
            
            //For each child instantiation (e.g. 0 and 1)
            for(int i = 0; i <= 1; i++)
            {
                //Get number of children with that instantiation and add to score with logfact.
                int count = Helpers.getCount(Sv, i);
                score += Helpers.logfact(count);
            }
        }
        else
        {
            //Get parental instantiations.
            ArrayList<String> instant = new ArrayList<>();
            for(int i = 0; i <= (int)Math.pow(2, parents.size()); i++)
            {
                String current = Integer.toBinaryString(i);
                int difference = parents.size() - current.length();
                for(int j = 0; j < difference; j++)
                {
                    current = "0" + current;
                }
                instant.add(current);
            }
            //For each parental instantiation
            for(String currentInst : instant)
            {
                //Get the training records that match (Sv)
                ArrayList<Day> Sv = Helpers.getMatches(parents, trainingDays, currentInst);
                //Size of that set is Nij
                int Nij = Sv.size();
                
                score += Helpers.logfact(1) + Helpers.logfact(Nij + 1);
                
                //For each child instantiation (e.g. 0 and 1)
                for(int i = 0; i <= 1; i++)
                {
                    //Get number of children with that instantiation and add to score with logfact.
                    int count = Helpers.getCount(Sv, i);
                    score += Helpers.logfact(count);
                }
            }
        }
        return score;
    }
}
