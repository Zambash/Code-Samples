package neuralnet;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NeuralNet {
    
    static ArrayList<Iris> data = new ArrayList<>();
    
    static ArrayList<Iris> trainingData = new ArrayList<>();
    static ArrayList<Iris> testData = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        //Make a file chooser for grabbing data.
        JFileChooser myChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Data Files", "csv");
        myChooser.setFileFilter(filter);

        //Pick a data file.
        JOptionPane.showMessageDialog(null, "Welcome to the Neural Network classifier!\nClick OK and I will have you choose your data file.");
        myChooser.showOpenDialog(null);
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Set up scanner and loop through training file, adding each line as a new Iris to the data list.
        Scanner myScanner = new Scanner(trainingFile);
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            data.add(new Iris(currentLine));
        }
        
        //Make a seed shuffling the data.
        long seed = System.nanoTime();
        Collections.shuffle(data, new Random(seed));
        
        //Create a confusion matrix.
        int [][] confusion = new int[3][3];
        
        //A text area for storing results.
        JTextArea resultsArea = new JTextArea();
        
        //Loop 10 times for 10-fold cross validation
        for(int i = 0; i < 10; i++)
        {
            trainingData.clear();
            testData.clear();
            
            trainingData.addAll(data);
            
            int counter = trainingData.size() / 10;
            
            //Pull out a random 10% of the data to use for test data, and leave the other 90% for training data.
            for(int q = 0; q < counter; q++)
            {
                int indexToRemove = (int)(Math.random() * (trainingData.size() - 1));
                testData.add(trainingData.get(indexToRemove));
                trainingData.remove(indexToRemove);
            }
            
            /*-----------------------------------------------------------------
            ----------------THIS IS WHERE YOU DEFINE YOUR LAYERS---------------
            ------------------------------------------------------------------*/
            int [] layerSize = {3,3,3};
            Network theNetwork = new Network(layerSize, .001);
            
            //Feed data for 10,000 epochs
            for(int j = 0; j < 10000; j++)
            {
                Collections.shuffle(trainingData, new Random(seed));
                for(Iris current: trainingData)
                {
                    theNetwork.FeedIris(current);
                }
            }
            
            //Classify the testData
            for(Iris current : testData)
            {
                theNetwork.FeedForward(current);
                current.GuessedType = theNetwork.GetClass();
            }
            
            int numerator = 0;
            int denominator = testData.size();
            
            //See how many you classified correctly and increment the confusion matrix.
            for(Iris current : testData)
            {
                confusion[current.GuessedType - 1][current.Type - 1]++;
                if(current.Type == current.GuessedType)
                {
                    numerator++;
                }
            }
            
            resultsArea.setText(resultsArea.getText() + "Fold " + (i+1) + ": " + numerator + "/" + denominator + " classified correctly\n");
        }
        
        resultsArea.setText(resultsArea.getText() + "\nConfusion Matrix:\n" + confusion[0][0] + "," + confusion[0][1] + "," + confusion[0][2] + 
                "\n" + confusion[1][0] + "," + confusion[1][1] + "," + confusion[1][2] + "\n" + confusion[2][0] + "," + confusion[2][1] +
                "," + confusion[2][2] + "\n");
        
        //Create a display to show guessed types vs labels and accuracy.
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        JLabel resultsLabel = new JLabel("Results:");
           
        //Configure and display all the GUI elements.
        resultsLabel.setPreferredSize(new Dimension(250, 15));
        resultsScroll.setPreferredSize(new Dimension(250,270));
        JFrame resultsFrame = new JFrame("Neural Network!");
        resultsFrame.setLayout(new FlowLayout());
        resultsFrame.setSize(270, 340);
        resultsFrame.setDefaultCloseOperation(3);
        resultsFrame.add(resultsLabel);
        resultsFrame.add(resultsScroll);
        resultsFrame.setVisible(true);
    }
    
}
