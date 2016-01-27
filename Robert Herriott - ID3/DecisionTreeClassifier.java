package decisiontreeclassifier;

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

public class DecisionTreeClassifier {

    static ArrayList<Tumor> trainingData = new ArrayList<>();
    static ArrayList<Tumor> testData = new ArrayList<>();
    
    public static void main(String[] args) throws FileNotFoundException {
        //Make a file chooser for grabbing data.
        JFileChooser myChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Data Files", "csv");
        myChooser.setFileFilter(filter);

        //Pick a training file.
        JOptionPane.showMessageDialog(null, "Welcome to the Decision Tree classifier!\nClick OK and I will have you choose your TRAINING data file.");
        myChooser.showOpenDialog(null);
        File trainingFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Pick a test file.
        JOptionPane.showMessageDialog(null, "Thanks, now click OK again and I will have you choose your TEST data file.");
        myChooser.showOpenDialog(null);
        File testFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Set up scanner and loop through training file, adding each line as a new Tumor to the trainingData list.
        Scanner myScanner = new Scanner(trainingFile);
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            trainingData.add(new Tumor(currentLine));
        }
        
        //Set up scanner and loop through training file, adding each line as a new Tumor to the testData list.
        myScanner = new Scanner(testFile);
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            testData.add(new Tumor(currentLine));
        }
        
        //Create a tree and run ID3 to build it.
        ID3Tree theTree = new ID3Tree();
        theTree.head = theTree.ID3(trainingData, theTree.allAttributes);
        
        //Use the tree to classify all the test tumors.
        for(Tumor currentTumor: testData)
        {
            theTree.Classify(currentTumor);
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
        int denominator = testData.size();
        
        //Loop through test fruit, see how many have their guessed type matching their actual type.
        for(int i = 0; i < testData.size(); i++)
        {
            Tumor currentTest = testData.get(i);
            guessedTypes.setText(guessedTypes.getText() + i + ": " + currentTest.classifiedType + "\n");
            labelTypes.setText(labelTypes.getText() + i + ": " + currentTest.Type + "\n");
            if(currentTest.classifiedType.equals(currentTest.Type))
            {
                numerator += 1;
            }
        }
        
        //Configure and display all the GUI elements.
        guessedLabel.setPreferredSize(new Dimension(175, 15));
        actualLabel.setPreferredSize(new Dimension(175, 15));
        leftScroll.setPreferredSize(new Dimension(175,600));
        rightScroll.setPreferredSize(new Dimension(175,600));
        accuracy.setText("Accuracy: " + numerator + " / " + denominator + " tumors classified correctly.");
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
