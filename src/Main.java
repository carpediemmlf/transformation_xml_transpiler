import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.MalformedURLException;

public class Main {
    public static void main  (String [] args)
            throws URISyntaxException, ExportException, IOException, ParserConfigurationException, SAXException {

        TalToGraph talGrapher = new TalToGraph("talendXML/talend_input_output.item");
        Graph<TalNode, DefaultEdge> trialTalGraph = talGrapher.getGraph();
        Mapping mapperTal = new Mapping();
        mapperTal.talToDot(trialTalGraph, "talDot.dot");

        // -------------------

        String pentahoXMLName = "testToGraph";
        String fileName = "pentahoXML/" + pentahoXMLName + ".ktr";
        PentahoToGraph pentGrapher = new PentahoToGraph(fileName);
        Graph<PentNode, DefaultEdge> trialPentGraph = pentGrapher.getGraph();
        // trialPentGraph.toString();
        Mapping mapperPent = new Mapping();
        String outputDotFileName = pentahoXMLName + ".dot";
        mapperPent.pentToDot(trialPentGraph, outputDotFileName);

        // System.out.println(new Mapping().pentToDot(trialPentGraph, "pentDot.dot"));
    }
}
