package matsimtests.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import com.google.common.base.Strings;

public class ErrosQuestionario {
		public static void main(String[] args) throws IOException, ParseException {
			File f = Paths.get("data", "population", "coimbraHoras.tsv").toFile();
			DateFormat df =  new SimpleDateFormat("H':'m");
			
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
//			FileWriter fw = new FileWriter("data/population/test_synthetic.tsv");
//			fw.write(line);
//			fw.write("\n");
			System.out.println(line);
			
			int inicioEqFim =0;
			int fimMenorInicio =0;
			double origEqDest =0;
			double grandDist =0;
			double grandDist2 =0;
			double blankHourInicio = 0;
			double blankHourFim = 0;
			List<String> linhasComProblema1 = new ArrayList<>();
			List<String> linhasComProblema2 = new ArrayList<>();
			List<String> linhasComProblema3 = new ArrayList<>();
			List<String> linhasComProblema4 = new ArrayList<>();
			List<String> linhasComProblema5 = new ArrayList<>();
			List<String> linhasComProblema6 = new ArrayList<>();
			
			line = br.readLine();
			while(line!=null) {
				System.out.println(line);
				int countlines = 0;
				String[] columns = line.split("\t");
				
				String id = columns[0];
				double x,y;

				//			Origem
				x = Double.parseDouble(columns[3]);
				y = Double.parseDouble(columns[4]);
				Coord origem = new Coord(x, y);

				//			Destino
				x = Double.parseDouble(columns[11]);
				y = Double.parseDouble(columns[12]);
				Coord destino = new Coord(x, y);

				//			Horas
//				System.out.println(line);
//				System.out.println(columns[17]);
				Date inicio = df.parse(columns[17]);
				Date fim = df.parse(columns[18]);
				Date duration = df.parse(columns[19]);
				double hoursInicio = inicio.getHours()*60*60+inicio.getMinutes()*60.0;
				double hoursFim = fim.getHours()*60*60+fim.getMinutes()*60.0; 
				double hoursDuration = duration.getHours()*60*60+duration.getMinutes()*60.0;
				
				
				if(hoursInicio == hoursFim) {
					System.out.println("PROBLEM1!");
					inicioEqFim++;
					linhasComProblema1.add(line);
				} 
				
				if(hoursInicio > hoursFim) {
					System.out.println("PROBLEM2!");
					fimMenorInicio++;
					linhasComProblema2.add(line);
				} 
						
				if (origem.equals(destino)) {
					System.out.println("PROBLEM3!");
					origEqDest++;
					linhasComProblema3.add(line);
				}
				
				double distance = CoordUtils.calcEuclideanDistance(origem, destino)/1000;
//				System.out.println(distance);
				if (distance >= 100) {
					System.out.println("PROBLEM4!");
					grandDist++;
					linhasComProblema4.add(line);
				}
		
				if (distance >= 300) {
					System.out.println("PROBLEM5!");
					grandDist2++;
					linhasComProblema5.add(line);
				
				}
				
//				if (Double.isNaN(hoursInicio)) {
//					   System.out.println("Problem6!");
//					   blankHourInicio++;
//					   linhasComProblema6.add(line);
//				}
//				if (Double.isNaN(hoursFim)) {
//					   System.out.println("Problem6!");
//					   blankHourFim++;
//					   linhasComProblema6.add(line);
//				}
			
					
				
				
				line = br.readLine();
				
				
			}
			br.close();
//			fw.close();
			
			System.out.println("Problema1: " + inicioEqFim);
			System.out.println("Problema2: " + fimMenorInicio);
			System.out.println("Problema3: " + origEqDest);
			System.out.println("Problema4: " + grandDist);
			System.out.println("Problema5: " + grandDist2);
			
//			System.out.println("Problema6: " + blankHourInicio + blankHourFim);
			
			
			System.out.println("Problema 1 Linhas:" );
			for(String prob1: linhasComProblema1)
				System.out.println(prob1);
			
			System.out.println("Problema 2 Linhas:" );
			for(String prob2: linhasComProblema2)
				System.out.println(prob2);
			
			System.out.println("Problema 3 Linhas:" );
			for(String prob3: linhasComProblema3)
				System.out.println(prob3);
			
			System.out.println("Problema 4 Linhas:" );
			for(String prob4: linhasComProblema4)
				System.out.println(prob4);
			
			System.out.println("Problema 5 Linhas:" );
			for(String prob5: linhasComProblema5)
				System.out.println(prob5);
			
			
			
//			System.out.println("Problema 6 Linhas:" );
//			for(String prob6: linhasComProblema6)
//				System.out.println(prob6);
		}
}
			


