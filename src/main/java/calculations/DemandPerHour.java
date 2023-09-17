package calculations;

import java.io.*;
import java.util.*;

public class DemandPerHour {
	public static void main(String[] args) throws IOException {
		// Read input file
		BufferedReader br = new BufferedReader(new FileReader("data/population/coimbraFiltro_synthetic9.tsv"));

		// Create demand array with 24 hours
		double[] demand = new double[25];

		// Read and discard the header line
		String headerLine = br.readLine();

		// Iterate over each line of the input file
		String line;
		while ((line = br.readLine()) != null) {
			// Split the line into columns
			String[] columns = line.split("\t");

			// Get the start time of the trip
			double startTime = convertTimeToDecimal(columns[17]);

			// Get the end time of the trip
			double endTime = convertTimeToDecimal(columns[18]);

			// Calculate the duration of the trip
			double duration = endTime - startTime;

			// Get the hour of the start time
			int startHour = (int) startTime;

			// Add the duration to the demand for that hour
			demand[startHour] += duration;
		}

		// Close input file
		br.close();

		// Write demand per hour to output file
		PrintWriter pw = new PrintWriter(new FileWriter("data/Calculations/outputDemandPerDay.txt"));
		for (int i = 0; i < 24; i++) {
			pw.println(String.format("%02d:00-%02d:00\t%.2f", i, i + 1, demand[i]));
		}
		pw.close();
	}

	private static double convertTimeToDecimal(String time) {
		int hours = Integer.parseInt(time.split(":")[0]);
		int minutes = Integer.parseInt(time.split(":")[1]);
		return hours + (double) minutes / 60.0;
	}

}
