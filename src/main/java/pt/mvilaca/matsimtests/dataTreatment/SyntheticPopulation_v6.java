package pt.mvilaca.matsimtests.dataTreatment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/*Este Código replica o número de viagens descritas no inquérito de mobilidade as vezes descritas pelo coef_exp; cria 
 * um banco de coordenadas por zona e selecciona de forma aleatória coordenadas para as viagens replicadas;
 * faz variar a hora de partida +/-10' e cria IDs unicos para as novas viagens* */

public class SyntheticPopulation_v6 {

	public static void main(String[] args) throws IOException {
		File inputFile = Paths.get("data", "population", "coimbra_filtro2.tsv").toFile();
		File outputFile = Paths.get("data", "population", "coimbraFiltro2_synthetic_v6.tsv").toFile();

		List<String> lines = readLinesFromFile(inputFile);
		StringBuilder outputBuilder = new StringBuilder();

		// Write header line
		String header = lines.get(0);
		outputBuilder.append(header).append("\n");
		System.out.println(header);

		// Create a map to store the lists of coordinates with the same zone codes
		Map<Integer, List<String[]>> zoneCoordMap = new HashMap<>();
		Random random = new Random();

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] columns = line.split("\t");
			double coefExp = Double.parseDouble(columns[38]);

			// Change the id of the new replications
			String id = columns[0];
			// Create a Set to store generated IDs to ensure uniqueness
			Set<String> generatedIds = new HashSet<>();

			// Write original line to outputBuilder
			outputBuilder.append(line).append("\n");

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
				String[] originCoord = sameOriginZoneCoords.get(random.nextInt(sameOriginZoneCoords.size()));
				String[] destinationCoord = sameDestinationZoneCoords.get(random.nextInt(sameDestinationZoneCoords.size()));

				// Add or subtract up to 10 minutes to the start time
				double startTime = convertTimeToDecimal(replicatedLines[17]);
				int timeDelta = random.nextInt(21) - 10; // add or subtract up to 10 minutes
				double newStartTime = startTime + timeDelta / 60.0;
				if (newStartTime < 0) {
					newStartTime += 24.0; // wrap around to next day
				}
				if (newStartTime > 24.0) {
					newStartTime -= 24.0; // wrap around to 00:00 of the same day
					double endTime = convertTimeToDecimal(replicatedLines[18]);
					double duration = endTime - startTime;
					double newEndTime = newStartTime + duration;
					if (newEndTime > 24.0) {
						newEndTime -= 24.0; // wrap around to 00:00 of the next day
					} else if (newEndTime < 0) {
					newEndTime += 24.0; // wrap around to 23:59 of the previous day
					}
					replicatedLines[18] = convertDecimalToTime(newEndTime);
					}
					replicatedLines[17] = convertDecimalToTime(newStartTime);
								// Update replicated columns with selected coordinates
			replicatedLines[3] = originCoord[3];
			replicatedLines[4] = originCoord[4];
			replicatedLines[11] = destinationCoord[11];
			replicatedLines[12] = destinationCoord[12];

			String newLine = String.join("\t", replicatedLines);
			outputBuilder.append(newLine).append("\n");
			countlines++; // increment countlines inside the while loop
		}
	}

	try (FileWriter fw = new FileWriter(outputFile)) {
		fw.write(outputBuilder.toString());
	}

	System.out.println("Program has finished executing.");

	// Get the number of lines in the output file
	int numLines = getNumLinesInFile(outputFile);
	System.out.println("Number of lines in the output file: " + numLines);
}

private static int getNumLinesInFile(File outputFile) throws IOException {
	int numLines = 0;
	try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
		while (br.readLine() != null) {
			numLines++;
		}
	}
	return numLines;
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
