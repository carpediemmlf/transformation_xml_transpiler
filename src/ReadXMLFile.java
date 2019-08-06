import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ReadXMLFile {

    public static Map getVertices(String fileName) throws ParserConfigurationException, IOException, SAXException {

            Map<String, String> myMap = new HashMap<String, String>();

            // Read in file located at same level as the root of the project folder.
            File fXmlFile = new File(fileName);

            // Build DOM Document. Throws ParserConfigurationException.
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Throws IOException and SAXException.
            Document doc = dBuilder.parse(fXmlFile);

            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("node");

            // System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    // System.out.println("Node : " + eElement.getAttribute("componentName"));
                    if (nNode.hasChildNodes()) {

                        myMap.put(eElement.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue(), eElement.getAttribute("componentName").toString());
                    }
                }
            }
            // System.out.println((myMap));

            return myMap;
    }
}
