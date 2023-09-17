package calculations;

import java.io.*;
import java.text.*;
import java.util.*;

public class DemandPerHourZone {

	public static void main(String[] args) throws IOException, ParseException {

		// Set the input and output file paths
		String inputFilePath = "data/population/coimbraFiltro_synthetic8.tsv";
		String outputFilePath = "data/Calculations/outputDemandPerDayZone.txt";

		// Initialize a map to store the request counts for each zone for each hour of
		// the day
		Map<String, Map<Integer, Integer>> requestCounts = new HashMap<>();

		// Initialize a date format to parse the pick up and drop off times
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");

		// Open the input file for reading
		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));

		// Skip header line
		reader.readLine();

		// Loop through each line of the input file
		String line;
		while ((line = reader.readLine()) != null) {

			// Split the line into fields
			String[] columns = line.split("\t");

			// Extract the start zone, end zone, pick up time, and drop off time
			String startZone = columns[7];
			String endZone = columns[15];
			Date pickUpTime = dateFormat.parse(columns[17]);
			Date dropOffTime = dateFormat.parse(columns[18]);

			// Get the hour of the pick up time
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(pickUpTime);
			int hour = (calendar.get(Calendar.HOUR_OF_DAY));

			// Increment the request count for the start zone for the current hour
			if (!requestCounts.containsKey(startZone)) {
				requestCounts.put(startZone, new HashMap<Integer, Integer>());
			}
			int startZoneCount = requestCounts.get(startZone).getOrDefault(hour, 0);
			requestCounts.get(startZone).put(hour, startZoneCount + 1);

			// Increment the request count for the end zone for the current hour
			if (!requestCounts.containsKey(endZone)) {
				requestCounts.put(endZone, new HashMap<Integer, Integer>());
			}
			int endZoneCount = requestCounts.get(endZone).getOrDefault(hour, 0);
			requestCounts.get(endZone).put(hour, endZoneCount + 1);

			// If the drop off time is on the next day, increment the request count for the
			// end zone for the next hour
			calendar.setTime(dropOffTime);
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
				endZoneCount = requestCounts.get(endZone).getOrDefault(24, 0);
				requestCounts.get(endZone).put(24, endZoneCount + 1);
			}
		}

		// Close the input file
		reader.close();

		// Open the output file for writing
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		// Write the request counts to the output file
		for (String zone : requestCounts.keySet()) {
			Map<Integer, Integer> counts = requestCounts.get(zone);
			writer.write("Zone " + zone + "\n");
			for (int hour = 0; hour <= 24; hour++) {
				writer.write(String.format("%02d:00-%02d:00\t%d\n", hour, hour + 1, counts.getOrDefault(hour, 0)));
			}
		}

		// Close the output file
		writer.close();

		System.out.println("Request counts");

		// Find the zones with the maximum demand and the time of the day with the
		// maximum value
		int maxCount = 0;
		String maxZone = "";
		int maxHour = 0;
		for (String zone : requestCounts.keySet()) {
			Map<Integer, Integer> counts = requestCounts.get(zone);
			for (int hour = 0; hour <= 24; hour++) {
				int count = counts.getOrDefault(hour, 0);
				if (count > maxCount) {
					maxCount = count;
					maxZone = zone;
					maxHour = hour;
				}
			}
		}
		System.out.println("Zone with maximum demand: " + maxZone);
		System.out.println(
				"Time of the day with maximum demand: " + String.format("%02d:00-%02d:00", maxHour, maxHour + 1));
	}
}
