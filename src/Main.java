import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ExportException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Main {

    public static void main  (String [] args)
            throws URISyntaxException, ExportException, IOException, ParserConfigurationException, SAXException {

        // PentahoToGraph grapher = new PentahoToGraph("WorkingEg.ktr");

        // PentahoToGraph grapher = new PentahoToGraph("pentaho_input_output.ktr");

        // WriteXMLFile writer = new WriteXMLFile(grapher.getGraph());

        TalToGraph talGrapher = new TalToGraph("WorkingEg.ktr")

    }
}
