

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NaiveBayesClassifier {
    static ArrayList<TrainingFruit> trainingFruit = new ArrayList<>();
    static ArrayList<TestingFruit> testFruit = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        
        //Make a file chooser for grabbing data.
        JFileChooser myChooser = new JFileChooser();

        //Pick a training file.
        JOptionPane.showMessageDialog(null, "Welcome to the Naive Bayes classifier!\nClick OK and I will have you choose your TRAINING data file.");
        myChooser.showOpenDialog(null);
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Pick a test file.
        JOptionPane.showMessageDialog(null, "Thanks, now click OK again and I will have you choose your TEST data file.");
        myChooser.showOpenDialog(null);
        File testFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Set number of bins.
        int bins = Integer.parseInt(JOptionPane.showInputDialog(null, "Great! Now how many bins would you like to use for classifying?"));
        
        //Set up scanner and loop through training file, adding each line as a new TrainingFruit to the arraylist.
        Scanner myScanner = new Scanner(trainingFile);
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            trainingFruit.add(new TrainingFruit(Double.parseDouble(currentLine[0]), Double.parseDouble(currentLine[1])
            , Double.parseDouble(currentLine[2]), Double.parseDouble(currentLine[3]), currentLine[4]));
        }
        
        //Reset scanner and loop through the test file, adding each line as a new TestFruit to the arraylist.
        myScanner = new Scanner(testFile);
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            testFruit.add(new TestingFruit(Double.parseDouble(currentLine[0]), Double.parseDouble(currentLine[1])
            , Double.parseDouble(currentLine[2]), Double.parseDouble(currentLine[3]), currentLine[4]));
        }
        
        //Make variables for storing mins and maxes.
        double redMax = 0;
        double redMin = Double.POSITIVE_INFINITY;
        
        double yellowMax = 0;
        double yellowMin = Double.POSITIVE_INFINITY;
        
        double massMax = 0;
        double massMin = Double.POSITIVE_INFINITY;
        
        double volMax = 0;
        double volMin = Double.POSITIVE_INFINITY;
        
        //Determine min and max values.
        for (TrainingFruit currentFruit : trainingFruit) 
        {
            if(currentFruit.redness > redMax)
            {
                redMax = currentFruit.redness;
            }
            if(currentFruit.redness < redMin)
            {
                redMin = currentFruit.redness;
            }
            
            if(currentFruit.yellowness > yellowMax)
            {
                yellowMax = currentFruit.yellowness;
            }
            if(currentFruit.yellowness < yellowMin)
            {
                yellowMin = currentFruit.yellowness;
            }
            
            if(currentFruit.mass > massMax)
            {
                massMax = currentFruit.mass;
            }
            if(currentFruit.mass < massMin)
            {
                massMin = currentFruit.mass;
            }
            
            if(currentFruit.volume > volMax)
            {
                volMax = currentFruit.volume;
            }
            if(currentFruit.volume < volMin)
            {
                volMin = currentFruit.volume;
            }
        }
        
        //Ints for naming to avoid magic numbers.
        int Redness = 0;
        int Yellowness = 1;
        int Mass = 2;
        int Volume = 3;
        
        int Peach = 0;
        int Orange = 1;
        int Apple = 2;
        int Lemon = 3;
        
        //A datacube with Attribute,Bin,Fruit
        int [][][] DataCube = new int[4][bins][4];
        
        //Loop through all the fruits and build up the datacube.
        for(TrainingFruit currentFruit : trainingFruit)
        {         
            DataCube[Redness][currentFruit.findBin(currentFruit.redness, redMin, redMax, bins)][currentFruit.numericType]++;
            DataCube[Yellowness][currentFruit.findBin(currentFruit.yellowness, yellowMin, yellowMax, bins)][currentFruit.numericType]++;
            DataCube[Mass][currentFruit.findBin(currentFruit.mass, massMin, massMax, bins)][currentFruit.numericType]++;
            DataCube[Volume][currentFruit.findBin(currentFruit.volume, volMin, volMax, bins)][currentFruit.numericType]++;
        }
        
        //Loop through the test fruits and classify each one
        for(TestingFruit currentFruit : testFruit)
        {
            //Find bin for this test fruit in current dimension.
            int currentBin = currentFruit.findBin(currentFruit.redness, redMin, redMax, bins);
            double binTotal;
            
            //Grab the number of each fruit from that bin for that dimension.
            double PeachRed = DataCube[Redness][currentBin][Peach];
            double OrangeRed = DataCube[Redness][currentBin][Orange];
            double AppleRed = DataCube[Redness][currentBin][Apple];
            double LemonRed = DataCube[Redness][currentBin][Lemon];
            
            //Add them up for the total.
            binTotal = PeachRed + OrangeRed + AppleRed + LemonRed;
            if(binTotal == 0)
            {
                binTotal = 1;
            }
            
            //Divide the number of each fruit by the total fruit in the bin.
            PeachRed = PeachRed / binTotal;
            OrangeRed = OrangeRed / binTotal;
            AppleRed = AppleRed / binTotal;
            LemonRed = LemonRed / binTotal;
            
            //Account for zero's.
            if(PeachRed == 0)
            {
                PeachRed = 1 / (1000 + binTotal);
            }
            if(OrangeRed == 0)
            {
                OrangeRed = 1 / (1000 + binTotal);
            }
            if(AppleRed == 0)
            {
                AppleRed = 1 / (1000 + binTotal);
            }
            if(LemonRed == 0)
            {
                LemonRed = 1 / (1000 + binTotal);
            }
            
            //REPEAT ABOVE STEPS FOR OTHER THREE DIMENSIONS
            currentBin = currentFruit.findBin(currentFruit.yellowness, yellowMin, yellowMax, bins);
            
            double PeachYellow = DataCube[Yellowness][currentBin][Peach];
            double OrangeYellow = DataCube[Yellowness][currentBin][Orange];
            double AppleYellow = DataCube[Yellowness][currentBin][Apple];
            double LemonYellow = DataCube[Yellowness][currentBin][Lemon];
            
            binTotal = PeachYellow + OrangeYellow + AppleYellow + LemonYellow;
            if(binTotal == 0)
            {
                binTotal = 1;
            }
            
            PeachYellow = PeachYellow / binTotal;
            OrangeYellow = OrangeYellow / binTotal;
            AppleYellow = AppleYellow / binTotal;
            LemonYellow = LemonYellow / binTotal;
            
            if(PeachYellow == 0)
            {
                PeachYellow = 1 / (1000 + binTotal);
            }
            if(OrangeYellow == 0)
            {
                OrangeYellow = 1 / (1000 + binTotal);
            }
            if(AppleYellow == 0)
            {
                AppleYellow = 1 / (1000 + binTotal);
            }
            if(LemonYellow == 0)
            {
                LemonYellow = 1 / (1000 + binTotal);
            }
            
            currentBin = currentFruit.findBin(currentFruit.mass, massMin, massMax, bins);
            
            double PeachMass = DataCube[Mass][currentBin][Peach];
            double OrangeMass = DataCube[Mass][currentBin][Orange];
            double AppleMass = DataCube[Mass][currentBin][Apple];
            double LemonMass = DataCube[Mass][currentBin][Lemon];
            
            binTotal = PeachMass + OrangeMass + AppleMass + LemonMass;
            if(binTotal == 0)
            {
                binTotal = 1;
            }
            
            PeachMass = PeachMass / binTotal;
            OrangeMass = OrangeMass / binTotal;
            AppleMass = AppleMass / binTotal;
            LemonMass = LemonMass / binTotal;
            
            if(PeachMass == 0)
            {
                PeachMass = 1 / (1000 + binTotal);
            }
            if(OrangeMass == 0)
            {
                OrangeMass = 1 / (1000 + binTotal);
            }
            if(AppleMass == 0)
            {
                AppleMass = 1 / (1000 + binTotal);
            }
            if(LemonMass == 0)
            {
                LemonMass = 1 / (1000 + binTotal);
            }
            
            currentBin = currentFruit.findBin(currentFruit.volume, volMin, volMax, bins);
            
            double PeachVol = DataCube[Volume][currentBin][Peach];
            double OrangeVol = DataCube[Volume][currentBin][Orange];
            double AppleVol = DataCube[Volume][currentBin][Apple];
            double LemonVol = DataCube[Volume][currentBin][Lemon];
            
            binTotal = PeachVol + OrangeVol + AppleVol + LemonVol;
            if(binTotal == 0)
            {
                binTotal = 1;
            }
            
            PeachVol = PeachVol / binTotal;
            OrangeVol = OrangeVol / binTotal;
            AppleVol = AppleVol / binTotal;
            LemonVol = LemonVol / binTotal;
            
            if(PeachVol == 0)
            {
                PeachVol = 1 / (1000 + binTotal);
            }
            if(OrangeVol == 0)
            {
                OrangeVol = 1 / (1000 + binTotal);
            }
            if(AppleVol == 0)
            {
                AppleVol = 1 / (1000 + binTotal);
            }
            if(LemonVol == 0)
            {
                LemonVol = 1 / (1000 + binTotal);
            }
            
            //Multiply results for each fruit type from each dimension.
            double PeachTotal = PeachRed * PeachYellow * PeachMass * PeachVol;
            double OrangeTotal = OrangeRed * OrangeYellow * OrangeMass * OrangeVol;
            double AppleTotal = AppleRed * AppleYellow * AppleMass * AppleVol;
            double LemonTotal = LemonRed * LemonYellow * LemonMass * LemonVol;
            
            //Stick the results in a list for sorting.
            ArrayList<Double> sortVotes = new ArrayList<>();
            
            sortVotes.add(PeachTotal);
            sortVotes.add(OrangeTotal);
            sortVotes.add(AppleTotal);
            sortVotes.add(LemonTotal);
            
            //Sort the list and assign the highest value to the test fruit.
            Collections.sort(sortVotes);
            
            if(sortVotes.get(3) == PeachTotal)
            {
                currentFruit.guessedType = "peach";
            }
            if(sortVotes.get(3) == OrangeTotal)
            {
                currentFruit.guessedType = "orange";
            }
            if(sortVotes.get(3) == AppleTotal)
            {
                currentFruit.guessedType = "apple";
            }
            if(sortVotes.get(3) == LemonTotal)
            {
                currentFruit.guessedType = "lemon";
            }
        }
        
        //Create a display to show guessed types vs labels and accuracy.
        JTextArea guessedTypes = new JTextArea();
        JTextArea labelTypes = new JTextArea();
        JScrollPane leftScroll = new JScrollPane(guessedTypes);
        JScrollPane rightScroll = new JScrollPane(labelTypes);
        JLabel guessedLabel = new JLabel("Classified Types:");
        JLabel actualLabel = new JLabel("Actual Types:");
        JLabel accuracy = new JLabel();
        
        int numerator = 0;
        int denominator = testFruit.size();
        
        //Loop through test fruit, see how many have their guessed type matching their actual type.
        for(int i = 0; i < testFruit.size(); i++)
        {
            TestingFruit currentTest = testFruit.get(i);
            guessedTypes.setText(guessedTypes.getText() + i + ": " + currentTest.guessedType + "\n");
            labelTypes.setText(labelTypes.getText() + i + ": " + currentTest.type + "\n");
            if(currentTest.guessedType.equals(currentTest.type))
            {
                numerator += 1;
            }
        }
        
        //Configure and display all the GUI elements.
        guessedLabel.setPreferredSize(new Dimension(175, 15));
        actualLabel.setPreferredSize(new Dimension(175, 15));
        leftScroll.setPreferredSize(new Dimension(175,600));
        rightScroll.setPreferredSize(new Dimension(175,600));
        accuracy.setText("Accuracy: " + numerator + " / " + denominator + " fruits classified correctly.");
        accuracy.setPreferredSize(new Dimension(350, 15));
        JFrame resultsFrame = new JFrame("KNN Results!");
        resultsFrame.setLayout(new FlowLayout());
        resultsFrame.setSize(380, 685);
        resultsFrame.setDefaultCloseOperation(3);
        resultsFrame.add(guessedLabel);
        resultsFrame.add(actualLabel);
        resultsFrame.add(leftScroll);
        resultsFrame.add(rightScroll);
        resultsFrame.add(accuracy);
        resultsFrame.setVisible(true);
    }
    
}
