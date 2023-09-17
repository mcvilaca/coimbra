package DRT;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DownsizePopulation {

    public static void main(String[] args) {
        try {
            String inputFilePath = "scenarios/coimbra_ewgt_drt/populationdrt.xml";
            String outputFilePath = "scenarios/coimbra_ewgt_drt/population10drt.xml";

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(inputFilePath));

            NodeList personNodes = document.getElementsByTagName("person");
            int totalPersons = personNodes.getLength();
            int targetCount = (int) Math.ceil(totalPersons * 0.1); // 10% of the total persons

            List<Integer> deletedIndexes = selectRandomIndexes(totalPersons, totalPersons - targetCount);

            for (int index : deletedIndexes) {
                Node personNode = personNodes.item(index);
                personNode.getParentNode().removeChild(personNode);
            }

            // Save the reduced document to a new XML file
            saveDocument(document, outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> selectRandomIndexes(int totalItems, int targetCount) {
        List<Integer> allIndexes = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {
            allIndexes.add(i);
        }

        Random random = new Random();
        List<Integer> deletedIndexes = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            int randomIndex = random.nextInt(allIndexes.size());
            deletedIndexes.add(allIndexes.remove(randomIndex));
        }

        return deletedIndexes;
    }

    private static void saveDocument(Document document, String filePath) throws Exception {
        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(document);
        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(filePath);
        transformer.transform(source, result);
        System.out.println("Reduced XML file saved to: " + filePath);
    }
}

