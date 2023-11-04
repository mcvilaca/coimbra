package pt.mvilaca.matsimtests.dataTreatment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

public class SyntheticPopulation_v7 {
	    public static void main(String[] args) throws IOException {
	        File inputFile = Paths.get("data", "population", "coimbra_filtro2.tsv").toFile();
	        File outputFile = Paths.get("data", "population", "coimbraFiltro2_synthetic_v7.tsv").toFile();

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

	            // Generate replicated lines based on coefExp
	            int countlines = 0;
	            while (countlines < coefExp) {
	                String[] replicatedLines = columns.clone();

	                // Generate new ID for replicated line
	                String replicatedId = generateUniqueId(id, countlines + 1);
	                replicatedLines[0] = replicatedId;

	                // Ensure the first trip starts in a coordinate related to the motive "casa"
	                if (countlines == 0) {
	                    // Find the coordinates related to "casa" based on P3-Motivo da viagem (P3 column)
	                    int motiveDaViagem = Integer.parseInt(columns[9]);
	                    List<String[]> casaCoords = getZoneAndMotivoCoordinates(lines, 7, columns[7], 9, motiveDaViagem);

	                    // Select a random coordinate from the list
	                    String[] casaCoord = casaCoords.get(random.nextInt(casaCoords.size()));

	                    // Update origin X and Y for the first trip
	                    replicatedLines[3] = casaCoord[3]; // Origem_X
	                    replicatedLines[4] = casaCoord[4]; // Origem_Y
	                } else {
	                    // For onward origin trips, set them to be equal to the destination of the last trip
	                    replicatedLines[3] = columns[11]; // Origem_X
	                    replicatedLines[4] = columns[12]; // Origem_Y
	                }

	                //  // Add or subtract up to 10 minutes to the start time
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

	    private static List<String[]> getZoneAndMotivoCoordinates(List<String> lines, int zoneColumnIndex, String zoneID, int motivoColumnIndex, int motivoID) {
	        List<String[]> sameZoneAndMotivoCoords = new ArrayList<>();
	        for (String line : lines) {
	            String[] cols = line.split("\t");
	            int lineZoneID = Integer.parseInt(cols[zoneColumnIndex]);
	            int lineMotivoID = Integer.parseInt(cols[motivoColumnIndex]);

	            // Check if the line's destination zone and motive match the desired criteria
	            if (lineZoneID == Integer.parseInt(zoneID) && lineMotivoID == motivoID) {
	                sameZoneAndMotivoCoords.add(cols);
	            }
	        }
	        return sameZoneAndMotivoCoords;
	    }

		private static List<String[]> getZoneAndMotivoCoordinates(List<String> lines, int zoneColumnIndex, int zoneID, int motivoColumnIndex, int motivoID) {
	        List<String[]> sameZoneAndMotivoCoords = new ArrayList<>();
	        for (String line : lines) {
	            String[] cols = line.split("\t");
	            int lineZoneID = Integer.parseInt(cols[zoneColumnIndex]);
	            int lineMotivoID = Integer.parseInt(cols[motivoColumnIndex]);

	            // Check if the line's destination zone and motivo da viagem match the desired criteria
	            if (lineZoneID == zoneID && lineMotivoID == motivoID) {
	                sameZoneAndMotivoCoords.add(cols);
	            }
	        }
	        return sameZoneAndMotivoCoords;
	    }

	    private static List<String[]> getCasaMotiveCoordinates(List<String> lines, int motiveColumnIndex, int motiveID) {
	        List<String[]> casaMotiveCoords = new ArrayList<>();
	        for (String line : lines) {
	            String[] cols = line.split("\t");
	            int lineMotiveID = Integer.parseInt(cols[motiveColumnIndex]);

	            if (lineMotiveID == motiveID) {
	                casaMotiveCoords.add(cols);
	            }
	        }
	        return casaMotiveCoords;
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
	}

    