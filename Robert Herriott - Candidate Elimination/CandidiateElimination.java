package candidiateelimination;

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

public class CandidiateElimination {

    static ArrayList<Day> trainingDays = new ArrayList<>();
    static ArrayList<Day> testDays = new ArrayList<>();
    
    static ArrayList<Hypothesis> S = new ArrayList<>();
    static ArrayList<Hypothesis> G = new ArrayList<>();
    
    static ArrayList<Hypothesis> VersionSpace = new ArrayList<>();
    
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
        JOptionPane.showMessageDialog(null, "Welcome to the Candidate Eliminiation classifier!\nClick OK and I will have you choose your data file.");
        myChooser.showOpenDialog(null);
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        for(int p = 0; p < 10; p++)
        {
            trainingDays.clear();
            testDays.clear();
            S.clear();
            G.clear();
            VersionSpace.clear();
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

            //Initialize G to the set of maximally general hyphotheses in H
            G.add(new Hypothesis("?","?","?","?","?","?"));
            //Initialize S to the set of maximally specific hypotheses in H
            S.add(new Hypothesis("!","!","!","!","!","!"));
            //For each training example d, do
            for(Day d : trainingDays)
            {
                //If d is a positive example
                if(d.type.equals("Enjoy Sport"))
                {
                    //Remove from G any hypothesis inconsistent with d
                    RemoveInconsistentG(d);
                    //For each hypothesis s in S that is not consistent with d
                    ArrayList<Hypothesis> tempS = new ArrayList<>(S);
                    ArrayList<Hypothesis> remFromS = new ArrayList<>();
                    for(Hypothesis s: S)
                    {
                        if(!s.PositivelyConsistent(d))
                        {
                            //Remove s from S
                            remFromS.add(s);
                            //Add to S all mimimal generalizations h of s such that...
                            tempS.addAll(PositiveGeneralizations(d,s));                       
                        }
                    }
                    S = tempS;
                    for(Hypothesis current: remFromS)
                    {
                        S.remove(current);
                    }
                    //Remove from S any hypothesis that is more general than another hypothesis in S
                    RemoveTooGeneral();
                }
                //If d is a negative example
                if(d.type.equals("Do Not Enjoy"))
                {
                    //Remove from S any hypothesis inconsistent with d
                    RemoveInconsistentS(d);
                    //For each hypothesis g in G that is not consistent with d
                    ArrayList<Hypothesis> tempG = new ArrayList<>(G);
                    ArrayList<Hypothesis> remFromG = new ArrayList<>();
                    for(Hypothesis g: G)
                    {
                        if(!g.NegativelyConsistent(d))
                        {
                            //Remove g from G
                            remFromG.add(g);
                            //Add to G all minimal specializations h of g such that...
                            tempG.addAll(g.getSpecializations(d,g.NumQuestions()));
                        }
                    }
                    G = tempG;
                    for(Hypothesis current: remFromG)
                    {
                        G.remove(current);
                    }
                    //Remove from G any hypothesis that is more specific than another hypothesis in G
                    RemoveTooSpecific();
                }
            }

            //Fill in the middle of the version space.
            ArrayList<Hypothesis> theMiddle = new Hypothesis().CalculateMiddle(S, G, S.get(0).NumQuestions(), G.get(0).NumQuestions());

            //Stick everything together into the whole version space.
            VersionSpace.addAll(S);
            VersionSpace.addAll(theMiddle);
            VersionSpace.addAll(G);

            //Loop through the test Days and classify them
            for(Day currentTest: testDays)
            {
                int numYes = 0;
                int numNo = 0;
                for(Hypothesis currentHypothesis: VersionSpace)
                {
                    if(currentHypothesis.PositivelyConsistent(currentTest))
                    {
                        numYes++;
                    }
                    else
                    {
                        numNo++;
                    }
                }
                if(numYes >= numNo)
                {
                    currentTest.guessedType = "Enjoy Sport";
                }
                else
                {
                    currentTest.guessedType = "Do Not Enjoy";
                }
            }
            
            int numerator = 0;
            int denominator = testDays.size();

            for (Day currentTest : testDays) 
            {
                if(currentTest.type.equals("Enjoy Sport") && currentTest.guessedType.equals("Enjoy Sport"))
                {
                    Positives++;
                }
                if(currentTest.type.equals("Enjoy Sport") && currentTest.guessedType.equals("Do Not Enjoy"))
                {
                    FalseNegatives++;
                }
                if(currentTest.type.equals("Do Not Enjoy") && currentTest.guessedType.equals("Enjoy Sport"))
                {
                    FalsePositives++;
                }
                if(currentTest.type.equals("Do Not Enjoy") && currentTest.guessedType.equals("Do Not Enjoy"))
                {
                    Negatives++;
                }
                if(currentTest.guessedType.equals(currentTest.type))
                {
                    numerator += 1;
                }
            }
            results[p] = numerator + "/" + denominator;          
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
        
        resultsArea.setText(resultsArea.getText() + "\nVersion space of final fold:\n");
        for(Hypothesis h: VersionSpace)
        {
            resultsArea.setText(resultsArea.getText() + h + "\n");
        }
        
        resultsArea.setText(resultsArea.getText() + "\nPositives: " + Positives + "\nNegatives: " + Negatives +
                "\nFalse Positives: " + FalsePositives + "\nFalse Negatives: " + FalseNegatives);
        
        //Configure and display all the GUI elements.
        resultsLabel.setPreferredSize(new Dimension(250, 15));
        resultsScroll.setPreferredSize(new Dimension(250,270));
        JFrame resultsFrame = new JFrame("Candidate Elimination!");
        resultsFrame.setLayout(new FlowLayout());
        resultsFrame.setSize(270, 340);
        resultsFrame.setDefaultCloseOperation(3);
        resultsFrame.add(resultsLabel);
        resultsFrame.add(resultsScroll);
        resultsFrame.setVisible(true);
    }
    
