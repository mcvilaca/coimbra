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

//Este código filtra as zonas de origem destino dos códigos das zonas que se definem
public class Scales_Filter {
	 public static void main(String[] args) {
	        // Define the path to the input TSV file
	        File inputFile = Paths.get("data", "population", "coimbra2_SMM.tsv").toFile();
	        
	        // Define the path to the output TSV file
	        File outputFile = Paths.get("data", "population", "Municipality.tsv").toFile();
	        
	        // Define os códigos e zonas desejadas3
	        List<String> codigosZonasDesejadas = Arrays.asList( "301", "310", "311", "312", "313", "314", "315", "316", "317", "318",
	        	    "319", "320", "322", "302", "323", "324", "325", "326", "327", "328",
	        	    "329", "330", "331", "332", "333", "334", "335", "336", "303", "337",
	        	    "338", "339", "340", "342", "343", "347", "348", "349", "350", "351",
	        	    "352", "355", "356", "357", "304", "358", "359", "360", "361", "362",
	        	    "364", "305", "306", "307", "308", "309", "321", "341", "353", "344",
	        	    "345");

	        // Read the input TSV file and filter the rows
	        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
	             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
	            // Escreve a linha de cabeçalho no arquivo TSV de saída
	            bw.write(br.readLine());
	            bw.newLine();
	            
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split("\t");
	                String origemZona = values[7]; // Coluna 'Origem_Zona'
	                String destinoZona = values[15]; // Coluna 'Destino_Zona'

	                // Verifica se a linha atende aos critérios desejados
	                if (codigosZonasDesejadas.contains(origemZona) && codigosZonasDesejadas.contains(destinoZona)) {
	                    bw.write(line);
	                    bw.newLine();
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	

}
