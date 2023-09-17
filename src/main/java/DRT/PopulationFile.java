package DRT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PopulationFile {
	public static void main(String[] args) {
        // Specify the path to the input population.xml file
        String inputPopulationFilePath = "scenarios/coimbra_ewgt/population.xml";

        // Specify the path for the output populationdrt.xml file
        String outputPopulationFilePath = "scenarios/coimbra_ewgt_drt/populationdrt.xml";

        try {
            // Read the input population.xml file
            String populationXml = new String(Files.readAllBytes(Paths.get(inputPopulationFilePath)));

            // Use regular expression to replace leg mode with "car"
            String modifiedPopulationXml = populationXml.replaceAll("leg mode=\"(.*?)\"", "leg mode=\"drt\"");

            // Write the modified XML to the output populationdrt.xml file
            Files.write(Paths.get(outputPopulationFilePath), modifiedPopulationXml.getBytes());

            System.out.println("Leg mode changed to 'drt' for all plans in population.xml.");
            System.out.println("Modified XML written to: " + outputPopulationFilePath);
        } catch (IOException e) {
            System.err.println("Error reading or writing population.xml file: " + e.getMessage());
        }
    }
}
