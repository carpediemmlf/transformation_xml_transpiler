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

        TalToGraph talGrapher = new TalToGraph("..//talend_input_output.item");
        Graph<TalNode, DefaultEdge> trialTalGraph = talGrapher.getGraph();
        // trialPentGraph.toString();
        Mapping mapperTal = new Mapping();
        mapperTal.talToDot(trialTalGraph, "talDot.dot");

        // -------------------

        PentahoToGraph pentGrapher = new PentahoToGraph("..//pentaho_input_output.ktr");
        Graph<PentNode, DefaultEdge> trialPentGraph = pentGrapher.getGraph();
        // trialPentGraph.toString();
        Mapping mapperPent = new Mapping();
        mapperPent.pentToDot(trialPentGraph, "pentDot.dot");

        // System.out.println(new Mapping().pentToDot(trialPentGraph, "pentDot.dot"));
    }
}
