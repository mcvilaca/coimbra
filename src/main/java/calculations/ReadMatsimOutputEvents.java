package calculations;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ReadMatsimOutputEvents {
	public static void main(String[] args) {
        try {
            // Specify the path to your Matsim output_events file
            String filePath = "scenarios/coimbra_ewgtdrt_art/outputs_20000_24_notimeconstrains2/output_events.xml";

         // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file and create a Document
            Document document = builder.parse(new File(filePath));

            // Get a list of all "event" elements
            NodeList eventList = document.getElementsByTagName("event");

            // Iterate through the list
            for (int i = 0; i < eventList.getLength(); i++) {
                Element eventElement = (Element) eventList.item(i);

                // Check if the event has type="PassengerRequest rejected"
                if (eventElement.getAttribute("type").equals("PassengerRequest rejected")) {
                    System.out.println(eventElement.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}