package DRT;

//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class PopulationDRT {
//	public static void main(String[] args) {
//        // Specify the path to the population.xml file
//        String populationFilePath = "scenarios/coimbra_ewgt_drt/population_filtro4.xml";
//        
//        try {
//            // Read the population.xml file
//            String populationXml = new String(Files.readAllBytes(Paths.get(populationFilePath)));
//
//            // Use regular expression to replace leg mode with "drt"
//            String modifiedPopulationXml = populationXml.replaceAll("leg mode=\"(.*?)\"", "leg mode=\"drt\"");
//
//            // Write the modified XML back to the population.xml file
//            Files.write(Paths.get(populationFilePath), modifiedPopulationXml.getBytes());
//            
//            System.out.println("Leg mode changed to 'drt' for all plans in population.xml.");
//        } catch (IOException e) {
//            System.err.println("Error reading or writing population.xml file: " + e.getMessage());
//        }
//    }
//
//}

//
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//Class that uses the original population xml file and change the modes 'car', 'pt' and'ride' by 'drt'

public class PopulationDRT {
    public static void main(String[] args) {
        // Specify the path to the population.xml file
        String populationFilePath = "scenarios/coimbra_ewgtdrt_v3/population.xml";

        try {
            // Read the population.xml file
            String populationXml = new String(Files.readAllBytes(Paths.get(populationFilePath)));

            // Use regular expression to replace car, pt, and ride modes with "drt" while keeping bike and walk modes unaltered
            String modifiedPopulationXml = populationXml.replaceAll("leg mode=\"(car|pt|ride)\"", "leg mode=\"drt\"");

            // Write the modified XML back to the population.xml file
            Files.write(Paths.get(populationFilePath), modifiedPopulationXml.getBytes());

            System.out.println("Modes 'car', 'pt', and 'ride' changed to 'drt' in population.xml.");
        } catch (IOException e) {
            System.err.println("Error reading or writing population.xml file: " + e.getMessage());
        }
    }
}
