package pt.mvilaca.matsimtests.dataTreatment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.io.File;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario.QuestionarioIndividual;

/*Este Código replica o número de viagens descritas no inquérito de mobilidade as vezes descritas pelo coef_exp
 * */

public class SyntheticPopulation_v1 {
	
	public static void main (String[] args) throws IOException, ParseException {
		File f = Paths.get("data", "population", "coimbra.tsv").toFile();
//		File f = Paths.get("data", "population", "coimbra_debug_synthetic.tsv").toFile();
	
		FileReader fr = new FileReader(f);
		FileWriter fw = new FileWriter("data/population/test_synthetic.tsv");
		BufferedReader br = new BufferedReader(fr);

		String line = br.readLine();
		fw.write(line);
		fw.write("\n");
		System.out.println(line);
		
		line = br.readLine();
		while(line!=null) {
//			System.out.println(line);
			int countlines = 0;
			String[] columns = line.split("\t");
			
			String id = columns[0];
			for(int i =id.length() ; i <= 5; i++) id = "0"+id; 
			
			double coefExp = Double.parseDouble(columns[38]);
			while(countlines<coefExp) {
				columns[0]= countlines+ id;
				String newLine = String.join("\t", columns);
				
				System.out.println(newLine);
				fw.write(newLine);
				fw.write("\n");
				fw.flush();
				countlines++;
			}
			
			
//			String[] columns = line.split("\t");
//			String lineOne = columns[1];
//			System.out.println(lineOne);
//			String lineThree = columns[3];
//			String lineFour = columns[4];
//			System.out.println(lineThree);
//			System.out.println(lineFour);
//			String CoefExp = columns [38];
//			System.out.println(CoefExp);
			
//			int countlines
//			while()
				
			
			line = br.readLine();
			
			
			
			
		}
		br.close();
		fw.close();
		
//		FileWriter fw = new FileWriter("data/population/Test_synthetic");
//		fw.write(line);
//		fw.write(line);
//		fw.close();
	}
}

		

