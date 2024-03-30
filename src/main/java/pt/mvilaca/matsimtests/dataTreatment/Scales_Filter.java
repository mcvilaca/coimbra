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
	        File outputFile = Paths.get("data", "population", "SubRegion.tsv").toFile();
	        
	        // Define os códigos e zonas desejadas3
	        List<String> codigosZonasDesejadas = Arrays.asList("8","9","19","20","21","22","23","25","29","34","35","37","39","43","44","53","54","105","111","112","114","301","302","303","304","305","306","307","308","309","310","311","312","313","314","315","316","317","318","319","320","321","322","323","324","325","326","327","328","329","330","331","332","333","334","335","336","337","338","339","340","341","342","343","344","345","347","348","349","350","351","352","353","355","356","357","358","359","360","361","362","364"
);

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
