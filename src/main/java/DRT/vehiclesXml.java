package DRT;

import java.io.FileWriter;
import java.io.IOException;

/*Class to generate the drt fleet*/

public class vehiclesXml {
    public static void main(String[] args) {
        String fileName = "scenarios/coimbra_ewgtdrt_v2/vehicles_20000.xml";
        int totalVehicles = 20000;
        String[] startLinks = {"48515", "1851", "28007", "46521", "35786", "57314"};

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write("<!DOCTYPE vehicles SYSTEM \"http://matsim.org/files/dtd/dvrp_vehicles_v1.dtd\">\n");
            writer.write("<vehicles>\n");

            for (int i = 0; i < totalVehicles; i++) {
                String vehicleId = "SV" + i;
                String startLink = startLinks[i % startLinks.length];

                writer.write("\t<vehicle id=\"" + vehicleId + "\" start_link=\"" + startLink + "\" capacity=\"4\" t_0=\"0.0\" t_1=\"86400.0\"/>\n");
            }

            writer.write("</vehicles>");
            writer.close();
            System.out.println("XML file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the XML file: " + e.getMessage());
        }
    }
}
