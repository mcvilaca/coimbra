package pt.mvilaca.matsimtests.dataTreatment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SyntheticPopulation_v5 {

	public static void main(String[] args) throws IOException {
//		File inputFile = Paths.get("data", "population", "coimbraFiltro.tsv").toFile();
//		File outputFile = Paths.get("data", "population", "coimbra_synthetic9.tsv").toFile();
		File inputFile = Paths.get("data", "population","Filtro2", "coimbraFiltro2.tsv").toFile();
		File outputFile = Paths.get("data", "population", "Filtro2", "coimbraFiltro2_synthetic.tsv").toFile();

		List<String> lines = readLinesFromFile(inputFile);
		FileWriter fw = new FileWriter(outputFile);

		// Write header line
		String header = lines.get(0);
		fw.write(header);
		fw.write("\n");
		System.out.println(header);

		// Create a map to store the lists of coordinates with the same zone codes
		Map<Integer, List<String[]>> zoneCoordMap = new HashMap<>();
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] columns = line.split("\t");
			double coefExp = Double.parseDouble(columns[38]);

			// Change the id of the new replications
			String id = columns[0];
			// Create a Set to store generated IDs to ensure uniqueness
			Set<String> generatedIds = new HashSet<>();

			// Write original line to file
			fw.write(line);
			fw.write("\n");

			// Generate replicated lines based on coefExp
			int countlines = 0;
			while (countlines < coefExp) {
				String[] replicatedLines = columns.clone();

				// Generate new ID for replicated line
				String replicatedId = generateUniqueId(id, countlines + 1);
				replicatedLines[0] = replicatedId;

				// Get origin and destination zone codes
				int originZone = Integer.parseInt(columns[7]);
				int destinationZone = Integer.parseInt(columns[15]);

				// Get list of coordinates with same origin zone
				List<String[]> sameOriginZoneCoords = getZoneCoordinates(lines, 7, originZone);

				// Get list of coordinates with same destination zone
				List<String[]> sameDestinationZoneCoords = getZoneCoordinates(lines, 15, destinationZone);

				// Randomly select a coordinate from the list for origin x, origin y,
				// destination x, and destination y
				String[] originCoord = sameOriginZoneCoords.get(new Random().nextInt(sameOriginZoneCoords.size()));
				String[] destinationCoord = sameDestinationZoneCoords
						.get(new Random().nextInt(sameDestinationZoneCoords.size()));

				// Add or subtract up to 10 minutes to the start time
				double startTime = convertTimeToDecimal(replicatedLines[17]);
				int timeDelta = new Random().nextInt(21) - 10; // add or subtract up to 10 minutes
				double newStartTime = startTime + timeDelta / 60.0;
				if (newStartTime < 0) {
				    newStartTime += 24.0; // wrap around to next day
				}
				replicatedLines[17] = convertDecimalToTime(newStartTime);

				// Calculate the new end time
				double duration = convertTimeToDecimal(replicatedLines[19]);
				double endTime = newStartTime + duration;
				if (endTime < 0) {
				    endTime += 24.0; // wrap around to next day
				}
				replicatedLines[18] = convertDecimalToTime(endTime);


				// Update replicated columns with selected coordinates
				replicatedLines[3] = originCoord[3];
				replicatedLines[4] = originCoord[4];
				replicatedLines[11] = destinationCoord[11];
				replicatedLines[12] = destinationCoord[12];

				String newLine = String.join("\t", replicatedLines);
				fw.write(newLine);
				fw.write("\n");
				fw.flush();
				countlines++; // increment countlines inside the while loop
			}
		}
		fw.close();
		System.out.println("Program has finished executing.");
	}

	public static String convertDecimalToTime(double decimalTime) {
		int hours = (int) decimalTime;
		int minutes = (int) ((decimalTime - hours) * 60);
		return String.format("%02d:%02d", hours, minutes);
	}

	public static double convertTimeToDecimal(String time) {
		String[] parts = time.split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return hours + (minutes / 60.0);
	}

	/**
	 * Returns a list of lines from a TSV file.
	 *
	 * @param inputFile the TSV file
	 * @return a list of lines
	 * @throws IOException if there is an error reading the file
	 */
	private static List<String> readLinesFromFile(File inputFile) throws IOException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines;
	}

	private static String generateUniqueId(String id, int count) {
		return id + "_" + count;
	}

	private static List<String[]> getZoneCoordinates(List<String> lines, int zoneColumnIndex, int zoneID) {
		List<String[]> sameZoneCoords = new ArrayList<>();
		for (String line : lines) {
			String[] cols = line.split("\t");
			if (cols[zoneColumnIndex].equals(String.valueOf(zoneID))) {
				sameZoneCoords.add(cols);
			}
		}
		return sameZoneCoords;
	}
}

