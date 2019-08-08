import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

public class Parser {
    private Document document;

    public Parser(){

        try {
            InputStream inputStream = new FileInputStream(new File("talend_input_output.item"));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            document = builder.parse(inputStream);

        } catch (FileNotFoundException notFound){
            System.out.println("File wasn't found");
        } catch (IOException IoE){
            System.out.println("IoE");
        } catch (SAXException SAX){
            System.out.println("SAX");
        } catch (ParserConfigurationException parse){
            System.out.println("Parse exception");
        }
    }

    public void printTesting() {
        System.out.println(document.getDoctype());
        System.out.println("Has child nodes: " + document.hasChildNodes());
    }

    public void printFirstNode () {
        NodeList children = document.getChildNodes();
        Element firstElement = (Element) children.item(0);
        System.out.println(firstElement.getNodeName());
    }

    public void printSteps() {
        NodeList nodes = document.getElementsByTagName("node");
        for (int i = 0; i < nodes.getLength(); i++){
            System.out.println(nodes.item(i).getAttributes().item(0));
            System.out.println(nodes.item(i).getAttributes().getNamedItem("componentName").getNodeValue());
        }
    }

    public ArrayList<String> getStepInfo (String nodeName) {
        ArrayList<String> info = new ArrayList<>();
        return info;
    }

}
