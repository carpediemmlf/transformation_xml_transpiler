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

   public static List getVertices(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List myList = new ArrayList();

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

                    myList.add(eElement.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue());

                }

            }

        }

        // System.out.println((myMap));

        return myList;
    }

    public static Map getEdges(String fileName) throws ParserConfigurationException, IOException, SAXException {
        Map<String, List<String>> myMap2 = new HashMap<>();
        List<String> link = new ArrayList<>();
        // Read in file located at same level as the root of the project folder.
        File fXmlFile = new File(fileName);

        // Build DOM Document. Throws ParserConfigurationException.
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // Throws IOException and SAXException.
        Document doc = dBuilder.parse(fXmlFile);

        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        NodeList linkList = doc.getElementsByTagName("connection");
        for (int a = 0; a < linkList.getLength(); a++) {
            Node linkNode = linkList.item(a);
            if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                Element linkElement = (Element) linkNode;
                System.out.println("Connection: " + linkElement.getAttribute("label"));
                link.add(linkElement.getAttribute("connectorName"));
                link.add(linkElement.getAttribute("source"));
                link.add(linkElement.getAttribute("target"));
                myMap2.put(linkElement.getAttribute("label"), link);
            }
        }
        return myMap2;
    }
}
