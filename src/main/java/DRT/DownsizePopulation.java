//package DRT;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class DownsizePopulation {

//    public static void main(String[] args) {
//        try {
//            String inputFilePath = "scenarios/coimbra_ewgtdrt_v3/population.xml";
//            String outputFilePath = "scenarios/coimbra_ewgtdrt_v3/population10drt.xml";
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new File(inputFilePath));
//
//            NodeList personNodes = document.getElementsByTagName("person");
//            int totalPersons = personNodes.getLength();
//            int targetCount = (int) Math.ceil(totalPersons * 0.1); // 10% of the total persons
//
//            List<Integer> deletedIndexes = selectRandomIndexes(totalPersons, totalPersons - targetCount);
//
//            for (int index : deletedIndexes) {
//                Node personNode = personNodes.item(index);
//                personNode.getParentNode().removeChild(personNode);
//            }
//
//            // Save the reduced document to a new XML file
//            saveDocument(document, outputFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<Integer> selectRandomIndexes(int totalItems, int targetCount) {
//        List<Integer> allIndexes = new ArrayList<>();
//        for (int i = 0; i < totalItems; i++) {
//            allIndexes.add(i);
//        }
//
//        Random random = new Random();
//        List<Integer> deletedIndexes = new ArrayList<>();
//        for (int i = 0; i < targetCount; i++) {
//            int randomIndex = random.nextInt(allIndexes.size());
//            deletedIndexes.add(allIndexes.remove(randomIndex));
//        }
//
//        return deletedIndexes;
//    }
//
//    private static void saveDocument(Document document, String filePath) throws Exception {
//        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
//        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
//        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(document);
//        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(filePath);
//        transformer.transform(source, result);
//        System.out.println("Reduced XML file saved to: " + filePath);
//    }
//}

//package DRT;

//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class DownsizePopulation {
//
//    public static void main(String[] args) {
//        try {
//            String inputFilePath = "scenarios/coimbra_ewgtdrt_v3/population.xml";
//            String outputFilePath = "scenarios/coimbra_ewgtdrt_v3/population10drt.xml";
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new File(inputFilePath));
//
//            NodeList personNodes = document.getElementsByTagName("person");
//            int totalPersons = personNodes.getLength();
//            int targetCount = (int) Math.ceil(totalPersons * 0.1); // 10% of the total persons
//
//            List<Integer> deletedIndexes = selectRandomIndexes(totalPersons, totalPersons - targetCount);
//
//            for (int index : deletedIndexes) {
//                if (index >= 0 && index < personNodes.getLength()) {
//                    Node personNode = personNodes.item(index);
//                    personNode.getParentNode().removeChild(personNode);
//                } else {
//                    System.out.println("Invalid index: " + index);
//                }
//            }
//
//            // Save the reduced document to a new XML file
//            saveDocument(document, outputFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<Integer> selectRandomIndexes(int totalItems, int targetCount) {
//        List<Integer> allIndexes = new ArrayList<>();
//        for (int i = 0; i < totalItems; i++) {
//            allIndexes.add(i);
//        }
//
//        Random random = new Random();
//        List<Integer> deletedIndexes = new ArrayList<>();
//        for (int i = 0; i < targetCount; i++) {
//            int randomIndex = random.nextInt(allIndexes.size());
//            deletedIndexes.add(allIndexes.remove(randomIndex));
//        }
//
//        return deletedIndexes;
//    }
//
//    private static void saveDocument(Document document, String filePath) throws Exception {
//        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
//        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
//        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(document);
//        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(filePath);
//        transformer.transform(source, result);
//        System.out.println("Reduced XML file saved to: " + filePath);
//    }
//}
//
//
package DRT;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DownsizePopulation {
    public static void main(String[] args) {
        try {
            // Load the XML file
            File xmlFile = new File("scenarios/coimbra_ewgtdrt_v3/population.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the list of persons
            NodeList personList = doc.getElementsByTagName("person");

            // Shuffle the list randomly
            List<Node> shuffledPersons = new ArrayList<>();
            for (int i = 0; i < personList.getLength(); i++) {
                shuffledPersons.add(personList.item(i).cloneNode(true));
            }
            Collections.shuffle(shuffledPersons, new Random());

            // Calculate 10% of the persons
            int numPersonsToSelect = (int) Math.ceil(0.1 * shuffledPersons.size());

            // Create a new Document for the selected persons
            Document selectedPersonsDoc = dBuilder.newDocument();
            Element rootElement = selectedPersonsDoc.createElement("population");
            selectedPersonsDoc.appendChild(rootElement);

            // Add the randomly chosen persons to the new Document
            for (int i = 0; i < numPersonsToSelect; i++) {
                Node selectedPerson = shuffledPersons.get(i);
                rootElement.appendChild(selectedPersonsDoc.importNode(selectedPerson, true));
            }

            // Write the selectedPersonsDoc to a new XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(selectedPersonsDoc);
            StreamResult result = new StreamResult(new File("scenarios/coimbra_ewgtdrt_v3/population10drt.xml"));
            transformer.transform(source, result);

            System.out.println("Randomly selected 10% of persons saved to output file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


