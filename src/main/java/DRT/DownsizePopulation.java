package DRT;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*Use as a input file the synthetic generated tsv file of the population and reduce it randomly 
 * (usually defined for 5% or 10% but adjustable)* */

public class DownsizePopulation {
    public static void main(String[] args) {
        try {
            // Load the original TSV file
            String originalFilePath = "data/population/Syntheticpopulation_filtro2_simplified_filtro3_EWGT.tsv";
            BufferedReader reader = new BufferedReader(new FileReader(originalFilePath));

            // Create a list to store lines
            List<String> lines = new ArrayList<>();

            // Read the lines from the original file
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            // Shuffle the list randomly
            Collections.shuffle(lines, new Random());

            // Calculate 5% of the lines
            int numLinesToSelect = (int) Math.ceil(0.05 * lines.size());

            // Create a new TSV file for the selected lines
            String selectedFilePath = "data/population/Syntheticpopulation_filtro2_simplified_filtro3_EWGT_downsize5.tsv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFilePath));

            // Write the randomly chosen lines to the new file
            for (int i = 0; i < numLinesToSelect; i++) {
                writer.write(lines.get(i));
                writer.newLine();
            }
            writer.close();

            System.out.println("Randomly selected 5% of lines saved to output file.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
