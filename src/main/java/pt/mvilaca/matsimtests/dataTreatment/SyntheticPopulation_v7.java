package pt.mvilaca.matsimtests.dataTreatment;

import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.matsim.api.core.v01.Coord;

import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.MotivoViagem;
import pt.mvilaca.matsimtests.population.CoimbraQuestionario3.QuestionarioIndividual;

public class SyntheticPopulation_v7 {
	
	 private static Map<String, List<Coord>> coordinateBank = new HashMap<>();
	    private static Random random = new Random();

    public static void main(String[] args) throws IOException, ParseException {
        File inputFile = Paths.get("data", "population", "coimbra.tsv").toFile();
        File outputFile = Paths.get("data", "population", "coimbraSynthetic_v7.tsv").toFile();

        List<String> lines = readLinesFromFile(inputFile);
        List<QuestionarioIndividual> syntheticPopulation = new ArrayList<>();
        Random random = new Random();
        populateCoordinateBank(lines);
        Map<String, Coord> homeCoordinatesMap = new HashMap<>();
        Map<String, Coord> workCoordinatesMap = new HashMap<>();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        StringBuilder outputBuilder = new StringBuilder();

        // Write header line
        String header = lines.get(0);
        outputBuilder.append(header).append("\n");

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] tokens = line.split("\t");

            String personId = tokens[0];
            int origZoneId = Integer.parseInt(tokens[8]);
            int destZoneId = Integer.parseInt(tokens[16]);
            double origX = Double.parseDouble(tokens[3]);
            double origY = Double.parseDouble(tokens[4]);
            double destX = Double.parseDouble(tokens[11]);
            double destY = Double.parseDouble(tokens[12]);
            Date startTime = df.parse(tokens[17]);
            MotivoViagem motivo = convertMotivoViagem(tokens[9]);
            String mode = convertToTransportMode(tokens[23]); 
            int startAtHome = Integer.parseInt(tokens[2]);
            int tripSequence = Integer.parseInt(tokens[1]);

            // Generating replicated lines
            int countLines = 0;
            int coefExp;
			while (countLines < coefExp) {
                double timeDelta = (random.nextDouble() * 20) - 10; // +/- 10 minutes
                Date newStartTime = new Date(startTime.getTime() + (long) (timeDelta * 60 * 1000));


                Coord newOrigCoord;
				Coord newDestCoord;
				if (motivo == MotivoViagem.casa || motivo == MotivoViagem.trabalho) {
                    newOrigCoord = generateOrRetrieveCoordinate(personId, origZoneId, origX, origY, homeCoordinatesMap);
                    newDestCoord = generateOrRetrieveCoordinate(personId, destZoneId, destX, destY, workCoordinatesMap);
                } else {
                	MotivoViagem firstActivityType;
					newOrigCoord = generateRandomCoordinate(firstActivityType, origZoneId);
                    newDestCoord = generateRandomCoordinate(motivo, destZoneId);
                }

                QuestionarioIndividual ai = new QuestionarioIndividual(personId, newOrigCoord, newDestCoord,
                        newStartTime.getHours() * 60 * 60 + newStartTime.getMinutes() * 60.0,
                        motivo, mode, startAtHome, tripSequence);

                outputBuilder.append(ai.toTSV()).append("\n");

                countLines++;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(outputBuilder.toString());
        }

        System.out.println("Program has finished executing.");
    }
    
    private static void populateCoordinateBank(List<String> lines) {
		// TODO Auto-generated method stub
		
	}

	private static Coord generateRandomCoordinate(MotivoViagem motivo, int zoneId) {
        String key = motivo + "_" + zoneId;
        List<Coord> coordinates = coordinateBank.get(key);

        if (coordinates != null && !coordinates.isEmpty()) {
            int randomIndex = random.nextInt(coordinates.size());
            return coordinates.get(randomIndex);
        } else {
            // Handle the case when no coordinates are available for the given type and zone
            // You can return a default or throw an exception based on your requirements
            return new Coord(0.0, 0.0); // Placeholder, replace with your logic
        }
    }

    private static Coord generateOrRetrieveCoordinate(String personId, int origZoneId, double origX, double origY,
			Map<String, Coord> homeCoordinatesMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String convertToTransportMode(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Coord generateRandomCoordinate() {
        // Replace with your logic to generate random coordinates
        return new Coord(/* Replace with your logic */);
    }

    private static MotivoViagem convertMotivoViagem(String motivo) {
        // Implement logic to convert motivo da viagem code to MotivoViagem enum
        // Replace the placeholder with your actual implementation
        switch (motivo.trim()) {
            case "11":
                return MotivoViagem.trabalho;
            case "2":
                return MotivoViagem.EDUCATION;
            case "3":
            case "14":
            case "16":
                return MotivoViagem.casa;
            case "4":
                return MotivoViagem.SHOPPING;
            case "5":
            case "6":
            case "15":
                return MotivoViagem.LEISURE;
            case "8":
                return MotivoViagem.RESTAURANT;
            case "9":
                return MotivoViagem.HEALTH;
            case "10":
            case "13":
            case "17":
            case "99":
                return MotivoViagem.OTHERS;
            case "12":
                return MotivoViagem.SERVICES;
            default:
                return MotivoViagem.UNKNOWN;
        }
    }

    private static List<String> readLinesFromFile(File inputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader
  