    //Removes items from S which are more general than other items in S
    public static void RemoveTooGeneral()
    {
        ArrayList<Hypothesis> toRemove = new ArrayList<>();
        int minq = 7;
        for(Hypothesis h: S)
        {
            if(h.NumQuestions() < minq)
            {
                minq = h.NumQuestions();
            }
        }
        for(Hypothesis h: S)
        {
            if(h.NumQuestions() > minq)
            {
                toRemove.add(h);
            }
        }
        for(Hypothesis current: toRemove)
        {
            S.remove(current);
        }
    }
    
    //Removes items in G which are more specific than other items in G
    public static void RemoveTooSpecific()
    {
        ArrayList<Hypothesis> toRemove = new ArrayList<>();
        int maxq = 0;
        for(Hypothesis h: G)
        {
            if(h.NumQuestions() > maxq)
            {
                maxq = h.NumQuestions();
            }
        }
        for(Hypothesis h: G)
        {
            if(h.NumQuestions() < maxq)
            {
                toRemove.add(h);
            }
        }
        for(Hypothesis current: toRemove)
        {
            G.remove(current);
        }
    }
    
    //Returns the minimally more general versions of a positive hypothesis.
    public static ArrayList<Hypothesis> PositiveGeneralizations(Day d, Hypothesis s)
    {
        ArrayList<Hypothesis> Generalizations = new ArrayList<>();
        
        if(s.ContainsNulls())
        {
            Generalizations.add(new Hypothesis(d.Sky, d.AirTemp, d.Humidity, d.Wind, d.Water, d.Forecast));
            return Generalizations;
        }
        else
        {
            Generalizations.add(s.getGeneralization(d));
            return Generalizations;
        }      
    }
    
    //Removes items from G which are inconsistent with a positive day.
    public static void RemoveInconsistentG(Day d)
    {
        ArrayList<Hypothesis> toRemove = new ArrayList<>();
        for(Hypothesis h : G)
        {
            if(!h.PositivelyConsistent(d))
            {
                toRemove.add(h);
            }
        }
        for(Hypothesis current: toRemove)
        {
            G.remove(current);
        }
    }
    
    //Removes items from S which are inconsistent with a negative day.
    public static void RemoveInconsistentS(Day d)
    {
        ArrayList<Hypothesis> toRemove = new ArrayList<>();
        for(Hypothesis h : S)
        {
            if(!h.NegativelyConsistent(d))
            {
                toRemove.add(h);
            }
        }
        for(Hypothesis current: toRemove)
        {
            S.remove(current);
        }
    }
}
