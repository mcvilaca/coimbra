package pt.mvilaca.matsimtests.dataTreatment;

import java.io.*;
import java.util.*;


import java.nio.file.Paths;

public class Filtro3 {
	public static void main(String[] args) throws Exception {
        // Open the input and output files
        File inputFile = Paths.get("data", "population", "Filtro2", "coimbraFiltro2_synthetic.tsv").toFile();
        File outputFile = Paths.get("data", "population", "Filtro2", "Synthetic_filtro3.tsv").toFile();
        Scanner scanner = new Scanner(inputFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        // Define a set of unwanted values
        Set<String> unwantedValues = new HashSet<>(Arrays.asList("Abrantes", "Águeda", "Albufeira", "Alcácer do Sal", "Alcobaça", "Alijó", "Almada", "Alvaiázere", "Armamar", "Aveiro", "Benavente", "Braga", "Caldas da Rainha", "Carregal do Sal", "Castelo Branco", "Castelo de Paiva", "Celorico da Beira", "Constância", "Covilhã", "Espinho", "Estremoz", "Ferreira do Zêzere", "Fora Portugal", "Gouveia", "Guarda", "Guimarães", "Idanha-a-Nova", "Ílhavo", "Lagos", "Leiria", "Lisboa", "Loulé", "Loures", "Lousada", "Maia", "Mangualde", "Marinha Grande", "Matosinhos", "Mirandela", "Murça", "Nazaré", "Nelas", "Odivelas", "Oeiras", "Oliveira do Bairro", "Ourém", "Ovar", "Ponte de Sor", "Portalegre", "Porto", "Porto de Mós", "Sabrosa", "Sabugal", "Santa Maria da Feira", "Santarém", "Seia", "Sertã", "Setúbal", "Sintra", "Tomar", "Tondela", "Torres Novas", "Vagos", "Viana do Castelo", "Vila Nova de Famalicão", "Vila Nova de Gaia", "Vila Real", "Viseu", "Almeirim", "Baião", "Chaves", "Faro", "Felgueiras", "Ferreira do Zêzere", "Figueira de Castelo Rodrigo", "Fora Portugal", "Fundão", "Lamego"));

        // Loop over each line of the input file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\t");

            // Check if the row should be deleted based on unwanted values
            if (unwantedValues.contains(parts[8]) || unwantedValues.contains(parts[16])) {
                continue; // Skip this row
            }
            
            // Check if the row should be deleted based on origem x/y == destino x/y
            if (parts[3].equals(parts[11]) && parts[4].equals(parts[12])) {
                continue; // Skip this row
            }

            // Write the line to the output file
            writer.write(line);
            writer.newLine();
        }

        // Close the input and output files
        scanner.close();
        writer.close ();
	}
}
