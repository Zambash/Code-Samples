package knn.fruit.classifier;

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

public class KNNFruitClassifier {
    static ArrayList<TrainingFruit> trainingFruit = new ArrayList<>();
    static ArrayList<TestFruit> testFruit = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        //Pick a training file.
        JFileChooser myChooser = new JFileChooser();
        JOptionPane.showMessageDialog(null, "Welcome to the simple KNN classifier!\nClick OK and I will have you choose your TRAINING data file.");
        myChooser.showOpenDialog(null);
        
        //Pick a test file.
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        JOptionPane.showMessageDialog(null, "Thanks, now click OK again and I will have you choose your TEST data file.");
        myChooser.showOpenDialog(null);
        
        //Pick a K value.
        File testFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        int k = Integer.parseInt(JOptionPane.showInputDialog(null, "Great! Now what value would you like to use for K? (Enter 0 for distance based voting)"));
        
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
            testFruit.add(new TestFruit(Double.parseDouble(currentLine[0]), Double.parseDouble(currentLine[1])
            , Double.parseDouble(currentLine[2]), Double.parseDouble(currentLine[3]), currentLine[4]));
        }
        
        //Normalize the training data.
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
        
        //Normalize using mins and maxes.
        for (TrainingFruit currentFruit : trainingFruit) 
        {
            currentFruit.redness = (currentFruit.redness - redMin) / (redMax - redMin);
            currentFruit.yellowness = (currentFruit.yellowness - yellowMin) / (yellowMax - yellowMin);
            currentFruit.mass = (currentFruit.mass - massMin) / (massMax - massMin);
            currentFruit.volume = (currentFruit.volume - volMin) / (volMax - volMin);
        }
        
        //Calc distances and sort and tally votes.
        for (TestFruit currentTest : testFruit) 
        {
            //Normalize test fruit.
            currentTest.redness = (currentTest.redness - redMin) / (redMax - redMin);
            currentTest.yellowness = (currentTest.yellowness - yellowMin) / (yellowMax - yellowMin);
            currentTest.mass = (currentTest.mass - massMin) / (massMax - massMin);
            currentTest.volume = (currentTest.volume - volMin) / (volMax - volMin);
            
            //Calc all distances.
            for(TrainingFruit currentFruit: trainingFruit)
            {
                currentFruit.calcDistance(currentTest);
            }
            
            //Sort by distance.
            Collections.sort(trainingFruit);
            
            double appleVotes = 0, peachVotes = 0, orangeVotes = 0, lemonVotes = 0;
            
            //Tally votes of all training fruit based on distance.
            if(k == 0)
            {
                for(TrainingFruit currentFruit: trainingFruit)
                {
                    switch(currentFruit.type)
                    {
                        case "apple":
                            appleVotes += 1 / Math.pow(currentFruit.sampleDistance, 2);
                            break;
                        case "peach":
                            peachVotes += 1 / Math.pow(currentFruit.sampleDistance, 2);
                            break;
                        case "orange":
                            orangeVotes += 1 / Math.pow(currentFruit.sampleDistance, 2);
                            break;
                        case "lemon":
                            lemonVotes += 1 / Math.pow(currentFruit.sampleDistance, 2);
                            break;
                    }
                }
            }
            //Tally votes of first K training fruit in the sorted list.
            else
            {
                for(int i = 0; i < k; i++)
                {
                    TrainingFruit currentFruit = trainingFruit.get(i);
                    
                    switch(currentFruit.type)
                    {
                        case "apple":
                            appleVotes += 1;
                            break;
                        case "peach":
                            peachVotes += 1;
                            break;
                        case "orange":
                            orangeVotes += 1;
                            break;
                        case "lemon":
                            lemonVotes += 1;
                            break;
                    }
                }
            }
            
            //Figure out which type has the most votes and assign that type to the current test.
            ArrayList<Double> theVotes = new ArrayList<>();
            theVotes.add(appleVotes);
            theVotes.add(peachVotes);
            theVotes.add(orangeVotes);
            theVotes.add(lemonVotes);
            Collections.sort(theVotes);
            
            if(theVotes.get(3) == appleVotes)
            {
                currentTest.guessedType = "apple";
            }
            else if(theVotes.get(3) == peachVotes)
            {
                currentTest.guessedType = "peach";
            }
            else if(theVotes.get(3) == orangeVotes)
            {
                currentTest.guessedType = "orange";
            }
            else
            {
                currentTest.guessedType = "lemon";
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
            TestFruit currentTest = testFruit.get(i);
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
