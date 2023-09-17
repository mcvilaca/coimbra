package pt.mvilaca.matsimtests.dataTreatment;

import org.w3c.dom.*;



import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

public class Filtro4_XMLProcessor {

    public static void main(String[] args) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File("scenarios/coimbra_ewgt_test/population.xml")); // Replace with your XML file path

            NodeList personNodes = doc.getElementsByTagName("person");

            for (int i = 0; i < personNodes.getLength(); i++) {
                Element person = (Element) personNodes.item(i);
                NodeList activityNodes = person.getElementsByTagName("activity");

                // Create a new activity element using the first activity's type
                Element firstActivity = (Element) activityNodes.item(0);
                Element newActivity = (Element) firstActivity.cloneNode(true);
                newActivity.removeAttribute("end_time");

                // Insert the new activity after the last activity
                Element plan = (Element) person.getElementsByTagName("plan").item(0);
                plan.appendChild(newActivity);

                // Remove dep_time and trav_time attributes from leg elements
                NodeList legNodes = person.getElementsByTagName("leg");
                for (int j = 0; j < legNodes.getLength(); j++) {
                    Element leg = (Element) legNodes.item(j);
                    leg.removeAttribute("dep_time");
                    leg.removeAttribute("trav_time");
                }
            }

            // Save the modified XML document
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("scenarios/coimbra_ewgt_test/population_filtro4.xml")); // Replace with desired output file path
            transformer.transform(source, result);

            System.out.println("XML processing completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
