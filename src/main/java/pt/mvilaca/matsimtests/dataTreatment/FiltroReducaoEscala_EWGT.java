package pt.mvilaca.matsimtests.dataTreatment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiltroReducaoEscala_EWGT {
    public static void main(String[] args) {
        // Define the path to the input TSV file
        File inputFile = Paths.get("data", "population", "Filtro2", "Synthetic_filtro3.tsv").toFile();
        
        // Define the path to the output TSV file
        File outputFile = Paths.get("data", "population", "Filtro2", "SyntheticPopulationCoimbra_EWGT.tsv").toFile();
        
     // Define the values and ranges to keep in column 7 and 15
        List<Object> wantedValues = new ArrayList<>();
        wantedValues.addAll(Arrays.asList(8, 9, 105, 54, 53, 39, 43, 44, 114, 37, 35, 34, 25, 23, 22, 112, 20, 21, 19, 111, "301-364"));
        
        // Read the input TSV file and filter the rows
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
        	// Write the header line to the output TSV file
            bw.write(br.readLine());
            bw.newLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                int col7Value = Integer.parseInt(values[7]);
                int col15Value = Integer.parseInt(values[15]);
                if (wantedValues.contains(col7Value) && wantedValues.contains(col15Value)) {
                    bw.write(line);
                    bw.newLine();
                } else {
                    for (Object obj : wantedValues) {
                        if (obj instanceof String) {
                            String range = (String) obj;
                            int startValue = Integer.parseInt(range.split("-")[0]);
                            int endValue = Integer.parseInt(range.split("-")[1]);
                            if (col7Value >= startValue && col7Value <= endValue && 
                                col15Value >= startValue && col15Value <= endValue) {
                                bw.write(line);
                                bw.newLine();
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
