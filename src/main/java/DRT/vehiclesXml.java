package DRT;

import java.io.FileWriter;
import java.io.IOException;

/*Class to generate the drt fleet*/

public class vehiclesXml {
    public static void main(String[] args) {
        String fileName = "scenarios/DRTStructure/RegionalDRT/vehicle_24300.xml";
        int totalVehicles = 24300;
        //Municipal
        //String[] startLinks = {"535282", "360005", "510964", "15957"};
        //Subregional
        //String[] startLinks = {"535282", "360005", "510964", "15957", "1851", "471024", "438922"};
        //Regional Scale
        String[] startLinks = {"535282", "360005", "510964", "15957", "1851", "471024", "438922", "55752", "701571", "204248"};

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
