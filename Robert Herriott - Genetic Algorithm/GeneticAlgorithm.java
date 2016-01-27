package geneticalgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GeneticAlgorithm {

    public static void main(String[] args) throws FileNotFoundException {
        //Pick a data file.
        JFileChooser myChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Data Files", "csv");
        myChooser.setFileFilter(filter);
        JOptionPane.showMessageDialog(null, "Welcome to the Genetic Algorithm!\nClick OK and I will have you choose your knapsack data file.");
        myChooser.showOpenDialog(null);
        File knapsackFile = new File(myChooser.getSelectedFile().getAbsolutePath());
        
        //Set up scanner and loop through data file, grabbing the knapsack data and passing it to the population.
        Scanner myScanner = new Scanner(knapsackFile);
        ArrayList<Integer> weights = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();
        myScanner.nextLine();
        while(myScanner.hasNextLine())
        {
            String [] currentLine = myScanner.nextLine().split(",");
            weights.add(Integer.parseInt(currentLine[0]));
            values.add(Integer.parseInt(currentLine[1]));
        }
        
        //Get desired tourney size, mutation rate, and number of generations from the user.
        int tourneySize = Integer.parseInt(JOptionPane.showInputDialog("Please enter your desired tourney size."));
        int mutationRate = Integer.parseInt(JOptionPane.showInputDialog("Please input your desired mutation rate,\n this should be a single integer x, for an x/L mutation rate."));
        int generations = Integer.parseInt(JOptionPane.showInputDialog("Please enter the number of generations you wish to run."));
        
        //Create a new population with the inputted settings and data.
        Population thePopulation = new Population(mutationRate, tourneySize, weights, values);
  
        Knapsack best;
        double currentBest = 0;
        int bestGeneration = 0;
        //Run the population through the inputted number of generations.
        for(int i = 0; i < generations; i++)
        {
            thePopulation.Generation();
            best = thePopulation.highestFitness();
            if(best.fitness > currentBest)
            {
                bestGeneration = i + 1;
                currentBest = best.fitness;
            }
        }

        //Calculate the greedy solution.
        GreedySolution badSol = new GreedySolution(values, weights);
        
        best = thePopulation.highestFitness();
        
        JOptionPane.showMessageDialog(null, "The greedy solution came up with\nValue: " + badSol.SolutionValue +
                "\nWeight: " + badSol.SolutionWeight + "\nAnd the GA solution came up with\nValue: " + best.fitness +
                "\nWeight: " + best.weight + "\nGeneration Achieved: " + bestGeneration);
        
    }
    
}
